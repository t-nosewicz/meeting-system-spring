package test.meeting.system.meetings.unit.core.signing.off;

import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import static io.vavr.control.Either.left;
import static meeting.system.meetings.core.dto.SignOffFromMeetingFailure.USER_WAS_NOT_SIGNED_ON;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignOffFromMeetingFailingPaths extends MeetingsCoreTestSetup {
    private final UserId meetingOrganizerId = new UserId(1L);
    private final MeetingGroupId groupId = new MeetingGroupId(2L);
    private final UserId groupMemberId = new UserId(3L);

    @Test
    public void userShouldFailToSignOffFromMeetingWithoutBeingSignedOn() {
//        given
        var groupMeetingId = meetingWasScheduled(meetingOrganizerId, groupId);
//        and
        userIsGroupMember(groupMemberId, groupId);
//        when
        var result = meetingsCoreFacade.signOffFromMeeting(groupMemberId, groupMeetingId);
//        then
        assertEquals(left(USER_WAS_NOT_SIGNED_ON), result);
    }

    @Test
    public void userShouldFailToSignOffTwice() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId);
//        and
        groupMemberSignedOnToMeeting(groupMemberId, meetingId);
//        and
        groupMemberSignedOffFromMeeting(groupMemberId, meetingId);
//        when
        var result = meetingsCoreFacade.signOffFromMeeting(groupMemberId, meetingId);
//        then
        assertEquals(left(USER_WAS_NOT_SIGNED_ON), result);
    }

    private void groupMemberSignedOnToMeeting(UserId groupMemberId, GroupMeetingId groupMeetingId) {
        userIsGroupMember(groupMemberId, groupId);
        assert meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId).isEmpty();
    }
}