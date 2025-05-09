package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.JoinGroupResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoiningGroup extends TestSetup {
    private final UserId user = new UserId(1L);

    @Test
    @DisplayName("joining non-existent group should fail")
    public void fail1() {
//        when
        var result = meetingGroupsFacade.joinGroup(user, randomMeetingGroupId());
//        then
        assertEquals(MEETING_GROUP_DOES_NOT_EXIST, result);
    }

    @Test
    @DisplayName("joining the same group twice should fail")
    public void fail2() {
//        given
        var meetingGroupId = groupWasCreated();
//        and
        userJoinedGroup(user, meetingGroupId);
//        when
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then
        assertEquals(USER_ALREADY_JOINED_GROUP, result);
    }

    @Test
    @DisplayName("user, that is a group organizer, should fail to join the group")
    public void fail3() {
//        given
        var meetingGroupId = groupWasCreated(user);
//        when
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then
        assertEquals(GROUP_ORGANIZER_CANNOT_JOIN_THE_GROUP, result);
    }

    @Test
    public void success() {
//        given
        var meetingGroupId = groupWasCreated();
//        when
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then
        assertEquals(new Success(), result);
    }
}