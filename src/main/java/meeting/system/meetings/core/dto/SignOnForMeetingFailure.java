package meeting.system.meetings.core.dto;

public enum SignOnForMeetingFailure {
    MEETING_ORGANIZER_CANNOT_SIGN_ON,
    MEETING_DOES_NOT_EXIST,
    NO_FREE_ATTENDEE_SLOTS,
    USER_IS_NOT_GROUP_MEMBER,
    USER_ALREADY_SIGNED_ON,
    USER_DOESNT_HAVE_ENOUGH_FUNDS_TO_SIGN_ON_FOR_PAID_MEETING
}