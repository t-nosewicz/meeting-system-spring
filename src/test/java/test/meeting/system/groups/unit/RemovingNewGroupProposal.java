package test.meeting.system.groups.unit;


import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.RemoveProposalResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemovingNewGroupProposal extends TestSetup {
    private final UserId proposalAuthor = new UserId(1L);
    private final UserId administrator = new UserId(2L);

    @Test
    public void shouldFailToRemoveNotExistingProposal() {
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(randomUserId(), randomProposalId());
//        then
        assertEquals(PROPOSAL_DOES_NOT_EXIST, result);
    }

    @Test
    public void userThatIsNotProposalAuthorShouldFailToRemoveWaitingProposal() {
//        given
        var proposalId = newGroupProposalGotSubmitted(proposalAuthor);
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(randomUserId(), proposalId);
//        then
        assertEquals(USER_IS_NOT_PROPOSAL_AUTHOR, result);
    }

    @Test
    public void shouldFailToRemoveAcceptedProposal() {
//        given
        var proposalId = newGroupProposalGotSubmitted(proposalAuthor);
//        and
        proposalGotAccepted(administrator, proposalId);
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(proposalAuthor, proposalId);
//        then
        assertEquals(PROPOSAL_ALREADY_PROCESSED, result);
    }

    @Test
    public void shouldFailToRemoveRejectedProposal() {
//        given
        var proposalId = newGroupProposalGotSubmitted(proposalAuthor);
//        and
        proposalGotRejected(administrator, proposalId);
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(proposalAuthor, proposalId);
//        then
        assertEquals(PROPOSAL_ALREADY_PROCESSED, result);
    }

    @Test
    public void shouldSuccessfullyRemoveWaitingProposal() {
//        given
        var proposalId = newGroupProposalGotSubmitted(proposalAuthor);
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(proposalAuthor, proposalId);
//        then
        assertEquals(new Success(), result);
    }
}