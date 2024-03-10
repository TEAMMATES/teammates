package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
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

    private static final FeedbackQuestionsLogic instance = new FeedbackQuestionsLogic();

    private final FeedbackQuestionsDb fqDb = FeedbackQuestionsDb.inst();

    private CoursesLogic coursesLogic;
    private FeedbackResponsesLogic frLogic;
    private FeedbackSessionsLogic fsLogic;
    private InstructorsLogic instructorsLogic;
    private StudentsLogic studentsLogic;

    private FeedbackQuestionsLogic() {
        // prevent initialization
    }

    public static FeedbackQuestionsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        coursesLogic = CoursesLogic.inst();
        frLogic = FeedbackResponsesLogic.inst();
        fsLogic = FeedbackSessionsLogic.inst();
        instructorsLogic = InstructorsLogic.inst();
        studentsLogic = StudentsLogic.inst();
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
     * database and has an ID already generated.
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

    /**
     * Filters the feedback questions in a course, with specified question type.
     * @param courseId the course to search from
     * @param questionType the question type to search on
     * @return a list of filtered questions
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionForCourseWithType(
            String courseId, FeedbackQuestionType questionType) {
        List<FeedbackSessionAttributes> feedbackSessions = fsLogic.getFeedbackSessionsForCourse(courseId);
        List<FeedbackQuestionAttributes> feedbackQuestions = new ArrayList<>();
        for (FeedbackSessionAttributes session : feedbackSessions) {
            feedbackQuestions.addAll(getFeedbackQuestionsForSession(session.getFeedbackSessionName(), courseId));
        }
        return feedbackQuestions.stream().filter(q -> q.getQuestionType().equals(questionType)).collect(Collectors.toList());
    }

    // TODO can be removed once we are sure that question numbers will be consistent
    private boolean areQuestionNumbersConsistent(List<FeedbackQuestionAttributes> questions) {
        Set<Integer> questionNumbersInSession = new HashSet<>();
        for (FeedbackQuestionAttributes question : questions) {
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
     * Checks if there are any questions for the given session that instructors can view/submit.
     */
    public boolean hasFeedbackQuestionsForInstructors(
            FeedbackSessionAttributes fsa, boolean isCreator) {
        boolean hasQuestions = fqDb.hasFeedbackQuestionsForGiverType(
                fsa.getFeedbackSessionName(), fsa.getCourseId(), FeedbackParticipantType.INSTRUCTORS);
        if (hasQuestions) {
            return true;
        }

        if (isCreator) {
            hasQuestions = fqDb.hasFeedbackQuestionsForGiverType(
                    fsa.getFeedbackSessionName(), fsa.getCourseId(), FeedbackParticipantType.SELF);
        }

        return hasQuestions;
    }

    /**
     * Gets a {@code List} of all questions for the given session that instructors can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructors(
            String feedbackSessionName, String courseId, String userEmail) {
        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, FeedbackParticipantType.INSTRUCTORS));

        if (userEmail != null && fsLogic.isCreatorOfSession(feedbackSessionName, courseId, userEmail)) {
            questions.addAll(
                    fqDb.getFeedbackQuestionsForGiverType(
                            feedbackSessionName, courseId, FeedbackParticipantType.SELF));
        }

        questions.sort(null);
        return questions;
    }

    /**
     * Filters through the given list of questions and returns a {@code List} of
     * questions that instructors can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructors(
            List<FeedbackQuestionAttributes> allQuestions, boolean isCreator) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        for (FeedbackQuestionAttributes question : allQuestions) {
            if (question.getGiverType() == FeedbackParticipantType.INSTRUCTORS
                    || question.getGiverType() == FeedbackParticipantType.SELF && isCreator) {
                questions.add(question);
            }
        }

        return questions;
    }

    /**
     * Checks if there are any questions for the given session that students can view/submit.
     */
    public boolean hasFeedbackQuestionsForStudents(FeedbackSessionAttributes fsa) {
        return fqDb.hasFeedbackQuestionsForGiverType(
                fsa.getFeedbackSessionName(), fsa.getCourseId(), FeedbackParticipantType.STUDENTS)
                || fqDb.hasFeedbackQuestionsForGiverType(
                        fsa.getFeedbackSessionName(), fsa.getCourseId(), FeedbackParticipantType.TEAMS);
    }

    /**
     * Gets a {@code List} of all questions for the given session that students can view/submit.
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
            if (question.getGiverType() == FeedbackParticipantType.STUDENTS
                    || question.getGiverType() == FeedbackParticipantType.TEAMS) {
                questions.add(question);
            }
        }

        return questions;
    }

    /**
     * Returns true if a session has question in either STUDENTS type or TEAMS type.
     */
    public boolean sessionHasQuestions(String feedbackSessionName, String courseId) {
        return fqDb.hasFeedbackQuestionsForGiverType(feedbackSessionName, courseId, FeedbackParticipantType.STUDENTS)
                || fqDb.hasFeedbackQuestionsForGiverType(feedbackSessionName, courseId, FeedbackParticipantType.TEAMS);
    }

    /**
     * Returns true if a session has question in a specific giverType.
     */
    public boolean sessionHasQuestionsForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        return fqDb.hasFeedbackQuestionsForGiverType(feedbackSessionName, courseId, giverType);
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
            FeedbackQuestionAttributes question,
            @Nullable InstructorAttributes instructorGiver, @Nullable StudentAttributes studentGiver,
            @Nullable CourseRoster courseRoster) {
        assert instructorGiver != null || studentGiver != null;

        Map<String, FeedbackQuestionRecipient> recipients = new HashMap<>();

        boolean isStudentGiver = studentGiver != null;
        boolean isInstructorGiver = instructorGiver != null;

        String giverEmail = "";
        String giverTeam = "";
        String giverSection = "";
        if (isStudentGiver) {
            giverEmail = studentGiver.getEmail();
            giverTeam = studentGiver.getTeam();
            giverSection = studentGiver.getSection();
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
            List<StudentAttributes> studentList;
            if (courseRoster == null) {
                if (generateOptionsFor == FeedbackParticipantType.STUDENTS_IN_SAME_SECTION) {
                    studentList = studentsLogic.getStudentsForSection(giverSection, question.getCourseId());
                } else {
                    studentList = studentsLogic.getStudentsForCourse(question.getCourseId());
                }
            } else {
                if (generateOptionsFor == FeedbackParticipantType.STUDENTS_IN_SAME_SECTION) {
                    final String finalGiverSection = giverSection;
                    studentList = courseRoster.getStudents().stream()
                            .filter(studentAttributes -> studentAttributes.getSection()
                                    .equals(finalGiverSection)).collect(Collectors.toList());
                } else {
                    studentList = courseRoster.getStudents();
                }
            }
            for (StudentAttributes student : studentList) {
                if (isInstructorGiver && !instructorGiver.isAllowedForPrivilege(
                        student.getSection(), question.getFeedbackSessionName(),
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
                        student.getSection(), student.getTeam()));
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorsInCourse;
            if (courseRoster == null) {
                instructorsInCourse = instructorsLogic.getInstructorsForCourse(question.getCourseId());
            } else {
                instructorsInCourse = courseRoster.getInstructors();
            }
            for (InstructorAttributes instr : instructorsInCourse) {
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
            Map<String, List<StudentAttributes>> teamToTeamMembersTable;
            List<StudentAttributes> teamStudents;
            if (courseRoster == null) {
                if (generateOptionsFor == FeedbackParticipantType.TEAMS_IN_SAME_SECTION) {
                    teamStudents = studentsLogic.getStudentsForSection(giverSection, question.getCourseId());
                } else {
                    teamStudents = studentsLogic.getStudentsForCourse(question.getCourseId());
                }
                teamToTeamMembersTable = CourseRoster.buildTeamToMembersTable(teamStudents);
            } else {
                if (generateOptionsFor == FeedbackParticipantType.TEAMS_IN_SAME_SECTION) {
                    final String finalGiverSection = giverSection;
                    teamStudents = courseRoster.getStudents().stream()
                            .filter(student -> student.getSection().equals(finalGiverSection))
                            .collect(Collectors.toList());
                    teamToTeamMembersTable = CourseRoster.buildTeamToMembersTable(teamStudents);
                } else {
                    teamToTeamMembersTable = courseRoster.getTeamToMembersTable();
                }
            }
            for (Map.Entry<String, List<StudentAttributes>> team : teamToTeamMembersTable.entrySet()) {
                if (isInstructorGiver && !instructorGiver.isAllowedForPrivilege(
                        team.getValue().iterator().next().getSection(),
                        question.getFeedbackSessionName(),
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
            List<StudentAttributes> students;
            if (courseRoster == null) {
                students = studentsLogic.getStudentsForTeam(giverTeam, question.getCourseId());
            } else {
                students = courseRoster.getTeamToMembersTable().getOrDefault(giverTeam, Collections.emptyList());
            }
            for (StudentAttributes student : students) {
                if (!student.getEmail().equals(giverEmail)) {
                    recipients.put(student.getEmail(), new FeedbackQuestionRecipient(student.getName(), student.getEmail(),
                            student.getSection(), student.getTeam()));
                }
            }
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            List<StudentAttributes> teamMembers;
            if (courseRoster == null) {
                teamMembers = studentsLogic.getStudentsForTeam(giverTeam, question.getCourseId());
            } else {
                teamMembers = courseRoster.getTeamToMembersTable().getOrDefault(giverTeam, Collections.emptyList());
            }
            for (StudentAttributes student : teamMembers) {
                // accepts self feedback too
                recipients.put(student.getEmail(), new FeedbackQuestionRecipient(student.getName(), student.getEmail(),
                        student.getSection(), student.getTeam()));
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
     * Builds a complete giver to recipient map for a {@code relatedQuestion}.
     *
     * @param relatedQuestion The question to be considered
     * @param courseRoster the roster in the course
     * @return a map from giver to recipient for the question.
     */
    public Map<String, Set<String>> buildCompleteGiverRecipientMap(
            FeedbackQuestionAttributes relatedQuestion, CourseRoster courseRoster) {
        Map<String, Set<String>> completeGiverRecipientMap = new HashMap<>();

        List<String> possibleGivers = getPossibleGivers(relatedQuestion, courseRoster);
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

                // only happens when a session creator quits their course
                if (instructorGiver == null) {
                    instructorGiver =
                            InstructorAttributes
                                    .builder(relatedQuestion.getCourseId(), possibleGiver)
                                    .build();
                }

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
            FeedbackQuestionAttributes fqa, CourseRoster courseRoster) {
        FeedbackParticipantType giverType = fqa.getGiverType();
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
            FeedbackSessionAttributes feedbackSession =
                    fsLogic.getFeedbackSession(fqa.getFeedbackSessionName(), fqa.getCourseId());
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
                    (FeedbackMcqQuestionDetails) feedbackQuestionAttributes.getQuestionDetailsCopy();
            optionList = feedbackMcqQuestionDetails.getMcqChoices();
            generateOptionsFor = feedbackMcqQuestionDetails.getGenerateOptionsFor();
        } else if (feedbackQuestionAttributes.getQuestionType() == FeedbackQuestionType.MSQ) {
            FeedbackMsqQuestionDetails feedbackMsqQuestionDetails =
                    (FeedbackMsqQuestionDetails) feedbackQuestionAttributes.getQuestionDetailsCopy();
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
        case STUDENTS_IN_SAME_SECTION:
        case STUDENTS_EXCLUDING_SELF:
            List<StudentAttributes> studentList;
            if (generateOptionsFor == FeedbackParticipantType.STUDENTS_IN_SAME_SECTION) {
                String courseId = feedbackQuestionAttributes.getCourseId();
                StudentAttributes studentAttributes =
                        studentsLogic.getStudentForEmail(courseId, emailOfEntityDoingQuestion);
                studentList = studentsLogic.getStudentsForSection(studentAttributes.getSection(), courseId);
            } else {
                studentList = studentsLogic.getStudentsForCourse(feedbackQuestionAttributes.getCourseId());
            }

            if (generateOptionsFor == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF) {
                studentList.removeIf(studentInList -> studentInList.getEmail().equals(emailOfEntityDoingQuestion));
            }

            for (StudentAttributes student : studentList) {
                optionList.add(student.getName() + " (" + student.getTeam() + ")");
            }

            optionList.sort(null);
            break;
        case TEAMS:
        case TEAMS_IN_SAME_SECTION:
        case TEAMS_EXCLUDING_SELF:
            try {
                List<String> teams;
                if (generateOptionsFor == FeedbackParticipantType.TEAMS_IN_SAME_SECTION) {
                    String courseId = feedbackQuestionAttributes.getCourseId();
                    StudentAttributes studentAttributes =
                            studentsLogic.getStudentForEmail(courseId, emailOfEntityDoingQuestion);
                    teams = coursesLogic.getTeamsForSection(studentAttributes.getSection(), courseId);
                } else {
                    teams = coursesLogic.getTeamsForCourse(feedbackQuestionAttributes.getCourseId());
                }

                if (generateOptionsFor == FeedbackParticipantType.TEAMS_EXCLUDING_SELF) {
                    teams.removeIf(team -> team.equals(teamOfEntityDoingQuestion));
                }

                for (String team : teams) {
                    optionList.add(team);
                }

                optionList.sort(null);
            } catch (EntityDoesNotExistException e) {
                assert false : "Course disappeared";
            }
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
        case OWN_TEAM_MEMBERS:
            if (teamOfEntityDoingQuestion != null) {
                List<StudentAttributes> teamMembers = studentsLogic.getStudentsForTeam(teamOfEntityDoingQuestion,
                        feedbackQuestionAttributes.getCourseId());

                if (generateOptionsFor == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                    teamMembers.removeIf(teamMember -> teamMember.getEmail().equals(emailOfEntityDoingQuestion));
                }

                teamMembers.forEach(teamMember -> optionList.add(teamMember.getName()));

                optionList.sort(null);
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
            assert false : "Trying to generate options for neither students, teams nor instructors";
            break;
        }

        if (feedbackQuestionAttributes.getQuestionType() == FeedbackQuestionType.MCQ) {
            FeedbackMcqQuestionDetails feedbackMcqQuestionDetails =
                    (FeedbackMcqQuestionDetails) feedbackQuestionAttributes.getQuestionDetailsCopy();
            feedbackMcqQuestionDetails.setMcqChoices(optionList);
            feedbackQuestionAttributes.setQuestionDetails(feedbackMcqQuestionDetails);
        } else if (feedbackQuestionAttributes.getQuestionType() == FeedbackQuestionType.MSQ) {
            FeedbackMsqQuestionDetails feedbackMsqQuestionDetails =
                    (FeedbackMsqQuestionDetails) feedbackQuestionAttributes.getQuestionDetailsCopy();
            feedbackMsqQuestionDetails.setMsqChoices(optionList);
            feedbackQuestionAttributes.setQuestionDetails(feedbackMsqQuestionDetails);
        }
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
        int oldQuestionNumber = oldQuestion.getQuestionNumber();
        int newQuestionNumber = newQuestion.getQuestionNumber();

        List<FeedbackQuestionAttributes> previousQuestionsInSession = new ArrayList<>();
        if (oldQuestionNumber != newQuestionNumber) {
            // get questions in session before update
            String feedbackSessionName = oldQuestion.getFeedbackSessionName();
            String courseId = oldQuestion.getCourseId();
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
                                    .withQuestionNumber(question.getQuestionNumber() + 1)
                                    .build());
                }
            } else if (oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()) {
                for (int i = oldQuestionNumber + 1; i <= newQuestionNumber; i++) {
                    FeedbackQuestionAttributes question = questions.get(i - 1);
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                                    .withQuestionNumber(question.getQuestionNumber() - 1)
                                    .build());
                }
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            assert false : "Adjusting question number should not cause: " + e.getMessage();
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
        if (questionToDelete.getQuestionNumber() < questionsToShiftQnNumber.size()) {
            shiftQuestionNumbersDown(questionToDelete.getQuestionNumber(), questionsToShiftQnNumber);
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
            if (question.getQuestionNumber() > questionNumberToShiftFrom) {
                try {
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                            .withQuestionNumber(question.getQuestionNumber() - 1)
                            .build());
                } catch (InvalidParametersException | EntityDoesNotExistException e) {
                    assert false : "Shifting question number should not cause: " + e.getMessage();
                }
            }
        }
    }

}
