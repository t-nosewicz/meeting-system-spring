package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.LeaveGroupResult.Success;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.LeaveGroupResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LeavingGroup extends TestSetup {
    private final UserId user = new UserId(1L);

    @Test
    @DisplayName("user should fail to leave non-existent group")
    public void fail1() {
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
    @DisplayName("user, that never joined the group, should fail to leave it")
    public void fail2() {
//        given
        var meetingGroupId = groupWasCreated();
//        when
        var result = meetingGroupsFacade.leaveGroup(user, meetingGroupId);
//        then
        assertEquals(USER_IS_NOT_A_REGULAR_GROUP_MEMBER, result);
    }

    @Test
    @DisplayName("user, that is a regular group member, should fail to leave the same group twice")
    public void fail3() {
//        given
        var meetingGroupId = groupWasCreated();
//        and
        userJoinedGroup(user, meetingGroupId);
//        and
        userLeftTheGroup(user, meetingGroupId);
//        when
        var result = meetingGroupsFacade.leaveGroup(user, meetingGroupId);
//        then
        assertEquals(USER_IS_NOT_A_REGULAR_GROUP_MEMBER, result);
    }

    @Test
    @DisplayName("user, that is a group organizer, should fail to leave the group")
    public void fail4() {
//        given
        var meetingGroupId = groupWasCreated(user);
//        when
        var result = meetingGroupsFacade.leaveGroup(user, meetingGroupId);
//        then
        assertEquals(GROUP_ORGANIZER_CANNOT_LEAVE_THE_GROUP, result);
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