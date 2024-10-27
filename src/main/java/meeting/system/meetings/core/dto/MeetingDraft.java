package meeting.system.meetings.core.dto;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MeetingDraft(
        MeetingGroupId meetingGroupId,
        LocalDate meetingDate,
        GroupMeetingName groupMeetingName,
        AttendeesLimit attendeesLimit,
        Option<BigDecimal> fee) {
}