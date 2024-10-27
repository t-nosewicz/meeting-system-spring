package meeting.system.meetings.waiting.list.dto;

import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.UserId;

import java.util.List;

public record WaitingListDetails(
        GroupMeetingId groupMeetingId,
        List<UserId> waitingListUsers) {
}