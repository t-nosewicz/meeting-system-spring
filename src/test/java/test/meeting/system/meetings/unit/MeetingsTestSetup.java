package test.meeting.system.meetings.unit;

import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.MeetingsConfiguration;
import meeting.system.meetings.MeetingsFacade;
import meeting.system.meetings.core.MeetingsCoreFacade;
import meeting.system.meetings.core.dto.*;
import meeting.system.meetings.waiting.list.WaitingListFacade;
import meeting.system.meetings.waiting.list.dto.WaitingListRemoved;
import meeting.system.notifications.NotificationsFacade;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static meeting.system.meetings.core.dto.CancelMeetingFailure.USER_IS_NOT_MEETING_ORGANIZER;
import static meeting.system.meetings.core.dto.ScheduleMeetingFailure.MEETING_NAME_IS_BLANK;
import static meeting.system.meetings.waiting.list.dto.MeetingSignOnFromWaitingListFailure.MEETING_HAS_NO_FREE_SLOTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MeetingsTestSetup {
    protected MeetingsFacade meetingsFacade;
    protected MeetingsCoreFacade meetingsCore;
    protected WaitingListFacade waitingList;
    protected NotificationsFacade meetingsNotifications;
    protected UserId meetingOrganizerId;
    protected GroupMeetingId meetingId;
    protected MeetingGroupId groupId;

    @BeforeEach
    public void init() {
        meetingsCore = mock(MeetingsCoreFacade.class);
        waitingList = mock(WaitingListFacade.class);
        meetingsNotifications = mock(NotificationsFacade.class);
        meetingsFacade = new MeetingsConfiguration().meetingsFacade(meetingsCore, waitingList, meetingsNotifications);
        meetingOrganizerId = new UserId(1L);
        meetingId = new GroupMeetingId(2L);
        groupId = new MeetingGroupId(3L);
    }

    protected GroupMeetingId meetingSchedulingSucceeds(MeetingGroupId groupId) {
        var meetingId = new GroupMeetingId(5L);
        when(meetingsCore.scheduleNewMeeting(meetingOrganizerId, anyMeetingDraft(groupId))).thenReturn(Either.right(meetingId));
        return meetingId;
    }

    protected MeetingDraft anyMeetingDraft(MeetingGroupId groupId) {
        return new MeetingDraft(groupId, LocalDate.now(), new GroupMeetingName("name"), new AttendeesLimit(3), Option.none());
    }

    protected void meetingSchedulingFails() {
        when(meetingsCore.scheduleNewMeeting(any(UserId.class), any(MeetingDraft.class))).thenReturn(left(MEETING_NAME_IS_BLANK));
    }

    protected void waitingListWasCreated(GroupMeetingId meetingId, MeetingGroupId groupId) {
        verify(waitingList).createWaitingList(meetingId, groupId);
    }

    protected void meetingCancellingFails(UserId meetingOrganizerId, GroupMeetingId meetingId) {
        when(meetingsCore.cancelMeeting(meetingOrganizerId, meetingId)).thenReturn(left(USER_IS_NOT_MEETING_ORGANIZER));
    }

    protected void waitingListMembersGotNotified(MeetingGotCancelled meetingGotCancelled, WaitingListRemoved waitingListGotRemoved) {
        waitingListMembersGotNotified(meetingGotCancelled.meetingName(), meetingGotCancelled.meetingDate(), waitingListGotRemoved.waitingListMembers());
    }

    private void waitingListMembersGotNotified(String meetingName, LocalDate meetingDate, List<UserId> userIds) {
        verify(meetingsNotifications).notifyAboutMeetingCancellation(meetingName, meetingDate, userIds);
    }

    protected MeetingGotCancelled meetingGetsCancelled(UserId meetingOrganizerId, GroupMeetingId meetingId) {
        var meetingGotCancelled = new MeetingGotCancelled("any name", LocalDate.now(), Set.of(new UserId(1L), new UserId(2L), new UserId(3L)));
        when(meetingsCore.cancelMeeting(meetingOrganizerId, meetingId)).thenReturn(right(meetingGotCancelled));
        return meetingGotCancelled;
    }

    protected WaitingListRemoved waitingListGetsRemoved(GroupMeetingId meetingId) {
        var waitingListRemoved = new WaitingListRemoved(List.of(new UserId(4L), new UserId(5L), new UserId(6L)));
        when(waitingList.removeWaitingList(meetingId)).thenReturn(right(waitingListRemoved));
        return waitingListRemoved;
    }

    protected void meetingAttendeesGotNotified(MeetingGotCancelled meetingGotCancelled) {
        verify(meetingsNotifications).notifyAboutMeetingCancellation(meetingGotCancelled.meetingName(), meetingGotCancelled.meetingDate(), meetingGotCancelled.meetingAttendees());
    }

    protected void meetingHoldingFails(UserId meetingOrganizerId, GroupMeetingId meetingId) {
        when(meetingsCore.holdMeeting(meetingOrganizerId, meetingId)).thenReturn(Option.of(HoldMeetingFailure.CANNOT_HOLD_MEETING_BEFORE_MEETING_DATE));
    }

    protected void meetingWasHeld(GroupMeetingId meetingId) {
        when(meetingsCore.holdMeeting(randomUserId(), meetingId)).thenReturn(Option.none());
    }

    protected void waitingListGotRemoved(GroupMeetingId meetingId) {
        verify(waitingList).removeWaitingList(meetingId);
    }

    protected UserId randomUserId() {
        return new UserId(1L);
    }

    protected UserSignedOffFromMeeting user1SignsOffFromMeeting(UserId user1, GroupMeetingId meetingId) {
        var userSignedOffFromMeeting = new UserSignedOffFromMeeting(user1, meetingId, "meeting name", LocalDate.now());
        when(meetingsCore.signOffFromMeeting(user1, meetingId)).thenReturn(Either.right(userSignedOffFromMeeting));
        return userSignedOffFromMeeting;
    }

    protected void user2GetsSignOnFromWaitingList(UserId user2, GroupMeetingId meetingId) {
        when(waitingList.signOnSomeoneForMeeting(meetingId)).thenReturn(Either.right(user2));
    }

    protected void userToGotNotifiedAboutBeingSignedOnFromWaitingList(UserId user2, UserSignedOffFromMeeting userSignedOffFromMeeting) {
        var meetingName = userSignedOffFromMeeting.meetingName();
        var meetingDate = userSignedOffFromMeeting.meetingDate();
        verify(meetingsNotifications).notifyAboutBeingSignedOnFromWaitingList(user2, meetingName, meetingDate);
    }

    protected void signingOnAnybodyFromWaitingListFails(GroupMeetingId meetingId) {
        when(waitingList.signOnSomeoneForMeeting(meetingId)).thenReturn(Either.left(MEETING_HAS_NO_FREE_SLOTS));
    }

    protected void user1FailsToSignOffFromMeeting(UserId user1, GroupMeetingId meetingId) {
        when(meetingsCore.signOffFromMeeting(user1, meetingId)).thenReturn(Either.left(SignOffFromMeetingFailure.USER_WAS_NOT_SIGNED_ON));
    }
}