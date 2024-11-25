# Introduction

The main goal of this repository is to present a modular approach to software design.

### What is a module?
- it is a part of the system with explicit boundaries, responsible for a process or group of closely related processes
- interaction with the module is allowed only through its public interface(no access to the internal implementation details: database tables, entities, repositories, submodules, etc.)
- every module has its own layers (web, domain, database access)

![modularity diagram](https://github.com/user-attachments/assets/a5cc9ee7-b7b0-4ba6-8320-2d98caef4ed4)

### What are the benefits of modularity?
If done correctly, it can improve maintainability of the system:
- code coupling is decreased, because components interact with each other only through the public interfaces (we can change the internals of the module without breaking the collaborators)
- it is easier to test different parts of the system in isolation thanks to the explicit boundaries

### What about the cons?
It can be difficult to create complex views or reports because the database tables are encapsulated, and we can't(or at least shouldn't) run Sql statements across the entire database schema.

# Domain description
Meeting system allows users to organize groups and schedule meetings for the group members.

To organize a group, users have to submit a proposal, that will be reviewed by an administrator. Every user can have at most 3 active groups. A group organizer can disband the group, unless it has scheduled meetings.

Group members can schedule meetings that other members can attend. A meeting should have specific name, date and number of spots. Meeting sign-on may require a fee. If there are no free spots, users should have the ability to sign on a waiting list. A meeting organiser can cancel the meeting before its date. If the meeting is cancelled, the meeting attendees should be notified about the meeting cancellation. If sign-on required a fee, they should also get their money back.

### Main processes and rules
group organizing process:
- a user has to submit a new group proposal
- proposal has to be reviewed by an administrator
- the administrator can either accept or reject the proposal
- a name of the group has to be unique including group names in the unreviewed proposals and the names of existing groups
- if proposal gets accepted by the administrator, a group is created, the proposal author becomes a group organizer and a group member
- the proposal authors can remove their unreviewed proposals
- any given user can have at most 3 groups and unreviewed proposals combined(e.g. max 3 unreviewed proposals, or max 3 groups, or 1 unreviewed proposal and 2 groups,  etc.)

group joining:
- users can join an unlimited amount of groups
- after joining a group, user becomes a regular group member
- group organizers cannot join their own groups

group leaving:
- regular group members can leave the group at any time
- group organizers cannot leave their own groups

group disbanding:
- only the group organizer can disband the group
- the group cannot be disbanded if there are meetings scheduled for that group

meeting scheduling:
- only the group members can schedule the meetings(both regular group members and group organizers)
- a group member that scheduled a meeting is a meeting organizer
- the meeting must have specified amount of spots, date, title
- the meetings may require a fee for a meeting sign-on

meeting sign-on:
- all group members, except meeting organizer, can sign on for a group meeting
- if scheduled meeting requires a fee, a user should be charged for the meeting sign-on
- if the user doesn't have enough funds on his account, the meeting sign-on should fail
- the amount of signed on users cannot exceed the limit specified by the meeting organizer

meeting sign-off:
- user can sign off from the meeting only before the meeting date
- if the meeting sign-on required a fee, the funds should be returned to the user that signed off from the meeting

meeting cancellation:
- a meeting can be cancelled only by a meeting organizer, only before a meeting date
- if cancelled meeting sign-on required a fee, the funds should be returned to the users that were signed on
- users, that were signed on for the meeting, should be notified about the meeting cancellation
- users from the waiting list should also be notified

waiting list:
- if scheduled meeting doesn't have any free spots, users can sign on a waiting list before the meeting date
- if some user signs off from the meeting, then someone from the waiting list should be signed on
- a user who has been signed on from the waiting list should be notified


# System design

### Users' roles and interactions with the system

