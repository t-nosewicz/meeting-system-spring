package meeting.system.meetings.waiting.list;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.waiting.list.dto.*;

@Slf4j
@AllArgsConstructor
class WaitingListLogs implements WaitingListFacade {
    private final WaitingListFacade waitingListFacade;

    @Override
    public Option<CreateWaitingListFailure> createWaitingList(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId) {
        log.info("creating waiting list for meeting {} for group {}", groupMeetingId.id(), meetingGroupId.id());
        return waitingListFacade
                .createWaitingList(groupMeetingId, meetingGroupId)
                .peek(failure -> log.warn("failed to create waiting list for meeting {}, reason: {}",groupMeetingId.id(), failure))
                .onEmpty(() -> log.info("waiting list for meeting {} got created", groupMeetingId.id()));
    }

    @Override
    public Either<MeetingSignOnFromWaitingListFailure, UserId> signOnSomeoneForMeeting(GroupMeetingId groupMeetingId) {
        log.info("signing on for a meeting {} someone from a waiting list", groupMeetingId.id());
        return waitingListFacade
                .signOnSomeoneForMeeting(groupMeetingId)
                .peekLeft(failure -> log.info("failed to sign on meeting {} anyone from waiting list, reason: {}", groupMeetingId.id(), failure))
                .peek(userId -> log.info("user {} got signed on for meeting {} from waiting list", userId.id(), groupMeetingId.id()));
    }

    @Override
    public Option<SignOnWaitListFailure> signOnWaitingList(UserId userId, GroupMeetingId groupMeetingId) {
        log.info("user {} is trying to sign on for a meeting {} waiting list", userId.id(), groupMeetingId.id());
        return waitingListFacade
                .signOnWaitingList(userId, groupMeetingId)
                .peek(failure -> log.info("user {} failed to sign on for meeting {} waiting list, reason: {}", userId.id(), groupMeetingId.id(), failure))
                .onEmpty(() -> log.info("user {} signed on for a meeting {} waiting list", userId.id(), groupMeetingId.id()));
    }

    @Override
    public Option<SignOffFromWaitListFailure> signOffWaitingList(UserId userId, GroupMeetingId groupMeetingId) {
        log.info("user {} is trying to sign off from waiting list for meeting {}", userId.id(), groupMeetingId.id());
        return waitingListFacade
                .signOffWaitingList(userId, groupMeetingId)
                .peek(failure -> log.info("user {} failed to sign off from meeting {} waiting list, reason: {}", userId.id(), groupMeetingId.id(), failure))
                .onEmpty(() -> log.info("user {} signed off from meeting {} waiting list", userId.id(), groupMeetingId.id()));
    }

    @Override
    public Either<RemoveWaitingListFailure, WaitingListRemoved> removeWaitingList(GroupMeetingId groupMeetingId) {
        log.info("removing waiting list for meeting {}", groupMeetingId.id());
        return waitingListFacade
                .removeWaitingList(groupMeetingId)
                .peekLeft(failure -> log.info("failed to remove waiting list for meeting {}, reason: {}", groupMeetingId.id(), failure))
                .peek(waitingListRemoved -> log.info("waiting list for meeting {} got removed", groupMeetingId.id()));
    }
}