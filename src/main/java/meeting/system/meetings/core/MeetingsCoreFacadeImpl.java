package meeting.system.meetings.core;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.core.dto.*;
import meeting.system.meetings.core.ports.Calendar;
import meeting.system.user.funds.UsersFundsFacade;

import java.math.BigDecimal;

import static io.vavr.control.Either.left;
import static io.vavr.control.Option.of;
import static io.vavr.control.Option.ofOptional;
import static java.util.function.Function.identity;
import static meeting.system.meetings.core.dto.CancelMeetingFailure.MEETING_DOESNT_EXIST;
import static meeting.system.meetings.core.dto.HoldMeetingFailure.CANNOT_HOLD_MEETING_BEFORE_MEETING_DATE;
import static meeting.system.meetings.core.dto.HoldMeetingFailure.MEETING_DOES_NOT_EXIST;
import static meeting.system.meetings.core.dto.SignOnForMeetingFailure.USER_IS_NOT_GROUP_MEMBER_NOR_GROUP_ORGANIZER;

@AllArgsConstructor
@Slf4j
class MeetingsCoreFacadeImpl implements MeetingsCoreFacade {
    private final MeetingRepository meetingRepository;
    private final MeetingsScheduler meetingsScheduler;
    private final MeetingGroupsRoles meetingGroups;
    private final UsersFundsFacade usersFunds;
    private final Calendar calendar;

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(UserId userId, MeetingDraft meetingDraft) {
        return meetingsScheduler.scheduleNewMeeting(userId, meetingDraft);
    }

