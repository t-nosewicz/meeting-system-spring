package test.meeting.system.meetings.unit.core.signing.on;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import java.math.BigDecimal;

import static meeting.system.meetings.core.dto.SignOnForMeetingFailure.USER_DOESNT_HAVE_ENOUGH_FUNDS_TO_SIGN_ON_FOR_PAID_MEETING;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignOnToPaidMeetingTest extends MeetingsCoreTestSetup {
    private final UserId groupMemberId = new UserId(1L);
    private final MeetingGroupId groupId = new MeetingGroupId(2L);
    private final UserId groupOrganizerId = new UserId(3L);
    private final BigDecimal meetingFee = BigDecimal.valueOf(20);

    @Test
    public void shouldFailIfGroupMemberDoesntHaveEnoughFunds() {
//        given
        var meetingId = paidMeetingWasScheduled(groupOrganizerId, groupId, meetingFee);
//        and
        userIsGroupMember(groupMemberId, groupId);
//        and
        userDoesNotHaveEnoughFunds(groupMemberId, meetingFee);
//        when
        var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, meetingId);
//        then
        assertEquals(Option.of(USER_DOESNT_HAVE_ENOUGH_FUNDS_TO_SIGN_ON_FOR_PAID_MEETING), result);
    }

    @Test
    public void happyPath() {
//        given
        var meetingId = paidMeetingWasScheduled(groupOrganizerId, groupId, meetingFee);
//        and
        userIsGroupMember(groupMemberId, groupId);
//        and
        userHasEnoughFunds(groupMemberId, meetingFee);
//        when
        var result = meetingsCoreFacade.signOnForMeeting(groupMemberId, meetingId);
//        then
        assertEquals(Option.none(), result);
    }
}