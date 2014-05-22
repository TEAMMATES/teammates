<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ListIterator"%>
<%@ page import="teammates.common.util.Assumption"%>
<%@ page import="teammates.common.util.Const"%>
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
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" media="screen">
    <link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/studentFeedback.css" type="text/css" media="screen">
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
    <div id="dhtmltooltip"></div>
    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />

    <div id="frameBody" class="container">
        <div id="frameBodyWrapper">
            <div id="topOfPage"></div>
            <h2>Feedback Results - Student</h2>
            <br />
            <div class="panel panel-default">
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
                                <label class="col-sm-2 control-label">Duration:</label>
                                <div class="col-sm-10">
                                    <p class="form-control-static">from:
                                        <%=StudentFeedbackResultsPageData.displayDateTime(data.bundle.feedbackSession.startTime)%> To: 
                                        <%=StudentFeedbackResultsPageData.displayDateTime(data.bundle.feedbackSession.endTime)%></p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <br />
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <%
                int qnIndx = 0;
                Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = data.bundle
                        .getQuestionResponseMap();

                for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionWithResponses : questionsWithResponses
                        .entrySet()) {
                    qnIndx++;
                    
                    FeedbackAbstractQuestionDetails questionDetails = questionWithResponses.getKey().getQuestionDetails();
            %>
                <br />
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h4>Question <%=qnIndx%>: <%=StudentFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText)%>
                        <%=questionDetails.getQuestionAdditionalInfoHtml(qnIndx, "")%></h4>
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
                        <div class="panel-heading">To: <%=recipientName%></div>
                        <table class="table">
                            <tbody>    
                        <%
                            }
                        %>
                            <tr class="resultSubheader">
                                <td>
                                    <span class="bold">From:</span> <%=giverName%>
                                </td>
                            </tr>
                            <tr>
                                <td class="multiline"><%=singleResponse.getResponseDetails().getAnswerHtml()%></td>
                            </tr>
                        <%
                            List<FeedbackResponseCommentAttributes> responseComments = data.bundle.responseComments.get(singleResponse.getId());
                            if (responseComments != null) {
                        %>
                            <tr>
                                <td>
                                    <div class="panel panel-default">
                                        <div class="panel-heading">Comments: </div>
                                        <table class="table table-bordered table-hover">
                                            <%
                                            	for (FeedbackResponseCommentAttributes comment : responseComments) {
                                            %>
                                            <tr>
                                                <td class="col-md-6"><%=comment.commentText.getValue()%></td>
                                                <td class="col-md-3"><%=comment.giverEmail%></td>
                                                <td class="col-md-3"><%=comment.createdAt%></td>
                                            </tr>
                                            <%
                                                }
                                            %>
                                        </table>
                                    </div>
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
            <% 
                }
                   if (questionsWithResponses.isEmpty()) {
            %>                
                    <br><br><br>
                    <div class="bold color_red centeralign">There are currently no responses for you for this feedback session.</div>
            <% 
                } 
            %>
        </div>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
    <!-- Placed at the end of the document so the pages load faster -->
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/tooltip.js"></script>
    <script type="text/javascript" src="/js/AnchorPosition.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
    <!-- Bootstrap core JavaScript ================================================== -->
    <script src="/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>