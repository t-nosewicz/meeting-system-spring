package meeting.system.group.disbanding;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.DisbandGroupResult;

@Slf4j
@AllArgsConstructor
class GroupDisbandingLogs implements GroupDisbandingFacade {
    private final GroupDisbandingFacade meetingGroupsDisbandingFacade;

    @Override
    public DisbandGroupResult disbandGroup(UserId userId, MeetingGroupId meetingGroupId) {
        log.info("user {} is trying to disband group {}", userId.id(), meetingGroupId.id());
        var result = meetingGroupsDisbandingFacade.disbandGroup(userId, meetingGroupId);
        switch (result) {
            case DisbandGroupResult.Failure failure -> log.info("user {} failed to disband group {}, reason: {}", userId.id(), meetingGroupId.id(), failure);
            case DisbandGroupResult.Success success -> log.info("user {} disbanded group {}", userId.id(), meetingGroupId.id());
        }
        return result;
    }
}