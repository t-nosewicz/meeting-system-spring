package meeting.system.meetings.core.dto;

public enum CancelMeetingFailure {
    USER_IS_NOT_MEETING_ORGANIZER,
    MEETING_DOESNT_EXIST,
    CANNOT_CANCEL_MEETING_AFTER_ITS_DATE
}