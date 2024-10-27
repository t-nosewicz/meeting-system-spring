package meeting.system.meetings.core.dto;

import meeting.system.commons.dto.UserId;

import java.time.LocalDate;
import java.util.Set;

public record MeetingGotCancelled(String meetingName, LocalDate meetingDate, Set<UserId> meetingAttendees) {
}