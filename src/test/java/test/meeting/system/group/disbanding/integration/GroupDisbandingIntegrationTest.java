package test.meeting.system.group.disbanding.integration;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.DisbandGroupResult.Failure.GROUP_HAS_SCHEDULED_MEETINGS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupDisbandingIntegrationTest extends GroupDisbandingIntegrationtSetup {
    private final UserId userId = new UserId(1L);
    private final MeetingGroupId groupId = new MeetingGroupId(1L);

    @Test
    @DisplayName("group disbanding should fail if there are meetings scheduled for that group")
    public void fail() throws Exception {
//        given
        groupHasScheduledMeetings(groupId);
//        when
        var result = disbandGroup(userId, groupId);
//        then
        assertEquals(Option.of(GROUP_HAS_SCHEDULED_MEETINGS), result);
    }

    @Test
    @DisplayName("group disbanding should fail if meeting groups module fails to disband the group")
    public void fail2() throws Exception {
//        given
        groupHasNoScheduledMeetings(groupId);
//        and
        meetingGroupsModuleFailsToDisbandTheGroup(groupId, userId, failure());
//        when
        var result = disbandGroup(userId, groupId);
//        then
        assertEquals(Option.of(failure()), result);
    }

    @Test
    public void success() throws Exception {
//        given
        groupHasNoScheduledMeetings(groupId);
//        and
        meetingGroupsModuleSucceedsToDisbandTheGroup(groupId, userId);
//        when
        var result = disbandGroup(userId, groupId);
//        then
        assertEquals(Option.none(), result);
    }


}