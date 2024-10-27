package test.meeting.system.meetings.unit.waiting.list;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignOnWaitingListTest extends WaitingListTestSetup {

    @BeforeEach
    public void initSignOnWaitingList() {
        waitingList.createWaitingList(meetingId, groupId);
    }

    @Test
    public void shouldFailIfUserIsNotGroupMember() {
//        given
        userIsNotGroupMember(groupMemberId, groupId);
//        when
        var result = waitingList.signOnWaitingList(groupMemberId, meetingId);
//        then
        assertEquals(of(USER_IS_NOT_GROUP_MEMBER), result);
    }

    @Test
    public void shouldFailIfWaitingListDoesntExist() {
//        given
        waitingList.removeWaitingList(meetingId);
//        when
        var result = waitingList.signOnWaitingList(groupMemberId, randomMeetingId());
//        then
        assertEquals(of(WAITING_LIST_DOES_NOT_EXIST), result);
    }

    @Test
    public void shouldFailIfMeetingMeetingStillHasFreeSpots() {
//        given
        meetingStillHasSomeFreeSpots();
//        when
        var result = waitingList.signOnWaitingList(groupMemberId, meetingId);
//        then
        assertEquals(of(MEETING_HAS_FREE_SLOTS), result);
    }

    @Test
    public void shouldFailIfUserIsAlreadySignedOnToWaitingList() {
//        given
        groupMemberSignedOnWaitingList(groupMemberId, meetingId);
//        when
        var result = waitingList.signOnWaitingList(groupMemberId, meetingId);
//        then
        assertEquals(of(USER_ALREADY_IS_ON_WAITING_LIST), result);
    }

    @Test
    public void shouldFailIfUserAlreadySignedOnToMeeting() {
//        given
        userIsGroupMember(groupMemberId, groupId);
//        and
        groupMemberIsSignedOnToMeeting(groupMemberId, meetingId);
//        when
        var result = waitingList.signOnWaitingList(groupMemberId, meetingId);
//        then
        assertEquals(of(USER_ALREADY_IS_SIGNED_ON_TO_MEETING), result);
    }

    @Test
    public void success() {
//        given
        userIsGroupMember(groupMemberId, groupId);
//        when
        var result = waitingList.signOnWaitingList(groupMemberId, meetingId);
//        then
        assertEquals(none(), result);
    }
}