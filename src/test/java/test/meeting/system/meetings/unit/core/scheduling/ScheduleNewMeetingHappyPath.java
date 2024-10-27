package test.meeting.system.meetings.unit.core.scheduling;

import io.vavr.control.Option;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meetings.core.dto.AttendeesLimit;
import meeting.system.meetings.core.dto.GroupMeetingName;
import meeting.system.meetings.core.dto.MeetingDraft;
import org.junit.jupiter.api.Test;
import test.meeting.system.meetings.unit.core.MeetingsCoreTestSetup;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ONE;

public class ScheduleNewMeetingHappyPath extends MeetingsCoreTestSetup {
    protected final MeetingGroupId meetingGroupId = new MeetingGroupId(1L);
    protected final UserId meetingOrganizerId = new UserId(2L);

    @Test
    public void scheduleGroupMeetingHappyPath() {
//        given
        groupExists(meetingGroupId);
//        and
        userIsGroupMember(meetingOrganizerId, meetingGroupId);
//        and
        var date3DaysFromNow = calendar.getCurrentDate().plusDays(3);
//        and
        var groupMeetingName = uniqueNotBlankMeetingName();
//        and
        var attendeesLimit = attendeesLimitIsAtLeastOne();
//        and
        var fee = feeIsAtLeastOne();
//        when
        var meetingDraft = createMeetingDraft(meetingGroupId, date3DaysFromNow, groupMeetingName, attendeesLimit, fee);
        var result = meetingsCoreFacade.scheduleNewMeeting(meetingOrganizerId, meetingDraft);
//        then
        assert result.isRight();
    }

    private MeetingDraft createMeetingDraft(MeetingGroupId meetingGroupId, LocalDate date3DaysFromNow, GroupMeetingName groupMeetingName, AttendeesLimit attendeesLimit, Option<BigDecimal> fee) {
        return new MeetingDraft(meetingGroupId, date3DaysFromNow, groupMeetingName, attendeesLimit, fee);
    }

    private AttendeesLimit attendeesLimitIsAtLeastOne() {
        return new AttendeesLimit(50);
    }

    private Option<BigDecimal> feeIsAtLeastOne() {
        return Option.of(ONE);
    }
}