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
import teammates.storage.sqlsearch.InstructorSearchManager;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link InstructorSearchManager}.
 */
public class InstructorSearchTest extends BaseTestCase {

    private InstructorSearchManager searchManager;

    @BeforeMethod
    public void setUpMethod() {
        searchManager = mock(InstructorSearchManager.class);
    }

    // ==================== SEARCH Tests ====================

    @Test
    public void testSearchInstructors_queryMatchesSome_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor ins1 = createInstructor(course, "Instructor 1", "instr1@test.com");
        Instructor ins2 = createInstructor(course, "Instructor 1 Alt", "instr1alt@test.com");
        List<Instructor> expectedResults = Arrays.asList(ins1, ins2);

        when(searchManager.searchInstructors("\"Instructor 1\"")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("\"Instructor 1\"");

        assertEquals(2, results.size());
        AssertHelper.assertSameContentIgnoreOrder(expectedResults, results);
        verify(searchManager, times(1)).searchInstructors("\"Instructor 1\"");
    }

    @Test
    public void testSearchInstructors_queryMatchesNone_returnsEmptyList() throws SearchServiceException {
        when(searchManager.searchInstructors("non-existent")).thenReturn(new ArrayList<>());

        List<Instructor> results = searchManager.searchInstructors("non-existent");

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchInstructors_emptyQuery_returnsEmptyList() throws SearchServiceException {
        when(searchManager.searchInstructors("")).thenReturn(new ArrayList<>());

        List<Instructor> results = searchManager.searchInstructors("");

        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchInstructors_caseInsensitive_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor ins = createInstructor(course, "Instructor 2", "instr2@test.com");
        List<Instructor> expectedResults = Collections.singletonList(ins);

        when(searchManager.searchInstructors("\"InStRuCtOr 2\"")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("\"InStRuCtOr 2\"");

        assertEquals(1, results.size());
        assertEquals(ins, results.get(0));
    }

    @Test
    public void testSearchInstructors_searchByEmail_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor ins = createInstructor(course, "Instructor", "unique.email@test.com");
        List<Instructor> expectedResults = Collections.singletonList(ins);

        when(searchManager.searchInstructors("unique.email@test.com")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("unique.email@test.com");

        assertEquals(1, results.size());
        assertEquals("unique.email@test.com", results.get(0).getEmail());
    }

    @Test
    public void testSearchInstructors_searchByName_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor ins = createInstructor(course, "Professor John Smith", "john@test.com");
        List<Instructor> expectedResults = Collections.singletonList(ins);

        when(searchManager.searchInstructors("\"Professor John Smith\"")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("\"Professor John Smith\"");

        assertEquals(1, results.size());
        assertEquals("Professor John Smith", results.get(0).getName());
    }

    @Test
    public void testSearchInstructors_searchByCourseId_success() throws SearchServiceException {
        Course course = new Course("unique-course-id", "Unique Course", "UTC", "Institute");
        Instructor ins1 = createInstructor(course, "Instructor 1", "instr1@test.com");
        Instructor ins2 = createInstructor(course, "Instructor 2", "instr2@test.com");
        List<Instructor> expectedResults = Arrays.asList(ins1, ins2);

        when(searchManager.searchInstructors("\"unique-course-id\"")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("\"unique-course-id\"");

        assertEquals(2, results.size());
    }

    @Test
    public void testSearchInstructors_searchByCourseName_success() throws SearchServiceException {
        Course course = new Course("course-id", "Advanced Data Structures", "UTC", "Institute");
        Instructor ins = createInstructor(course, "Instructor 1", "instr1@test.com");
        List<Instructor> expectedResults = Collections.singletonList(ins);

        when(searchManager.searchInstructors("\"Advanced Data Structures\"")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("\"Advanced Data Structures\"");

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchInstructors_searchByRole_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor ins = createInstructor(course, "Instructor", "instr@test.com");
        ins.setRole(teammates.common.datatransfer.InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        List<Instructor> expectedResults = Collections.singletonList(ins);

        when(searchManager.searchInstructors("Co-owner")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("Co-owner");

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchInstructors_searchByDisplayedName_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor ins = createInstructor(course, "Instructor", "instr@test.com");
        ins.setDisplayName("Assistant Professor");
        List<Instructor> expectedResults = Collections.singletonList(ins);

        when(searchManager.searchInstructors("\"Assistant Professor\"")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("\"Assistant Professor\"");

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchInstructors_multipleMatches_success() throws SearchServiceException {
        Course course1 = new Course("course-1", "Course 1", "UTC", "Institute A");
        Course course2 = new Course("course-2", "Course 2", "UTC", "Institute B");
        Instructor ins1 = createInstructor(course1, "Instructor 1 Course1", "instr1c1@test.com");
        Instructor ins2 = createInstructor(course2, "Instructor 1 Course2", "instr1c2@test.com");
        List<Instructor> expectedResults = Arrays.asList(ins1, ins2);

        when(searchManager.searchInstructors("Instructor 1")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("Instructor 1");

        assertEquals(2, results.size());
        AssertHelper.assertSameContentIgnoreOrder(expectedResults, results);
    }

    @Test
    public void testSearchInstructors_archivedCourseInstructorsIncluded_success() throws SearchServiceException {
        Course archivedCourse = new Course("archived-course", "Archived Course", "UTC", "Institute");
        archivedCourse.setDeletedAt(java.time.Instant.now());
        Instructor ins = createInstructor(archivedCourse, "Instructor Of Archived", "archived@test.com");
        List<Instructor> expectedResults = Collections.singletonList(ins);

        when(searchManager.searchInstructors("\"Instructor Of Archived\"")).thenReturn(expectedResults);

        List<Instructor> results = searchManager.searchInstructors("\"Instructor Of Archived\"");

        assertEquals(1, results.size());
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
    public void testPutDocument_validInstructor_success() throws SearchServiceException {
        Course course = getTypicalCourse();
        Instructor ins = createInstructor(course, "Instructor", "instr@test.com");

        searchManager.putDocument(ins);

        verify(searchManager, times(1)).putDocument(ins);
    }

    // ==================== Helper Methods ====================

    private Instructor createInstructor(Course course, String name, String email) {
        Instructor instructor = new Instructor(course, name, email, false, "", null, null);
        instructor.setId(UUID.randomUUID());
        return instructor;
    }
}
