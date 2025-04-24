package meeting.system.meetings;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.core.MeetingsCoreFacade;
import meeting.system.meetings.core.dto.*;
import meeting.system.meetings.waiting.list.WaitingListFacade;
import meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure;
import meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure;
import meeting.system.notifications.NotificationsFacade;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
public class MeetingsFacade {
    private final MeetingsCoreFacade meetingsCoreFacade;
    private final WaitingListFacade waitingListFacade;
    private final NotificationsFacade meetingsNotifications;

    @Transactional
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(UserId userId, MeetingDraft meetingDraft) {
        return meetingsCoreFacade
                .scheduleNewMeeting(userId, meetingDraft)
                .peek(groupMeetingId -> waitingListFacade.createWaitingList(groupMeetingId, meetingDraft.meetingGroupId()));
    }

    @Transactional
    public Option<HoldMeetingFailure> holdMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return meetingsCoreFacade
                .holdMeeting(userId, groupMeetingId)
                .onEmpty(() -> waitingListFacade.removeWaitingList(groupMeetingId));
    }

    @Transactional
    public Option<CancelMeetingFailure> cancelMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return meetingsCoreFacade
                .cancelMeeting(userId, groupMeetingId)
                .peek(cancelled -> meetingGotCancelled(groupMeetingId, cancelled.meetingName(), cancelled.meetingDate(), cancelled.meetingAttendees()))
                .swap().toOption();
    }

    private void meetingGotCancelled(GroupMeetingId groupMeetingId, String meetingName, LocalDate meetingDate, Set<UserId> meetingAttendees) {
        meetingsNotifications.notifyAboutMeetingCancellation(meetingName, meetingDate, meetingAttendees);
        waitingListFacade
                .removeWaitingList(groupMeetingId)
                .peek(waitingListRemoved -> meetingsNotifications.notifyAboutMeetingCancellation(meetingName, meetingDate, waitingListRemoved.waitingListMembers()));
    }

    @Transactional
    public Option<SignOnForMeetingFailure> signOnForMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return meetingsCoreFacade.signOnForMeeting(userId, groupMeetingId);
    }

    @Transactional
    public Option<SignOffFromMeetingFailure> signOffFromMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        return meetingsCoreFacade
                .signOffFromMeeting(userId, groupMeetingId)
                .peek(this::signOnSomeoneFromWaitingList)
                .swap().toOption();
    }

    private void signOnSomeoneFromWaitingList(UserSignedOffFromMeeting userSignedOffFromMeeting) {
        var meetingName = userSignedOffFromMeeting.meetingName();
        var meetingDate = userSignedOffFromMeeting.meetingDate();
        waitingListFacade
                .signOnSomeoneForMeeting(userSignedOffFromMeeting.meetingId())
                .peek(userId -> meetingsNotifications.notifyAboutBeingSignedOnFromWaitingList(userId, meetingName, meetingDate));
    }

    @Transactional
    public Option<SignOnWaitListFailure> signOnWaitingList(UserId userId, GroupMeetingId groupMeetingId) {
        return waitingListFacade.signOnWaitingList(userId, groupMeetingId);
    }

    @Transactional
    public Option<SignOffFromWaitListFailure> signOffFromWaitingList(UserId userId, GroupMeetingId groupMeetingId) {
        return waitingListFacade.signOffWaitingList(userId, groupMeetingId);
    }

    @Transactional(readOnly = true)
    public Option<MeetingDetails> findMeetingDetails(GroupMeetingId groupMeetingId) {
        return meetingsCoreFacade.findMeetingDetails(groupMeetingId);
    }

    @Transactional(readOnly = true)
    public boolean areAnyMeetingsScheduledForGroup(MeetingGroupId meetingGroupId) {
        return meetingsCoreFacade.areAnyMeetingsScheduledForGroup(meetingGroupId);
    }

}