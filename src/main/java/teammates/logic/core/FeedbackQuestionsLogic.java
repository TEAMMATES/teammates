package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.api.FeedbackQuestionsDb;

/**
 * Handles operations related to feedback questions.
 *
 * @see FeedbackQuestionAttributes
 * @see FeedbackQuestionsDb
 */
public final class FeedbackQuestionsLogic {

    static final String USER_NAME_FOR_SELF = "Myself";

    private static final Logger log = Logger.getLogger();

    private static FeedbackQuestionsLogic instance = new FeedbackQuestionsLogic();

    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private FeedbackQuestionsLogic() {
        // prevent initialization
    }

    public static FeedbackQuestionsLogic inst() {
        return instance;
    }

    /**
     * Creates a new feedback question.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     */
    public FeedbackQuestionAttributes createFeedbackQuestion(FeedbackQuestionAttributes fqa)
            throws InvalidParametersException {

        List<FeedbackQuestionAttributes> questionsBefore =
                getFeedbackQuestionsForSession(fqa.getFeedbackSessionName(), fqa.getCourseId());

        FeedbackQuestionAttributes createdQuestion = fqDb.putEntity(fqa);

        adjustQuestionNumbers(questionsBefore.size() + 1, createdQuestion.getQuestionNumber(), questionsBefore);
        return createdQuestion;
    }

