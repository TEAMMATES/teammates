<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.Collections"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResponseStatus" %>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionType"%>
<%
    InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData) request.getAttribute("data");
    FieldValidator validator = new FieldValidator();
    boolean showAll = data.bundle.isComplete;
    boolean shouldCollapsed = data.bundle.responses.size() > 500;
    boolean groupByTeamEnabled = (data.groupByTeam == null || !data.groupByTeam.equals("on")) ? false : true;
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
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByRQG.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxResponseRate.js"></script>
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

            <% if(!showAll) {
                    if(data.selectedSection.equals("All")){
                    int sectionIndex = 0; 
                    for(String section: data.sections){
            %>
                        <div class="panel panel-success">
                                <div class="panel-heading ajax_submit">
                                    <div class="row">
                                        <div class="col-sm-9 panel-heading-text">
                                            <strong><%=section%></strong>
                                        </div>
                                        <div class="col-sm-3">
                                            <div class="pull-right">
                                                <a class="btn btn-success btn-xs" id="collapse-panels-button-section-<%=sectionIndex%>" data-toggle="tooltip" title='Collapse or expand all <%= groupByTeamEnabled == true ? "team" : "student" %> panels. You can also click on the panel heading to toggle each one individually.' style="display:none;">
                                                    Expand
                                                    <%= groupByTeamEnabled == true ? " Teams" : " Students" %>
                                                </a>
                                                &nbsp;
                                                <div class="display-icon" style="display:inline;">
                                                    <span class="glyphicon glyphicon-chevron-down"></span>
                                                </div>
                                            </div>
                                         </div>
                                    </div>

                                    <form style="display:none;" id="seeMore-<%=sectionIndex%>" class="seeMoreForm-<%=sectionIndex%>" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=data.bundle.feedbackSession.courseId %>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=data.bundle.feedbackSession.feedbackSessionName %>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION %>" value="<%=section%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="<%=data.groupByTeam%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="<%=data.sortType%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on" id="showStats-<%=sectionIndex%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX%>" value="on" id="mainIndex-<%=sectionIndex%>">
                                    </form>
                                </div>
                                <div class="panel-collapse collapse">
                                <div class="panel-body">
                                </div>
                                </div>
                        </div>
            <%
                    sectionIndex++;
                    }
            %>
                    <div class="panel panel-success">
                            <div class="panel-heading ajax_submit">
                                <div class="row">
                                        <div class="col-sm-9 panel-heading-text">
                                            <strong>Not in a section</strong>
                                        </div>
                                        <div class="col-sm-3">
                                            <div class="pull-right">
                                                <a class="btn btn-success btn-xs" id="collapse-panels-button-section-<%=sectionIndex%>" data-toggle="tooltip" title='Collapse or expand all <%= groupByTeamEnabled == true ? "team" : "student" %> panels. You can also click on the panel heading to toggle each one individually.' style="display:none;">
                                                    Expand
                                                    <%= groupByTeamEnabled == true ? " Teams" : " Students" %>
                                                </a>
                                                &nbsp;
                                                <div class="display-icon" style="display:inline;">
                                                    <span class="glyphicon glyphicon-chevron-down"></span>
                                                </div>
                                            </div>
                                         </div>
                                    </div>
                                <form style="display:none;" id="seeMore-<%=sectionIndex%>" class="seeMoreForm-<%=sectionIndex%>" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=data.bundle.feedbackSession.courseId %>">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=data.bundle.feedbackSession.feedbackSessionName %>">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION %>" value="None">
                                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="<%=data.groupByTeam%>">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="<%=data.sortType%>">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_MAIN_INDEX%>" value="on" id="mainIndex-<%=sectionIndex%>">
                                </form>
                            </div>
                            <div class="panel-collapse collapse">
                            <div class="panel-body">
                            </div>
                            </div>
                    </div>
            <%  
                    } else {

            %>
                    <div class="panel panel-success">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-sm-9 panel-heading-text">
                                    <strong><%=data.selectedSection%></strong>                   
                                </div>
                                <div class="col-sm-3">
                                    <div class="pull-right">
                                        <span class="glyphicon glyphicon-chevron-up"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="panel-collapse collapse in">
                            <div class="panel-body" id="sectionBody-0">
                                <%=InstructorFeedbackResultsPageData.EXCEEDING_RESPONSES_ERROR_MESSAGE%>
                            </div>
                        </div>
                    </div>
            <%    
                    }
                } else {
            %>

            <%
                String currentTeam = null;
                boolean newTeam = false;
                String currentSection = null;
                boolean newSection = false;
                int sectionIndex = -1;
                int teamIndex = 0;
            %>
        <%
            Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> allResponses = data.bundle.getResponsesSortedByRecipientQuestionGiver(groupByTeamEnabled);
            Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> teamResponses = data.bundle.getQuestionResponseMapByRecipientTeam();
            Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;
            int recipientIndex = data.startIndex;
            
            Set<String> teamMembersEmail = new HashSet<String>(); 
            Set<String> teamMembersWithResponses = new HashSet<String>();
            
            Set<String> teamsInSection = new HashSet<String>();
            Set<String> receivingTeams = new HashSet<String>();
            
            Set<String> sectionsInCourse = data.bundle.rosterSectionTeamNameTable.keySet();
            Set<String> receivingSections = new HashSet<String>();
            
            
            for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesForRecipient : allResponses.entrySet()) {
                recipientIndex++;
                
                Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> recipientData = responsesForRecipient.getValue();
                Object[] recipientDataArray =  recipientData.keySet().toArray();
                FeedbackResponseAttributes firstResponse = recipientData.get(recipientDataArray[0]).get(0);
                String targetEmail = firstResponse.recipientEmail;

                FeedbackParticipantType firstQuestionRecipientType = questions.get(firstResponse.feedbackQuestionId).recipientType;
                
                String mailtoStyleAttr = (firstQuestionRecipientType == FeedbackParticipantType.NONE || 
                                firstQuestionRecipientType == FeedbackParticipantType.TEAMS || 
                                targetEmail.contains("@@"))?"style=\"display:none;\"":"";
                
        %>

        <%
            if(currentTeam != null && !(data.bundle.getTeamNameForEmail(targetEmail)=="" ? currentTeam.equals(data.bundle.getNameForEmail(targetEmail)): currentTeam.equals(data.bundle.getTeamNameForEmail(targetEmail)))) {
                currentTeam = data.bundle.getTeamNameForEmail(targetEmail);
                if(currentTeam.equals("")){
                    currentTeam = data.bundle.getNameForEmail(targetEmail);
                }
                newTeam = true;

                // print out the "missing response" rows for the previous team  
                Set<String> teamMembersWithNoReceivedResponses = new HashSet<String>(teamMembersEmail);
                teamMembersWithNoReceivedResponses.removeAll(teamMembersWithResponses);
                
                for (String email : teamMembersWithNoReceivedResponses) {
        %>
                    <div class="panel panel-primary">
                    <div class="panel-heading">
                        To: 
            <%
                    if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email).isEmpty()) {
            %>
                        <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="<%=data.getProfilePictureLink(email)%>">
                            <strong><%=data.bundle.getFullNameFromRoster(email)%></strong>
                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                            <a class="link-in-dark-bg" href="mailTo:<%=email%>"  >[<%=email%>]</a>
                        </div>
            <%
                    } else {
            %>
                <div class="inline panel-heading-text">
                    <strong><%=data.bundle.getFullNameFromRoster(email)%></strong>
                    <a class="link-in-dark-bg" href="mailTo:<%=email%>"  >[<%=email%>]</a>
                </div>
            <%
                    }
            %>
                <span class='glyphicon glyphicon-chevron-up pull-right'></span>
            </div>
            <div class='panel-collapse collapse in'>
                <div class="panel-body"> There are no responses received by this user 
                </div>
            </div>
            </div>
        <%
                }
                if (groupByTeamEnabled) {
        %>
                    </div>
                    </div>
                </div>
        <%
                }
            }
        %>

        <% 
            if(currentSection != null && !firstResponse.recipientSection.equals(currentSection)){
                currentSection = firstResponse.recipientSection;
                newSection = true;
        %>
                </div>
                </div>
            </div>
        <% 
            }
        %>

        <%
        
        if(currentSection == null || newSection == true){
                    currentSection = firstResponse.recipientSection;
                    newSection = false;
                    sectionIndex++;
                    
                    receivingSections.add(currentSection);
                    teamsInSection = data.bundle.getTeamsInSectionFromRoster(currentSection);
                    receivingTeams = new HashSet<String>();
        %>
                <div class="panel panel-success">
                    <div class="panel-heading">
                        <div class="row">
                            <div class="col-sm-9 panel-heading-text">
                                <strong><%=currentSection.equals("None")? "Not in a section" : currentSection%></strong>                        
                            </div>
                            <div class="col-sm-3">
                                <div class="pull-right">
                                    <a class="btn btn-success btn-xs" id="collapse-panels-button-section-<%=sectionIndex%>" data-toggle="tooltip" title='Collapse or expand all <%= groupByTeamEnabled == true ? "team" : "student" %> panels. You can also click on the panel heading to toggle each one individually.'>
                                        <%= shouldCollapsed ? "Expand " : "Collapse " %>
                                        <%= groupByTeamEnabled == true ? "Teams" : "Students" %>
                                    </a>
                                    &nbsp;
                                    <span class="glyphicon glyphicon-chevron-up"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="panel-collapse collapse in">
                    <div class="panel-body" id="sectionBody-<%=sectionIndex%>">
        <%
            }
        %>

        <%
        	if(currentTeam==null || newTeam==true) {
                        currentTeam = data.bundle.getTeamNameForEmail(targetEmail);
                        
                        if(currentTeam.equals("")){
                            currentTeam = data.bundle.getNameForEmail(targetEmail);
                        }
                        
                        teamMembersEmail = data.bundle.getTeamMembersFromRoster(currentTeam);
                        
                        newTeam = false;
                        
                        receivingTeams.add(currentTeam);
                        if (groupByTeamEnabled) {
                            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> currentTeamResponses = teamResponses.get(currentTeam);
        %>
                <div class="panel panel-warning">
                    <div class="panel-heading">
                        <div class="inline panel-heading-text">
                            <strong><%=currentTeam%></strong>
                        </div>
                        <span class='glyphicon <%= !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %> pull-right'></span>
                    </div>
                    <div class='panel-collapse collapse <%= shouldCollapsed ? "" : "in" %>'>
                    <div class="panel-body background-color-warning">
                        <div class="resultStatistics">
                            <%
                                if(currentTeamResponses.size() > 0){
                            %>
                                <h3><%=currentTeam%> Received Responses Statistics </h3>
                                <hr class="margin-top-0">
                                <%
                                    int numStatsShown = 0;
                                    for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> teamResponseEntries : currentTeamResponses.entrySet()) {
                                        FeedbackQuestionAttributes question = questions.get(teamResponseEntries.getKey().getId());
                                        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                        String statsHtml = questionDetails.getQuestionResultStatisticsHtml(teamResponseEntries.getValue(), question, data.account, data.bundle, "recipient-question-giver");
                                        if(statsHtml != ""){
                                            numStatsShown++;
                                %>
                                            <div class="panel panel-info">
                                                <div class="panel-heading">
                                                    <strong>Question <%=teamResponseEntries.getKey().questionNumber%>: </strong><span class="text-preserve-space"><%=data.bundle.getQuestionText(teamResponseEntries.getKey().getId())%><%
                                                        out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, ""));
                                                    %></span>
                                                </div>
                                                <div class="panel-body padding-0">                
                                                    <div class="resultStatistics">
                                                        <%=statsHtml%>
                                                    </div>
                                                </div>
                                            </div>
                                <%
                                        }
                                    }
                                    if(numStatsShown == 0){
                                %>
                                        <p class="text-color-gray"><i>No statistics available.</i></p>
                            <%
                                    }
                                }
                            %>
                            <%
                                if(currentTeamResponses.size() > 0){
                            %>
                                <div class="row">
                                    <div class="col-sm-9">
                                        <h3><%=currentTeam%> Detailed Responses </h3>
                                    </div>
                                    <div class="col-sm-3 h3">
                                        <a class="btn btn-warning btn-xs pull-right" id="collapse-panels-button-team-<%=teamIndex%>" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">
                                            <%= shouldCollapsed ? "Expand " : "Collapse " %> Students
                                        </a>
                                    </div>
                                </div>
                                <hr class="margin-top-0">
                            <%
                                }
                            %>
                        </div>
        <%
                        }
            }
            
        %>


                <div class="panel panel-primary">
                <div class="panel-heading">
                    To: 
                    <% if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, targetEmail).isEmpty()) { %>
                        <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="<%=data.getProfilePictureLink(targetEmail)%>">
                            <strong><%=responsesForRecipient.getKey()%></strong>
                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                            <a class="link-in-dark-bg" href="mailTo:<%= targetEmail%> " <%=mailtoStyleAttr%>>[<%=targetEmail%>]</a>
                        </div>
                    <% } else {%>
                    <div class="inline panel-heading-text">
                        <strong><%=responsesForRecipient.getKey()%></strong>                    
                        <a class="link-in-dark-bg" href="mailTo:<%= targetEmail%> " <%=mailtoStyleAttr%>>[<%=targetEmail%>]</a>
                    </div>
                    <% } %>
                    <span class='glyphicon <%= !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %> pull-right'></span>
                </div>
                <div class='panel-collapse collapse <%= shouldCollapsed ? "" : "in" %>'>
                <div class="panel-body">
                <%
                    
                    teamMembersWithResponses.add(targetEmail);
                       
                    int questionIndex = 0;
                    
                    for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForRecipientForQuestion : responsesForRecipient.getValue().entrySet()) {
                        questionIndex++;
                        FeedbackQuestionAttributes question = responsesForRecipientForQuestion.getKey();
                        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();

                        List<String> possibleGiversToRecipient = data.bundle.getPossibleGivers(question, targetEmail);
                        
                        List<FeedbackResponseAttributes> responseEntries = responsesForRecipientForQuestion.getValue();
                %>
                        <div class="panel panel-info">
                            <div class="panel-heading">Question <%=question.questionNumber%>: <span class="text-preserve-space"><%
                                    out.print(InstructorFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText));
                                    out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "recipient-"+recipientIndex+"-question-"+questionIndex));%></span>
                            </div>
                            <div class="panel-body padding-0">
                                <div class="resultStatistics">
                                    <%=questionDetails.getQuestionResultStatisticsHtml(responseEntries, question, data.account, data.bundle, "recipient-question-giver")%>
                                </div>
                                <table class="table table-striped table-bordered dataTable margin-0">
                                    <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                                        <tr>
                                            <th>
                                                Photo
                                            </th>
                                            <th id="button_sortFromName" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 15%;">
                                                Giver
                                                <span class="icon-sort unsorted"></span>
                                            </th>
                                            <th id="button_sortFromTeam" class="button-sort-ascending" onclick="toggleSort(this,3)" style="width: 15%;">
                                                Team
                                                <span class="icon-sort unsorted"></span>
                                            </th>
                                            <th id="button_sortFeedback" class="button-sort-none" onclick="toggleSort(this,4)">
                                                Feedback
                                                <span class="icon-sort unsorted"></span>
                                            </th>
                                        </tr>
                                    <thead>
                                    <tbody>
                                        <%
                                            for(FeedbackResponseAttributes responseEntry: responseEntries) {
                                        %>
                                                <tr>
                                        <%
                                            	String giverName = data.bundle.getGiverNameForResponse(question, responseEntry);
                                                String giverTeamName = data.bundle.getTeamNameForEmail(responseEntry.giverEmail);
                                                
                                                if (!data.bundle.isGiverVisible(responseEntry)) {
                                                    // do not show possible givers if givers are anonymised
                                                	possibleGiversToRecipient.clear();
                                                }
                                                if (question.giverType == FeedbackParticipantType.TEAMS) {
                                                  possibleGiversToRecipient.remove(data.bundle.getFullNameFromRoster(responseEntry.giverEmail));
                                                } else {
                                                  possibleGiversToRecipient.remove(responseEntry.giverEmail);
                                                }
                                                
                                                if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, responseEntry.giverEmail).isEmpty()) {
                                        %>
                                                    <td class="middlealign">
                                                        <div class="profile-pic-icon-click align-center" data-link="<%=data.getProfilePictureLink(responseEntry.giverEmail)%>">
                                                            <a class="student-profile-pic-view-link btn-link">
                                                                View Photo
                                                            </a>
                                                            <img src="" alt="No Image Given" class="hidden">
                                                        </div>
                                                    </td>
                                        <%
                                        	   } else {
                                        %>
                                                    <td class="middlealign">
                                                        <div class="align-center" data-link="">
                                                            <a class="btn-link">
                                                                View Photo
                                                            </a>
                                                        </div>
                                                    </td>
                                        <%
                                        	   }
                                        %>
                                                <td class="middlealign"><%=giverName%></td>
                                                <td class="middlealign"><%=giverTeamName%></td>
                                                <td class="text-preserve-space"><%=data.bundle.getResponseAnswerHtml(responseEntry, question)%></td>
                                            </tr>
                                        
                                        <%
                                            }
                                                                                                                                                                                                            
                                            for (String possibleGiverWithNoResponse : possibleGiversToRecipient) {
                                                if (questionDetails.shouldShowNoResponseText(possibleGiverWithNoResponse, targetEmail, question)) {
                                                    if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, possibleGiverWithNoResponse).isEmpty()) {
                                                                                    %>
                                                        <tr class="pending_response_row">
                                                            <td class="middlealign">
                                                                <div class="profile-pic-icon-click align-center" data-link="<%=data.getProfilePictureLink(possibleGiverWithNoResponse)%>">
                                                                    <a class="student-profile-pic-view-link btn-link">
                                                                        View Photo
                                                                    </a>
                                                                    <img src="" alt="No Image Given" class="hidden">
                                                                </div>
                                                            </td>
                                        <%
                                          	        } else {
                                        %>
                                                        <td class="middlealign">
                                                            <div class="align-center" data-link="">
                                                                <a class="btn-link">
                                                                    View Photo
                                                                </a>
                                                            </div>
                                                        </td>   
                                        <%
                                           	        }
                                        %>        
                                                        <td class="middlealign color_neutral"><%=data.bundle.getFullNameFromRoster(possibleGiverWithNoResponse)%></td>
                                                        <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(possibleGiverWithNoResponse)%></td>
                                                        <td class="text-preserve-space color_neutral"><%=questionDetails.getNoResponseTextInHtml(possibleGiverWithNoResponse, targetEmail, data.bundle, question)%> </td>
                                                    </tr>
                                        <%
                                        	    }
                                            }
                                        %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                <%
                	}
                %>
                </div>
                </div>
            </div>
        <%
        	}
            

            // print out the "missing response" rows for the last team
            Set<String> teamMembersWithNoReceivedResponses = new HashSet<String>(teamMembersEmail);
            teamMembersWithNoReceivedResponses.removeAll(teamMembersWithResponses);
                
            for (String email : teamMembersWithNoReceivedResponses) {
        %>
                <div class="panel panel-primary">
                <div class="panel-heading">
                    To: 
            <%
                if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email).isEmpty()) {
            %>
                    <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="<%=data.getProfilePictureLink(email)%>">
                        <strong><%=data.bundle.getFullNameFromRoster(email)%></strong>
                        <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                        <a class="link-in-dark-bg" href="mailTo:<%=email%>"  >[<%=email%>]</a>
                    </div>
            <%
                } else {
            %>
            <div class="inline panel-heading-text">
                <strong><%=data.bundle.getFullNameFromRoster(email)%></strong>
                <a class="link-in-dark-bg" href="mailTo:<%=email%>"  >[<%=email%>]</a>
            </div>
            <%
                }
            %>
                <span class='glyphicon glyphicon-chevron-up pull-right'></span>
            </div>
            <div class='panel-collapse collapse in'>
                <div class="panel-body"> There are no responses received by this user 
                </div>
            </div>
            </div>
        <%
            }        
            
        	//close the last team panel.
            if(groupByTeamEnabled==true) {
        %>
                    </div>
                    </div>
                </div>
        <%
        	}
                    Set<String> teamsWithNoResponseReceived = new HashSet<String>(teamsInSection);
                    teamsWithNoResponseReceived.removeAll(receivingTeams);
                    
                    
                        List<String> teamsWithNoResponseReceivedList = new ArrayList<String>(teamsWithNoResponseReceived);
                        Collections.sort(teamsWithNoResponseReceivedList);
                        for (String teamWithNoResponseReceived: teamsWithNoResponseReceivedList) {
                            if (groupByTeamEnabled) {
        %>
                        <div class="panel panel-warning">
                            <div class="panel-heading">
                                <div class="inline panel-heading-text">
                                    <strong> <%=teamWithNoResponseReceived%></strong>
                                </div>
                                <span class="glyphicon pull-right glyphicon-chevron-up"></span>
                            </div>
                            <div class="panel-collapse collapse in" id="panelBodyCollapse-2" style="height: auto;">
                                <div class="panel-body background-color-warning">
                                <%
                            }
                                	List<String> teamMembers = new ArrayList<String>(data.bundle.getTeamMembersFromRoster(teamWithNoResponseReceived));
                                    Collections.sort(teamMembers);
                              
                                    for (String teamMember : teamMembers) {
                                %>
                                 <div class="panel panel-primary">
                                                <div class="panel-heading">
                                                    To: 
                                                    <%
                                                	   if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, teamMember).isEmpty()) {
                                                    %>
                                                        <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="<%=data.getProfilePictureLink(teamMember)%>">
                                                            <strong><%=data.bundle.getFullNameFromRoster(teamMember)%></strong>
                                                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                            <a class="link-in-dark-bg" href="mailTo:<%=teamMember%>"  >[<%=teamMember%>]</a>
                                                        </div>
                                                    <%
                                                    	} else {
                                                    %>
                                                    <div class="panel-heading-text">
                                                        <strong><%=data.bundle.getFullNameFromRoster(teamMember)%></strong>
                                                        <a class="link-in-dark-bg" href="mailTo:<%=teamMember%>"  >[<%=teamMember%>]</a>
                                                    </div>
                                                    <%
                                                    	}
                                                    %>
                                                    <span class='glyphicon glyphicon-chevron-up pull-right'></span>
                                                </div>
                                                <div class='panel-collapse collapse in'>
                                                    <div class="panel-body"> There are no responses received by this user 
                                                    </div>
                                                </div>
                                             </div>
                                  <%
                                  	}
                                    if (groupByTeamEnabled) {
                                  %>
                                         
                                </div>
                            </div>
                        </div>                
                    <%
                                    }
                        
                    }
                    %>
        
                </div>
                </div>
        
            </div>

        <%
                    Set<String> sectionsWithNoResponseReceived = new HashSet<String>(sectionsInCourse);
                    sectionsWithNoResponseReceived.removeAll(receivingSections);
                    
                    if (data.selectedSection.equals("All")) {
                        List<String> sectionsWithNoResponseReceivedList = new ArrayList<String>(sectionsWithNoResponseReceived);
                        Collections.sort(sectionsWithNoResponseReceivedList);
                        
                        for (String sectionWithNoResponseReceived: sectionsWithNoResponseReceivedList) {
         %>
                        <div class="panel panel-success">
                            <div class="panel-heading">
                                <div class="inline panel-heading-text">
                                    <strong> <%=sectionWithNoResponseReceived%></strong>
                                </div>
                                <span class="glyphicon pull-right glyphicon-chevron-up"></span>
                            </div>
                            <div class="panel-collapse collapse in" id="panelBodyCollapse-2" style="height: auto;">
                                <div class="panel-body">
                                    <%
                                    	Set<String> teamsFromSection = data.bundle.getTeamsInSectionFromRoster(sectionWithNoResponseReceived);
                                                                    List<String> teamsFromSectionList = new ArrayList<String>(teamsFromSection);
                                                                    Collections.sort(teamsFromSectionList);
                                                                    
                                        for (String team : teamsFromSectionList) {
                                            List<String> teamMembers = new ArrayList<String>(data.bundle.getTeamMembersFromRoster(team));
                                            Collections.sort(teamMembers);
                                            if (groupByTeamEnabled) {
                                    %>
                                                <div class="panel panel-warning">
                                                  <div class="panel-heading">
                                                    <div class="inline panel-heading-text">
                                                        <strong> <%=team%></strong>
                                                    </div>
                                                      <span class="glyphicon pull-right glyphicon-chevron-up"></span>
                                                  </div>
                                                  <div class="panel-collapse collapse in" id="panelBodyCollapse-2" style="height: auto;">
                                                      <div class="panel-body background-color-warning">
                                      <%
                                            }
                                      	    for (String teamMember : teamMembers) {
                                      %>
                                                 <div class="panel panel-primary">
                                                    <div class="panel-heading">
                                                        To: 
                                                    <%
                                                    	if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, teamMember).isEmpty()) {
                                                    %>
                                                            <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="<%=data.getProfilePictureLink(teamMember)%>">
                                                                <strong><%=data.bundle.getFullNameFromRoster(teamMember)%></strong>
                                                                <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                <a class="link-in-dark-bg" href="mailTo:<%= teamMember%>"  >[<%=teamMember%>]</a>
                                                            </div>
                                                    <%
                                                    	} else {
                                                    %>
                                                        <div class="inline panel-heading-text">
                                                            <strong><%=data.bundle.getFullNameFromRoster(teamMember)%></strong>
                                                            <a class="link-in-dark-bg" href="mailTo:<%= teamMember%>"  >[<%=teamMember%>]</a>
                                                        </div>
                                                    <%  } %>
                                                        
                                                        <span class='glyphicon glyphicon-chevron-up pull-right'></span>
                                                    </div>
                                                    <div class='panel-collapse collapse in'>
                                                        <div class="panel-body"> There are no responses received by this user 
                                                        </div>
                                                    </div>
                                                 </div>
                                            
                                        <% 
                                            }
                                            if (groupByTeamEnabled) {
                                        %>
                                                </div>
                                                  </div>
                                              </div>    
                                    <% 
                                            }
                                        }
                                    
                                    %>
                                </div>
                            </div>
                        </div>                
        <% 
                        }
                    }
            }
        %>

        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BOTTOM%>" />
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>