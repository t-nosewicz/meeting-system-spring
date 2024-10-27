package test.meeting.system.meetings.unit.core.signing.off;

import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

public class SignOffFromMeetingHappyPath extends MeetingsCoreTestSetup {
    private final UserId meetingOrganizerId = new UserId(1L);
    private final MeetingGroupId meetingGroupId = new MeetingGroupId(2L);
    private final UserId groupMemberId = new UserId(3L);
    private final BigDecimal fee = BigDecimal.valueOf(30);

    @Test
    public void freeMeetingSignOffSuccess() {
//        given
        var groupMeetingId = meetingWasScheduled(meetingOrganizerId, meetingGroupId);
//        and
        groupMemberSignedOnToMeeting(groupMemberId, groupMeetingId);
//        when
        var result = meetingsCoreFacade.signOffFromMeeting(groupMemberId, groupMeetingId);
//        then
        assertTrue(result.isRight());
//        and
        verifyNoInteractions(usersFunds);
    }

    @Test
    public void paidMeetingSignOffSuccess() {
//        given
        var groupMeetingId = paidMeetingWasScheduled(meetingOrganizerId, meetingGroupId, fee);
//        and
        groupMemberSignedOnToPaidMeeting(groupMemberId, groupMeetingId, fee);
//        when
        var result = meetingsCoreFacade.signOffFromMeeting(groupMemberId, groupMeetingId);
//        then
        assertTrue(result.isRight());
//        and
        groupMemberGetsMoneyReturned(groupMemberId, fee);
    }

    private void groupMemberSignedOnToMeeting(UserId groupMemberId, GroupMeetingId groupMeetingId) {
        groupMemberSignedOnToMeeting(groupMemberId, groupMeetingId, meetingGroupId);
    }

    private void groupMemberSignedOnToPaidMeeting(UserId groupMemberId, GroupMeetingId groupMeetingId, BigDecimal fee) {
        userHasEnoughFunds(groupMemberId, fee);
        groupMemberSignedOnToMeeting(groupMemberId, groupMeetingId);
    }
}