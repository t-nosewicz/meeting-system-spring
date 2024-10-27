package meeting.system.meetings.core.dto;

import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.UserId;

import java.time.LocalDate;

public record UserSignedOffFromMeeting(UserId signedOffUserId, GroupMeetingId meetingId, String meetingName, LocalDate meetingDate) {
}