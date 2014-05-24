<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.SessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentResultBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalSubmissionPageData"%>
<%@ page import="teammates.ui.controller.InstructorStudentRecordsPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%
    InstructorStudentRecordsPageData data = (InstructorStudentRecordsPageData)request.getAttribute("data");
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
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" media="screen">
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/AnchorPosition.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorStudentRecords.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
    <!-- Bootstrap core JavaScript ================================================== -->
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script type="text/javascript">
        var showCommentBox = "<%=data.showCommentBox%>";
    </script>
</head>

<body onload="readyStudentRecordsPage();">
    <div id="dhtmltooltip"></div>
    <div id="frameTop">
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
    </div>

    <div id="frameBody" class="container">
        <div id="frameBodyWrapper">
            <div id="topOfPage"></div>
            <h1><%=data.courseId %> - <%=data.student.name %>'s Records</h1>
            <br />
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <div class="panel panel-info">
              <div class="panel-heading">Student Records' Comments</div>
              <div class="panel-body">
                Your comments on this student:
                <button type="button" title="Add comment" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" onclick="showAddCommentBox();"
                data-toggle="tooltip" data-placement="top" title="Add comment">
                  <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                </button>
                <ul class="list-group comment-list">
                <%
                    int commentIdx = -1;
                    for(CommentAttributes comment : data.comments){
                        commentIdx++;
                %>
                  <li class="list-group-item list-group-item-warning">
                  <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT%>" name="form_commentedit" class="form_comment" id="form_commentedit-<%=commentIdx %>">
                    <div id="commentBar<%=commentIdx %>">
                        <span class="text-muted"><%=TimeHelper.formatTime(comment.createdAt)%></span>
                        <a type="button" id="commentdelete-<%=commentIdx %>" title="Delete comment" class="btn btn-default btn-xs icon-button pull-right" onclick="return deleteComment('<%=commentIdx %>');"
                        data-toggle="tooltip" data-placement="top" title="Delete comment"> 
                            <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                        </a>
                        <a type="button" id="commentedit-<%=commentIdx %>" title="Edit comment" class="btn btn-default btn-xs icon-button pull-right" onclick="return enableEdit('<%=commentIdx %>', '<%=data.comments.size() %>');"
                        data-toggle="tooltip" data-placement="top" title="Edit comment">
                            <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                        </a>
                    </div>
                    <div id="plainCommentText<%=commentIdx %>"><%=comment.commentText.getValue() %></div>
                    <div id="commentTextEdit<%=commentIdx %>" style="display:none;">
                        <div class="form-group">
                            <textarea class="form-control" rows="3" placeholder="Your comment about this student" name=<%=Const.ParamsNames.COMMENT_TEXT%> id="commentText<%=commentIdx %>"><%=comment.commentText.getValue() %></textarea>
                        </div>
                        <div class="col-sm-offset-5">
                            <input id="commentsave-<%=commentIdx %>" title="Save comment" onclick="return submitCommentForm('<%=commentIdx %>');" type="submit" class="btn btn-primary" id="button_save_comment" value="Save">
                            <input type="button" class="btn btn-default" value="Cancel" onclick="return disableComment('<%=commentIdx %>');">
                        </div>
                    </div>
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENT_EDITTYPE%> id="<%=Const.ParamsNames.COMMENT_EDITTYPE%>-<%=commentIdx %>" value="edit">
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENT_ID%> value="<%=comment.getCommentId()%>">
                    <input type="hidden" name=<%=Const.ParamsNames.COURSE_ID%> value="<%=data.courseId%>">
                    <input type="hidden" name=<%=Const.ParamsNames.STUDENT_EMAIL%> value="<%=data.student.email %>">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                  </form>
                  </li>
                  <%
                      }
                  %>
                  <li class="list-group-item list-group-item-warning" id="comment_box" style="display:none;">
                      <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD%>" name="form_commentadd" class="form_comment">
                        <div class="form-group">
                          <textarea class="form-control" rows="3" placeholder="Your comment about this student" name=<%=Const.ParamsNames.COMMENT_TEXT%> id="commentText"></textarea>
                        </div>
                        <div class="col-sm-offset-5">
                          <input type="submit" class="btn btn-primary" id="button_save_comment" value="Add Comment">
                          <input type="button" class="btn btn-default" value="Cancel" onclick="hideAddCommentBox();">
                          <input type="hidden" name=<%=Const.ParamsNames.COURSE_ID%> value="<%=data.courseId%>">
                          <input type="hidden" name=<%=Const.ParamsNames.STUDENT_EMAIL%> value="<%=data.student.email %>">
                          <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                        </div>
                      </form>
                  </li>
                </ul>
              </div>
            </div>
            <br>
            <br>
            <%
                int evalIndex = -1;
                int fbIndex = -1;
                int sessionIndex = -1;
                for(SessionResultsBundle sessionResult: data.results){
                    sessionIndex++;
                    if(sessionResult instanceof StudentResultBundle){
                        evalIndex++;
                        StudentResultBundle studentResult = (StudentResultBundle) sessionResult;
                        EvaluationAttributes eval = (EvaluationAttributes) data.sessions.get(sessionIndex);
                    
            %>
                    <div class="student_eval" id="studentEval-<%=evalIndex%>">
                    <table class="inputTable" id="studentEvaluationInfo">
                        <tr>
                            <td class="label rightalign bold" width="250px">Evaluation Name:</td>
                            <td class="leftalign" id="eval_name-<%=evalIndex%>"width="250px"><%=InstructorStudentRecordsPageData.sanitizeForHtml(eval.name)%></td>
                        </tr>
                    </table>
                <%
                    for(boolean byReviewee = true, repeat=true; repeat; repeat = byReviewee, byReviewee=false){
                %>
                        <h2 class="centeralign"><%=InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name) + (byReviewee ? "'s Result" : "'s Submission")%></h2>
                        <table class="resultTable">
                            <thead><tr>
                                <th colspan="2" width="10%" class="bold leftalign">
                                    <span class="resultHeader"><%=byReviewee ? "Reviewee" : "Reviewer"%>: </span><%=data.student.name%></th>
                                <th class="bold leftalign"><span class="resultHeader"
                                    onmouseover="ddrivetip('<%=Const.Tooltips.CLAIMED%>')"
                                    onmouseout="hideddrivetip()">
                                    Claimed Contribution: </span><%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.claimedToInstructor,true)%></th>
                                <th class="bold leftalign"><span class="resultHeader"
                                    onmouseover="ddrivetip('<%=Const.Tooltips.PERCEIVED%>')"
                                    onmouseout="hideddrivetip()">
                                    Perceived Contribution: </span><%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.perceivedToInstructor,true)%></th>
                                </tr></thead>
                                <tr>
                                <td colspan="4"><span class="bold">Self evaluation:</span><br>
                                        <%=InstructorEvalSubmissionPageData.getJustificationAsSanitizedHtml(studentResult.getSelfEvaluation())%></td>
                                </tr>
                                <tr>
                                    <td colspan="4"><span class="bold">Comments about team:</span><br>
                                        <%=InstructorEvalSubmissionPageData.sanitizeForHtml(studentResult.getSelfEvaluation().p2pFeedback.getValue())%></td>
                                </tr>
                                <tr class="resultSubheader">
                                    <td width="15%" class="bold"><%=byReviewee ? "From" : "To"%> Student</td>
                                    <td width="5%" class="bold">Contribution</td>
                                    <td width="40%" class="bold">Confidential comments</td>
                                    <td width="40%" class="bold">Feedback to peer</td>
                                </tr>
                    <%
                        for(SubmissionAttributes sub: (byReviewee ? studentResult.incoming : studentResult.outgoing)){
                            if(sub.reviewer.equals(sub.reviewee)) continue;
                    %>
                            <tr>
                                <td><b><%=InstructorEvalSubmissionPageData.sanitizeForHtml(byReviewee ? sub.details.reviewerName : sub.details.revieweeName)%></b></td>
                                <td><%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(sub.details.normalizedToInstructor,false)%></td>
                                <td><%=InstructorEvalSubmissionPageData.getJustificationAsSanitizedHtml(sub)%></td>
                                <td><%=InstructorEvalSubmissionPageData.getP2pFeedbackAsHtml(InstructorEvalSubmissionPageData.sanitizeForHtml(sub.p2pFeedback.getValue()), eval.p2pEnabled)%></td>
                            </tr>
                    <%
                        }
                    %>
                        </table>
                        <br><br>
                <%
                    }
                %>
                    <div class="centeralign">
                        <input type="button" class="button" id="button_edit-<%=evalIndex %>" value="Edit Submission"
                            onclick="window.location.href='<%=data.getInstructorEvaluationSubmissionEditLink(eval.courseId, eval.name, data.student.email)%>'">
                    </div>
                    </div>
                    <br>
                    <hr>
                    <br>
            <%
                } else if(sessionResult instanceof FeedbackSessionResultsBundle){
                    FeedbackSessionResultsBundle feedback = (FeedbackSessionResultsBundle) sessionResult;
                    
                    fbIndex++;

                    String giverName = feedback.appendTeamNameToName(data.student.name, data.student.team);
                    String recipientName = giverName;
                    Map<String, List<FeedbackResponseAttributes>> received = feedback 
                            .getResponsesSortedByRecipient().get(recipientName);
                    Map<String, List<FeedbackResponseAttributes>> given = feedback
                            .getResponsesSortedByGiver().get(giverName);
            %>
                    <div class="student_feedback" id="studentFeedback-<%=fbIndex%>">
                    <table class="inputTable" id="studentEvaluationInfo">
                        <tr>
                            <td class="label rightalign bold" width="250px">Feedback Session Name:</td>
                            <td class="leftalign" id="feedback_name-<%=fbIndex%>" width="250px"><%=InstructorStudentRecordsPageData.sanitizeForHtml(feedback.feedbackSession.feedbackSessionName)%></td>
                        </tr>
                    </table>
                    <br><br>
            <%
                    if(received != null){
            %>
                        <div class="backgroundBlock">
                            <h2 class="color_white">To: <%=recipientName%></h2>
                    <%
                        int giverIndex = 0;
                        for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesReceived : received.entrySet()) {
                            giverIndex++;
                    %>
                            <table class="resultTable" style="width: 100%">
                                <thead>
                                    <tr>
                                        <th class="leftalign"><span class="bold">From: </span><%=responsesReceived.getKey()%></th>
                                    </tr>
                                </thead>
                        <%
                            int qnIndx = 1;
                            for (FeedbackResponseAttributes singleResponse : responsesReceived.getValue()) {
                        %>
                                <tr class="resultSubheader">
                                    <td class="multiline"><span class="bold">Question <%=feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber%>: </span><%=
                                        feedback.getQuestionText(singleResponse.feedbackQuestionId)%><%
                                        Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                        FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                        FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                                        out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-"+giverIndex+"-session-"+fbIndex));
                                    %></td>
                                </tr>
                                <tr>
                                    <td class="multiline"><span class="bold">Response: </span><%=singleResponse.getResponseDetails().getAnswerHtml()%></td>
                                </tr>
                            <%
                                List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                                if (responseComments != null) {
                            %>
                                <tr>
                                    <td>
                                        <span class="bold">Comments: </span>
                                        <table class="responseCommentTable">
                                            <%
                                                for (FeedbackResponseCommentAttributes comment : responseComments) {
                                            %>
                                                    <tr>
                                                        <td class="feedbackResponseCommentText"><%=comment.commentText.getValue() %></td>
                                                        <td class="feedbackResponseCommentGiver"><%=comment.giverEmail %></td>
                                                        <td class="feedbackResponseCommentTime"><%=comment.createdAt %></td>
                                                    </tr>
                                            <%
                                                }
                                            %>
                                        </table> 
                                    </td>
                                </tr>
                            <%
                                }
                                qnIndx++;
                            }
                            if (responsesReceived.getValue().isEmpty()) {
                            %>
                                <tr>
                                    <td class="bold color_red">No feedback from this user.</td>
                                </tr>
                        <%
                            }
                        %>
                            </table>
                    <%
                        }
                    %>
                    </div>
                    <br>
                    <br>
                <%
                    } else{
                %>
                    <div class="backgroundBlock">
                            <h4 class="centeralign color_white">No feedback for <%=data.student.name%> found</h4>
                    </div>
                    <br>
                    <br>
                <%
                    }
                    if(given != null){
                %>
                        <div class="backgroundBlock">
                            <h2 class="color_white">From: <%=giverName%></h2>
                    <%
                        int recipientIndex = 0;
                        for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesGiven : given.entrySet()) {
                        recipientIndex++;
                    %>
                            <table class="resultTable" style="width: 100%">
                                <thead>
                                    <tr>
                                        <th class="leftalign"><span class="bold">To: </span><%=responsesGiven.getKey()%></th>
                                    </tr>
                                </thead>
                        <%
                            int qnIndx = 1;
                            for (FeedbackResponseAttributes singleResponse : responsesGiven.getValue()) {
                        %>
                                <tr class="resultSubheader">
                                    <td class="multiline"><span class="bold">Question <%=feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber%>: </span><%=
                                        feedback.getQuestionText(singleResponse.feedbackQuestionId)%><%
                                        Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                        FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                        FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                                        out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "recipient-"+recipientIndex+"-session-"+fbIndex));
                                    %></td>
                                </tr>
                                <tr>
                                    <td class="multiline"><span class="bold">Response: </span><%=singleResponse.getResponseDetails().getAnswerHtml()%></td>
                                </tr>
                            <%
                                List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                                if (responseComments != null) {
                            %>
                                <tr>
                                    <td>
                                        <span class="bold">Comments: </span>
                                        <table class="responseCommentTable">
                                            <%
                                                for (FeedbackResponseCommentAttributes comment : responseComments) {
                                            %>
                                                    <tr>
                                                        <td class="feedbackResponseCommentText"><%=comment.commentText.getValue() %></td>
                                                        <td class="feedbackResponseCommentGiver"><%=comment.giverEmail %></td>
                                                        <td class="feedbackResponseCommentTime"><%=comment.createdAt %></td>
                                                    </tr>
                                            <%
                                                }
                                            %>
                                        </table> 
                                    </td>
                                </tr>
                            <%
                                }
                                qnIndx++;
                            }
                            if (responsesGiven.getValue().isEmpty()) {
                            %>
                                <tr>
                                    <td class="bold color_red">No feedback from this user.</td>
                                </tr>
                        <%
                            }
                        %>
                            </table>
                            <br>
                        
                    <%
                        }
                    %>
                    </div>
                <%
                    } else{
                %>
                        <div class="backgroundBlock">
                            <h4 class="centeralign color_white">No feedback by <%=data.student.name%> found</h4>
                        </div>
                <%
                    }
                %>    
                    </div>
                    <br>
                    <hr>
                    <br>
            <%
                }
                }
            %>
            
            <br>
            <br>
            <br>

        </div>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>