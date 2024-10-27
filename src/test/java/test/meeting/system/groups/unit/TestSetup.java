package test.meeting.system.groups.unit;

import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsConfiguration;
import meeting.system.meeting.groups.MeetingGroupsFacade;
import meeting.system.meeting.groups.dto.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSetup {
    protected MeetingGroupsFacade meetingGroupsFacade;

    @BeforeEach
    public void setup() {
        meetingGroupsFacade = new MeetingGroupsConfiguration().inMemoryMeetingGroupsFacade();
    }

    protected MeetingGroupId groupWasCreated() {
        return groupWasCreated(randomUserId(), randomGroupName());
    }

    protected MeetingGroupId groupWasCreated(UserId groupOrganizerId) {
        return groupWasCreated(groupOrganizerId, randomGroupName());
    }

    protected MeetingGroupId groupWasCreated(UserId groupOrganizerId, String meetingGroupName) {
        return groupWasCreated(groupOrganizerId, proposalWithName(meetingGroupName));
    }

    protected MeetingGroupId groupWasCreated(UserId groupOrganizerId, NewGroupProposal proposal) {
        var submitProposalSuccess = (SubmitProposalResult.Success) meetingGroupsFacade.submitNewGroupProposal(groupOrganizerId, proposal);
        var acceptProposalSuccess = (AcceptProposalResult.Success) meetingGroupsFacade.acceptProposal(randomUserId(), submitProposalSuccess.groupProposalId());
        return acceptProposalSuccess.meetingGroupId();
    }

    protected GroupProposalId newGroupProposalGotSubmitted() {
        var success = (SubmitProposalResult.Success) meetingGroupsFacade.submitNewGroupProposal(randomUserId(), randomGroupProposal());
        return success.groupProposalId();
    }

    protected GroupProposalId newGroupProposalGotSubmitted(UserId groupOrganizerId) {
        return newGroupProposalGotSubmitted(groupOrganizerId, randomGroupProposal());
    }

    protected GroupProposalId newGroupProposalGotSubmitted(UserId groupOrganizerId, NewGroupProposal newGroupProposal) {
        var result = (SubmitProposalResult.Success) meetingGroupsFacade.submitNewGroupProposal(groupOrganizerId, newGroupProposal);
        return  result.groupProposalId();
    }

    protected MeetingGroupId proposalGotAccepted(GroupProposalId proposalId) {
        return proposalGotAccepted(randomUserId(), proposalId);
    }

    protected MeetingGroupId proposalGotAccepted(UserId administrator, GroupProposalId proposalId) {
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
        assertEquals(AcceptProposalResult.Success.class, result.getClass());
        return ((AcceptProposalResult.Success) result).meetingGroupId();
    }

    protected void proposalGotRejected(GroupProposalId proposalId) {
        proposalGotRejected(randomUserId(), proposalId);
    }

    protected void proposalGotRejected(UserId administrator, GroupProposalId proposalId) {
        assertEquals(new RejectProposalResult.Success(), meetingGroupsFacade.rejectProposal(administrator, proposalId));
    }

    protected void userJoinedGroup(UserId user, MeetingGroupId meetingGroupId) {
        assertEquals(new JoinGroupResult.Success(), meetingGroupsFacade.joinGroup(user, meetingGroupId));
    }

    protected void userLeftTheGroup(UserId user, MeetingGroupId meetingGroupId) {
        assertEquals(new LeaveGroupResult.Success(), meetingGroupsFacade.leaveGroup(user, meetingGroupId));
    }

    protected void groupGotDisbanded(UserId userId, MeetingGroupId meetingGroupId) {
        assertEquals(new DisbandGroupResult.Success(), meetingGroupsFacade.disbandGroup(userId, meetingGroupId));
    }

    protected NewGroupProposal randomGroupProposal() {
        return new NewGroupProposal(randomGroupName());
    }

    protected UserId randomUserId() {
        return new UserId(randomLong());
    }

    protected NewGroupProposal proposalWithName(String groupName) {
        return new NewGroupProposal(groupName);
    }

    protected GroupProposalId randomProposalId() {
        return new GroupProposalId(randomLong());
    }

    protected String randomGroupName() {
        return UUID.randomUUID().toString();
    }

    protected MeetingGroupId randomMeetingGroupId() {
        return new MeetingGroupId(randomLong());
    }

    protected long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }
}