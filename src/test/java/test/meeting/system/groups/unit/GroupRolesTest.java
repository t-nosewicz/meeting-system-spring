package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupRolesTest extends TestSetup {
    private final UserId groupOrganizerId = randomUserId();
    private final UserId userId = randomUserId();

    @Test
    @DisplayName("user that submitted a group proposal should be a group organizer and group member, but not a regular group member")
    public void test1() {
//        when
        var groupId = groupWasCreated(groupOrganizerId);
//        then
        assertTrue(meetingGroupsFacade.isGroupOrganizer(groupOrganizerId, groupId));
//        and
        assertTrue(meetingGroupsFacade.isGroupMember(groupOrganizerId, groupId));
//        and
        assertFalse(meetingGroupsFacade.isRegularGroupMember(groupOrganizerId, groupId));
    }

    @Test
    @DisplayName("user that joined a group should be a regular group member and group member, but not a group organizer")
    public void test2() {
//        given
        var groupId = groupWasCreated();
//        when
        userJoinedGroup(userId, groupId);
//        then
        assertTrue(meetingGroupsFacade.isRegularGroupMember(userId, groupId));
//        and
        assertTrue(meetingGroupsFacade.isGroupMember(userId, groupId));
//        and
        assertFalse(meetingGroupsFacade.isGroupOrganizer(userId, groupId));
    }
}