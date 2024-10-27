package test.meeting.system.meetings.unit;

import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verifyNoInteractions;

public class MeetingSignOff extends MeetingsTestSetup {
    private final UserId user1 = new UserId(1L);
    private final UserId user2 = new UserId(2L);

    @Test
    @DisplayName("if user 1 signs off from a meeting and user 2 gets signed on from waiting list, then user 2 should be notified about being signed on")
    public void test1() {
//        given
        var userSignedOffFromMeeting = user1SignsOffFromMeeting(user1, meetingId);
//        and
        user2GetsSignOnFromWaitingList(user2, meetingId);
//        when
        var result = meetingsFacade.signOffFromMeeting(user1, meetingId);
//        then
        userToGotNotifiedAboutBeingSignedOnFromWaitingList(user2, userSignedOffFromMeeting);
    }

    @Test
    @DisplayName("if user signs off from meeting and signing on from waiting list fails, nobody gets notified")
    public void test2() {
//        given
        user1SignsOffFromMeeting(user1, meetingId);
//        and
        signingOnAnybodyFromWaitingListFails(meetingId);
//        when
        meetingsFacade.signOffFromMeeting(user1, meetingId);
//        then
        verifyNoInteractions(meetingsNotifications);
    }

    @Test
    @DisplayName("if user1 fails to sign off from meeting, then nobody should signed on from waiting list and nobody should be notified")
    public void test3() {
//        given
        user1FailsToSignOffFromMeeting(user1, meetingId);
//        when
        meetingsFacade.signOffFromMeeting(user1, meetingId);
//        then
        verifyNoInteractions(waitingList);
//        and
        verifyNoInteractions(meetingsNotifications);
    }
}