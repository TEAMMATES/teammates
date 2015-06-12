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
import teammates.ui.template.SearchCommentsForStudentsTable;
import teammates.ui.template.SearchStudentsTable;
import teammates.ui.template.SearchCommentsForResponsesTable;
import teammates.ui.template.StudentRow;

/**
 * PageData: the data to be used in the InstructorSearchPage
 */
public class InstructorSearchPageData extends PageData {

    public CommentSearchResultBundle commentSearchResultBundle = new CommentSearchResultBundle();
    public FeedbackResponseCommentSearchResultBundle feedbackResponseCommentSearchResultBundle = new FeedbackResponseCommentSearchResultBundle();
    public StudentSearchResultBundle studentSearchResultBundle = new StudentSearchResultBundle();
    public String searchKey = "";
    public int totalResultsSize;
    public boolean isSearchCommentForStudents;
    public boolean isSearchCommentForResponses;
    public boolean isSearchForStudents;
    private List<SearchCommentsForStudentsTable> searchCommentsForStudentsTables;
    private List<SearchCommentsForResponsesTable> searchCommentsForResponsesTables;
    private List<SearchStudentsTable> searchStudentsTables;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(CommentSearchResultBundle commentSearchResultBundle, 
                         FeedbackResponseCommentSearchResultBundle feedbackResponseCommentSearchResultBundle,
                             StudentSearchResultBundle studentSearchResultBundle, String searchKey, int totalResultsSize,
                                 boolean isSearchCommentForStudents, boolean isSearchCommentForResponses, boolean isSearchForStudents) {
        this.commentSearchResultBundle = commentSearchResultBundle;
        this.feedbackResponseCommentSearchResultBundle = feedbackResponseCommentSearchResultBundle;
        this.studentSearchResultBundle = studentSearchResultBundle;
        this.searchKey = searchKey;
        this.totalResultsSize = totalResultsSize;
        this.isSearchCommentForStudents = isSearchCommentForStudents;
        this.isSearchCommentForResponses = isSearchCommentForResponses;
        this.isSearchForStudents = isSearchForStudents;
        
        setSearchCommentsForStudentsTables();
        setSearchCommentsForResponsesTables();
        setSearchStudentsTables();
    }
    
