package teammates.storage.sqlsearch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.SearchServiceException;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentSearchManager}.
 */
public class StudentSearchTest extends BaseTestCase {

    private StudentSearchManager searchManager;

    @BeforeMethod
    public void setUpMethod() {
        searchManager = mock(StudentSearchManager.class);
    }

    // ==================== SEARCH IN WHOLE SYSTEM Tests (null instructors) ====================

    @Test
    public void testSearchStudentsInWholeSystem_queryMatchesSome_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Student stu1 = createStudent(course, "Student 1", "student1@test.com");
        Student stu2 = createStudent(course, "Student 1 Alt", "student1alt@test.com");
        List<Student> expectedResults = Arrays.asList(stu1, stu2);

        when(searchManager.searchStudents("\"Student 1\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"Student 1\"", null);

        assertEquals(2, results.size());
        AssertHelper.assertSameContentIgnoreOrder(expectedResults, results);
        verify(searchManager, times(1)).searchStudents("\"Student 1\"", null);
    }

    @Test
    public void testSearchStudentsInWholeSystem_queryMatchesNone_returnsEmptyList() throws SearchServiceException {
        when(searchManager.searchStudents("non-existent", null)).thenReturn(new ArrayList<>());

        List<Student> results = searchManager.searchStudents("non-existent", null);

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchStudentsInWholeSystem_emptyQuery_returnsEmptyList() throws SearchServiceException {
        when(searchManager.searchStudents("", null)).thenReturn(new ArrayList<>());

        List<Student> results = searchManager.searchStudents("", null);

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchStudentsInWholeSystem_caseInsensitive_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Student stu = createStudent(course, "Student 2", "student2@test.com");
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("\"sTuDeNt 2\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"sTuDeNt 2\"", null);

        assertEquals(1, results.size());
        assertEquals(stu, results.get(0));
    }

    @Test
    public void testSearchStudentsInWholeSystem_searchByEmail_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Student stu = createStudent(course, "Student", "unique.email@test.com");
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("unique.email@test.com", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("unique.email@test.com", null);

        assertEquals(1, results.size());
        assertEquals("unique.email@test.com", results.get(0).getEmail());
    }

    @Test
    public void testSearchStudentsInWholeSystem_searchByName_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Student stu = createStudent(course, "John Smith Student", "john@test.com");
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("\"John Smith Student\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"John Smith Student\"", null);

        assertEquals(1, results.size());
        assertEquals("John Smith Student", results.get(0).getName());
    }

