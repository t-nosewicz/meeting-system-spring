package meeting.system.meetings.waiting.list;

import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.core.MeetingsCoreFacade;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EntityScan
public class WaitingListConfiguration {

    @Bean
    public WaitingListFacade meetingsWaitingListFacade(WaitingListRepository waitingListRepository,
                                                       MeetingsCoreFacade meetingsCoreFacade,
                                                       MeetingGroupsRoles meetingGroupsRoles) {
        return new WaitingListLogs(new WaitingListFacadeImpl(waitingListRepository, meetingsCoreFacade, meetingGroupsRoles));
    }

    public WaitingListFacade inMemoryMeetingsWaitingListFacade(MeetingsCoreFacade meetingsCoreFacade, MeetingGroupsRoles meetingGroupsRoles) {
        return meetingsWaitingListFacade(new WaitingListRepository.InMemory(), meetingsCoreFacade, meetingGroupsRoles);
    }
}