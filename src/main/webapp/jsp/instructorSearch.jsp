<%@page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set" %>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.Sanitizer"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CommentAttributes"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.ui.controller.InstructorSearchPageData"%>
<%@ page import="teammates.ui.controller.PageData"%>

<%
    InstructorSearchPageData data = (InstructorSearchPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>TEAMMATES - Instructor</title>
<!-- Bootstrap core CSS -->
<link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<!-- Bootstrap theme -->
<link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
<link rel="stylesheet" href="/stylesheets/teammatesCommon.css"
    type="text/css" media="screen">
<link href="/stylesheets/omniComment.css" rel="stylesheet">
<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
<script type="text/javascript" src="/js/instructor.js"></script>
<script type="text/javascript" src="/js/instructorSearch.js"></script>
<jsp:include page="../enableJS.jsp"></jsp:include>
<!-- Bootstrap core JavaScript ================================================== -->
<script src="/bootstrap/js/bootstrap.min.js"></script>
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBody">
        <div id="frameBodyWrapper" class="container">
            <div>
                <h1>Search</h1>
            </div>
            <br>
            <div>
                <form method="get" action="<%=data.getInstructorSearchLink()%>" name="search_form">
                    <div class="well well-plain">
                    <div class="form-group">
                        <div class="input-group">
                            <input type="text" name="searchkey" value="<%=InstructorSearchPageData.sanitizeForHtml(data.searchKey)%>" title="Search for comment" placeholder="Your search keyword" class="form-control" id="searchBox">
                            <span class="input-group-btn">
                                <button class="btn btn-primary" type="submit" value="Search" id="buttonSearch">Search</button>
                            </span>
                        </div>
                        <input type="hidden" name="user" value="<%=data.account.googleId%>">
                    </div>
                    <div class="form-group">
                        <ul class="list-inline">
                            <li>
                                <span data-toggle="tooltip" title="Tick the checkboxes to limit your search to certain categories" class="glyphicon glyphicon-info-sign"></span>
                            </li>
                            <li>
                                <input id="comments-for-student-check" type="checkbox" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS%>" value="true" <%=data.isSearchCommentForStudents?"checked":""%>>
                                <label for="comments-for-student-check">Comments for students</label>
                            </li>
                            <li>
                                <input id="comments-for-responses-check" type="checkbox" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES%>" value="true" <%=data.isSearchCommentForResponses?"checked":""%>>
                                <label for="comments-for-responses-check">Comments for responses</label>
                            </li>
                            <li>
                                <input id="students-check" type="checkbox" name="<%=Const.ParamsNames.SEARCH_STUDENTS%>" value="true" <%=data.isSearchForStudents || 
                                (!data.isSearchCommentForStudents && !data.isSearchCommentForResponses)?"checked":""%>>
                                <label for="students-check">Students</label>
                            </li>
                        </ul>
                    </div>
                    </div>
                </form>
            </div>
            <br>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <% if(data.commentSearchResultBundle.getResultSize() != 0) { %>
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <strong>Comments for students</strong>
                </div>
                <div class="panel-body">
                    <%
                        int commentIdx = 0;
                        for (String giverEmailPlusCourseId : data.commentSearchResultBundle.giverCommentTable.keySet()) {//comment loop starts
                    %>
                    <div class="panel panel-info student-record-comments">
                        <div class="panel-heading">
                            From <b><%=data.commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId)%></b>
                        </div>
                        <ul class="list-group comments">
                            <%
                                for (CommentAttributes comment : data.commentSearchResultBundle.giverCommentTable.get(giverEmailPlusCourseId)) {
                                    String recipientDisplay = data.commentSearchResultBundle.recipientTable.get(comment.getCommentId().toString());
                                    commentIdx++;
                            %>
                            <li class="list-group-item list-group-item-warning"
                                class="form_comment"
                                id="form_commentedit-<%=commentIdx%>">
                                <div id="commentBar-<%=commentIdx%>">
                                    <span class="text-muted">To <b><%=recipientDisplay%></b> on
                                        <%=TimeHelper.formatTime(comment.createdAt)%></span>
                                    <a type="button" href="<%=data.getInstructorCommentsLink() + "&" + Const.ParamsNames.COURSE_ID 
                                    + "=" + comment.courseId + "#" + comment.getCommentId()%>" target="_blank" class="btn btn-default btn-xs icon-button pull-right"
                                    data-toggle="tooltip" data-placement="top" data-original-title="Edit comment in the Comments page" style="display:none;">
                                        <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>
                                    </a>
                                </div>
                                <div id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                            </li>
                            <% } %>
                        </ul>
                    </div>
                    <%
                        }//comment loop ends
                    %>
                </div>
            </div>
            <% } %>

            <% if(data.feedbackResponseCommentSearchResultBundle.getResultSize() != 0) { %>
            <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Comments for responses</strong>
                    </div>
                <%
                    int fsIndx = 0;
                    Set<String> emailList = data.feedbackResponseCommentSearchResultBundle.instructorEmails;
                  
                    for (String fsName : data.feedbackResponseCommentSearchResultBundle.questions.keySet()) {
                        List<FeedbackQuestionAttributes> questionList = data.feedbackResponseCommentSearchResultBundle.questions.get(fsName);
                        for(int i = questionList.size() - 1; i >= 0; i--) {
                                FeedbackQuestionAttributes question = questionList.get(i);
                                List<FeedbackResponseAttributes> responseList = data.feedbackResponseCommentSearchResultBundle.responses.get(question.getId());
                                for(int j = responseList.size() - 1; j >= 0; j--){
                                    FeedbackResponseAttributes feedbackResponse = responseList.get(j);
                                    List<FeedbackResponseCommentAttributes> commentList = data.feedbackResponseCommentSearchResultBundle.comments.get(feedbackResponse.getId());
                                    for(int k = commentList.size() - 1;  k >= 0; k--){
                                        FeedbackResponseCommentAttributes comment = commentList.get(k);
                                        if(emailList.contains(comment.giverEmail)){
                                            continue;
                                        }
                                        boolean isVisibilityFollowingFeedbackQuestion = comment.isVisibilityFollowingFeedbackQuestion;
                                        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion? true: comment.isVisibleTo(FeedbackParticipantType.GIVER);
                                        if(isVisibleToGiver && emailList.contains(feedbackResponse.giverEmail)){
                                            continue;
                                        }
                                        boolean isVisibleToReceiver = isVisibilityFollowingFeedbackQuestion? 
                                                    question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER): 
                                                        comment.isVisibleTo(FeedbackParticipantType.RECEIVER);
                                        if(isVisibleToReceiver && emailList.contains(feedbackResponse.recipientEmail)){
                                            continue;
                                        }
                                        boolean isVisibleToInstructor = isVisibilityFollowingFeedbackQuestion? 
                                                    question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS): 
                                                        comment.isVisibleTo(FeedbackParticipantType.INSTRUCTORS);
                                        if(isVisibleToInstructor){
                                            continue;
                                        }

                                        commentList.remove(k);
                                    }
                                    if(commentList.size() == 0){
                                        responseList.remove(j);
                                    }
                                }
                                if(responseList.size() == 0){
                                    questionList.remove(i);
                                }
                            }
                        if(questionList.size() == 0){
                            data.feedbackResponseCommentSearchResultBundle.questions.remove(fsName);
                        }
                    }

                    for (String fsName : data.feedbackResponseCommentSearchResultBundle.questions.keySet()) {//FeedbackSession loop starts
                        List<FeedbackQuestionAttributes> questionList = data.feedbackResponseCommentSearchResultBundle.questions.get(fsName);
                        fsIndx++;
                %>
                <div class="panel-body">
                <div class="row <%=fsIndx == 1?"":"border-top-gray"%>">
                    <div class="col-md-2">
                        <strong>Session: <%=fsName%> (<%=data.feedbackResponseCommentSearchResultBundle.sessions.get(fsName).courseId%>)</strong>
                    </div>
                    <div class="col-md-10">
                        <%
                                int qnIndx = 0;
                                for (FeedbackQuestionAttributes question : questionList) {//FeedbackQuestion loop starts
                                    qnIndx++;
                        %>
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <b>Question <%=question.questionNumber%></b>:
                                <%=question.getQuestionDetails().questionText%>
                                <%=question.getQuestionDetails().getQuestionAdditionalInfoHtml(question.questionNumber, "")%>
                            </div>
                            <table class="table">
                                <tbody>
                                    <%
                                        int responseIndex = 0;
                                        List<FeedbackResponseAttributes> responseList = data.feedbackResponseCommentSearchResultBundle.responses.get(question.getId());
                                        for (FeedbackResponseAttributes responseEntry : responseList) {//FeedbackResponse loop starts
                                            responseIndex++;
                                            String giverName = data.feedbackResponseCommentSearchResultBundle.responseGiverTable.get(responseEntry.getId());
                                            String recipientName = data.feedbackResponseCommentSearchResultBundle.responseRecipientTable.get(responseEntry.getId());
                                    %>
                                    <tr>
                                        <td><b>From:</b> <%=giverName%>
                                            <b>To:</b> <%=recipientName%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><strong>Response:
                                        </strong><%=responseEntry.getResponseDetails().getAnswerHtml(question.getQuestionDetails())%>
                                        </td>
                                    </tr>
                                    <tr class="active">
                                        <td>Comment(s):
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <%
                                                List<FeedbackResponseCommentAttributes> frcList = data.feedbackResponseCommentSearchResultBundle.comments.get(responseEntry.getId());
                                            %>
                                            <ul
                                                class="list-group comments"
                                                id="responseCommentTable-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>"
                                                style="<%=frcList != null && frcList.size() > 0 ? "" : "display:none"%>">
                                                <%
                                                    int responseCommentIndex = 0;
                                                                for (FeedbackResponseCommentAttributes frc : frcList) {//FeedbackResponseComments loop starts
                                                                    responseCommentIndex++;
                                                                    String frCommentGiver = data.feedbackResponseCommentSearchResultBundle.commentGiverTable.get(frc.getId().toString());
                                                %>
                                                <li
                                                    class="list-group-item list-group-item-warning"
                                                    id="responseCommentRow-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>">
                                                    <div
                                                        id="commentBar-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>">
                                                        <span class="text-muted">From:
                                                            <b><%=frCommentGiver%></b>
                                                            on <%=TimeHelper.formatTime(frc.createdAt)%>
                                                        </span>
                                                        <a type="button" href="<%=data.getInstructorCommentsLink() + "&" + Const.ParamsNames.COURSE_ID 
                                                        + "=" + frc.courseId + "#" + frc.getId()%>" target="_blank" class="btn btn-default btn-xs icon-button pull-right"
                                                        data-toggle="tooltip" data-placement="top" data-original-title="Edit comment in the Comments page" style="display:none;">
                                                            <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>
                                                        </a>
                                                    </div> <!-- frComment Content -->
                                                    <div
                                                        id="plainCommentText-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>"><%=frc.commentText.getValue()%></div>
                                                </li>
                                                <%
                                                    }//FeedbackResponseComments loop ends
                                                %>
                                            </ul>
                                        </td>
                                    </tr>
                                    <%
                                        }//FeedbackResponse loop ends
                                    %>
                                </tbody>
                            </table>
                        </div>
                        <%
                            }//FeedbackQuestion loop ends
                        %>
                    </div>
                    </div>
                    </div>
                <%
                    }//FeedbackSession loop ends
                %>
                </div>
                <% } %>

            <% if(data.studentSearchResultBundle.getResultSize() != 0) { %>
            <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Students</strong>
                    </div>
                    <div class="panel-body">

                <%
                    String currentCourse = null;
                    int studentIdx = 0;
                    int courseIdx = -1;
                    for (StudentAttributes student : data.studentSearchResultBundle.studentList) {
                        studentIdx++;
                        if(currentCourse != null && !currentCourse.equals(student.course)){
                %>                  
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                <%
                        }
                        if(currentCourse == null || (currentCourse != null && !currentCourse.equals(student.course))){
                            courseIdx++;
                            studentIdx = 0;
                            currentCourse = student.course;
                %>
                            <div class="panel panel-info">
                                <div class="panel-heading">
                                    <strong>[<%=currentCourse%>]</strong>
                                </div>
                                <div class="panel-body padding-0">
                                    <table class="table table-responsive table-striped table-bordered margin-0">
                                        <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                                            <tr>
                                                <th>Photo</th>
                                                <th id="button_sortsection-<%=courseIdx%>" class="button-sort-none" onclick="toggleSort(this, 1)">Section <span class="icon-sort unsorted"></th>
                                                <th id="button_sortteam-<%=courseIdx%>" class="button-sort-none" onclick="toggleSort(this, 2)">Team <span class="icon-sort unsorted"></th>
                                                <th id="button_sortname-<%=courseIdx%>" class="button-sort-none" onclick="toggleSort(this, 3)">Student Name <span class="icon-sort unsorted"></th>
                                                <th id="button_sortemail-<%=courseIdx%>" class="button-sort-none" onclick="toggleSort(this, 4)">Email <span class="icon-sort unsorted"></th>
                                                <th>Action(s)</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                <%
                        }
                %>
                        <tr id="student-c<%=courseIdx%>.<%=studentIdx%>" style="display: table-row;">
                            <td id="studentphoto-c<%=courseIdx%>.<%=studentIdx%>">
                                <div class="profile-pic-icon-click align-center" data-link="<%=student.getPublicProfilePictureUrl()%>">
                                    <a class="student-profile-pic-view-link btn-link" >
                                       View Photo</a>
                                    <img src="" alt="No Image Given" class="hidden">
                                </div>
                            </td>
                            <td id="studentsection-c<%=courseIdx %>.<%=studentIdx%>">
                                <%=PageData.sanitizeForHtml(student.section)%>
                            </td>
                            <td id="studentteam-c<%=courseIdx %>.<%=studentIdx%>">
                                <%=PageData.sanitizeForHtml(student.team)%>
                            </td>
                            <td id="studentname-c<%=courseIdx %>.<%=studentIdx%>">
                                <%=PageData.sanitizeForHtml(student.name)%>
                            </td>
                            <td id="studentemail-c<%=courseIdx %>.<%=studentIdx%>">
                                <%=PageData.sanitizeForHtml(student.email)%>
                            </td>
                            <td class="no-print align-center">
                                <a class="btn btn-default btn-xs student-view-for-test" 
                                    href="<%=data.getCourseStudentDetailsLink(student.course, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>"
                                    data-toggle="tooltip" data-placement="top"
                                    <% InstructorAttributes instructor = data.studentSearchResultBundle.instructors.get(student.course);
                                       if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {%>
                                       disabled="disabled"
                                    <% } %>
                                    > View
                                </a> 
                                
                                <a class="btn btn-default btn-xs student-edit-for-test"
                                    href="<%=data.getCourseStudentEditLink(student.course, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_EDIT%>"
                                    data-toggle="tooltip" data-placement="top"
                                    <% if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {%>
                                       disabled="disabled"
                                    <% } %>
                                    > Edit
                                </a> 
                                
                                <a class="btn btn-default btn-xs student-delete-for-test"
                                    href="<%=data.getCourseStudentDeleteLink(student.course, student)%>"
                                    onclick="return toggleDeleteStudentConfirmation('<%=Sanitizer.sanitizeForJs(student.course)%>','<%=Sanitizer.sanitizeForJs(student.name)%>')"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_DELETE%>"
                                    data-toggle="tooltip" data-placement="top"
                                    <% if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {%>
                                       disabled="disabled"
                                    <% } %>> Delete
                                </a>
                                
                                <a class="btn btn-default btn-xs student-records-for-test"
                                    href="<%=data.getStudentRecordsLink(student.course, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>"
                                    data-toggle="tooltip"
                                    data-placement="top"> All Records
                                </a>
                                
                                <div class="dropdown" style="display:inline;">
                                    <a class="btn btn-default btn-xs dropdown-toggle" 
                                        href="javascript:;" data-toggle="dropdown"
                                        <% if (!instructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                                            disabled="disabled"
                                        <% } %>
                                        > Add Comment
                                    </a>
                                  <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="text-align:left;">
                                    <li role="presentation"><a role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(student.course, student)
                                        +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=student"%>">
                                        Comment on <%=PageData.sanitizeForHtml(student.name)%></a></li>
                                    <li role="presentation"><a role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(student.course, student)
                                        +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=team"%>">
                                        Comment on <%=PageData.sanitizeForHtml(student.team)%></a></li>
                                    <% if(!student.section.equals(Const.DEFAULT_SECTION)) { %>
                                    <li role="presentation"><a role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(student.course, student)
                                        +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=section"%>">
                                        Comment on <%=PageData.sanitizeForHtml(student.section)%></a></li>
                                    <% } %>
                                  </ul>
                                </div>
                            </td>
                        </tr>                           
                <%
                    }
                %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                <% } %>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>