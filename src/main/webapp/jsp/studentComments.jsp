<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@page import="teammates.common.datatransfer.CommentAttributes"%>
<%@page import="teammates.common.datatransfer.CommentRecipientType"%>
<%@page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.TimeHelper" %>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentCommentsPageData"%>
<%
	StudentCommentsPageData data = (StudentCommentsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES - Student</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
    <script type="text/javascript" src="/js/studentComments.js"></script>
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
      <![endif]-->
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />

    <div id="frameBodyWrapper" class="container theme-showcase">
        <div id="topOfPage"></div>
        <h2>Comments</h2>

        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>
            <%
            	if(data.coursePaginationList.size() > 0) {
            %>
            <ul class="pagination">
                <li><a href="<%=data.previousPageLink%>">«</a></li>
                <%
                	for (String courseId : data.coursePaginationList) {
                %>
                <li
                    class="<%=courseId.equals(data.courseId) ? "active" : ""%>">
                    <a
                    href="<%=data.getStudentCommentsLink(false) + "&courseid=" + courseId%>"><%=courseId%></a>
                </li>
                <%
                	}
                %>
                <li><a href="<%=data.nextPageLink%>">»</a></li>
            </ul>
            <div class="well well-plain">
                <div class="text-color-primary">
                    <h4>
                        <strong> <%=data.courseName%>
                        </strong>
                    </h4>
                </div>
                <div id="no-comment-panel" style="<%=data.comments.size() == 0 && data.feedbackResultBundles.keySet().size() == 0?"":"display:none;"%>">
                    <br>
                    <div class="panel">
                        <div class="panel-body">
                            You don't have any comment in this course.
                        </div>
                    </div>
                </div>
                <%
                	if(data.comments.size() != 0){// check student comments starts
                %>
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Comments for students</strong>
                    </div>
                    <div class="panel-body">
                        <%
                        	int commentIdx = 0;
                                                    int studentIdx = 0;
                                                    for (CommentAttributes comment : data.comments) {//comment loop starts
                                                        studentIdx++;
                        %>
                        <%
                        	String recipientDisplay = data.getRecipientNames(comment.recipients);
                        %>
                        <div class="panel panel-info student-record-comments <%=recipientDisplay.equals("you")?"giver_display-to-you":"giver_display-to-others"%>">
                            <div class="panel-heading">
                                To <b><%=recipientDisplay%></b>
                            </div>
                            <ul class="list-group comments">
                                <%
                                	CommentRecipientType recipientTypeForThisRecipient = CommentRecipientType.PERSON;//default value is PERSON
                                                                    commentIdx++;
                                                                    recipientTypeForThisRecipient = comment.recipientType;
                                %>
                                <li class="list-group-item list-group-item-warning"
                                    name="form_commentedit"
                                    class="form_comment"
                                    id="form_commentedit-<%=commentIdx%>">
                                    <div id="commentBar-<%=commentIdx%>">
                                        <%
                                        	InstructorAttributes instructor = data.roster.getInstructorForEmail(comment.giverEmail);
                                                                                   String giverDisplay = comment.giverEmail;
                                                                                   if(instructor != null){
                                                                                       giverDisplay = instructor.displayedName + " " + instructor.name;
                                                                                   }
                                        %>
                                        <span class="text-muted">From <b><%=giverDisplay%></b> on
                                            <%=TimeHelper.formatDate(comment.createdAt)%></span>
                                    </div>
                                    <div id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                                </li>
                            </ul>
                        </div>
                        <%
                        	}//comment loop ends
                        %>
                    </div>
                </div>
                <%
                	}// check student comments ends
                %>
                <%
                	int fsIndx = 0;
                                    for (String fsName : data.feedbackResultBundles.keySet()) {//FeedbackSession loop starts
                                        FeedbackSessionResultsBundle bundle = data.feedbackResultBundles.get(fsName);
                                        fsIndx++;
                %>
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Comments in session: <%=fsName%></strong>
                    </div>
                    <div class="panel-body">
                        <%
                        	int qnIndx = 0;
                                                        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries : bundle
                                                                .getQuestionResponseMap().entrySet()) {//FeedbackQuestion loop starts
                                                            qnIndx++;
                        %>
                        <div class="panel panel-info">
                            <div class="panel-heading">
                                <b>Question <%=responseEntries.getKey().questionNumber%></b>:
                                <%=bundle.getQuestionText(responseEntries.getKey().getId())%>
                                <%
                                	Map<String, FeedbackQuestionAttributes> questions = bundle.questions;
                                                                            FeedbackQuestionAttributes question = questions.get(responseEntries.getKey().getId());
                                                                            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, ""));
                                %>
                            </div>
                            <table class="table">
                                <tbody>
                                    <%
                                                int responseIndex = 0;
                                                for (FeedbackResponseAttributes responseEntry : responseEntries.getValue()) {//FeedbackResponse loop starts
                                                    responseIndex++;
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
                                        </strong><%=responseEntry.getResponseDetails().getAnswerHtml(questionDetails)%>
                                        </td>
                                    </tr>
                                    <tr class="active">
                                        <td>Comment(s):
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <%
                                                List<FeedbackResponseCommentAttributes> frcList = bundle.responseComments.get(responseEntry.getId());
                                            %>
                                            <ul
                                                class="list-group comments"
                                                id="responseCommentTable-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>"
                                                style="<%=frcList != null && frcList.size() > 0 ? "" : "display:none"%>">
                                                <%
                                                    int responseCommentIndex = 0;
                                                                for (FeedbackResponseCommentAttributes frc : frcList) {//FeedbackResponseComments loop starts
                                                                    responseCommentIndex++;
                                                                    String frCommentGiver = frc.giverEmail;
                                                                    InstructorAttributes instructor = data.roster.getInstructorForEmail(frc.giverEmail);
                                                                    if (instructor != null) {
                                                                        frCommentGiver = instructor.displayedName + " " + instructor.name;
                                                                    }
                                                %>
                                                <li
                                                    class="list-group-item list-group-item-warning"
                                                    id="responseCommentRow-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>">
                                                    <div
                                                        id="commentBar-<%=fsIndx%>-<%=qnIndx%>-<%=responseIndex%>-<%=responseCommentIndex%>">
                                                        <span class="text-muted">From:
                                                            <b><%=frCommentGiver%></b>
                                                            on <%=TimeHelper.formatDate(frc.createdAt)%>
                                                        </span>
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
                <%
                    }//FeedbackSession loop ends
                %>
            </div>
            <ul class="pagination">
                <li><a href="<%=data.previousPageLink%>">«</a></li>
                <%
                    for (String courseId : data.coursePaginationList) {
                %>
                <li
                    class="<%=courseId.equals(data.courseId) ? "active" : ""%>">
                    <a
                    href="<%=data.getStudentCommentsLink(false) + "&courseid=" + courseId%>"><%=courseId%></a>
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
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>