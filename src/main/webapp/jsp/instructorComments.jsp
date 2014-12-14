<%@page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@page import="teammates.common.datatransfer.CommentSendingState"%>
<%@page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@page import="teammates.common.datatransfer.CommentRecipientType"%>
<%@page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@page import="teammates.common.datatransfer.StudentAttributes"%>
<%@page import="teammates.common.datatransfer.CommentStatus"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.SessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentResultBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalSubmissionPageData"%>
<%@ page import="teammates.ui.controller.InstructorCommentsPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>

<%
    InstructorCommentsPageData data = (InstructorCommentsPageData) request.getAttribute("data");
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
<script src="/js/omniComment.js"></script>
<script type="text/javascript" src="/js/feedbackResponseComments.js"></script>
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
            <div id="topOfPage"></div>
            <div class="inner-container">
                <div class="row">
                    <div class="col-sm-6">
                        <h1>Comments from Instructors</h1>
                    </div>
                    <div class="col-sm-6 instructor-header-bar">
                        <form method="get" action="<%=data.getInstructorSearchLink()%>"
                            name="search_form">
                            <div class="input-group">
                                <input type="text" name="<%=Const.ParamsNames.SEARCH_KEY%>"
                                    title="Search for comment"
                                    class="form-control"
                                    placeholder="Any info related to comments"
                                    id="searchBox"> 
                                <span class="input-group-btn">
                                    <button class="btn btn-default"
                                        type="submit" value="Search"
                                        id="buttonSearch">Search</button>
                                </span>
                            </div>
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS%>" value="true">
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES%>" value="true">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                        </form>
                    </div>
                </div>
            </div>
            <br>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <div class="well well-plain">
                <div class="row">
                    <div class="col-md-2">
                        <div class="checkbox">
                            <input id="option-check" type="checkbox">
                            <label for="option-check">Show More
                                Options</label>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="checkbox">
                            <input id="displayArchivedCourses_check"
                                type="checkbox"
                                <%=data.isDisplayArchive ? "checked=\"checked\"" : ""%>>
                            <label for="displayArchivedCourses_check">Include
                                Archived Courses</label>
                            <div id="displayArchivedCourses_link" style="display:none;">
                                <a href="<%=data.getInstructorCommentsLink()%>">link back to the page</a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div id="more-options" class="well well-plain">
                        <form class="form-horizontal" role="form">
                            <div class="row">
                                <div class="col-sm-4">
                                    <div class="text-color-primary">
                                        <strong>Show comments for: </strong>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input id="panel_all"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="panel_all"><strong>All</strong></label>
                                    </div>
                                    <br>
                                    <% int panelIdx = 0; %>
                                    <% if(data.comments.keySet().size() != 0){ 
                                           panelIdx++;
                                    %>
                                    <div class="checkbox">
                                        <input id="panel_check-<%=panelIdx%>"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="panel_check-<%=panelIdx%>">
                                            Students </label>
                                    </div>
                                    <% } %>
                                    <% for(FeedbackSessionAttributes fs : data.feedbackSessions){ 
                                           panelIdx++;
                                    %>
                                    <div class="checkbox">
                                        <input id="panel_check-<%=panelIdx%>"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="panel_check-<%=panelIdx%>">
                                            Session: <%=fs.feedbackSessionName%> </label>
                                    </div>
                                    <% } %>
                                </div>
                                <div class="col-sm-4">
                                    <div class="text-color-primary">
                                        <strong>Show comments from: </strong>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input type="checkbox" value=""
                                            id="giver_all"
                                            checked="checked"> <label
                                            for="giver_all"><strong>All</strong></label>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input id="giver_check-by-you"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="giver_check-by-you">
                                            You </label>
                                    </div>
                                    <div class="checkbox">
                                        <input id="giver_check-by-others"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="giver_check-by-others">
                                            Others </label>
                                    </div>
                                </div>
                                <div class="col-sm-4">
                                    <div class="text-color-primary">
                                        <strong>Show comments with status: </strong>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input type="checkbox" value=""
                                            id="status_all"
                                            checked="checked"> <label
                                            for="status_all"><strong>All</strong></label>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input id="status_check-public"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="status_check-public">
                                            Public </label>
                                    </div>
                                    <div class="checkbox">
                                        <input id="status_check-private"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="status_check-private">
                                            Private </label>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <% if(data.coursePaginationList.size() > 0) { %>
            <ul class="pagination">
                <li><a href="<%=data.previousPageLink%>">«</a></li>
                <!--<li class="<%=data.isViewingDraft ? "active" : ""%>"><a
                    href="<%=data.getInstructorCommentsLink()%>">Drafts</a></li>-->
                <%
                    for (String courseId : data.coursePaginationList) {
                %>
                <li
                    class="<%=!data.isViewingDraft && courseId.equals(data.courseId) ? "active" : ""%>">
                    <a
                    href="<%=data.getInstructorCommentsLink() + "&courseid=" + courseId%>"><%=courseId%></a>
                </li>
                <%
                    }
                %>
                <li><a href="<%=data.nextPageLink%>">»</a></li>
            </ul>
            <div class="well well-plain">
                <div class="row">
                    <h4 class="col-sm-9 text-color-primary">
                        <strong> <%=data.isViewingDraft ? "Drafts" : data.courseName%>
                        </strong>
                    </h4>
                    <div class="btn-group pull-right" style="<%=data.numberOfPendingComments==0?"display:none":""%>">
                      <a type="button" class="btn btn-sm btn-info" data-toggle="tooltip" style="margin-right: 17px;"
                         href="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_CLEAR_PENDING + "?" + Const.ParamsNames.COURSE_ID + "=" + data.courseId
                                        + "&" + Const.ParamsNames.USER_ID + "=" + data.account.googleId%>"
                         title="Send email notification to <%=data.numberOfPendingComments%> recipient(s) of comments pending notification">
                        <span class="badge" style="margin-right: 5px"><%=data.numberOfPendingComments%></span>
                        <span class="glyphicon glyphicon-comment"></span>
                        <span class="glyphicon glyphicon-arrow-right"></span>
                        <span class="glyphicon glyphicon-envelope"></span>
                      </a>
                    </div>
                </div>
                <div id="no-comment-panel" style="<%=data.comments.keySet().size() == 0 && data.feedbackSessions.size() == 0?"":"display:none;"%>">
                    <br>
                    <div class="panel">
                        <div class="panel-body">
                            You don't have any comment in this course.
                        </div>
                    </div>
                </div>
                <%  panelIdx = 0;
                    if(data.comments.keySet().size() != 0){// check student comments starts 
                        panelIdx++;
                %>
                <div id="panel_display-<%=panelIdx%>">
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong><%=data.isViewingDraft ? "Comment drafts" : "Comments for students"%></strong>
                    </div>
                    <div class="panel-body">
                        <%=data.isViewingDraft ? "Your comments that are not finished:" : ""%>
                        <%
                            int commentIdx = 0;
                            int studentIdx = 0;
                            for (String giverEmail : data.comments.keySet()) {//recipient loop starts
                                studentIdx++;
                        %>
                        <div class="panel panel-info student-record-comments <%=giverEmail.equals(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST)?"giver_display-by-you":"giver_display-by-others"%>"
                            <% if (data.comments.get(giverEmail).isEmpty()) { %> style="display: none;" <% } %>>
                            <div class="panel-heading">
                                From <b><%=data.getGiverName(giverEmail)%></b>
                            </div>
                            <ul class="list-group comments">
                                <%
                                    CommentRecipientType recipientTypeForThisRecipient = CommentRecipientType.PERSON;//default value is PERSON
                                    for (CommentAttributes comment : data.comments.get(giverEmail)) {//student comments loop starts
                                            commentIdx++;
                                            recipientTypeForThisRecipient = comment.recipientType;
                                %>
                                <li id="<%=comment.getCommentId()%>"
                                    class="list-group-item list-group-item-warning <%=comment.showCommentTo.size()>0?"status_display-public":"status_display-private"%>">
                                    <form method="post"
                                        action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT%>"
                                        name="form_commentedit"
                                        class="form_comment"
                                        id="form_commentedit-<%=commentIdx%>">
                                        <div id="commentBar-<%=commentIdx%>">
                                            
                                            <span class="text-muted">To <%=data.getRecipientNames(comment.recipients)%> on
                                                <%=TimeHelper.formatTime(comment.createdAt)%></span>
                                            <%
                                               if (comment.giverEmail.equals(data.instructorEmail)
                                                       || (data.currentInstructor != null && 
                                                       data.isInstructorAllowedForPrivilegeOnComment(comment, 
                                                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS))) {//comment edit/delete control starts
                                            %>
                                            <a type="button"
                                                id="commentdelete-<%=commentIdx%>"
                                                class="btn btn-default btn-xs icon-button pull-right"
                                                onclick="return deleteComment('<%=commentIdx%>');"
                                                data-toggle="tooltip"
                                                data-placement="top"
                                                title=""
                                                data-original-title="<%=Const.Tooltips.COMMENT_DELETE%>"
                                                style="display: none;">
                                                <span
                                                class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                            </a> <a type="button"
                                                id="commentedit-<%=commentIdx%>"
                                                class="btn btn-default btn-xs icon-button pull-right"
                                                onclick="return enableEdit('<%=commentIdx%>');"
                                                data-toggle="tooltip"
                                                data-placement="top"
                                                title=""
                                                data-original-title="<%=Const.Tooltips.COMMENT_EDIT%>"
                                                style="display: none;">
                                                <span
                                                class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                            </a>
                                            <% }//comment edit/delete control ends %>
                                            <% if(comment.showCommentTo.size() > 0){ 
                                                   String peopleCanSee = data.getTypeOfPeopleCanViewComment(comment);
                                            %>
                                            <span class="glyphicon glyphicon-eye-open" data-toggle="tooltip" style="margin-left: 5px;"
                                                data-placement="top"
                                                title="This comment is visible to <%=peopleCanSee%>"></span>
                                            <% } %>
                                            <% 
                                               if(comment.sendingState == CommentSendingState.PENDING){ 
                                            %>
                                            <span class="glyphicon glyphicon-bell" data-toggle="tooltip" 
                                                data-placement="top"
                                                title="This comment is pending notification. i.e., you have not sent a notification about this comment yet"></span>
                                            <% } %>
                                        </div>
                                        <div
                                            id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                                        <%
                                           if (comment.giverEmail.equals(data.instructorEmail)
                                        		   || (data.currentInstructor != null && 
                                                   data.isInstructorAllowedForPrivilegeOnComment(comment, 
                                                           Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS))) {//comment edit/delete control starts
                                        %>
                                        <div
                                            id="commentTextEdit<%=commentIdx%>"
                                            style="display: none;">
                                           <div class="form-group form-inline">
                                                <div class="form-group text-muted">
                                                    You may change comment's visibility using the visibility options on the right hand side.
                                                </div>
                                                <a id="visibility-options-trigger<%=commentIdx%>"
                                                    class="btn btn-sm btn-info pull-right">
                                                    <span class="glyphicon glyphicon-eye-close"></span>
                                                    Show Visibility Options
                                                </a>
                                            </div>
                                            <div id="visibility-options<%=commentIdx%>" class="panel panel-default"
                                                style="display: none;">
                                                <div class="panel-heading">Visibility Options</div>
                                                <table class="table text-center text-color-black">
                                                    <tbody>
                                                        <tr>
                                                            <th class="text-center">User/Group</th>
                                                            <th class="text-center">Can see
                                                                your comment</th>
                                                            <th class="text-center">Can see
                                                                giver's name</th>
                                                            <th class="text-center">Can see
                                                                recipient's name</th>
                                                        </tr>
                                                        <% if(comment.recipientType == CommentRecipientType.PERSON){ %>
                                                        <tr id="recipient-person<%=commentIdx%>">
                                                            <td class="text-left">
                                                                <div data-toggle="tooltip"
                                                                    data-placement="top" title=""
                                                                    data-original-title="Control what comment recipient(s) can view">
                                                                    Recipient(s)</div>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox answerCheckbox"
                                                                type="checkbox" value="<%=CommentRecipientType.PERSON%>"
                                                                <%=comment.showCommentTo.contains(CommentRecipientType.PERSON)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox giverCheckbox"
                                                                type="checkbox" value="<%=CommentRecipientType.PERSON%>"
                                                                <%=comment.showGiverNameTo.contains(CommentRecipientType.PERSON)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox recipientCheckbox"
                                                                name="receiverFollowerCheckbox"
                                                                type="checkbox" value="<%=CommentRecipientType.PERSON%>"
                                                                disabled="disabled"></td>
                                                        </tr>
                                                        <% } %>
                                                        <% if(comment.recipientType == CommentRecipientType.PERSON
                                                                || comment.recipientType == CommentRecipientType.TEAM){ %>
                                                        <tr id="recipient-team<%=commentIdx%>">
                                                            <td class="text-left">
                                                                <div data-toggle="tooltip"
                                                                    data-placement="top" title=""
                                                                    data-original-title="Control what team members of comment recipients can view">
                                                                    <%=comment.recipientType == CommentRecipientType.TEAM? "Recipient Team" : "Recipient's Team" %></div>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox answerCheckbox"
                                                                type="checkbox"
                                                                value="<%=CommentRecipientType.TEAM%>"
                                                                <%=comment.showCommentTo.contains(CommentRecipientType.TEAM)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox giverCheckbox"
                                                                type="checkbox"
                                                                value="<%=CommentRecipientType.TEAM%>"
                                                                <%=comment.showGiverNameTo.contains(CommentRecipientType.TEAM)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox recipientCheckbox"
                                                                type="checkbox"
                                                                value="<%=CommentRecipientType.TEAM%>"
                                                                <%=comment.recipientType == CommentRecipientType.TEAM? "disabled=\"disabled\"" : "" %>
                                                                <%=comment.showRecipientNameTo.contains(CommentRecipientType.TEAM)?"checked=\"checked\"":""%>>
                                                            </td>
                                                        </tr>
                                                        <% } %>
                                                        <% if(comment.recipientType != CommentRecipientType.COURSE){ %>
                                                        <tr id="recipient-section<%=commentIdx%>">
                                                            <td class="text-left">
                                                                <div data-toggle="tooltip"
                                                                    data-placement="top" title=""
                                                                    data-original-title="Control what students in the same section can view">
                                                                    <%=comment.recipientType == CommentRecipientType.SECTION? "Recipient Section" : "Recipient's Section" %></div>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox answerCheckbox"
                                                                type="checkbox"
                                                                value="<%=CommentRecipientType.SECTION%>"
                                                                <%=comment.showCommentTo.contains(CommentRecipientType.SECTION)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox giverCheckbox"
                                                                type="checkbox"
                                                                value="<%=CommentRecipientType.SECTION%>"
                                                                <%=comment.showGiverNameTo.contains(CommentRecipientType.SECTION)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox recipientCheckbox"
                                                                type="checkbox"
                                                                value="<%=CommentRecipientType.SECTION%>"
                                                                <%=comment.recipientType == CommentRecipientType.SECTION? "disabled=\"disabled\"" : "" %>
                                                                <%=comment.showRecipientNameTo.contains(CommentRecipientType.SECTION)?"checked=\"checked\"":""%>>
                                                            </td>
                                                        </tr>
                                                        <% } %>
                                                        <tr id="recipient-course<%=commentIdx%>">
                                                            <td class="text-left">
                                                                <div data-toggle="tooltip"
                                                                    data-placement="top" title=""
                                                                    data-original-title="Control what other students in this course can view">
                                                                    <%=comment.recipientType == CommentRecipientType.COURSE? "Students in this course" : "Other students in this course" %></div>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox answerCheckbox"
                                                                type="checkbox" value="<%=CommentRecipientType.COURSE%>"
                                                                <%=comment.showCommentTo.contains(CommentRecipientType.COURSE)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox giverCheckbox"
                                                                type="checkbox" value="<%=CommentRecipientType.COURSE%>"
                                                                <%=comment.showGiverNameTo.contains(CommentRecipientType.COURSE)?"checked=\"checked\"":""%>>
                                                            </td>
                                                            <td><input
                                                                class="visibilityCheckbox recipientCheckbox"
                                                                type="checkbox" value="<%=CommentRecipientType.COURSE%>"
                                                                <%=comment.recipientType == CommentRecipientType.COURSE? "disabled=\"disabled\"" : "" %>
                                                                <%=comment.showRecipientNameTo.contains(CommentRecipientType.COURSE)?"checked=\"checked\"":""%>>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td class="text-left">
                                                                <div data-toggle="tooltip"
                                                                    data-placement="top" title=""
                                                                    data-original-title="Control what instructors can view">
                                                                    Instructors</div>
                                                        </td>
                                                        <td><input
                                                            class="visibilityCheckbox answerCheckbox"
                                                            type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>"
                                                            <%=comment.showCommentTo.contains(CommentRecipientType.INSTRUCTOR)?"checked=\"checked\"":""%>>
                                                        </td>
                                                        <td><input
                                                            class="visibilityCheckbox giverCheckbox"
                                                            type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>"
                                                            <%=comment.showGiverNameTo.contains(CommentRecipientType.INSTRUCTOR)?"checked=\"checked\"":""%>>
                                                        </td>
                                                            <td><input
                                                                class="visibilityCheckbox recipientCheckbox"
                                                                type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>"
                                                                <%=comment.showRecipientNameTo.contains(CommentRecipientType.INSTRUCTOR)?"checked=\"checked\"":""%>>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <div class="form-group">
                                                <textarea
                                                    class="form-control"
                                                    rows="3"
                                                    placeholder="Your comment about this student"
                                                    name=<%=Const.ParamsNames.COMMENT_TEXT%>
                                                    id="commentText<%=commentIdx%>"><%=comment.commentText.getValue()%></textarea>
                                            </div>
                                            <div class="col-sm-offset-5">
                                                <input
                                                    id="commentsave-<%=commentIdx%>"
                                                    title="Save comment"
                                                    onclick="return submitCommentForm('<%=commentIdx%>');"
                                                    type="submit"
                                                    class="btn btn-primary"
                                                    value="Save">
                                                <input type="button"
                                                    class="btn btn-default"
                                                    value="Cancel"
                                                    onclick="return disableComment('<%=commentIdx%>');">
                                            </div>
                                        </div>
                                        <input type="hidden"
                                            name=<%=Const.ParamsNames.COMMENT_EDITTYPE%>
                                            id="<%=Const.ParamsNames.COMMENT_EDITTYPE%>-<%=commentIdx%>"
                                            value="edit">
                                        <input type="hidden"
                                            name=<%=Const.ParamsNames.COMMENT_ID%>
                                            value="<%=comment.getCommentId()%>">
                                        <input type="hidden"
                                            name=<%=Const.ParamsNames.COURSE_ID%>
                                            value="<%=data.courseId%>">
                                        <input type="hidden"
                                            name=<%=Const.ParamsNames.FROM_COMMENTS_PAGE%>
                                            value="true"> 
                                        <input type="hidden" 
                                            name=<%=Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO%> 
                                            value="<%=data.removeBracketsForArrayString(comment.showCommentTo.toString())%>">
                                        <input type="hidden" 
                                            name=<%=Const.ParamsNames.COMMENTS_SHOWGIVERTO%> 
                                            value="<%=data.removeBracketsForArrayString(comment.showGiverNameTo.toString())%>">
                                        <input type="hidden" 
                                            name=<%=Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO%> 
                                            value="<%=data.removeBracketsForArrayString(comment.showRecipientNameTo.toString())%>">
                                        <input type="hidden"
                                            name="<%=Const.ParamsNames.USER_ID%>"
                                            value="<%=data.account.googleId%>">
                                        <% }//comment edit/delete control ends %>
                                    </form>
                                </li>
                                <%
                                    }//student comments loop ends
                                %>
                            </ul>
                        </div>
                        <%
                            }//recipient loop ends
                        %>
                    </div>
                </div>
                </div>
                <% }// check student comments ends %>
                <%
                    int fsIndx = 0;
                    for (FeedbackSessionAttributes fs : data.feedbackSessions) {//FeedbackSession loop starts
                        String fsName = fs.feedbackSessionName;
                        fsIndx++;
                        panelIdx++;
                %>
                <div id="panel_display-<%=panelIdx%>">
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading" onclick="loadFeedbackResponseComments('<%=data.account.googleId%>','<%=data.courseId%>','<%=fsName%>', this);"
                        style="cursor: pointer;">
                        <strong>Comments in session: <%=fsName%></strong>
                        <div class="placeholder-img-loading pull-right"></div>
                    </div>
                    <div class="panel-body hidden">
                        <div class="placeholder-error-msg-<%=panelIdx%> hidden">
                            <div class="panel panel-info">
                                <ul class="list-group comments">
                                    <li class="list-group-item list-group-item-danger">
                                        Failed to load response comments for this session. Please try again later.
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                </div>
                <%
                    }//FeedbackSession loop ends
                %>
            </div>
            <ul class="pagination">
                <li><a href="<%=data.previousPageLink%>">«</a></li>
                <!--<li class="<%=data.isViewingDraft ? "active" : ""%>"><a
                    href="<%=data.getInstructorCommentsLink()%>">Drafts</a></li>-->
                <%
                    for (String courseId : data.coursePaginationList) {
                %>
                <li
                    class="<%=!data.isViewingDraft && courseId.equals(data.courseId) ? "active" : ""%>">
                    <a
                    href="<%=data.getInstructorCommentsLink() + "&courseid=" + courseId%>"><%=courseId%></a>
                </li>
                <%
                    }
                %>
                <li><a href="<%=data.nextPageLink%>">»</a></li>
            </ul>
            <% } else { %>
            <div id="statusMessage" class="alert alert-warning">
                There is no comment to display
            </div>
                
            <% } %>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>