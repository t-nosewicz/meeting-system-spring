package meeting.system.meeting.groups;

import lombok.Value;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.GroupProposalId;

@Value
class ProposalAccepted {
    GroupProposalId proposalId;
    UserId groupOrganizerId;
    String groupName;
}
