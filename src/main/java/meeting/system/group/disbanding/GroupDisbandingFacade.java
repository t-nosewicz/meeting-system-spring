package meeting.system.group.disbanding;

import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.DisbandGroupResult;

public interface GroupDisbandingFacade {

    DisbandGroupResult disbandGroup(UserId userId, MeetingGroupId meetingGroupId);
}
