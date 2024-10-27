package test.meeting.system.meetings.unit.core.cancelling;

import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import java.time.LocalDate;

import static io.vavr.control.Either.left;
import static meeting.system.meetings.core.dto.CancelMeetingFailure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class CancelMeetingFailingPaths extends MeetingsCoreTestSetup {
    private final UserId meetingOrganizerId = new UserId(1L);
    private final MeetingGroupId groupId = new MeetingGroupId(2L);

    @Test
    public void meetingOrganizerShouldFailToCancelTheSameMeetingTwice() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId);
//        and
        meetingWasCancelled(meetingOrganizerId, meetingId);
//        when
        var result = meetingsCoreFacade.cancelMeeting(meetingOrganizerId, meetingId);
//        then
        assertEquals(left(MEETING_DOESNT_EXIST), result);
    }

    @Test
    public void userThatIsNotMeetingOrganizerShouldFailToCancelMeeting() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId);
//        when
        var result = meetingsCoreFacade.cancelMeeting(randomUserId(), meetingId);
//        then
        assertEquals(left(USER_IS_NOT_MEETING_ORGANIZER), result);
    }

    @Test
    public void userShouldFailToCancelMeetingAfterMeetingDate() {
//        given
        var meetingDate = LocalDate.now().plusDays(4);
        var meetingId = meetingWasScheduled(meetingOrganizerId, groupId, meetingDate);
//        and
        meetingCancellationHappensOneDayAfterMeetingDate(meetingDate);
//        when
        var result = meetingsCoreFacade.cancelMeeting(meetingOrganizerId, meetingId);
//        then
        assertEquals(left(CANNOT_CANCEL_MEETING_AFTER_ITS_DATE), result);
    }

    private void meetingWasCancelled(UserId meetingOrganizerId, GroupMeetingId meetingId) {
        assertTrue(meetingsCoreFacade.cancelMeeting(meetingOrganizerId, meetingId).isRight());
    }

    private void meetingCancellationHappensOneDayAfterMeetingDate(LocalDate meetingDate) {
        when(calendar.getCurrentDate()).thenReturn(meetingDate.plusDays(1));
    }
}