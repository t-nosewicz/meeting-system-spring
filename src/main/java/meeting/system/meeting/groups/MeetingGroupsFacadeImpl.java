package meeting.system.meeting.groups;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.*;

import java.util.List;

import static java.util.function.Function.identity;
import static meeting.system.meeting.groups.dto.AcceptProposalResult.Failure.PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST;
import static meeting.system.meeting.groups.dto.DisbandGroupResult.Failure.GROUP_DOESNT_EXIST;
import static meeting.system.meeting.groups.dto.DisbandGroupResult.Failure.USER_IS_NOT_GROUP_ORGANIZER;
import static meeting.system.meeting.groups.dto.JoinGroupResult.Failure.MEETING_GROUP_DOES_NOT_EXIST;
import static meeting.system.meeting.groups.dto.LeaveGroupResult.Failure.GROUP_DOES_NOT_EXIST;
import static meeting.system.meeting.groups.dto.RejectProposalResult.Failure.PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST;
import static meeting.system.meeting.groups.dto.RemoveProposalResult.Failure.PROPOSAL_ALREADY_PROCESSED;
import static meeting.system.meeting.groups.dto.RemoveProposalResult.Failure.USER_IS_NOT_PROPOSAL_AUTHOR;

@AllArgsConstructor
@Slf4j
class MeetingGroupsFacadeImpl implements MeetingGroupsFacade {
    private final ProposalRepository proposalRepository;
    private final GroupRepository groupRepository;
    private final ProposalSubmitter proposalSubmitter;

    @Override
    public SubmitProposalResult submitNewGroupProposal(UserId userId, NewGroupProposal newGroupProposal) {
        return proposalSubmitter.submitMeetingGroupProposal(userId, newGroupProposal);
    }

    @Override
    public RemoveProposalResult removeWaitingProposal(UserId userId, GroupProposalId proposalId) {
        return proposalRepository
                .findById(proposalId.id())
                .map(proposal -> removeProposal(userId, proposal))
                .orElse(RemoveProposalResult.Failure.PROPOSAL_DOES_NOT_EXIST);
    }

    private RemoveProposalResult removeProposal(UserId userId, ProposalEntity proposal) {
        if (!proposal.isAuthor(userId.id()))
            return USER_IS_NOT_PROPOSAL_AUTHOR;
        if (!proposal.isWaitingForAdministratorDecision())
            return PROPOSAL_ALREADY_PROCESSED;
        proposalRepository.deleteById(proposal.getId());
        return new RemoveProposalResult.Success();
    }

    @Override
    public AcceptProposalResult acceptProposal(UserId userId, GroupProposalId proposalId) {
        return proposalRepository
                .findById(proposalId.id())
                .map(this::acceptProposal)
                .orElse(PROPOSAL_WITH_GIVEN_ID_DOES_NOT_EXIST);
    }

    private AcceptProposalResult acceptProposal(ProposalEntity proposal) {
        return proposal
                .accept()
                .map(GroupEntity::create)
                .peek(groupRepository::save)
                .map(GroupEntity::getMeetingGroupId)
                .peek(groupId -> log.info("setting group id to {} in proposal {}", groupId.id(), proposal.getId()))
                .peek(proposal::updateGroupId)
                .map(AcceptProposalResult.Success::new)
                .fold(identity(), identity());
    }

    @Override
    public RejectProposalResult rejectProposal(UserId userId, GroupProposalId proposalId) {
        return proposalRepository
                .findById(proposalId.id())
                .map(ProposalEntity::reject)
                .orElse(PROPOSAL_WITH_GIVEN_ID_DOESNT_EXIST);
    }

    @Override
    public JoinGroupResult joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        return groupRepository
                .findById(meetingGroupId.id())
                .map(meetingGroup -> meetingGroup.join(userId))
                .orElse(MEETING_GROUP_DOES_NOT_EXIST);
    }

    @Override
    public LeaveGroupResult leaveGroup(UserId userId, MeetingGroupId meetingGroupId) {
        return groupRepository
                .findById(meetingGroupId.id())
                .map(meetingGroup -> meetingGroup.leave(userId))
                .orElse(GROUP_DOES_NOT_EXIST);
    }

    @Override
    public DisbandGroupResult disbandGroup(UserId userId, MeetingGroupId meetingGroupId) {
        return groupRepository
                .findById(meetingGroupId.id())
                .map(group -> disbandGroup(userId, group))
                .orElse(GROUP_DOESNT_EXIST);
    }

    private DisbandGroupResult disbandGroup(UserId userId, GroupEntity group) {
        if (!group.userIsGroupOrganizer(userId))
            return USER_IS_NOT_GROUP_ORGANIZER;
        groupRepository.delete(group);
        proposalRepository
                .findByMeetingGroupId(group.getMeetingGroupId().id())
                .peek(ProposalEntity::groupGotDisbanded);
        return new DisbandGroupResult.Success();
    }

    @Override
    public Option<ProposalDto> findProposalById(GroupProposalId proposalId) {
        return proposalRepository
                .findById(proposalId.id())
                .map(ProposalEntity::toDto)
                .map(Option::of)
                .orElse(Option.none());
    }

    @Override
    public List<ProposalDto> findAllProposalsByAuthorId(UserId proposalAuthorId) {
        return proposalRepository
                .findByProposalAuthorId(proposalAuthorId.id())
                .stream()
                .map(ProposalEntity::toDto)
                .toList();
    }

    @Override
    public Option<MeetingGroupDto> findMeetingGroupDetails(MeetingGroupId meetingGroupId) {
        return groupRepository
                .findById(meetingGroupId.id())
                .map(GroupEntity::toDto)
                .map(Option::of)
                .orElse(Option.none());
    }

    @Override
    public boolean groupExists(MeetingGroupId meetingGroupId) {
        return groupRepository.existsById(meetingGroupId.id());
    }

    @Override
    public boolean isGroupMember(UserId userId, MeetingGroupId meetingGroupId) {
        return findGroupById(meetingGroupId)
                .map(group -> group.userIsGroupMember(userId))
                .getOrElse(false);
    }

    @Override
    public boolean isRegularGroupMember(UserId userId, MeetingGroupId meetingGroupId) {
        return findGroupById(meetingGroupId)
                .map(group -> group.userIsRegularGroupMember(userId))
                .getOrElse(false);
    }

    @Override
    public boolean isGroupOrganizer(UserId userId, MeetingGroupId meetingGroupId) {
        return findGroupById(meetingGroupId)
                .map(group -> group.userIsGroupOrganizer(userId))
                .getOrElse(false);
    }

    private Option<GroupEntity> findGroupById(MeetingGroupId meetingGroupId) {
        return Option.ofOptional(groupRepository.findById(meetingGroupId.id()));
    }
}