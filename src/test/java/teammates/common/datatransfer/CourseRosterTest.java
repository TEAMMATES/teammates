package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link CourseRoster}.
 */
public class CourseRosterTest extends BaseTestCase {

    @Test
    public void allTests() {

        ______TS("No students");

        CourseRoster roster = new CourseRoster(null, null);
        assertFalse(roster.isStudentInCourse("studentEmail"));

        ______TS("only 1 student, no instructors");

        roster = new CourseRoster(createStudentList("team 1", "s1@gmail.com"), null);
        assertFalse(roster.isStudentInCourse("non-existent@gmail.com"));
        assertTrue(roster.isStudentInCourse("s1@gmail.com"));

        assertFalse(roster.isStudentInTeam("non-existent@gmail.com", "team 1"));
        assertFalse(roster.isStudentInTeam("s1@gmail.com", "team 123"));
        assertTrue(roster.isStudentInTeam("s1@gmail.com", "team 1"));

        assertFalse(roster.isStudentsInSameTeam("non-existent@gmail.com", "s1@gmail.com"));
        assertFalse(roster.isStudentsInSameTeam("s1@gmail.com", "non-existent@gmail.com"));
        assertTrue(roster.isStudentsInSameTeam("s1@gmail.com", "s1@gmail.com"));

        assertEquals(roster.getStudentForEmail("s1@gmail.com").getEmail(), "s1@gmail.com");
        assertEquals(roster.getStudentForEmail("s1@gmail.com").getTeam(), "team 1");
        assertNull(roster.getInstructorForEmail("ins@email.com"));

        ______TS("only 1 instructor, no students");

        roster = new CourseRoster(null, createInstructorList("John", "ins1@email.com"));
        assertEquals(roster.getInstructorForEmail("ins1@email.com").getEmail(), "ins1@email.com");
        assertEquals(roster.getInstructorForEmail("ins1@email.com").getName(), "John");

        assertNull(roster.getInstructorForEmail("non-existent@email.com"));

        ______TS("multiple students, multiple instructors");

        roster = new CourseRoster(createStudentList("team 1", "s1@gmail.com",
                                                        "team 1", "s2@gmail.com",
                                                        "team 2", "s3@gmail.com"),
                                   createInstructorList("John", "ins1@email.com",
                                                          "Jean", "ins2@email.com"));

        assertFalse(roster.isStudentInCourse("non-existent@gmail.com"));
        assertTrue(roster.isStudentInCourse("s2@gmail.com"));

        assertFalse(roster.isStudentInTeam("non-existent@gmail.com", "team 1"));
        assertFalse(roster.isStudentInTeam("s3@gmail.com", "team 1"));
        assertTrue(roster.isStudentInTeam("s1@gmail.com", "team 1"));
        assertTrue(roster.isStudentInTeam("s2@gmail.com", "team 1"));
        assertTrue(roster.isStudentInTeam("s3@gmail.com", "team 2"));

        assertFalse(roster.isStudentsInSameTeam("non-existent@gmail.com", "s1@gmail.com"));
        assertFalse(roster.isStudentsInSameTeam("s1@gmail.com", "s3@gmail.com"));
        assertTrue(roster.isStudentsInSameTeam("s2@gmail.com", "s1@gmail.com"));

        assertEquals(roster.getInstructorForEmail("ins1@email.com").getEmail(), "ins1@email.com");
        assertEquals(roster.getInstructorForEmail("ins1@email.com").getName(), "John");
        assertEquals(roster.getInstructorForEmail("ins2@email.com").getEmail(), "ins2@email.com");
        assertEquals(roster.getInstructorForEmail("ins2@email.com").getName(), "Jean");

    }

    @Test
    public void testBuildTeamToMembersTable_emptyStudentList_shouldReturnsEmptyMap() {
        Map<String, List<StudentAttributes>> teamToMembersTable =
                CourseRoster.buildTeamToMembersTable(Collections.emptyList());
        assertEquals(0, teamToMembersTable.size());
    }

    @Test
    public void testBuildTeamToMembersTable_typicalStudentList_shouldBuildMap() {
        List<StudentAttributes> students = createStudentList(
                "team 1", "s1@gmail.com",
                "team 1", "s2@gmail.com",
                "team 2", "s3@gmail.com");
        Map<String, List<StudentAttributes>> teamToMembersTable = CourseRoster.buildTeamToMembersTable(students);
        assertEquals(2, teamToMembersTable.size());
        assertEquals(2, teamToMembersTable.get("team 1").size());
        assertEquals(1, teamToMembersTable.get("team 2").size());
        assertEquals("s3@gmail.com", teamToMembersTable.get("team 2").iterator().next().getEmail());
    }

