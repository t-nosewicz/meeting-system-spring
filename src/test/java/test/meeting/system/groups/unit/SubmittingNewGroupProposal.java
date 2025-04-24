package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.SubmitProposalResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubmittingNewGroupProposal extends TestSetup {
    private final UserId groupOrganizer = new UserId(1L);

    @Test
    @DisplayName("submitting a proposal by a group organizer with 3 groups should fail")
    public void fail1() {
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
    @DisplayName("submitting a proposal by a group organizer with 2 groups and 1 waiting proposal should fail")
    public void fail2() {
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
    @DisplayName("submitting a proposal with a name, that is already used in existing group, should fail")
    public void fail3() {
//        given
        var groupNameUsedTwice = randomGroupName();
        groupWasCreated(groupOrganizer, groupNameUsedTwice);
//        when
        var result = meetingGroupsFacade.submitNewGroupProposal(groupOrganizer, proposalWithName(groupNameUsedTwice));
//        then
        assertEquals(MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS, result);
    }

    @Test
    @DisplayName("submitting a proposal with a name, that is already used in other proposal, should fail")
    public void fail4() {
//        given
        String usedName = randomGroupName();
        newGroupProposalGotSubmitted(groupOrganizer, proposalWithName(usedName));
//        when
        var result = meetingGroupsFacade.submitNewGroupProposal(groupOrganizer, proposalWithName(usedName));
//        then
        assertEquals(PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS, result);
    }

    @Test
    public void success() {
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