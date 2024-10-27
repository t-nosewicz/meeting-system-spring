package meeting.system.group.disbanding.http;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.system.commons.dto.MeetingGroupId;
import meeting.system.group.disbanding.GroupDisbandingFacade;
import meeting.system.commons.logged.user.LoggedUserFacade;
import meeting.system.meeting.groups.dto.DisbandGroupResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class GroupDisbandingController {
    private final GroupDisbandingFacade groupsDisbandingFacade;
    private final LoggedUserFacade loggedUserFacade;

    @PostMapping("/groups/disbanding/{groupId}")
    ResponseEntity<Option<DisbandGroupResult.Failure>> disbandGroup(@PathVariable Long groupId) {
        return loggedUserFacade
                .getLoggedUserId()
                .map(userId -> groupsDisbandingFacade.disbandGroup(userId, new MeetingGroupId(groupId)))
                .map(this::toResponse)
                .getOrElse(new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<Option<DisbandGroupResult.Failure>> toResponse(DisbandGroupResult result) {
        return switch (result) {
            case DisbandGroupResult.Failure failure -> ResponseEntity.status(200).body(Option.of(failure));
            case DisbandGroupResult.Success success -> ResponseEntity.status(200). body(Option.none());
        };
    }
}