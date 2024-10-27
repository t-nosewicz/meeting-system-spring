package test.meeting.system.groups.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.system.commons.ObjectMapperConfiguration;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.dto.UserId;
import meeting.system.meeting.groups.MeetingGroupsConfiguration;
import meeting.system.meeting.groups.MeetingGroupsFacade;
import meeting.system.meeting.groups.dto.*;
import meeting.system.meeting.groups.http.MeetingGroupsController;
import meeting.system.meetings.MeetingsConfiguration;
import meeting.system.meetings.core.MeetingsCoreConfiguration;
import meeting.system.meetings.http.MeetingsController;
import meeting.system.meetings.waiting.list.WaitingListConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.meeting.system.test.utils.IntegrationTestBase;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(
        classes = {
                MeetingGroupsConfiguration.class,
                MeetingGroupsController.class,
                ObjectMapperConfiguration.class},
        properties = {"spring.jpa.show-sql=true", "spring.jpa.properties.hibernate.format_sql=true"})
public class MeetingGroupsIntegrationSetup extends IntegrationTestBase {
    @Autowired
    private MeetingGroupsFacade meetingGroupsFacade;

    protected Either<SubmitProposalResult.Failure, SubmitProposalResult.Success> submitGroupProposal(UserId userId, NewGroupProposal newGroupProposal) throws Exception {
        logIn(userId);
        var request = post("/groups/proposals/submission")
                .contentType(APPLICATION_JSON)
                .content(serialize(newGroupProposal));
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Either<AcceptProposalResult.Failure, AcceptProposalResult.Success> acceptProposal(UserId userId, GroupProposalId proposalId) throws Exception {
        logIn(userId);
        var request = post("/groups/proposals/acceptance/{proposalId}", proposalId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<JoinGroupResult.Failure> joinGroup(UserId userId, MeetingGroupId groupId) throws Exception {
        logIn(userId);
        var request = post("/groups/joining/{groupId}", groupId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected Option<LeaveGroupResult.Failure> leaveGroup(UserId userId, MeetingGroupId groupId) throws Exception {
        logIn(userId);
        var request = post("/groups/leaving/{groupId}", groupId.id());
        return httpCall(request, new TypeReference<>() {
        });
    }

    protected DisbandGroupResult disbandTheGroup(UserId groupOrganizerId, MeetingGroupId groupId) {
        return meetingGroupsFacade.disbandGroup(groupOrganizerId, groupId);
    }
}