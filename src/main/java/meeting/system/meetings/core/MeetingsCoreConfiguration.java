package meeting.system.meetings.core;


import meeting.system.meeting.groups.MeetingGroupsRoles;
import meeting.system.meetings.core.ports.Calendar;
import meeting.system.user.funds.UsersFundsFacade;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.Clock;
import java.time.LocalDate;

@Configuration
@EnableJpaRepositories
@EntityScan
public class MeetingsCoreConfiguration {

    @Bean
    public MeetingsCoreFacade meetingsCoreFacade(MeetingRepository meetingRepository, Calendar calendar, MeetingGroupsRoles meetingGroupsRoles, UsersFundsFacade usersFunds) {
        var meetingsScheduler = new MeetingsScheduler(meetingRepository, meetingGroupsRoles, calendar);
        return new MeetingsCoreLogs(new MeetingsCoreFacadeImpl(meetingRepository, meetingsScheduler, meetingGroupsRoles, usersFunds, calendar));
    }

    @Bean
    public Calendar calendar() {
        Clock clock = Clock.systemDefaultZone();
        return () -> LocalDate.now(clock);
    }

    public MeetingsCoreFacade inMemoryMeetingsCoreFacade(Calendar calendar, MeetingGroupsRoles meetingGroupsRoles, UsersFundsFacade usersFunds) {
        return meetingsCoreFacade(new MeetingRepository.InMemory(), calendar, meetingGroupsRoles, usersFunds);
    }
}