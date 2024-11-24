package test.meeting.system.meetings.unit.core.signing.on;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.core.dto.AttendeesLimit;
import meeting.system.meetings.core.dto.GroupMeetingName;
import meeting.system.meetings.core.dto.MeetingDraft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import static meeting.system.meetings.core.dto.SignOnForMeetingFailure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignOnForFreeMeetingFailurePaths extends MeetingsCoreTestSetup {
    private final UserId groupMemberId = new UserId(1L);
    private final UserId meetingOrganizerId = new UserId(2L);
    private final MeetingGroupId meetingGroupId = new MeetingGroupId(3L);
    private final int attendeesLimit = 5;
    private GroupMeetingId groupMeetingId;

    @BeforeEach
    public void init() {
        userIsGroupMember(meetingOrganizerId, meetingGroupId);
        userIsGroupMember(groupMemberId, meetingGroupId);
        groupMeetingId = meetingWasScheduled(meetingOrganizerId, meetingDraft());
    }

    @Test
    public void groupMemberShouldFailToSignOnToNotExistingMeeting() {
//        when
        var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, randomMeetingId());
//        then
        assertEquals(Option.of(MEETING_DOES_NOT_EXIST), result);
    }

    @Test
    public void userThatIsNotGroupMemberShouldFailToSignUpForMeeting() {
//        given
        userIsNotGroupMember(groupMemberId, meetingGroupId);
//        when
        var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId);
//        then
        assertEquals(Option.of(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void groupMemberShouldFailToSignUpForMeetingThatHasNoFreeSlots() {
//        given
        scheduledMeetingHasNoFreeSlots();
//        when
        var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId);
//        then
        assertEquals(Option.of(NO_FREE_ATTENDEE_SLOTS), result);
    }

    @Test
    public void groupMemberShouldFailToSignOnToTheSameMeetingTwice() {
//        given
        groupMemberSignedOnToMeeting(groupMemberId, groupMeetingId, meetingGroupId);
//        when
        var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId);
//        then
        assertEquals(Option.of(USER_ALREADY_SIGNED_ON), result);
    }

    @Test
    public void groupOrganizerShouldFailToSignOnForTheMeeting() {
//        given
        groupMemberSignedOnToMeeting(groupMemberId, groupMeetingId, meetingGroupId);
//        when
        var result = meetingsCoreFacade.signOnForMeeting(meetingOrganizerId, groupMeetingId);
//        then
        assertEquals(Option.of(MEETING_ORGANIZER_CANNOT_SIGN_ON), result);
    }

    private void scheduledMeetingHasNoFreeSlots() {
        for (int i = 0; i < attendeesLimit; i++) {
            var groupMemberId= randomUserId();
            userIsGroupMember(groupMemberId, meetingGroupId);
            assert meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId).isEmpty();
        }
    }

    private MeetingDraft meetingDraft() {
        return new MeetingDraft(
                meetingGroupId,
                calendar.getCurrentDate().plusDays(3),
                new GroupMeetingName("some-meetingName"),
                new AttendeesLimit(attendeesLimit),
                Option.none());
    }
}