package meeting.system.meetings.core;

import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.core.dto.*;
import org.springframework.transaction.annotation.Transactional;

public interface MeetingsCoreFacade extends MeetingsCoreQueryFacade {

    Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(UserId userId, MeetingDraft meetingDraft);

    Option<HoldMeetingFailure> holdMeeting(UserId userId, GroupMeetingId groupMeetingId);

    Either<CancelMeetingFailure, MeetingGotCancelled> cancelMeeting(UserId userId, GroupMeetingId groupMeetingId);

    Option<SignOnForMeetingFailure> signOnForMeeting(UserId userId, GroupMeetingId groupMeetingId);

    Either<SignOffFromMeetingFailure, UserSignedOffFromMeeting> signOffFromMeeting(UserId userId, GroupMeetingId groupMeetingId);

    boolean userIsSignedOn(UserId userId, GroupMeetingId groupMeetingId);
}