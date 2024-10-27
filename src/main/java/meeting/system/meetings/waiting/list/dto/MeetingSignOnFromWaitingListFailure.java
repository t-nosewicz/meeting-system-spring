package meeting.system.meetings.waiting.list.dto;

public enum MeetingSignOnFromWaitingListFailure {
    WAITING_LIST_IS_EMPTY,
    WAITING_LIST_DOESNT_EXIST,
    NONE_OF_PEOPLE_FROM_WAITING_LIST_WERE_ABLE_TO_SIGN_ON,
    MEETING_HAS_NO_FREE_SLOTS
}