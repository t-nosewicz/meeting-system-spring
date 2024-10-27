package meeting.system.meetings.waiting.list.dto;

public enum SignOnWaitListFailure {
    USER_IS_NOT_GROUP_MEMBER,
    WAITING_LIST_DOES_NOT_EXIST,
    MEETING_HAS_FREE_SLOTS,
    USER_ALREADY_IS_ON_WAITING_LIST,
    USER_ALREADY_IS_SIGNED_ON_TO_MEETING
}