package meeting.system.meeting.groups.dto;

public sealed interface RemoveProposalResult {

    record Success() implements RemoveProposalResult {
    }

    enum Failure implements RemoveProposalResult {
        USER_IS_NOT_PROPOSAL_AUTHOR,
        PROPOSAL_ALREADY_PROCESSED,
        PROPOSAL_DOES_NOT_EXIST
    }
}
