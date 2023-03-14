package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.test.BaseTestCase;

/**
 * SUT: {@code CoursesLogic}.
 */
public class CoursesLogicTest extends BaseTestCase {

    private CoursesLogic coursesLogic = CoursesLogic.inst();
    private FeedbackSessionsLogic fsLogic;
    private CoursesDb coursesDb;

    @BeforeMethod
    public void setUp() {
        coursesDb = mock(CoursesDb.class);
        fsLogic = mock(FeedbackSessionsLogic.class);
        coursesLogic.initLogicDependencies(coursesDb, fsLogic);
    }

    @Test
    public void testMoveCourseToRecycleBin_shouldReturnDeletedAt_success()
            throws EntityDoesNotExistException {
        Course course = generateTypicalCourse();
        String courseId = course.getId();

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        Instant deletedAt = coursesLogic.moveCourseToRecycleBin(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);
        assertNotNull(deletedAt);
    }

    @Test
    public void testMoveCourseToRecycleBin_courseDoesNotExist_throwEntityDoesNotExistException() {
        String courseId = generateTypicalCourse().getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.moveCourseToRecycleBin(courseId));

        assertEquals("Trying to move a non-existent course to recycling bin.", ex.getMessage());
    }

    @Test
    public void testRestoreCourseFromRecycleBin_shouldSetDeletedAtToNull_success()
            throws EntityDoesNotExistException {
        Course course = generateTypicalCourse();
        String courseId = course.getId();
        course.setDeletedAt(Instant.parse("2021-01-01T00:00:00Z"));

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        coursesLogic.restoreCourseFromRecycleBin(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);
        assertNull(course.getDeletedAt());
    }

    @Test
    public void testRestoreCourseFromRecycleBin_courseDoesNotExist_throwEntityDoesNotExistException() {
        String courseId = generateTypicalCourse().getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.restoreCourseFromRecycleBin(courseId));

        assertEquals("Trying to restore a non-existent course from recycling bin.", ex.getMessage());
    }

    @Test
    public void testGetSectionNamesForCourse_shouldReturnListOfSectionNames_success() throws EntityDoesNotExistException {
        Course course = generateTypicalCourse();
        String courseId = course.getId();
        course.setSections(generateTypicalSections());

        when(coursesDb.getCourse(courseId)).thenReturn(course);

        List<String> sectionNames = coursesLogic.getSectionNamesForCourse(courseId);

        verify(coursesDb, times(1)).getCourse(courseId);

        List<String> expectedSectionNames = List.of("test-sectionName1", "test-sectionName2");

        assertEquals(sectionNames, expectedSectionNames);
    }

    @Test
    public void testGetSectionNamesForCourse_courseDoesNotExist_throwEntityDoesNotExistException()
            throws EntityDoesNotExistException {
        String courseId = generateTypicalCourse().getId();

        when(coursesDb.getCourse(courseId)).thenReturn(null);

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> coursesLogic.getSectionNamesForCourse(courseId));

        assertEquals("Trying to get section names for a non-existent course.", ex.getMessage());
    }

    private Course generateTypicalCourse() {
        return new Course("test-courseId", "test-courseName", "test-courseTimeZone", "test-courseInstitute");
    }

    private List<Section> generateTypicalSections() {
        List<Section> sections = new ArrayList<>();

        sections.add(new Section(generateTypicalCourse(), "test-sectionName1"));
        sections.add(new Section(generateTypicalCourse(), "test-sectionName2"));

        return sections;
    }
}
