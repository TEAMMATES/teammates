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
<%@ page
    import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackSessionResultsBundle"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.SessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentResultBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page
    import="teammates.ui.controller.InstructorEvalSubmissionPageData"%>
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

<script type="text/javascript" src="/js/instructor.js"></script>
<script src="/js/omniComment.js"></script>
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
                    <div class="col-sm-5">
                        <h1>Instructor Comments</h1>
                    </div>
                    <div class="col-sm-5 instructor-header-bar">
                        <form method="post" action="#"
                            name="search_form">
                            <div class="input-group">
                                <input type="text" name="searchkey"
                                    title="Search for comment"
                                    class="form-control"
                                    placeholder="Any info related to comments"
                                    id="searchBox"> <span
                                    class="input-group-btn">
                                    <button class="btn btn-default"
                                        type="submit" value="Search"
                                        id="buttonSearch">Search</button>
                                </span>
                            </div>
                        </form>
                    </div>
                    <div class="col-md-1 instructor-header-bar">
                        <a class="btn btn-primary btn-md"
                            href="./omniComment_bulkEdit.html">
                            Comment in Bulk </a>
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
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div id="more-options" class="well well-plain">
                        <form class="form-horizontal" role="form">
                            <div class="row">
                                <div class="col-sm-4">
                                    <div class="text-color-primary">
                                        <strong>Comment Panels</strong>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input id="team_all"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="team_all"><strong>Display
                                                All</strong></label>
                                    </div>
                                    <br>

                                    <div class="checkbox">
                                        <input id="team_check-0-0"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="team_check-0-0">
                                            Drafts </label>
                                    </div>

                                    <div class="checkbox">
                                        <input id="team_check-0-1"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="team_check-0-1">
                                            Comments about students </label>
                                    </div>

                                    <div class="checkbox">
                                        <input id="team_check-0-2"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="team_check-0-2">
                                            Comments in Feedback
                                            Session: the First Feedback
                                            Session </label>
                                    </div>
                                </div>
                                <div class="col-sm-4">
                                    <div class="text-color-primary">
                                        <strong>Teams</strong>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input type="checkbox" value=""
                                            id="course_all"
                                            checked="checked"> <label
                                            for="course_all"><strong>Display
                                                all</strong></label>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input id="course_check-0"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="course_check-0">
                                            [CS2103 Aug 2013] : Team 0 </label>
                                    </div>
                                    <div class="checkbox">
                                        <input id="course_check-1"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="course_check-1">
                                            [CS2103 Aug 2013] : Team 1 </label>
                                    </div>
                                    <div class="checkbox">
                                        <input id="course_check-2"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="course_check-2">
                                            [CS2103 Aug 2013] : Team 2 </label>
                                    </div>
                                    <div class="checkbox">
                                        <input id="course_check-3"
                                            type="checkbox"
                                            checked="checked"> <label
                                            for="course_check-3">
                                            [CS2103 Aug 2013] : Team 3 </label>
                                    </div>
                                </div>
                                <div class="col-sm-4">
                                    <div class="text-color-primary">
                                        <strong>Emails</strong>
                                    </div>
                                    <br>
                                    <div class="checkbox">
                                        <input id="show_email"
                                            type="checkbox"> <label
                                            for="show_email"><strong>Show
                                                Emails</strong></label>
                                    </div>
                                    <br>
                                    <div id="emails"
                                        style="display: none;">

                                        <div id="student_email-c0.0"
                                            style="display: block;">alice.b.tmms@gmail.com</div>

                                        <div id="student_email-c0.1"
                                            style="display: block;">benny.c.tmms@gmail.com</div>

                                        <div id="student_email-c0.2"
                                            style="display: block;">danny.e.tmms@gmail.com</div>

                                        <div id="student_email-c0.3"
                                            style="display: block;">emma.f.tmms@gmail.com</div>

                                        <div id="student_email-c0.4"
                                            style="display: block;">charlie.d.tmms@gmail.com</div>

                                        <div id="student_email-c0.5"
                                            style="display: block;">francis.g.tmms@gmail.com</div>

                                        <div id="student_email-c0.6"
                                            style="display: block;">gene.h.tmms@gmail.com</div>

                                        <div id="student_email-c0.7"
                                            style="display: block;">Kai@Kai</div>

                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <ul class="pagination">
                <%
                    //TODO: use js to handle navigation links
                %>
                <li><a href="#">«</a></li>
                <li class="<%=data.isViewingDraft ? "active" : ""%>"><a
                    href="<%=data.getInstructorCommentsLink()%>">Drafts</a></li>
                <%
                    for (String courseId : data.courseIdList) {
                %>
                <li
                    class="<%=!data.isViewingDraft && courseId.equals(data.courseIdToView) ? "active" : ""%>">
                    <a
                    href="<%=data.getInstructorCommentsLink() + "&courseid=" + courseId%>"><%=courseId%></a>
                </li>
                <%
                    }
                %>
                <li><a href="#">»</a></li>
            </ul>
            <div class="well well-plain">
                <div class="text-color-primary">
                    <h4>
                        <strong> <%=data.isViewingDraft ? "Drafts" : data.courseNameToView%>
                        </strong>
                    </h4>
                </div>
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong><%=data.isViewingDraft ? "Comment drafts" : "Comments about students"%></strong>
                    </div>
                    <div class="panel-body">
                        <%=data.isViewingDraft ? "Your comments that are not finished:" : "Your comments on student in this course:"%>
                        <%
                            for (String recipient : data.comments.keySet()) {
                        %>
                        <%
                            StudentAttributes student = data.students.get(recipient);
                                Boolean isRecipientStudent = student != null;
                        %>
                        <div
                            class="panel panel-info student-record-comments">
                            <div class="panel-heading">
                                From <b>you</b> to <b><%=isRecipientStudent ? student.name : recipient%></b>
                                <%=isRecipientStudent ? " (" + student.team + ", <a href=\"mailto:" + student.email + "\">" + student.email + "</a>)" : ""%>
                            </div>
                            <ul class="list-group">
                                <%
                                    for (CommentAttributes comment : data.comments.get(recipient)) {
                                %>
                                <li
                                    class="list-group-item list-group-item-warning">
                                    <div id="commentBar0">
                                        <span class="text-muted">on
                                            <%=TimeHelper.formatTime(comment.createdAt)%></span>
                                        <a type="button"
                                            id="commentdelete-0"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            onclick="return deleteComment('0');"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            title=""
                                            data-original-title="Delete this comment"
                                            style="display: none;">
                                            <span
                                            class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                        </a> <a type="button"
                                            id="commentedit-0"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            onclick="return enableEdit('0', '1');"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            title=""
                                            data-original-title="Edit this comment with advanced editor"
                                            style="display: none;">
                                            <span
                                            class="glyphicon glyphicon-th glyphicon-primary"></span>
                                        </a> <a type="button"
                                            id="commentedit-0"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            onclick="return enableEdit('0', '1');"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            title=""
                                            data-original-title="Edit this comment"
                                            style="display: none;">
                                            <span
                                            class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                        </a>
                                    </div>
                                    <div id="plainCommentText0"><%=comment.commentText.getValue()%></div>
                                </li>
                                <%
                                    }
                                %>
                            </ul>
                        </div>
                        <%
                            }
                        %>
                        <%
                            if (data.comments.size() == 0) {
                        %>
                        <div
                            class="panel panel-warning student-record-comments">
                            <div class="panel-heading">You don't
                                have any comment here.</div>
                        </div>
                        <%
                            }
                        %>
                    </div>
                </div>
                <% for(String fsName: data.fsNameTofeedbackQuestionsMap.keySet()){//FeedbackSession loop starts %>
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Feedback Session: <%=fsName%></strong>
                    </div>
                    <div class="panel-body">
                        <% List<FeedbackQuestionAttributes> fqList = data.fsNameTofeedbackQuestionsMap.get(fsName);
                           for(FeedbackQuestionAttributes fq : fqList){//FeedbackQuestion loop starts %>
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <%//TODO: handle this [More] dropdown %>
                                <b>Question <%=fq.questionNumber%>:</b> <%=fq.getQuestionDetails().questionText%> [More]
                            </div>
                            <table class="table">
                                <tbody>
                                    <% List<FeedbackResponseAttributes> frList = data.questionIdToFeedbackResponsesMap.get(fq.getId());
                                       for(FeedbackResponseAttributes fr : frList){//FeedbackResponse loop starts
                                           StudentAttributes giver = data.students.get(fr.giverEmail);
                                           String giverName = giver == null? fr.giverEmail: giver.name;
                                           
                                           StudentAttributes recipient = data.students.get(fr.recipientEmail);
                                           String recipientName = recipient == null? fr.recipientEmail: recipient.name;
                                    %>
                                    <tr>
                                        <td>
                                            <b>From:</b> <%=giverName%> <%=giver == null? "": "(" + giver.team + ")"%>
                                            <b>To:</b> <%=recipientName%> <%=recipient == null? "": "(" + recipient.team + ")"%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><strong>Response:
                                        </strong><%=fr.getResponseDetails().getAnswerHtml()%>
                                        </td>
                                    </tr>
                                    <tr class="active">
                                        <td>Comment:
                                            <button type="button"
                                                class="btn btn-default btn-xs icon-button pull-right"
                                                id="button_add_comment"
                                                onclick="showAddCommentBox();"
                                                data-toggle="tooltip"
                                                data-placement="top"
                                                title=""
                                                data-original-title="Add comment">
                                                <span
                                                    class="glyphicon glyphicon-comment glyphicon-primary"></span>
                                            </button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <ul class="list-group">
                                                <% List<FeedbackResponseCommentAttributes> frcList = data.responseIdToFrCommentsMap.get(fr.getId());
                                                   for(FeedbackResponseCommentAttributes frc : frcList){//FeedbackResponseComments loop starts
                                                %>
                                                <li
                                                    class="list-group-item list-group-item-warning">
                                                    <div
                                                        id="commentBar0">
                                                        <span
                                                            class="text-muted">on <%=TimeHelper.formatTime(frc.createdAt)%>
                                                        </span> <a
                                                            type="button"
                                                            id="commentdelete-0"
                                                            class="btn btn-default btn-xs icon-button pull-right"
                                                            onclick="return deleteComment('0');"
                                                            data-toggle="tooltip"
                                                            data-placement="top"
                                                            title=""
                                                            data-original-title="Delete this comment"
                                                            style="display: none;">
                                                            <span
                                                            class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                                        </a> <a
                                                            type="button"
                                                            id="commentedit-0"
                                                            class="btn btn-default btn-xs icon-button pull-right"
                                                            onclick="return enableEdit('0', '1');"
                                                            data-toggle="tooltip"
                                                            data-placement="top"
                                                            title=""
                                                            data-original-title="Edit this comment with advanced editor"
                                                            style="display: none;">
                                                            <span
                                                            class="glyphicon glyphicon-th glyphicon-primary"></span>
                                                        </a> <a
                                                            type="button"
                                                            id="commentedit-0"
                                                            class="btn btn-default btn-xs icon-button pull-right"
                                                            onclick="return enableEdit('0', '1');"
                                                            data-toggle="tooltip"
                                                            data-placement="top"
                                                            title=""
                                                            data-original-title="Edit this comment"
                                                            style="display: none;">
                                                            <span
                                                            class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                                        </a>
                                                    </div>
                                                    <div
                                                        id="plainCommentText0"><%=frc.commentText.getValue()%></div>
                                                </li>
                                                <% }//FeedbackResponseComments loop ends %>
                                            </ul>
                                        </td>
                                    </tr>
                                    <% }//FeedbackResponse loop ends %>
                                </tbody>
                            </table>
                        </div>
                        <% }//FeedbackQuestion loop ends %>
                    </div>
                </div>
                <% }//FeedbackSession loop ends %>
            </div>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>