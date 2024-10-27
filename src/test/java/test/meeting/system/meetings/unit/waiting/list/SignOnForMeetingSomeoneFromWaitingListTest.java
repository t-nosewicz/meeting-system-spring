package test.meeting.system.meetings.unit.waiting.list;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static meeting.system.meetings.waiting.list.dto.MeetingSignOnFromWaitingListFailure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignOnForMeetingSomeoneFromWaitingListTest extends WaitingListTestSetup {

    @BeforeEach
    public void initSignOnForMeeting() {
        waitingList.createWaitingList(meetingId, groupId);
    }

    @Test
    public void shouldFailForEmptyWaitingList() {
//        when
        var result = waitingList.signOnSomeoneForMeeting(meetingId);
//        then
        assertEquals(left(WAITING_LIST_IS_EMPTY), result);
    }

    @Test
    public void shouldFailIfWaitingListDoesNotExist() {
//        given
        waitingList.removeWaitingList(meetingId);
//        when
        var result = waitingList.signOnSomeoneForMeeting(meetingId);
//        then
        assertEquals(left(WAITING_LIST_DOESNT_EXIST), result);
    }

    @Test
    public void shouldFailIfEveryoneFromWaitingListFailed() {
//        given
        groupMemberSignedOnWaitingList(new UserId(1L), meetingId);
        groupMemberSignedOnWaitingList(new UserId(2L), meetingId);
        groupMemberSignedOnWaitingList(new UserId(3L), meetingId);
//        and
        everyoneFailsToSignOnForMeeting();
//        when
        var result = waitingList.signOnSomeoneForMeeting(meetingId);
//        then
        assertEquals(left(NONE_OF_PEOPLE_FROM_WAITING_LIST_WERE_ABLE_TO_SIGN_ON), result);
    }

    @Test
    public void shouldFailIfMeetingHasNoFreeSlots() {
//        given
        meetingHasNoFreeSlots();
//        and
        groupMemberSignedOnWaitingList(groupMemberId, meetingId);
//        when
        var result = waitingList.signOnSomeoneForMeeting(meetingId);
//        then
        assertEquals(left(MEETING_HAS_NO_FREE_SLOTS), result);
    }

    @Test
    public void shouldSignOnForMeetingPersonThatWasFirstOnWaitingList() {
//        given
        groupMemberSignedOnWaitingList(new UserId(1L), meetingId);
        groupMemberSignedOnWaitingList(new UserId(2L), meetingId);
        groupMemberSignedOnWaitingList(new UserId(3L), meetingId);
//        and
        signOnForMeetingSucceeds();
//        when
        var result = waitingList.signOnSomeoneForMeeting(meetingId);
//        then
        assertEquals(right(new UserId(1L)), result);
    }
}