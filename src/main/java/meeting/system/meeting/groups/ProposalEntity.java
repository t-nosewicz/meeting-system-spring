package meeting.system.meeting.groups;

import io.vavr.control.Either;
import io.vavr.control.Option;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.commons.persistance.BaseEntity;
import meeting.system.meeting.groups.dto.*;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
class ProposalEntity extends BaseEntity {
    private Long proposalAuthorId;
    @Getter
    private String groupName;
    private State state;
    private Long meetingGroupId;

    public boolean groupIdEqualsTo(Long meetingGroupId) {
        if (this.meetingGroupId == null)
            return false;
        return this.meetingGroupId.equals(meetingGroupId);
    }

    Either<AcceptProposalResult.Failure, ProposalAccepted> accept() {
        if (state == State.PROPOSAL_ACCEPTED)
            return left(AcceptProposalResult.Failure.PROPOSAL_WAS_ALREADY_ACCEPTED);
        if (state == State.PROPOSAL_REJECTED)
            return left(AcceptProposalResult.Failure.PROPOSAL_WAS_ALREADY_REJECTED);
        this.state = State.PROPOSAL_ACCEPTED;
        return right(new ProposalAccepted(new GroupProposalId(getId()), new UserId(proposalAuthorId), groupName));
    }

    void updateGroupId(MeetingGroupId meetingGroupId) {
        this.meetingGroupId = meetingGroupId.id();
    }

    RejectProposalResult reject() {
        if (state == State.PROPOSAL_ACCEPTED)
            return RejectProposalResult.Failure.PROPOSAL_IS_ALREADY_ACCEPTED;
        if (state == State.PROPOSAL_REJECTED)
            return RejectProposalResult.Failure.PROPOSAL_IS_ALREADY_REJECTED;
        this.state = State.PROPOSAL_REJECTED;
        return new RejectProposalResult.Success();
    }

    void groupGotDisbanded() {
        state = State.GROUP_DISBANDED;
    }

    ProposalDto toDto() {
        return new ProposalDto(new GroupProposalId(getId()), new ProposalAuthorId(proposalAuthorId), groupName, toDto(state), Option.of(meetingGroupId).map(MeetingGroupId::new));
    }

    private ProposalDto.State toDto(State state) {
        if (state == State.WAITING_FOR_ADMIN_DECISION)
            return ProposalDto.State.WAITING;
        if (state == State.PROPOSAL_ACCEPTED)
            return ProposalDto.State.ACCEPTED;
        else
            return ProposalDto.State.REJECTED;
    }

    boolean isWaitingForAdministratorDecision() {
        return state == State.WAITING_FOR_ADMIN_DECISION;
    }

    static ProposalEntity createFrom(UserId proposalAuthor, NewGroupProposal proposalDraft) {
        return new ProposalEntity(proposalAuthor.id(), proposalDraft.groupName(), State.WAITING_FOR_ADMIN_DECISION, null);
    }

    boolean isAuthor(Long proposalAuthorId) {
        return this.proposalAuthorId.equals(proposalAuthorId);
    }

    private enum State {
        WAITING_FOR_ADMIN_DECISION,
        PROPOSAL_ACCEPTED,
        PROPOSAL_REJECTED,
        GROUP_DISBANDED
    }

}