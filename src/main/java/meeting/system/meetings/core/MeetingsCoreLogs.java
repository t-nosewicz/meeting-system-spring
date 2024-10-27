package meeting.system.meetings.core;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.core.dto.*;

@AllArgsConstructor
@Slf4j
class MeetingsCoreLogs implements MeetingsCoreFacade {
    private final MeetingsCoreFacade meetingsFacade;

    @Override
    public Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(UserId userId, MeetingDraft meetingDraft) {
        log.info("user {} is trying to schedule a meeting {}", userId.id(), meetingDraft);
        return meetingsFacade
                .scheduleNewMeeting(userId, meetingDraft)
                .peek(groupMeetingId -> log.info("meeting {} got scheduled", groupMeetingId.id()))
                .peekLeft(failure -> log.info("failed to schedule meeting, reason: " + failure));
    }

    @Override
    public Either<CancelMeetingFailure, MeetingGotCancelled> cancelMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        log.info("user {} is trying to cancel meeting {}", userId.id(), groupMeetingId.id());
        return meetingsFacade
                .cancelMeeting(userId, groupMeetingId)
                .peekLeft(failure -> log.info("user {} failed to cancel meeting {}, reason: {}", userId.id(), groupMeetingId.id(), failure))
                .peek(meetingGotCancelled -> log.info("user {} cancelled meeting {}", userId.id(), groupMeetingId.id()));
    }

    @Override
    public Option<HoldMeetingFailure> holdMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        log.info("user {} is trying to hold meeting {}", userId.id(), groupMeetingId.id());
        return meetingsFacade
                .holdMeeting(userId, groupMeetingId)
                .peek(failure -> log.info("user {} failed to hold meeting {}, reason: {}", userId.id(), groupMeetingId.id(), failure))
                .onEmpty(() -> log.info("user {} held meeting {}", userId.id(), groupMeetingId.id()));
    }

    @Override
    public Option<SignOnForMeetingFailure> signOnForMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        log.info("user {} is trying to sign on meeting {}", userId.id(), groupMeetingId.id());
        return meetingsFacade
                .signOnForMeeting(userId, groupMeetingId)
                .peek(failure -> log.info("user {} failed to sign on for meeting {}, reason: {}", userId.id(), groupMeetingId.id(), failure))
                .onEmpty(() -> log.info("user {} signed on for meeting {}", userId.id(), groupMeetingId.id()));
    }

    @Override
    public Either<SignOffFromMeetingFailure, UserSignedOffFromMeeting> signOffFromMeeting(UserId userId, GroupMeetingId groupMeetingId) {
        log.info("user {} is trying to sign off from meeting {}", userId.id(), groupMeetingId.id());
        return meetingsFacade
                .signOffFromMeeting(userId, groupMeetingId)
                .peekLeft(failure -> log.info("user {} failed to sign off meeting {}, reason: {}", userId.id(), groupMeetingId.id(), failure))
                .peek(userSignedOffFromMeeting -> log.info("user got {} signed off meeting {}", userId.id(), groupMeetingId.id()));
    }

    @Override
    public boolean userIsSignedOn(UserId userId, GroupMeetingId groupMeetingId) {
        log.info("checking if user {} is signed on to meeting {}", userId.id(), groupMeetingId.id());
        boolean isSignedOn = meetingsFacade.userIsSignedOn(userId, groupMeetingId);
        if (isSignedOn)
            log.info("user {} IS signed on", userId.id());
        log.info("user {} is NOT signed on", userId.id());
        return isSignedOn;
    }

    @Override
    public boolean areAnyMeetingsScheduledForGroup(MeetingGroupId meetingGroupId) {
        log.info("checking if there are any meetings scheduled for a group {}", meetingGroupId.id());
        boolean groupHasScheduledMeetings = meetingsFacade.areAnyMeetingsScheduledForGroup(meetingGroupId);
        if (groupHasScheduledMeetings)
            log.info("group {} has scheduled meetings", meetingGroupId.id());
        log.info("group {} has no scheduled meetings", meetingGroupId.id());
        return groupHasScheduledMeetings;
    }

    @Override
    public boolean hasFreeSpots(GroupMeetingId groupMeetingId) {
        log.info("checking if meeting {} has free spots", groupMeetingId.id());
        boolean hasFreeSpots = meetingsFacade.hasFreeSpots(groupMeetingId);
        if (hasFreeSpots)
            log.info("meeting {} has free spots", groupMeetingId.id());
        log.info("meeting {} doesn't have free spots", groupMeetingId.id());
        return hasFreeSpots;
    }

    @Override
    public Option<MeetingDetails> findMeetingDetails(GroupMeetingId groupMeetingId) {
        return meetingsFacade.findMeetingDetails(groupMeetingId);
    }

    @Override
    public Option<MeetingGroupId> getMeetingGroupId(GroupMeetingId groupMeetingId) {
        return meetingsFacade.getMeetingGroupId(groupMeetingId);
    }
}