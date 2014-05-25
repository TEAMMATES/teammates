<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
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
    <script type="text/javascript" src="/js/AnchorPosition.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
    <!-- Bootstrap core JavaScript ================================================== -->
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body onload="">
    <div id="dhtmltooltip"></div>
    <div id="frameTop">
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
    </div>

    <div id="frameBody" class="container">
        <div id="frameBodyWrapper">
            <div id="topOfPage"></div>
            <div id="headerOperation">
                <h1>Feedback Results - Instructor</h1>
            </div>
            <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
            <br>
            <%
                Map<String, Map<String, List<FeedbackResponseAttributes>>> allResponses = data.bundle.getResponsesSortedByGiver();
                Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;

                int giverIndex = 0;
                for (Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>> responsesFromGiver : allResponses.entrySet()) {
                    giverIndex++;

            
                Map<String, List<FeedbackResponseAttributes> > giverData = responsesFromGiver.getValue();
                Object[] giverDataArray =  giverData.keySet().toArray();
                FeedbackResponseAttributes firstResponse = giverData.get(giverDataArray[0]).get(0);
                String targetEmail = firstResponse.giverEmail.replace(Const.TEAM_OF_EMAIL_OWNER,"");
                String targetEmailDisplay = firstResponse.giverEmail;

            %>
            <div class="backgroundBlock">
                <h2 class="color_white">
                    From: <%=responsesFromGiver.getKey()%>
      <a class="emailIdLink" href="mailTo:<%=targetEmail%> ">[<%=targetEmailDisplay%>]</a></h2>
                <%
                    int recipientIndex = 0;
                    for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesFromGiverToRecipient : responsesFromGiver.getValue().entrySet()) {
                        recipientIndex++;
                %>
                <table class="resultTable" style="width: 100%">
                    <thead>
                        <tr>
                            <th class="leftalign"><span class="bold">To: </span><%=responsesFromGiverToRecipient.getKey()%></th>
                        </tr>
                    </thead>
                    <%
                        int qnIndx = 1;
                        for (FeedbackResponseAttributes singleResponse : responsesFromGiverToRecipient.getValue()) {
                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                            FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                    %>
                    <tr class="resultSubheader">
                        <td class="multiline"><span class="bold">Question <%=question.questionNumber%>: </span><%
                                out.print(InstructorFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText));
                                out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-"+giverIndex+"-recipient-"+recipientIndex));
                        %></td>
                    </tr>
                    <tr>
                        <td class="multiline"><span class="bold">Response: </span><%=singleResponse.getResponseDetails().getAnswerHtml()%></td>
                    </tr>
                    <%
                            qnIndx++;
                        }
                        if (responsesFromGiverToRecipient.getValue().isEmpty()) {
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
            <br>
            <br>
            <%
                }
            %>
            
            <%
                // Only output the list of students who haven't responded when there are responses.
                FeedbackSessionResponseStatus responseStatus = data.bundle.responseStatus;
                if (!responseStatus.hasResponse.isEmpty()) {
            %>
            <div class="backgroundBlock">
                <h2 class="color_white">Student Response Information</h2>
                
                <table class="resultTable" style="width: 100%">
                    <thead>
                        <tr>
                            <th>Students Who Did Not Respond to Any Question</th>
                        </tr>
                    </thead>
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

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>