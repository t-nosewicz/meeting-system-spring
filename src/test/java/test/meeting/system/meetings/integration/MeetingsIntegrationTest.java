package test.meeting.system.meetings.integration;

import io.vavr.control.Option;
import io.vavr.control.Try;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.core.dto.*;
import meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure;
import meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MeetingsIntegrationTest extends MeetingsIntegrationTestSetup {
    private final UserId meetingOrganizerId = randomUserId();
    private final MeetingGroupId groupId = randomGroupId();
    private final List<UserId> meetingAttendees = List.of(randomUserId(), randomUserId(), randomUserId());
    private final AttendeesLimit attendeesLimit = new AttendeesLimit(meetingAttendees.size());
    private final List<UserId> waitingListMembers = List.of(randomUserId(), randomUserId(), randomUserId());

    @Test
    public void test() throws Exception {
        var meetingId = scheduleMeeting(meetingOrganizerId, getMeetingDraft()).get();
        signOnFewUsersForMeeting(meetingId);
        signFewUsersOnWaitingList(meetingId);
        singOffSomeoneFromWaitingList(meetingId);
        signOffSomeoneFromMeeting(meetingId);
        assertTrue(findMeetingDetails(meetingId).isDefined());
        cancelMeeting(meetingId, groupId, meetingOrganizerId);
        assertTrue(findMeetingDetails(meetingId).isEmpty());

    }

    private void signOnFewUsersForMeeting(GroupMeetingId meetingId) throws Exception {
        for (var userId : meetingAttendees) {
            Option<SignOnForMeetingFailure> result = signOnForMeeting(meetingId, groupId, userId);
            assertEquals(Option.none(), result);
        }
    }

    private void signFewUsersOnWaitingList(GroupMeetingId meetingId) throws Exception {
        for (var userId : waitingListMembers) {
            Option<SignOnWaitListFailure> result = signOnWaitingList(meetingId, groupId, userId);
            assertEquals(Option.none(), result);
        }
    }

    private void singOffSomeoneFromWaitingList(GroupMeetingId meetingId) {
        waitingListMembers
                .stream().findAny()
                .ifPresent(userId -> Try.run(() -> {
                    Option<SignOffFromWaitListFailure> result = signOffFromWaitingList(meetingId, groupId, userId);
                    assertEquals(Option.none(), result);
                }));
    }

    private void signOffSomeoneFromMeeting(GroupMeetingId meetingId) {
        meetingAttendees
                .stream().findAny()
                .ifPresent(userId -> Try.run(() -> {
                    Option<SignOffFromMeetingFailure> result = signOffFromMeeting(meetingId, groupId, userId);
                    assertEquals(Option.none(), result);
                }));
    }

    private MeetingDraft getMeetingDraft() {
        return new MeetingDraft(groupId, LocalDate.now().plusDays(4), new GroupMeetingName("random name"), attendeesLimit, Option.of(TEN));
    }

    private MeetingGroupId randomGroupId() {
        return new MeetingGroupId(ThreadLocalRandom.current().nextLong());
    }
}