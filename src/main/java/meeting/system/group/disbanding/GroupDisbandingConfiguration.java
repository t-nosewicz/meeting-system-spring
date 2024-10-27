package meeting.system.group.disbanding;

import meeting.system.meeting.groups.MeetingGroupsFacade;
import meeting.system.meetings.MeetingsFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupDisbandingConfiguration {

    @Bean
    public GroupDisbandingFacade meetingGroupsDisbandingFacade(
            MeetingGroupsFacade meetingGroupsFacade,
            MeetingsFacade meetingsQueryFacade) {
        return new GroupDisbandingLogs(new GroupDisbandingFacadeImpl(meetingGroupsFacade, meetingsQueryFacade));
    }
}