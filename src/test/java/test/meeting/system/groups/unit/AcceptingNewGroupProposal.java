package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.AcceptProposalResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AcceptingNewGroupProposal extends TestSetup {
    private final UserId administrator = randomUserId();
    private final UserId groupOrganizer = randomUserId();

    @Test
    public void shouldFailToAcceptProposalThatWasAlreadyAccepted() {
//        given
        var proposalId = newGroupProposalGotSubmitted();
//        and
        proposalGotAccepted(proposalId);
//        when
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
//        then
        assertEquals(PROPOSAL_WAS_ALREADY_ACCEPTED, result);
    }

    @Test
    public void shouldFailAcceptProposalThatAlreadyGotRejected() {
//        given
        var proposalId = newGroupProposalGotSubmitted();
//        and
        proposalGotRejected(proposalId);
//        when
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
//        then
        assertEquals(PROPOSAL_WAS_ALREADY_REJECTED, result);
    }

    @Test
    public void shouldFailToAcceptProposalThatDoesNotExist() {
//        when
        var result = meetingGroupsFacade.acceptProposal(administrator, randomProposalId());
//        then
        assertEquals(PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST, result);
    }

    @Test
    public void success() {
//        given
        var proposalId = newGroupProposalGotSubmitted(groupOrganizer);
//        when
        var result = meetingGroupsFacade.acceptProposal(administrator, proposalId);
//        then
        assertEquals(Success.class, result.getClass());
    }
}