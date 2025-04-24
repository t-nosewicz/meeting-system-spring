package test.meeting.system.groups.unit;


import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.RemoveProposalResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemovingNewGroupProposal extends TestSetup {
    private final UserId proposalAuthor = new UserId(1L);
    private final UserId administrator = new UserId(2L);

    @Test
    @DisplayName("administrator should fail to remove non-existent proposal")
    public void fail1() {
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(proposalAuthor, randomProposalId());
//        then
        assertEquals(PROPOSAL_DOES_NOT_EXIST, result);
    }

    @Test
    @DisplayName("user, that is not a proposal author, should fail to remove the proposal")
    public void fail2() {
//        given
        var proposalId = newGroupProposalGotSubmitted(proposalAuthor);
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(randomUserId(), proposalId);
//        then
        assertEquals(USER_IS_NOT_PROPOSAL_AUTHOR, result);
    }

    @Test
    @DisplayName("user, that is a proposal author, should fail to remove accepted proposal")
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
    @DisplayName("user, that is a proposal author, should fail to remove rejected proposal")
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
    public void success() {
//        given
        var proposalId = newGroupProposalGotSubmitted(proposalAuthor);
//        when
        var result = meetingGroupsFacade.removeWaitingProposal(proposalAuthor, proposalId);
//        then
        assertEquals(new Success(), result);
    }
}