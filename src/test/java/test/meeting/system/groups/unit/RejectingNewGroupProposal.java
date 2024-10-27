package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.RejectProposalResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RejectingNewGroupProposal extends TestSetup {
    private final UserId administrator = new UserId(1L);

    @Test
    public void administratorShouldFailToRejectProposalThatDoesNotExist() {
//        when
        var result = meetingGroupsFacade.rejectProposal(administrator, randomProposalId());
//        then
        assertEquals(PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST, result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatAlreadyGotRejected() {
//        given
        var proposalId = newGroupProposalGotSubmitted();
//        and
        proposalGotRejected(administrator, proposalId);
//        when
        var result = meetingGroupsFacade.rejectProposal(administrator, proposalId);
//        then
        assertEquals(PROPOSAL_IS_ALREADY_REJECTED, result);
    }

    @Test
    public void administratorShouldFailToRejectProposalThatAlreadyGotAccepted() {
//        given
        var proposalId = newGroupProposalGotSubmitted();
//        and
        proposalGotAccepted(administrator, proposalId);
//        when
        var result = meetingGroupsFacade.rejectProposal(administrator, proposalId);
//        then
        assertEquals(PROPOSAL_IS_ALREADY_ACCEPTED, result);
    }

    @Test
    public void administratorShouldSuccessfullyRejectSubmittedProposal() {
//        given
        var proposalId = newGroupProposalGotSubmitted();
//        when
        var result = meetingGroupsFacade.rejectProposal(administrator, proposalId);
//        then
        assertEquals(new Success(), result);
    }
}