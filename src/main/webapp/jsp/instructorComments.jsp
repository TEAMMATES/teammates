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
                <div class="text-color-primary">
                    <h4>
                        <strong> <%=data.isViewingDraft ? "Drafts" : data.courseName%>
                        </strong>
                    </h4>
                </div>
                <br>
                <%
                    if (data.comments.size() == 0) {//comment size check starts
                %>
                <div
                    class="panel student-record-comments">
                    <div class="panel-heading">You don't
                        have any comment in this course.</div>
                </div>
                <%
                    } else {
                %>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong><%=data.isViewingDraft ? "Comment drafts" : "Comments about students"%></strong>
                    </div>
                    <div class="panel-body">
                        <%=data.isViewingDraft ? "Your comments that are not finished:" : "Your comments on student in this course:"%>
                        <%
                            int commentIdx = 0;
                            int studentIdx = 0;
                            for (String recipient : data.comments.keySet()) {//recipient loop starts
                                studentIdx++;
                        %>
                        <%
                            StudentAttributes student = data.roster.getStudentForEmail(recipient);
                                Boolean isRecipientStudent = student != null;
                        %>
                        <div
                            class="panel panel-info student-record-comments">
                            <div class="panel-heading">
                                From <b>you</b> to <b><%=isRecipientStudent ? student.name : recipient%></b>
                                <%=isRecipientStudent ? " (" + student.team + ", <a href=\"mailto:" + student.email + "\">" + student.email + "</a>)" : ""%>
                                <button type="button"
                                    class="btn btn-default btn-xs icon-button pull-right"
                                    id="button_add_comment_<%=studentIdx%>"
                                    onclick="showAddCommentBox(<%=studentIdx%>);"
                                    data-toggle="tooltip"
                                    data-placement="top"
                                    title="<%=Const.Tooltips.COMMENT_ADD%>">
                                    <span
                                        class="glyphicon glyphicon-comment glyphicon-primary"></span>
                                </button>
                            </div>
                            <ul class="list-group comments">
                                <%
                                    for (CommentAttributes comment : data.comments.get(recipient)) {//student comments loop starts
                                            commentIdx++;
                                %>
                                <li
                                    class="list-group-item list-group-item-warning">
                                    <form method="post"
                                        action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT%>"
                                        name="form_commentedit"
                                        class="form_comment"
                                        id="form_commentedit-<%=commentIdx%>">
                                        <div id="commentBar-<%=commentIdx%>">
                                            <span class="text-muted">on
                                                <%=TimeHelper.formatTime(comment.createdAt)%></span>
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
                                        </div>
                                        <div
                                            id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                                        <div
                                            id="commentTextEdit<%=commentIdx%>"
                                            style="display: none;">
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
                                                    id="button_save_comment"
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
                                            value="edit"> <input
                                            type="hidden"
                                            name=<%=Const.ParamsNames.COMMENT_ID%>
                                            value="<%=comment.getCommentId()%>">
                                        <input type="hidden"
                                            name=<%=Const.ParamsNames.COURSE_ID%>
                                            value="<%=data.courseId%>">
                                        <input type="hidden"
                                            name=<%=Const.ParamsNames.STUDENT_EMAIL%>
                                            value="<%=recipient%>">
                                        <input type="hidden"
                                            name=<%=Const.ParamsNames.FROM_COMMENTS_PAGE%>
                                            value="true"> <input
                                            type="hidden"
                                            name="<%=Const.ParamsNames.USER_ID%>"
                                            value="<%=data.account.googleId%>">
                                    </form>
                                </li>
                                <%
                                    }//student comments loop ends
                                %>
                                <li
                                    class="list-group-item list-group-item-warning"
                                    id="comment_box_<%=studentIdx%>"
                                    style="display: none;">
                                    <form method="post"
                                        action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD%>"
                                        name="form_commentadd"
                                        class="form_comment">
                                        <div class="form-group">
                                            <textarea
                                                class="form-control"
                                                rows="3"
                                                placeholder="Your comment about this student"
                                                name=<%=Const.ParamsNames.COMMENT_TEXT%>
                                                id="commentText_<%=studentIdx%>"></textarea>
                                        </div>
                                        <div class="col-sm-offset-5">
                                            <input type="submit"
                                                class="btn btn-primary"
                                                id="button_save_comment"
                                                value="Add Comment">
                                            <input type="button"
                                                class="btn btn-default"
                                                value="Cancel"
                                                onclick="hideAddCommentBox(<%=studentIdx%>);">
                                            <input type="hidden"
                                                name=<%=Const.ParamsNames.COURSE_ID%>
                                                value="<%=data.courseId%>">
                                            <input type="hidden"
                                                name=<%=Const.ParamsNames.STUDENT_EMAIL%>
                                                value="<%=recipient%>">
                                            <input type="hidden"
                                                name=<%=Const.ParamsNames.FROM_COMMENTS_PAGE%> 
                                                value="true"> 
                                            <input type="hidden"
                                                name="<%=Const.ParamsNames.USER_ID%>"
                                                value="<%=data.account.googleId%>">
                                        </div>
                                    </form>
                                </li>
                            </ul>
                        </div>
                        <%
                            }//recipient loop ends
                        %>
                    </div>
                </div>
                <% }//comment size check ends %>
                <%
                    for (String fsName : data.feedbackResultBundles.keySet()) {//FeedbackSession loop starts
                        FeedbackSessionResultsBundle bundle = data.feedbackResultBundles.get(fsName);
                    if(bundle.getQuestionResponseMap().entrySet().size() == 0)
                        continue;
                %>
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Feedback Session: <%=fsName%></strong>
                    </div>
                    <div class="panel-body">
                        <%
                                int qnIndx = 0;
                                for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries : bundle
                                        .getQuestionResponseMap().entrySet()) {//FeedbackQuestion loop starts
                                    if (responseEntries.getValue().size() == 0)
                                        continue;
                                    qnIndx++;
                        %>
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <b>Question <%=responseEntries.getKey().questionNumber%></b>:
                                <%=bundle.getQuestionText(responseEntries.getKey().getId())%>
                                <%
                                    Map<String, FeedbackQuestionAttributes> questions = bundle.questions;
                                            FeedbackQuestionAttributes question = questions.get(responseEntries.getKey().getId());
                                            FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, ""));
                                %>
                            </div>
                            <table class="table">
                                <tbody>
                                    <%
                                        int giverIndex = 0;
                                                int recipientIndex = 0;
                                                for (FeedbackResponseAttributes responseEntry : responseEntries.getValue()) {//FeedbackResponse loop starts
                                                    giverIndex++;
                                                    recipientIndex++;
                                                    String giverName = bundle.getGiverNameForResponse(responseEntries.getKey(), responseEntry);
                                                    String giverTeamName = bundle.getTeamNameForEmail(responseEntry.giverEmail);
                                                    giverName = bundle.appendTeamNameToName(giverName, giverTeamName);

                                                    String recipientName = bundle.getRecipientNameForResponse(responseEntries.getKey(), responseEntry);
                                                    String recipientTeamName = bundle.getTeamNameForEmail(responseEntry.recipientEmail);
                                                    recipientName = bundle.appendTeamNameToName(recipientName, recipientTeamName);
                                    %>
                                    <tr>
                                        <td><b>From:</b> <%=giverName%>
                                            <b>To:</b> <%=recipientName%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td><strong>Response:
                                        </strong><%=responseEntry.getResponseDetails().getAnswerHtml()%>
                                        </td>
                                    </tr>
                                    <tr class="active">
                                        <td>Comment:
                                            <button type="button"
                                                class="btn btn-default btn-xs icon-button pull-right"
                                                id="button_add_comment"
                                                onclick="showResponseCommentAddForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>)"
                                                data-toggle="tooltip"
                                                data-placement="top"
                                                title="<%=Const.Tooltips.COMMENT_ADD%>">
                                                <span
                                                    class="glyphicon glyphicon-comment glyphicon-primary"></span>
                                            </button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <%
                                                List<FeedbackResponseCommentAttributes> frcList = bundle.responseComments.get(responseEntry.getId());
                                            %>
                                            <ul
                                                class="list-group comments"
                                                id="responseCommentTable-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>"
                                                style="<%=frcList != null && frcList.size() > 0 ? "" : "display:none"%>">
                                                <%
                                                    int responseCommentIndex = 0;
                                                                for (FeedbackResponseCommentAttributes frc : frcList) {//FeedbackResponseComments loop starts
                                                                    responseCommentIndex++;
                                                %>
                                                <li
                                                    class="list-group-item list-group-item-warning"
                                                    id="responseCommentRow-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                    <div
                                                        id="commentBar-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                        <span
                                                            <%String frCommentGiver = frc.giverEmail;
                            if (frc.giverEmail.equals(data.instructorEmail)) {
                                frCommentGiver = "you";
                            } else if (data.roster.getInstructorForEmail(frc.giverEmail) != null) {
                                frCommentGiver = data.roster.getInstructorForEmail(frc.giverEmail).name;
                            }%>
                                                            class="text-muted">From:
                                                            <b><%=frCommentGiver%></b>
                                                            [<%=frc.createdAt%>]
                                                        </span>
                                                        <%
                                                            if (frc.giverEmail.equals(data.instructorEmail)) {//FeedbackResponseComment edit/delete control starts
                                                        %>
                                                        <form
                                                            class="responseCommentDeleteForm pull-right">
                                                            <a
                                                                href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE%>"
                                                                type="button"
                                                                id="commentdelete-<%=responseCommentIndex%>"
                                                                class="btn btn-default btn-xs icon-button"
                                                                data-toggle="tooltip"
                                                                data-placement="top"
                                                                title="<%=Const.Tooltips.COMMENT_DELETE%>"
                                                                style="display: none;">
                                                                <span
                                                                class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                                            </a> <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID%>"
                                                                value="<%=frc.getId()%>">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.COURSE_ID%>"
                                                                value="<%=responseEntry.courseId%>">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                                                value="<%=responseEntry.feedbackSessionName%>">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.USER_ID%>"
                                                                value="<%=data.account.googleId%>">
                                                        </form>
                                                        <a type="button"
                                                            id="commentedit-<%=responseCommentIndex%>"
                                                            class="btn btn-default btn-xs icon-button pull-right"
                                                            onclick="showResponseCommentEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>,<%=responseCommentIndex%>)"
                                                            data-toggle="tooltip"
                                                            data-placement="top"
                                                            title="<%=Const.Tooltips.COMMENT_EDIT%>"
                                                            style="display: none;">
                                                            <span
                                                            class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                                        </a>
                                                        <%
                                                            }//FeedbackResponseComment edit/delete control ends
                                                        %>
                                                    </div> <!-- frComment Content -->
                                                    <div
                                                        id="plainCommentText-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"><%=InstructorCommentsPageData.sanitizeForHtml(frc.commentText.getValue())%></div>
                                                    <!-- frComment Edit Form -->
                                                    <form
                                                        style="display: none;"
                                                        id="responseCommentEditForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"
                                                        class="responseCommentEditForm">
                                                        <div
                                                            class="form-group">
                                                            <textarea
                                                                class="form-control"
                                                                rows="3"
                                                                placeholder="Your comment about this response"
                                                                name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>"
                                                                id="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"><%=frc.commentText.getValue()%></textarea>
                                                        </div>
                                                        <div
                                                            class="col-sm-offset-5">
                                                            <a
                                                                href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT%>"
                                                                class="btn btn-primary"
                                                                id="button_save_comment_for_edit-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                                Save </a> <input
                                                                type="button"
                                                                class="btn btn-default"
                                                                value="Cancel"
                                                                onclick="return hideResponseCommentEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>,<%=responseCommentIndex%>);">
                                                        </div>
                                                        <input
                                                            type="hidden"
                                                            name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID%>"
                                                            value="<%=frc.getId()%>">
                                                        <input
                                                            type="hidden"
                                                            name="<%=Const.ParamsNames.COURSE_ID%>"
                                                            value="<%=responseEntry.courseId%>">
                                                        <input
                                                            type="hidden"
                                                            name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                                            value="<%=responseEntry.feedbackSessionName%>">
                                                        <input
                                                            type="hidden"
                                                            name="<%=Const.ParamsNames.USER_ID%>"
                                                            value="<%=data.account.googleId%>">
                                                    </form>
                                                </li>
                                                <%
                                                    }//FeedbackResponseComments loop ends
                                                %>
                                                <!-- frComment Add form -->
                                                <li
                                                    class="list-group-item list-group-item-warning"
                                                    id="showResponseCommentAddForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>"
                                                    style="display: none;">
                                                    <form
                                                        class="responseCommentAddForm">
                                                        <div
                                                            class="form-group">
                                                            <textarea
                                                                class="form-control"
                                                                rows="3"
                                                                placeholder="Your comment about this response"
                                                                name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>"
                                                                id="responseCommentAddForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>"></textarea>
                                                        </div>
                                                        <div
                                                            class="col-sm-offset-5">
                                                            <a
                                                                href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD%>"
                                                                class="btn btn-primary"
                                                                id="button_save_comment_for_add-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">Add</a>
                                                            <input
                                                                type="button"
                                                                class="btn btn-default"
                                                                value="Cancel"
                                                                onclick="hideResponseCommentAddForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>)">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.COURSE_ID%>"
                                                                value="<%=responseEntry.courseId%>">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                                                                value="<%=responseEntry.feedbackSessionName%>">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>"
                                                                value="<%=responseEntry.feedbackQuestionId%>">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID%>"
                                                                value="<%=responseEntry.getId()%>">
                                                            <input
                                                                type="hidden"
                                                                name="<%=Const.ParamsNames.USER_ID%>"
                                                                value="<%=data.account.googleId%>">
                                                        </div>
                                                    </form>
                                                </li>
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