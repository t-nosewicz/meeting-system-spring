package meeting.system.meeting.groups.dto;

public sealed interface SubmitProposalResult {

    record Success(GroupProposalId groupProposalId) implements SubmitProposalResult {
    }

    enum Failure implements SubmitProposalResult {
        GROUP_LIMIT_PER_USER_EXCEEDED,
        MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS,
        PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS
    }
}