![users roles and their interactions with the system](https://github.com/user-attachments/assets/43c39c7e-6b83-4807-b447-3b199ef38916)

### Segregating processes into the modules
Processes that appear to be closely related should be put together:

![users and modules](https://github.com/user-attachments/assets/c10c706a-f6cf-4f21-a5cf-352595374449)

### Interactions between the modules
Now we should describe interactions between the components:

![3 1interactions between modules](https://github.com/user-attachments/assets/2dc92cb9-cd8a-44b7-ba97-c82d38abe0db)

Things that should bring our attention:
- circular dependencies
- too much communication between the modules

'meetings' and 'waiting list': both issues are present. These two components should be wrapped with a parent module that restricts direct access to them and is responsible for their collaboration.

![3 2 interactions between modules](https://github.com/user-attachments/assets/5607545f-1260-4dcc-8bfb-758d71a6fb87)

'groups' and 'meetings': there is not much communication but there is a circular dependency between them. We can create a separate component that will be responsible for orchestrating the group disbanding process.

![3 3 interactions between modules](https://github.com/user-attachments/assets/74d7b249-3dbf-478a-aa60-98b81aca2f7d)

'groups' and 'group proposals': no circular dependency but too much communication. We can merge them. Later on the unit tests will show if that was a good decision(if unit tests of a module are too complicated, it usually means that the module is too big, and it should be split).

![3 4 interactions between modules](https://github.com/user-attachments/assets/e5e7ea7e-2ab3-47bc-a524-ee41760e495d)

### Final system architecture:

![4 final system diagram](https://github.com/user-attachments/assets/badaba79-f18e-427c-80e6-03c7bc28718b)

# Implementation

### Technology stack:
- Java 21
- Spring Boot 3 (web, data-jpa, test, h2)
- Archunit

### Package structure

Application design from the final diagram is mapped to the following Java package structure:

![modules mapped to java packages](https://github.com/user-attachments/assets/643e50a6-234f-4580-8538-45aa61ecb0a5)

Structure of the sample module: 'meeting groups'

![meeting groups module package structure](https://github.com/user-attachments/assets/f6735167-510c-4981-bc4d-efa18b30b0b9)

As we can see, the only public classes in the module are:
- 'MeetingGroupsFacade' interface containing all the commands
- 'MeetingGroupsConfiguration' class that creates the instance of the facade
- 'MeetingGroupsRoles' separate interface used by other modules
- classes accepted and returned by the facade in 'dto' package
- controllers in 'http' package (have to be public for integration testing purposes)

### Database access
Domain layer of the module accesses the database through the repositories. Implementation is provided by extending the interfaces from Spring Data Jpa.

```java
interface GroupRepository extends CrudRepository<GroupEntity, Long> {

    Collection<GroupEntity> findByGroupOrganizerId(Long groupOrganizerId);
```

# Testing

Every module has its own test package.

![test package structure](https://github.com/user-attachments/assets/9f3d6d93-e2c1-42cf-aad2-3d9132c6122e)

Tests are split into 3 categories:
- unit tests
- integration tests
- architecture tests (ArchUnit library)

### Unit testing
Modules are unit tested in isolation from other modules, web layer and database.

#### How to isolate module from other modules?
Use mocks and stubs as references to other modules.

#### How to isolate module from the web layer?
Use the facade of the module instead of http calls.

#### How to isolate modules from the database?
Create a generic implementation of the CrudRepository interface from Spring Data Jpa that stores entities in a hash map.

```java
@RequiredArgsConstructor
public class InMemoryCrudRepository<E, ID> implements CrudRepository<E, ID> {
    protected final Map<ID, E> entities = new HashMap<>();
    protected final Function<E, ID> idGetter;
    protected final BiConsumer<E, ID> idSetter;
    protected final Supplier<ID> idGenerator;

    @Override
    public <S extends E> S save(S entity) {
        if (geId(entity) == null)
            setId(entity, idGenerator.get());
        entities.put(geId(entity), entity);
        return entity;
    }
...
```

In a specific repository interface add the specific in-memory implementation.

```java
interface GroupRepository extends CrudRepository<GroupEntity, Long> {

    Collection<GroupEntity> findByGroupOrganizerId(Long groupOrganizerId);

    class InMemory extends InMemoryCrudRepository<GroupEntity, Long> implements GroupRepository {

        InMemory() {
            super((GroupEntity::getId), (GroupEntity::setId), new InMemoryLongIdGenerator());
        }

        @Override
        public Collection<GroupEntity> findByGroupOrganizerId(Long groupOrganizerId) {
            return entities
                    .values()
                    .stream()
                    .filter(meetingGroup -> meetingGroup.getGroupOrganizerId().equals(groupOrganizerId))
                    .toList();
        }
    }
}
```

Configuration class of the module should have two methods:
- @Bean method that creates the module with a real database
- 'inMemory...' method that creates module for unit testing

```java
@Configuration
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
```

### Integration testing
Integration tests of a module are done in isolation from the other modules, but they include all the layers(web, domain, database). Integration tests check only few main scenarios.

### Architecture tests
Architecture tests verify that architectural and dependency rules that cannot be checked at compile time are followed, e.g. child modules in 'meetings' component should only be accessed by their parent module.

'meetings' package structure:

![meetings module package structure](https://github.com/user-attachments/assets/3bcdeaae-face-4eea-a587-c5d7deed4944)

Tests:

```java

public class MeetingsArchitectureTest extends ArchitectureTestBase {

    @Test
    @DisplayName("'meetings core' submodule should only be used by 'meetings' module and 'waiting list' submodule")
    public void test1() {
        theClass(MeetingsCoreFacade.class)
                .should().onlyBeAccessed()
                .byClassesThat().resideInAnyPackage(
                        "meeting.system.meetings.core",
                        "meeting.system.meetings",
                        "meeting.system.meetings.waiting.list")
                .check(javaClasses);
    }

    @Test
    @DisplayName("'waiting list' submodule should only be used by 'meetings' module")
    public void test2() {
        theClass(WaitingListFacade.class)
                .should().onlyBeAccessed()
                .byClassesThat().resideInAnyPackage(
                        "meeting.system.meetings",
                        "meeting.system.meetings.waiting.list")
                .check(javaClasses);
    }
}
```

# Limitations
- no frontend
- no authorization/authentication
- naive database implementation (h2)
- the process of depositing money by users is not implemented
