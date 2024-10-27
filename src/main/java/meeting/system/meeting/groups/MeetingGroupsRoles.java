package meeting.system.meeting.groups;

import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;

public interface MeetingGroupsRoles {
    boolean isGroupMember(UserId userId, MeetingGroupId meetingGroupId);

    boolean isRegularGroupMember(UserId userId, MeetingGroupId meetingGroupId);

    boolean isGroupOrganizer(UserId userId, MeetingGroupId meetingGroupId);

    boolean groupExists(MeetingGroupId meetingGroupId);
}
