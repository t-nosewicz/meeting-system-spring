package test.meeting.system.meetings.unit.core.cancelling;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

public class CancelMeetingHappyPath extends MeetingsCoreTestSetup {
    private final UserId meetingOrganizerId = new UserId(1L);
    private final MeetingGroupId meetingGroupId = new MeetingGroupId(2L);
    private final BigDecimal meetingFee = BigDecimal.valueOf(20);

    @Test
    public void cancelFreeMeeting() {
//        given
        var meetingId = meetingWasScheduled(meetingOrganizerId, meetingGroupId);
//        and
        fewGroupMembersSignedOnToMeeting(meetingId);
//        when
        var result = meetingsCoreFacade.cancelMeeting(meetingOrganizerId, meetingId);
//        then
        assertTrue(result.isRight());
//        and
        verifyNoInteractions(usersFunds);
    }

    @Test
    public void cancelPaidMeeting() {
//        given
        var meetingId = paidMeetingWasScheduled();
//        and
        var groupMembers = fewGroupMembersSignedOnForPaidMeeting(meetingId);
//        when
        var result = meetingsCoreFacade.cancelMeeting(meetingOrganizerId, meetingId);
//        then
        assertTrue(result.isRight());
//        and
        signedOnGroupMembersGetTheyMoneyBack(groupMembers);
    }

    private GroupMeetingId paidMeetingWasScheduled() {
        return paidMeetingWasScheduled(meetingOrganizerId, meetingGroupId, meetingFee);
    }

    private List<UserId> fewGroupMembersSignedOnForPaidMeeting(GroupMeetingId groupMeetingId) {
        return getRandomGroupMembers()
                .stream()
                .peek(groupMemberId -> groupMemberSignedOnForMeeting(groupMemberId, groupMeetingId, meetingGroupId, Option.of(meetingFee)))
                .toList();
    }

    private List<UserId> getRandomGroupMembers() {
        return List.of(randomUserId(), randomUserId(), randomUserId());
    }

    private void signedOnGroupMembersGetTheyMoneyBack(List<UserId> groupMembers) {
        groupMembers.forEach(groupMemberId -> groupMemberGetsMoneyReturned(groupMemberId, meetingFee));
    }

    protected void fewGroupMembersSignedOnToMeeting(GroupMeetingId meetingId) {
        getRandomGroupMembers().forEach(groupMemberId -> groupMemberSignedOnForMeeting(groupMemberId, meetingId, meetingGroupId, Option.none()));
    }
}