    @Override
    public Option<HoldMeetingFailure> holdMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return findMeetingById(groupMeetingId)
                .toEither(MEETING_DOES_NOT_EXIST)
                .map(meeting -> holdMeeting(userId, meeting))
                .fold(Option::of, identity());
    }

    private Option<HoldMeetingFailure> holdMeeting(UserId userId, MeetingEntity meeting) {
        if (!meeting.userIsMeetingOrganizer(userId))
            return Option.of(HoldMeetingFailure.USER_IS_NOT_MEETING_ORGANIZER);
        if (calendar.getCurrentDate().isBefore(meeting.getMeetingDate()))
            return Option.of(CANNOT_HOLD_MEETING_BEFORE_MEETING_DATE);
        meetingRepository.delete(meeting);
        return Option.none();
    }

    @Override
    public Either<CancelMeetingFailure, MeetingGotCancelled> cancelMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return findMeetingById(groupMeetingId)
                .toEither(MEETING_DOESNT_EXIST)
                .flatMap(meeting -> cancelMeeting(userId, meeting));
    }

    private Either<CancelMeetingFailure, MeetingGotCancelled> cancelMeeting(UserId userId, MeetingEntity meeting) {
        var currentDate = calendar.getCurrentDate();
        if (currentDate.isAfter(meeting.getMeetingDate()))
            return left(CancelMeetingFailure.CANNOT_CANCEL_MEETING_AFTER_ITS_DATE);
        if (!meeting.userIsMeetingOrganizer(userId))
            return left(CancelMeetingFailure.USER_IS_NOT_MEETING_ORGANIZER);
        meeting
                .getFee()
                .peek(fee -> returnFundsForCancelledMeeting(meeting, fee));
        var meetingGotCancelled = new MeetingGotCancelled(meeting.getMeetingName(), meeting.getMeetingDate(), meeting.getAttendees());
        meetingRepository.deleteById(meeting.getId());
        return Either.right(meetingGotCancelled);
    }

    private void returnFundsForCancelledMeeting(MeetingEntity meeting, BigDecimal fee) {
        meeting
                .toDto()
                .attendees()
                .forEach(userId -> usersFunds.returnFunds(userId, fee));
    }


    @Override
    public Option<SignOnForMeetingFailure> signOnForMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return findMeetingById(groupMeetingId)
                .toEither(SignOnForMeetingFailure.MEETING_DOES_NOT_EXIST)
                .map(meeting -> signOn(meeting, userId))
                .fold(Option::of, identity());
    }

    private Option<MeetingEntity> findMeetingById(GroupMeetingId groupMeetingId) {
        return ofOptional(meetingRepository
                .findById(groupMeetingId.id()));
    }

    private Option<SignOnForMeetingFailure> signOn(MeetingEntity meeting, UserId userId) {
        var meetingGroupId = meeting.getMeetingGroupId();
        if (!isGroupMemberOrOrganizer(userId, meetingGroupId))
            return of(USER_IS_NOT_GROUP_MEMBER_NOR_GROUP_ORGANIZER);
        if (meeting.getFee().isDefined())
            return signOnAndCharge(meeting, userId, meeting.getFee().get());
        else
            return meeting.signOn(userId);
    }

    private Option<SignOnForMeetingFailure> signOnAndCharge(MeetingEntity meeting, UserId userId, BigDecimal fee) {
        Option<SignOnForMeetingFailure> signOnResult = meeting.signOn(userId);
        if (signOnResult.isDefined())
            return signOnResult;
        return usersFunds
                .charge(userId, fee)
                .peek(paymentFailure -> signOffAfterFailedPayment(meeting, userId))
                .map(paymentFailure -> SignOnForMeetingFailure.USER_DOESNT_HAVE_ENOUGH_FUNDS_TO_SIGN_ON_FOR_PAID_MEETING);
    }

    private void signOffAfterFailedPayment(MeetingEntity meeting, UserId userId) {
        meeting
                .signOff(userId)
                .peek(failure -> log.warn("failed to sign off user {} after failed payment attempt for a meeting sign-on, reason: {}", userId.id(), failure));
    }

    @Override
    public Either<SignOffFromMeetingFailure, UserSignedOffFromMeeting> signOffFromMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return findMeetingById(groupMeetingId)
                .toEither(SignOffFromMeetingFailure.MEETING_DOES_NOT_EXIST)
                .flatMap(meeting -> signOff(userId, meeting));
    }

    @Override
    public boolean userIsSignedOn(UserId userId, GroupMeetingId groupMeetingId) {
        return findMeetingById(groupMeetingId)
                .map(meetingEntity -> meetingEntity.isSignedOn(userId))
                .getOrElse(false);
    }

    private Either<SignOffFromMeetingFailure, UserSignedOffFromMeeting> signOff(UserId userId, MeetingEntity meeting) {
        return meeting
                .signOff(userId)
                .peek(us -> returnFunds(userId, meeting));
    }

    private void returnFunds(UserId userId, MeetingEntity meeting) {
        meeting
                .getFee()
                .peek(fee -> usersFunds.returnFunds(userId, fee));
    }

    @Override
    public boolean areAnyMeetingsScheduledForGroup(MeetingGroupId meetingGroupId) {
        return !meetingRepository
                .findByMeetingGroupId(meetingGroupId.id())
                .isEmpty();
    }

    @Override
    public boolean hasFreeSpots(GroupMeetingId groupMeetingId) {
        return findMeetingById(groupMeetingId)
                .map(MeetingEntity::hasFreeSpots)
                .getOrElse(false);
    }

    @Override
    public Option<MeetingDetails> findMeetingDetails(GroupMeetingId groupMeetingId) {
        return findMeetingById(groupMeetingId)
                .map(MeetingEntity::toDto);
    }

    @Override
    public Option<MeetingGroupId> getMeetingGroupId(GroupMeetingId groupMeetingId) {
        return findMeetingDetails(groupMeetingId)
                .map(MeetingDetails::meetingGroupId);
    }

    private boolean isGroupMemberOrOrganizer(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroups.isGroupMember(userId, meetingGroupId) ||
                meetingGroups.isGroupOrganizer(userId, meetingGroupId);
    }
}