package meeting.system.meetings.waiting.list;

import io.vavr.control.Option;
import meeting.system.commons.persistance.BaseEntity;
import meeting.system.commons.persistance.InMemoryCrudRepository;
import meeting.system.commons.persistance.InMemoryLongIdGenerator;
import org.springframework.data.repository.CrudRepository;

interface WaitingListRepository extends CrudRepository<WaitingListEntity, Long> {

    Option<WaitingListEntity> findByMeetingId(Long groupMeetingId);

    boolean existsByMeetingId(Long groupMeetingId);

    void deleteByMeetingId(Long groupMeetingId);

    class InMemory extends InMemoryCrudRepository<WaitingListEntity, Long> implements WaitingListRepository {

        public InMemory() {
            super(BaseEntity::getId, BaseEntity::setId, new InMemoryLongIdGenerator());
        }

        @Override
        public Option<WaitingListEntity> findByMeetingId(Long meetingId) {
            return Option.ofOptional(
                    entities
                            .values()
                            .stream()
                            .filter(waitingListEntity -> waitingListEntity.meetingIdEquals(meetingId))
                            .findFirst());
        }

        @Override
        public boolean existsByMeetingId(Long groupMeetingId) {
            return entities
                    .values()
                    .stream()
                    .anyMatch(waitingListEntity -> waitingListEntity.getMeetingId().id().equals(groupMeetingId));
        }

        @Override
        public void deleteByMeetingId(Long meetingId) {
            entities
                    .values()
                    .stream()
                    .filter(waitingListEntity -> waitingListEntity.meetingIdEquals(meetingId))
                    .findFirst()
                    .map(waitingListEntity -> entities.remove(waitingListEntity.getId()));
        }
    }
}