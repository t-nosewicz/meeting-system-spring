package meeting.system.meeting.groups;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.*;

import java.util.List;

@AllArgsConstructor
@Slf4j
class MeetingGroupsLogs implements MeetingGroupsFacade {
    private final MeetingGroupsFacade meetingGroupsFacade;

    @Override
    public SubmitProposalResult submitNewGroupProposal(UserId userId, NewGroupProposal newGroupProposal) {
        log.info("user {} is trying to submit group proposal: {}", userId.id(), newGroupProposal);
        var result = meetingGroupsFacade.submitNewGroupProposal(userId, newGroupProposal);
        switch (result) {
            case SubmitProposalResult.Failure failure ->
                    log.info("user {} failed to submit proposal, reason {}", userId.id(), failure);
            case SubmitProposalResult.Success success ->
                    log.info("user {} submitted proposal with id {}", userId.id(), success.groupProposalId().id());
        }
        return result;
    }

    @Override
    public RemoveProposalResult removeWaitingProposal(UserId userId, GroupProposalId proposalId) {
        log.info("user {} is trying to remove group proposal {}", userId.id(), proposalId.id());
        var result = meetingGroupsFacade.removeWaitingProposal(userId, proposalId);
        switch (result) {
            case RemoveProposalResult.Failure failure ->
                    log.info("user {} failed to remove waiting proposal {}, reason: {}", userId.id(), proposalId.id(), failure);
            case RemoveProposalResult.Success success ->
                    log.info("user {} removed waiting proposal {}", userId.id(), proposalId.id());
        }
        return result;
    }

    @Override
    public AcceptProposalResult acceptProposal(UserId userId, GroupProposalId proposalId) {
        log.info("user {} is trying to accept group proposal {}", userId.id(), proposalId.id());
        var result = meetingGroupsFacade.acceptProposal(userId, proposalId);
        switch (result) {
            case AcceptProposalResult.Failure failure ->
                    log.info("user {} failed to accept proposal, reason {}", userId.id(), failure);
            case AcceptProposalResult.Success success ->
                    log.info("user {} accepted proposal {}, meeting group {} got created", userId.id(), proposalId.id(), success.meetingGroupId().id());
        }
        return result;
    }

    @Override
    public RejectProposalResult rejectProposal(UserId userId, GroupProposalId proposalId) {
        log.info("user {} is trying to reject group proposal {}", userId.id(), proposalId.id());
        var result = meetingGroupsFacade.rejectProposal(userId, proposalId);
        switch (result) {
            case RejectProposalResult.Failure failure ->
                    log.info("user {} failed to reject proposal {}, reason: {}", userId.id(), proposalId.id(), failure);
            case RejectProposalResult.Success success ->
                    log.info("user {} rejected proposal {}", userId.id(), proposalId.id());
        }
        return result;
    }

    @Override
    public JoinGroupResult joinGroup(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("user {} is trying to join group {}", userId.id(), meetingGroupId.id());
        var result = meetingGroupsFacade.joinGroup(userId, meetingGroupId);
        switch (result) {
            case JoinGroupResult.Failure failure ->
                    log.info("user {} failed to join meeting group, reason: {}", meetingGroupId.id(), failure);
            case JoinGroupResult.Success success ->
                    log.info("user {} joined group {}", userId.id(), meetingGroupId.id());
        }
        return result;
    }

    @Override
    public LeaveGroupResult leaveGroup(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("user {} is trying to leave group {}", userId.id(), meetingGroupId.id());
        var result = meetingGroupsFacade.leaveGroup(userId, meetingGroupId);
        switch (result) {
            case LeaveGroupResult.Failure failure ->
                    log.info("user {} failed to leave the group, reason: {}", userId.id(), failure);
            case LeaveGroupResult.Success success ->
                    log.info("user {} left the group {}", userId.id(), meetingGroupId.id());
        }
        return result;
    }

    @Override
    public DisbandGroupResult disbandGroup(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("user {} is trying to disband group {}", userId.id(), meetingGroupId.id());
        var result = meetingGroupsFacade.disbandGroup(userId, meetingGroupId);
        switch (result) {
            case DisbandGroupResult.Failure failure ->
                    log.info("user {} failed to disband group {}, reason: {}", userId.id(), meetingGroupId.id(), failure);
            case DisbandGroupResult.Success success ->
                    log.info("user {} disbanded group {}", userId.id(), meetingGroupId.id());
        }
        return result;
    }

    @Override
    public Option<ProposalDto> findProposalById(GroupProposalId proposalId) {
        log.info("trying to find proposal by id: {}", proposalId.id());
        return meetingGroupsFacade
                .findProposalById(proposalId)
                .peek(proposalDto -> log.info("proposal with id {} found", proposalId.id()))
                .onEmpty(() -> log.info("proposal with id {} not found", proposalId.id()));
    }

    @Override
    public List<ProposalDto> findAllProposalsByAuthorId(UserId proposalAuthorId) {
        log.info("trying to find proposal by author id: {}", proposalAuthorId);
        var proposals = meetingGroupsFacade.findAllProposalsByAuthorId(proposalAuthorId);
        log.info("number of found proposals: {}", proposals.size());
        return proposals;
    }

    @Override
    public Option<MeetingGroupDto> findMeetingGroupDetails(MeetingGroupId meetingGroupId) {
        log.info("trying to find meeting group details by id {}", meetingGroupId.id());
        return meetingGroupsFacade
                .findMeetingGroupDetails(meetingGroupId)
                .peek(proposalDto -> log.info("group details with id {} found", meetingGroupId.id()))
                .onEmpty(() -> log.info("group details with id {} not found", meetingGroupId.id()));
    }

    @Override
    public boolean groupExists(MeetingGroupId meetingGroupId) {
        log.info("checking if group {} exists", meetingGroupId.id());
        var groupExists = meetingGroupsFacade.groupExists(meetingGroupId);
        if (groupExists)
            log.info("group {} exists", meetingGroupId.id());
        else
            log.info("group {} does not exist", meetingGroupId.id());
        return groupExists;
    }

    @Override
    public boolean isGroupMember(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("checking if user {} is a member of group {}", userId.id(), meetingGroupId.id());
        var isGroupMember = meetingGroupsFacade.isGroupMember(userId, meetingGroupId);
        if (isGroupMember)
            log.info("user {} is a member of group {}", userId.id(), meetingGroupId.id());
        else
            log.info("user {} is not a member of group {}", userId.id(), meetingGroupId.id());
        return isGroupMember;
    }

    @Override
    public boolean isRegularGroupMember(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("checking if user {} is a regular member of the group {}", userId.id(), meetingGroupId.id());
        var isGroupMember = meetingGroupsFacade.isRegularGroupMember(userId, meetingGroupId);
        if (isGroupMember)
            log.info("user {} is a regular member of group {}", userId.id(), meetingGroupId.id());
        else
            log.info("user {} is not a regular member of group {}", userId.id(), meetingGroupId.id());
        return isGroupMember;
    }

    @Override
    public boolean isGroupOrganizer(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("checking if user {} is an organizer of group {}", userId.id(), meetingGroupId.id());
        var isGroupOrganizer = meetingGroupsFacade.isGroupOrganizer(userId, meetingGroupId);
        if (isGroupOrganizer)
            log.info("user {} is an organizer of group {}", userId.id(), meetingGroupId.id());
        else
            log.info("user {} is not an organizer of group {}", userId.id(), meetingGroupId.id());
        return isGroupOrganizer;
    }
}