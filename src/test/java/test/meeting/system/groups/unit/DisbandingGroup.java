package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.dto.DisbandGroupResult.Success;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.DisbandGroupResult.Failure.GROUP_DOESNT_EXIST;
import static meeting.system.meeting.groups.dto.DisbandGroupResult.Failure.USER_IS_NOT_GROUP_ORGANIZER;
import static org.junit.jupiter.api.Assertions.*;

public class DisbandingGroup extends TestSetup {
    private final UserId groupOrganizer = new UserId(1L);

    @Test
    public void shouldFailToDisbandSameGroupTwice() {
//        given
        var meetingGroupId = groupWasCreated(groupOrganizer);
//        and
        groupGotDisbanded(groupOrganizer, meetingGroupId);
//        when
        var result = meetingGroupsFacade.disbandGroup(groupOrganizer, meetingGroupId);
//        then
        assertEquals(GROUP_DOESNT_EXIST, result);
    }

    @Test
    public void shouldFailBecauseUserIsNotGroupOrganizer() {
//        given
        var meetingGroupId = groupWasCreated(groupOrganizer);
//        when
        var result = meetingGroupsFacade.disbandGroup(randomUserId(), meetingGroupId);
//        then
        assertEquals(USER_IS_NOT_GROUP_ORGANIZER, result);
    }

    @Test
    public void successfulGroupDisbandment() {
//        given
        var meetingGroupId = groupWasCreated(groupOrganizer);
//        when
        var result = meetingGroupsFacade.disbandGroup(groupOrganizer, meetingGroupId);
//        then
        assertEquals(new Success(), result);
    }
}