    public String getSearchKey() {
        return sanitizeForHtml(searchKey);
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
    
    public CommentSearchResultBundle getCommentSearchResultBundle() {
        return commentSearchResultBundle;
    }
    
    public FeedbackResponseCommentSearchResultBundle getFeedbackResponseCommentSearchResultBundle() {
        return feedbackResponseCommentSearchResultBundle;
    }
    
    public StudentSearchResultBundle getStudentSearchResultBundle() {
        return studentSearchResultBundle;
    }
    
    public List<SearchCommentsForStudentsTable> getSearchCommentsForStudentsTables() {
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
    
    private void setSearchCommentsForStudentsTables() {
        searchCommentsForStudentsTables = new ArrayList<SearchCommentsForStudentsTable>();       
        for (String giverEmailPlusCourseId : commentSearchResultBundle.giverCommentTable.keySet()) {
            searchCommentsForStudentsTables.add(createSearchCommentsForStudentsTable(giverEmailPlusCourseId));
        }
    }
    
    private void setSearchCommentsForResponsesTables() {
        searchCommentsForResponsesTables = new ArrayList<SearchCommentsForResponsesTable>();
        searchCommentsForResponsesTables.add(createSearchCommentsForResponsesTable());
    }
    
    private void setSearchStudentsTables() {
        searchStudentsTables = new ArrayList<SearchStudentsTable>(); // 1 table for each course      
        List<String> courseIdList = getCourseIdsFromStudentSearchResultBundle(studentSearchResultBundle.studentList);
        
        for (String courseId : courseIdList) {
            searchStudentsTables.add(createSearchStudentsTable(courseId));
        }
    }
    
    private List<String> getCourseIdsFromStudentSearchResultBundle(
                                    List<StudentAttributes> studentList) {
        List<String> courses = new ArrayList<String>();
        
        for (StudentAttributes student : studentSearchResultBundle.studentList) {
            String course = student.course;
            if (!courses.contains(course)) {
                courses.add(course);
            }
        }
        return courses;
    }

    private SearchCommentsForStudentsTable createSearchCommentsForStudentsTable(String giverEmailPlusCourseId) {
        String giverDetails = commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId);
        return new SearchCommentsForStudentsTable(giverDetails, createCommentRows(giverEmailPlusCourseId));
    }
    
    private SearchCommentsForResponsesTable createSearchCommentsForResponsesTable() {
        return new SearchCommentsForResponsesTable(createFeedbackSessionRows());
    }
    
    private SearchStudentsTable createSearchStudentsTable(String courseId) {
        return new SearchStudentsTable(courseId, createStudentRows(courseId));
    }
    
    private List<FeedbackSessionRow> createFeedbackSessionRows() {
        List<FeedbackSessionRow> rows = new ArrayList<FeedbackSessionRow>();
        
        for (String fsName : feedbackResponseCommentSearchResultBundle.questions.keySet()) {
            String courseId = feedbackResponseCommentSearchResultBundle.sessions.get(fsName).courseId;
            
            rows.add(new FeedbackSessionRow(fsName, courseId, createQuestionTables(fsName)));
        }
        return rows;
    }
    
    private List<QuestionTable> createQuestionTables(String fsName) {
        List<QuestionTable> questionTables = new ArrayList<QuestionTable>();
        List<FeedbackQuestionAttributes> questionList = feedbackResponseCommentSearchResultBundle.questions.get(fsName);
        
        for (FeedbackQuestionAttributes question : questionList) {
            int questionNumber = question.questionNumber;
            String questionText = question.getQuestionDetails().questionText;
            String additionalInfo = question.getQuestionDetails().getQuestionAdditionalInfoHtml(questionNumber, "");
            
            questionTables.add(new QuestionTable(questionNumber, questionText, additionalInfo, createResponseRows(question)));
        }
        return questionTables;
    }
    
    private List<ResponseRow> createResponseRows(FeedbackQuestionAttributes question) {
        List<ResponseRow> rows = new ArrayList<ResponseRow>();
        List<FeedbackResponseAttributes> responseList = feedbackResponseCommentSearchResultBundle.responses.get(question.getId());
        
        for (FeedbackResponseAttributes responseEntry : responseList) {
            String giverName = feedbackResponseCommentSearchResultBundle.responseGiverTable.get(responseEntry.getId());
            String recipientName = feedbackResponseCommentSearchResultBundle.responseRecipientTable.get(responseEntry.getId());
            String response = responseEntry.getResponseDetails().getAnswerHtml(question.getQuestionDetails());
            
            rows.add(new ResponseRow(giverName, recipientName, response, createFeedbackResponseCommentRows(responseEntry)));
        }
        return rows;
    }
    
    private List<CommentRow> createCommentRows(String giverEmailPlusCourseId) {
        List<CommentRow> rows = new ArrayList<CommentRow>();
        String giverDetails = commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId);
        String instructorCommentsLink = getInstructorCommentsLink();
        
        for (CommentAttributes comment : commentSearchResultBundle.giverCommentTable.get(giverEmailPlusCourseId)) {          
            String recipientDetails = commentSearchResultBundle.recipientTable.get(comment.getCommentId().toString());
            String creationTime = TimeHelper.formatTime(comment.createdAt);          
            String link = instructorCommentsLink + "&" + Const.ParamsNames.COURSE_ID 
                                            + "=" + comment.courseId + "#" + comment.getCommentId();           
            ElementTag editButton = createEditButton(link, Const.Tooltips.COMMENT_EDIT_IN_COMMENTS_PAGE);
            
            rows.add(new CommentRow(giverDetails, comment, recipientDetails, creationTime, editButton));
        }       
        return rows;
    }
    
    private List<FeedbackResponseCommentRow> createFeedbackResponseCommentRows(FeedbackResponseAttributes responseEntry) {
        List<FeedbackResponseCommentRow> rows = new ArrayList<FeedbackResponseCommentRow>();
        List<FeedbackResponseCommentAttributes> frcList = feedbackResponseCommentSearchResultBundle
                                                              .comments.get(responseEntry.getId());
        
        for (FeedbackResponseCommentAttributes frc : frcList) {
            String frCommentGiver = feedbackResponseCommentSearchResultBundle
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
    
    private List<StudentRow> createStudentRows(String courseId) {
        List<StudentRow> rows = new ArrayList<StudentRow>();      
        List<StudentAttributes> studentsInCourse = filterStudentsByCourse(courseId);
        
        for (StudentAttributes student : studentsInCourse) {
            String viewPhotoLink = addUserIdToUrl(student.getPublicProfilePictureUrl());        
            String actions = getStudentActions(student);

            rows.add(new StudentRow(viewPhotoLink, student.section, student.team, student.name, student.email, actions));
        }
        return rows;
    }
    
    /**
     * Filters students from studentSearchResultBundle by course ID
     * @param courseId 
     * @return students whose course ID is equal to the courseId given in the parameter
     */
    private List<StudentAttributes> filterStudentsByCourse(String courseId) {
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
    public String getStudentActions(StudentAttributes student) {
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