    @Test
    public void testGetTeamToMembersTable_typicalCase_shouldGroupTeamCorrectly() {
        CourseRoster roster = new CourseRoster(
                createStudentList(
                        "team 1", "s1@gmail.com",
                        "team 1", "s2@gmail.com",
                        "team 2", "s3@gmail.com"),
                createInstructorList(
                        "John", "ins1@email.com",
                        "Jean", "ins2@email.com"));

        assertEquals(2, roster.getTeamToMembersTable().size());
        assertEquals(2, roster.getTeamToMembersTable().get("team 1").size());
        assertEquals(1, roster.getTeamToMembersTable().get("team 2").size());
        assertEquals("s3@gmail.com", roster.getTeamToMembersTable().get("team 2").iterator().next().getEmail());
    }

    @Test
    public void testGetInfoForIdentifier_studentCase_shouldShowCorrectInfo() {
        CourseRoster roster = new CourseRoster(
                createStudentList(
                        "John Doe", "john@gmail.com",
                        "s2", "s2@gmail.com",
                        "s3", "s3@gmail.com"),
                createInstructorList(
                        "John", "john@email.com",
                        "Jean", "ins2@email.com"));
        CourseRoster.ParticipantInfo info = roster.getInfoForIdentifier("john@gmail.com");
        assertEquals("John Doe", info.getName());
        assertEquals("John Doe", info.getTeamName());
        assertEquals("John Doe's Section", info.getSectionName());
    }

    @Test
    public void testGetInfoForIdentifier_instructorCase_shouldShowCorrectInfo() {
        CourseRoster roster = new CourseRoster(
                createStudentList(
                        "s1", "s1@gmail.com",
                        "s2", "s2@gmail.com",
                        "s3", "s3@gmail.com"),
                createInstructorList(
                        "John Doe", "john@email.com",
                        "Jean", "ins2@email.com"));
        CourseRoster.ParticipantInfo info = roster.getInfoForIdentifier("john@email.com");
        assertEquals("John Doe", info.getName());
        assertEquals(Const.USER_TEAM_FOR_INSTRUCTOR, info.getTeamName());
        assertEquals(Const.DEFAULT_SECTION, info.getSectionName());
    }

    @Test
    public void testGetInfoForIdentifier_teamCase_shouldShowCorrectInfo() {
        CourseRoster roster = new CourseRoster(
                createStudentList(
                        "s1", "s1@gmail.com",
                        "s2", "s2@gmail.com",
                        "s3", "s3@gmail.com"),
                createInstructorList(
                        "John", "john@email.com",
                        "Jean", "ins2@email.com"));
        CourseRoster.ParticipantInfo info = roster.getInfoForIdentifier("s1");
        assertEquals("s1", info.getName());
        assertEquals("s1", info.getTeamName());
        assertEquals("s1's Section", info.getSectionName());
    }

    @Test
    public void testGetInfoForIdentifier_unknownCase_shouldShowCorrectInfo() {
        CourseRoster roster = new CourseRoster(
                createStudentList(
                        "s1", "s1@gmail.com",
                        "s2", "s2@gmail.com",
                        "s3", "s3@gmail.com"),
                createInstructorList(
                        "John", "john@email.com",
                        "Jean", "ins2@email.com"));
        CourseRoster.ParticipantInfo info = roster.getInfoForIdentifier("random");
        assertEquals(Const.USER_NOBODY_TEXT, info.getName());
        assertEquals(Const.USER_NOBODY_TEXT, info.getTeamName());
        assertEquals(Const.DEFAULT_SECTION, info.getSectionName());
    }

    private List<StudentAttributes> createStudentList(String... studentData) {
        List<StudentAttributes> students = new ArrayList<>();
        for (int i = 0; i < studentData.length; i += 2) {
            String studentEmail = studentData[i + 1];
            String studentName = studentData[i];
            StudentAttributes student = StudentAttributes
                    .builder("", studentEmail)
                    .withName(studentName)
                    .withTeamName(studentName)
                    .withSectionName(studentName + "'s Section")
                    .build();
            students.add(student);
        }
        return students;
    }

    private List<InstructorAttributes> createInstructorList(String... instructorData) {
        List<InstructorAttributes> instructors = new ArrayList<>();
        for (int i = 0; i < instructorData.length; i += 2) {
            String instructorEmail = instructorData[i + 1];
            String instructorName = instructorData[i];
            InstructorAttributes instructor = InstructorAttributes
                    .builder("courseId", instructorEmail)
                    .withGoogleId("googleId")
                    .withName(instructorName)
                    .build();
            instructors.add(instructor);
        }
        return instructors;
    }

}
