package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCoursesPageData;

public class InstructorCoursesPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        ______TS("test no course");
        AccountAttributes instructorAccountWithourCourses = dataBundle.accounts.get("instructorWithoutCourses");
        InstructorCoursesPageData pageData = new InstructorCoursesPageData(instructorAccountWithourCourses);
        List<CourseDetailsBundle> activeCourses = new ArrayList<CourseDetailsBundle>();
        List<CourseDetailsBundle> archivedCourses = new ArrayList<CourseDetailsBundle>();
        Map<String, InstructorAttributes> instructorForCourses = new HashMap<String, InstructorAttributes>();
        pageData.init(activeCourses, archivedCourses, instructorForCourses);
        
        assertTrue(pageData.getActiveCourses() != null);
        assertTrue(pageData.getActiveCourses().getRows() != null);
        assertEquals(0, pageData.getActiveCourses().getRows().size());
        
        assertTrue(pageData.getArchivedCourses() != null);
        assertTrue(pageData.getArchivedCourses().getRows() != null);
        assertEquals(0, pageData.getArchivedCourses().getRows().size());
        
        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());
        
        ______TS("test 1 active course");
        AccountAttributes instructorAccountWithOneActiveCourse = dataBundle.accounts.get("instructor1OfCourse1");
        pageData = new InstructorCoursesPageData(instructorAccountWithOneActiveCourse);
        activeCourses = new ArrayList<CourseDetailsBundle>();
        activeCourses.add(new CourseDetailsBundle(dataBundle.courses.get("typicalCourse1")));
        
        archivedCourses = new ArrayList<CourseDetailsBundle>();
        instructorForCourses = new HashMap<String, InstructorAttributes>();
        instructorForCourses.put("idOfTypicalCourse1", dataBundle.instructors.get("instructor1OfCourse1"));
        pageData.init(activeCourses, archivedCourses, instructorForCourses);
        
        assertTrue(pageData.getActiveCourses() != null);
        assertTrue(pageData.getActiveCourses().getRows() != null);
        assertEquals(1, pageData.getActiveCourses().getRows().size());
        
        assertTrue(pageData.getArchivedCourses() != null);
        assertTrue(pageData.getArchivedCourses().getRows() != null);
        assertEquals(0, pageData.getArchivedCourses().getRows().size());
        
        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());
        
        ______TS("test 2 active courses");
        AccountAttributes instructorAccountWithTwoActiveCourses = dataBundle.accounts.get("instructor3");
        pageData = new InstructorCoursesPageData(instructorAccountWithTwoActiveCourses);
        activeCourses = new ArrayList<CourseDetailsBundle>();
        activeCourses.add(new CourseDetailsBundle(dataBundle.courses.get("typicalCourse1")));
        activeCourses.add(new CourseDetailsBundle(dataBundle.courses.get("typicalCourse2")));
        
        archivedCourses = new ArrayList<CourseDetailsBundle>();
        instructorForCourses = new HashMap<String, InstructorAttributes>();
        instructorForCourses.put("idOfTypicalCourse1", dataBundle.instructors.get("instructor3OfCourse1"));
        instructorForCourses.put("idOfTypicalCourse2", dataBundle.instructors.get("instructor3OfCourse2"));
        pageData.init(activeCourses, archivedCourses, instructorForCourses, "Id to show", "Name to show");
        
        assertTrue(pageData.getActiveCourses() != null);
        assertTrue(pageData.getActiveCourses().getRows() != null);
        assertEquals(2, pageData.getActiveCourses().getRows().size());
        
        assertTrue(pageData.getArchivedCourses() != null);
        assertTrue(pageData.getArchivedCourses().getRows() != null);
        assertEquals(0, pageData.getArchivedCourses().getRows().size());
        
        assertEquals("Id to show", pageData.getCourseIdToShow());
        assertEquals("Name to show", pageData.getCourseNameToShow());
        
        ______TS("test 1 archived course");
        AccountAttributes instructorAccountWithOneArchivedCourse = dataBundle.accounts.get("instructorOfArchivedCourse");
        pageData = new InstructorCoursesPageData(instructorAccountWithOneArchivedCourse);
        activeCourses = new ArrayList<CourseDetailsBundle>();
        
        archivedCourses = new ArrayList<CourseDetailsBundle>();
        archivedCourses.add(new CourseDetailsBundle(dataBundle.courses.get("archivedCourse")));
        
        instructorForCourses = new HashMap<String, InstructorAttributes>();
        instructorForCourses.put("idOfArchivedCourse", dataBundle.instructors.get("instructorOfArchivedCourse"));
        
        pageData.init(activeCourses, archivedCourses, instructorForCourses);
        
        assertTrue(pageData.getActiveCourses() != null);
        assertTrue(pageData.getActiveCourses().getRows() != null);
        assertEquals(0, pageData.getActiveCourses().getRows().size());
        
        assertTrue(pageData.getArchivedCourses() != null);
        assertTrue(pageData.getArchivedCourses().getRows() != null);
        assertEquals(1, pageData.getArchivedCourses().getRows().size());
        
        assertEquals("", pageData.getCourseIdToShow());
        assertEquals("", pageData.getCourseNameToShow());
        
    }
}
