<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ListIterator"%>
<%@ page import="teammates.common.util.Assumption"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.Url"%>
<%@ page import="teammates.common.datatransfer.AccountAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackTextQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMcqQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractResponseDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackTextResponseDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackMcqResponseDetails"%>
<%@ page import="teammates.ui.controller.StudentFeedbackResultsPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%
    StudentFeedbackResultsPageData data = (StudentFeedbackResultsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TEAMMATES - Submit Feedback</title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" media="screen">
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
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
    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />

    <div id="frameBody">
        <div id="frameBodyWrapper" class="container">
            <div id="topOfPage"></div>
            <h1>Feedback Results - Student</h1>
            <br />
            <% if (data.account.googleId == null) { 
                String joinUrl = new Url(Const.ActionURIs.STUDENT_COURSE_JOIN_NEW)
                            .withRegistrationKey(request.getParameter(Const.ParamsNames.REGKEY))
                            .withStudentEmail(request.getParameter(Const.ParamsNames.STUDENT_EMAIL))
                            .withCourseId(request.getParameter(Const.ParamsNames.COURSE_ID))
                            .toString();
            %>
                <div id="registerMessage" class="alert alert-info">
                    <%=String.format(Const.StatusMessages.UNREGISTERED_STUDENT, joinUrl)%>
                </div>
            <% } %>
            <div class="well well-plain">
                <div class="panel-body">
                    <div class="form-horizontal">
                        <div class="panel-heading">
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Course:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=sanitizeForHtml(data.bundle.feedbackSession.courseId)%></p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Session:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=sanitizeForHtml(data.bundle.feedbackSession.feedbackSessionName)%></p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Opening time:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=StudentFeedbackResultsPageData.displayDateTime(data.bundle.feedbackSession.startTime)%></p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">Closing time:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static"><%=StudentFeedbackResultsPageData.displayDateTime(data.bundle.feedbackSession.endTime)%></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <br />
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <br />
            <%
                int qnIndx = 0;
                Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = data.bundle
                        .getQuestionResponseMap();

                for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionWithResponses : questionsWithResponses
                        .entrySet()) {
                    qnIndx++;
                    
                    FeedbackQuestionAttributes question = questionWithResponses.getKey();
                    FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
            %>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4>Question <%=qnIndx%>: <span class="text-preserve-space"><%=StudentFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText)%>
                        <%=questionDetails.getQuestionAdditionalInfoHtml(qnIndx, "")%></span></h4>
                        <%=
                            questionDetails.getQuestionResultStatisticsHtml(questionWithResponses
                                    .getValue(), question, data.account, data.bundle, "student")
                        %>
                    <%
                    	ListIterator<FeedbackResponseAttributes> itr = questionWithResponses
                    				.getValue().listIterator();
                    		String previousRecipientEmail = null;
                    		while (itr.hasNext()) {
                    			FeedbackResponseAttributes singleResponse = itr.next();

                    			String giverName = data.bundle.getGiverNameForResponse(
                    					questionWithResponses.getKey(), singleResponse);

                    			if (questionWithResponses.getKey().giverType == FeedbackParticipantType.TEAMS) {
                    				if (data.student.team.equals(giverName)) {
                    					giverName = "Your Team (" + giverName + ")";
                    				}
                    			} else if (data.student.email
                    					.equals(singleResponse.giverEmail)) {
                    				giverName = "You";
                    			}

                    			// New table if previous recipient != current or is first response                    
                    			if (previousRecipientEmail == null
                    					|| previousRecipientEmail
                    							.equals(singleResponse.recipientEmail) == false) {
                    				previousRecipientEmail = singleResponse.recipientEmail;
                    				String recipientName = data.bundle
                    						.getRecipientNameForResponse(
                    								questionWithResponses.getKey(),
                    								singleResponse);

                    				if (questionWithResponses.getKey().recipientType == FeedbackParticipantType.TEAMS) {
                    					if (data.student.team
                    							.equals(singleResponse.recipientEmail)) {
                    						recipientName = "Your Team (" + recipientName
                    								+ ")";
                    					}
                    				} else if (data.student.email
                    						.equals(singleResponse.recipientEmail)
                    						&& data.student.name.equals(recipientName)) {
                    					recipientName = "You";
                    				}

                    				//if the giver is the same user, show the real name of the receiver. 
                    				if (giverName.equals("You")
                    						&& (!recipientName.equals("You"))) {
                    					recipientName = data.bundle
                    							.getNameForEmail(singleResponse.recipientEmail);
                    				}
                    %>
                    <div class="panel panel-primary">
                        <div class="panel-heading"><b>To:</b> <%=recipientName%></div>
                        <table class="table">
                            <tbody>    
                        <%
                            }
                        %>
                            <tr class="resultSubheader">
                                <td>
                                    <span class="bold"><b>From:</b></span> <%=giverName%>
                                </td>
                            </tr>
                            <tr>
                                <td class="multiline"><%=singleResponse.getResponseDetails().getAnswerHtml(questionDetails)%></td>
                            </tr>
                        <%
                            List<FeedbackResponseCommentAttributes> responseComments = data.bundle.responseComments.get(singleResponse.getId());
                            if (responseComments != null) {
                        %>
                            <tr>
                                <td>
                                    <ul class="list-group comment-list">
                                            <%
                                                for (FeedbackResponseCommentAttributes comment : responseComments) {
                                            %>
                                                    <li class="list-group-item list-group-item-warning">
                                                        <span class="text-muted">From: <%=comment.giverEmail %> [<%=comment.createdAt %>]</span>
                                                        <div><%=comment.commentText.getValue() %></div>
                                                    </li>
                                            <%
                                                }
                                            %>
                                    </ul>
                                 </td>    
                            </tr>
                        <%
                            }
                            
                            // Close table if going to be new recipient
                            boolean closeTable = true;
                            if(!itr.hasNext()) {
                                closeTable = true;
                            } else if (itr.next().recipientEmail.equals(singleResponse.recipientEmail)) {
                                itr.previous();
                                closeTable = false;
                            } else {
                                itr.previous();
                            }
                            if (closeTable) {
                        %>
                                </tbody>
                            </table>
                        </div>
                    <%
                            }
                        }
                    %>
                    </div>
                </div>
                <br />
            <% 
                }
                   if (questionsWithResponses.isEmpty()) {
            %>
                    <div class="col-sm-12" style="color: red">There are currently no responses for you for this feedback session.</div>
            <% 
                } 
            %>
        </div>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>