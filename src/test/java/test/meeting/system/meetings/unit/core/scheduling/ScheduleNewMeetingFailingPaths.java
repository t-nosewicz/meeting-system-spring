package test.meeting.system.meetings.unit.core.scheduling;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.core.dto.AttendeesLimit;
import meeting.system.meetings.core.dto.GroupMeetingName;
import meeting.system.meetings.core.dto.MeetingDraft;
import meeting.system.meetings.core.dto.ScheduleMeetingFailure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.vavr.control.Either.left;
import static java.math.BigDecimal.ZERO;
import static meeting.system.meetings.core.dto.ScheduleMeetingFailure.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScheduleNewMeetingFailingPaths extends MeetingsCoreTestSetup {
    private final UserId meetingOrganizerId = new UserId(1L);
    private final MeetingGroupId groupId = new MeetingGroupId(2L);
    private final GroupMeetingName meetingName = new GroupMeetingName("random meetingName");

    @BeforeEach
    public void init() {
        groupExists(groupId);
        userIsGroupMember(meetingOrganizerId, groupId);
    }

    @Test
    public void schedulingMeetingWithDateThatIsLessThan3DaysInAdvanceShouldFail() {
//        when
        var meetingDate = calendar.getCurrentDate().plusDays(2);
        var result = meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, correctMeetingDraft(meetingDate));
//        then
        assertEquals(left(ScheduleMeetingFailure.MEETING_DATE_IS_NOT_3_DAYS_IN_ADVANCE), result);
    }

    @Test
    public void meetingSchedulingMForNotExistingGroupShouldFail() {
//        when
        var result = meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, correctMeetingDraft(randomGroupId()));
//        then
        assertEquals(left(MEETING_GROUP_DOES_NOT_EXIST), result);
    }

    @Test
    public void schedulingMeetingWithBlankNameShouldFail() {
//        when
        var result = meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, correctMeetingDraft(blankName()));
//        then
        assertEquals(left(MEETING_NAME_IS_BLANK), result);
    }

    @Test
    public void schedulingMeetingWithNameThatIsNotUniqueInParticularGroupShouldFail() {
//        given
        meetingWasScheduled(meetingName);
//        when
        var result = meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, correctMeetingDraft(meetingName));
//        then
        assertEquals(left(MEETING_NAME_IS_NOT_UNIQUE), result);
    }

    @Test
    public void schedulingMeetingWithAttendeesLimitLessThanOneShouldFail() {
//        when
        var result = meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, correctMeetingDraft(attendeesLimitLessThanOne()));
//        then
        assertEquals(left(ATTENDEES_LIMIT_CANNOT_BE_LESS_THAN_ONE), result);
    }

    @Test
    public void schedulingMeetingWithFeeThatIsLessThanOneShouldFail() {
//        when
        var result = meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, correctMeetingDraft(feeLessThanOne()));
//        then
        assertEquals(left(FEE_CANNOT_BE_LOWER_THAN_ONE), result);
    }

    private void meetingWasScheduled(GroupMeetingName meetingName) {
        meetingWasScheduled(meetingOrganizerId, correctMeetingDraft(meetingName));
    }

    private MeetingDraft correctMeetingDraft(int attendeesLimit) {
        return new MeetingDraft(groupId, date3DaysFromNow(), uniqueNotBlankMeetingName(), new AttendeesLimit(attendeesLimit), Option.none());
    }

    private MeetingDraft correctMeetingDraft(Option<BigDecimal> fee) {
        return new MeetingDraft(groupId, date3DaysFromNow(), uniqueNotBlankMeetingName(), new AttendeesLimit(50), fee);
    }

    private MeetingDraft correctMeetingDraft(MeetingGroupId meetingGroupId) {
        return new MeetingDraft(meetingGroupId, date3DaysFromNow(), uniqueNotBlankMeetingName(), new AttendeesLimit(50), Option.none());
    }

    private MeetingDraft correctMeetingDraft(LocalDate meetingDate) {
        return new MeetingDraft(groupId, meetingDate, uniqueNotBlankMeetingName(), new AttendeesLimit(50), Option.none());
    }

    private MeetingDraft correctMeetingDraft(GroupMeetingName groupMeetingName) {
        return new MeetingDraft(groupId, date3DaysFromNow(), groupMeetingName, new AttendeesLimit(50), Option.none());
    }

    private MeetingDraft correctMeetingDraft() {
        return new MeetingDraft(
                groupId,
                date3DaysFromNow(),
                uniqueNotBlankMeetingName(),
                new AttendeesLimit(50),
                Option.none());
    }

    private LocalDate date3DaysFromNow() {
        return calendar.getCurrentDate().plusDays(3);
    }

    private GroupMeetingName blankName() {
        return new GroupMeetingName("    ");
    }

    private GroupMeetingName notUniqueName() {
        return new GroupMeetingName("not-unique-meetingName");
    }

    private Option<BigDecimal> feeLessThanOne() {
        return Option.of(ZERO);
    }

    private int attendeesLimitLessThanOne() {
        return 0;
    }
}