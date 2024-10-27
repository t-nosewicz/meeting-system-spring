package meeting.system.meetings.http;

import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.system.commons.dto.GroupMeetingId;
import meeting.system.meetings.core.dto.*;
import meeting.system.meetings.waiting.list.dto.SignOffFromWaitListFailure;
import meeting.system.meetings.waiting.list.dto.SignOnWaitListFailure;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.OK;

class ResultMapper {

    ResponseEntity<Either<ScheduleMeetingFailure, GroupMeetingId>> scheduleMeetingResult(Either<ScheduleMeetingFailure, GroupMeetingId> scheduleMeetingResult) {
        return scheduleMeetingResult
                .fold(
                        failure -> ResponseEntity.status(OK).body(Either.left(failure)),
                        meetingId -> ResponseEntity.status(OK).body(Either.right(meetingId)));
    }

    ResponseEntity<Option<HoldMeetingFailure>> holdMeetingResult(Option<HoldMeetingFailure> holdMeetingResult) {
        return holdMeetingResult
                .map(failure -> ResponseEntity.status(OK).body(Option.of(failure)))
                .getOrElse(ResponseEntity.status(OK).body(Option.none()));
    }

    ResponseEntity<Option<CancelMeetingFailure>> cancelMeetingResult(Option<CancelMeetingFailure> cancelMeetingFailure) {
        return cancelMeetingFailure
                .map(failure -> ResponseEntity.status(OK).body(Option.of(failure)))
                .getOrElse(ResponseEntity.status(OK).body(Option.none()));
    }

    ResponseEntity<Option<SignOnForMeetingFailure>> signOnForMeetingResult(Option<SignOnForMeetingFailure> cancelMeetingFailure) {
        return cancelMeetingFailure
                .map(failure -> ResponseEntity.status(OK).body(Option.of(failure)))
                .getOrElse(ResponseEntity.status(OK).body(Option.none()));
    }

    ResponseEntity<Option<SignOffFromMeetingFailure>> signOffFromMeetingResult(Option<SignOffFromMeetingFailure> cancelMeetingFailure) {
        return cancelMeetingFailure
                .map(failure -> ResponseEntity.status(OK).body(Option.of(failure)))
                .getOrElse(ResponseEntity.status(OK).body(Option.none()));
    }

    ResponseEntity<Option<SignOnWaitListFailure>> signOnWaitingListResult(Option<SignOnWaitListFailure> cancelMeetingFailure) {
        return cancelMeetingFailure
                .map(failure -> ResponseEntity.status(OK).body(Option.of(failure)))
                .getOrElse(ResponseEntity.status(OK).body(Option.none()));
    }

    ResponseEntity<Option<SignOffFromWaitListFailure>> signOffFromWaitingListResult(Option<SignOffFromWaitListFailure> cancelMeetingFailure) {
        return cancelMeetingFailure
                .map(failure -> ResponseEntity.status(OK).body(Option.of(failure)))
                .getOrElse(ResponseEntity.status(OK).body(Option.none()));
    }
}