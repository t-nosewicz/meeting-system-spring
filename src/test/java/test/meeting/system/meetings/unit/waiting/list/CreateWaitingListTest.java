package test.meeting.system.meetings.unit.waiting.list;

import meeting.system.meetings.waiting.list.dto.CreateWaitingListFailure;
import org.junit.jupiter.api.Test;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateWaitingListTest extends WaitingListTestSetup {

    @Test
    public void creatingWaitingListTwiceForTheSameMeetingShouldFail() {
//        given
        waitingList.createWaitingList(meetingId, groupId);
//        when
        var result = waitingList.createWaitingList(meetingId, groupId);
//        then
        assertEquals(of(CreateWaitingListFailure.WAITING_LIST_ALREADY_EXIST), result);
    }

    @Test
    public void success() {
//        when
        var result = waitingList.createWaitingList(meetingId, groupId);
//        then
        assertEquals(none(), result);
    }
}