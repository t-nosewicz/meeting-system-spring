package meeting.system.meeting.groups;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MeetingGroupsFacade extends MeetingGroupsRoles {

    @Transactional
    SubmitProposalResult submitNewGroupProposal(UserId userId, NewGroupProposal newGroupProposal);

    @Transactional
    RemoveProposalResult removeWaitingProposal(UserId userId, GroupProposalId proposalId);

    @Transactional
    AcceptProposalResult acceptProposal(UserId userId, GroupProposalId proposalId);

    @Transactional
    RejectProposalResult rejectProposal(UserId userId, GroupProposalId proposalId);

    @Transactional
    JoinGroupResult joinGroup(UserId userId, MeetingGroupId meetingGroupId);

    @Transactional
    LeaveGroupResult leaveGroup(UserId userId, MeetingGroupId meetingGroupId);

    @Transactional
    DisbandGroupResult disbandGroup(UserId userId, MeetingGroupId meetingGroupId);

    @Transactional(readOnly = true)
    Option<ProposalDto> findProposalById(GroupProposalId proposalId);

    @Transactional(readOnly = true)
    List<ProposalDto> findAllProposalsByAuthorId(UserId proposalAuthorId);

    @Transactional(readOnly = true)
    Option<MeetingGroupDto> findMeetingGroupDetails(MeetingGroupId meetingGroupId);
}