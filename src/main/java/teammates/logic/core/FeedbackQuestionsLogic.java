package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Student;
import teammates.logic.entity.Team;
import teammates.logic.entity.User;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.ui.request.FeedbackQuestionUpdateRequest;

/**
 * Handles operations related to feedback questions.
 *
 * @see FeedbackQuestion
 * @see FeedbackQuestionsDb
 */
public final class FeedbackQuestionsLogic {

    static final String USER_NAME_FOR_SELF = "Myself";

    private static final Logger log = Logger.getLogger();

    private static final FeedbackQuestionsLogic instance = new FeedbackQuestionsLogic();
    private FeedbackQuestionsDb fqDb;
    private CoursesLogic coursesLogic;
    private FeedbackResponsesLogic frLogic;
    private UsersLogic usersLogic;
    private FeedbackSessionsLogic feedbackSessionsLogic;

    private FeedbackQuestionsLogic() {
        // prevent initialization
    }

    public static FeedbackQuestionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(FeedbackQuestionsDb fqDb, CoursesLogic coursesLogic, FeedbackResponsesLogic frLogic,
                               UsersLogic usersLogic, FeedbackSessionsLogic feedbackSessionsLogic) {
        this.fqDb = fqDb;
        this.coursesLogic = coursesLogic;
        this.frLogic = frLogic;
        this.usersLogic = usersLogic;
        this.feedbackSessionsLogic = feedbackSessionsLogic;
    }

    /**
     * Creates a new feedback question.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     * @throws EntityAlreadyExistsException if the question already exists
     */
    public FeedbackQuestion createFeedbackQuestion(FeedbackQuestion feedbackQuestion)
            throws InvalidParametersException, EntityAlreadyExistsException {
        validateFeedbackQuestion(feedbackQuestion);

        if (fqDb.getFeedbackQuestion(feedbackQuestion.getId()) != null) {
            String errorMessage = String.format(Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS, feedbackQuestion.toString());
            throw new EntityAlreadyExistsException(errorMessage);
        }

        FeedbackQuestion createdQuestion = fqDb.createFeedbackQuestion(feedbackQuestion);

        List<FeedbackQuestion> questionsBefore = getFeedbackQuestionsForSession(feedbackQuestion.getFeedbackSession());
        questionsBefore.remove(createdQuestion);

        adjustQuestionNumbers(questionsBefore.size() + 1, createdQuestion.getQuestionNumber(), questionsBefore);
        return createdQuestion;
    }

    /**
     * Gets an feedback question by feedback question id.
     * @param id of feedback question.
     * @return the specified feedback question.
     */
    public FeedbackQuestion getFeedbackQuestion(UUID id) {
        return fqDb.getFeedbackQuestion(id);
    }

    /**
     * Gets a {@link List} of every FeedbackQuestion in the given session.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForSession(FeedbackSession feedbackSession) {

        List<FeedbackQuestion> questions = fqDb.getFeedbackQuestionsForSession(feedbackSession.getId());
        questions.sort(null);

        // check whether the question numbers are consistent
        if (questions.size() > 1 && !areQuestionNumbersConsistent(questions)) {
            log.severe(feedbackSession.getCourseId() + ": " + feedbackSession.getName()
                    + " has invalid question numbers");
        }

        return questions;
    }

    /**
     * Checks if there are any questions for the given session that instructors can view/submit.
     */
    public boolean hasFeedbackQuestionsForInstructors(List<FeedbackQuestion> fqs, boolean isCreator) {
        boolean hasQuestions = hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.INSTRUCTORS);
        if (hasQuestions) {
            return true;
        }

