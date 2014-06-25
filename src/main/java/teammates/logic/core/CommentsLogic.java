package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Utils;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

public class CommentsLogic {
    
    private static CommentsLogic instance;

    @SuppressWarnings("unused") //used by test
    private static final Logger log = Utils.getLogger();

    private static final CommentsDb commentsDb = new CommentsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();

    public static CommentsLogic inst() {
        if (instance == null)
            instance = new CommentsLogic();
        return instance;
    }

    public void createComment(CommentAttributes comment)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        verifyIsCoursePresentForCreateComment(comment.courseId);
        verifyIsInstructorOfCourse(comment.courseId, comment.giverEmail);

        commentsDb.createEntity(comment);
    }

    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForGiver(courseId, giverEmail);
    }

    public List<CommentAttributes> getCommentsForReceiver(String courseId, CommentRecipientType recipientType, String receiverEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForReceiver(courseId, recipientType, receiverEmail);
    }
    
    public void clearPendingComments(String courseId) throws EntityDoesNotExistException{
        verifyIsCoursePresentForClearPendingComments(courseId);
        commentsDb.clearPendingComments(courseId);
    }
    
    public void updateComment(CommentAttributes comment)
            throws InvalidParametersException, EntityDoesNotExistException{
        verifyIsCoursePresentForUpdateComments(comment.courseId);
        commentsDb.updateComment(comment);
    }
    
    public void deleteComment(CommentAttributes comment){
        commentsDb.deleteEntity(comment);
    }
    
    public List<CommentAttributes> getCommentDrafts(String giverEmail)
            throws EntityDoesNotExistException {
        return commentsDb.getCommentDrafts(giverEmail);
    }
    
    public List<CommentAttributes> getCommentsForStudent(StudentAttributes student)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(student.course);
        
        List<StudentAttributes> teammates = studentsLogic.getStudentsForTeam(student.team, student.course);
        List<String> teammatesEmails = getTeammatesEmails(teammates);

        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        HashSet<String> commentsVisitedSet = new HashSet<String>();
        
        List<CommentAttributes> commentsForStudent = getCommentsForReceiver(student.course, CommentRecipientType.PERSON, student.email);
        removeNonVisibleCommentsForStudent(commentsForStudent, commentsVisitedSet, comments);
        
        List<CommentAttributes> commentsForTeam = getCommentsForCommentViewer(student.course, CommentRecipientType.TEAM);
        removeNonVisibleCommentsForTeam(commentsForTeam, student, teammatesEmails, commentsVisitedSet, comments);
        
        //TODO: handle comments for section
        
        List<CommentAttributes> commentsForCourse = getCommentsForCommentViewer(student.course, CommentRecipientType.COURSE);
        removeNonVisibleCommentsForCourse(commentsForCourse, student, teammatesEmails, commentsVisitedSet, comments);
        
        java.util.Collections.sort(comments);
        
        return comments;
    }
    
    public List<CommentAttributes> getCommentsForInstructor(InstructorAttributes instructor)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(instructor.courseId);
        verifyIsInstructorOfCourse(instructor.courseId, instructor.email);
        HashSet<String> commentsVisitedSet = new HashSet<String>();
        
        List<CommentAttributes> comments = getCommentsForGiverAndStatus(instructor.courseId, instructor.email, CommentStatus.FINAL);
        for(CommentAttributes c: comments){
            preventAppendingThisCommentAgain(commentsVisitedSet, c);
        }
        
        List<CommentAttributes> commentsForOtherInstructor = getCommentsForCommentViewer(instructor.courseId, CommentRecipientType.INSTRUCTOR);
        removeNonVisibleCommentsForInstructor(commentsForOtherInstructor, commentsVisitedSet, comments);
        
        java.util.Collections.sort(comments);
        
        return comments;
    }
    
    public void sendEmailForPendingComments(String courseId) throws EntityDoesNotExistException {
        List<StudentAttributes> allStudents = new StudentsDb().getStudentsForCourse(courseId);
        
        //prepare roster
        CourseRoster roster = new CourseRoster(
                allStudents,
                new InstructorsDb().getInstructorsForCourse(courseId));
        
        //prepare team-student and section-student tables
        Map<String, List<StudentAttributes>> teamStudentTable = new HashMap<String, List<StudentAttributes>>();
        Map<String, List<StudentAttributes>> sectionStudentTable = new HashMap<String, List<StudentAttributes>>();
        populateTeamSectionStudentTables(allStudents, teamStudentTable,
                sectionStudentTable);
        
        Set<String> recipientEmailsList = new HashSet<String>();
        List<CommentAttributes> pendingCommentsList = commentsDb.getPendingComments(courseId);
        populateRecipientEmailsFromPendingComments(pendingCommentsList, 
                allStudents, roster, teamStudentTable,
                sectionStudentTable,
                recipientEmailsList);
        
        Map<String, Set<String>> responseCommentsAddedTable = new HashMap<String, Set<String>>();
        List<FeedbackResponseCommentAttributes> pendingResponseCommentsList = frcLogic.getPendingFeedbackResponseComments(courseId);
    }

    private void populateTeamSectionStudentTables(
            List<StudentAttributes> allStudents,
            Map<String, List<StudentAttributes>> teamStudentTable,
            Map<String, List<StudentAttributes>> sectionStudentTable) {
        for(StudentAttributes student:allStudents){
            List<StudentAttributes> teammates = teamStudentTable.get(student.team);
            if(teammates == null){
                teammates = new ArrayList<StudentAttributes>();
                teamStudentTable.put(student.team, teammates);
            }
            teammates.add(student);
            List<StudentAttributes> studentsInTheSameSection = sectionStudentTable.get(student.section);
            if(studentsInTheSameSection == null){
                studentsInTheSameSection = new ArrayList<StudentAttributes>();
                sectionStudentTable.put(student.section, studentsInTheSameSection);
            }
            studentsInTheSameSection.add(student);
        }
    }

    private void populateRecipientEmailsFromPendingComments(
            List<CommentAttributes> pendingCommentsList,
            List<StudentAttributes> allStudents, CourseRoster roster,
            Map<String, List<StudentAttributes>> teamStudentTable,
            Map<String, List<StudentAttributes>> sectionStudentTable,
            Set<String> recipientEmailList) {
        
        Map<String, Set<String>> studentCommentsAddedTable = new HashMap<String, Set<String>>();
        
        for(CommentAttributes pendingComment : pendingCommentsList){
            switch(pendingComment.recipientType){
            case PERSON:
                populateRecipientEmailsForRecipientPerson(allStudents, roster,
                        teamStudentTable, sectionStudentTable,
                        studentCommentsAddedTable, recipientEmailList,
                        pendingComment);
                break;
            case TEAM:
                populateRecipientEmailsForRecipientTeam(allStudents, teamStudentTable,
                        sectionStudentTable, studentCommentsAddedTable,
                        recipientEmailList, pendingComment);
                break;
            case SECTION:
                populateRecipientEmailsForRecipientSection(allStudents, sectionStudentTable,
                        studentCommentsAddedTable, recipientEmailList,
                        pendingComment);
                break;
            case COURSE:
                populateRecipientEmailsForCourse(allStudents, studentCommentsAddedTable,
                        recipientEmailList, pendingComment);
                break;
            default:
                break;
            }
        }
    }

    private void populateRecipientEmailsForRecipientSection(
            List<StudentAttributes> allStudents,
            Map<String, List<StudentAttributes>> sectionStudentTable,
            Map<String, Set<String>> studentCommentsAddedTable,
            Set<String> recipientEmailList,
            CommentAttributes pendingComment) {
        for(String section : pendingComment.recipients){
            populateRecipientEmailsForViewerSection(sectionStudentTable, studentCommentsAddedTable,
                    recipientEmailList, pendingComment, section);
        }
        populateRecipientEmailsForCourse(allStudents, studentCommentsAddedTable,
                recipientEmailList, pendingComment);
    }

    private void populateRecipientEmailsForRecipientTeam(
            List<StudentAttributes> allStudents,
            Map<String, List<StudentAttributes>> teamStudentTable,
            Map<String, List<StudentAttributes>> sectionStudentTable,
            Map<String, Set<String>> studentCommentsAddedTable,
            Set<String> recipientEmailList,
            CommentAttributes pendingComment) {
        for(String team : pendingComment.recipients){
            populateRecipientEmailsForViewerTeam(teamStudentTable, studentCommentsAddedTable,
                    recipientEmailList, pendingComment, team);
            
            List<StudentAttributes> studentsInThisTeam = teamStudentTable.get(team);    
            if(studentsInThisTeam != null && 
                    studentsInThisTeam.size() > 0 
                    && studentsInThisTeam.get(0) != null){
                String section = studentsInThisTeam.get(0).section;
                populateRecipientEmailsForViewerSection(sectionStudentTable, studentCommentsAddedTable,
                        recipientEmailList, pendingComment,
                        section);
            }
            
            populateRecipientEmailsForCourse(allStudents, studentCommentsAddedTable,
                    recipientEmailList, pendingComment);
        }
    }

    private void populateRecipientEmailsForRecipientPerson(
            List<StudentAttributes> allStudents, CourseRoster roster,
            Map<String, List<StudentAttributes>> teamStudentTable,
            Map<String, List<StudentAttributes>> sectionStudentTable,
            Map<String, Set<String>> studentCommentsAddedTable,
            Set<String> recipientEmailList,
            CommentAttributes pendingComment) {
        for(String recipientEmail:pendingComment.recipients){
            populateRecipientEmailsForViewerPerson(studentCommentsAddedTable,
                    recipientEmailList, pendingComment,
                    recipientEmail);
            
            StudentAttributes studentOfThisEmail = roster.getStudentForEmail(recipientEmail);
            if(studentOfThisEmail != null){
                populateRecipientEmailsForViewerTeam(teamStudentTable, studentCommentsAddedTable,
                        recipientEmailList, pendingComment, studentOfThisEmail.team);
                populateRecipientEmailsForViewerSection(sectionStudentTable, studentCommentsAddedTable,
                        recipientEmailList, pendingComment,
                        studentOfThisEmail.section);
            }
            
            populateRecipientEmailsForCourse(allStudents, studentCommentsAddedTable,
                    recipientEmailList, pendingComment);
        }
    }

    private void populateRecipientEmailsForViewerSection(
            Map<String, List<StudentAttributes>> sectionStudentTable,
            Map<String, Set<String>> studentCommentsAddedTable,
            Set<String> recipientEmailList,
            CommentAttributes pendingComment, String section) {
        //TODO: recover this part when comment in section is finished
        /*
        List<StudentAttributes> studentsInThisSection = sectionStudentTable.get(section);
        if(studentsInThisSection == null)
            return;
        for(StudentAttributes student : studentsInThisSection){
            String recipientEmailInTheSameSection = student.email;
            if(pendingComment.isVisibleTo(CommentRecipientType.SECTION)){
                addRecipientEmailsToList(studentCommentsAddedTable,
                        recipientEmailList, pendingComment, recipientEmailInTheSameSection);
            } else {
                preventAddRecipientEmailsToList(studentCommentsAddedTable, pendingComment, recipientEmailInTheSameSection);
            }
        }*/
    }

    private void populateRecipientEmailsForViewerTeam(
            Map<String, List<StudentAttributes>> teamStudentTable,
            Map<String, Set<String>> studentCommentsAddedTable,
            Set<String> recipientEmailList,
            CommentAttributes pendingComment, String team) {
        List<StudentAttributes> studentsInThisTeam = teamStudentTable.get(team);
        if(studentsInThisTeam == null)
            return;
        for(StudentAttributes student : studentsInThisTeam){
            String teammatesEmail = student.email;
            if(pendingComment.isVisibleTo(CommentRecipientType.TEAM)){
                addRecipientEmailsToList(studentCommentsAddedTable,
                        recipientEmailList, pendingComment, teammatesEmail);
            } else {
                preventAddRecipientEmailsToList(studentCommentsAddedTable, pendingComment, teammatesEmail);
            }
        }
    }

    private void populateRecipientEmailsForViewerPerson(
            Map<String, Set<String>> studentCommentsAddedTable,
            Set<String> recipientEmailList,
            CommentAttributes pendingComment, String recipientEmail) {
        if(pendingComment.isVisibleTo(CommentRecipientType.PERSON)){
            addRecipientEmailsToList(studentCommentsAddedTable,
                    recipientEmailList, pendingComment, recipientEmail);
        } else {
            preventAddRecipientEmailsToList(studentCommentsAddedTable, pendingComment, recipientEmail);
        }
    }

    private void populateRecipientEmailsForCourse(List<StudentAttributes> allStudents,
            Map<String, Set<String>> studentCommentsAddedTable,
            Set<String> recipientEmailList,
            CommentAttributes pendingComment) {
        for(StudentAttributes student : allStudents){
            String recipientEmail = student.email;
            if(pendingComment.isVisibleTo(CommentRecipientType.COURSE)){
                addRecipientEmailsToList(studentCommentsAddedTable,
                        recipientEmailList, pendingComment, recipientEmail);
            }
        }
    }

    private void addRecipientEmailsToList(
            Map<String, Set<String>> isAddedTable,
            Set<String> targetTable,
            CommentAttributes comment, String key) {
        //prevent re-entry
        Set<String> commentIdsSet = isAddedTable.get(key);
        if(commentIdsSet == null){
            commentIdsSet = new HashSet<String>();
            isAddedTable.put(key, commentIdsSet);
        }
        if(!commentIdsSet.contains(comment.getCommentId().toString())){
            commentIdsSet.add(comment.getCommentId().toString());
            targetTable.add(key);
        }
    }
    
    private void preventAddRecipientEmailsToList(
            Map<String, Set<String>> isAddedTable,
            CommentAttributes comment, String key){
        Set<String> commentIdsSet = isAddedTable.get(key);
        if(commentIdsSet == null){
            commentIdsSet = new HashSet<String>();
            isAddedTable.put(key, commentIdsSet);
        }
        commentIdsSet.add(comment.getCommentId().toString());
    }
    
    private List<CommentAttributes> getCommentsForCommentViewer(String courseId, CommentRecipientType commentViewerType)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForCommentViewer(courseId, commentViewerType);
    }
    
    private List<CommentAttributes> getCommentsForGiverAndStatus(String courseId, String giverEmail, CommentStatus status)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForGiverAndStatus(courseId, giverEmail, status);
    }
    
    private void removeNonVisibleCommentsForInstructor(
            List<CommentAttributes> commentsForInstructor,
            HashSet<String> commentsVisitedSet, List<CommentAttributes> comments) {
        for(CommentAttributes c:commentsForInstructor){
            removeGiverAndRecipientNameByVisibilityOptions(c, CommentRecipientType.INSTRUCTOR);
            appendComments(c, comments, commentsVisitedSet);
        }
    }

    private List<String> getTeammatesEmails(List<StudentAttributes> teammates) {
        List<String> teammatesEmails = new ArrayList<String>();
        for(StudentAttributes teammate : teammates){
            teammatesEmails.add(teammate.email);
        }
        return teammatesEmails;
    }

    private void removeNonVisibleCommentsForCourse(
            List<CommentAttributes> commentsForCourse, StudentAttributes student, List<String> teammates, HashSet<String> commentsVisitedSet,
            List<CommentAttributes> comments) {
        //ensure comments for teammates or team is separated from comments for course
        removeNonVisibleCommentsForTeam(commentsForCourse, student, teammates, commentsVisitedSet, comments);
        
        for(CommentAttributes c: commentsForCourse){
            if(c.courseId.equals(student.course)){
                if(c.recipientType == CommentRecipientType.COURSE) {
                    removeGiverNameByVisibilityOptions(c, CommentRecipientType.COURSE);
                } else {
                    removeGiverAndRecipientNameByVisibilityOptions(c, CommentRecipientType.COURSE);
                }
                appendComments(c, comments, commentsVisitedSet);
            }
        }
    }
    
    private void removeNonVisibleCommentsForTeam(List<CommentAttributes> commentsForTeam,
            StudentAttributes student, List<String> teammates, HashSet<String> commentsVisitedSet,
            List<CommentAttributes> comments) {
        for(CommentAttributes c:commentsForTeam){
            //for teammates
            if(c.recipientType == CommentRecipientType.PERSON
                    && isCommentRecipientsContainTeammates(teammates, c)){
                if(c.showCommentTo.contains(CommentRecipientType.TEAM)){
                    removeGiverAndRecipientNameByVisibilityOptions(c, CommentRecipientType.TEAM);
                    appendComments(c, comments, commentsVisitedSet);
                } else {
                    preventAppendingThisCommentAgain(commentsVisitedSet, c);
                }
            //for team
            } else if(c.recipientType == CommentRecipientType.TEAM 
                    && c.recipients.contains(student.team)){
                if(c.showCommentTo.contains(CommentRecipientType.TEAM)){
                    removeGiverNameByVisibilityOptions(c, CommentRecipientType.TEAM);
                    appendComments(c, comments, commentsVisitedSet);
                } else {
                    preventAppendingThisCommentAgain(commentsVisitedSet, c);
                }
            }
        }
    }

    private void removeNonVisibleCommentsForStudent(List<CommentAttributes> commentsForStudent, HashSet<String> commentsVisitedSet,
            List<CommentAttributes> comments){
        for(CommentAttributes c:commentsForStudent){
            if(c.showCommentTo.contains(CommentRecipientType.PERSON)){
                removeGiverNameByVisibilityOptions(c, CommentRecipientType.PERSON);
                appendComments(c, comments, commentsVisitedSet);
            } else {
                preventAppendingThisCommentAgain(commentsVisitedSet, c);
            }
        }
    }
    
    private void removeGiverNameByVisibilityOptions(CommentAttributes c, CommentRecipientType viewerType) {
        if (!c.showGiverNameTo.contains(viewerType)){
            c.giverEmail = "Anonymous";
        }
    }

    private void removeGiverAndRecipientNameByVisibilityOptions(CommentAttributes c, CommentRecipientType viewerType) {
        removeGiverNameByVisibilityOptions(c, viewerType);
        if(!c.showRecipientNameTo.contains(viewerType)){
            c.recipients = new HashSet<String>();
            c.recipients.add("Anonymous");
        }
    }
    
    private void appendComments(CommentAttributes c, List<CommentAttributes> toThisCommentList, HashSet<String> commentsVisitedSet){
        if(!commentsVisitedSet.contains(c.getCommentId().toString())){
            toThisCommentList.add(c);
            preventAppendingThisCommentAgain(commentsVisitedSet, c);
        }
    }
    
    private void preventAppendingThisCommentAgain(
            HashSet<String> commentsVisitedSet, CommentAttributes c) {
        commentsVisitedSet.add(c.getCommentId().toString());
    }

    private boolean isCommentRecipientsContainTeammates(List<String> teammates, CommentAttributes c) {
        for(String recipient : c.recipients){
            if(teammates.contains(recipient)){
                return true;
            }
        }
        return false;
    }
    
    //TODO:refactor these
    private void verifyIsCoursePresentForCreateComment(String courseId)
            throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to create comments for a course that does not exist.");
        }
    }
    
    private void verifyIsCoursePresentForGetComments(String courseId)
            throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to get comments for a course that does not exist.");
        }
    }
    
    private void verifyIsCoursePresentForUpdateComments(String courseId)
            throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to update comments for a course that does not exist.");
        }
    }
    
    private void verifyIsCoursePresentForClearPendingComments(String courseId)
            throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to clear pending comments for a course that does not exist.");
        }
    }
    
    private void verifyIsInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException{
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if(instructor == null){
            throw new EntityDoesNotExistException(
                    "User " + email + " is not a registered instructor for course "+ courseId + ".");
        }
    }
}
