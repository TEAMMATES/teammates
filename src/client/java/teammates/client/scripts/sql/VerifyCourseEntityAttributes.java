package teammates.client.scripts.sql;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.Instructor;
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
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Course> {

    public VerifyCourseEntityAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Course.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifyCourseEntityAttributes script = new VerifyCourseEntityAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course newCourse, Course oldCourse) {
        try {
            HibernateUtil.beginTransaction();
            newCourse = HibernateUtil.get(teammates.storage.sqlentity.Course.class, newCourse.getId());
            boolean isEqual = true;
            if (!verifyCourse(newCourse, oldCourse)) {
                log("Failed course verification");
                isEqual = false;
            }

            if (!verifySectionChain(newCourse)) {
                log("Failed section chain verification");
                isEqual = false;
            }

            if (!verifyFeedbackChain(newCourse)) {
                log("Failed feedback chain verification"); 
                isEqual = false;
            }

            if (!verifyInstructors(newCourse)) {
                log("Failed instructor verification");
                isEqual = false;
            }

            if (!verifyDeadlineExtensions(newCourse)) {
                log("Failed deadline extension verification");
                isEqual = false;
            }

            HibernateUtil.commitTransaction();
            return isEqual;
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            log("ERROR, IllegalArgumentException " + iae.getMessage());
            HibernateUtil.commitTransaction();
            return false;
        }
    }

    private boolean verifyCourse(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
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

    private boolean verifySectionChain(teammates.storage.sqlentity.Course newCourse) {
        // Get old and new students
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", newCourse.getId())
                .list();
        List<Student> newStudents = getNewStudents(newCourse.getId());

        // Group students by section
        Map<String, List<CourseStudent>> sectionToOldStuMap = oldStudents.stream()
                .collect(Collectors.groupingBy(CourseStudent::getSectionName));
        Map<String, List<Student>> sectionToNewStuMap = newStudents.stream()
                .collect(Collectors.groupingBy(Student::getSectionName));

        List<Section> newSections = getNewSections(newCourse.getId());

        boolean isNotSectionsCountEqual = newSections.size() != sectionToOldStuMap.size()
                || newSections.size() != sectionToNewStuMap.size();
        if (isNotSectionsCountEqual) {
            log(String.format("newSection size: %d, sectionToOldStuMap: %d, sectionToOldStuMap: %d", newSections.size(), 
                sectionToOldStuMap.size(), sectionToNewStuMap.size()));
            log("Section chain - section count not equal");
            return false;
        }

        return newSections.stream().allMatch(section -> {
            List<CourseStudent> oldSectionStudents = sectionToOldStuMap.get(section.getName());
            List<Student> newSectionStudents = sectionToNewStuMap.get(section.getName());

            // If either of the sectionStudent is null,
            // then section is not present in the corresponding datastore or sql
            // which means a possible migration error
            boolean sectionNameNotPresent = oldSectionStudents == null || newSectionStudents == null;
            if (sectionNameNotPresent) {
                log("Section chain - section name not present");
                return false;
            }

            // Group students by team
            Map<String, List<CourseStudent>> teamNameToOldStuMap = oldSectionStudents.stream()
                    .collect(Collectors.groupingBy(CourseStudent::getTeamName));
            Map<String, List<Student>> teamNameToNewStuMap = newSectionStudents.stream()
                    .collect(Collectors.groupingBy(Student::getTeamName));
            return verifyTeams(section, teamNameToOldStuMap, teamNameToNewStuMap);
        });

    }

    private boolean verifyTeams(Section newSection,
            Map<String, List<CourseStudent>> teamNameToOldStuMap, Map<String, List<Student>> teamNameToNewStuMap) {

        List<Team> newTeams = newSection.getTeams();

        boolean isNotTeamCountEqual = newTeams.size() != teamNameToNewStuMap.size()
                || newTeams.size() != teamNameToOldStuMap.size();
        if (isNotTeamCountEqual) {
            log("Section chain - team count not equal");
            return false;
        }

        return newTeams.stream().allMatch(team -> {
            List<CourseStudent> oldTeamStudents = teamNameToOldStuMap.get(team.getName());
            List<Student> newTeamStudents = teamNameToNewStuMap.get(team.getName());

            // If either of the teamStudent is null,
            // then team is not present in the corresponding datastore or sql
            // which means a possible migration error
            boolean teamNameNotPresent = oldTeamStudents == null || newTeamStudents == null;
            if (teamNameNotPresent) {
                log("Section chain - team name not present");
                return false;
            }
            return verifyStudents(oldTeamStudents, newTeamStudents);
        });
    }

    private boolean verifyStudents(
            List<CourseStudent> oldTeamStudents, List<Student> newTeamStudents) {
        if (oldTeamStudents.size() != newTeamStudents.size()) {
            log("Section chain - number of students not equal");
            return false;
        }
        oldTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        newTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        for (int i = 0; i < oldTeamStudents.size(); i++) {
            CourseStudent oldStudent = oldTeamStudents.get(i);
            Student newStudent = newTeamStudents.get(i);
            if (!verifyStudent(oldStudent, newStudent)) {
                log("Section chain - student failed attribute comparison. Old:" + oldStudent + " New:" + newStudent);
                return false;
            }
        }
        return true;
    }

    private boolean verifyStudent(CourseStudent oldStudent,
            Student newStudent) {
        if (!(newStudent.getGoogleId() == null ? newStudent.getGoogleId() == oldStudent.getGoogleId() :
        newStudent.getGoogleId().equals(oldStudent.getGoogleId()))) {
            log("Mismatch in google ids " + newStudent.getGoogleId() + "  " + oldStudent.getGoogleId());
        }

        return newStudent.getName().equals(oldStudent.getName())
                && newStudent.getEmail().equals(oldStudent.getEmail())
                && newStudent.getComments().equals(oldStudent.getComments())
                && newStudent.getUpdatedAt().equals(oldStudent.getUpdatedAt())
                && newStudent.getCreatedAt().equals(oldStudent.getCreatedAt())
                && newStudent.getRegKey().equals(oldStudent.getRegistrationKey())
                && (newStudent.getGoogleId() == null ? newStudent.getGoogleId() == oldStudent.getGoogleId() :
                    newStudent.getGoogleId().equals(oldStudent.getGoogleId())
                );
    }

    // methods for verify feedback chain -----------------------------------------------------------------------------------
    // entities: FeedbackSession, FeedbackQuestion, FeedbackResponse, FeedbackResponseComment

    private boolean verifyFeedbackChain(teammates.storage.sqlentity.Course newCourse) {
        List<teammates.storage.sqlentity.FeedbackSession> newSessions = newCourse.getFeedbackSessions();
        List<FeedbackSession> oldSessions = ofy().load().type(FeedbackSession.class)
                .filter("courseId", newCourse.getId()).list();

        if (newSessions.size() != oldSessions.size()) {
            log(String.format("Mismatched session counts for course id: %s. Old size: %d, New size: %d", newCourse.getId(), newSessions.size(), oldSessions.size()));
            return false;
        }

        Map<String, FeedbackSession> sessionNameToOldSessionMap = oldSessions.stream()
                .collect(Collectors.toMap(FeedbackSession::getFeedbackSessionName, session -> session));

        return newSessions.stream().allMatch(newSession -> {
            FeedbackSession oldSession = sessionNameToOldSessionMap.get(newSession.getName());
            return verifyFeedbackSession(oldSession, newSession);
        });
    }

    private boolean verifyFeedbackSession(FeedbackSession oldSession, teammates.storage.sqlentity.FeedbackSession newSession) {
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
                && newSession.getCreatedAt().equals(oldSession.getCreatedTime())
                && (newSession.getDeletedAt() == oldSession.getDeletedTime()
                        || newSession.getDeletedAt().equals(oldSession.getDeletedTime()));
        if (!doFieldsMatch) {
            log(String.format("Mismatched fields for session: %s, course id: %s",
                    oldSession.getFeedbackSessionName(), oldSession.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackQuestion> newQuestions = newSession.getFeedbackQuestions();
        List<FeedbackQuestion> oldQuestions = ofy().load().type(FeedbackQuestion.class)
                .filter("courseId", newSession.getCourse().getId())
                .filter("feedbackSessionName", newSession.getName()).list();

        if (newQuestions.size() != oldQuestions.size()) {
            log(String.format("Mismatched question counts for session: %s, course id: %s",
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
                && newQuestion.getCreatedAt().equals(oldQuestion.getCreatedAt())
                && newQuestion.getUpdatedAt().equals(oldQuestion.getUpdatedAt());
        if (!doFieldsMatch) {
            log(String.format("Mismatched fields for question %s, session: %s, course id: %s",
                    oldQuestion.getQuestionNumber(), oldQuestion.getFeedbackSessionName(), oldQuestion.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackResponse> newResponses = newQuestion.getFeedbackResponses();
        List<FeedbackResponse> oldResponses = ofy().load().type(FeedbackResponse.class)
                .filter("feedbackQuestionId", oldQuestion.getId()).list();

        if (newResponses.size() != oldResponses.size()) {
            log(String.format("Mismatched response counts for question. New: %d, Old: %d, %s, session: %s, course id: %s",
                    newResponses.size(), oldResponses.size(),
                    oldQuestion.getQuestionNumber(), oldQuestion.getFeedbackSessionName(), oldQuestion.getCourseId()));
            return false;
        }

        Map<String, FeedbackResponse> responseIdToOldResponseMap = oldResponses.stream()
                .collect(Collectors.toMap(FeedbackResponse::getId, response -> response));

        return newResponses.stream().allMatch(newResponse -> {
            String oldResponseId = FeedbackResponse.generateId(oldQuestion.getId(), newResponse.getGiver(),
                    newResponse.getRecipient());
            FeedbackResponse oldResponse = responseIdToOldResponseMap.get(oldResponseId);
            return verifyFeedbackResponse(oldResponse, newResponse);
        });
    }

    private boolean verifyFeedbackResponse(FeedbackResponse oldResponse,
            teammates.storage.sqlentity.FeedbackResponse newResponse) {
        boolean allFieldsMatch = newResponse.getGiver().equals(oldResponse.getGiverEmail())
                && newResponse.getGiverSection().getCourse().getId().equals(oldResponse.getCourseId())
                && newResponse.getGiverSectionName().equals(oldResponse.getGiverSection())
                && newResponse.getRecipient().equals(oldResponse.getRecipientEmail())
                && newResponse.getRecipientSectionName().equals(oldResponse.getRecipientSection())
                && newResponse.getCreatedAt().equals(oldResponse.getCreatedAt())
                && newResponse.getUpdatedAt().equals(oldResponse.getUpdatedAt())
                && newResponse.getFeedbackResponseDetailsCopy().getJsonString().equals(oldResponse.getAnswer());
        if (!allFieldsMatch) {
            log(String.format("Mismatched fields for response %s, question %s, session: %s, course id: %s",
                    oldResponse.getId(), oldResponse.getFeedbackQuestionId(), oldResponse.getFeedbackSessionName(),
                    oldResponse.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackResponseComment> newComments = newResponse.getFeedbackResponseComments();
        List<FeedbackResponseComment> oldComments = ofy().load()
                .type(teammates.storage.entity.FeedbackResponseComment.class)
                .filter("feedbackResponseId", oldResponse.getId()).list();

        if (newComments.size() != oldComments.size()) {
            log(String.format("Mismatched comment counts for response %s, question %s, session: %s, course id: %s",
                    oldResponse.getId(), oldResponse.getFeedbackQuestionId(), oldResponse.getFeedbackSessionName(),
                    oldResponse.getCourseId()));
            return false;
        }

        boolean allCommentFieldsMatch = oldComments.stream().allMatch(oldComment -> newComments.stream()
                .anyMatch(newComment -> verifyFeedbackResponseComment(oldComment, newComment)));
        if (!allCommentFieldsMatch) {
            log(String.format("Mismatched fields for comments in response %s, question %s, session: %s, course id: %s",
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
                && newComment.getUpdatedAt().equals(oldComment.getLastEditedAt())
                && newComment.getLastEditorEmail().equals(oldComment.getLastEditorEmail());
    }

    // Verify Instructor ----------------------------
    private boolean verifyInstructors(teammates.storage.sqlentity.Course newCourse) {
        List<teammates.storage.sqlentity.Instructor> newInstructors = getNewInstructors(newCourse.getId());
        List<Instructor> oldInstructors = ofy().load().type(Instructor.class).filter("courseId", newCourse.getId())
                .list();

        if (oldInstructors.size() != newInstructors.size()) {
            log("Feedback chain - Instructor counts not equal");
            return false;
        }

        newInstructors.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        oldInstructors.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        for (int i = 0; i < oldInstructors.size(); i++) {
            Instructor oldInstructor = oldInstructors.get(i);
            teammates.storage.sqlentity.Instructor newInstructor = newInstructors.get(i);
            if (!verifyInstructor(oldInstructor, newInstructor)) {
                log("Feedback chain - Instructor attributes failed comparison");
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
                && newInstructor.getUpdatedAt().equals(oldInstructor.getUpdatedAt())
                && (newInstructor.getGoogleId() == null ? newInstructor.getGoogleId() == oldInstructor.getGoogleId() :
                    newInstructor.getGoogleId().equals(oldInstructor.getGoogleId()));
    }

    // Verify DeadlineExtensions ----------------------------
    private boolean verifyDeadlineExtensions(teammates.storage.sqlentity.Course newCourse) {
        List<teammates.storage.sqlentity.DeadlineExtension> newDeadlineExt = getNewDeadlineExtensions(newCourse.getId());
        List<DeadlineExtension> oldDeadlineExt = ofy().load()
                .type(DeadlineExtension.class).filter("courseId", newCourse.getId()).list();

        if (oldDeadlineExt.size() != newDeadlineExt.size()) {
            log("Deadline extension size not equal");

            return false;
        }

        newDeadlineExt.sort((a, b) -> a.getId().compareTo(b.getId()));
        oldDeadlineExt.sort((a, b) -> a.getId().compareTo(b.getId()));

        for (int i = 0; i < oldDeadlineExt.size(); i++) {
            DeadlineExtension oldDeadline = oldDeadlineExt.get(i);
            teammates.storage.sqlentity.DeadlineExtension newDeadline = newDeadlineExt.get(i);
            if (!verifyDeadlineExtension(oldDeadline, newDeadline)) {
                log("Deadline extension failed comparison");
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
                && newDeadline.getUpdatedAt().equals(oldDeadline.getUpdatedAt())
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
        cr.select(sectionRoot).where(cb.equal(sectionRoot.get("courseId"), courseId));
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
