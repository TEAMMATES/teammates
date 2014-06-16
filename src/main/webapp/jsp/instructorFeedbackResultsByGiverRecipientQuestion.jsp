<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResponseStatus" %>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%
    InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TEAMMATES - Feedback Session Results</title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" media="screen">
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResults.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
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
            <div id="headerOperation">
                <h1>Feedback Results - Instructor</h1>
            </div>
            <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
            <br>
            <%
                boolean groupByTeamEnabled = data.groupByTeam==null ? false : true;
                String currentTeam = null;
                boolean newTeam = false;
            %>
            <%
                Map<String, Map<String, List<FeedbackResponseAttributes>>> allResponses = data.bundle.getResponsesSortedByGiver(groupByTeamEnabled);
                Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;

                int giverIndex = 0;
                for (Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>> responsesFromGiver : allResponses.entrySet()) {
                    giverIndex++;

            
                Map<String, List<FeedbackResponseAttributes> > giverData = responsesFromGiver.getValue();
                Object[] giverDataArray =  giverData.keySet().toArray();
                FeedbackResponseAttributes firstResponse = giverData.get(giverDataArray[0]).get(0);
                String targetEmail = firstResponse.giverEmail.replace(Const.TEAM_OF_EMAIL_OWNER,"");
                String targetEmailDisplay = firstResponse.giverEmail;
                String mailtoStyleAttr = (targetEmailDisplay.contains("@@"))?"style=\"display:none;\"":"";

            %>
            <%
                if(currentTeam != null && !(data.bundle.getTeamNameForEmail(targetEmail)=="" ? currentTeam.equals(data.bundle.getNameForEmail(targetEmail)): currentTeam.equals(data.bundle.getTeamNameForEmail(targetEmail)))) {
                    currentTeam = data.bundle.getTeamNameForEmail(targetEmail);
                    if(currentTeam.equals("")){
                        currentTeam = data.bundle.getNameForEmail(targetEmail);
                    }
                    newTeam = true;
            %>
                    </div>
                    </div>
                </div>
            <%
                }
                if(groupByTeamEnabled == true && (currentTeam==null || newTeam==true)) {
                    currentTeam = data.bundle.getTeamNameForEmail(targetEmail);
                    if(currentTeam.equals("")){
                        currentTeam = data.bundle.getNameForEmail(targetEmail);
                    }
                    newTeam = false;
            %>
                    <div class="panel panel-warning">
                        <div class="panel-heading">
                            <strong><%=currentTeam%></strong>
                        </div>
                        <div class="panel-collapse">
                        <div class="panel-body background-color-warning">
            <%
                }
            %>

            <div class="panel panel-primary">
                <div class="panel-heading">
                    From: <strong><%=responsesFromGiver.getKey()%></strong>
                        <a class="link-in-dark-bg" href="mailTo:<%= targetEmail%> " <%=mailtoStyleAttr%>>[<%=targetEmailDisplay%>]</a>
                </div>
                <div class="panel-collapse">
                <div class="panel-body">
                <%
                    int recipientIndex = 0;
                    for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesFromGiverToRecipient : responsesFromGiver.getValue().entrySet()) {
                        recipientIndex++;
                %>
                    <div class="row <%=recipientIndex == 1? "": "border-top-gray"%>">
                            <div class="col-md-2"><strong>To: <%=responsesFromGiverToRecipient.getKey()%></strong></div>
                            <div class="col-md-10">
                    <%
                        int qnIndx = 1;
                        for (FeedbackResponseAttributes singleResponse : responsesFromGiverToRecipient.getValue()) {
                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                            FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                    %>
                    <div class="panel panel-info">
                                        <div class="panel-heading">Question <%=question.questionNumber%>: <%
                                                out.print(InstructorFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText));
                                                out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-"+giverIndex+"-recipient-"+recipientIndex));
                                        %></div>
                                        <div class="panel-collapse">
                                        <div class="panel-body">
                                            <div style="clear:both; overflow: hidden">
                                                <div class="pull-left"><%=singleResponse.getResponseDetails().getAnswerHtml()%></div>
                                                <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" 
                                                    onclick="showResponseCommentAddForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>)"
                                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_ADD%>">
                                                    <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                                                </button>
                                            </div>
                                            <% List<FeedbackResponseCommentAttributes> responseComments = data.bundle.responseComments.get(singleResponse.getId()); %>
                                            <ul class="list-group" id="responseCommentTable-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>"
                                             style="<%=responseComments != null && responseComments.size() > 0? "margin-top:15px;": "display:none"%>">
                                            <%
                                                if (responseComments != null && responseComments.size() > 0) {
                                                    int responseCommentIndex = 1;
                                                    for (FeedbackResponseCommentAttributes comment : responseComments) {
                                            %>
                                        <li class="list-group-item list-group-item-warning" id="responseCommentRow-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                            <div id="commentBar-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                            <span class="text-muted">From: <%=comment.giverEmail%> [<%=comment.createdAt%>]</span>
                                            <% 
                                                if (comment.giverEmail.equals(data.instructor.email)) {
                                            %>
                                            <!-- frComment delete Form -->
                                            <form class="responseCommentDeleteForm pull-right">
                                                <a href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE%>" type="button" id="commentdelete-<%=responseCommentIndex %>" class="btn btn-default btn-xs icon-button" 
                                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_DELETE%>"> 
                                                    <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                                </a>
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%=comment.getId()%>">
                                                <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                                            </form>
                                            <a type="button" id="commentedit-<%=responseCommentIndex %>" class="btn btn-default btn-xs icon-button pull-right" 
                                                onclick="showResponseCommentEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>,<%=responseCommentIndex%>)"
                                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_EDIT%>">
                                                <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                            </a>
                                            </div>
                                            <%  } %>
                                            <!-- frComment Content -->
                                            <div id="plainCommentText-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"><%=InstructorFeedbackResultsPageData.sanitizeForHtml(comment.commentText.getValue()) %></div>
                                            <!-- frComment Edit Form -->
                                            <form style="display:none;" id="responseCommentEditForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>" class="responseCommentEditForm">
                                                <div class="form-group">
                                                    <textarea class="form-control" rows="3" placeholder="Your comment about this response" 
                                                    name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>"
                                                    id="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"><%=comment.commentText.getValue() %></textarea>
                                                </div>
                                                <div class="col-sm-offset-5">
                                                    <a href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT%>" type="button" class="btn btn-primary" id="button_save_comment_for_edit-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                        Save 
                                                    </a>
                                                    <input type="button" class="btn btn-default" value="Cancel" onclick="return hideResponseCommentEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>,<%=responseCommentIndex%>);">
                                                </div>
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%=comment.getId()%>">
                                                <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                                            </form>
                                        </li>
                                            <%
                                                        responseCommentIndex++;
                                                    }
                                                }
                                            %>
                                        <!-- frComment Add form -->    
                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>" style="display:none;">
                                            <form class="responseCommentAddForm">
                                                <div class="form-group">
                                                    <textarea class="form-control" rows="3" placeholder="Your comment about this response" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>" id="responseCommentAddForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>"></textarea>
                                                </div>
                                                <div class="col-sm-offset-5">
                                                    <a href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD%>" type="button" class="btn btn-primary" id="button_save_comment_for_add-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">Add</a>
                                                    <input type="button" class="btn btn-default" value="Cancel" onclick="hideResponseCommentAddForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>)">
                                                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
                                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
                                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID %>" value="<%=singleResponse.feedbackQuestionId %>">                                            
                                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="<%=singleResponse.getId() %>">
                                                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                                                </div>
                                            </form>
                                        </li>
                                    </ul></div></div></div>
                    <%
                            qnIndx++;
                        }
                        if (responsesFromGiverToRecipient.getValue().isEmpty()) {
                    %>
                    <div class="col-sm-12" style="color:red;">No feedback from this user.</div>
                    <%
                        }
                    %>
                </div></div>
                <%
                    }
                %>
            </div>
            </div>
            </div>
            <%
                }
            %>

            <%
                //close the last team panel.
                if(groupByTeamEnabled==true) {
            %>
                        </div>
                        </div>
                    </div>
            <%
                }
            %>
            
            <%
                // Only output the list of students who haven't responded when there are responses.
                FeedbackSessionResponseStatus responseStatus = data.bundle.responseStatus;
                if (!responseStatus.hasResponse.isEmpty()) {
            %>
            <div class="panel panel-info">
                    <div class="panel-heading">Students Who Did Not Respond to Any Question</div>
                    
                    <table class="table table-striped">
                        <tbody>
                        <%
                            for (String studentName : responseStatus.getStudentsWhoDidNotRespondToAnyQuestion()) {
                        %>
                                <tr>
                                    <td><%=studentName%></td>
                                </tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>
                <br> <br>
            <%
                }
            %>

        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>