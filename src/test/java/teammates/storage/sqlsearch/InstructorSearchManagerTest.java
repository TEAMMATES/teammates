package teammates.storage.sqlsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

import teammates.common.util.Const;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link InstructorSearchManager}.
 */
public class InstructorSearchManagerTest extends BaseTestCase {

    private InstructorSearchManager searchManager;

    @BeforeMethod
    public void setUp() {
        // Create manager with empty host (no Solr client) for testing methods that don't need Solr
        searchManager = new InstructorSearchManager("", false);
    }

    @Test
    public void testGetCollectionName_returnsCorrectCollectionName() throws Exception {
        // Use reflection to test protected method
        Method method = InstructorSearchManager.class.getDeclaredMethod("getCollectionName");
        method.setAccessible(true);
        String collectionName = (String) method.invoke(searchManager);
        assertEquals(collectionName, "instructors");
    }

    @Test
    public void testCreateDocument_createsCorrectDocumentType() throws Exception {
        Course course = createTestCourse();
        Instructor instructor = createTestInstructor(course);

        // Use reflection to access protected method
        Method method = InstructorSearchManager.class.getDeclaredMethod("createDocument", Instructor.class);
        method.setAccessible(true);
        InstructorSearchDocument document = (InstructorSearchDocument) method.invoke(searchManager, instructor);

        assertNotNull(document);
        // Verify it's the correct type by checking its fields
        var fields = document.getSearchableFields();
        assertEquals(fields.get("email"), instructor.getEmail());
        assertEquals(fields.get("courseId"), instructor.getCourseId());
    }

    @Test
    public void testSortResult_sortsCorrectly() throws Exception {
        Course course = createTestCourse();
        Instructor instructor1 = createTestInstructor(course, "inst1@example.com", "Instructor One");
        Instructor instructor2 = createTestInstructor(course, "inst2@example.com", "Instructor Two");
        Instructor instructor3 = createTestInstructor(course, "inst3@example.com", "Instructor Three");

        List<Instructor> instructors = new ArrayList<>(Arrays.asList(instructor3, instructor1, instructor2));

        // Use reflection to access protected method
        Method method = InstructorSearchManager.class.getDeclaredMethod("sortResult", List.class);
        method.setAccessible(true);
        method.invoke(searchManager, instructors);

        // Verify sorted by: courseId, role, name, email
        assertEquals(instructors.get(0), instructor1);
        assertEquals(instructors.get(1), instructor2);
        assertEquals(instructors.get(2), instructor3);
    }

    @Test
    public void testGetBasicQuery_buildsQueryCorrectly() throws Exception {
        // Use reflection to access package-private method on base class
        Method method = InstructorSearchManager.class.getSuperclass()
                .getDeclaredMethod("getBasicQuery", String.class);
        method.setAccessible(true);

        String queryString = "test query";
        SolrQuery query = (SolrQuery) method.invoke(searchManager, queryString);

        assertNotNull(query);
        assertEquals((int) query.getStart(), 0);
        assertEquals((int) query.getRows(), Const.SEARCH_QUERY_SIZE_LIMIT);
    }

    @Test
    public void testSearchInstructors_withMockedSolrClient_returnsResults() throws Exception {
        // Setup: Create mocks for Solr client and databases
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockUsersDb = mock(UsersDb.class);
        InstructorSearchManager managerWithMock =
                new InstructorSearchManager(mockClient, mockCoursesDb, mockUsersDb, false);

        // Create test data
        Course course = createTestCourse();
        Instructor instructor = createTestInstructor(course);

        // Create mock Solr response
        QueryResponse mockResponse = mock(QueryResponse.class);
        SolrDocumentList mockResults = new SolrDocumentList();
        SolrDocument mockDoc = new SolrDocument();
        mockDoc.addField("courseId", course.getId());
        mockDoc.addField("email", instructor.getEmail());
        mockResults.add(mockDoc);

        // Setup mock behavior
        when(mockClient.query(eq("instructors"), any(SolrQuery.class))).thenReturn(mockResponse);
        when(mockResponse.getResults()).thenReturn(mockResults);
        when(mockUsersDb.getInstructorForEmail(course.getId(), instructor.getEmail())).thenReturn(instructor);

        // Execute
        List<Instructor> results = managerWithMock.searchInstructors("instructor");

        // Verify
        assertNotNull(results);
        assertEquals(results.size(), 1);
        assertEquals(results.get(0), instructor);
        assertEquals(results.get(0).getEmail(), instructor.getEmail());
    }

    @Test
    public void testPutDocument_withMockedClient_success() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        CoursesDb mockCoursesDb = mock(CoursesDb.class);
        UsersDb mockUsersDb = mock(UsersDb.class);
        InstructorSearchManager managerWithMock =
                new InstructorSearchManager(mockClient, mockCoursesDb, mockUsersDb, false);

        Course course = createTestCourse();
        Instructor instructor = createTestInstructor(course);

        // Mock CoursesDb to return the course when getCourse is called
        when(mockCoursesDb.getCourse(course.getId())).thenReturn(course);

        // Execute
        managerWithMock.putDocument(instructor);

        // Verify: Capture the collection argument
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<SolrInputDocument>> collectionCaptor =
                ArgumentCaptor.forClass(Collection.class);
        verify(mockClient).add(eq("instructors"), collectionCaptor.capture());
        verify(mockClient).commit(eq("instructors"));

        // Verify the collection contains exactly one document
        Collection<SolrInputDocument> capturedCollection = collectionCaptor.getValue();
        assertNotNull(capturedCollection);
        assertEquals(capturedCollection.size(), 1);

        // Verify the document content
        SolrInputDocument document = capturedCollection.iterator().next();
        assertNotNull(document);
        assertEquals(document.getFieldValue("id"), instructor.getId());
        assertEquals(document.getFieldValue("courseId"), instructor.getCourseId());
        assertEquals(document.getFieldValue("email"), instructor.getEmail());
        assertNotNull(document.getFieldValue("_text_"));
    }

    @Test
    public void testDeleteDocuments_withMockedClient_success() throws Exception {
        // Setup
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        InstructorSearchManager managerWithMock = new InstructorSearchManager(mockClient, false);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<String> keys = Arrays.asList(id1.toString(), id2.toString());

        // Execute
        managerWithMock.deleteDocuments(keys);

        // Verify: Method was called with correct collection name
        verify(mockClient).deleteById("instructors", keys);
        verify(mockClient).commit("instructors");
    }

    @Test
    public void testDeleteDocuments_withEmptyList_doesNothing() throws Exception {
        HttpSolrClient mockClient = mock(HttpSolrClient.class);
        InstructorSearchManager managerWithMock = new InstructorSearchManager(mockClient, false);

        managerWithMock.deleteDocuments(new ArrayList<>());

        verify(mockClient, never()).deleteById(anyString(), any(List.class));
        verify(mockClient, never()).commit(anyString());
    }

    // Helper methods to create test entities
    private Course createTestCourse() {
        return new Course("test-course", "Test Course", "UTC", "Test Institute");
    }

    private Instructor createTestInstructor(Course course) {
        return createTestInstructor(course, "instructor@example.com", "Test Instructor");
    }

    private Instructor createTestInstructor(Course course, String email, String name) {
        return new Instructor(course, name, email, false, "Display " + name,
                teammates.common.datatransfer.InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                new teammates.common.datatransfer.InstructorPrivileges());
    }
}

