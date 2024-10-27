package meeting.system.meetings.core;

import meeting.system.commons.persistance.BaseEntity;
import meeting.system.commons.persistance.InMemoryCrudRepository;
import meeting.system.commons.persistance.InMemoryLongIdGenerator;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface MeetingRepository extends CrudRepository<MeetingEntity, Long> {

    boolean existsByMeetingName(String groupMeetingName);

    List<MeetingEntity> findByMeetingGroupId(Long meetingGroupId);

    class InMemory extends InMemoryCrudRepository<MeetingEntity, Long> implements MeetingRepository {

        public InMemory() {
            super((BaseEntity::getId), BaseEntity::setId, new InMemoryLongIdGenerator());
        }

        @Override
        public boolean existsByMeetingName(String groupMeetingName) {
            return entities
                    .values()
                    .stream()
                    .anyMatch(meeting -> meeting.getMeetingName().equals(groupMeetingName));
        }

        @Override
        public List<MeetingEntity> findByMeetingGroupId(Long meetingGroupId) {
            return entities
                    .values()
                    .stream()
                    .filter(meeting -> meeting.getMeetingGroupId().id().equals(meetingGroupId))
                    .toList();
        }
    }
}