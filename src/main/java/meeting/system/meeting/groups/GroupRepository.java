package meeting.system.meeting.groups;

import meeting.system.commons.dto.UserId;
import meeting.system.commons.persistance.InMemoryCrudRepository;
import meeting.system.commons.persistance.InMemoryLongIdGenerator;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

interface GroupRepository extends CrudRepository<GroupEntity, Long> {
    boolean existsByGroupName(String groupName);

    Collection<GroupEntity> findByGroupOrganizerId(Long groupOrganizerId);

    class InMemory extends InMemoryCrudRepository<GroupEntity, Long> implements GroupRepository {

        InMemory() {
            super((GroupEntity::getId), (GroupEntity::setId), new InMemoryLongIdGenerator());
        }

        @Override
        public boolean existsByGroupName(String groupName) {
            return entities
                    .values()
                    .stream()
                    .anyMatch(meetingGroup -> meetingGroup.getGroupName().equals(groupName));
        }

        @Override
        public Collection<GroupEntity> findByGroupOrganizerId(Long groupOrganizerId) {
            return entities
                    .values()
                    .stream()
                    .filter(meetingGroup -> meetingGroup.userIsGroupOrganizer(new UserId(groupOrganizerId)))
                    .toList();
        }
    }
}