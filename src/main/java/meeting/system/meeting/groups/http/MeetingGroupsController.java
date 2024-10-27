package meeting.system.meeting.groups.http;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.commons.logged.user.LoggedUserFacade;
import meeting.system.meeting.groups.MeetingGroupsFacade;
import meeting.system.meeting.groups.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MeetingGroupsController {
    private final MeetingGroupsFacade meetingGroups;
    private final LoggedUserFacade loggedUser;
    private final ResultMapper resultMapper = new ResultMapper();

    @PostMapping("/groups/proposals/submission")
    ResponseEntity<Either<SubmitProposalResult.Failure, SubmitProposalResult.Success>> submitGroupProposal(@RequestBody NewGroupProposal newGroupProposal) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingGroups.submitNewGroupProposal(userId, newGroupProposal))
                .map(resultMapper::toResponseEntity)
                .getOrElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }



    @DeleteMapping("/groups/proposals/{proposalId}")
    ResponseEntity<Option<RemoveProposalResult.Failure>> removeWaitingProposal(@PathVariable Long proposalId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingGroups.removeWaitingProposal(userId, new GroupProposalId(proposalId)))
                .map(resultMapper::toResponseEntity)
                .getOrElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @PostMapping("/groups/proposals/acceptance/{proposalId}")
    ResponseEntity<Either<AcceptProposalResult.Failure, AcceptProposalResult.Success>> acceptProposal(@PathVariable Long proposalId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingGroups.acceptProposal(userId, new GroupProposalId(proposalId)))
                .map(resultMapper::toResponseEntity)
                .getOrElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @PostMapping("/groups/proposals/rejection/{proposalId}")
    ResponseEntity<Option<RejectProposalResult.Failure>> rejectProposal(@PathVariable Long proposalId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingGroups.rejectProposal(userId, new GroupProposalId(proposalId)))
                .map(resultMapper::toResponseEntity)
                .getOrElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }



    @PostMapping("/groups/joining/{groupId}")
    ResponseEntity<Option<JoinGroupResult.Failure>> joinGroup(@PathVariable Long groupId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingGroups.joinGroup(userId, new MeetingGroupId(groupId)))
                .map(resultMapper::toResponseEntity)
                .getOrElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    @PostMapping("/groups/leaving/{groupId}")
    ResponseEntity<Option<LeaveGroupResult.Failure>> leaveGroup(@PathVariable Long groupId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingGroups.leaveGroup(userId, new MeetingGroupId(groupId)))
                .map(resultMapper::toResponseEntity)
                .getOrElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }
}