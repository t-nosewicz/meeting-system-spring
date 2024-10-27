package meeting.system.meeting.groups;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EntityScan
public class MeetingGroupsConfiguration {

    @Bean
    public MeetingGroupsFacade meetingGroupsFacade(ProposalRepository proposalRepository, GroupRepository groupRepository) {
        var proposalSubmitter = new ProposalSubmitter(proposalRepository, groupRepository);
        var meetingGroups = new MeetingGroupsFacadeImpl(proposalRepository, groupRepository, proposalSubmitter);
        return new MeetingGroupsLogs(meetingGroups);
    }

    public MeetingGroupsFacade inMemoryMeetingGroupsFacade() {
        return meetingGroupsFacade(new ProposalRepository.InMemory(), new GroupRepository.InMemory());
    }
}