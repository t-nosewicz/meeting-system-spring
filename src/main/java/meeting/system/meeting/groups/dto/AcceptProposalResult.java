package meeting.system.meeting.groups.dto;

import meeting.system.commons.dto.MeetingGroupId;

public sealed interface AcceptProposalResult {

    record Success(MeetingGroupId meetingGroupId) implements AcceptProposalResult {
    }

    enum Failure implements AcceptProposalResult {
        PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST,
        PROPOSAL_WAS_ALREADY_ACCEPTED,
        PROPOSAL_WAS_ALREADY_REJECTED
    }
}