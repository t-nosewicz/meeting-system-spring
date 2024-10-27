package meeting.system.meetings.waiting.list;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.core.MeetingsCoreFacade;
import meeting.system.meetings.waiting.list.dto.*;

import java.util.function.Function;

import static io.vavr.control.Option.of;
import static java.util.function.Function.identity;
import static meeting.system.meetings.core.dto.SignOnForMeetingFailure.NO_FREE_ATTENDEE_SLOTS;
import static meeting.system.meetings.waiting.list.dto.MeetingSignOnFromWaitingListFailure.MEETING_HAS_NO_FREE_SLOTS;
import static meeting.system.meetings.waiting.list.dto.MeetingSignOnFromWaitingListFailure.NONE_OF_PEOPLE_FROM_WAITING_LIST_WERE_ABLE_TO_SIGN_ON;
import static meeting.system.meetings.waiting.list.dto.RemoveWaitingListFailure.WAITING_LIST_NOT_FOUND;
import static meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure.*;

@Slf4j
@AllArgsConstructor
class WaitingListFacadeImpl implements WaitingListFacade {
    private final WaitingListRepository waitingListRepository;
    private final MeetingsCoreFacade meetingsCoreFacade;
    private final MeetingGroupsRoles meetingGroupsRoles;

    @Override
    public Option<CreateWaitingListFailure> createWaitingList(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId) {
        if (waitingListRepository.existsByMeetingId(groupMeetingId.id()))
            return of(CreateWaitingListFailure.WAITING_LIST_ALREADY_EXIST);
        waitingListRepository.save(WaitingListEntity.create(groupMeetingId, meetingGroupId));
        return Option.none();
    }

    @Override
    public Either<MeetingSignOnFromWaitingListFailure, UserId> signOnSomeoneForMeeting(GroupMeetingId groupMeetingId) {
        return waitingListRepository
                .findByMeetingId(groupMeetingId.id())
                .toEither(MeetingSignOnFromWaitingListFailure.WAITING_LIST_DOESNT_EXIST)
                .flatMap(this::signOnSomeoneToMeetingFromWaitingList);
    }

    private Either<MeetingSignOnFromWaitingListFailure, UserId> signOnSomeoneToMeetingFromWaitingList(WaitingListEntity waitingList) {
        if (waitingList.isEmpty())
            return Either.left(MeetingSignOnFromWaitingListFailure.WAITING_LIST_IS_EMPTY);
        var groupMeetingId = waitingList.getMeetingId();
        for (var groupMemberId : waitingList.getWaitingListMembers()) {
            var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId);
            if (result.isEmpty()) {
                waitingList.signOff(groupMemberId);
                return Either.right(groupMemberId);
            }
            if (result.get().equals(NO_FREE_ATTENDEE_SLOTS))
                return Either.left(MEETING_HAS_NO_FREE_SLOTS);
        }
        return Either.left(NONE_OF_PEOPLE_FROM_WAITING_LIST_WERE_ABLE_TO_SIGN_ON);
    }

    @Override
    public Option<SignOnWaitListFailure> signOnWaitingList(UserId userId, GroupMeetingId groupMeetingId) {
        if (meetingsCoreFacade.hasFreeSpots(groupMeetingId))
            return of(MEETING_HAS_FREE_SLOTS);
        if (meetingsCoreFacade.userIsSignedOn(userId, groupMeetingId))
            return of(USER_ALREADY_IS_SIGNED_ON_TO_MEETING);
        return waitingListRepository
                .findByMeetingId(groupMeetingId.id())
                .toEither(SignOnWaitListFailure.WAITING_LIST_DOES_NOT_EXIST)
                .map(waitingList -> signOnWaitingList(userId, waitingList))
                .fold(Option::of, identity());
    }

    private Option<SignOnWaitListFailure> signOnWaitingList(UserId userId, WaitingListEntity waitingList) {
        var meetingGroupId = waitingList.getGroupId();
        if (!meetingGroupsRoles.isGroupMember(userId, meetingGroupId))
            return of(USER_IS_NOT_GROUP_MEMBER);
        return waitingList.signOn(userId);
    }

    @Override
    public Option<SignOffFromWaitListFailure> signOffWaitingList(UserId userId, GroupMeetingId groupMeetingId) {
        return waitingListRepository
                .findByMeetingId(groupMeetingId.id())
                .map(waitingList -> waitingList.signOff(userId))
                .toEither(SignOffFromWaitListFailure.WAITING_LIST_DOESNT_EXIST)
                .fold(Option::of, Function.identity());
    }

    @Override
    public Either<RemoveWaitingListFailure, WaitingListRemoved> removeWaitingList(GroupMeetingId groupMeetingId) {
        return waitingListRepository
                .findByMeetingId(groupMeetingId.id())
                .toEither(WAITING_LIST_NOT_FOUND)
                .map(waitingList -> {
                    var waitingListRemoved = new WaitingListRemoved(waitingList.getWaitingListMembers());
                    waitingListRepository.deleteByMeetingId(groupMeetingId.id());
                    return waitingListRemoved;
                });
    }
}