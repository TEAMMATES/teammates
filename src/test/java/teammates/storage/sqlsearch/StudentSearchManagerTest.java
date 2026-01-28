package teammates.storage.sqlsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link StudentSearchManager}.
 */
public class StudentSearchManagerTest extends BaseTestCase {

    private StudentSearchManager searchManager;
    private CoursesDb mockCoursesDb;
    private UsersDb mockUsersDb;

    @BeforeMethod
    public void setUp() {
        // Use mocked DBs to avoid CoursesDb.inst() / UsersDb.inst() before DB is ready in full suite
        mockCoursesDb = mock(CoursesDb.class);
        mockUsersDb = mock(UsersDb.class);
        when(mockCoursesDb.getCourse(anyString())).thenReturn(null);
        searchManager = new StudentSearchManager(null, mockCoursesDb, mockUsersDb, false);
    }

    @Test
    public void testGetCollectionName_returnsCorrectCollectionName() throws Exception {
        // Use reflection to test protected method
        Method method = StudentSearchManager.class.getDeclaredMethod("getCollectionName");
        method.setAccessible(true);
        String collectionName = (String) method.invoke(searchManager);
        assertEquals(collectionName, "students");
    }

    @Test
    public void testCreateDocument_createsCorrectDocumentType() throws Exception {
        Course course = createTestCourse();
        Student student = createTestStudent(course);

        // Use reflection to access protected method
        Method method = StudentSearchManager.class.getDeclaredMethod("createDocument", Student.class);
        method.setAccessible(true);
        StudentSearchDocument document = (StudentSearchDocument) method.invoke(searchManager, student);

        assertNotNull(document);
        // Verify it's the correct type by checking its fields
        var fields = document.getSearchableFields();
        assertEquals(fields.get("email"), student.getEmail());
        assertEquals(fields.get("courseId"), student.getCourseId());
    }

    @Test
    public void testSortResult_sortsCorrectly() throws Exception {
        Course course = createTestCourse();
        Student student1 = createTestStudent(course, "student1@example.com", "Student One", "Team A", "Section 1");
        Student student2 = createTestStudent(course, "student2@example.com", "Student Two", "Team B", "Section 1");
        Student student3 = createTestStudent(course, "student3@example.com", "Student Three", "Team A", "Section 2");

        List<Student> students = new ArrayList<>(Arrays.asList(student3, student1, student2));

        // Use reflection to access protected method
        Method method = StudentSearchManager.class.getDeclaredMethod("sortResult", List.class);
        method.setAccessible(true);
        method.invoke(searchManager, students);

        // Verify sorted by: courseId, section, team, name, email
        assertEquals(students.get(0), student1); // Section 1, Team A, Student One
        assertEquals(students.get(1), student2); // Section 1, Team B, Student Two
        assertEquals(students.get(2), student3); // Section 2, Team A, Student Three
    }

    @Test
    public void testGetBasicQuery_buildsQueryCorrectly() throws Exception {
        // Use reflection to access package-private method
        Method method = StudentSearchManager.class.getSuperclass()
                .getDeclaredMethod("getBasicQuery", String.class);
        method.setAccessible(true);

        String queryString = "test query";
        SolrQuery query = (SolrQuery) method.invoke(searchManager, queryString);

        assertNotNull(query);
        assertEquals((int) query.getStart(), 0);
        assertEquals((int) query.getRows(), Const.SEARCH_QUERY_SIZE_LIMIT);
    }

    @Test
    public void testSearchStudents_withMockedSolrClient_returnsResults() throws Exception {
        // Setup: Create mocks for Solr client and databases
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockStudentsDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockStudentsDb, false);

        // Create test data
        Course course = createTestCourse();
        Student student = createTestStudent(course, "student@example.com", "Test Student", "Team 1", "Section 1");

        // Create mock Solr response
        QueryResponse mockResponse = mock(QueryResponse.class);
        SolrDocumentList mockResults = new SolrDocumentList();
        SolrDocument mockDoc = new SolrDocument();
        mockDoc.addField("courseId", course.getId());
        mockDoc.addField("email", "student@example.com");
        mockResults.add(mockDoc);

        // Setup mock behavior
        when(mockClient.query(eq("students"), any(SolrQuery.class))).thenReturn(mockResponse);
        when(mockResponse.getResults()).thenReturn(mockResults);
        when(mockStudentsDb.getStudentForEmail(course.getId(), "student@example.com")).thenReturn(student);

        // Execute
        List<Student> results = managerWithMock.searchStudents("student", null);

