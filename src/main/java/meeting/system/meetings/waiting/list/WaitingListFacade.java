package meeting.system.meetings.waiting.list;

import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.waiting.list.dto.*;
import org.springframework.transaction.annotation.Transactional;

public interface WaitingListFacade {

    Option<CreateWaitingListFailure> createWaitingList(GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId);

    Either<MeetingSignOnFromWaitingListFailure, UserId> signOnSomeoneForMeeting(GroupMeetingId groupMeetingId);

    Option<SignOnWaitListFailure> signOnWaitingList(UserId userId, GroupMeetingId groupMeetingId);

    Option<SignOffFromWaitListFailure> signOffWaitingList(UserId userId, GroupMeetingId groupMeetingId);

    Either<RemoveWaitingListFailure, WaitingListRemoved> removeWaitingList(GroupMeetingId groupMeetingId);
}