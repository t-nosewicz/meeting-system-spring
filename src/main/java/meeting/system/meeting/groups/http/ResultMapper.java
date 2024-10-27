package meeting.system.meeting.groups.http;

import io.vavr.control.Either;
import io.vavr.control.Option;
import meeting.system.meeting.groups.dto.*;
import org.springframework.http.ResponseEntity;

class ResultMapper {

    ResponseEntity<Either<SubmitProposalResult.Failure, SubmitProposalResult.Success>> toResponseEntity(SubmitProposalResult result) {
        return switch (result) {
            case SubmitProposalResult.Failure failure -> ResponseEntity.status(200).body(Either.left(failure));
            case SubmitProposalResult.Success success -> ResponseEntity.status(200). body(Either.right(success));
        };
    }

    ResponseEntity<Option<RemoveProposalResult.Failure>> toResponseEntity(RemoveProposalResult result) {
        return switch (result) {
            case RemoveProposalResult.Failure failure -> ResponseEntity.status(200).body(Option.of(failure));
            case RemoveProposalResult.Success success -> ResponseEntity.status(200). body(Option.none());
        };
    }

    ResponseEntity<Either<AcceptProposalResult.Failure, AcceptProposalResult.Success>> toResponseEntity(AcceptProposalResult result) {
        return switch (result) {
            case AcceptProposalResult.Failure failure -> ResponseEntity.status(200).body(Either.left(failure));
            case AcceptProposalResult.Success success -> ResponseEntity.status(200). body(Either.right(success));
        };
    }

    ResponseEntity<Option<RejectProposalResult.Failure>> toResponseEntity(RejectProposalResult result) {
        return switch (result) {
            case RejectProposalResult.Failure failure -> ResponseEntity.status(200).body(Option.of(failure));
            case RejectProposalResult.Success success -> ResponseEntity.status(200). body(Option.none());
        };
    }

    ResponseEntity<Option<JoinGroupResult.Failure>> toResponseEntity(JoinGroupResult result) {
        return switch (result) {
            case JoinGroupResult.Failure failure -> ResponseEntity.status(200).body(Option.of(failure));
            case JoinGroupResult.Success success -> ResponseEntity.status(200). body(Option.none());
        };
    }

    ResponseEntity<Option<LeaveGroupResult.Failure>> toResponseEntity(LeaveGroupResult result) {
        return switch (result) {
            case LeaveGroupResult.Failure failure -> ResponseEntity.status(200).body(Option.of(failure));
            case LeaveGroupResult.Success success -> ResponseEntity.status(200). body(Option.none());
        };
    }
}