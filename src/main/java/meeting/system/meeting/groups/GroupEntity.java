package meeting.system.meeting.groups;

import io.vavr.control.Option;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.commons.persistance.BaseEntity;
import meeting.system.meeting.groups.dto.JoinGroupResult;
import meeting.system.meeting.groups.dto.LeaveGroupResult;
import meeting.system.meeting.groups.dto.MeetingGroupDto;

import java.util.HashSet;
import java.util.Set;

import static io.vavr.control.Option.ofOptional;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
class GroupEntity extends BaseEntity {
    @Getter
    private String groupName;
    private Long groupOrganizerId;
    @OneToMany(fetch = EAGER, cascade = ALL, mappedBy = "groupEntity", orphanRemoval = true)
    private Set<GroupMemberEntity> groupMembers;

    JoinGroupResult join(UserId userId) {
        if (userIsGroupOrganizer(userId))
            return JoinGroupResult.Failure.GROUP_ORGANIZER_CANNOT_JOIN_THE_GROUP;
        if (containsGroupMemberWithId(userId))
            return JoinGroupResult.Failure.USER_ALREADY_JOINED_GROUP;
        groupMembers.add(new GroupMemberEntity(this, userId.id()));
        return new JoinGroupResult.Success();
    }

    private boolean containsGroupMemberWithId(UserId userId) {
        return findGroupMemberById(userId).isDefined();
    }

    MeetingGroupId getMeetingGroupId() {
        return new MeetingGroupId(getId());
    }

    boolean userIsGroupOrganizer(UserId userId) {
        return this.groupOrganizerId.equals(userId.id());
    }

    boolean userIsGroupMember(UserId userId) {
        return userIsGroupOrganizer(userId) || userIsRegularGroupMember(userId);
    }

    boolean userIsRegularGroupMember(UserId userId) {
        return groupMembers
                .stream()
                .anyMatch(groupMember -> groupMember.isGroupMember(userId));
    }

    LeaveGroupResult leave(UserId userId) {
        return findGroupMemberById(userId)
                .toEither(LeaveGroupResult.Failure.USER_IS_NOT_GROUP_MEMBER)
                .peek(groupMembers::remove)
                .map(e -> new LeaveGroupResult.Success())
                .fold(identity(), identity());
    }

    private Option<GroupMemberEntity> findGroupMemberById(UserId userId) {
        return ofOptional(groupMembers
                .stream()
                .filter(groupMemberEntity -> groupMemberEntity.isGroupMember(userId))
                .findAny());
    }

    MeetingGroupDto toDto() {
        return new MeetingGroupDto(new MeetingGroupId(getId()), groupName, new UserId(groupOrganizerId), groupMembersIds());
    }

    private Set<UserId> groupMembersIds() {
        return groupMembers.stream().map(GroupMemberEntity::groupMemberId).collect(toSet());
    }

    static GroupEntity create(ProposalAccepted proposalAccepted) {
        return new GroupEntity(proposalAccepted.getGroupName(), proposalAccepted.getGroupOrganizerId().id(), new HashSet<>());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    private static class GroupMemberEntity extends BaseEntity {
        @ManyToOne
        private GroupEntity groupEntity;
        private Long groupMemberId;

        private UserId groupMemberId() {
            return new UserId(groupMemberId);
        }

        boolean isGroupMember(UserId userId) {
            return groupMemberId.equals(userId.id());
        }
    }
}