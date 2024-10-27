package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.SubmitProposalResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubmittingNewGroupProposal extends TestSetup {
    private final UserId groupOrganizer = new UserId(1L);
    @Test
    public void submittingProposalByGroupOrganizerWith3GroupsShouldFail() {
//        given
        groupWasCreated(groupOrganizer);
        groupWasCreated(groupOrganizer);
        groupWasCreated(groupOrganizer);
//        when
        var result = meetingGroupsFacade.submitNewGroupProposal(groupOrganizer, randomGroupProposal());
//        then
        assertEquals(GROUP_LIMIT_PER_USER_EXCEEDED, result);
    }

    @Test
    public void submittingProposalByGroupOrganizerWith2GroupsAnd1WaitingProposalShouldFail() {
//        given
        groupWasCreated(groupOrganizer);
        groupWasCreated(groupOrganizer);
//        and
        newGroupProposalGotSubmitted(groupOrganizer);
//        when
        var result = meetingGroupsFacade.submitNewGroupProposal(groupOrganizer, randomGroupProposal());
//        then
        assertEquals(GROUP_LIMIT_PER_USER_EXCEEDED, result);
    }

    @Test
    public void submittingProposalWithGroupNameOccupiedByExistingMeetingGroupShouldFail() {
//        given
        var groupNameUsedTwice = randomGroupName();
        groupWasCreated(groupOrganizer, groupNameUsedTwice);
//        when
        var result = meetingGroupsFacade.submitNewGroupProposal(groupOrganizer, proposalWithName(groupNameUsedTwice));
//        then
        assertEquals(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS, result);
    }

    @Test
    public void submittingProposalWithNameUsedInOtherProposalShouldFail() {
//        given
        String usedName = randomGroupName();
        newGroupProposalGotSubmitted(groupOrganizer, proposalWithName(usedName));
//        when
        var result = meetingGroupsFacade.submitNewGroupProposal(groupOrganizer, proposalWithName(usedName));
//        then
        assertEquals(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS, result);
    }

    @Test
    public void submittingProposalByGroupOrganizerWithLessThan3GroupsAndProposalsCombinedShouldSucceed() {
//        given
        groupWasCreated(groupOrganizer);
//        and
        newGroupProposalGotSubmitted(groupOrganizer);
//        when
        var result = meetingGroupsFacade.submitNewGroupProposal(groupOrganizer, randomGroupProposal());
//        then
        assertEquals(Success.class, result.getClass());
    }
}