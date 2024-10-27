package meeting.system.meeting.groups.dto;

public sealed interface JoinGroupResult {

    record Success() implements JoinGroupResult {
    }

    enum Failure implements JoinGroupResult {
        USER_ALREADY_JOINED_GROUP,
        MEETING_GROUP_DOES_NOT_EXIST,
        GROUP_ORGANIZER_CANNOT_JOIN_THE_GROUP
    }
}