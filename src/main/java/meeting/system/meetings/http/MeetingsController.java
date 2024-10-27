package meeting.system.meetings.http;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.commons.logged.user.LoggedUserFacade;
import meeting.system.meetings.MeetingsFacade;
import meeting.system.meetings.core.dto.*;
import meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure;
import meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@AllArgsConstructor
@RestController
@RequestMapping("/meetings")
public class MeetingsController {
    private final LoggedUserFacade loggedUser;
    private final MeetingsFacade meetingsFacade;
    private final ResultMapper resultMapper = new ResultMapper();

    @PostMapping("/scheduling")
    ResponseEntity<Either<ScheduleMeetingFailure, GroupMeetingId>> scheduleNewMeeting(@RequestBody MeetingDraft meetingDraft) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingsFacade.scheduleNewMeeting(userId, meetingDraft))
                .map(resultMapper::scheduleMeetingResult)
                .getOrElse(new ResponseEntity<>(FORBIDDEN));
    }

    @PostMapping("/holding/{meetingId}")
    ResponseEntity<Option<HoldMeetingFailure>> holdMeeting(@PathVariable Long meetingId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingsFacade.holdMeeting(userId, new GroupMeetingId(meetingId)))
                .map(resultMapper::holdMeetingResult)
                .getOrElse(new ResponseEntity<>(FORBIDDEN));
    }

    @PostMapping("/cancelling/{meetingId}")
    ResponseEntity<Option<CancelMeetingFailure>> cancelMeeting(@PathVariable Long meetingId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingsFacade.cancelMeeting(userId, new GroupMeetingId(meetingId)))
                .map(resultMapper::cancelMeetingResult)
                .getOrElse(new ResponseEntity<>(FORBIDDEN));
    }

    @PostMapping("/sign-on/{meetingId}")
    ResponseEntity<Option<SignOnForMeetingFailure>> signOnForMeeting(@PathVariable Long meetingId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingsFacade.signOnForMeeting(userId, new GroupMeetingId(meetingId)))
                .map(resultMapper::signOnForMeetingResult)
                .getOrElse(new ResponseEntity<>(FORBIDDEN));
    }

    @PostMapping("/sign-off/{meetingId}")
    ResponseEntity<Option<SignOffFromMeetingFailure>> signOffFromMeeting(@PathVariable Long meetingId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingsFacade.signOffFromMeeting(userId, new GroupMeetingId(meetingId)))
                .map(resultMapper::signOffFromMeetingResult)
                .getOrElse(new ResponseEntity<>(FORBIDDEN));
    }

    @PostMapping("/waiting-list/sign-on/{meetingId}")
    ResponseEntity<Option<SignOnWaitListFailure>> signOnWaitingList(@PathVariable Long meetingId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingsFacade.signOnWaitingList(userId, new GroupMeetingId(meetingId)))
                .map(resultMapper::signOnWaitingListResult)
                .getOrElse(new ResponseEntity<>(FORBIDDEN));
    }

    @PostMapping("/waiting-list/sign-off/{meetingId}")
    ResponseEntity<Option<SignOffFromWaitListFailure>> signOffFromWaitingList(@PathVariable Long meetingId) {
        return loggedUser
                .getLoggedUserId()
                .map(userId -> meetingsFacade.signOffFromWaitingList(userId, new GroupMeetingId(meetingId)))
                .map(resultMapper::signOffFromWaitingListResult)
                .getOrElse(new ResponseEntity<>(FORBIDDEN));
    }

    @GetMapping("/{meetingId}")
    ResponseEntity<Option<MeetingDetails>> findMeetingDetails(@PathVariable Long meetingId) {
        return meetingsFacade
                .findMeetingDetails(new GroupMeetingId(meetingId))
                .map(meetingDetails -> ResponseEntity.status(OK).body(Option.of(meetingDetails)))
                .getOrElse(ResponseEntity.status(OK).body(Option.none()));
    }
}