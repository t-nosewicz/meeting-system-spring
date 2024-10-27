package meeting.system.user.funds;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsersFundsConfiguration {

    @Bean
    public UsersFundsFacade usersFundsFacade() {
        return new LogsDecorator(new UsersFundsFacadeImpl());
    }
}