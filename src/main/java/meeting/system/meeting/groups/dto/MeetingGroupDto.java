package meeting.system.meeting.groups.dto;

import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;

import java.util.Set;

public record MeetingGroupDto(
        MeetingGroupId meetingGroupId,
        String groupName,
        UserId groupOrganizerId,
        Set<UserId> groupMemberIds) {
}