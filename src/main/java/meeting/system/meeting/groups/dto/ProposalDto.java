package meeting.system.meeting.groups.dto;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;

public record ProposalDto(
        GroupProposalId proposalId,
        ProposalAuthorId proposalAuthorId,
        String groupName,
        State state,
        Option<MeetingGroupId> meetingGroupId) {
    public enum State {
        ACCEPTED, REJECTED, WAITING
    }
}