package meeting.system.notifications;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationsConfiguration {

    @Bean
    public NotificationsFacade meetingsNotificationsFacade() {
        return new NotificationsFacade();
    }
}