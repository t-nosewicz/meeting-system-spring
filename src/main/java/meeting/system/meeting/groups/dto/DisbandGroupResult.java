package meeting.system.meeting.groups.dto;

public sealed interface DisbandGroupResult {

    record Success() implements DisbandGroupResult {
    }

    enum Failure implements DisbandGroupResult {
        USER_IS_NOT_GROUP_ORGANIZER,
        GROUP_DOESNT_EXIST,
        GROUP_HAS_SCHEDULED_MEETINGS
    }
}
