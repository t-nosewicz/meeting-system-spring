package meeting.system.meetings.core;

import io.vavr.control.Either;
import io.vavr.control.Option;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.commons.persistance.BaseEntity;
import meeting.system.meetings.core.dto.*;
import org.hibernate.annotations.OptimisticLock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static io.vavr.control.Option.of;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Getter
@Entity
class MeetingEntity extends BaseEntity {
    @Version
    private Long version;
    private Long meetingGroupId;
    private Long meetingOrganizerId;
    private LocalDate meetingDate;
    private String meetingName;
    private Integer attendeesLimit;
    @OneToMany(fetch = EAGER, cascade = ALL, mappedBy = "meetingEntity", orphanRemoval = true)
    @OptimisticLock(excluded = false)
    private Set<MeetingAttendeeEntity> signOnEntities;
    private BigDecimal fee;

    MeetingGroupId getMeetingGroupId() {
        return new MeetingGroupId(meetingGroupId);
    }

    Option<BigDecimal> getFee() {
        return Option.of(fee);
    }

    boolean userIsMeetingOrganizer(UserId userId) {
        return this.meetingOrganizerId.equals(userId.id());
    }

    Either<SignOffFromMeetingFailure, UserSignedOffFromMeeting> signOff(UserId userId) {
        if (!this.contains(userId))
            return Either.left(SignOffFromMeetingFailure.USER_WAS_NOT_SIGNED_ON);
        signOnEntities.remove(new MeetingAttendeeEntity(this, userId.id()));
        return Either.right(new UserSignedOffFromMeeting(userId, new GroupMeetingId(getId()), meetingName, meetingDate));
    }

    Option<SignOnForMeetingFailure> signOn(UserId userId) {
        if (this.contains(userId))
            return Option.of(SignOnForMeetingFailure.USER_ALREADY_SIGNED_ON);
        if (!hasFreeSpots())
            return of(SignOnForMeetingFailure.NO_FREE_ATTENDEE_SLOTS);
        signOnEntities.add(new MeetingAttendeeEntity(this, userId.id()));
        return Option.none();
    }

    public boolean isSignedOn(UserId userId) {
        return contains(userId);
    }

    private boolean contains(UserId userId) {
        return signOnEntities
                .stream()
                .anyMatch(signOnEntity -> signOnEntity.isUserSignedOn(userId));
    }

    boolean hasFreeSpots() {
        return signOnEntities.size() < attendeesLimit;
    }

    Set<UserId> getAttendees() {
        return signOnEntities.stream().map(MeetingAttendeeEntity::getAttendeeId).map(UserId::new).collect(toSet());
    }

    MeetingDetails toDto() {
        return new MeetingDetails(
                new GroupMeetingId(getId()),
                new MeetingGroupId(meetingGroupId),
                new GroupMeetingName(meetingName),
                new AttendeesLimit(attendeesLimit),
                new HashSet<>(getAttendees()),
                Option.of(fee));
    }

    static MeetingEntity create(UserId meetingOrganizerId, MeetingDraft meetingDraft) {
        return new MeetingEntity(
                null,
                meetingDraft.meetingGroupId().id(),
                meetingOrganizerId.id(),
                meetingDraft.meetingDate(),
                meetingDraft.groupMeetingName().name(),
                meetingDraft.attendeesLimit().limit(),
                new HashSet<>(),
                meetingDraft.fee().getOrNull());
    }

    @Value
    static class AttendeeSignedUpFromWaitList {
        UserId attendeeId;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Entity
    private static class MeetingAttendeeEntity extends BaseEntity {
        @ManyToOne
        private MeetingEntity meetingEntity;
        private Long attendeeId;

        boolean isUserSignedOn(UserId userId) {
            return attendeeId.equals(userId.id());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MeetingAttendeeEntity that = (MeetingAttendeeEntity) o;
            return Objects.equals(meetingEntity.getId(), that.meetingEntity.getId()) && Objects.equals(attendeeId, that.attendeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(meetingEntity.getId(), attendeeId);
        }
    }
}