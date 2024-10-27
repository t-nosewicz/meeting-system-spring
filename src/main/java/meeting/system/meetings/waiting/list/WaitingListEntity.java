package meeting.system.meetings.waiting.list;

import io.vavr.control.Option;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.commons.persistance.BaseEntity;
import meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure;
import meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
class WaitingListEntity extends BaseEntity {
    private Long meetingId;
    private Long groupId;
    @OneToMany(fetch = EAGER, cascade = ALL, mappedBy = "waitingList", orphanRemoval = true)
    private Set<WaitingListMember> waitingListMembers;

    GroupMeetingId getMeetingId() {
        return new GroupMeetingId(meetingId);
    }

    MeetingGroupId getGroupId() {
        return new MeetingGroupId(groupId);
    }

    List<UserId> getWaitingListMembers() {
        return this.waitingListMembers.stream().map(WaitingListMember::getMemberId).toList();
    }

    boolean meetingIdEquals(Long meetingId) {
        return this.meetingId.equals(meetingId);
    }

    Option<SignOnWaitListFailure> signOn(UserId userId) {
        var waitingListMember = new WaitingListMember(this, userId.id());
        if (waitingListMembers.contains(waitingListMember))
            return Option.of(SignOnWaitListFailure.USER_ALREADY_IS_ON_WAITING_LIST);
        waitingListMembers.add(waitingListMember);
        return Option.none();
    }

    Option<SignOffFromWaitListFailure> signOff(UserId userId) {
        if (waitingListMembers.remove(new WaitingListMember(this, userId.id())))
            return Option.none();
        return Option.of(SignOffFromWaitListFailure.USER_WAS_NOT_ON_WAITING_LIST);
    }

    boolean isEmpty() {
        return waitingListMembers.isEmpty();
    }

    static WaitingListEntity create(GroupMeetingId meetingId, MeetingGroupId meetingGroupId) {
        return new WaitingListEntity(meetingId.id(), meetingGroupId.id(), new HashSet<>());
    }

    @Entity
    @AllArgsConstructor
    @NoArgsConstructor(access = PROTECTED)
    private static class WaitingListMember extends BaseEntity {
        @ManyToOne
        private WaitingListEntity waitingList;
        private Long waitingListMemberId;

        UserId getMemberId() {
            return new UserId(waitingListMemberId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WaitingListMember that = (WaitingListMember) o;
            return Objects.equals(waitingList.getId(), that.waitingList.getId()) && Objects.equals(waitingListMemberId, that.waitingListMemberId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(waitingList.getId(), waitingListMemberId);
        }
    }
}