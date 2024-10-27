package meeting.system.meetings.core.dto;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;

import java.math.BigDecimal;
import java.util.Set;

public record MeetingDetails(
        GroupMeetingId groupMeetingId,
        MeetingGroupId meetingGroupId,
        GroupMeetingName groupMeetingName,
        AttendeesLimit attendeesLimit,
        Set<UserId> attendees,
        Option<BigDecimal> fee) {
}