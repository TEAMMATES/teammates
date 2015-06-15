package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.CommentRow;
import teammates.ui.template.ElementTag;
import teammates.ui.template.FeedbackResponseCommentRow;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.QuestionTable;
import teammates.ui.template.ResponseRow;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.SearchStudentsTable;
import teammates.ui.template.SearchCommentsForResponsesTable;
import teammates.ui.template.StudentRow;

/**
 * PageData: the data to be used in the InstructorSearchPage
 */
public class InstructorSearchPageData extends PageData {
    private String searchKey = "";
    
    /* Whether checkbox is checked for search input */
    private boolean isSearchCommentForStudents;
    private boolean isSearchCommentForResponses;
    private boolean isSearchForStudents;
    
    /* Whether search results are empty */
    private boolean isCommentsForStudentsEmpty;
    private boolean isCommentsForResponsesEmpty;
    private boolean isStudentsEmpty;
    
    /* Tables containing search results */
    private List<CommentsForStudentsTable> searchCommentsForStudentsTables;
    private List<SearchCommentsForResponsesTable> searchCommentsForResponsesTables;
    private List<SearchStudentsTable> searchStudentsTables;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(CommentSearchResultBundle commentSearchResultBundle, 
                     FeedbackResponseCommentSearchResultBundle frcSearchResultBundle,
                     StudentSearchResultBundle studentSearchResultBundle, 
                     String searchKey, boolean isSearchCommentForStudents, 
                     boolean isSearchCommentForResponses, boolean isSearchForStudents) {
        
        this.searchKey = searchKey;
        
        this.isSearchCommentForStudents = isSearchCommentForStudents;
        this.isSearchCommentForResponses = isSearchCommentForResponses;
        this.isSearchForStudents = isSearchForStudents;
        
        this.isCommentsForStudentsEmpty = commentSearchResultBundle.getResultSize() == 0;
        this.isCommentsForResponsesEmpty = frcSearchResultBundle.getResultSize() == 0;
        this.isStudentsEmpty = studentSearchResultBundle.getResultSize() == 0;
        
        setSearchCommentsForStudentsTables(commentSearchResultBundle);
        setSearchCommentsForResponsesTables(frcSearchResultBundle);
        setSearchStudentsTables(studentSearchResultBundle);
    }
    
    /*************** Get methods ********************/
    public String getSearchKey() {
        return sanitizeForHtml(searchKey);
    }
    
    public boolean isCommentsForStudentsEmpty() {
        return isCommentsForStudentsEmpty;
    }
    
    public boolean isCommentsForResponsesEmpty() {
        return isCommentsForResponsesEmpty;
    }
    
    public boolean isStudentsEmpty() {
        return isStudentsEmpty;
    }
    
    
    public boolean isSearchCommentForStudents() {
        return isSearchCommentForStudents;
    }
    
    public boolean isSearchCommentForResponses() {
        return isSearchCommentForResponses;
    }
    
    public boolean isSearchForStudents() {
        return isSearchForStudents;
    }
    
    
    public List<CommentsForStudentsTable> getSearchCommentsForStudentsTables() {
        return searchCommentsForStudentsTables;
    }
    
    public List<SearchCommentsForResponsesTable> getSearchCommentsForResponsesTables() {
        return searchCommentsForResponsesTables;
    }
    
