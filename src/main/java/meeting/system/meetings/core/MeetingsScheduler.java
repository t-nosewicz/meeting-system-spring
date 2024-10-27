package meeting.system.meetings.core;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.core.dto.GroupMeetingName;
import meeting.system.meetings.core.dto.MeetingDraft;
import meeting.system.meetings.core.dto.ScheduleMeetingFailure;
import meeting.system.meetings.core.ports.Calendar;

import java.time.LocalDate;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static java.math.BigDecimal.ONE;
import static meeting.system.meetings.core.dto.ScheduleMeetingFailure.*;

@AllArgsConstructor
class MeetingsScheduler {
    private final MeetingRepository meetingRepository;
    private final MeetingGroupsRoles meetingGroupsRoles;
    private final Calendar calendar;

    Either<ScheduleMeetingFailure, GroupMeetingId> scheduleNewMeeting(UserId userId, MeetingDraft meetingDraft) {
        var meetingGroupId = meetingDraft.meetingGroupId();
        if (!meetingGroupsRoles.groupExists(meetingGroupId))
            return left(MEETING_GROUP_DOES_NOT_EXIST);
        if (!isGroupMemberOrOrganizer(userId, meetingGroupId))
            return left(USER_IS_NEITHER_GROUP_ORGANIZER_OR_GROUP_MEMBER);
        if (!dateIsAtLeast3DaysInAdvance(meetingDraft.meetingDate()))
            return left(MEETING_DATE_IS_NOT_3_DAYS_IN_ADVANCE);
        if (meetingNameIsAlreadyUsed(meetingDraft.groupMeetingName()))
            return left(MEETING_NAME_IS_NOT_UNIQUE);
        if (meetingNameIsBlank(meetingDraft.groupMeetingName()))
            return left(MEETING_NAME_IS_BLANK);
        if (meetingDraft.attendeesLimit().limit() < 1)
            return left(ATTENDEES_LIMIT_CANNOT_BE_LESS_THAN_ONE);
        if (feeIsLessThanOne(meetingDraft))
            return left(FEE_CANNOT_BE_LOWER_THAN_ONE);
        var meeting = MeetingEntity.create(userId, meetingDraft);
        var groupMeetingId = meetingRepository.save(meeting).getId();
        return right(new GroupMeetingId(groupMeetingId));
    }

    private boolean feeIsLessThanOne(MeetingDraft meetingDraft) {
        return meetingDraft
                .fee()
                .map(fee -> fee.compareTo(ONE) < 0)
                .getOrElse(false);
    }

    private boolean isGroupMemberOrOrganizer(UserId userId, MeetingGroupId meetingGroupId) {
        return meetingGroupsRoles.isGroupOrganizer(userId, meetingGroupId) || meetingGroupsRoles.isGroupMember(userId, meetingGroupId);
    }

    private boolean meetingNameIsAlreadyUsed(GroupMeetingName groupMeetingName) {
        return meetingRepository.existsByMeetingName(groupMeetingName.name());
    }

    private boolean meetingNameIsBlank(GroupMeetingName groupMeetingName) {
        return groupMeetingName.name().isBlank();
    }

    private boolean dateIsAtLeast3DaysInAdvance(LocalDate meetingDate) {
        return calendar.getCurrentDate().plusDays(3).isBefore(meetingDate) ||
                calendar.getCurrentDate().plusDays(3).isEqual(meetingDate);
    }
}