package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.Institute;
import teammates.test.GroupNames;
import teammates.test.scenariobuilder.GivenData.AccountVerificationRequestRef;
import teammates.test.scenariobuilder.GivenData.CourseRef;
import teammates.test.scenariobuilder.GivenData.InstituteRef;

/**
 * Tests for {@link InstitutesDb}.
 */
public class InstitutesDbTest extends BaseDbTestcase {
    InstitutesDb institutesDb = InstitutesDb.inst();

    @Test(groups = GroupNames.DB)
    public void getInstituteByNameAndCountry_matchExists_returnsInstitute() {
        InstituteRef institute = given.institute("institute", i -> {
            i.name("Test University");
            i.country("SG");
        });
        persistGivenData(given);

        Institute actual = inTransaction(() ->
                institutesDb.getInstituteByNameAndCountry("Test University", "SG"));

        assertNotNull(actual);
        assertEquals(institute.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getInstituteByNameAndCountry_differentCountry_returnsNull() {
        given.institute("institute", i -> {
            i.name("Test University");
            i.country("SG");
        });
        persistGivenData(given);

        Institute actual = inTransaction(() ->
                institutesDb.getInstituteByNameAndCountry("Test University", "US"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void removeInstitute_instituteHasCoursesAndAccountVerificationRequests_cascadeDeletes() {
        InstituteRef institute = given.institute("institute");
        CourseRef course = given.course("course", c -> c.institute(institute.alias()));
        AccountVerificationRequestRef accountVerificationRequest = given.accountVerificationRequest("account-request", a -> a.institute(institute.alias()));
        // An unrelated institute and course that must survive the deletion.
        InstituteRef otherInstitute = given.institute("other-institute");
        CourseRef otherCourse = given.course("other-course", c -> c.institute(otherInstitute.alias()));
        persistGivenData(given);

        inTransaction(() -> institutesDb.removeInstitute(institutesDb.getInstitute(institute.id())));

        verifyAbsentInDatabase(Institute.class, institute.id());
        verifyAbsentInDatabase(Course.class, course.id());
        verifyAbsentInDatabase(AccountVerificationRequest.class, accountVerificationRequest.id());

        verifyPresentInDatabase(Institute.class, otherInstitute.id());
        verifyPresentInDatabase(Course.class, otherCourse.id());
    }
}
