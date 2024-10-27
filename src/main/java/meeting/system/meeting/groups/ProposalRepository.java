package meeting.system.meeting.groups;

import io.vavr.control.Option;
import meeting.system.commons.persistance.InMemoryCrudRepository;
import meeting.system.commons.persistance.InMemoryLongIdGenerator;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

interface ProposalRepository extends CrudRepository<ProposalEntity, Long> {
    boolean existsByGroupName(String groupName);

    Collection<ProposalEntity> findByProposalAuthorId(Long proposalAuthorId);

    Option<ProposalEntity> findByMeetingGroupId(Long id);

    class InMemory extends InMemoryCrudRepository<ProposalEntity, Long> implements ProposalRepository {

        InMemory() {
            super((ProposalEntity::getId), (ProposalEntity::setId), new InMemoryLongIdGenerator());
        }

        @Override
        public boolean existsByGroupName(String groupName) {
            return entities
                    .values()
                    .stream()
                    .anyMatch(proposal -> proposal.getGroupName().equals(groupName));
        }

        @Override
        public Collection<ProposalEntity> findByProposalAuthorId(Long proposalAuthorId) {
            return entities
                    .values()
                    .stream()
                    .filter(proposalEntity -> proposalEntity.isAuthor(proposalAuthorId))
                    .toList();
        }

        @Override
        public Option<ProposalEntity> findByMeetingGroupId(Long id) {
            return Option.ofOptional(entities
                    .values().stream()
                    .filter(proposalEntity -> proposalEntity.groupIdEqualsTo(id))
                    .findAny());
        }
    }
}