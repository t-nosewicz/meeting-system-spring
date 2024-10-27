package test.meeting.system.groups.integration;

import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.DisbandGroupResult.Success;
import meeting.system.meeting.groups.dto.NewGroupProposal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MeetingGroupsIntegrationTest extends MeetingGroupsIntegrationSetup {
    private final UserId groupOrganizerId = randomUserId();
    private final UserId administratorId = randomUserId();
    private final List<UserId> users = List.of(randomUserId(), randomUserId(), randomUserId());
    private final NewGroupProposal newGroupProposal = new NewGroupProposal("group name");

    @Test
    public void test() throws Exception {
        var groupProposalId = submitGroupProposal(groupOrganizerId, newGroupProposal)
                .get()
                .groupProposalId();
        var groupId = acceptProposal(administratorId, groupProposalId)
                .get()
                .meetingGroupId();
        joinUsersToTheGroup(groupId, users);
        usersLeaveTheGroup(users, groupId);
        var disbandGroupResult = disbandTheGroup(groupOrganizerId, groupId);
        assertEquals(new Success(), disbandGroupResult);
    }

    private List<UserId> joinUsersToTheGroup(MeetingGroupId groupId, List<UserId> userIds) throws Exception {
        for (var userId : userIds) {
            var result = joinGroup(userId, groupId);
            assertTrue(result.isEmpty());
        }
        return userIds;
    }

    private void usersLeaveTheGroup(List<UserId> joinedUsers, MeetingGroupId groupId) throws Exception {
        for (var userId : joinedUsers) {
            var result = leaveGroup(userId, groupId);
            assertTrue(result.isEmpty());
        }
    }
}