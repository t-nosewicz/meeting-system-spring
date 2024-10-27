package meeting.system.meetings;

import meeting.system.meetings.core.MeetingsCoreFacade;
import meeting.system.meetings.waiting.list.WaitingListFacade;
import meeting.system.notifications.NotificationsFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeetingsConfiguration {

    @Bean
    public MeetingsFacade meetingsFacade(MeetingsCoreFacade meetingsCoreFacade,
                                         WaitingListFacade waitingListFacade,
                                         NotificationsFacade notificationsFacade) {
        return new MeetingsFacade(meetingsCoreFacade, waitingListFacade, notificationsFacade);
    }
}