    /**
     * Gets a single question corresponding to the given parameters. <br><br>
     * <b>Note:</b><br>
     * *    This method should only be used if the question already exists in the<br>
     * datastore and has an ID already generated.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        return fqDb.getFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Gets a single question corresponding to the given parameters.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(
            String feedbackSessionName,
            String courseId,
            int questionNumber) {
        return fqDb.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
    }

    /**
     * Gets a {@link List} of every FeedbackQuestion in the given session.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(
            String feedbackSessionName, String courseId) {

        List<FeedbackQuestionAttributes> questions =
                fqDb.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        questions.sort(null);

        // check whether the question numbers are consistent
        if (questions.size() > 1 && !areQuestionNumbersConsistent(questions)) {
            log.severe(courseId + ": " + feedbackSessionName + " has invalid question numbers");
        }

        return questions;
    }

    // TODO can be removed once we are sure that question numbers will be consistent
    private boolean areQuestionNumbersConsistent(List<FeedbackQuestionAttributes> questions) {
        Set<Integer> questionNumbersInSession = new HashSet<>();
        for (FeedbackQuestionAttributes question : questions) {
            if (!questionNumbersInSession.add(question.questionNumber)) {
                return false;
            }
        }

        for (int i = 1; i <= questions.size(); i++) {
            if (!questionNumbersInSession.contains(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets a {@code List} of all questions for the given session for an
     * instructor to view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructor(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {

        if (fsLogic.getFeedbackSession(feedbackSessionName, courseId) == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get questions for a feedback session that does not exist.");
        }

        if (fsLogic.isCreatorOfSession(feedbackSessionName, courseId, userEmail)) {
            return getFeedbackQuestionsForCreatorInstructor(feedbackSessionName, courseId);
        }

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        boolean isInstructor = instructor != null;

        if (isInstructor) {
            questions.addAll(fqDb.getFeedbackQuestionsForGiverType(
                            feedbackSessionName, courseId, FeedbackParticipantType.INSTRUCTORS));
        }
        questions.sort(null);
        return questions;
    }

    /**
     * Filters through the given list of questions and returns a {@code List} of
     * questions that an instructor can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructor(
            List<FeedbackQuestionAttributes> allQuestions, boolean isCreator) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        for (FeedbackQuestionAttributes question : allQuestions) {
            if (question.giverType == FeedbackParticipantType.INSTRUCTORS
                    || question.giverType == FeedbackParticipantType.SELF && isCreator) {
                questions.add(question);
            }
        }

        return questions;
    }

    /**
     * Gets a {@code List} of all questions for the list of questions that an
     * instructor who is the creator of the course can view/submit.
     */
    List<FeedbackQuestionAttributes> getFeedbackQuestionsForCreatorInstructor(
            String feedbackSessionName, String courseId)
                    throws EntityDoesNotExistException {

        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (fsa == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get questions for a feedback session that does not exist.");
        }

        return getFeedbackQuestionsForCreatorInstructor(fsa);
    }

    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCreatorInstructor(
                                    FeedbackSessionAttributes fsa) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        String feedbackSessionName = fsa.getFeedbackSessionName();
        String courseId = fsa.getCourseId();

        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(
                                       feedbackSessionName, courseId, FeedbackParticipantType.INSTRUCTORS));

        // Return all self (creator) questions
        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSessionName,
                courseId, FeedbackParticipantType.SELF));

        questions.sort(null);
        return questions;
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            String feedbackSessionName, String courseId) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, FeedbackParticipantType.STUDENTS));
        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, FeedbackParticipantType.TEAMS));

        questions.sort(null);
        return questions;
    }

    /**
     * Filters through the given list of questions and returns a {@code List} of
     * questions that students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            List<FeedbackQuestionAttributes> allQuestions) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        for (FeedbackQuestionAttributes question : allQuestions) {
            if (question.giverType == FeedbackParticipantType.STUDENTS
                    || question.giverType == FeedbackParticipantType.TEAMS) {
                questions.add(question);
            }
        }

        return questions;
    }

    Map<String, String> getRecipientsForQuestion(FeedbackQuestionAttributes question, String giver)
            throws EntityDoesNotExistException {

        InstructorAttributes instructorGiver = instructorsLogic.getInstructorForEmail(question.courseId, giver);
        StudentAttributes studentGiver = studentsLogic.getStudentForEmail(question.courseId, giver);

        Map<String, String> recipients = new HashMap<>();

        FeedbackParticipantType recipientType = question.recipientType;

        String giverTeam = getGiverTeam(giver, instructorGiver, studentGiver);

        switch (recipientType) {
        case SELF:
            if (question.giverType == FeedbackParticipantType.TEAMS) {
                recipients.put(studentGiver.team, studentGiver.team);
            } else {
                recipients.put(giver, USER_NAME_FOR_SELF);
            }
            break;
        case STUDENTS:
            List<StudentAttributes> studentsInCourse = studentsLogic.getStudentsForCourse(question.courseId);
            for (StudentAttributes student : studentsInCourse) {
                // Ensure student does not evaluate himself
                if (!giver.equals(student.email)) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorsInCourse = instructorsLogic.getInstructorsForCourse(question.courseId);
            for (InstructorAttributes instr : instructorsInCourse) {
                // Ensure instructor does not evaluate himself
                if (!giver.equals(instr.email)) {
                    recipients.put(instr.email, instr.name);
                }
            }
            break;
        case TEAMS:
            List<String> teams = coursesLogic.getTeamsForCourse(question.courseId);
            for (String team : teams) {
                // Ensure student('s team) does not evaluate own team.
                if (!giverTeam.equals(team)) {
                    // recipientEmail doubles as team name in this case.
                    recipients.put(team, team);
                }
            }
            break;
        case OWN_TEAM:
            recipients.put(giverTeam, giverTeam);
            break;
        case OWN_TEAM_MEMBERS:
            List<StudentAttributes> students = studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            for (StudentAttributes student : students) {
                if (!student.email.equals(giver)) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            List<StudentAttributes> teamMembers = studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            for (StudentAttributes student : teamMembers) {
                // accepts self feedback too
                recipients.put(student.email, student.name);
            }
            break;
        case NONE:
            recipients.put(Const.GENERAL_QUESTION, Const.GENERAL_QUESTION);
            break;
        default:
            break;
        }
        return recipients;
    }

    /**
     * Gets the recipients of a feedback question.
     *
     * @param question the feedback question
     * @param instructorGiver can be null for student giver
     * @param studentGiver can be null for instructor giver
     * @param courseRoster if provided, the function can be completed without touching database
     * @return a map which keys are the identifiers of the recipients and values are the names of the recipients
     */
    public Map<String, String> getRecipientsOfQuestion(
            FeedbackQuestionAttributes question,
            @Nullable InstructorAttributes instructorGiver, @Nullable StudentAttributes studentGiver,
            @Nullable CourseRoster courseRoster) {
        Assumption.assertTrue(instructorGiver != null || studentGiver != null);

        Map<String, String> recipients = new HashMap<>();

        boolean isStudentGiver = studentGiver != null;
        boolean isInstructorGiver = instructorGiver != null;

        String giverEmail = "";
        String giverTeam = "";
        if (isStudentGiver) {
            giverEmail = studentGiver.email;
            giverTeam = studentGiver.team;
        } else if (isInstructorGiver) {
            giverEmail = instructorGiver.email;
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        }

        FeedbackParticipantType recipientType = question.recipientType;

        switch (recipientType) {
        case SELF:
            if (question.giverType == FeedbackParticipantType.TEAMS) {
                recipients.put(giverTeam, giverTeam);
            } else {
                recipients.put(giverEmail, USER_NAME_FOR_SELF);
            }
            break;
        case STUDENTS:
            List<StudentAttributes> studentsInCourse;
            if (courseRoster == null) {
                studentsInCourse = studentsLogic.getStudentsForCourse(question.courseId);
            } else {
                studentsInCourse = courseRoster.getStudents();
            }
            for (StudentAttributes student : studentsInCourse) {
                if (isInstructorGiver && !instructorGiver.isAllowedForPrivilege(
                        student.section, question.getFeedbackSessionName(),
                        Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)) {
                    // instructor can only see students in allowed sections for him/her
                    continue;
                }
                // Ensure student does not evaluate himself
                if (!giverEmail.equals(student.email)) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorsInCourse;
            if (courseRoster == null) {
                instructorsInCourse = instructorsLogic.getInstructorsForCourse(question.courseId);
            } else {
                instructorsInCourse = courseRoster.getInstructors();
            }
            for (InstructorAttributes instr : instructorsInCourse) {
                // remove hidden instructors for students
                if (isStudentGiver && !instr.isDisplayedToStudents()) {
                    continue;
                }
                // Ensure instructor does not evaluate himself
                if (!giverEmail.equals(instr.email)) {
                    recipients.put(instr.email, instr.name);
                }
            }
            break;
        case TEAMS:
            Map<String, List<StudentAttributes>> teamToTeamMembersTable;
            if (courseRoster == null) {
                List<StudentAttributes> students = studentsLogic.getStudentsForCourse(question.courseId);
                teamToTeamMembersTable = CourseRoster.buildTeamToMembersTable(students);
            } else {
                teamToTeamMembersTable = courseRoster.getTeamToMembersTable();
            }
            for (Map.Entry<String, List<StudentAttributes>> team : teamToTeamMembersTable.entrySet()) {
                if (isInstructorGiver && !instructorGiver.isAllowedForPrivilege(
                        team.getValue().iterator().next().getSection(),
                        question.getFeedbackSessionName(),
                        Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)) {
                    // instructor can only see teams in allowed sections for him/her
                    continue;
                }
                // Ensure student('s team) does not evaluate own team.
                if (!giverTeam.equals(team.getKey())) {
                    // recipientEmail doubles as team name in this case.
                    recipients.put(team.getKey(), team.getKey());
                }
            }
            break;
        case OWN_TEAM:
            recipients.put(giverTeam, giverTeam);
            break;
        case OWN_TEAM_MEMBERS:
            List<StudentAttributes> students;
            if (courseRoster == null) {
                students = studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            } else {
                students = courseRoster.getTeamToMembersTable().getOrDefault(giverTeam, Collections.emptyList());
            }
            for (StudentAttributes student : students) {
                if (!student.email.equals(giverEmail)) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            List<StudentAttributes> teamMembers;
            if (courseRoster == null) {
                teamMembers = studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            } else {
                teamMembers = courseRoster.getTeamToMembersTable().getOrDefault(giverTeam, Collections.emptyList());
            }
            for (StudentAttributes student : teamMembers) {
                // accepts self feedback too
                recipients.put(student.email, student.name);
            }
            break;
        case NONE:
            recipients.put(Const.GENERAL_QUESTION, Const.GENERAL_QUESTION);
            break;
        default:
            break;
        }
        return recipients;
    }

    /**
     * Builds a complete giver to recipient map for a {@code relatedQuestion}.
     *
     * @param feedbackSession The feedback session that contains the question
     * @param relatedQuestion The question to be considered
     * @param courseRoster the roster in the course
     * @return a map from giver to recipient for the question.
     */
    public Map<String, Set<String>> buildCompleteGiverRecipientMap(
            FeedbackSessionAttributes feedbackSession, FeedbackQuestionAttributes relatedQuestion,
            CourseRoster courseRoster) {
        Map<String, Set<String>> completeGiverRecipientMap = new HashMap<>();

        List<String> possibleGivers = getPossibleGivers(feedbackSession, relatedQuestion, courseRoster);
        for (String possibleGiver : possibleGivers) {
            switch (relatedQuestion.getGiverType()) {
            case STUDENTS:
                StudentAttributes studentGiver = courseRoster.getStudentForEmail(possibleGiver);
                completeGiverRecipientMap
                        .computeIfAbsent(possibleGiver, key -> new HashSet<>())
                        .addAll(getRecipientsOfQuestion(
                                relatedQuestion, null, studentGiver, courseRoster).keySet());
                break;
            case TEAMS:
                StudentAttributes oneTeamMember =
                        courseRoster.getTeamToMembersTable().get(possibleGiver).iterator().next();
                completeGiverRecipientMap
                        .computeIfAbsent(possibleGiver, key -> new HashSet<>())
                        .addAll(getRecipientsOfQuestion(
                                relatedQuestion, null, oneTeamMember, courseRoster).keySet());
                break;
            case INSTRUCTORS:
            case SELF:
                InstructorAttributes instructorGiver = courseRoster.getInstructorForEmail(possibleGiver);
                completeGiverRecipientMap
                        .computeIfAbsent(possibleGiver, key -> new HashSet<>())
                        .addAll(getRecipientsOfQuestion(
                                relatedQuestion, instructorGiver, null, courseRoster).keySet());
                break;
            default:
                log.severe("Invalid giver type specified");
                break;
            }
        }

        return completeGiverRecipientMap;
    }

    /**
     * Gets possible giver identifiers for a feedback question.
     *
     * @param fqa the feedback question
     * @param courseRoster roster of all students and instructors
     * @return a list of giver identifier
     */
    private List<String> getPossibleGivers(
            FeedbackSessionAttributes feedbackSession,
            FeedbackQuestionAttributes fqa, CourseRoster courseRoster) {
        FeedbackParticipantType giverType = fqa.giverType;
        List<String> possibleGivers = new ArrayList<>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = courseRoster.getStudents()
                    .stream()
                    .map(StudentAttributes::getEmail)
                    .collect(Collectors.toList());
            break;
        case INSTRUCTORS:
            possibleGivers = courseRoster.getInstructors()
                    .stream()
                    .map(InstructorAttributes::getEmail)
                    .collect(Collectors.toList());
            break;
        case TEAMS:
            possibleGivers = new ArrayList<>(courseRoster.getTeamToMembersTable().keySet());
            break;
        case SELF:
            possibleGivers = Collections.singletonList(feedbackSession.getCreatorEmail());
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        return possibleGivers;
    }

    /**
     * Populates fields that need dynamic generation in a question.
     *
     * <p>Currently, only MCQ/MSQ needs to generate choices dynamically.</p>
     *
     * @param feedbackQuestionAttributes the question to populate
     * @param emailOfEntityDoingQuestion the email of the entity doing the question
     * @param teamOfEntityDoingQuestion the team of the entity doing the question. If the entity is an instructor,
     *                                  it can be {@code null}.
     */
    public void populateFieldsToGenerateInQuestion(FeedbackQuestionAttributes feedbackQuestionAttributes,
            String emailOfEntityDoingQuestion, String teamOfEntityDoingQuestion) {
        List<String> optionList;

        FeedbackParticipantType generateOptionsFor;

        if (feedbackQuestionAttributes.getQuestionType() == FeedbackQuestionType.MCQ) {
            FeedbackMcqQuestionDetails feedbackMcqQuestionDetails =
                    (FeedbackMcqQuestionDetails) feedbackQuestionAttributes.getQuestionDetails();
            optionList = feedbackMcqQuestionDetails.getMcqChoices();
            generateOptionsFor = feedbackMcqQuestionDetails.getGenerateOptionsFor();
        } else if (feedbackQuestionAttributes.getQuestionType() == FeedbackQuestionType.MSQ) {
            FeedbackMsqQuestionDetails feedbackMsqQuestionDetails =
                    (FeedbackMsqQuestionDetails) feedbackQuestionAttributes.getQuestionDetails();
            optionList = feedbackMsqQuestionDetails.getMsqChoices();
            generateOptionsFor = feedbackMsqQuestionDetails.getGenerateOptionsFor();
        } else {
            // other question types
            return;
        }

        switch (generateOptionsFor) {
        case NONE:
            break;
        case STUDENTS:
            //fallthrough
        case STUDENTS_EXCLUDING_SELF:
            List<StudentAttributes> studentList =
                    studentsLogic.getStudentsForCourse(feedbackQuestionAttributes.getCourseId());

            if (generateOptionsFor == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF) {
                studentList.removeIf(studentInList -> studentInList.email.equals(emailOfEntityDoingQuestion));
            }

            for (StudentAttributes student : studentList) {
                optionList.add(student.name + " (" + student.team + ")");
            }

            optionList.sort(null);
            break;
        case TEAMS:
            //fallthrough
        case TEAMS_EXCLUDING_SELF:
            try {
                List<String> teams = coursesLogic.getTeamsForCourse(feedbackQuestionAttributes.getCourseId());

                if (generateOptionsFor == FeedbackParticipantType.TEAMS_EXCLUDING_SELF) {
                    teams.removeIf(team -> team.equals(teamOfEntityDoingQuestion));
                }

                for (String team : teams) {
                    optionList.add(team);
                }

                optionList.sort(null);
            } catch (EntityDoesNotExistException e) {
                Assumption.fail("Course disappeared");
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorList =
                    instructorsLogic.getInstructorsForCourse(feedbackQuestionAttributes.getCourseId());

            for (InstructorAttributes instructor : instructorList) {
                optionList.add(instructor.getName());
            }

            optionList.sort(null);
            break;
        default:
            Assumption.fail("Trying to generate options for neither students, teams nor instructors");
            break;
        }

        if (feedbackQuestionAttributes.getQuestionType() == FeedbackQuestionType.MCQ) {
            FeedbackMcqQuestionDetails feedbackMcqQuestionDetails =
                    (FeedbackMcqQuestionDetails) feedbackQuestionAttributes.getQuestionDetails();
            feedbackMcqQuestionDetails.setMcqChoices(optionList);
            feedbackQuestionAttributes.setQuestionDetails(feedbackMcqQuestionDetails);
        } else if (feedbackQuestionAttributes.getQuestionType() == FeedbackQuestionType.MSQ) {
            FeedbackMsqQuestionDetails feedbackMsqQuestionDetails =
                    (FeedbackMsqQuestionDetails) feedbackQuestionAttributes.getQuestionDetails();
            feedbackMsqQuestionDetails.setMsqChoices(optionList);
            feedbackQuestionAttributes.setQuestionDetails(feedbackMsqQuestionDetails);
        }
    }

    private String getGiverTeam(String defaultTeam, InstructorAttributes instructorGiver,
            StudentAttributes studentGiver) {
        String giverTeam = defaultTeam;
        boolean isStudentGiver = studentGiver != null;
        boolean isInstructorGiver = instructorGiver != null;
        if (isStudentGiver) {
            giverTeam = studentGiver.team;
        } else if (isInstructorGiver) {
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        }
        return giverTeam;
    }

    public boolean isQuestionFullyAnsweredByUser(FeedbackQuestionAttributes question, String email)
            throws EntityDoesNotExistException {

        int numberOfResponsesGiven =
                frLogic.getFeedbackResponsesFromGiverForQuestion(question.getId(), email).size();
        int numberOfResponsesNeeded =
                question.numberOfEntitiesToGiveFeedbackTo;

        if (numberOfResponsesNeeded == Const.MAX_POSSIBLE_RECIPIENTS) {
            numberOfResponsesNeeded = getRecipientsForQuestion(question, email).size();
        }

        return numberOfResponsesGiven >= numberOfResponsesNeeded;
    }

    /**
     * Updates a feedback question by {@code FeedbackQuestionAttributes.UpdateOptions}.
     *
     * <p>Cascade adjust the question number of questions in the same session.
     *
     * <p>Cascade adjust the existing response of the question.
     *
     * @return updated feedback question
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback question cannot be found
     */
    public FeedbackQuestionAttributes updateFeedbackQuestionCascade(FeedbackQuestionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackQuestionAttributes oldQuestion = fqDb.getFeedbackQuestion(updateOptions.getFeedbackQuestionId());
        if (oldQuestion == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback question that does not exist.");
        }

        FeedbackQuestionAttributes newQuestion = oldQuestion.getCopy();
        newQuestion.update(updateOptions);
        int oldQuestionNumber = oldQuestion.questionNumber;
        int newQuestionNumber = newQuestion.questionNumber;

        List<FeedbackQuestionAttributes> previousQuestionsInSession = new ArrayList<>();
        if (oldQuestionNumber != newQuestionNumber) {
            // get questions in session before update
            String feedbackSessionName = oldQuestion.feedbackSessionName;
            String courseId = oldQuestion.courseId;
            previousQuestionsInSession = getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        }

        // update question
        FeedbackQuestionAttributes updatedQuestion = fqDb.updateFeedbackQuestion(updateOptions);

        if (oldQuestionNumber != newQuestionNumber) {
            // shift other feedback questions (generate an empty "slot")
            adjustQuestionNumbers(oldQuestionNumber, newQuestionNumber, previousQuestionsInSession);
        }

        // adjust responses
        if (oldQuestion.areResponseDeletionsRequiredForChanges(updatedQuestion)) {
            frLogic.deleteFeedbackResponsesForQuestionCascade(oldQuestion.getId());
        }

        return updatedQuestion;
    }

    /**
     * Adjust questions between the old and new number,
     * if the new number is smaller, then shift up (increase qn#) all questions in between.
     * if the new number is bigger, then shift down(decrease qn#) all questions in between.
     */
    private void adjustQuestionNumbers(int oldQuestionNumber,
            int newQuestionNumber, List<FeedbackQuestionAttributes> questions) {
        try {
            if (oldQuestionNumber > newQuestionNumber && oldQuestionNumber >= 1) {
                for (int i = oldQuestionNumber - 1; i >= newQuestionNumber; i--) {
                    FeedbackQuestionAttributes question = questions.get(i - 1);
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                                    .withQuestionNumber(question.questionNumber + 1)
                                    .build());
                }
            } else if (oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()) {
                for (int i = oldQuestionNumber + 1; i <= newQuestionNumber; i++) {
                    FeedbackQuestionAttributes question = questions.get(i - 1);
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                                    .withQuestionNumber(question.questionNumber - 1)
                                    .build());
                }
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Adjusting question number should not cause: " + e.getMessage());
        }
    }

    /**
     * Deletes a feedback question cascade its responses and comments.
     *
     * <p>Silently fail if question does not exist.
     */
    public void deleteFeedbackQuestionCascade(String feedbackQuestionId) {
        FeedbackQuestionAttributes questionToDelete =
                        getFeedbackQuestion(feedbackQuestionId);

        if (questionToDelete == null) {
            return; // Silently fail if question does not exist.
        }

        // cascade delete responses for question.
        frLogic.deleteFeedbackResponsesForQuestionCascade(questionToDelete.getId());

        List<FeedbackQuestionAttributes> questionsToShiftQnNumber =
                getFeedbackQuestionsForSession(questionToDelete.getFeedbackSessionName(), questionToDelete.getCourseId());

        // delete question
        fqDb.deleteFeedbackQuestion(feedbackQuestionId);

        // adjust question numbers
        if (questionToDelete.questionNumber < questionsToShiftQnNumber.size()) {
            shiftQuestionNumbersDown(questionToDelete.questionNumber, questionsToShiftQnNumber);
        }
    }

    /**
     * Deletes questions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackQuestions(AttributesDeletionQuery query) {
        fqDb.deleteFeedbackQuestions(query);
    }

    // Shifts all question numbers after questionNumberToShiftFrom down by one.
    private void shiftQuestionNumbersDown(int questionNumberToShiftFrom,
            List<FeedbackQuestionAttributes> questionsToShift) {
        for (FeedbackQuestionAttributes question : questionsToShift) {
            if (question.questionNumber > questionNumberToShiftFrom) {
                try {
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                            .withQuestionNumber(question.questionNumber - 1)
                            .build());
                } catch (InvalidParametersException | EntityDoesNotExistException e) {
                    Assumption.fail("Shifting question number should not cause: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Gets the number of generated options for MCQ-type and MSQ-type question.
     */
    public int getNumOfGeneratedChoicesForParticipantType(String courseId, FeedbackParticipantType participantType) {
        if (participantType == FeedbackParticipantType.STUDENTS
                || participantType == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF) {
            List<StudentAttributes> studentList = studentsLogic.getStudentsForCourse(courseId);
            return studentList.size() - (participantType == FeedbackParticipantType.STUDENTS ? 0 : 1);
        }

        if (participantType == FeedbackParticipantType.TEAMS
                || participantType == FeedbackParticipantType.TEAMS_EXCLUDING_SELF) {
            try {
                List<String> teams = coursesLogic.getTeamsForCourse(courseId);
                return teams.size() - (participantType == FeedbackParticipantType.TEAMS ? 0 : 1);
            } catch (EntityDoesNotExistException e) {
                Assumption.fail("Course disappeared");
            }
        }

        if (participantType == FeedbackParticipantType.INSTRUCTORS) {
            List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(courseId);
            return instructorList.size();
        }

        return 0;
    }

}