    public List<SearchStudentsTable> getSearchStudentsTables() {
        return searchStudentsTables;
    }

    
    public String getCourseStudentDetailsLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getCourseStudentEditLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    //TODO: create another delete action which redirects to studentListPage?
    public String getCourseStudentDeleteLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentRecordsLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    /*************** Set results tables *********************/
    private void setSearchCommentsForStudentsTables(
                                    CommentSearchResultBundle commentSearchResultBundle) {
        
        searchCommentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();      
        
        for (String giverEmailPlusCourseId : commentSearchResultBundle.giverCommentTable.keySet()) {
            String giverDetails = commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId);
            searchCommentsForStudentsTables.add(new CommentsForStudentsTable(
                                                  giverDetails, createCommentRows(giverEmailPlusCourseId, 
                                                                            commentSearchResultBundle)));
        }
    }
    
    private void setSearchCommentsForResponsesTables(
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        searchCommentsForResponsesTables = new ArrayList<SearchCommentsForResponsesTable>();
        searchCommentsForResponsesTables.add(new SearchCommentsForResponsesTable(
                                               createFeedbackSessionRows(frcSearchResultBundle)));
    }
    
    private void setSearchStudentsTables(StudentSearchResultBundle studentSearchResultBundle) {
        
        searchStudentsTables = new ArrayList<SearchStudentsTable>(); // 1 table for each course      
        List<String> courseIdList = getCourseIdsFromStudentSearchResultBundle(
                                        studentSearchResultBundle.studentList, studentSearchResultBundle);
        
        for (String courseId : courseIdList) {
            searchStudentsTables.add(new SearchStudentsTable(
                                       courseId, createStudentRows(courseId, studentSearchResultBundle)));
        }
    }  
    
    /*************** Create data structures for feedback response comments results ********************/
    private List<FeedbackSessionRow> createFeedbackSessionRows(
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<FeedbackSessionRow> rows = new ArrayList<FeedbackSessionRow>();
        
        for (String fsName : frcSearchResultBundle.questions.keySet()) {
            String courseId = frcSearchResultBundle.sessions.get(fsName).courseId;
            
            rows.add(new FeedbackSessionRow(fsName, courseId, createQuestionTables(
                                                                fsName, frcSearchResultBundle)));
        }
        return rows;
    }
    
    private List<QuestionTable> createQuestionTables(
                                    String fsName, 
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<QuestionTable> questionTables = new ArrayList<QuestionTable>();
        List<FeedbackQuestionAttributes> questionList = frcSearchResultBundle.questions.get(fsName);
        
        for (FeedbackQuestionAttributes question : questionList) {
            int questionNumber = question.questionNumber;
            String questionText = question.getQuestionDetails().questionText;
            String additionalInfo = question.getQuestionDetails()
                                            .getQuestionAdditionalInfoHtml(questionNumber, "");
            
            questionTables.add(new QuestionTable(questionNumber, questionText, additionalInfo, 
                                            createResponseRows(question, frcSearchResultBundle)));
        }
        return questionTables;
    }
    
    private List<ResponseRow> createResponseRows(
                                    FeedbackQuestionAttributes question, 
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<ResponseRow> rows = new ArrayList<ResponseRow>();
        List<FeedbackResponseAttributes> responseList = frcSearchResultBundle.responses.get(question.getId());
        
        for (FeedbackResponseAttributes responseEntry : responseList) {
            String giverName = frcSearchResultBundle.responseGiverTable.get(responseEntry.getId());            
            String recipientName = frcSearchResultBundle.responseRecipientTable.get(responseEntry.getId());           
            String response = responseEntry.getResponseDetails().getAnswerHtml(question.getQuestionDetails());
            
            rows.add(new ResponseRow(giverName, recipientName, response, 
                                       createFeedbackResponseCommentRows(responseEntry, frcSearchResultBundle)));
        }
        return rows;
    }
    
    private List<CommentRow> createCommentRows(
                                    String giverEmailPlusCourseId, 
                                    CommentSearchResultBundle commentSearchResultBundle) {
        
        List<CommentRow> rows = new ArrayList<CommentRow>();
        String giverDetails = commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId);
        String instructorCommentsLink = getInstructorCommentsLink();
        
        for (CommentAttributes comment : commentSearchResultBundle.giverCommentTable.get(giverEmailPlusCourseId)) {            
            String recipientDetails = commentSearchResultBundle.recipientTable
                                                                   .get(comment.getCommentId().toString());
            String creationTime = TimeHelper.formatTime(comment.createdAt);          
            
            String link = instructorCommentsLink + "&" + Const.ParamsNames.COURSE_ID 
                                            + "=" + comment.courseId + "#" + comment.getCommentId();           
            ElementTag editButton = createEditButton(link, Const.Tooltips.COMMENT_EDIT_IN_COMMENTS_PAGE);
            
            rows.add(new CommentRow(giverDetails, comment, recipientDetails, creationTime, editButton));
        }       
        return rows;
    }
    
    private List<FeedbackResponseCommentRow> createFeedbackResponseCommentRows(
                                    FeedbackResponseAttributes responseEntry,
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<FeedbackResponseCommentRow> rows = new ArrayList<FeedbackResponseCommentRow>();
        List<FeedbackResponseCommentAttributes> frcList = frcSearchResultBundle
                                                              .comments.get(responseEntry.getId());
        
        for (FeedbackResponseCommentAttributes frc : frcList) {
            String frCommentGiver = frcSearchResultBundle
                                            .commentGiverTable.get(frc.getId().toString());
            String creationTime = TimeHelper.formatTime(frc.createdAt);         
            String link = getInstructorCommentsLink() + "&" + Const.ParamsNames.COURSE_ID + "=" 
                              + frc.courseId + "#" + frc.getId();         
            ElementTag editButton = createEditButton(link, Const.Tooltips.COMMENT_EDIT_IN_COMMENTS_PAGE);
            
            rows.add(new FeedbackResponseCommentRow(frCommentGiver, frc.commentText.getValue(), 
                                                        creationTime, editButton));
        } 
        return rows;
    }
    
    /*************** Create data structures for student search results ********************/
    private List<StudentRow> createStudentRows(String courseId, 
                                               StudentSearchResultBundle studentSearchResultBundle) {
        List<StudentRow> rows = new ArrayList<StudentRow>();      
        List<StudentAttributes> studentsInCourse = filterStudentsByCourse(
                                                       courseId, studentSearchResultBundle);
        
        for (StudentAttributes student : studentsInCourse) {
            String viewPhotoLink = addUserIdToUrl(student.getPublicProfilePictureUrl());        
            String actions = getStudentActions(student, studentSearchResultBundle);

            rows.add(new StudentRow(viewPhotoLink, student.section, student.team, 
                                            student.name, student.email, actions));
        }
        return rows;
    }
    
    
    private List<String> getCourseIdsFromStudentSearchResultBundle(
                                    List<StudentAttributes> studentList, 
                                    StudentSearchResultBundle studentSearchResultBundle) {
        List<String> courses = new ArrayList<String>();
        
        for (StudentAttributes student : studentSearchResultBundle.studentList) {
            String course = student.course;
            if (!courses.contains(course)) {
                courses.add(course);
            }
        }
        return courses;
    }
    
    /**
     * Filters students from studentSearchResultBundle by course ID
     * @param courseId 
     * @return students whose course ID is equal to the courseId given in the parameter
     */
    private List<StudentAttributes> filterStudentsByCourse(
                                    String courseId, 
                                    StudentSearchResultBundle studentSearchResultBundle) {
        
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();
        
        for (StudentAttributes student : studentSearchResultBundle.studentList) {
            if (courseId.equals(student.course)) {
                students.add(student);
            }
        }
        return students;
    }

    private ElementTag createEditButton(String href, String tooltip) {
        return new ElementTag("href", href, "title", tooltip);
    }
    
    /**
     * Returns HTML string for student action links - view, edit, delete, all records, add comment
     * when an instructor searches for a student
     * @param student Details of the student
     * @return HTML output for the links
     */
    public String getStudentActions(StudentAttributes student, 
                                    StudentSearchResultBundle studentSearchResultBundle) {
        
        StringBuilder result = new StringBuilder();
        InstructorAttributes instructor = studentSearchResultBundle.instructors.get(student.course);
        String disabledStr = "";
        
        // View       
        if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {
            disabledStr = "disabled=\"disabled\"";
        } else {
            disabledStr = "";
        }
        
        result.append("<a class=\"btn btn-default btn-xs student-view-for-test\" "
                       + "href=\"" + getCourseStudentDetailsLink(student.course, student) + "\" "
                       + "title=\"" + Const.Tooltips.COURSE_STUDENT_DETAILS + "\" "
                       + "data-toggle=\"tooltip\" data-placement=\"top\" "
                       + disabledStr + ">");
        result.append("View");
        result.append("</a> ");
        
        // Edit
        if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {
            disabledStr = "disabled=\"disabled\"";
        } else {
            disabledStr = "";
        }

        result.append("<a class=\"btn btn-default btn-xs student-edit-for-test\" "
                       + "href=\"" + getCourseStudentEditLink(student.course, student) + "\" "
                       + "title=\"" + Const.Tooltips.COURSE_STUDENT_EDIT + "\" "
                       + "data-toggle=\"tooltip\" data-placement=\"top\" "
                       + disabledStr + ">");
        result.append("Edit");
        result.append("</a> ");
        
        // Delete
        if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {
            disabledStr = "disabled=\"disabled\"";
        } else {
            disabledStr = "";
        }

        result.append("<a class=\"btn btn-default btn-xs student-delete-for-test\" "
                       + "href=\"" + getCourseStudentDeleteLink(student.course, student) + "\" "
                       + "onclick=\"return toggleDeleteStudentConfirmation('" 
                                         + Sanitizer.sanitizeForJs(student.course) + "','" + Sanitizer.sanitizeForJs(student.name) + "')\""
                       + "title=\"" + Const.Tooltips.COURSE_STUDENT_DELETE + "\" "
                       + "data-toggle=\"tooltip\" data-placement=\"top\" "
                       + disabledStr + ">");
        result.append("Delete");
        result.append("</a> ");
        
        // All records
        result.append("<a class=\"btn btn-default btn-xs student-records-for-test\" "
                       + "href=\"" + getStudentRecordsLink(student.course, student) + "\" "
                       + "title=\"" + Const.Tooltips.COURSE_STUDENT_RECORDS + "\" "
                       + "data-toggle=\"tooltip\" data-placement=\"top\" >");
        result.append("All Records");
        result.append("</a> ");
        
        // Add comment
        if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) {
            disabledStr = "disabled=\"disabled\"";
        } else {
            disabledStr = "";
        }
        
        result.append("<div class=\"dropdown\" style=\"display: inline;\">"
                          + "<a class=\"btn btn-default btn-xs dropdown-toggle\" "
                             + "href=\"javascript:;\" "
                             + "data-toggle=\"dropdown\""
                             + disabledStr + ">");
        result.append("Add Comment");
        result.append("</a>");
        
        // Add comment dropdown
        result.append("<ul class=\"dropdown-menu\" role=\"menu\" aria-labelledby=\"dLabel\" style=\"text-align: left;\">"
                          + "<li role=\"presentation\">"
                              + "<a role=\"menuitem\" tabindex=\"-1\" "
                                  + "href=\"" + getCourseStudentDetailsLink(student.course, student) 
                                              +"&"+ Const.ParamsNames.SHOW_COMMENT_BOX+"=student\">"
                                  + "Comment on " + PageData.sanitizeForHtml(student.name)
                              + "</a>"
                          + "</li>"
                              
                          + "<li role=\"presentation\">"
                              + "<a role=\"menuitem\" tabindex=\"-1\" "
                                  + "href=\"" + getCourseStudentDetailsLink(student.course, student) 
                                              +"&"+ Const.ParamsNames.SHOW_COMMENT_BOX+"=team\">"
                                  + "Comment on " + PageData.sanitizeForHtml(student.team)
                              + "</a>"
                          + "</li>");

                
        if (!student.section.equals(Const.DEFAULT_SECTION)) {
            result.append("<li role=\"presentation\">"
                              + "<a role=\"menuitem\" tabindex=\"-1\" "
                                  + "href=\"" + getCourseStudentDetailsLink(student.course, student) 
                                              +"&"+ Const.ParamsNames.SHOW_COMMENT_BOX+"=section\">"
                                  + "Comment on " + PageData.sanitizeForHtml(student.section)
                              + "</a>"
                         + "</li>");
        }
        result.append("</ul>"
                + "</div>");        

        return result.toString();
    }
}
