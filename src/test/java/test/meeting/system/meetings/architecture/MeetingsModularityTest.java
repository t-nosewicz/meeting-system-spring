package test.meeting.system.meetings.architecture;

import meeting.system.meetings.core.MeetingsCoreFacade;
import meeting.system.meetings.waiting.list.WaitingListFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import test.meeting.system.test.utils.ArchitectureTestBase;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.theClass;

public class MeetingsModularityTest extends ArchitectureTestBase {

    @Test
    @DisplayName("'meetings core' submodule should only be used by 'meetings' parent module and 'waiting list' submodule")
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
    @DisplayName("'waiting list' submodule should only be used by 'meetings' parent module")
    public void test2() {
        theClass(WaitingListFacade.class)
                .should().onlyBeAccessed()
                .byClassesThat().resideInAnyPackage(
                        "meeting.system.meetings",
                        "meeting.system.meetings.waiting.list")
                .check(javaClasses);
    }
}