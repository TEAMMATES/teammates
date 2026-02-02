package teammates.storage.sqlsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

    @BeforeMethod
    public void setUp() {
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockUsersDb = mock(UsersDb.class);
        when(mockCoursesDb.getCourse(anyString())).thenReturn(null);
        searchManager = new StudentSearchManager(null, mockCoursesDb, mockUsersDb, false);
    }

    @Test
    public void testGetCollectionName_returnsCorrectCollectionName() {
        String collectionName = searchManager.getCollectionName();
        assertEquals(collectionName, "students");
    }

    @Test
    public void testCreateDocument_createsCorrectDocumentType() {
        Course course = createTestCourse();
        Student student = createTestStudent(course);

        StudentSearchDocument document = searchManager.createDocument(student);

        assertNotNull(document);
        var fields = document.getSearchableFields();
        assertEquals(fields.get("email"), student.getEmail());
        assertEquals(fields.get("courseId"), student.getCourseId());
    }

    @Test
    public void testSortResult_sortsCorrectly() {
        Course course = createTestCourse();
        Student student1 = createTestStudent(course, "student1@example.com", "Student One", "Team A", "Section 1");
        Student student2 = createTestStudent(course, "student2@example.com", "Student Two", "Team B", "Section 1");
        Student student3 = createTestStudent(course, "student3@example.com", "Student Three", "Team A", "Section 2");

        List<Student> students = new ArrayList<>(Arrays.asList(student3, student1, student2));

        searchManager.sortResult(students);

        assertEquals(students.get(0), student1);
        assertEquals(students.get(1), student2);
        assertEquals(students.get(2), student3);
    }

    @Test
    public void testGetBasicQuery_buildsQueryCorrectly() {
        String queryString = "test query";
        SolrQuery query = searchManager.getBasicQuery(queryString);

        assertNotNull(query);
        assertEquals((int) query.getStart(), 0);
        assertEquals((int) query.getRows(), Const.SEARCH_QUERY_SIZE_LIMIT);
    }

    @Test
    public void testSearchStudents_withMockedSolrClient_returnsResults() throws Exception {
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockStudentsDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockStudentsDb, false);

        Course course = createTestCourse();
        Student student = createTestStudent(course, "student@example.com", "Test Student", "Team 1", "Section 1");

        QueryResponse mockResponse = mock(QueryResponse.class);
        SolrDocumentList mockResults = new SolrDocumentList();
        SolrDocument mockDoc = new SolrDocument();
        mockDoc.addField("courseId", course.getId());
        mockDoc.addField("email", "student@example.com");
        mockResults.add(mockDoc);

        when(mockClient.query(eq("students"), any(SolrQuery.class))).thenReturn(mockResponse);
        when(mockResponse.getResults()).thenReturn(mockResults);
        when(mockStudentsDb.getStudentForEmail(course.getId(), "student@example.com")).thenReturn(student);

        List<Student> results = managerWithMock.searchStudents("student", null);

        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), student);
    }

    @Test
    public void testSearchStudents_withInstructorFilter_appliesFilter() throws Exception {
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockStudentsDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockStudentsDb, false);

        Course course = createTestCourse();
        Student student = createTestStudent(course, "student@example.com", "Test Student", "Team 1", "Section 1");

        Instructor instructor = createTestInstructor(course);
        instructor.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);

        QueryResponse mockResponse = mock(QueryResponse.class);
        SolrDocumentList mockResults = new SolrDocumentList();
        SolrDocument mockDoc = new SolrDocument();
        mockDoc.addField("courseId", course.getId());
        mockDoc.addField("email", "student@example.com");
        mockResults.add(mockDoc);

        when(mockClient.query(eq("students"), any(SolrQuery.class))).thenReturn(mockResponse);
        when(mockResponse.getResults()).thenReturn(mockResults);
        when(mockStudentsDb.getStudentForEmail(course.getId(), "student@example.com")).thenReturn(student);

        List<Student> results = managerWithMock.searchStudents("student", Arrays.asList(instructor));

        verify(mockClient).query(eq("students"), any(SolrQuery.class));
        assertNotNull(results);
    }

    @Test
    public void testSearchStudents_withNoViewPrivilege_returnsEmpty() throws Exception {
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockStudentsDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockStudentsDb, false);

        Course course = createTestCourse();
        Instructor instructor = createTestInstructor(course);
        instructor.getPrivileges().getCourseLevelPrivileges().setCanViewStudentInSections(false);

        List<Student> results = managerWithMock.searchStudents("student", Arrays.asList(instructor));

        assertNotNull(results);
        assertEquals(results.size(), 0);
        verifyNoInteractions(mockClient);
    }

    @Test
    public void testPutDocument_withMockedClient_success() throws Exception {
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockUsersDb = mock(UsersDb.class);
        StudentSearchManager managerWithMock =
                new StudentSearchManager(mockClient, mockCoursesDb, mockUsersDb, false);

        Course course = createTestCourse();
        Student student = createTestStudent(course);

        when(mockCoursesDb.getCourse(course.getId())).thenReturn(course);

        managerWithMock.putDocument(student);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<SolrInputDocument>> collectionCaptor =
                ArgumentCaptor.forClass(Collection.class);
        verify(mockClient).add(eq("students"), collectionCaptor.capture());
        verify(mockClient).commit(eq("students"));

        Collection<SolrInputDocument> capturedCollection = collectionCaptor.getValue();
        assertNotNull(capturedCollection);
        assertEquals(capturedCollection.size(), 1);

        SolrInputDocument document = capturedCollection.iterator().next();
        assertNotNull(document);
        assertEquals(document.getFieldValue("id"), student.getId());
        assertEquals(document.getFieldValue("courseId"), student.getCourseId());
        assertEquals(document.getFieldValue("email"), student.getEmail());
        assertNotNull(document.getFieldValue("_text_"));
    }

    @Test
    public void testDeleteDocuments_withMockedClient_success() throws Exception {
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        StudentSearchManager managerWithMock = new StudentSearchManager(mockClient, false);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<String> keys = Arrays.asList(id1.toString(), id2.toString());

        managerWithMock.deleteDocuments(keys);

        verify(mockClient).deleteById("students", keys);
        verify(mockClient).commit("students");
    }

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