        if (isCreator) {
            hasQuestions = hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.SELF);
        }

        return hasQuestions;
    }

    /**
     * Gets a {@code List} of all questions for the given session that instructors can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForInstructors(
            FeedbackSession feedbackSession, String userEmail) {
        List<FeedbackQuestion> questions = new ArrayList<>();

        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                    feedbackSession, FeedbackParticipantType.INSTRUCTORS));

        if (SanitizationHelper.areEmailsEqual(feedbackSession.getCreatorEmail(), userEmail)) {
            questions.addAll(
                    fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSession, FeedbackParticipantType.SELF));
        }

        return questions;
    }

    /**
     * Gets a {@code List} of all questions for the given session that students can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForStudents(FeedbackSession feedbackSession) {
        List<FeedbackQuestion> questions = new ArrayList<>();

        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSession, FeedbackParticipantType.STUDENTS));
        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSession, FeedbackParticipantType.TEAMS));
        questions.sort(null);
        return questions;
    }

    /**
     * Updates a feedback question.
     *
     * <p>Cascade adjust the question number of questions in the same session.
     *
     * <p>Cascade adjust the existing response of the question.
     *
     * @return updated feedback question
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback question cannot be found
     */
    public FeedbackQuestion updateFeedbackQuestionCascade(UUID questionId, FeedbackQuestionUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackQuestion question = fqDb.getFeedbackQuestion(questionId);
        if (question == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback question that does not exist.");
        }

        int oldQuestionNumber = question.getQuestionNumber();
        int newQuestionNumber = updateRequest.getQuestionNumber();

        List<FeedbackQuestion> previousQuestionsInSession = new ArrayList<>();
        if (oldQuestionNumber != newQuestionNumber) {
            // get questions in session before update
            previousQuestionsInSession = getFeedbackQuestionsForSession(question.getFeedbackSession());
        }

        // update question
        question.setQuestionNumber(updateRequest.getQuestionNumber());
        question.setDescription(updateRequest.getQuestionDescription());
        question.setQuestionDetails(updateRequest.getQuestionDetails());
        question.setGiverType(updateRequest.getGiverType());
        question.setRecipientType(updateRequest.getRecipientType());
        question.setNumOfEntitiesToGiveFeedbackTo(updateRequest.getNumberOfEntitiesToGiveFeedbackTo());
        question.setShowResponsesTo(updateRequest.getShowResponsesTo());
        question.setShowGiverNameTo(updateRequest.getShowGiverNameTo());
        question.setShowRecipientNameTo(updateRequest.getShowRecipientNameTo());

        // validate questions (giver & recipient)
        String err = question.getQuestionDetailsCopy().validateGiverRecipientVisibility(question);
        if (!err.isEmpty()) {
            throw new InvalidParametersException(err);
        }
        // validate questions (question details)
        FeedbackQuestionDetails questionDetails = question.getQuestionDetailsCopy();
        List<String> questionDetailsErrors = questionDetails.validateQuestionDetails();

        if (!questionDetailsErrors.isEmpty()) {
            throw new InvalidParametersException(questionDetailsErrors.toString());
        }

        if (oldQuestionNumber != newQuestionNumber) {
            // shift other feedback questions (generate an empty "slot")
            adjustQuestionNumbers(oldQuestionNumber, newQuestionNumber, previousQuestionsInSession);
        }

        // adjust responses
        if (question.areResponseDeletionsRequiredForChanges(updateRequest.getGiverType(),
                updateRequest.getRecipientType(), updateRequest.getQuestionDetails())) {
            frLogic.deleteFeedbackResponsesForQuestionCascade(question);
        }

        validateFeedbackQuestion(question);

        return question;
    }

    /**
     * Checks if there are any questions for the given session that students can view/submit.
     */
    public boolean hasFeedbackQuestionsForStudents(List<FeedbackQuestion> fqs) {
        return hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.STUDENTS)
                || hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.TEAMS);
    }

    /**
     * Checks if there is any feedback questions in a session in a course for the given giver type.
     */
    public boolean hasFeedbackQuestionsForGiverType(
            List<FeedbackQuestion> feedbackQuestions, FeedbackParticipantType giverType) {
        assert feedbackQuestions != null;
        assert giverType != null;

        for (FeedbackQuestion fq : feedbackQuestions) {
            if (fq.getGiverType() == giverType) {
                return true;
            }
        }
        return false;
    }

    // TODO can be removed once we are sure that question numbers will be consistent
    private boolean areQuestionNumbersConsistent(List<FeedbackQuestion> questions) {
        Set<Integer> questionNumbersInSession = new HashSet<>();
        for (FeedbackQuestion question : questions) {
            if (!questionNumbersInSession.add(question.getQuestionNumber())) {
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
     * Adjust questions between the old and new number,
     * if the new number is smaller, then shift up (increase qn#) all questions in between.
     * if the new number is bigger, then shift down(decrease qn#) all questions in between.
     */
    private void adjustQuestionNumbers(int oldQuestionNumber, int newQuestionNumber, List<FeedbackQuestion> questions) {
        if (oldQuestionNumber > newQuestionNumber && oldQuestionNumber >= 1) {
            for (int i = oldQuestionNumber - 1; i >= newQuestionNumber; i--) {
                FeedbackQuestion question = questions.get(i - 1);
                question.setQuestionNumber(question.getQuestionNumber() + 1);
            }
        } else if (oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()) {
            for (int i = oldQuestionNumber + 1; i <= newQuestionNumber; i++) {
                FeedbackQuestion question = questions.get(i - 1);
                question.setQuestionNumber(question.getQuestionNumber() - 1);
            }
        }
    }

    /**
     * Gets the dynamically generated options for a question if applicable.
     *
     * <p>
     * This only applies to MCQ and MSQ questions with "generate options for" field.
     *
     * @param feedbackQuestion the question to get the dynamically generated options for
     * @param student the student who is doing the question, or null if the entity doing the question is an instructor
     *
     * @return an Optional containing a list of dynamically generated options, or an empty Optional if not applicable
     */
    public Optional<List<String>> getDynamicallyGeneratedOptions(FeedbackQuestion feedbackQuestion, Student student) {
        FeedbackQuestionType questionType = feedbackQuestion.getQuestionType();
        String courseId = feedbackQuestion.getCourseId();

        return switch (questionType) {
        case MCQ -> {
            FeedbackMcqQuestionDetails feedbackMcqQuestionDetails =
                    (FeedbackMcqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
            yield Optional.ofNullable(
                generateMcqMsqOptions(feedbackMcqQuestionDetails.getGenerateOptionsFor(), student, courseId));
        }
        case MSQ -> {
            FeedbackMsqQuestionDetails feedbackMsqQuestionDetails =
                    (FeedbackMsqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
            yield Optional.ofNullable(
                generateMcqMsqOptions(feedbackMsqQuestionDetails.getGenerateOptionsFor(), student, courseId));
        }
        default -> Optional.empty();
        };
    }

    private void validateFeedbackQuestion(FeedbackQuestion feedbackQuestion) throws InvalidParametersException {
        if (!feedbackQuestion.isValid()) {
            throw new InvalidParametersException(feedbackQuestion.getInvalidityInfo());
        }
    }

    /**
     * Generates the options for MCQ/MSQ questions based on the generateOptionsFor field.
     *
     * @param generateOptionsFor the type of participants to generate options for
     * @param student the student who is doing the question, or null if the entity doing the question is an instructor
     * @param courseId the ID of the course
     * @return a list of generated options, or null if the generateOptionsFor type is NONE or invalid
     */
    private List<String> generateMcqMsqOptions(
            FeedbackParticipantType generateOptionsFor,
            Student student,
            String courseId
    ) {
        return switch (generateOptionsFor) {
        case NONE -> null;
        case STUDENTS -> usersLogic.getStudentsForCourse(courseId)
                .stream()
                .map(s -> s.getName() + " (" + s.getTeamName() + ")")
                .sorted()
                .toList();
        case STUDENTS_IN_SAME_SECTION -> {
            if (student == null) {
                // Instructors have no section.
                yield new ArrayList<>();
            }
            yield usersLogic.getStudentsForSection(student.getSectionName(), courseId)
                    .stream()
                    .map(s -> s.getName() + " (" + s.getTeamName() + ")")
                    .sorted()
                    .toList();
        }
        case STUDENTS_EXCLUDING_SELF -> usersLogic.getStudentsForCourse(courseId)
                .stream()
                .filter(s -> student == null || !s.getId().equals(student.getId()))
                .map(s -> s.getName() + " (" + s.getTeamName() + ")")
                .sorted()
                .toList();
        case TEAMS -> coursesLogic.getTeamsForCourse(courseId)
                .stream()
                .map(Team::getName)
                .sorted()
                .toList();
        case TEAMS_IN_SAME_SECTION -> {
            if (student == null) {
                // Instructors have no section.
                yield new ArrayList<>();
            }
            yield student.getSection().getTeams()
                    .stream()
                    .map(Team::getName)
                    .sorted()
                    .toList();
        }
        case TEAMS_EXCLUDING_SELF -> coursesLogic.getTeamsForCourse(courseId)
                .stream()
                .filter(team -> student == null || !team.getId().equals(student.getTeam().getId()))
                .map(Team::getName)
                .sorted()
                .toList();
        case OWN_TEAM_MEMBERS_INCLUDING_SELF -> {
            if (student == null) {
                // Instructors have no team.
                yield new ArrayList<>();
            }
            yield student.getTeam()
                    .getUsers()
                    .stream()
                    .map(User::getName)
                    .sorted()
                    .toList();
        }
        case OWN_TEAM_MEMBERS -> {
            if (student == null) {
                // Instructors have no team.
                yield new ArrayList<>();
            }
            yield student.getTeam()
                    .getUsers()
                    .stream()
                    .filter(teamMember -> !teamMember.getId().equals(student.getId()))
                    .map(User::getName)
                    .sorted()
                    .toList();
        }
        case INSTRUCTORS -> usersLogic.getInstructorsForCourse(courseId)
                .stream()
                .map(Instructor::getName)
                .sorted()
                .toList();
        default -> {
            assert false : "Invalid generateOptionsFor type: " + generateOptionsFor;
            yield null;
        }
        };
    }

    /**
     * Gets the recipients of a feedback question including recipient section and team.
     *
     * @param question the feedback question
     * @param instructorGiver can be null for student giver
     * @param studentGiver can be null for instructor giver
     * @param courseRoster if provided, the function can be completed without touching database
     * @return a Map of {@code FeedbackQuestionRecipient} as the value and identifier as the key.
     */
    public Map<String, FeedbackQuestionRecipient> getRecipientsOfQuestion(
            FeedbackQuestion question,
            @Nullable Instructor instructorGiver, @Nullable Student studentGiver,
            @Nullable CourseRoster courseRoster) {
        assert instructorGiver != null || studentGiver != null;

        String courseId = question.getCourseId();

        Map<String, FeedbackQuestionRecipient> recipients = new HashMap<>();

        boolean isStudentGiver = studentGiver != null;
        boolean isInstructorGiver = instructorGiver != null;

        String giverEmail = "";
        String giverTeam = "";
        String giverSection = "";
        if (isStudentGiver) {
            giverEmail = studentGiver.getEmail();
            giverTeam = studentGiver.getTeamName();
            giverSection = studentGiver.getSectionName();
        } else if (isInstructorGiver) {
            giverEmail = instructorGiver.getEmail();
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
            giverSection = Const.DEFAULT_SECTION;
        }

        FeedbackParticipantType recipientType = question.getRecipientType();
        FeedbackParticipantType generateOptionsFor = recipientType;

        switch (recipientType) {
        case SELF:
            if (question.getGiverType() == FeedbackParticipantType.TEAMS) {
                recipients.put(giverTeam,
                       new FeedbackQuestionRecipient(giverTeam, giverTeam));
            } else {
                recipients.put(giverEmail,
                        new FeedbackQuestionRecipient(USER_NAME_FOR_SELF, giverEmail));
            }
            break;
        case STUDENTS:
        case STUDENTS_EXCLUDING_SELF:
        case STUDENTS_IN_SAME_SECTION:
            List<Student> studentList;
            if (courseRoster == null) {
                if (generateOptionsFor == FeedbackParticipantType.STUDENTS_IN_SAME_SECTION) {
                    studentList = usersLogic.getStudentsForSection(giverSection, courseId);
                } else {
                    studentList = usersLogic.getStudentsForCourse(courseId);
                }
            } else {
                if (generateOptionsFor == FeedbackParticipantType.STUDENTS_IN_SAME_SECTION) {
                    final String finalGiverSection = giverSection;
                    studentList = courseRoster.getStudents().stream()
                            .filter(studentAttributes -> studentAttributes.getSectionName()
                                    .equals(finalGiverSection)).collect(Collectors.toList());
                } else {
                    studentList = courseRoster.getStudents();
                }
            }
            for (Student student : studentList) {
                if (isInstructorGiver && !instructorGiver.isAllowedForPrivilege(
                        student.getSectionName(), question.getFeedbackSession().getName(),
                        Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)) {
                    // instructor can only see students in allowed sections for him/her
                    continue;
                }
                // Ensure student does not evaluate him/herself if it's STUDENTS_EXCLUDING_SELF or
                // STUDENTS_IN_SAME_SECTION
                if (giverEmail.equals(student.getEmail()) && generateOptionsFor != FeedbackParticipantType.STUDENTS) {
                    continue;
                }
                recipients.put(student.getEmail(), new FeedbackQuestionRecipient(student.getName(), student.getEmail(),
                        student.getSectionName(), student.getTeamName()));
            }
            break;
        case INSTRUCTORS:
            List<Instructor> instructorsInCourse;
            if (courseRoster == null) {
                instructorsInCourse = usersLogic.getInstructorsForCourse(courseId);
            } else {
                instructorsInCourse = courseRoster.getInstructors();
            }
            for (Instructor instr : instructorsInCourse) {
                // remove hidden instructors for students
                if (isStudentGiver && !instr.isDisplayedToStudents()) {
                    continue;
                }
                // Ensure instructor does not evaluate himself
                if (!giverEmail.equals(instr.getEmail())) {
                    recipients.put(instr.getEmail(),
                            new FeedbackQuestionRecipient(instr.getName(), instr.getEmail()));
                }
            }
            break;
        case TEAMS:
        case TEAMS_EXCLUDING_SELF:
        case TEAMS_IN_SAME_SECTION:
            Map<String, List<Student>> teamToTeamMembersTable;
            List<Student> teamStudents;
            if (courseRoster == null) {
                if (generateOptionsFor == FeedbackParticipantType.TEAMS_IN_SAME_SECTION) {
                    teamStudents = usersLogic.getStudentsForSection(giverSection, courseId);
                } else {
                    teamStudents = usersLogic.getStudentsForCourse(courseId);
                }
                teamToTeamMembersTable = CourseRoster.buildTeamToMembersTable(teamStudents);
            } else {
                if (generateOptionsFor == FeedbackParticipantType.TEAMS_IN_SAME_SECTION) {
                    final String finalGiverSection = giverSection;
                    teamStudents = courseRoster.getStudents().stream()
                            .filter(student -> student.getSectionName().equals(finalGiverSection))
                            .collect(Collectors.toList());
                    teamToTeamMembersTable = CourseRoster.buildTeamToMembersTable(teamStudents);
                } else {
                    teamToTeamMembersTable = courseRoster.getTeamToMembersTable();
                }
            }
            for (Map.Entry<String, List<Student>> team : teamToTeamMembersTable.entrySet()) {
                if (isInstructorGiver && !instructorGiver.isAllowedForPrivilege(
                        team.getValue().iterator().next().getSectionName(),
                        question.getFeedbackSession().getName(),
                        Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)) {
                    // instructor can only see teams in allowed sections for him/her
                    continue;
                }
                // Ensure student('s team) does not evaluate own team if it's TEAMS_EXCLUDING_SELF or
                // TEAMS_IN_SAME_SECTION
                if (giverTeam.equals(team.getKey()) && generateOptionsFor != FeedbackParticipantType.TEAMS) {
                    continue;
                }
                // recipientEmail doubles as team name in this case.
                recipients.put(team.getKey(), new FeedbackQuestionRecipient(team.getKey(), team.getKey()));
            }
            break;
        case OWN_TEAM:
            recipients.put(giverTeam, new FeedbackQuestionRecipient(giverTeam, giverTeam));
            break;
        case OWN_TEAM_MEMBERS:
            List<Student> students;
            if (courseRoster == null) {
                students = usersLogic.getStudentsForTeam(giverTeam, courseId);
            } else {
                students = courseRoster.getTeamToMembersTable().getOrDefault(giverTeam, Collections.emptyList());
            }
            for (Student student : students) {
                if (!SanitizationHelper.areEmailsEqual(student.getEmail(), giverEmail)) {
                    recipients.put(student.getEmail(), new FeedbackQuestionRecipient(student.getName(), student.getEmail(),
                            student.getSectionName(), student.getTeamName()));
                }
            }
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            List<Student> teamMembers;
            if (courseRoster == null) {
                teamMembers = usersLogic.getStudentsForTeam(giverTeam, courseId);
            } else {
                teamMembers = courseRoster.getTeamToMembersTable().getOrDefault(giverTeam, Collections.emptyList());
            }
            for (Student student : teamMembers) {
                // accepts self feedback too
                recipients.put(student.getEmail(), new FeedbackQuestionRecipient(student.getName(), student.getEmail(),
                        student.getSectionName(), student.getTeamName()));
            }
            break;
        case NONE:
            recipients.put(Const.GENERAL_QUESTION,
                    new FeedbackQuestionRecipient(Const.GENERAL_QUESTION, Const.GENERAL_QUESTION));
            break;
        default:
            break;
        }
        return recipients;
    }

    /**
     * Returns true if a session has question in a specific giverType.
     */
    public boolean sessionHasQuestionsForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        return fqDb.hasFeedbackQuestionsForGiverType(feedbackSessionName, courseId, giverType);
    }

    /**
     * Returns true if a session has question in either STUDENTS type or TEAMS type.
     */
    public boolean sessionHasQuestionsForStudent(String feedbackSessionName, String courseId) {
        return fqDb.hasFeedbackQuestionsForGiverType(feedbackSessionName, courseId, FeedbackParticipantType.STUDENTS)
                || fqDb.hasFeedbackQuestionsForGiverType(feedbackSessionName, courseId, FeedbackParticipantType.TEAMS);
    }

    /**
     * Deletes a feedback question cascade its responses and comments.
     *
     * <p>Silently fail if question does not exist.
     */
    public void deleteFeedbackQuestionCascade(UUID feedbackQuestionId) {
        FeedbackQuestion questionToDelete = fqDb.getFeedbackQuestion(feedbackQuestionId);
        if (questionToDelete == null) {
            return;
        }

        int questionNumberToDelete = questionToDelete.getQuestionNumber();
        List<FeedbackQuestion> questionsToShiftQnNumber =
                getFeedbackQuestionsForSession(questionToDelete.getFeedbackSession());

        fqDb.deleteFeedbackQuestion(questionToDelete);

        // Shift question numbers down for all questions after the deleted one
        shiftQuestionNumbersDown(questionNumberToDelete, questionsToShiftQnNumber);
    }

    // Shifts all question numbers after questionNumberToShiftFrom down by one.
    private void shiftQuestionNumbersDown(int questionNumberToShiftFrom, List<FeedbackQuestion> questionsToShift) {
        for (FeedbackQuestion question : questionsToShift) {
            if (question.getQuestionNumber() > questionNumberToShiftFrom) {
                question.setQuestionNumber(question.getQuestionNumber() - 1);
            }
        }
    }

    /**
     * Filters the feedback questions in a course, with specified question type.
     * @param courseId the course to search from
     * @param questionType the question type to search on
     * @return a list of filtered questions
     */
    public List<FeedbackQuestion> getFeedbackQuestionForCourseWithType(
            String courseId, FeedbackQuestionType questionType) {
        List<FeedbackSession> feedbackSessions = feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
        List<FeedbackQuestion> feedbackQuestions = new ArrayList<>();

        for (FeedbackSession session : feedbackSessions) {
            feedbackQuestions.addAll(getFeedbackQuestionsForSession(session));
        }

        return feedbackQuestions
                .stream()
                .filter(q -> q.getQuestionType() == questionType)
                .collect(Collectors.toList());
    }

    /**
     * Builds a complete giver to recipient map for a {@code relatedQuestion}.
     *
     * @param relatedQuestion The question to be considered
     * @param courseRoster the roster in the course
     * @return a map from giver to recipient for the question.
     */
    public Map<String, Set<String>> buildCompleteGiverRecipientMap(
            FeedbackQuestion relatedQuestion, CourseRoster courseRoster) {
        Map<String, Set<String>> completeGiverRecipientMap = new HashMap<>();

        List<String> possibleGiverEmails = getPossibleGivers(relatedQuestion, courseRoster);
        for (String possibleGiverEmail : possibleGiverEmails) {
            switch (relatedQuestion.getGiverType()) {
            case STUDENTS:
                Student studentGiver = courseRoster.getStudentForEmail(possibleGiverEmail);
                completeGiverRecipientMap
                        .computeIfAbsent(possibleGiverEmail, key -> new HashSet<>())
                        .addAll(getRecipientsOfQuestion(
                                relatedQuestion, null, studentGiver, courseRoster).keySet());
                break;
            case TEAMS:
                Student oneTeamMember =
                        courseRoster.getTeamToMembersTable().get(possibleGiverEmail).iterator().next();
                completeGiverRecipientMap
                        .computeIfAbsent(possibleGiverEmail, key -> new HashSet<>())
                        .addAll(getRecipientsOfQuestion(
                                relatedQuestion, null, oneTeamMember, courseRoster).keySet());
                break;
            case INSTRUCTORS:
            case SELF:
                Instructor instructorGiver = courseRoster.getInstructorForEmail(possibleGiverEmail);

                // only happens when a session creator quits their course
                if (instructorGiver == null) {
                    instructorGiver = new Instructor(
                            relatedQuestion.getCourse(),
                            USER_NAME_FOR_SELF,
                            possibleGiverEmail,
                            false,
                            USER_NAME_FOR_SELF,
                            null,
                            null
                            );
                }

                completeGiverRecipientMap
                        .computeIfAbsent(possibleGiverEmail, key -> new HashSet<>())
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
     * @param fq the feedback question
     * @param courseRoster roster of all students and instructors
     * @return a list of giver identifier
     */
    private List<String> getPossibleGivers(
            FeedbackQuestion fq, CourseRoster courseRoster) {
        FeedbackParticipantType giverType = fq.getGiverType();
        List<String> possibleGivers = new ArrayList<>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = courseRoster.getStudents()
                    .stream()
                    .map(Student::getEmail)
                    .collect(Collectors.toList());
            break;
        case INSTRUCTORS:
            possibleGivers = courseRoster.getInstructors()
                    .stream()
                    .map(Instructor::getEmail)
                    .collect(Collectors.toList());
            break;
        case TEAMS:
            possibleGivers = new ArrayList<>(courseRoster.getTeamToMembersTable().keySet());
            break;
        case SELF:
            FeedbackSession feedbackSession =
                    feedbackSessionsLogic.getFeedbackSession(fq.getFeedbackSessionName(), fq.getCourseId());
            possibleGivers = Collections.singletonList(feedbackSession.getCreatorEmail());
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        return possibleGivers;
    }

}
