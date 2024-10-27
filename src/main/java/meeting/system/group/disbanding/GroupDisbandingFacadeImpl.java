package meeting.system.group.disbanding;

import lombok.AllArgsConstructor;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsFacade;
import meeting.system.meeting.groups.dto.DisbandGroupResult;
import meeting.system.meetings.MeetingsFacade;

import static meeting.system.meeting.groups.dto.DisbandGroupResult.Failure.GROUP_HAS_SCHEDULED_MEETINGS;

@AllArgsConstructor
class GroupDisbandingFacadeImpl implements GroupDisbandingFacade {
    private final MeetingGroupsFacade meetingGroupsFacade;
    private final MeetingsFacade meetingsFacade;

    @Override
    public DisbandGroupResult disbandGroup(UserId userId, MeetingGroupId meetingGroupId) {
        if (meetingsFacade.areAnyMeetingsScheduledForGroup(meetingGroupId))
            return GROUP_HAS_SCHEDULED_MEETINGS;
        else {
            return meetingGroupsFacade.disbandGroup(userId, meetingGroupId);
        }
    }
}