        // Verify
        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), student);
        assertEquals(results.get(0).getEmail(), "student@example.com");
    }

    @Test
    public void testSearchStudents_withInstructorFilter_appliesFilter() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockStudentsDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockStudentsDb, false);

        Course course = createTestCourse();
        Student student = createTestStudent(course, "student@example.com", "Test Student", "Team 1", "Section 1");

        Instructor instructor = createTestInstructor(course);
        // Make instructor have view student privilege
        instructor.getPrivileges().getCourseLevelPrivileges().setCanViewStudentInSections(true);

        // Create mock Solr response
        QueryResponse mockResponse = mock(QueryResponse.class);
        SolrDocumentList mockResults = new SolrDocumentList();
        SolrDocument mockDoc = new SolrDocument();
        mockDoc.addField("courseId", course.getId());
        mockDoc.addField("email", "student@example.com");
        mockResults.add(mockDoc);

        // Setup mock behavior
        when(mockClient.query(eq("students"), any(SolrQuery.class))).thenReturn(mockResponse);
        when(mockResponse.getResults()).thenReturn(mockResults);
        when(mockStudentsDb.getStudentForEmail(course.getId(), "student@example.com")).thenReturn(student);

        // Execute with instructor filter
        List<Student> results = managerWithMock.searchStudents("student", Arrays.asList(instructor));

        // Verify: Filter query should be added
        verify(mockClient).query(eq("students"), any(SolrQuery.class));
        assertNotNull(results);
    }

    @Test
    public void testSearchStudents_withNoViewPrivilege_returnsEmpty() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockStudentsDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockStudentsDb, false);

        Course course = createTestCourse();
        Instructor instructor = createTestInstructor(course);
        // Instructor does NOT have view student privilege
        instructor.getPrivileges().getCourseLevelPrivileges().setCanViewStudentInSections(false);

        // Execute
        List<Student> results = managerWithMock.searchStudents("student", Arrays.asList(instructor));

        // Verify: Should return empty list without querying Solr
        assertNotNull(results);
        assertEquals(results.size(), 0);
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testPutDocument_withMockedClient_success() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockUsersDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockUsersDb, false);

        Course course = createTestCourse();
        Student student = createTestStudent(course);

        // Mock CoursesDb to return the course when getCourse is called
        when(mockCoursesDb.getCourse(course.getId())).thenReturn(course);

        // Execute
        managerWithMock.putDocument(student);

        // Verify: Capture the collection argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<SolrInputDocument>> collectionCaptor =
                ArgumentCaptor.forClass(Collection.class);
        verify(mockClient).add(eq("students"), collectionCaptor.capture());
        verify(mockClient).commit(eq("students"));

        // Verify the collection contains exactly one document
        Collection<SolrInputDocument> capturedCollection = collectionCaptor.getValue();
        assertNotNull(capturedCollection);
        assertEquals(capturedCollection.size(), 1);

        // Verify the document content
        SolrInputDocument document = capturedCollection.iterator().next();
        assertNotNull(document);
        assertEquals(document.getFieldValue("id"), student.getId());
        assertEquals(document.getFieldValue("courseId"), student.getCourseId());
        assertEquals(document.getFieldValue("email"), student.getEmail());
        assertNotNull(document.getFieldValue("_text_"));
    }

    @Test
    public void testDeleteDocuments_withMockedClient_success() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        StudentSearchManager managerWithMock = new StudentSearchManager(mockClient, false);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<String> keys = Arrays.asList(id1.toString(), id2.toString());

        // Execute
        managerWithMock.deleteDocuments(keys);

        // Verify: Method was called with correct collection name
        verify(mockClient).deleteById("students", keys);
        verify(mockClient).commit("students");
    }

    // Helper methods to create test entities
    private Course createTestCourse() {
        return new Course("test-course", "Test Course", "UTC", "Test Institute");
    }

    private Student createTestStudent(Course course) {
        return createTestStudent(course, "student@example.com", "Test Student", "Team 1", "Section 1");
    }

    private Student createTestStudent(Course course, String email, String name, String teamName, String sectionName) {
        Section section = new Section(course, sectionName);
        Team team = new Team(section, teamName);
        Student student = new Student(course, name, email, "Comments");
        student.setTeam(team);
        return student;
    }

    private Instructor createTestInstructor(Course course) {
        InstructorPrivileges privileges = new InstructorPrivileges();
        return new Instructor(course, "Instructor Name", "instructor@example.com",
                false, "Instructor", InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER, privileges);
    }
}
