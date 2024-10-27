package test.meeting.system.meetings.unit.core;

import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.core.MeetingsCoreConfiguration;
import meeting.system.meetings.core.MeetingsCoreFacade;
import meeting.system.meetings.core.dto.AttendeesLimit;
import meeting.system.meetings.core.dto.GroupMeetingName;
import meeting.system.meetings.core.dto.MeetingDraft;
import meeting.system.meetings.core.ports.Calendar;
import meeting.system.user.funds.UsersFundsFacade;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.UUID.randomUUID;
import static meeting.system.user.funds.dto.ChargeFailure.USER_DOESNT_HAVE_ENOUGH_FUNDS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class MeetingsCoreTestSetup {
    protected MeetingsCoreFacade meetingsCoreFacade;
    protected Calendar calendar;
    protected MeetingGroupsRoles meetingGroupsRoles;
    protected UsersFundsFacade usersFunds;

    @BeforeEach
    public void testSetup() {
        calendar = mock(Calendar.class);
        var date = LocalDate.now();
        when(calendar.getCurrentDate()).thenReturn(date);
        meetingGroupsRoles = mock(MeetingGroupsRoles.class);
        usersFunds = mock(UsersFundsFacade.class);
        meetingsCoreFacade = new MeetingsCoreConfiguration().inMemoryMeetingsCoreFacade(calendar, meetingGroupsRoles, usersFunds);
    }

    protected GroupMeetingId meetingWasScheduled(UserId meetingOrganizerId, MeetingGroupId meetingGroupId) {
        var meetingDraft = new MeetingDraft(meetingGroupId, calendar.getCurrentDate().plusDays(4), new GroupMeetingName("some-meetingName"), new AttendeesLimit(100), Option.none());
        return meetingWasScheduled(meetingOrganizerId, meetingDraft);
    }

    protected GroupMeetingId paidMeetingWasScheduled(UserId meetingOrganizerId, MeetingGroupId meetingGroupId, BigDecimal meetingPrice) {
        var meetingDraft = new MeetingDraft(meetingGroupId, calendar.getCurrentDate().plusDays(4), new GroupMeetingName("some-meetingName"), new AttendeesLimit(100), Option.of(meetingPrice));
        return meetingWasScheduled(meetingOrganizerId, meetingDraft);
    }

    protected GroupMeetingId meetingWasScheduled(UserId meetingOrganizerId, MeetingGroupId groupId, LocalDate meetingDate) {
        return meetingWasScheduled(meetingOrganizerId, new MeetingDraft(groupId, meetingDate, new GroupMeetingName("some name"), new AttendeesLimit(1), Option.none()));
    }

    protected GroupMeetingId meetingWasScheduled(UserId meetingOrganizerId, MeetingDraft meetingDraft) {
        groupExists(meetingDraft.meetingGroupId());
        userIsGroupMember(meetingOrganizerId, meetingDraft.meetingGroupId());
        return meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, meetingDraft).get();
    }

    protected void groupMemberSignedOnToMeeting(UserId groupMemberId, GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId) {
        userIsGroupMember(groupMemberId, meetingGroupId);
        assertTrue(meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId).isEmpty());
    }

    protected void groupMemberSignedOnForMeeting(UserId groupMemberId, GroupMeetingId groupMeetingId, MeetingGroupId meetingGroupId, Option<BigDecimal> meetingPrice) {
        meetingPrice.peek(price -> userHasEnoughFunds(groupMemberId, price));
        userIsGroupMember(groupMemberId, meetingGroupId);
        assertTrue(meetingsCoreFacade.signOnForMeeting(groupMemberId, groupMeetingId).isEmpty());
    }

    protected void userHasEnoughFunds(UserId groupMemberId, BigDecimal meetingFee) {
        var userId = new UserId(groupMemberId.id());
        when(usersFunds.charge(userId, meetingFee)).thenReturn(Option.none());
    }

    protected void userDoesNotHaveEnoughFunds(UserId groupMemberId, BigDecimal meetingFee) {
        var userId = new UserId(groupMemberId.id());
        when(usersFunds.charge(userId, meetingFee)).thenReturn(Option.of(USER_DOESNT_HAVE_ENOUGH_FUNDS));
    }

    protected void groupExists(MeetingGroupId meetingGroupId) {
        when(meetingGroupsRoles.groupExists(meetingGroupId)).thenReturn(true);
    }

    protected void userIsGroupMember(UserId groupMemberId, MeetingGroupId meetingGroupId) {
        when(meetingGroupsRoles.isGroupMember(groupMemberId, meetingGroupId)).thenReturn(true);
    }

    protected void userIsNotGroupMember(UserId groupMemberId, MeetingGroupId meetingGroupId) {
        when(meetingGroupsRoles.isGroupMember(groupMemberId, meetingGroupId)).thenReturn(false);
    }

    protected void groupMemberSignedOffFromMeeting(UserId groupMemberId, GroupMeetingId groupMeetingId) {
        assert meetingsCoreFacade.signOffFromMeeting(groupMemberId, groupMeetingId).isRight();
    }

    protected void groupMemberGetsMoneyReturned(UserId groupMemberId, BigDecimal meetingPrice) {
        verify(usersFunds).returnFunds(groupMemberId, meetingPrice);
    }

    protected MeetingGroupId randomGroupId() {
        return new MeetingGroupId(randomLong());
    }

    protected GroupMeetingId randomMeetingId() {
        return new GroupMeetingId(randomLong());
    }

    protected GroupMeetingName uniqueNotBlankMeetingName() {
        return new GroupMeetingName(randomUUID().toString());
    }

    protected UserId randomUserId() {
        return new UserId(randomLong());
    }

    private long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }
}