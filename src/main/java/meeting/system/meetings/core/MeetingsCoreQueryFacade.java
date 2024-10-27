package meeting.system.meetings.core;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.meetings.core.dto.MeetingDetails;

public interface MeetingsCoreQueryFacade {
    Option<MeetingDetails> findMeetingDetails(GroupMeetingId groupMeetingId);

    Option<MeetingGroupId> getMeetingGroupId(GroupMeetingId groupMeetingId);

    boolean areAnyMeetingsScheduledForGroup(MeetingGroupId meetingGroupId);

    boolean hasFreeSpots(GroupMeetingId groupMeetingId);
}