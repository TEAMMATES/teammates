package teammates.client.scripts.sql;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.Instructor;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Class for verifying account attributes.
 */
@SuppressWarnings({ "PMD", "deprecation" })
public class VerifyCourseEntityAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<teammates.storage.entity.Course, Course> {

    public VerifyCourseEntityAttributes() {
        super(teammates.storage.entity.Course.class,
                Course.class);
    }

    @Override
    protected String generateID(Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifyCourseEntityAttributes script = new VerifyCourseEntityAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(Course newCourse, teammates.storage.entity.Course oldCourse) {
        // Refetch course to ensure that the information is upto date
        String courseId = newCourse.getId();
        try {
            boolean isEqual = true;
            HibernateUtil.beginTransaction();
            isEqual = isEqual && verifyCourse(courseId, oldCourse);
            HibernateUtil.commitTransaction();
            
            HibernateUtil.beginTransaction();
            isEqual = isEqual && verifySections(courseId);
            HibernateUtil.commitTransaction();

            HibernateUtil.beginTransaction();
            isEqual = isEqual && verifyTeams(courseId);
            HibernateUtil.commitTransaction();

            HibernateUtil.beginTransaction();
            isEqual = isEqual && verifyStudents(courseId);
            HibernateUtil.commitTransaction();

            HibernateUtil.beginTransaction();
            isEqual = isEqual && verifyFeedbackChain(courseId);
            HibernateUtil.commitTransaction();

            HibernateUtil.beginTransaction();
            isEqual = isEqual && verifyInstructors(newCourse);
            HibernateUtil.commitTransaction();

            HibernateUtil.beginTransaction();
            isEqual = isEqual && verifyDeadlineExtensions(newCourse);
            HibernateUtil.commitTransaction();

            // if (!verifySectionChain(newCourse)) {
            //     logValidationError("Failed section chain verification");
            //     isEqual = false;
            // }

            // if (!verifyFeedbackChain(newCourse)) {
            //     logValidationError("Failed feedback chain verification"); 
            //     isEqual = false;
            // }

            // if (!verifyInstructors(newCourse)) {
            //     logValidationError("Failed instructor verification");
            //     isEqual = false;
            // }

            // if (!verifyDeadlineExtensions(newCourse)) {
            //     logValidationError("Failed deadline extension verification");
            //     isEqual = false;
            // }

            return isEqual;
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            logValidationError("ERROR, IllegalArgumentException " + iae.getMessage());
            HibernateUtil.commitTransaction();
            return false;
        }
    }

    private Course getCourse(String courseId) {
        return HibernateUtil.get(Course.class, courseId);
    }

    private boolean verifyCourse(String courseId, teammates.storage.entity.Course oldCourse) {
        log("Verifying Course attributes");
        Course newCourse = getCourse(courseId);
        boolean isEqual = true;
        if (!verifyCourseEntityAttributes(newCourse, oldCourse)) {
            logValidationError("Failed course verification");
            isEqual = false;
        }
        return isEqual;
    }

    private boolean verifyCourseEntityAttributes(Course sqlEntity, teammates.storage.entity.Course datastoreEntity) {
        return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                && sqlEntity.getName().equals(datastoreEntity.getName())
                && sqlEntity.getTimeZone().equals(datastoreEntity.getTimeZone())
                && sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())
                && sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt())
                && datastoreEntity.getDeletedAt() == null ? sqlEntity.getDeletedAt() == null
                        : sqlEntity.getDeletedAt().equals(datastoreEntity.getDeletedAt());
    }

    // methods for verify section chain  -----------------------------------------------------------------------------------
    // entities: Section, Team, Student

    private boolean verifySections(String courseId) {
        log("Verifying sections");
        // Get datastore sections
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId)
                .list();

        List<Section> newSections = getNewSections(courseId);

        HashSet<String> oldSectionNames = new HashSet<String>();
        HashSet<String> newSectionNames = new HashSet<String>();

        for (CourseStudent oldStudent : oldStudents) {
            oldSectionNames.add(oldStudent.getSectionName());
        }

        for (Section newSection : newSections) {
            newSectionNames.add(newSection.getName());;
        }

        boolean isSectionsCountEqual = newSectionNames.size() == oldSectionNames.size();
        if (!isSectionsCountEqual) {
            logValidationError(String.format("Section chain - section count not equal (%d but expected %d)", newSectionNames.size(),
                oldSectionNames.size()));
            return false;
        }

        if (!newSectionNames.equals(oldSectionNames)) {
            logValidationError(String.format("Section chain - section attributes are not equal"));
            return false;
        }
        return true;
    }

    // private boolean verifyTeams(Section newSection,
    //         Map<String, List<CourseStudent>> teamNameToOldStuMap, Map<String, List<Student>> teamNameToNewStuMap) {

    //     List<Team> newTeams = newSection.getTeams();

    //     boolean isNotTeamCountEqual = newTeams.size() != teamNameToNewStuMap.size()
    //             || newTeams.size() != teamNameToOldStuMap.size();
    //     if (isNotTeamCountEqual) {
    //         logValidationError("Section chain - team count not equal");
    //         return false;
    //     }

    //     return newTeams.stream().allMatch(team -> {
    //         List<CourseStudent> oldTeamStudents = teamNameToOldStuMap.get(team.getName());
    //         List<Student> newTeamStudents = teamNameToNewStuMap.get(team.getName());

    //         // If either of the teamStudent is null,
    //         // then team is not present in the corresponding datastore or sql
    //         // which means a possible migration error
    //         boolean teamNameNotPresent = oldTeamStudents == null || newTeamStudents == null;
    //         if (teamNameNotPresent) {
    //             logValidationError("Section chain - team name not present");
    //             return false;
    //         }
    //         return verifyStudents(oldTeamStudents, newTeamStudents);
    //     });
    // }

    private boolean verifyTeams(String courseId) {
        log("Verifying teams");
       // Assume that team names are unique within a section but not unique among all sections

        // get all datastore students related to course
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId)
                .list();

        // get all teams related to sections
        int numberOldTeams = 0;
        Map<String, HashSet<String>> oldSectionToTeamHashSet = new HashMap<String, HashSet<String>>();
        for (CourseStudent student : oldStudents) {
            String sectionName = student.getSectionName();
            oldSectionToTeamHashSet.putIfAbsent(sectionName, new HashSet<>());
            HashSet<String> teamHashSet = oldSectionToTeamHashSet.get(sectionName);
            boolean addedToSet = teamHashSet.add(student.getTeamName());
            if (addedToSet) {
                numberOldTeams += 1;
            }
        }

        // map team to section
        int numberNewTeams = 0;
        Map<String, HashSet<String>> newSectionToTeamHashSet = new HashMap<String, HashSet<String>>();
        List<Section> newSections = getNewSections(courseId);
        for (Section newSection : newSections) {
            String sectionName = newSection.getName();
            HashSet<String> teamHashSet = new HashSet<>();
            for (Team newTeam : newSection.getTeams()) {
                boolean addedToSet = teamHashSet.add(newTeam.getName());
                if (addedToSet) {
                    numberNewTeams += 1;
                }
            }
            newSectionToTeamHashSet.put(sectionName, teamHashSet);
        }

        boolean isTeamCountEqual = numberNewTeams == numberOldTeams;
        if (!isTeamCountEqual) {
            logValidationError(String.format("Section chain - team count not equal (%d but expected %d)", numberNewTeams, 
                numberOldTeams));
            return false;
        }

        if (!newSectionToTeamHashSet.equals(oldSectionToTeamHashSet)) {
            logValidationError(String.format("Section chain - team attributes are not equal"));
            return false;
        }
        return true;
    }

    private boolean verifyStudents(String courseId) {
        log("Verifying students");
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", courseId)
            .list();

        Map<String, Student> studentIdToStudentMap = new HashMap<String, Student>();

        for (Student newStudent : getNewStudents(courseId)) {
            // Assume that every course have students with unique emails
            studentIdToStudentMap.put(newStudent.getEmail(), newStudent);
        }


        for (CourseStudent oldStudent : oldStudents) {
            Student newStudent = studentIdToStudentMap.get(oldStudent.getEmail());
            if (!verifyStudent(oldStudent, newStudent)) {
                return false;
            }
        }
        return true;
    }

    // private boolean verifySectionChain(Course newCourse) {
    //     // Get old and new students
        
    //     List<Student> newStudents = getNewStudents(newCourse.getId());

    //     // Group students by section
    //     Map<String, List<CourseStudent>> sectionToOldStuMap = oldStudents.stream()
    //             .collect(Collectors.groupingBy(CourseStudent::getSectionName));
    //     Map<String, List<Student>> sectionToNewStuMap = newStudents.stream()
    //             .collect(Collectors.groupingBy(Student::getSectionName));

    //     List<Section> newSections = getNewSections(newCourse.getId());

    //     boolean isNotSectionsCountEqual = newSections.size() != sectionToOldStuMap.size()
    //             || newSections.size() != sectionToNewStuMap.size();
    //     if (isNotSectionsCountEqual) {
    //         logValidationError(String.format("newSection size: %d, sectionToOldStuMap: %d, sectionToOldStuMap: %d", newSections.size(), 
    //             sectionToOldStuMap.size(), sectionToNewStuMap.size()));
    //         logValidationError("Section chain - section count not equal");
    //         return false;
    //     }

    //     return newSections.stream().allMatch(section -> {
    //         List<CourseStudent> oldSectionStudents = sectionToOldStuMap.get(section.getName());
    //         List<Student> newSectionStudents = sectionToNewStuMap.get(section.getName());

    //         // If either of the sectionStudent is null,
    //         // then section is not present in the corresponding datastore or sql
    //         // which means a possible migration error
    //         boolean sectionNameNotPresent = oldSectionStudents == null || newSectionStudents == null;
    //         if (sectionNameNotPresent) {
    //             logValidationError("Section chain - section name not present");
    //             return false;
    //         }

    //         // Group students by team
    //         Map<String, List<CourseStudent>> teamNameToOldStuMap = oldSectionStudents.stream()
    //                 .collect(Collectors.groupingBy(CourseStudent::getTeamName));
    //         Map<String, List<Student>> teamNameToNewStuMap = newSectionStudents.stream()
    //                 .collect(Collectors.groupingBy(Student::getTeamName));
    //         return verifyTeams(section, teamNameToOldStuMap, teamNameToNewStuMap);
    //     });

    // }

    // private boolean verifyTeams(Section newSection,
    //         Map<String, List<CourseStudent>> teamNameToOldStuMap, Map<String, List<Student>> teamNameToNewStuMap) {

    //     List<Team> newTeams = newSection.getTeams();

    //     boolean isNotTeamCountEqual = newTeams.size() != teamNameToNewStuMap.size()
    //             || newTeams.size() != teamNameToOldStuMap.size();
    //     if (isNotTeamCountEqual) {
    //         logValidationError("Section chain - team count not equal");
    //         return false;
    //     }

    //     return newTeams.stream().allMatch(team -> {
    //         List<CourseStudent> oldTeamStudents = teamNameToOldStuMap.get(team.getName());
    //         List<Student> newTeamStudents = teamNameToNewStuMap.get(team.getName());

    //         // If either of the teamStudent is null,
    //         // then team is not present in the corresponding datastore or sql
    //         // which means a possible migration error
    //         boolean teamNameNotPresent = oldTeamStudents == null || newTeamStudents == null;
    //         if (teamNameNotPresent) {
    //             logValidationError("Section chain - team name not present");
    //             return false;
    //         }
    //         return verifyStudents(oldTeamStudents, newTeamStudents);
    //     });
    // }

    // private boolean verifyStudents(
    //         List<CourseStudent> oldTeamStudents, List<Student> newTeamStudents) {
    //     if (oldTeamStudents.size() != newTeamStudents.size()) {
    //         logValidationError("Section chain - number of students not equal");
    //         return false;
    //     }
    //     oldTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
    //     newTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
    //     for (int i = 0; i < oldTeamStudents.size(); i++) {
    //         CourseStudent oldStudent = oldTeamStudents.get(i);
    //         Student newStudent = newTeamStudents.get(i);
    //         if (!verifyStudent(oldStudent, newStudent)) {
    //             logValidationError("Section chain - student failed attribute comparison. Old:" + oldStudent + " New:" + newStudent);
    //             return false;
    //         }
    //     }
    //     return true;
    // }

    

    private boolean verifyStudent(CourseStudent oldStudent,
            Student newStudent) {
        if (!(newStudent.getGoogleId() == null ? newStudent.getGoogleId() == oldStudent.getGoogleId() :
        newStudent.getGoogleId().equals(oldStudent.getGoogleId()))) {
            logValidationError(String.format("Mismatch in google ids. Expected %s but got %s",
                newStudent.getGoogleId(),
                oldStudent.getGoogleId()));
            return false;
        }

        boolean attributesAreEqual = newStudent.getName().equals(oldStudent.getName())
                && newStudent.getEmail().equals(oldStudent.getEmail())
                && newStudent.getComments().equals(oldStudent.getComments())
                && newStudent.getCreatedAt().equals(oldStudent.getCreatedAt())
                && newStudent.getRegKey().equals(oldStudent.getRegistrationKey())
                && newStudent.getCourseId().equals(oldStudent.getCourseId())
                && newStudent.getSectionName().equals(oldStudent.getSectionName())
                && newStudent.getTeamName().equals(oldStudent.getTeamName())
                && (newStudent.getGoogleId() == null ? newStudent.getGoogleId() == oldStudent.getGoogleId() :
                    newStudent.getGoogleId().equals(oldStudent.getGoogleId())
                );

        if (!attributesAreEqual) {
            logValidationError(String.format("Section chain - student attributes are not equal"));
            return false;
        }

        return true;
    }

    // methods for verify feedback chain -----------------------------------------------------------------------------------
    // entities: FeedbackSession, FeedbackQuestion, FeedbackResponse, FeedbackResponseComment

    private boolean verifyFeedbackChain(String courseId) {
        log("Verifying feedback chain");
        Course newCourse = getCourse(courseId);
        List<FeedbackSession> newSessions = newCourse.getFeedbackSessions();
        List<teammates.storage.entity.FeedbackSession> oldSessions = ofy().load().type(teammates.storage.entity.FeedbackSession.class)
                .filter("courseId", newCourse.getId()).list();

        if (newSessions.size() != oldSessions.size()) {
            logValidationError(String.format("Mismatched session counts for course id: %s. Old size: %d, New size: %d", newCourse.getId(), newSessions.size(), oldSessions.size()));
            return false;
        }

        Map<String, teammates.storage.entity.FeedbackSession> sessionNameToOldSessionMap = oldSessions.stream()
                .collect(Collectors.toMap(teammates.storage.entity.FeedbackSession::getFeedbackSessionName, session -> session));

        return newSessions.stream().allMatch(newSession -> {
            teammates.storage.entity.FeedbackSession oldSession = sessionNameToOldSessionMap.get(newSession.getName());
            return verifyFeedbackSession(oldSession, newSession);
        });
    }

    private boolean verifyFeedbackSession(teammates.storage.entity.FeedbackSession oldSession, FeedbackSession newSession) {
        boolean doFieldsMatch = newSession.getCourse().getId().equals(oldSession.getCourseId())
                && newSession.getName().equals(oldSession.getFeedbackSessionName())
                && newSession.getCreatorEmail().equals(oldSession.getCreatorEmail())
                && newSession.getInstructions().equals(SanitizationHelper.sanitizeForRichText(oldSession.getInstructions()))
                && newSession.getStartTime().equals(oldSession.getStartTime())
                && newSession.getEndTime().equals(oldSession.getEndTime())
                && newSession.getSessionVisibleFromTime().equals(oldSession.getSessionVisibleFromTime())
                && newSession.getResultsVisibleFromTime().equals(oldSession.getResultsVisibleFromTime())
                && newSession.getGracePeriod().equals(Duration.ofMinutes(oldSession.getGracePeriod()))
                && newSession.isOpeningEmailEnabled() == oldSession.isOpeningEmailEnabled()
                && newSession.isClosingEmailEnabled() == oldSession.isClosingEmailEnabled()
                && newSession.isOpenEmailSent() == oldSession.isSentOpenEmail()
                && newSession.isOpeningSoonEmailSent() == oldSession.isSentOpeningSoonEmail()
                && newSession.isClosedEmailSent() == oldSession.isSentClosedEmail()
                && newSession.isClosingSoonEmailSent() == oldSession.isSentClosingEmail()
                && newSession.isPublishedEmailSent() == oldSession.isSentPublishedEmail()
                && newSession.isPublishedEmailEnabled() == oldSession.isPublishedEmailEnabled()
                && newSession.getCreatedAt().equals(oldSession.getCreatedTime())
                && (newSession.getDeletedAt() == oldSession.getDeletedTime()
                        || newSession.getDeletedAt().equals(oldSession.getDeletedTime()));
        if (!doFieldsMatch) {
            logValidationError(String.format("Mismatched fields for session: %s, course id: %s",
                    oldSession.getFeedbackSessionName(), oldSession.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackQuestion> newQuestions = newSession.getFeedbackQuestions();
        List<FeedbackQuestion> oldQuestions = ofy().load().type(FeedbackQuestion.class)
                .filter("courseId", newSession.getCourse().getId())
                .filter("feedbackSessionName", newSession.getName()).list();

        if (newQuestions.size() != oldQuestions.size()) {
            logValidationError(String.format("Mismatched question counts for session: %s, course id: %s",
                    oldSession.getFeedbackSessionName(), oldSession.getCourseId()));
            return false;
        }

        Map<Integer, FeedbackQuestion> questionNumberToOldQuestionMap = oldQuestions.stream()
                .collect(Collectors.toMap(FeedbackQuestion::getQuestionNumber, question -> question));

        return newQuestions.stream().allMatch(newQuestion -> {
            FeedbackQuestion oldQuestion = questionNumberToOldQuestionMap.get(newQuestion.getQuestionNumber());
            return verifyFeedbackQuestion(oldQuestion, newQuestion);
        });
    }

    private boolean verifyFeedbackQuestion(FeedbackQuestion oldQuestion,
            teammates.storage.sqlentity.FeedbackQuestion newQuestion) {
        boolean doFieldsMatch = newQuestion.getQuestionNumber() == oldQuestion.getQuestionNumber()
                && newQuestion.getDescription().equals(oldQuestion.getQuestionDescription())
                && newQuestion.getGiverType().equals(oldQuestion.getGiverType())
                && newQuestion.getRecipientType().equals(oldQuestion.getRecipientType())
                && newQuestion.getNumOfEntitiesToGiveFeedbackTo().equals(oldQuestion.getNumberOfEntitiesToGiveFeedbackTo())
                && newQuestion.getShowResponsesTo().equals(oldQuestion.getShowResponsesTo())
                && newQuestion.getShowGiverNameTo().equals(oldQuestion.getShowGiverNameTo())
                && newQuestion.getShowRecipientNameTo().equals(oldQuestion.getShowRecipientNameTo())
                && newQuestion.getQuestionDetailsCopy().getJsonString().equals(oldQuestion.getQuestionText())
                && newQuestion.getCreatedAt().equals(oldQuestion.getCreatedAt());
        if (!doFieldsMatch) {
            logValidationError(String.format("Mismatched fields for question %s, session: %s, course id: %s",
                    oldQuestion.getQuestionNumber(), oldQuestion.getFeedbackSessionName(), oldQuestion.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackResponse> newResponses = newQuestion.getFeedbackResponses();
        List<FeedbackResponse> oldResponses = ofy().load().type(FeedbackResponse.class)
                .filter("feedbackQuestionId", oldQuestion.getId()).list();

        if (newResponses.size() != oldResponses.size()) {
            logValidationError(String.format("Mismatched response counts for question. New: %d, Old: %d, %s, session: %s, course id: %s",
                    newResponses.size(), oldResponses.size(),
                    oldQuestion.getQuestionNumber(), oldQuestion.getFeedbackSessionName(), oldQuestion.getCourseId()));
            return false;
        }

        Map<String, FeedbackResponse> responseIdToOldResponseMap = oldResponses.stream()
                .collect(Collectors.toMap(FeedbackResponse::getId, response -> response));

        boolean responsesAreEqual = newResponses.stream().allMatch(newResponse -> {
            String oldResponseId = FeedbackResponse.generateId(oldQuestion.getId(), newResponse.getGiver(),
                    newResponse.getRecipient());
            FeedbackResponse oldResponse = responseIdToOldResponseMap.get(oldResponseId);
            return verifyFeedbackResponse(oldResponse, newResponse);
        });

        return responsesAreEqual;
    }

    private boolean verifyFeedbackResponse(FeedbackResponse oldResponse,
            teammates.storage.sqlentity.FeedbackResponse newResponse) {
        boolean allFieldsMatch = newResponse.getGiver().equals(oldResponse.getGiverEmail())
                && newResponse.getGiverSection().getCourse().getId().equals(oldResponse.getCourseId())
                && newResponse.getGiverSectionName().equals(oldResponse.getGiverSection())
                && newResponse.getRecipient().equals(oldResponse.getRecipientEmail())
                && newResponse.getRecipientSectionName().equals(oldResponse.getRecipientSection())
                && newResponse.getCreatedAt().equals(oldResponse.getCreatedAt())
                && newResponse.getFeedbackResponseDetailsCopy().getJsonString().equals(oldResponse.getAnswer());
        if (!allFieldsMatch) {
            logValidationError(String.format("Mismatched fields for response %s, question %s, session: %s, course id: %s",
                    oldResponse.getId(), oldResponse.getFeedbackQuestionId(), oldResponse.getFeedbackSessionName(),
                    oldResponse.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackResponseComment> newComments = newResponse.getFeedbackResponseComments();
        List<FeedbackResponseComment> oldComments = ofy().load()
                .type(teammates.storage.entity.FeedbackResponseComment.class)
                .filter("feedbackResponseId", oldResponse.getId()).list();

        if (newComments.size() != oldComments.size()) {
            logValidationError(String.format("Mismatched comment counts for response %s, question %s, session: %s, course id: %s",
                    oldResponse.getId(), oldResponse.getFeedbackQuestionId(), oldResponse.getFeedbackSessionName(),
                    oldResponse.getCourseId()));
            return false;
        }

        boolean allCommentFieldsMatch = oldComments.stream().allMatch(oldComment -> newComments.stream()
                .anyMatch(newComment -> verifyFeedbackResponseComment(oldComment, newComment)));
        if (!allCommentFieldsMatch) {
            logValidationError(String.format("Mismatched fields for comments in response %s, question %s, session: %s, course id: %s",
                    oldResponse.getId(), oldResponse.getFeedbackQuestionId(), oldResponse.getFeedbackSessionName(),
                    oldResponse.getCourseId()));
            return false;
        }

        return true;
    }

    private boolean verifyFeedbackResponseComment(FeedbackResponseComment oldComment,
            teammates.storage.sqlentity.FeedbackResponseComment newComment) {
        return newComment.getGiver().equals(oldComment.getGiverEmail())
                && newComment.getCommentText().equals(oldComment.getCommentText())
                && newComment.getGiverType().equals(oldComment.getCommentGiverType())
                && newComment.getGiverSection().getCourse().getId().equals(oldComment.getCourseId())
                && newComment.getGiverSection().getName().equals(oldComment.getGiverSection())
                && newComment.getRecipientSection().getName().equals(oldComment.getReceiverSection())
                && newComment.getIsVisibilityFollowingFeedbackQuestion()
                        == oldComment.getIsVisibilityFollowingFeedbackQuestion()
                && newComment.getIsCommentFromFeedbackParticipant() == oldComment.getIsCommentFromFeedbackParticipant()
                && newComment.getShowCommentTo().equals(oldComment.getShowCommentTo())
                && newComment.getShowGiverNameTo().equals(oldComment.getShowGiverNameTo())
                && newComment.getCreatedAt().equals(oldComment.getCreatedAt())
                && newComment.getLastEditorEmail().equals(oldComment.getLastEditorEmail());
    }

    // Verify Instructor ----------------------------
    private boolean verifyInstructors(Course newCourse) {
        List<teammates.storage.sqlentity.Instructor> newInstructors = getNewInstructors(newCourse.getId());
        List<Instructor> oldInstructors = ofy().load().type(Instructor.class).filter("courseId", newCourse.getId())
                .list();

        if (oldInstructors.size() != newInstructors.size()) {
            logValidationError("Feedback chain - Instructor counts not equal");
            return false;
        }

        newInstructors.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        oldInstructors.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        for (int i = 0; i < oldInstructors.size(); i++) {
            Instructor oldInstructor = oldInstructors.get(i);
            teammates.storage.sqlentity.Instructor newInstructor = newInstructors.get(i);
            if (!verifyInstructor(oldInstructor, newInstructor)) {
                logValidationError("Feedback chain - Instructor attributes failed comparison");
                return false;
            }
        }
        return true;
    }

    private boolean verifyInstructor(Instructor oldInstructor,
            teammates.storage.sqlentity.Instructor newInstructor) {
        InstructorPrivileges oldPrivileges;
        if (oldInstructor.getInstructorPrivilegesAsText() == null) {
            oldPrivileges = new InstructorPrivileges(oldInstructor.getRole());
        } else {
            InstructorPrivilegesLegacy privilegesLegacy = JsonUtils
                    .fromJson(oldInstructor.getInstructorPrivilegesAsText(), InstructorPrivilegesLegacy.class);
            oldPrivileges = new InstructorPrivileges(privilegesLegacy);
        }

        return newInstructor.getName().equals(oldInstructor.getName())
                && newInstructor.getEmail().equals(oldInstructor.getEmail())
                && newInstructor.getRole().getRoleName().equals(oldInstructor.getRole())
                && newInstructor.getRegKey().equals(oldInstructor.getRegistrationKey())
                && newInstructor.getDisplayName().equals(oldInstructor.getDisplayedName())
                && newInstructor.getPrivileges().equals(oldPrivileges)
                && newInstructor.isDisplayedToStudents() == oldInstructor.isDisplayedToStudents()
                && newInstructor.getCreatedAt().equals(oldInstructor.getCreatedAt())
                // && newInstructor.getUpdatedAt().equals(oldInstructor.getUpdatedAt());
                && (newInstructor.getGoogleId() == null ? newInstructor.getGoogleId() == oldInstructor.getGoogleId() :
                    newInstructor.getGoogleId().equals(oldInstructor.getGoogleId()));
    }

    // Verify DeadlineExtensions ----------------------------
    private boolean verifyDeadlineExtensions(Course newCourse) {
        List<teammates.storage.sqlentity.DeadlineExtension> newDeadlineExt = getNewDeadlineExtensions(newCourse.getId());
        List<DeadlineExtension> oldDeadlineExt = ofy().load()
                .type(DeadlineExtension.class).filter("courseId", newCourse.getId()).list();

        if (oldDeadlineExt.size() != newDeadlineExt.size()) {
            logValidationError("Deadline extension size not equal");

            return false;
        }

        newDeadlineExt.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        oldDeadlineExt.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));

        for (int i = 0; i < oldDeadlineExt.size(); i++) {
            DeadlineExtension oldDeadline = oldDeadlineExt.get(i);
            teammates.storage.sqlentity.DeadlineExtension newDeadline = newDeadlineExt.get(i);
            if (!verifyDeadlineExtension(oldDeadline, newDeadline)) {
                logValidationError("Deadline extension failed comparison");
                // logValidationError(String.format("Expected oldDeadline with feedback name %s, userEmail %s, endTime %s, closingEmailSent %b, createdAt %s",
                // oldDeadline.getFeedbackSessionName(),                 
                //     oldDeadline.getUserEmail(),
                //     oldDeadline.getEndTime(),
                //     oldDeadline.getSentClosingEmail(),
                //     oldDeadline.getCreatedAt()));
                // logValidationError(String.format("Expected oldDeadline with feedback name %s, userEmail %s, endTime %s, closingEmailSent %b, createdAt %s",
                //     newDeadline.getFeedbackSession().getName(),
                //     newDeadline.getUser().getEmail(),
                //     newDeadline.getEndTime(),
                //     newDeadline.isClosingSoonEmailSent(),
                //     newDeadline.getCreatedAt()));
                return false;
            }
        }
        return true;
    }

    private boolean verifyDeadlineExtension(DeadlineExtension oldDeadline,
            teammates.storage.sqlentity.DeadlineExtension newDeadline) {
        return newDeadline.getFeedbackSession().getName().equals(oldDeadline.getFeedbackSessionName())
                && newDeadline.getUser().getEmail().equals(oldDeadline.getUserEmail())
                && newDeadline.getEndTime().equals(oldDeadline.getEndTime())
                && newDeadline.isClosingSoonEmailSent() == oldDeadline.getSentClosingEmail()
                // && newDeadline.getUpdatedAt().equals(oldDeadline.getUpdatedAt())
                && newDeadline.getCreatedAt().equals(oldDeadline.getCreatedAt());
    }

    // Verify Get methods ----------------------------
    private List<Student> getNewStudents(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Student> cr = cb
                .createQuery(teammates.storage.sqlentity.Student.class);
        Root<teammates.storage.sqlentity.Student> studentRoot = cr.from(teammates.storage.sqlentity.Student.class);
        cr.select(studentRoot).where(cb.equal(studentRoot.get("courseId"), courseId));
        List<Student> newStudents = HibernateUtil.createQuery(cr).getResultList();
        return newStudents;
    }

    private List<Section> getNewSections(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Section> cr = cb
                .createQuery(teammates.storage.sqlentity.Section.class);
        Root<teammates.storage.sqlentity.Section> sectionRoot = cr.from(teammates.storage.sqlentity.Section.class);
        cr.select(sectionRoot).where(cb.equal(sectionRoot.get("course").get("id"), courseId));
        List<Section> newSections = HibernateUtil.createQuery(cr).getResultList();
        return newSections;
    }

    private List<Section> getnew(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Section> cr = cb
                .createQuery(teammates.storage.sqlentity.Section.class);
        Root<teammates.storage.sqlentity.Section> sectionRoot = cr.from(teammates.storage.sqlentity.Section.class);
        cr.select(sectionRoot).where(cb.equal(sectionRoot.get("course").get("id"), courseId));
        List<Section> newSections = HibernateUtil.createQuery(cr).getResultList();
        return newSections;
    }

    private List<teammates.storage.sqlentity.Instructor> getNewInstructors(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Instructor> cr = cb
                .createQuery(teammates.storage.sqlentity.Instructor.class);
        Root<teammates.storage.sqlentity.Instructor> instructorRoot = cr.from(teammates.storage.sqlentity.Instructor.class);
        cr.select(instructorRoot).where(cb.equal(instructorRoot.get("courseId"), courseId));
        List<teammates.storage.sqlentity.Instructor> newInstructors = HibernateUtil.createQuery(cr).getResultList();
        return newInstructors;
    }

    private List<teammates.storage.sqlentity.DeadlineExtension> getNewDeadlineExtensions(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.DeadlineExtension> cr = cb
                .createQuery(teammates.storage.sqlentity.DeadlineExtension.class);
        Root<teammates.storage.sqlentity.DeadlineExtension> deadlineExtensionsRoot = cr
                .from(teammates.storage.sqlentity.DeadlineExtension.class);
        cr.select(deadlineExtensionsRoot).where(cb.equal(deadlineExtensionsRoot.get("user").get("courseId"), courseId));
        List<teammates.storage.sqlentity.DeadlineExtension> newDeadlineExt = HibernateUtil.createQuery(cr)
                .getResultList();
        return newDeadlineExt;
    }
}
