package test.meeting.system.meetings.unit.waiting.list;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.core.MeetingsCoreFacade;
import meeting.system.meetings.waiting.list.WaitingListConfiguration;
import meeting.system.meetings.waiting.list.WaitingListFacade;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.stubbing.OngoingStubbing;

import java.util.concurrent.ThreadLocalRandom;

import static io.vavr.control.Option.of;
import static meeting.system.meetings.core.dto.SignOnForMeetingFailure.NO_FREE_ATTENDEE_SLOTS;
import static meeting.system.meetings.core.dto.SignOnForMeetingFailure.USER_IS_NOT_GROUP_MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WaitingListTestSetup {
    protected WaitingListFacade waitingList;
    protected MeetingsCoreFacade meetingsCoreFacade;
    protected MeetingGroupsRoles meetingGroupsRoles;
    protected UserId groupMemberId = new UserId(1L);
    protected GroupMeetingId meetingId = new GroupMeetingId(2L);
    protected MeetingGroupId groupId = new MeetingGroupId(3L);

    @BeforeEach
    public void init() {
        meetingsCoreFacade = mock(MeetingsCoreFacade.class);
        meetingGroupsRoles = mock(MeetingGroupsRoles.class);
        waitingList = new WaitingListConfiguration().inMemoryMeetingsWaitingListFacade(meetingsCoreFacade, meetingGroupsRoles);
    }

    protected void waitingListIsFull() {
        for (int i = 0; i < 50; i++) {
            var groupMemberId = new UserId(randomLong());
            groupMemberSignedOnWaitingList(groupMemberId, meetingId);
        }
    }

    protected void everyoneFailsToSignOnForMeeting() {
        when(meetingsCoreFacade.signOnForMeeting(any(UserId.class), any(GroupMeetingId.class))).thenReturn(of(USER_IS_NOT_GROUP_MEMBER));
    }

    protected void meetingHasNoFreeSlots() {
        when(meetingsCoreFacade.signOnForMeeting(groupMemberId, meetingId)).thenReturn(of(NO_FREE_ATTENDEE_SLOTS));
    }

    protected void signOnForMeetingSucceeds() {
         when(meetingsCoreFacade.signOnForMeeting(any(UserId.class), any(GroupMeetingId.class))).thenReturn(Option.none());
    }

    protected void groupMemberIsSignedOnToMeeting(UserId groupMemberId, GroupMeetingId meetingId) {
        when(meetingsCoreFacade.userIsSignedOn(groupMemberId, meetingId)).thenReturn(true);
    }

    protected void userIsNotGroupMember(UserId userId, MeetingGroupId meetingGroupId) {
        when(meetingGroupsRoles.isGroupMember(userId, meetingGroupId)).thenReturn(false);
    }

    protected void userIsGroupMember(UserId userId, MeetingGroupId meetingGroupId) {
        when(meetingGroupsRoles.isGroupMember(userId, meetingGroupId)).thenReturn(true);
    }

    protected void groupMemberSignedOnWaitingList(UserId groupMemberId, GroupMeetingId meetingId) {
        userIsGroupMember(groupMemberId, groupId);
        var result = waitingList.signOnWaitingList(groupMemberId, meetingId);
        assertEquals(Option.none(), result);
    }

    protected OngoingStubbing<Boolean> meetingStillHasSomeFreeSpots() {
        return when(meetingsCoreFacade.hasFreeSpots(meetingId)).thenReturn(true);
    }

    protected GroupMeetingId randomMeetingId() {
        return new GroupMeetingId(randomLong());
    }

    private Long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }
}