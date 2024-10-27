package meeting.system.notifications;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import meeting.system.commons.dto.UserId;

import java.time.LocalDate;
import java.util.Collection;

@AllArgsConstructor
@Slf4j
public class NotificationsFacade {

    public void notifyAboutBeingSignedOnFromWaitingList(UserId userId, String meetingName, LocalDate meetingDate) {
        log.info("notifying user {} about being signed on for a meeting from a waiting list, meeting tile: {}, date {}", userId.id(), meetingName, meetingDate);
    }

    public void notifyAboutMeetingCancellation(String meetingName, LocalDate meetingDate, Collection<UserId> attendees) {
        attendees.forEach(attendeeId -> notifyAboutMeetingCancellation(attendeeId, meetingName, meetingDate));
    }

    private void notifyAboutMeetingCancellation(UserId userId, String meetingName, LocalDate meetingDate) {
        log.info("notifying user {} about meeting cancellation, meeting tile: {}, date: {}", userId.id(), meetingName, meetingDate);
    }
}