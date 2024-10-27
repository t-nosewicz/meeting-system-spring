package test.meeting.system.meetings.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

public class MeetingScheduling extends MeetingsTestSetup {

    @Test
    @DisplayName("if meeting scheduling succeeds, then waiting list should be created for this meeting")
    public void test1() {
//        given
        var meetingId = meetingSchedulingSucceeds(groupId);
//        when
        var result = meetingsFacade.scheduleNewMeeting(meetingOrganizerId, anyMeetingDraft(groupId));
//        then
        assertEquals(right(meetingId), result);
//        and
        waitingListWasCreated(meetingId, groupId);
    }

    @Test
    @DisplayName("if meeting scheduling fails, then waiting list should NOT be created")
    public void test2() {
//        given
        meetingSchedulingFails();
//        when
        var result = meetingsFacade.scheduleNewMeeting(meetingOrganizerId, anyMeetingDraft(groupId));
//        then
        assertTrue(result.isLeft());
//        and
        verifyNoInteractions(waitingList);
    }
}