package test.meeting.system.meetings.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.system.MeetingSystemSpringApplication;
import meeting.system.commons.ObjectMapperConfiguration;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.MeetingsConfiguration;
import meeting.system.meetings.core.MeetingsCoreConfiguration;
import meeting.system.meetings.core.dto.*;
import meeting.system.meetings.http.MeetingsController;
import meeting.system.meetings.waiting.list.WaitingListConfiguration;
import meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure;
import meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure;
import meeting.system.notifications.NotificationsFacade;
import meeting.system.user.funds.UsersFundsFacade;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import test.meeting.system.test.utils.IntegrationTestBase;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(
        classes = {
                MeetingsConfiguration.class,
                MeetingsCoreConfiguration.class,
                WaitingListConfiguration.class,
                MeetingsController.class,
                ObjectMapperConfiguration.class},
        properties = {"spring.jpa.show-sql=true", "spring.jpa.properties.hibernate.format_sql=true"})
public class MeetingsIntegrationTestSetup extends IntegrationTestBase {
    @MockBean
    private MeetingGroupsRoles meetingGroupsRoles;
    @MockBean
    private NotificationsFacade notificationsFacade;
    @MockBean
    private UsersFundsFacade usersFunds;

    protected Either<ScheduleMeetingFailure, GroupMeetingId> scheduleMeeting(UserId meetingOrganizerId, MeetingDraft meetingDraft) throws Exception {
        logIn(meetingOrganizerId);
        groupExists(meetingDraft.meetingGroupId());
        userIsGroupMember(meetingOrganizerId, meetingDraft.meetingGroupId());
        var request = post("/meetings/scheduling").contentType(APPLICATION_JSON).content(serialize(meetingDraft));
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<SignOnForMeetingFailure> signOnForMeeting(GroupMeetingId meetingId, MeetingGroupId groupId, UserId userId) throws Exception {
        logIn(userId);
        userIsGroupMember(userId, groupId);
        chargingForPaidMeetingSignOnSucceeds();
        var request = post("/meetings/sign-on/{meetingId}", meetingId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<SignOnWaitListFailure> signOnWaitingList(GroupMeetingId meetingId, MeetingGroupId groupId, UserId userId) throws Exception {
        logIn(userId);
        userIsGroupMember(userId, groupId);
        var request = post("/meetings/waiting-list/sign-on/{meetingId}", meetingId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<SignOffFromWaitListFailure> signOffFromWaitingList(GroupMeetingId meetingId, MeetingGroupId groupId, UserId userId) throws Exception {
        logIn(userId);
        var request = post("/meetings/waiting-list/sign-off/{meetingId}", meetingId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<SignOffFromMeetingFailure> signOffFromMeeting(GroupMeetingId meetingId, MeetingGroupId groupId, UserId userId) throws Exception {
        logIn(userId);
        userIsGroupMember(userId, groupId);
        var request = post("/meetings/sign-off/{meetingId}", meetingId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<MeetingDetails> findMeetingDetails(GroupMeetingId meetingId) throws Exception {
        var request = get("/meetings/{meetingId}", meetingId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<CancelMeetingFailure> cancelMeeting(GroupMeetingId meetingId, MeetingGroupId groupId, UserId userId) throws Exception {
        logIn(userId);
        userIsGroupMember(userId, groupId);
        var request = post("/meetings/cancelling/{meetingId}", meetingId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    private void groupExists(MeetingGroupId meetingGroupId) {
        when(meetingGroupsRoles.groupExists(meetingGroupId)).thenReturn(true);
    }

    private void userIsGroupMember(UserId userId, MeetingGroupId groupId) {
        when(meetingGroupsRoles.isGroupMember(userId, groupId)).thenReturn(true);
    }

    private void chargingForPaidMeetingSignOnSucceeds() {
        when(usersFunds.charge(any(UserId.class), any(BigDecimal.class))).thenReturn(Option.none());
    }
}