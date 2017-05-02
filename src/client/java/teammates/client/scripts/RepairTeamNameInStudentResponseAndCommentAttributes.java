package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;

/**
 * Previews and removes extra spaces in StudentAttributes, FeedbackResponseAttributes
 * and CommentAttributes.
 */
public class RepairTeamNameInStudentResponseAndCommentAttributes extends RemoteApiClient {
    private static final boolean isPreview = true;

    private StudentsDb studentsDb = new StudentsDb();
    private StudentsLogic studentsLogic = StudentsLogic.inst();
    private FeedbackResponsesLogic responsesLogic = FeedbackResponsesLogic.inst();
    private CommentsLogic commentsLogic = CommentsLogic.inst();

    public static void main(String[] args) throws IOException {
        RepairTeamNameInStudentResponseAndCommentAttributes migrator =
                new RepairTeamNameInStudentResponseAndCommentAttributes();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<CourseAttributes> courseList = getCoursesWithinOneYear();
        List<CourseStudent> allStudents = getStudentsFromCourses(courseList);
        int totalNumberOfStudents = allStudents.size();

        System.out.println("There is/are " + courseList.size() + " course(s) within one year.");

        if (isPreview) {
            System.out.println("Checking extra spaces in team name...");
        } else {
            System.out.println("Removing extra spaces in team name...");
        }

        try {
            int numberOfStudentsWithExtraSpacesInTeamName = removeExtraSpacesInStudents(allStudents);
            Map<String, Set<String>> courseTeamListMap = createCourseTeamNameList(allStudents);
            int numberOfReponsesWithExtraSpacesInRecipient = removeExtraSpacesInResponses(courseTeamListMap);
            int numberOfCommentsWithExtraSpacesInRecipient = removeExtraSpacesInComments(courseTeamListMap);

            if (isPreview) {
                System.out.println("There are/is " + numberOfStudentsWithExtraSpacesInTeamName
                                   + "/" + totalNumberOfStudents + " student(s) with extra spaces in team name!");
                System.out.println("There are/is " + numberOfReponsesWithExtraSpacesInRecipient
                                   + " response(s) with extra spaces in recipient and/or giver!");
                System.out.println("There are/is " + numberOfCommentsWithExtraSpacesInRecipient
                                   + " comment(s) with extra spaces in recipient!");

            } else {
                System.out.println(numberOfStudentsWithExtraSpacesInTeamName
                                   + "/" + totalNumberOfStudents + " student(s) have been fixed!");
                System.out.println(numberOfReponsesWithExtraSpacesInRecipient
                                   + " response(s) have been fixed!");
                System.out.println(numberOfCommentsWithExtraSpacesInRecipient
                                   + " comment(s) have been fixed!");
                System.out.println("Extra space removing done!");
            }
        } catch (InvalidParametersException | EntityDoesNotExistException | EntityAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    private List<CourseStudent> getStudentsFromCourses(List<CourseAttributes> courseList) {
        List<CourseStudent> studentList = new ArrayList<CourseStudent>();
        System.out.println("Number of courses in last year : " + courseList.size());
        for (CourseAttributes course : courseList) {
            System.out.println("Getting students of " + course.getId()
                               + " and adding to current total of " + studentList.size());
            studentList.addAll(studentsDb.getCourseStudentEntitiesForCourse(course.getId()));
        }
        return studentList;
    }

    private List<CourseAttributes> getCoursesWithinOneYear() {
        List<CourseAttributes> courseList = new ArrayList<CourseAttributes>();

        Query q = PM.newQuery(Course.class);
        q.declareParameters("java.util.Date startTime");
        q.setFilter("createdAt >= startTime");

        Calendar startTime = new GregorianCalendar(2016, Calendar.JANUARY, 1);

        @SuppressWarnings("unchecked")
        List<Course> courseEntityList = (List<Course>) q.execute(startTime.getTime());
        for (Course course : courseEntityList) {
            courseList.add(new CourseAttributes(course));
        }
        return courseList;
    }

    /**
     * Creates a list of team names that contain extra spaces over courseId.
     */
    private Map<String, Set<String>> createCourseTeamNameList(List<CourseStudent> allStudents) {
        Map<String, Set<String>> courseTeamListMap = new TreeMap<String, Set<String>>();
        for (CourseStudent studentEntity : allStudents) {
            if (!hasExtraSpaces(studentEntity.getTeamName())) {
                continue;
            }
            String courseId = studentEntity.getCourseId();
            String teamName = studentEntity.getTeamName();
            if (!courseTeamListMap.containsKey(courseId)) {
                courseTeamListMap.put(courseId, new TreeSet<String>());
            }
            courseTeamListMap.get(courseId).add(teamName);
        }
        return courseTeamListMap;
    }

    /**
     * Concatenates strings with extra spaces in the set.
     */
    private String extractStringsWithExtraSpace(Set<String> set) {
        StringBuilder result = new StringBuilder();
        for (String s : set) {
            if (hasExtraSpaces(s)) {
                result.append(s).append(' ');
            }
        }
        return result.toString();
    }

    /**
     * Previews or removes extra spaces in team name of students.
     * @return the number of students with extra spaces in team name.
     */
    private int removeExtraSpacesInStudents(List<CourseStudent> allStudents)
                    throws InvalidParametersException, EntityDoesNotExistException {
        int numberOfStudentsWithExtraSpacesInTeamName = 0;
        for (CourseStudent studentEntity : allStudents) {
            if (!hasExtraSpaces(studentEntity.getTeamName())) {
                continue;
            }
            numberOfStudentsWithExtraSpacesInTeamName++;

            if (isPreview) {
                System.out.println(numberOfStudentsWithExtraSpacesInTeamName
                                   + ". \"" + studentEntity.getTeamName() + "\" "
                                   + "courseId: " + studentEntity.getCourseId());
            } else {
                StudentAttributes student = new StudentAttributes(studentEntity);
                updateStudent(student.email, student);
            }
        }
        return numberOfStudentsWithExtraSpacesInTeamName;
    }

    /**
     * Previews or removes extra spaces in recipients of comments.
     * @return the number of comments with extra spaces in recipients.
     */
    private int removeExtraSpacesInComments(Map<String, Set<String>> courseTeamListMap)
                    throws InvalidParametersException, EntityDoesNotExistException {
        int numberOfCommentWithExtraSpacesInRecipient = 0;

        for (Entry<String, Set<String>> entry : courseTeamListMap.entrySet()) {
            String courseId = entry.getKey();
            Set<String> teamNameList = entry.getValue();
            for (String teamName : teamNameList) {
                List<CommentAttributes> comments = commentsLogic.getCommentsForReceiver(
                                                                    courseId, CommentParticipantType.TEAM, teamName);
                numberOfCommentWithExtraSpacesInRecipient += removeExtraSpacesInComments(comments);
            }
        }

        return numberOfCommentWithExtraSpacesInRecipient;
    }

    private int removeExtraSpacesInComments(List<CommentAttributes> comments)
                    throws InvalidParametersException, EntityDoesNotExistException {
        int numberOfCommentWithExtraSpacesInRecipient = 0;
        for (CommentAttributes comment : comments) {
            numberOfCommentWithExtraSpacesInRecipient++;
            if (isPreview) {
                String recipientsWithExtraSpace = extractStringsWithExtraSpace(comment.recipients);
                System.out.println(numberOfCommentWithExtraSpacesInRecipient
                                   + ". \"" + recipientsWithExtraSpace + "\""
                                   + "courseId: " + comment.courseId);
            } else {
                comment.recipients = StringHelper.removeExtraSpace(comment.recipients);
                commentsLogic.updateComment(comment);
            }
        }
        return numberOfCommentWithExtraSpacesInRecipient;
    }

    /**
     * Previews or removes extra spaces in recipient and/or giver of feedback responses.
     * @return the number of responses with extra spaces in recipient and/or giver.
     */
    private int removeExtraSpacesInResponses(Map<String, Set<String>> courseTeamListMap)
                    throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        int numberOfReponsesWithExtraSpacesInRecipient = 0;
        for (Entry<String, Set<String>> entry : courseTeamListMap.entrySet()) {
            String courseId = entry.getKey();
            Set<String> teamNameList = entry.getValue();
            for (String teamName : teamNameList) {
                List<FeedbackResponseAttributes> responsesTeamAsReceiver =
                        responsesLogic.getFeedbackResponsesForReceiverForCourse(courseId, teamName);
                numberOfReponsesWithExtraSpacesInRecipient += removeExtraSpacesInResponses(responsesTeamAsReceiver);

                List<FeedbackResponseAttributes> responsesTeamAsGiver =
                        responsesLogic.getFeedbackResponsesFromGiverForCourse(courseId, teamName);
                numberOfReponsesWithExtraSpacesInRecipient += removeExtraSpacesInResponses(responsesTeamAsGiver);
            }
        }
        return numberOfReponsesWithExtraSpacesInRecipient;
    }

    private int removeExtraSpacesInResponses(List<FeedbackResponseAttributes> responses)
                    throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        int numberOfReponsesWithExtraSpacesInRecipient = 0;
        for (FeedbackResponseAttributes response : responses) {
            numberOfReponsesWithExtraSpacesInRecipient++;
            if (isPreview) {
                System.out.println(numberOfReponsesWithExtraSpacesInRecipient
                                   + ". From \"" + response.giver + "\" "
                                   + ". To \"" + response.recipient + "\" "
                                   + "courseId: " + response.courseId + " sessionName: "
                                   + response.feedbackSessionName);
            } else {
                response.recipient = StringHelper.removeExtraSpace(response.recipient);
                response.giver = StringHelper.removeExtraSpace(response.giver);
                responsesLogic.updateFeedbackResponse(response);
            }
        }
        return numberOfReponsesWithExtraSpacesInRecipient;
    }

    /**
     * Check if there is extra space in the string.
     */
    private boolean hasExtraSpaces(String s) {
        return !s.equals(StringHelper.removeExtraSpace(s));
    }

    private void updateStudent(String originalEmail, StudentAttributes student) throws InvalidParametersException,
                                                                                      EntityDoesNotExistException {
        studentsDb.verifyStudentExists(student.course, originalEmail);
        StudentAttributes originalStudent = studentsLogic.getStudentForEmail(student.course, originalEmail);

        // prepare new student
        student.updateWithExistingRecord(originalStudent);

        if (!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }

        studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.section,
                                 student.email, student.googleId, student.comments, true);
    }
}
