package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.LeaveGroupResult.Success;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.LeaveGroupResult.Failure.GROUP_DOES_NOT_EXIST;
import static meeting.system.meeting.groups.dto.LeaveGroupResult.Failure.USER_IS_NOT_GROUP_MEMBER;
import static org.junit.jupiter.api.Assertions.*;

public class LeavingGroup extends TestSetup {
    private final UserId user = new UserId(1L);

    @Test
    public void groupMemberShouldFailToLeaveNotExistingGroup() {
//        given
        var meetingGroupId = groupWasCreated();
//        and
        userJoinedGroup(user, meetingGroupId);
//        when
        var result = meetingGroupsFacade.leaveGroup(user, randomMeetingGroupId());
//        then
        assertEquals(GROUP_DOES_NOT_EXIST, result);
    }

    @Test
    public void userShouldFailToLeaveTheSameGroupTwice() {
//        given
        var meetingGroupId = groupWasCreated();
//        and
        userJoinedGroup(user, meetingGroupId);
//        and
        userLeftTheGroup(user, meetingGroupId);
//        when
        var result = meetingGroupsFacade.leaveGroup(user, meetingGroupId);
//        then
        assertEquals(USER_IS_NOT_GROUP_MEMBER, result);
    }

    @Test
    public void success() {
//        given
        var meetingGroupId = groupWasCreated();
//        and
        userJoinedGroup(user, meetingGroupId);
//        when
        var result = meetingGroupsFacade.leaveGroup(user, meetingGroupId);
//        then
        assertEquals(new Success(), result);
    }
}