package meeting.system.meeting.groups.dto;

public sealed interface LeaveGroupResult {

    record Success() implements LeaveGroupResult {
    }

    enum Failure implements LeaveGroupResult {
        GROUP_DOES_NOT_EXIST, USER_IS_NOT_A_REGULAR_GROUP_MEMBER, GROUP_ORGANIZER_CANNOT_LEAVE_THE_GROUP
    }
}
