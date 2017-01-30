package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.storage.api.CommentsDb;

/**
 * Handles operations related to student comments.
 * 
 * @see {@link CommentAttributes}
 * @see {@link CommentsDb}
 */
public final class CommentsLogic {
    
    private static CommentsLogic instance = new CommentsLogic();

    private static final CommentsDb commentsDb = new CommentsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private CommentsLogic() {
        // prevent initialization
    }
    
    public static CommentsLogic inst() {
        return instance;
    }
    
    /************ CRUD ************/

    public CommentAttributes createComment(CommentAttributes comment)
           throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        verifyIsCoursePresent(comment.courseId, "create");
        verifyIsInstructorOfCourse(comment.courseId, comment.giverEmail);

        return commentsDb.createEntity(comment);
    }
    
    public CommentAttributes getComment(Long commentId) {
        return commentsDb.getComment(commentId);
    }

    public CommentAttributes getComment(CommentAttributes comment) {
        return commentsDb.getComment(comment);
    }

    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail)
           throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        return commentsDb.getCommentsForGiver(courseId, giverEmail);
    }
    
    public List<CommentAttributes> getCommentsForReceiver(String courseId,
                                                          String giverEmail,
                                                          CommentParticipantType recipientType,
                                                          String receiverEmail)
                                                          throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        List<CommentAttributes> comments = commentsDb.getCommentsForReceiver(courseId, recipientType, receiverEmail);
        Iterator<CommentAttributes> iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes c = iterator.next();
            if (!c.giverEmail.equals(giverEmail)) {
                iterator.remove();
            }
        }
        return comments;
    }

    public List<CommentAttributes> getCommentsForReceiver(String courseId,
                                                          CommentParticipantType recipientType,
                                                          String receiverEmail)
                                                          throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        return commentsDb.getCommentsForReceiver(courseId, recipientType, receiverEmail);
    }
    
    /**
     * Gets comments for a particular receiver, then filters out comments that the instructor cannot see.
     * @param courseId
     * @param recipientType
     * @param receiverEmail
     * @param instructorEmail
     * @return List of comments visible to the instructor, directed at the receiver.
     * @throws EntityDoesNotExistException
     */
    public List<CommentAttributes> getCommentsForReceiverVisibleToInstructor(
            String courseId, CommentParticipantType recipientType, String receiverEmail, String instructorEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        verifyIsInstructorOfCourse(courseId, instructorEmail);
        List<CommentAttributes> commentsFromDb =
                commentsDb.getCommentsForReceiver(courseId, recipientType, receiverEmail);
        Iterator<CommentAttributes> iterator = commentsFromDb.iterator();
        List<CommentAttributes> comments = new LinkedList<CommentAttributes>();
        HashSet<String> commentsVisitedSet = new HashSet<String>();
        boolean canViewCommentsFromOthers =
                canViewCommentsFromOthers(courseId, recipientType, receiverEmail, instructorEmail);
        
        // add in the instructor's own comments to the list
        while (iterator.hasNext()) {
            CommentAttributes c = iterator.next();
            if (c.giverEmail.equals(instructorEmail)) {
                comments.add(c);
                preventAppendingThisCommentAgain(commentsVisitedSet, c);
                iterator.remove();
            } else if (!canViewCommentsFromOthers) {
                iterator.remove();
            }
        }
        
        // add in other instructor's comments, but only if they are visible to instructors
        removeNonVisibleCommentsForInstructor(commentsFromDb, commentsVisitedSet, comments);
        
        // remove comments if the receiver is now anonymous
        iterator = comments.iterator();
        while (iterator.hasNext()) {
            CommentAttributes c = iterator.next();
            String firstRecipient = null;
            if (!c.recipients.isEmpty()) {
                firstRecipient = c.recipients.iterator().next();
            }
            // ANONYMOUS_RECEIVER is guaranteed to be the only recipient if the receiver name is hidden
            if (firstRecipient == null || Const.DISPLAYED_NAME_FOR_ANONYMOUS_COMMENT_PARTICIPANT.equals(firstRecipient)) {
                iterator.remove();
            }
        }
        return comments;
    }

    public List<CommentAttributes> getCommentsForSendingState(String courseId, CommentSendingState sendingState)
           throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        return commentsDb.getCommentsForSendingState(courseId, sendingState);
    }
    
    public void updateCommentsSendingState(String courseId, CommentSendingState oldState, CommentSendingState newState)
           throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "clear pending");
        commentsDb.updateComments(courseId, oldState, newState);
    }
    
    public CommentAttributes updateComment(CommentAttributes comment)
           throws InvalidParametersException, EntityDoesNotExistException {
        verifyIsCoursePresent(comment.courseId, "update");
        
        return commentsDb.updateComment(comment);
    }
    
    /**
     * updates comment's giver and last editor email (assume to be an instructor)
     * @param courseId
     * @param oldInstrEmail
     * @param updatedInstrEmail
     */
    public void updateInstructorEmail(String courseId, String oldInstrEmail, String updatedInstrEmail) {
        commentsDb.updateInstructorEmail(courseId, oldInstrEmail, updatedInstrEmail);
    }
    
    /**
     * update comment's recipient email (assume to be a student)
     * @param courseId
     * @param oldStudentEmail
     * @param updatedStudentEmail
     */
    public void updateStudentEmail(String courseId, String oldStudentEmail, String updatedStudentEmail) {
        commentsDb.updateStudentEmail(courseId, oldStudentEmail, updatedStudentEmail);
    }
    
    public void deleteCommentsForInstructor(String courseId, String instructorEmail) {
        commentsDb.deleteCommentsByInstructorEmail(courseId, instructorEmail);
    }
    
    public void deleteCommentsForStudent(String courseId, String studentEmail) {
        commentsDb.deleteCommentsByStudentEmail(courseId, studentEmail);
    }
    
    public void deleteCommentsForTeam(String courseId, String teamName) {
        commentsDb.deleteCommentsForTeam(courseId, teamName);
    }
    
    public void deleteCommentsForSection(String courseId, String sectionName) {
        commentsDb.deleteCommentsForSection(courseId, sectionName);
    }
    
    public void deleteCommentsForCourse(String courseId) {
        commentsDb.deleteCommentsForCourse(courseId);
    }
    
    public void deleteCommentAndDocument(CommentAttributes comment) {
        this.deleteComment(comment);
        this.deleteDocument(comment);
    }
    
    public void deleteComment(CommentAttributes comment) {
        commentsDb.deleteEntity(comment);
    }
    
    public void deleteDocument(CommentAttributes comment) {
        commentsDb.deleteDocument(comment);
    }
    
    public List<CommentAttributes> getCommentDrafts(String giverEmail) {
        return commentsDb.getCommentDrafts(giverEmail);
    }
    
    /**
     * Create or update document for comment
     * @param comment
     */
    public void putDocument(CommentAttributes comment) {
        commentsDb.putDocument(comment);
    }
    
    public CommentSearchResultBundle searchComment(String queryString, List<InstructorAttributes> instructors) {
        return commentsDb.search(queryString, instructors);
    }
    
    private void verifyIsCoursePresent(String courseId, String action) throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to " + action + " comments for a course that does not exist.");
        }
    }
    
    private void verifyIsInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException {
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if (instructor == null) {
            throw new EntityDoesNotExistException(
                    "User " + email + " is not a registered instructor for course " + courseId + ".");
        }
    }
    
    /************ Get Comments For an Instructor ************/
    
    /**
     * Get comments visible for the given instructor
     * @param instructor
     * @return list of {@link CommentAttributes}
     * @throws EntityDoesNotExistException when the course doesn't exist
     */
    public List<CommentAttributes> getCommentsForInstructor(InstructorAttributes instructor)
           throws EntityDoesNotExistException {
        verifyIsCoursePresent(instructor.courseId, "get");
        verifyIsInstructorOfCourse(instructor.courseId, instructor.email);
        HashSet<String> commentsVisitedSet = new HashSet<String>();
        
        //When the given instructor is the comment giver,
        List<CommentAttributes> comments = getCommentsForGiverAndStatus(instructor.courseId,
                                                                        instructor.email,
                                                                        CommentStatus.FINAL);
        for (CommentAttributes c : comments) {
            preventAppendingThisCommentAgain(commentsVisitedSet, c);
        }
        
        //When other giver's comments are visible to the given instructor
        List<CommentAttributes> commentsForOtherInstructor = getCommentsForCommentViewer(instructor.courseId,
                                                                 CommentParticipantType.INSTRUCTOR);
        removeNonVisibleCommentsForInstructor(commentsForOtherInstructor, commentsVisitedSet, comments);
        
        java.util.Collections.sort(comments);
        
        return comments;
    }
    
    private void removeNonVisibleCommentsForInstructor(List<CommentAttributes> commentsForInstructor,
                                                       HashSet<String> commentsVisitedSet,
                                                       List<CommentAttributes> comments) {
        for (CommentAttributes c : commentsForInstructor) {
            removeGiverAndRecipientNameByVisibilityOptions(c, CommentParticipantType.INSTRUCTOR);
            appendComments(c, comments, commentsVisitedSet);
        }
    }
    
    private List<CommentAttributes> getCommentsForCommentViewer(String courseId,
                                                                CommentParticipantType commentViewerType)
                                                                throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        return commentsDb.getCommentsForCommentViewer(courseId, commentViewerType);
    }
    
    private List<CommentAttributes> getCommentsForGiverAndStatus(String courseId,
                                                                 String giverEmail,
                                                                 CommentStatus status)
                                                                 throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId, "get");
        return commentsDb.getCommentsForGiverAndStatus(courseId, giverEmail, status);
    }
    
    /************ Get Comments For a Student ************/
    
    /**
     * Get comments visible to the given student
     * @param student
     * @return list of {@link CommentAttributes}
     * @throws EntityDoesNotExistException when the course doesn't exist
     */
    public List<CommentAttributes> getCommentsForStudent(StudentAttributes student)
           throws EntityDoesNotExistException {
        verifyIsCoursePresent(student.course, "get");
        List<StudentAttributes> teammates = studentsLogic.getStudentsForTeam(student.team, student.course);
        List<StudentAttributes> studentsInTheSameSection = studentsLogic.getStudentsForSection(student.section,
                                                                                               student.course);
        List<String> teammatesEmails = getTeammatesEmails(teammates);
        List<String> sectionStudentsEmails = getSectionStudentsEmails(studentsInTheSameSection);
        List<String> teamsInThisSection = getTeamsForSection(studentsInTheSameSection);

        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        HashSet<String> commentsVisitedSet = new HashSet<String>();
        
        //Get comments sent to the given student
        List<CommentAttributes> commentsForStudent = getCommentsForReceiver(student.course,
                                                                            CommentParticipantType.PERSON,
                                                                            student.email);
        removeNonVisibleCommentsForStudent(commentsForStudent, commentsVisitedSet, comments);
        
        //Get comments visible to the given student's teammates
        List<CommentAttributes> commentsForTeam = getCommentsForCommentViewer(student.course,
                                                                              CommentParticipantType.TEAM);
        removeNonVisibleCommentsForTeam(commentsForTeam, student, teammatesEmails, commentsVisitedSet, comments);
        
        //Get comments visible to the given student's section
        List<CommentAttributes> commentsForSection = getCommentsForCommentViewer(student.course,
                                                                                 CommentParticipantType.SECTION);
        removeNonVisibleCommentsForSection(commentsForSection, student, teammatesEmails,
                                           sectionStudentsEmails, teamsInThisSection,
                                           commentsVisitedSet, comments);
        
        //Get comments visible to the whole course
        List<CommentAttributes> commentsForCourse = getCommentsForCommentViewer(student.course,
                                                                                CommentParticipantType.COURSE);
        removeNonVisibleCommentsForCourse(commentsForCourse, student, teammatesEmails,
                                          sectionStudentsEmails, teamsInThisSection,
                                          commentsVisitedSet, comments);
        
        java.util.Collections.sort(comments);
        
        return comments;
    }
    
    private List<String> getTeamsForSection(List<StudentAttributes> studentsInTheSameSection) {
        List<String> teams = new ArrayList<String>();
        for (StudentAttributes stu : studentsInTheSameSection) {
            teams.add(stu.team);
        }
        return teams;
    }

    private List<String> getSectionStudentsEmails(List<StudentAttributes> studentsInTheSameSection) {
        List<String> sectionStudentsEmails = new ArrayList<String>();
        for (StudentAttributes stu : studentsInTheSameSection) {
            sectionStudentsEmails.add(stu.email);
        }
        return sectionStudentsEmails;
    }
    
    private List<String> getTeammatesEmails(List<StudentAttributes> teammates) {
        List<String> teammatesEmails = new ArrayList<String>();
        for (StudentAttributes teammate : teammates) {
            teammatesEmails.add(teammate.email);
        }
        return teammatesEmails;
    }

    private void removeNonVisibleCommentsForCourse(List<CommentAttributes> commentsForCourse, StudentAttributes student,
                                                   List<String> teammates, List<String> sectionStudentsEmails,
                                                   List<String> teamsInThisSection, HashSet<String> commentsVisitedSet,
                                                   List<CommentAttributes> comments) {
        removeNonVisibleCommentsForSection(commentsForCourse, student, teammates,
                                           sectionStudentsEmails, teamsInThisSection,
                                           commentsVisitedSet, comments);
        
        for (CommentAttributes c : commentsForCourse) {
            if (c.courseId.equals(student.course)) {
                if (c.recipientType == CommentParticipantType.COURSE) {
                    removeGiverNameByVisibilityOptions(c, CommentParticipantType.COURSE);
                } else {
                    removeGiverAndRecipientNameByVisibilityOptions(c, CommentParticipantType.COURSE);
                }
                appendComments(c, comments, commentsVisitedSet);
            }
        }
    }
    
    private void removeNonVisibleCommentsForSection(List<CommentAttributes> commentsForSection,
            StudentAttributes student, List<String> teammatesEmails, List<String> sectionStudentsEmails,
            List<String> teamsInThisSection, HashSet<String> commentsVisitedSet, List<CommentAttributes> comments) {
        removeNonVisibleCommentsForTeam(commentsForSection, student, teammatesEmails, commentsVisitedSet, comments);
        
        for (CommentAttributes c : commentsForSection) {
            //for teammates
            if (c.recipientType == CommentParticipantType.PERSON
                    && isCommentRecipientsWithinGroup(sectionStudentsEmails, c)) {
                if (c.showCommentTo.contains(CommentParticipantType.SECTION)) {
                    removeGiverAndRecipientNameByVisibilityOptions(c, CommentParticipantType.SECTION);
                    appendComments(c, comments, commentsVisitedSet);
                } else {
                    preventAppendingThisCommentAgain(commentsVisitedSet, c);
                }
            //for team
            } else if (c.recipientType == CommentParticipantType.TEAM
                       && isCommentRecipientsWithinGroup(teamsInThisSection, c)) {
                if (c.showCommentTo.contains(CommentParticipantType.SECTION)) {
                    removeGiverNameByVisibilityOptions(c, CommentParticipantType.SECTION);
                    appendComments(c, comments, commentsVisitedSet);
                } else {
                    preventAppendingThisCommentAgain(commentsVisitedSet, c);
                }
            //for section
            } else if (c.recipientType == CommentParticipantType.SECTION && c.recipients.contains(student.section)) {
                if (c.showCommentTo.contains(CommentParticipantType.SECTION)) {
                    removeGiverNameByVisibilityOptions(c, CommentParticipantType.SECTION);
                    appendComments(c, comments, commentsVisitedSet);
                } else {
                    preventAppendingThisCommentAgain(commentsVisitedSet, c);
                }
            }
        }
    }
    
    private void removeNonVisibleCommentsForTeam(List<CommentAttributes> commentsForTeam, StudentAttributes student,
                                                 List<String> teammates, HashSet<String> commentsVisitedSet,
                                                 List<CommentAttributes> comments) {
        for (CommentAttributes c : commentsForTeam) {
            //for teammates
            if (c.recipientType == CommentParticipantType.PERSON && isCommentRecipientsWithinGroup(teammates, c)) {
                if (c.showCommentTo.contains(CommentParticipantType.TEAM)) {
                    removeGiverAndRecipientNameByVisibilityOptions(c, CommentParticipantType.TEAM);
                    appendComments(c, comments, commentsVisitedSet);
                } else {
                    preventAppendingThisCommentAgain(commentsVisitedSet, c);
                }
            //for team
            } else if (c.recipientType == CommentParticipantType.TEAM
                       && c.recipients.contains(Sanitizer.sanitizeForHtml(student.team))) {
                if (c.showCommentTo.contains(CommentParticipantType.TEAM)) {
                    removeGiverNameByVisibilityOptions(c, CommentParticipantType.TEAM);
                    appendComments(c, comments, commentsVisitedSet);
                } else {
                    preventAppendingThisCommentAgain(commentsVisitedSet, c);
                }
            }
        }
    }

    private void removeNonVisibleCommentsForStudent(List<CommentAttributes> commentsForStudent,
                                                    HashSet<String> commentsVisitedSet,
                                                    List<CommentAttributes> comments) {
        for (CommentAttributes c : commentsForStudent) {
            if (c.showCommentTo.contains(CommentParticipantType.PERSON)) {
                removeGiverNameByVisibilityOptions(c, CommentParticipantType.PERSON);
                appendComments(c, comments, commentsVisitedSet);
            } else {
                preventAppendingThisCommentAgain(commentsVisitedSet, c);
            }
        }
    }
    
    private boolean canViewCommentsFromOthers(String courseId, CommentParticipantType recipientType,
                                              String receiverEmail, String instructorEmail) {
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, instructorEmail);
        if (CommentParticipantType.PERSON.equals(recipientType)) {
            StudentAttributes student = studentsLogic.getStudentForEmail(courseId, receiverEmail);
            if (student != null) {
                return instructor.isAllowedForPrivilege(
                        student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS);
            }
        } else if (CommentParticipantType.TEAM.equals(recipientType)) {
            String sectionName = studentsLogic.getSectionForTeam(courseId, receiverEmail);
            return instructor.isAllowedForPrivilege(
                    sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS);
        } else if (CommentParticipantType.SECTION.equals(recipientType)) {
            return instructor.isAllowedForPrivilege(
                    receiverEmail, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS);
        }
        return false;
    }
    
    private void removeGiverNameByVisibilityOptions(CommentAttributes c, CommentParticipantType viewerType) {
        if (!c.showGiverNameTo.contains(viewerType)) {
            c.giverEmail = Const.DISPLAYED_NAME_FOR_ANONYMOUS_COMMENT_PARTICIPANT;
        }
    }

    private void removeGiverAndRecipientNameByVisibilityOptions(CommentAttributes c,
                                                                CommentParticipantType viewerType) {
        removeGiverNameByVisibilityOptions(c, viewerType);
        if (!c.showRecipientNameTo.contains(viewerType)) {
            c.recipients = new HashSet<String>();
            c.recipients.add(Const.DISPLAYED_NAME_FOR_ANONYMOUS_COMMENT_PARTICIPANT);
        }
    }
    
    private void appendComments(CommentAttributes c,
                                List<CommentAttributes> toThisCommentList,
                                HashSet<String> commentsVisitedSet) {
        if (!commentsVisitedSet.contains(c.getCommentId().toString())) {
            toThisCommentList.add(c);
            preventAppendingThisCommentAgain(commentsVisitedSet, c);
        }
    }
    
    private void preventAppendingThisCommentAgain(HashSet<String> commentsVisitedSet, CommentAttributes c) {
        commentsVisitedSet.add(c.getCommentId().toString());
    }

    private boolean isCommentRecipientsWithinGroup(List<String> group, CommentAttributes c) {
        for (String recipient : c.recipients) {
            if (group.contains(recipient)) {
                return true;
            }
        }
        return false;
    }
    
    /************ Send Email For Pending Comments ************/
    
    /**
     * Get recipient emails for comments with sending state.
     * When pending comments are cleared, they'll become sending comments.
     * @param courseId
     * @return set of emails for recipients who can see the sending comments
     * @throws EntityDoesNotExistException when the course doesn't exist
     */
    public Set<String> getRecipientEmailsForSendingComments(String courseId) throws EntityDoesNotExistException {
        List<StudentAttributes> allStudents = studentsLogic.getStudentsForCourse(courseId);

        CourseRoster roster = new CourseRoster(allStudents, instructorsLogic.getInstructorsForCourse(courseId));
        
        Map<String, List<StudentAttributes>> teamStudentTable = new HashMap<String, List<StudentAttributes>>();
        Map<String, List<StudentAttributes>> sectionStudentTable = new HashMap<String, List<StudentAttributes>>();
        populateTeamSectionStudentTables(allStudents, teamStudentTable, sectionStudentTable);
        
        Set<String> recipientEmailsList = populateRecipientEmails(courseId,
                allStudents, roster, teamStudentTable, sectionStudentTable);
        
        return recipientEmailsList;
    }

    private Set<String> populateRecipientEmails(String courseId, List<StudentAttributes> allStudents,
            CourseRoster roster, Map<String, List<StudentAttributes>> teamStudentTable,
            Map<String, List<StudentAttributes>> sectionStudentTable) throws EntityDoesNotExistException {
        Set<String> recipientEmailsList = new HashSet<String>();
        
        List<CommentAttributes> sendingCommentsList = commentsDb.getCommentsForSendingState(courseId,
                                                                    CommentSendingState.SENDING);
        populateRecipientEmailsFromPendingComments(sendingCommentsList, allStudents, roster,
                                                   teamStudentTable, sectionStudentTable, recipientEmailsList);
        
        List<FeedbackResponseCommentAttributes> sendingResponseCommentsList =
                frcLogic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.SENDING);
        populateRecipientEmailsFromPendingResponseComments(sendingResponseCommentsList, allStudents, roster,
                                                           teamStudentTable, recipientEmailsList);
        
        return recipientEmailsList;
    }
    
    private void populateTeamSectionStudentTables(List<StudentAttributes> allStudents,
                                                  Map<String, List<StudentAttributes>> teamStudentTable,
                                                  Map<String, List<StudentAttributes>> sectionStudentTable) {
        for (StudentAttributes student : allStudents) {
            List<StudentAttributes> teammates = teamStudentTable.get(student.team);
            if (teammates == null) {
                teammates = new ArrayList<StudentAttributes>();
                teamStudentTable.put(student.team, teammates);
            }
            teammates.add(student);
            List<StudentAttributes> studentsInTheSameSection = sectionStudentTable.get(student.section);
            if (studentsInTheSameSection == null) {
                studentsInTheSameSection = new ArrayList<StudentAttributes>();
                sectionStudentTable.put(student.section, studentsInTheSameSection);
            }
            studentsInTheSameSection.add(student);
        }
    }
    
    /************ Send Email For Pending Comments : populate recipients emails from Feedback Response Comments ************/

    private void populateRecipientEmailsFromPendingResponseComments(
                     List<FeedbackResponseCommentAttributes> sendingResponseCommentsList,
                     List<StudentAttributes> allStudents, CourseRoster roster,
                     Map<String, List<StudentAttributes>> teamStudentTable,
                     Set<String> recipientEmailsList) {
        
        Map<String, FeedbackQuestionAttributes> feedbackQuestionsTable =
                new HashMap<String, FeedbackQuestionAttributes>();
        Map<String, FeedbackResponseAttributes> feedbackResponsesTable =
                new HashMap<String, FeedbackResponseAttributes>();
        Map<String, Set<String>> responseCommentsAddedTable = new HashMap<String, Set<String>>();
        
        for (FeedbackResponseCommentAttributes frc : sendingResponseCommentsList) {
            FeedbackQuestionAttributes relatedQuestion = getRelatedQuestion(feedbackQuestionsTable, frc);
            FeedbackResponseAttributes relatedResponse = getRelatedResponse(feedbackResponsesTable, frc);
            
            if (relatedQuestion != null && relatedResponse != null) {
                populateRecipientEmailsForGiver(roster, teamStudentTable, recipientEmailsList,
                        responseCommentsAddedTable, frc, relatedQuestion, relatedResponse);
                populateRecipientEmailsForReceiver(roster, teamStudentTable, recipientEmailsList,
                        responseCommentsAddedTable, frc, relatedResponse);
                populateRecipientEmailsForTeamMember(roster, teamStudentTable, recipientEmailsList,
                        responseCommentsAddedTable, frc, relatedResponse);
                populateRecipientEmailsForAllStudents(allStudents, recipientEmailsList,
                        responseCommentsAddedTable, frc);
            }
        }
    }

    private void populateRecipientEmailsForAllStudents(List<StudentAttributes> allStudents,
            Set<String> recipientEmailsList, Map<String, Set<String>> responseCommentsAddedTable,
            FeedbackResponseCommentAttributes frc) {
        if (frc.isVisibleTo(FeedbackParticipantType.STUDENTS)) {
            for (StudentAttributes student : allStudents) {
                addRecipientEmailsToList(responseCommentsAddedTable, recipientEmailsList,
                                         frc.getId().toString(), student.email);
            }
        }
    }

    private void populateRecipientEmailsForTeamMember(CourseRoster roster,
            Map<String, List<StudentAttributes>> teamStudentTable, Set<String> recipientEmailsList,
            Map<String, Set<String>> responseCommentsAddedTable, FeedbackResponseCommentAttributes frc,
            FeedbackResponseAttributes relatedResponse) {
        if (frc.isVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {
            StudentAttributes studentOfThisEmail = roster.getStudentForEmail(relatedResponse.recipient);
            if (studentOfThisEmail == null) {
                addRecipientEmailsForTeam(teamStudentTable, recipientEmailsList, responseCommentsAddedTable,
                                                frc.getId().toString(), relatedResponse.recipient);
            } else {
                addRecipientEmailsForTeam(teamStudentTable, recipientEmailsList, responseCommentsAddedTable,
                                                frc.getId().toString(), studentOfThisEmail.team);
            }
        }
    }

    private void populateRecipientEmailsForReceiver(CourseRoster roster,
            Map<String, List<StudentAttributes>> teamStudentTable, Set<String> recipientEmailsList,
            Map<String, Set<String>> responseCommentsAddedTable, FeedbackResponseCommentAttributes frc,
            FeedbackResponseAttributes relatedResponse) {
        if (frc.isVisibleTo(FeedbackParticipantType.RECEIVER)) {
            //recipientEmail is email
            if (roster.getStudentForEmail(relatedResponse.recipient) == null) {
                addRecipientEmailsForTeam(teamStudentTable, recipientEmailsList,
                                                responseCommentsAddedTable, frc.getId().toString(),
                                                relatedResponse.recipient);
            } else {
                addRecipientEmailsToList(responseCommentsAddedTable, recipientEmailsList,
                                                frc.getId().toString(), relatedResponse.recipient);
            }
        }
    }

    private void populateRecipientEmailsForGiver(CourseRoster roster,
            Map<String, List<StudentAttributes>> teamStudentTable, Set<String> recipientEmailsList,
            Map<String, Set<String>> responseCommentsAddedTable, FeedbackResponseCommentAttributes frc,
            FeedbackQuestionAttributes relatedQuestion, FeedbackResponseAttributes relatedResponse) {
        StudentAttributes giver = roster.getStudentForEmail(relatedResponse.giver);
        if (giver == null) {
            return;
        }
        
        if (frc.isVisibleTo(FeedbackParticipantType.GIVER)) {
            addRecipientEmailsToList(responseCommentsAddedTable, recipientEmailsList,
                                     frc.getId().toString(), relatedResponse.giver);
        }
        
        if (relatedQuestion.giverType == FeedbackParticipantType.TEAMS
                || frc.isVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            addRecipientEmailsForTeam(teamStudentTable, recipientEmailsList, responseCommentsAddedTable,
                                      frc.getId().toString(), giver.team);
        }
    }

    private FeedbackResponseAttributes getRelatedResponse(
            Map<String, FeedbackResponseAttributes> feedbackResponsesTable, FeedbackResponseCommentAttributes frc) {
        FeedbackResponseAttributes relatedResponse = feedbackResponsesTable.get(frc.feedbackResponseId);
        if (relatedResponse == null) {
            relatedResponse = frLogic.getFeedbackResponse(frc.feedbackResponseId);
            feedbackResponsesTable.put(frc.feedbackResponseId, relatedResponse);
        }
        return relatedResponse;
    }

    private FeedbackQuestionAttributes getRelatedQuestion(
            Map<String, FeedbackQuestionAttributes> feedbackQuestionsTable, FeedbackResponseCommentAttributes frc) {
        FeedbackQuestionAttributes relatedQuestion = feedbackQuestionsTable.get(frc.feedbackQuestionId);
        if (relatedQuestion == null) {
            relatedQuestion = fqLogic.getFeedbackQuestion(frc.feedbackQuestionId);
            feedbackQuestionsTable.put(frc.feedbackQuestionId, relatedQuestion);
        }
        return relatedQuestion;
    }
    
    /************ Send Email For Pending Comments : populate recipients emails from Student Comments ************/

    private void populateRecipientEmailsFromPendingComments(List<CommentAttributes> sendingCommentsList,
                                                            List<StudentAttributes> allStudents, CourseRoster roster,
                                                            Map<String, List<StudentAttributes>> teamStudentTable,
                                                            Map<String, List<StudentAttributes>> sectionStudentTable,
                                                            Set<String> recipientEmailList) {
        
        Map<String, Set<String>> studentCommentsAddedTable = new HashMap<String, Set<String>>();
        
        for (CommentAttributes pendingComment : sendingCommentsList) {
            populateRecipientEmailsForPerson(recipientEmailList, studentCommentsAddedTable, pendingComment);
            populateRecipientEmailsForTeam(recipientEmailList, roster, teamStudentTable,
                                           studentCommentsAddedTable, pendingComment);
            populateRecipientEmailsForSection(recipientEmailList, roster, teamStudentTable, sectionStudentTable,
                                              studentCommentsAddedTable, pendingComment);
            populateRecipientEmailsForCourse(recipientEmailList, allStudents,
                                             studentCommentsAddedTable, pendingComment);
        }
    }

    private void populateRecipientEmailsForCourse(Set<String> recipientEmailList, List<StudentAttributes> allStudents,
                                                  Map<String, Set<String>> studentCommentsAddedTable,
                                                  CommentAttributes pendingComment) {
        if (pendingComment.isVisibleTo(CommentParticipantType.COURSE)) {
            for (StudentAttributes student : allStudents) {
                addRecipientEmailsToList(studentCommentsAddedTable, recipientEmailList,
                                         pendingComment.getCommentId().toString(), student.email);
            }
        }
    }
    
    private void populateRecipientEmailsForSection(Set<String> recipientEmailList, CourseRoster roster,
                                                   Map<String, List<StudentAttributes>> teamStudentTable,
                                                   Map<String, List<StudentAttributes>> sectionStudentTable,
                                                   Map<String, Set<String>> studentCommentsAddedTable,
                                                   CommentAttributes pendingComment) {
        String commentId = pendingComment.getCommentId().toString();
        if (pendingComment.isVisibleTo(CommentParticipantType.SECTION)) {
            if (pendingComment.recipientType == CommentParticipantType.PERSON) {
                for (String recipientEmail : pendingComment.recipients) {
                    StudentAttributes student = roster.getStudentForEmail(recipientEmail);
                    if (student == null) {
                        continue;
                    }
                    addRecipientEmailsForSection(sectionStudentTable, recipientEmailList, studentCommentsAddedTable,
                                                 commentId, student.section);
                }
            } else if (pendingComment.recipientType == CommentParticipantType.TEAM) {
                for (String team : pendingComment.recipients) {
                    List<StudentAttributes> students = teamStudentTable.get(team);
                    if (students == null) {
                        continue;
                    }
                    for (StudentAttributes stu : students) {
                        addRecipientEmailsForSection(sectionStudentTable, recipientEmailList,
                                                     studentCommentsAddedTable, commentId, stu.section);
                    }
                }
            } else if (pendingComment.recipientType == CommentParticipantType.SECTION) {
                for (String section : pendingComment.recipients) {
                    addRecipientEmailsForSection(sectionStudentTable, recipientEmailList, studentCommentsAddedTable,
                                                 commentId, section);
                }
            }
        } else { //not visible to SECTION
            if (pendingComment.recipientType == CommentParticipantType.PERSON) {
                for (String recipientEmail : pendingComment.recipients) {
                    StudentAttributes student = roster.getStudentForEmail(recipientEmail);
                    if (student == null) {
                        continue;
                    }
                    preventAddRecipientEmailsForSection(teamStudentTable, studentCommentsAddedTable,
                                                        commentId, student.section);
                }
            } else if (pendingComment.recipientType == CommentParticipantType.TEAM) {
                for (String team : pendingComment.recipients) {
                    List<StudentAttributes> students = teamStudentTable.get(team);
                    if (students == null) {
                        continue;
                    }
                    for (StudentAttributes stu : students) {
                        preventAddRecipientEmailsForSection(teamStudentTable, studentCommentsAddedTable,
                                                            commentId, stu.section);
                    }
                }
            } else if (pendingComment.recipientType == CommentParticipantType.SECTION) {
                for (String section : pendingComment.recipients) {
                    preventAddRecipientEmailsForSection(teamStudentTable, studentCommentsAddedTable,
                                                        commentId, section);
                }
            }
        }
    }

    private void populateRecipientEmailsForTeam(Set<String> recipientEmailList, CourseRoster roster,
                                                Map<String, List<StudentAttributes>> teamStudentTable,
                                                Map<String, Set<String>> studentCommentsAddedTable,
                                                CommentAttributes pendingComment) {
        String commentId = pendingComment.getCommentId().toString();
        if (pendingComment.isVisibleTo(CommentParticipantType.TEAM)) {
            if (pendingComment.recipientType == CommentParticipantType.PERSON) {
                for (String recipientEmail : pendingComment.recipients) {
                    StudentAttributes student = roster.getStudentForEmail(recipientEmail);
                    if (student == null) {
                        continue;
                    }
                    addRecipientEmailsForTeam(teamStudentTable, recipientEmailList, studentCommentsAddedTable,
                                              commentId, student.team);
                }
            } else if (pendingComment.recipientType == CommentParticipantType.TEAM) {
                for (String team : pendingComment.recipients) {
                    addRecipientEmailsForTeam(teamStudentTable, recipientEmailList, studentCommentsAddedTable,
                                              commentId, team);
                }
            }
        } else { //not visible to TEAM
            if (pendingComment.recipientType == CommentParticipantType.PERSON) {
                for (String recipientEmail : pendingComment.recipients) {
                    StudentAttributes student = roster.getStudentForEmail(recipientEmail);
                    if (student == null) {
                        continue;
                    }
                    preventAddRecipientEmailsForTeam(teamStudentTable, studentCommentsAddedTable,
                                                     commentId, student.team);
                }
            } else if (pendingComment.recipientType == CommentParticipantType.TEAM) {
                for (String team : pendingComment.recipients) {
                    preventAddRecipientEmailsForTeam(teamStudentTable, studentCommentsAddedTable,
                                                     commentId, team);
                }
            }
        }
    }

    private void populateRecipientEmailsForPerson(Set<String> recipientEmailList,
                                                  Map<String, Set<String>> studentCommentsAddedTable,
                                                  CommentAttributes pendingComment) {
        String commentId = pendingComment.getCommentId().toString();
        if (pendingComment.isVisibleTo(CommentParticipantType.PERSON)) {
            for (String recipientEmail : pendingComment.recipients) {
                addRecipientEmailsToList(studentCommentsAddedTable, recipientEmailList,
                                         commentId, recipientEmail);
            }
        } else { //not visible to PERSON
            for (String recipientEmail : pendingComment.recipients) {
                preventAddRecipientEmailsToList(studentCommentsAddedTable, commentId, recipientEmail);
            }
        }
    }

    private void addRecipientEmailsToList(Map<String, Set<String>> isAddedTable, Set<String> targetTable,
                                          String subKey, String key) {
        //prevent re-entry
        Set<String> commentIdsSet = isAddedTable.get(key);
        if (commentIdsSet == null) {
            commentIdsSet = new HashSet<String>();
            isAddedTable.put(key, commentIdsSet);
        }
        if (!commentIdsSet.contains(subKey)) {
            commentIdsSet.add(subKey);
            targetTable.add(key);
        }
    }
    
    private void addRecipientEmailsForSection(Map<String, List<StudentAttributes>> sectionStudentTable,
            Set<String> recipientEmailsList, Map<String, Set<String>> responseCommentsAddedTable,
            String commentId, String sectionName) {
        List<StudentAttributes> students = sectionStudentTable.get(sectionName);
        if (students == null) {
            return;
        }
        
        for (StudentAttributes stu : students) {
            addRecipientEmailsToList(responseCommentsAddedTable, recipientEmailsList, commentId, stu.email);
        }
    }
    
    private void addRecipientEmailsForTeam(Map<String, List<StudentAttributes>> teamStudentTable,
            Set<String> recipientEmailsList, Map<String, Set<String>> responseCommentsAddedTable,
            String commentId, String teamName) {
        List<StudentAttributes> students = teamStudentTable.get(teamName);
        if (students == null) {
            return;
        }
        
        for (StudentAttributes stu : students) {
            addRecipientEmailsToList(responseCommentsAddedTable, recipientEmailsList,
                                     commentId, stu.email);
        }
    }
    
    private void preventAddRecipientEmailsToList(Map<String, Set<String>> isAddedTable, String subKey, String key) {
        Set<String> commentIdsSet = isAddedTable.get(key);
        if (commentIdsSet == null) {
            commentIdsSet = new HashSet<String>();
            isAddedTable.put(key, commentIdsSet);
        }
        commentIdsSet.add(subKey);
    }
    
    private void preventAddRecipientEmailsForSection(Map<String, List<StudentAttributes>> sectionStudentTable,
            Map<String, Set<String>> isAddedTable, String commentId, String section) {
        List<StudentAttributes> students = sectionStudentTable.get(section);
        if (students == null) {
            return;
        }
        
        for (StudentAttributes stu : students) {
            preventAddRecipientEmailsToList(isAddedTable,
                    commentId, stu.email);
        }
    }
    
    private void preventAddRecipientEmailsForTeam(Map<String, List<StudentAttributes>> teamStudentTable,
            Map<String, Set<String>> isAddedTable, String commentId, String team) {
        List<StudentAttributes> teammates = teamStudentTable.get(team);
        if (teammates == null) {
            return;
        }
        
        for (StudentAttributes teamMember : teammates) {
            preventAddRecipientEmailsToList(isAddedTable, commentId, teamMember.email);
        }
    }

    @SuppressWarnings("deprecation")
    public List<CommentAttributes> getAllComments() {
        return commentsDb.getAllComments();
    }
    
}
