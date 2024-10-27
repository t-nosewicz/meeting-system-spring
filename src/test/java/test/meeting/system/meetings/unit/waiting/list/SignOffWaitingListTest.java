package test.meeting.system.meetings.unit.waiting.list;

import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import static meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure.USER_WAS_NOT_ON_WAITING_LIST;
import static meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure.WAITING_LIST_DOESNT_EXIST;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignOffWaitingListTest extends WaitingListTestSetup {

    @Test
    public void shouldFailIfWaitingListDoesntExist() {
//        given
        waitingList.removeWaitingList(meetingId);
//        when
        var result = waitingList.signOffWaitingList(groupMemberId, meetingId);
//        then
        assertEquals(result, Option.of(WAITING_LIST_DOESNT_EXIST));
    }

    @Test
    public void shouldFailIfUserWasNotOnWaitingList() {
//        given
        waitingList.createWaitingList(meetingId, groupId);
//        when
        var result = waitingList.signOffWaitingList(groupMemberId, meetingId);
//        then
        assertEquals(result, Option.of(USER_WAS_NOT_ON_WAITING_LIST));
    }
}