package meeting.system.meeting.groups.dto;

public sealed interface RejectProposalResult {

    record Success() implements RejectProposalResult {
    }

    enum Failure implements RejectProposalResult {
        PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST,
        PROPOSAL_IS_ALREADY_ACCEPTED,
        PROPOSAL_IS_ALREADY_REJECTED
    }
}
