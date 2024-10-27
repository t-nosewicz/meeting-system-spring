package test.meeting.system.meetings.unit;

import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

public class MeetingCancelling extends MeetingsTestSetup {

    @Test
    @DisplayName("if meeting gets cancelled, then waiting list is removed, meeting attendees and waiting list members are notified about it")
    public void test1() {
//        given
        var meetingGotCancelled = meetingGetsCancelled(meetingOrganizerId, meetingId);
//        and
        var waitingListRemoved = waitingListGetsRemoved(meetingId);
//        when
        var result = meetingsFacade.cancelMeeting(meetingOrganizerId, meetingId);
//        then
        assertEquals(Option.none(), result);
//        and
        meetingAttendeesGotNotified(meetingGotCancelled);
//        and
        waitingListMembersGotNotified(meetingGotCancelled, waitingListRemoved);
    }

    @Test
    @DisplayName("if user fails to cancel the meeting, then no interactions with waiting list nor notifications occur")
    public void test2() {
//        given
        meetingCancellingFails(meetingOrganizerId, meetingId);
//        when
        var result = meetingsFacade.cancelMeeting(meetingOrganizerId, meetingId);
//        then
        assertTrue(result.isDefined());
//        and
        verifyNoInteractions(waitingList);
//        and
        verifyNoInteractions(meetingsNotifications);
    }
}