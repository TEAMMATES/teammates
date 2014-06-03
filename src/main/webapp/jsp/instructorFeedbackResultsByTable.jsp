<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResponseStatus"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%
    InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
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
    <script type="text/javascript" src="/js/instructor.js"></script>
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
                for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries : data.bundle
                        .getQuestionResponseMap().entrySet()) {
            %>
            <div class="well well-default">
                    <h4>Question <%=responseEntries.getKey().questionNumber%>:<br><%=data.bundle.getQuestionText(responseEntries.getKey().getId())%><%
                        Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;
                        FeedbackQuestionAttributes question = questions.get(responseEntries.getKey().getId());
                        FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                        out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, ""));
                    %></h4>
                    <div class="panel panel-primary">
                    <table class="table table-striped table-bordered dataTable">
                    <thead class="fill-primary">
                        <tr>
                            <th id="button_sortFrom" class="button-sort-ascending" onclick="toggleSort(this,1)" style="width: 25%;">From<span class="icon-sort unsorted"></span></th>
                            <th id="button_sortTo" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 25%;">To<span class="icon-sort unsorted"></span></th>
                            <th id="button_sortFeedback" class="button-sort-none" onclick="toggleSort(this,3)" style="width: 50%;">Feedback<span class="icon-sort unsorted"></span></th>
                        </tr>
                    <thead>
                    <tbody>
                        <%
                            for(FeedbackResponseAttributes responseEntry: responseEntries.getValue()) {
                        %>
                        <tr>
                        <%
                            String giverName = data.bundle.getGiverNameForResponse(responseEntries.getKey(), responseEntry);
                            String giverTeamName = data.bundle.getTeamNameForEmail(responseEntry.giverEmail);
                            giverName = data.bundle.appendTeamNameToName(giverName, giverTeamName);

                            String recipientName = data.bundle.getRecipientNameForResponse(responseEntries.getKey(), responseEntry);
                            String recipientTeamName = data.bundle.getTeamNameForEmail(responseEntry.recipientEmail);
                            recipientName = data.bundle.appendTeamNameToName(recipientName, recipientTeamName);
                        %>
                            <td class="middlealign"><%=giverName%></td>
                            <td class="middlealign"><%=recipientName%></td>
                            <td class="multiline"><%=responseEntry.getResponseDetails().getAnswerHtml()%></td>
                        </tr>        
                        <%
                            }
                        %></tbody>
                    </table></div>
                </div>
            <br>
            <%
                }
            %>
            
            <%
            // Only output the list of students who haven't responded when there are responses.
            FeedbackSessionResponseStatus responseStatus = data.bundle.responseStatus;
            if (!responseStatus.hasResponse.isEmpty()) {
        %>
                <div class="panel panel-info">
                    <div class="panel-heading">Additional Information</div>
                    
                    <table class="table table-striped">
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

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>