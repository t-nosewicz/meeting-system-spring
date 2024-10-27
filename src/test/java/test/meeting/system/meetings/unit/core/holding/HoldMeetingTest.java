package test.meeting.system.meetings.unit.core.holding;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import java.time.LocalDate;

import static meeting.system.meetings.core.dto.HoldMeetingFailure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class HoldMeetingTest extends MeetingsCoreTestSetup {
    private final UserId meetingOrganizerId = new UserId(1L);
    private final MeetingGroupId groupId = new MeetingGroupId(2L);
    private final LocalDate meetingDate = LocalDate.now().plusDays(4);

    @Test
    public void userThatIsNotMeetingOrganizerShouldFailToHoldMeeting() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId, meetingDate);
//        when
        var result = meetingsCoreFacade.holdMeeting(randomUserId(), meetingId);
//        then
        assertEquals(Option.of(USER_IS_NOT_MEETING_ORGANIZER), result);
    }

    @Test
    public void meetingOrganizerShouldFailToHoldMeetingBeforeMeetingDate() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId, meetingDate);
//        and
        userTriesToHoldMeetingBeforeMeetingDate(meetingDate);
//        when
        var result = meetingsCoreFacade.holdMeeting(meetingOrganizerId, meetingId);
//        then
        assertEquals(Option.of(CANNOT_HOLD_MEETING_BEFORE_MEETING_DATE), result);
    }

    @Test
    public void meetingOrganizerShouldFailToHoldTheSameMeetingTwice() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId, meetingDate);
//        and
        userTriesToHoldMeetingDayAfter(meetingDate);
//        and
        meetingWasHeld(meetingOrganizerId, meetingId);
//        when
        var result = meetingsCoreFacade.holdMeeting(meetingOrganizerId, meetingId);
//        then
        assertEquals(Option.of(MEETING_DOES_NOT_EXIST), result);
    }

    @Test
    public void success() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId, meetingDate);
//        and
        userTriesToHoldMeetingDayAfter(meetingDate);
//        when
        var result = meetingsCoreFacade.holdMeeting(meetingOrganizerId, meetingId);
//        then
        assertEquals(Option.none(), result);
    }

    private void meetingWasHeld(UserId meetingOrganizerId, GroupMeetingId meetingId) {
        assertEquals(Option.none(), meetingsCoreFacade.holdMeeting(meetingOrganizerId, meetingId));
    }

    private void userTriesToHoldMeetingBeforeMeetingDate(LocalDate meetingDate) {
        when(calendar.getCurrentDate()).thenReturn(meetingDate.minusDays(1));
    }

    private void userTriesToHoldMeetingDayAfter(LocalDate meetingDate) {
        when(calendar.getCurrentDate()).thenReturn(meetingDate.plusDays(1));
    }
}