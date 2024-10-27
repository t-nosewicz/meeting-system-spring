package test.meeting.system.meetings.unit.core.signing.on;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SignOnForFreeMeetingHappyPath extends MeetingsCoreTestSetup {
    private final UserId groupMemberId = new UserId(1L);
    private final MeetingGroupId meetingGroupId = new MeetingGroupId(2L);
    private final UserId meetingOrganizerId = new UserId(3L);

    @Test
    public void signOnForFreeMeetingHappyPath() {
//        given
        var groupMeetingId = meetingWasScheduled(meetingOrganizerId, meetingGroupId);
//        and
        userIsGroupMember(groupMemberId, meetingGroupId);
//        when
        var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId);
//        then
        assertEquals(Option.none(), result);
//        and
        assertTrue(meetingsCoreFacade.userIsSignedOn(groupMemberId, groupMeetingId));
    }
}