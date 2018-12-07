package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorCoursesPageData;

/**
 * SUT: {@link InstructorCoursesPageData}.
 */
public class InstructorCoursesPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();

    @Test
    public void testAll() {
        ______TS("test no course");
        AccountAttributes instructorAccountWithoutCourses = dataBundle.accounts.get("instructorWithoutCourses");
        InstructorCoursesPageData pageData =
                new InstructorCoursesPageData(instructorAccountWithoutCourses, dummySessionToken);
        List<CourseAttributes> activeCourses = new ArrayList<>();
        List<CourseAttributes> archivedCourses = new ArrayList<>();
        List<CourseAttributes> softDeletedCourses = new ArrayList<>();
        Map<String, InstructorAttributes> instructorForCourses = new HashMap<>();
        pageData.init(activeCourses, archivedCourses, softDeletedCourses, instructorForCourses);

        assertNotNull(pageData.getActiveCourses());
        assertNotNull(pageData.getActiveCourses().getRows());
        assertEquals(0, pageData.getActiveCourses().getRows().size());

        assertNotNull(pageData.getArchivedCourses());
        assertNotNull(pageData.getArchivedCourses().getRows());
        assertEquals(0, pageData.getArchivedCourses().getRows().size());

        assertNotNull(pageData.getSoftDeletedCourses());
        assertNotNull(pageData.getSoftDeletedCourses().getRows());
        assertEquals(0, pageData.getSoftDeletedCourses().getRows().size());

        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());

        assertTrue(pageData.isInstructorAllowedToModify());

        ______TS("test 1 active course");
        AccountAttributes instructorAccountWithOneActiveCourse = dataBundle.accounts.get("instructor1OfCourse1");
        pageData = new InstructorCoursesPageData(instructorAccountWithOneActiveCourse, dummySessionToken);
        activeCourses = new ArrayList<>();
        activeCourses.add(dataBundle.courses.get("typicalCourse1"));

        archivedCourses = new ArrayList<>();
        instructorForCourses = new HashMap<>();
        instructorForCourses.put("idOfTypicalCourse1", dataBundle.instructors.get("instructor1OfCourse1"));
        pageData.init(activeCourses, archivedCourses, softDeletedCourses, instructorForCourses);

        assertNotNull(pageData.getActiveCourses());
        assertNotNull(pageData.getActiveCourses().getRows());
        assertEquals(1, pageData.getActiveCourses().getRows().size());

        assertNotNull(pageData.getArchivedCourses());
        assertNotNull(pageData.getArchivedCourses().getRows());
        assertEquals(0, pageData.getArchivedCourses().getRows().size());

        assertNotNull(pageData.getSoftDeletedCourses());
        assertNotNull(pageData.getSoftDeletedCourses().getRows());
        assertEquals(0, pageData.getSoftDeletedCourses().getRows().size());

        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());

        assertTrue(pageData.isInstructorAllowedToModify());

        ______TS("test 2 active courses");
        AccountAttributes instructorAccountWithTwoActiveCourses = dataBundle.accounts.get("instructor3");
        pageData = new InstructorCoursesPageData(instructorAccountWithTwoActiveCourses, dummySessionToken);
        activeCourses = new ArrayList<>();
        activeCourses.add(dataBundle.courses.get("typicalCourse1"));
        activeCourses.add(dataBundle.courses.get("typicalCourse2"));

        archivedCourses = new ArrayList<>();
        instructorForCourses = new HashMap<>();
        instructorForCourses.put("idOfTypicalCourse1", dataBundle.instructors.get("instructor3OfCourse1"));
        instructorForCourses.put("idOfTypicalCourse2", dataBundle.instructors.get("instructor3OfCourse2"));
        pageData.init(activeCourses, archivedCourses, softDeletedCourses, instructorForCourses,
                "Id to show", "Name to show");

        assertNotNull(pageData.getActiveCourses());
        assertNotNull(pageData.getActiveCourses().getRows());
        assertEquals(2, pageData.getActiveCourses().getRows().size());

        assertNotNull(pageData.getArchivedCourses());
        assertNotNull(pageData.getArchivedCourses().getRows());
        assertEquals(0, pageData.getArchivedCourses().getRows().size());

        assertNotNull(pageData.getSoftDeletedCourses());
        assertNotNull(pageData.getSoftDeletedCourses().getRows());
        assertEquals(0, pageData.getSoftDeletedCourses().getRows().size());

        assertEquals("Id to show", pageData.getCourseIdToShow());
        assertEquals("Name to show", pageData.getCourseNameToShow());

        assertTrue(pageData.isInstructorAllowedToModify());

        ______TS("test 1 archived course");
        AccountAttributes instructorAccountWithOneArchivedCourse = dataBundle.accounts.get("instructorOfArchivedCourse");
        pageData = new InstructorCoursesPageData(instructorAccountWithOneArchivedCourse, dummySessionToken);
        activeCourses = new ArrayList<>();

        archivedCourses = new ArrayList<>();
        archivedCourses.add(dataBundle.courses.get("archivedCourse"));

        instructorForCourses = new HashMap<>();
        instructorForCourses.put("idOfArchivedCourse", dataBundle.instructors.get("instructorOfArchivedCourse"));

        pageData.init(activeCourses, archivedCourses, softDeletedCourses, instructorForCourses);

        assertNotNull(pageData.getActiveCourses());
        assertNotNull(pageData.getActiveCourses().getRows());
        assertEquals(0, pageData.getActiveCourses().getRows().size());

        assertNotNull(pageData.getArchivedCourses());
        assertNotNull(pageData.getArchivedCourses().getRows());
        assertEquals(1, pageData.getArchivedCourses().getRows().size());

        assertNotNull(pageData.getSoftDeletedCourses());
        assertNotNull(pageData.getSoftDeletedCourses().getRows());
        assertEquals(0, pageData.getSoftDeletedCourses().getRows().size());

        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());

        assertTrue(pageData.isInstructorAllowedToModify());

        ______TS("test 1 deleted course in Recycle Bin");
        AccountAttributes instructorAccountWithOneDeletedCourse = dataBundle.accounts.get("instructor2OfCourse3");
        pageData = new InstructorCoursesPageData(instructorAccountWithOneDeletedCourse, dummySessionToken);

        activeCourses = new ArrayList<>();
        archivedCourses = new ArrayList<>();
        softDeletedCourses.add(dataBundle.courses.get("typicalCourse3"));

        instructorForCourses = new HashMap<>();
        instructorForCourses.put("idOfTypicalCourse3", dataBundle.instructors.get("instructor2OfCourse3"));

        pageData.init(activeCourses, archivedCourses, softDeletedCourses, instructorForCourses);

        assertNotNull(pageData.getActiveCourses());
        assertNotNull(pageData.getActiveCourses().getRows());
        assertEquals(0, pageData.getActiveCourses().getRows().size());

        assertNotNull(pageData.getArchivedCourses());
        assertNotNull(pageData.getArchivedCourses().getRows());
        assertEquals(0, pageData.getArchivedCourses().getRows().size());

        assertNotNull(pageData.getSoftDeletedCourses());
        assertNotNull(pageData.getSoftDeletedCourses().getRows());
        assertEquals(1, pageData.getSoftDeletedCourses().getRows().size());

        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());

        assertFalse(pageData.isInstructorAllowedToModify());

    }
}
