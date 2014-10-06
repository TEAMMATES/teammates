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
    boolean showAll = data.bundle.isComplete;
    boolean shouldCollapsed = data.bundle.responses.size() > 500;
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
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByQuestion.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxResponseRate.js"></script>
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
                <h1>Session Results</h1>
            </div>            
            <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
            <br>
            <%  
                int questionIndex = -1;
                for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries : data.bundle
                        .getQuestionResponseMap().entrySet()) {
                    questionIndex++;
                    FeedbackQuestionAttributes question = responseEntries.getKey();
            %>
            <div class="panel panel-info">
                <div class="panel-heading<%= showAll ? "" : " ajax_submit"%>">
                    <form style="display:none;" id="seeMore-<%=question.questionNumber%>" class="seeMoreForm-<%=question.questionNumber%>" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=data.bundle.feedbackSession.courseId %>">
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=data.bundle.feedbackSession.feedbackSessionName %>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="<%=data.groupByTeam%>">
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="<%=data.sortType%>">
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on" id="showStats-<%=question.questionNumber%>">
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER %>" value="<%=question.questionNumber %>">
                    </form>
                    <div class='display-icon pull-right'>
                    <span class="glyphicon <%= showAll && !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %> pull-right"></span>
                    </div>
                    <strong>Question <%=question.questionNumber%>: </strong><span class="text-preserve-space"><%=data.bundle.getQuestionText(question.getId())%><%
                        FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
                        out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, ""));
                    %></span>
                </div>
                <div class="panel-collapse collapse <%= showAll && !shouldCollapsed ? "in" : "" %>">
                <div class="panel-body padding-0" id="questionBody-<%=questionIndex%>">
                    <%
                        if(responseEntries.getValue().size() == 0){
                    %>
                        <div class="col-sm-12">
                            <i class="text-muted">There are no responses for this question.</i>
                        </div>
                    <%
                        }
                        if(showAll && responseEntries.getValue().size() > 0) {
                    %>                
                    <div class="resultStatistics">
                        <%=questionDetails.getQuestionResultStatisticsHtml(responseEntries.getValue(), question, data.account, data.bundle, "question")%>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-striped table-bordered dataTable margin-0">
                            <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                                <tr>
                                    <th id="button_sortFromName" class="button-sort-none" onclick="toggleSort(this,1)" style="width: 15%;">
                                        Giver
                                        <span class="icon-sort unsorted"></span>
                                    </th>
                                    <th id="button_sortFromTeam" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 15%;">
                                        Team
                                        <span class="icon-sort unsorted"></span>
                                    </th>
                                    <th id="button_sortToName" class="button-sort-none" onclick="toggleSort(this,3)" style="width: 15%;">
                                        Recipient
                                        <span class="icon-sort unsorted"></span>
                                    </th>
                                    <th id="button_sortToTeam" class="button-sort-ascending" onclick="toggleSort(this,4)" style="width: 15%;">
                                        Team
                                        <span class="icon-sort unsorted"></span>
                                    </th>
                                    <th id="button_sortFeedback" class="button-sort-none" onclick="toggleSort(this,5)">
                                        Feedback
                                        <span class="icon-sort unsorted"></span>
                                    </th>
                                </tr>
                            <thead>
                            <tbody>
                                <%
                                    for(FeedbackResponseAttributes responseEntry: responseEntries.getValue()) {
                                %>
                                <tr>
                                <%
                                    String giverName = data.bundle.getGiverNameForResponse(question, responseEntry);
                                    String giverTeamName = data.bundle.getTeamNameForEmail(responseEntry.giverEmail);

                                    String recipientName = data.bundle.getRecipientNameForResponse(question, responseEntry);
                                    String recipientTeamName = data.bundle.getTeamNameForEmail(responseEntry.recipientEmail);
                                %>
                                    <td class="middlealign"><%=giverName%></td>
                                    <td class="middlealign"><%=giverTeamName%></td>
                                    <td class="middlealign"><%=recipientName%></td>
                                    <td class="middlealign"><%=recipientTeamName%></td>
                                    <td class="text-preserve-space"><%=data.bundle.getResponseAnswerHtml(responseEntry, question)%></td>
                                </tr>        
                                <%
                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                    <% } %>
                </div>
                </div>
            </div>
            <%
                }
            %>
            
            <% if(data.selectedSection.equals("All") && (!data.bundle.isComplete || data.bundle.responses.size() > 0)){ %>
            <div class="panel panel-warning">
                <div class="panel-heading<%= showAll ? "" : " ajax_response_rate_submit"%>">
                    <form style="display:none;" id="responseRate" class="responseRateForm" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_AJAX_RESPONSE_RATE%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=data.bundle.feedbackSession.courseId %>">
                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=data.bundle.feedbackSession.feedbackSessionName %>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                    </form>
                    <div class='display-icon pull-right'>
                    <span class="glyphicon <%= showAll ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %> pull-right"></span>
                    </div>
                    Participants who have not responded to any question</div>
                <div class="panel-collapse collapse <%= showAll ? "in" : "" %>">
            <% if(showAll) {
                // Only output the list of students who haven't responded when there are responses.
                FeedbackSessionResponseStatus responseStatus = data.bundle.responseStatus;
                if (data.selectedSection.equals("All") && !responseStatus.noResponse.isEmpty()) {
            %>          
                    <div class="panel-body padding-0">
                        <table class="table table-striped table-bordered margin-0">
                            <tbody>
                            <%  
                                List<String> students = responseStatus.getStudentsWhoDidNotRespondToAnyQuestion();
                                for (String studentName : students) {
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
            <%
                    } else {
            %>
                    <div class="panel-body">
                        All students have responsed to some questions in this session.
                    </div>
            <%
                    }
                } 
            %>
                </div>
                </div>
            <% } %>
            </div>    
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>