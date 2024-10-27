package test.meeting.system.groups.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;

import static meeting.system.meeting.groups.dto.JoinGroupResult.Failure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoiningGroup extends TestSetup {
    private final UserId user = new UserId(1L);

    @Test
    public void joiningGroupThatDoesNotExistShouldFail() {
//        when
        var result = meetingGroupsFacade.joinGroup(user, randomMeetingGroupId());
//        then
        assertEquals(MEETING_GROUP_DOES_NOT_EXIST, result);
    }

    @Test
    public void joiningSameGroupTwiceShouldFail() {
//        given
        var meetingGroupId = groupWasCreated();
//        and
        userJoinedGroup(user, meetingGroupId);
//        when
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then
        assertEquals(USER_ALREADY_JOINED_GROUP, result);
    }

    @Test
    public void happyPath() {
//        given
        var meetingGroupId = groupWasCreated();
//        when
        var result = meetingGroupsFacade.joinGroup(user, meetingGroupId);
//        then
        assertEquals(new Success(), result);
    }
}