package meeting.system.meetings.waiting.list.dto;

import meeting.system.commons.dto.UserId;

import java.util.List;

public record WaitingListRemoved(List<UserId> waitingListMembers) {
}