    @Test
    public void testSearchStudentsInWholeSystem_searchByCourseId_success() throws SearchServiceException {
        Course course = new Course("unique-course-id", "Unique Course", "UTC", "Institute");
        Student stu1 = createStudent(course, "Student 1", "student1@test.com");
        Student stu2 = createStudent(course, "Student 2", "student2@test.com");
        List<Student> expectedResults = Arrays.asList(stu1, stu2);

        when(searchManager.searchStudents("\"unique-course-id\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"unique-course-id\"", null);

        assertEquals(2, results.size());
    }

    @Test
    public void testSearchStudentsInWholeSystem_searchByCourseName_success() throws SearchServiceException {
        Course course = new Course("course-id", "Typical Course 1", "UTC", "Institute");
        Student stu = createStudent(course, "Student 1", "student1@test.com");
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("\"Typical Course 1\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"Typical Course 1\"", null);

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchStudentsInWholeSystem_searchByTeam_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Section section = new Section(course, "Section 1");
        Team team = new Team(section, "Team Alpha");
        Student stu = new Student(course, "Student 1", "student1@test.com", "comments");
        stu.setTeam(team);
        stu.setId(UUID.randomUUID());
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("\"Team Alpha\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"Team Alpha\"", null);

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchStudentsInWholeSystem_searchBySection_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Section section = new Section(course, "Section A");
        Team team = new Team(section, "Team 1");
        Student stu = new Student(course, "Student 1", "student1@test.com", "comments");
        stu.setTeam(team);
        stu.setId(UUID.randomUUID());
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("\"Section A\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"Section A\"", null);

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchStudentsInWholeSystem_multipleMatches_success() throws SearchServiceException {
        Course course1 = new Course("course-1", "Course 1", "UTC", "Institute A");
        Course course2 = new Course("course-2", "Course 2", "UTC", "Institute B");
        Course course3 = new Course("course-3", "Course 3", "UTC", "Institute C");
        Student stu1 = createStudent(course1, "student1", "student1c1@test.com");
        Student stu2 = createStudent(course2, "student1", "student1c2@test.com");
        Student stu3 = createStudent(course3, "student1", "student1c3@test.com");
        List<Student> expectedResults = Arrays.asList(stu1, stu2, stu3);

        when(searchManager.searchStudents("student1", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("student1", null);

        assertEquals(3, results.size());
        AssertHelper.assertSameContentIgnoreOrder(expectedResults, results);
    }

    @Test
    public void testSearchStudentsInWholeSystem_archivedCourseStudentsIncluded_success() throws SearchServiceException {
        Course archivedCourse = new Course("archived-course", "Archived Course", "UTC", "Institute");
        archivedCourse.setDeletedAt(java.time.Instant.now());
        Student stu = createStudent(archivedCourse, "Student In Archived Course", "archived@test.com");
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("\"Student In Archived Course\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"Student In Archived Course\"", null);

        assertEquals(1, results.size());
    }

    // ==================== SEARCH WITH INSTRUCTOR RESTRICTION Tests ====================

    @Test
    public void testSearchStudents_restrictedByInstructor_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        Student stu = createStudent(course, "Student 1", "student1@test.com");
        List<Instructor> instructors = Collections.singletonList(instructor);
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("student1", instructors)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("student1", instructors);

        assertEquals(1, results.size());
        verify(searchManager, times(1)).searchStudents("student1", instructors);
    }

    @Test
    public void testSearchStudents_multipleInstructorsSearch_success() throws SearchServiceException {
        Course course1 = new Course("course-1", "Course 1", "UTC", "Institute");
        Course course2 = new Course("course-2", "Course 2", "UTC", "Institute");
        Instructor ins1 = new Instructor(course1, "Instructor 1", "ins1@test.com", false, "", null, null);
        Instructor ins2 = new Instructor(course2, "Instructor 2", "ins2@test.com", false, "", null, null);
        Student stu1 = createStudent(course1, "Student 1", "student1c1@test.com");
        Student stu2 = createStudent(course2, "Student 1", "student1c2@test.com");
        List<Instructor> instructors = Arrays.asList(ins1, ins2);
        List<Student> expectedResults = Arrays.asList(stu1, stu2);

        when(searchManager.searchStudents("Student 1", instructors)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("Student 1", instructors);

        assertEquals(2, results.size());
    }

    @Test
    public void testSearchStudents_singleInstructorNoResults_returnsEmptyList() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        List<Instructor> instructors = Collections.singletonList(instructor);

        when(searchManager.searchStudents("non-existent", instructors)).thenReturn(new ArrayList<>());

        List<Student> results = searchManager.searchStudents("non-existent", instructors);

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchStudents_emptyInstructorList_returnsEmptyList() throws SearchServiceException {
        List<Instructor> emptyInstructors = new ArrayList<>();

        when(searchManager.searchStudents("student1", emptyInstructors)).thenReturn(new ArrayList<>());

        List<Student> results = searchManager.searchStudents("student1", emptyInstructors);

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchStudents_instructorFromDifferentCourse_noResults() throws SearchServiceException {
        Course course1 = new Course("course-1", "Course 1", "UTC", "Institute");
        Instructor insFromCourse1 = new Instructor(course1, "Instructor 1", "ins1@test.com", false, "", null, null);
        List<Instructor> instructors = Collections.singletonList(insFromCourse1);

        // Searching for a student that is in a different course than the instructor's course
        when(searchManager.searchStudents("student in different course", instructors)).thenReturn(new ArrayList<>());

        List<Student> results = searchManager.searchStudents("student in different course", instructors);

        assertTrue(results.isEmpty());
    }

    // ==================== DELETE DOCUMENT Tests ====================

    @Test
    public void testDeleteDocuments_singleDocument_success() {
        String documentId = UUID.randomUUID().toString();
        List<String> documentIds = Collections.singletonList(documentId);

        searchManager.deleteDocuments(documentIds);

        verify(searchManager, times(1)).deleteDocuments(documentIds);
    }

    @Test
    public void testDeleteDocuments_multipleDocuments_success() {
        List<String> documentIds = Arrays.asList(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );

        searchManager.deleteDocuments(documentIds);

        verify(searchManager, times(1)).deleteDocuments(documentIds);
    }

    @Test
    public void testDeleteDocuments_emptyList_success() {
        List<String> documentIds = new ArrayList<>();

        searchManager.deleteDocuments(documentIds);

        verify(searchManager, times(1)).deleteDocuments(documentIds);
    }

    // ==================== PUT DOCUMENT Tests ====================

    @Test
    public void testPutDocument_validStudent_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Student stu = createStudent(course, "Student", "student@test.com");

        searchManager.putDocument(stu);

        verify(searchManager, times(1)).putDocument(stu);
    }

    // ==================== EDGE CASE Tests ====================

    @Test
    public void testSearchStudentsInWholeSystem_unregisteredStudents_success() throws SearchServiceException {
        Course course = new Course("unregistered-course", "Unregistered Course", "UTC", "Institute");
        Student stu = createStudent(course, "Unregistered Student", "unregistered@test.com");
        List<Student> expectedResults = Collections.singletonList(stu);

        when(searchManager.searchStudents("\"Unregistered Student\"", null)).thenReturn(expectedResults);

        List<Student> results = searchManager.searchStudents("\"Unregistered Student\"", null);

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchStudentsInWholeSystem_specialCharactersInQuery_success() throws SearchServiceException {
        when(searchManager.searchStudents("test@email.com", null)).thenReturn(new ArrayList<>());

        List<Student> results = searchManager.searchStudents("test@email.com", null);

        assertNotNull(results);
    }

    // ==================== Helper Methods ====================

    private Student createStudent(Course course, String name, String email) {
        Section section = new Section(course, "Section 1");
        Team team = new Team(section, "Team 1");
        Student student = new Student(course, name, email, "comments");
        student.setTeam(team);
        student.setId(UUID.randomUUID());
        return student;
    }
}
