package test.meeting.system.group.disbanding.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vavr.control.Option;
import meeting.system.commons.ObjectMapperConfiguration;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.group.disbanding.GroupDisbandingConfiguration;
import meeting.system.group.disbanding.http.GroupDisbandingController;
import meeting.system.meeting.groups.MeetingGroupsFacade;
import meeting.system.meeting.groups.dto.DisbandGroupResult;
import meeting.system.meeting.groups.dto.DisbandGroupResult.Failure;
import meeting.system.meetings.MeetingsFacade;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import test.meeting.system.test.utils.IntegrationTestBase;

import static meeting.system.meeting.groups.dto.DisbandGroupResult.Failure.GROUP_DOESNT_EXIST;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(
        classes = {
                GroupDisbandingConfiguration.class,
                GroupDisbandingController.class,
                ObjectMapperConfiguration.class},
        properties = {"spring.jpa.show-sql=true", "spring.jpa.properties.hibernate.format_sql=true"})
public class GroupDisbandingIntegrationtSetup extends IntegrationTestBase {
    @MockBean
    protected MeetingGroupsFacade meetingGroupsFacade;
    @MockBean
    protected MeetingsFacade meetingsQueryFacade;

    protected Option<Failure> disbandGroup(UserId userId, MeetingGroupId groupId) throws Exception {
        logIn(userId);
        var request = post("/groups/disbanding/{groupId}", groupId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected void groupHasNoScheduledMeetings(MeetingGroupId groupId) {
        when(meetingsQueryFacade.areAnyMeetingsScheduledForGroup(groupId)).thenReturn(false);
    }

    protected void groupHasScheduledMeetings(MeetingGroupId groupId) {
        when(meetingsQueryFacade.areAnyMeetingsScheduledForGroup(groupId)).thenReturn(true);
    }

    protected void meetingGroupsModuleSucceedsToDisbandTheGroup(MeetingGroupId groupId, UserId userId) {
        when(meetingGroupsFacade.disbandGroup(userId, groupId)).thenReturn(new DisbandGroupResult.Success());
    }

    protected void meetingGroupsModuleFailsToDisbandTheGroup(MeetingGroupId groupId, UserId userId, Failure failure) {
        when(meetingGroupsFacade.disbandGroup(userId, groupId)).thenReturn(failure);
    }

    protected Failure failure() {
        return GROUP_DOESNT_EXIST;
    }
}