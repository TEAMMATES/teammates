package teammates.lnp.scripts.setup;

import teammates.lnp.scripts.create.config.CreateStudentProfileTestConfigData;
import teammates.lnp.scripts.create.data.CreateStudentProfileTestData;

/**
 *  Sets up the Student Profile performance test by generating the relevant data and creating entities in the datastore.
 */
public final class SetupStudentProfileTest extends SetupTest {

    private SetupStudentProfileTest() {
        // Intentional private constructor to prevent external instantiation
    }

    public static void main(String[] args) {
        SetupTest setup = new SetupStudentProfileTest();
        setup.setupTestData(new CreateStudentProfileTestData(), new CreateStudentProfileTestConfigData());
    }

}
