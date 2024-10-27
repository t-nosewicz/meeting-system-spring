package meeting.system.meeting.groups;

import lombok.AllArgsConstructor;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.GroupProposalId;
import meeting.system.meeting.groups.dto.NewGroupProposal;
import meeting.system.meeting.groups.dto.SubmitProposalResult;

@AllArgsConstructor
class ProposalSubmitter {
    private static final int GROUPS_AND_WAITING_PROPOSALS_LIMIT_PER_USER = 3;
    private final ProposalRepository proposalRepository;
    private final GroupRepository groupRepository;

    SubmitProposalResult submitMeetingGroupProposal(UserId proposalAuthor, NewGroupProposal proposalDraft) {
        if (meetingGroupsPerUserLimitExceeded(proposalAuthor))
            return SubmitProposalResult.Failure.GROUP_LIMIT_PER_USER_EXCEEDED;
        if (groupWithNameAlreadyExists(proposalDraft.groupName()))
            return SubmitProposalResult.Failure.MEETING_GROUP_WITH_PROPOSED_NAME_ALREADY_EXISTS;
        if (proposalWithSameGroupNameIsAlreadySubmitted(proposalDraft.groupName()))
            return SubmitProposalResult.Failure.PROPOSAL_WITH_THE_SAME_GROUP_NAME_ALREADY_EXISTS;
        var proposalEntity = ProposalEntity.createFrom(proposalAuthor, proposalDraft);
        proposalRepository.save(proposalEntity);
        return new SubmitProposalResult.Success(new GroupProposalId(proposalEntity.getId()));
    }

    private boolean meetingGroupsPerUserLimitExceeded(UserId userId) {
        int meetingGroupsCount = countAllGroupsOfOrganizer(userId);
        long waitingProposalsCount = countAllWaitingProposalsOfOrganizer(userId);
        return meetingGroupsCount + waitingProposalsCount >= GROUPS_AND_WAITING_PROPOSALS_LIMIT_PER_USER;
    }

    private boolean groupWithNameAlreadyExists(String groupName) {
        return groupRepository.existsByGroupName(groupName);
    }

    private boolean proposalWithSameGroupNameIsAlreadySubmitted(String groupName) {
        return proposalRepository.existsByGroupName(groupName);
    }

    private int countAllGroupsOfOrganizer(UserId userId) {
        return groupRepository.findByGroupOrganizerId(userId.id()).size();
    }

    private long countAllWaitingProposalsOfOrganizer(UserId userId) {
        return proposalRepository
                .findByProposalAuthorId(userId.id())
                .stream()
                .filter(ProposalEntity::isWaitingForAdministratorDecision)
                .count();
    }
}