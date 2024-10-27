package test.meeting.system.meetings.unit;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

public class MeetingHolding extends MeetingsTestSetup {
    private final GroupMeetingId meetingId = new GroupMeetingId(6L);

    @Test
    @DisplayName("if meeting holding succeeds, then its waiting list should be removed")
    public void test1() {
//        given
        meetingWasHeld(meetingId);
//        when
        var result = meetingsFacade.holdMeeting(meetingOrganizerId, meetingId);
//        then
        assertEquals(Option.none(), result);
//        and
        waitingListGotRemoved(meetingId);

    }

    @Test
    @DisplayName("if meeting holding fails, then its waiting list should not be removed")
    public void test2() {
//        given
        meetingHoldingFails(meetingOrganizerId, meetingId);
//        when
        var result = meetingsFacade.holdMeeting(meetingOrganizerId, meetingId);
//        then
        assertTrue(result.isDefined());
//        and
        verifyNoInteractions(waitingList);

    }
}