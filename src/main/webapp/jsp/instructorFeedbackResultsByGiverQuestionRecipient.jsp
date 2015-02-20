<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.ArrayList"%>
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
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByGQR.js"></script>
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

            <%
            	if(!showAll) {
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
                                                <a class="btn btn-success btn-xs" id="collapse-panels-button-section-<%=sectionIndex%>" data-toggle="tooltip" title='Collapse or expand all <%=groupByTeamEnabled == true ? "team" : "student"%> panels. You can also click on the panel heading to toggle each one individually.' style="display:none;">
                                                    Expand
                                                    <%=groupByTeamEnabled == true ? " Teams" : " Students"%>
                                                </a>
                                                &nbsp;
                                                <div class="display-icon" style="display:inline;">
                                                    <span class="glyphicon glyphicon-chevron-down"></span>
                                                </div>
                                            </div>
                                         </div>
                                    </div>

                                    <form style="display:none;" id="seeMore-<%=sectionIndex%>" class="seeMoreForm-<%=sectionIndex%>" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" value="<%=section%>">
                                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
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
                                                <a class="btn btn-success btn-xs" id="collapse-panels-button-section-<%=sectionIndex%>" data-toggle="tooltip" title='Collapse or expand all <%=groupByTeamEnabled == true ? "team" : "student"%> panels. You can also click on the panel heading to toggle each one individually.' style="display:none;">
                                                    Expand
                                                    <%=groupByTeamEnabled == true ? " Teams" : " Students"%>
                                                </a>
                                                &nbsp;
                                                <div class="display-icon" style="display:inline;">
                                                    <span class="glyphicon glyphicon-chevron-down"></span>
                                                </div>
                                            </div>
                                         </div>
                                    </div>
                                <form style="display:none;" id="seeMore-<%=sectionIndex%>" class="seeMoreForm-<%=sectionIndex%>" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
                                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" value="None">
                                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
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
            Set<String> teamMembersEmail = new HashSet<String>(); 
            Set<String> teamMembersWithResponses = new HashSet<String>();
        %>
        <%
        	Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> allResponses = data.bundle.getResponsesSortedByGiverQuestionRecipient(groupByTeamEnabled);
                    Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> teamResponses = data.bundle.getQuestionResponseMapByGiverTeam();
                    Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;
                    int giverIndex = data.startIndex;
                    
                    Set<String> teamsInSection = new HashSet<String>();
                    Set<String> receivingTeams = new HashSet<String>();
                    
                    Set<String> sectionsInCourse = data.bundle.rosterSectionTeamNameTable.keySet();
                    Set<String> receivingSections = new HashSet<String>();
                    
                    for (Map.Entry<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> responsesFromGiver : allResponses.entrySet()) {
                        giverIndex++;
                        
                        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> giverData = responsesFromGiver.getValue();
                        Object[] giverDataArray =  giverData.keySet().toArray();
                        FeedbackResponseAttributes firstResponse = giverData.get(giverDataArray[0]).get(0);
                        String giverEmail = firstResponse.giverEmail;
                        boolean isGiverVisible = data.bundle.isGiverVisible(firstResponse);

                        FeedbackParticipantType firstQuestionGiverType = questions.get(firstResponse.feedbackQuestionId).giverType;
                        String mailtoStyleAttr = (firstQuestionGiverType == FeedbackParticipantType.NONE || 
                                        firstQuestionGiverType == FeedbackParticipantType.TEAMS || 
                                        giverEmail.contains("@@"))?"style=\"display:none;\"":"";
        %>

        <%
        	if(currentTeam != null && !(data.bundle.getTeamNameForEmail(giverEmail)=="" ? currentTeam.equals(data.bundle.getNameForEmail(giverEmail)): currentTeam.equals(data.bundle.getTeamNameForEmail(giverEmail)))) {
                        currentTeam = data.bundle.getTeamNameForEmail(giverEmail);
                        newTeam = true;

                        // print out the "missing response" rows for the previous team 
                        Set<String> teamMembersWithoutReceivingResponses = new HashSet<String>(teamMembersEmail);
                        teamMembersWithoutReceivingResponses.removeAll(teamMembersWithResponses);
                        
                        List<String> teamMembersList = new ArrayList<String>(teamMembersWithoutReceivingResponses);
                        Collections.sort(teamMembersList);
                        for (String email : teamMembersList) {
        %>
                            <div class="panel panel-primary">
                            <div class="panel-heading">
                                From: 
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
                                <div class="pull-right">
                                <% 
                                    boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(email), 
                                                                                                        data.feedbackSessionName, 
                                                                                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                                    String disabledAttribute = !isAllowedToModerate? "disabled=\"disabled\"" : "";
                                %>
                                        <form class="inline" method="post" action="<%=data.getInstructorEditStudentFeedbackLink() %>" target="_blank"> 
                                        
                                            <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" <%=disabledAttribute%> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
                                            <input type="hidden" name="courseid" value="<%=data.courseId %>">
                                            <input type="hidden" name="fsname" value="<%= data.feedbackSessionName%>">
                                            <input type="hidden" name="moderatedstudent" value=<%= email%>>
                                        
                                        </form>
                                    &nbsp;
                                    <div class="display-icon" style="display:inline;">
                                        <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right'></span>
                                    </div>                
                                </div>
                            </div>
                            <div class='panel-collapse collapse in'>
                                <div class="panel-body"> There are no responses given by this user 
                                </div>
                            </div>
                            </div>
        <%
                        }

                        if(currentTeam.equals("")){
                            currentTeam = data.bundle.getNameForEmail(giverEmail);
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
        	if(currentSection != null && !firstResponse.giverSection.equals(currentSection)){
                        currentSection = firstResponse.giverSection;
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
                            currentSection = firstResponse.giverSection;
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
                                    <a class="btn btn-success btn-xs" id="collapse-panels-button-section-<%=sectionIndex%>" data-toggle="tooltip" title='Collapse or expand all <%=groupByTeamEnabled == true ? "team" : "student"%> panels. You can also click on the panel heading to toggle each one individually.'>
                                        <%=shouldCollapsed ? "Expand " : "Collapse "%>
                                        <%=groupByTeamEnabled == true ? "Teams" : "Students"%>
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
                        currentTeam = data.bundle.getTeamNameForEmail(giverEmail);
                        if(currentTeam.equals("")){
                            currentTeam = data.bundle.getNameForEmail(giverEmail);
                        }
                        
                        teamMembersWithResponses = new HashSet<String>();                                
                        teamMembersEmail = new HashSet<String>(data.bundle.getTeamMembersFromRoster(currentTeam));
                        
                        receivingTeams.add(currentTeam);
                        newTeam = false;

                        if (groupByTeamEnabled) {
                            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> currentTeamResponses = teamResponses.get(currentTeam);
        %>
                <div class="panel panel-warning">
                    <div class="panel-heading">
                        <div class="inline panel-heading-text">
                            <strong><%=currentTeam%></strong>
                        </div>
                        <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right'></span>
                    </div>
                    <div class='panel-collapse collapse <%=shouldCollapsed ? "" : "in"%>'>
                    <div class="panel-body background-color-warning">
                        <div class="resultStatistics">
                            <%
                            	if(currentTeamResponses.size() > 0){
                            %>
                                <h3><%=currentTeam%> Given Responses Statistics </h3>
                                <hr class="margin-top-0">
                                <%
                                	int numStatsShown = 0;
                                    for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> teamResponseEntries : currentTeamResponses.entrySet()) {
                                        FeedbackQuestionAttributes question = questions.get(teamResponseEntries.getKey().getId());
                                        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                        String statsHtml = questionDetails.getQuestionResultStatisticsHtml(teamResponseEntries.getValue(), question, data.account, data.bundle, "giver-question-recipient");
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
                                            <%=shouldCollapsed ? "Expand " : "Collapse "%> Students
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
                    From: 
                    <%
                        if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giverEmail).isEmpty()) {
                    %>
                            <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="<%=data.getProfilePictureLink(giverEmail)%>">
                                <strong><%=responsesFromGiver.getKey()%></strong>
                                <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                <a class="link-in-dark-bg" href="mailTo:<%=giverEmail%> " <%=mailtoStyleAttr%>>[<%=giverEmail%>]</a>
                            </div>
                    <%
                    	} else {
                    %>
                        <div class="inline panel-heading-text">
                            <strong><%=responsesFromGiver.getKey()%></strong>
                            <a class="link-in-dark-bg" href="mailTo:<%=giverEmail%> " <%=mailtoStyleAttr%>>[<%=giverEmail%>]</a>
                        </div>
                    <%
                    	}
                        teamMembersWithResponses.add(giverEmail);
                    %>
                    <div class="pull-right">
                    <% 
                        boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(giverEmail), 
                                                                                         data.feedbackSessionName, 
                                                                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                        String disabledAttribute = !isAllowedToModerate? "disabled=\"disabled\"" : "";
                        if (isGiverVisible && data.bundle.isParticipantIdentifierStudent(giverEmail)) { 
                    %>
                            <form class="inline" method="post" action="<%=data.getInstructorEditStudentFeedbackLink() %>" target="_blank"> 
                            
                                <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" <%= disabledAttribute %> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
                                <input type="hidden" name="courseid" value="<%=data.courseId %>">
                                <input type="hidden" name="fsname" value="<%= data.feedbackSessionName%>">
                                <input type="hidden" name="moderatedstudent" value=<%= giverEmail%>>
                            
                            </form>
                    <% } %>
                        &nbsp;
                        <div class="display-icon" style="display:inline;">
                            <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right'></span>
                        </div>                
                    </div>
                </div>
                <div class='panel-collapse collapse <%=shouldCollapsed ? "" : "in"%>'>
                <div class="panel-body">
                <%
                	int questionIndex = 0;
                                    for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromGiverForQuestion : responsesFromGiver.getValue().entrySet()) {
                                        questionIndex++;
                                        FeedbackQuestionAttributes question = responsesFromGiverForQuestion.getKey();
                                        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                        List<FeedbackResponseAttributes> responseEntries = responsesFromGiverForQuestion.getValue();
                                        
                                        List<String> possibleRecipientsForQuestion = data.bundle.getPossibleRecipients(question, giverEmail);
                %>
                        <div class="panel panel-info">
                            <div class="panel-heading">Question <%=question.questionNumber%>: <span class="text-preserve-space"><%
                                    out.print(InstructorFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText));
                                    out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-"+giverIndex+"-question-"+questionIndex));%></span>
                            </div>
                            <div class="panel-body padding-0">
                                <div class="resultStatistics">
                                    <%=questionDetails.getQuestionResultStatisticsHtml(responseEntries, question, data.account, data.bundle, "giver-question-recipient")%>
                                </div>
                                <table class="table table-striped table-bordered dataTable margin-0">
                                    <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                                        <tr>
                                            <th>Photo</th>
                                            <th id="button_sortTo" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 15%;">
                                                Recipient
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
                                            	String recipientName = data.bundle.getRecipientNameForResponse(question, responseEntry);
                                                String recipientTeamName = data.bundle.getTeamNameForEmail(responseEntry.recipientEmail);
                                                
                                                if (question.recipientType == FeedbackParticipantType.TEAMS) {
                                                  possibleRecipientsForQuestion.remove(data.bundle.getFullNameFromRoster(responseEntry.recipientEmail));
                                                } else {
                                                  possibleRecipientsForQuestion.remove(responseEntry.recipientEmail);
                                                }
                                                
                                                if (!data.bundle.isRecipientVisible(responseEntry)) {
                                                  // do not show possible recipients if recipients are anonymised
                                                  possibleRecipientsForQuestion.clear();
                                                }
                                                
                                                if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, responseEntry.recipientEmail).isEmpty()) {
                                        %>
                                                    <td class="middlealign">
                                                        <div class="profile-pic-icon-click align-center" data-link="<%=data.getProfilePictureLink(responseEntry.recipientEmail)%>">
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
                                                            <a class="student-profile-pic-view-link btn-link">
                                                                No Photo
                                                            </a>
                                                        </div>
                                                    </td>
                                        <%
                                        	    }
                                        %>
                                                <td class="middlealign"><%=recipientName%></td>
                                                <td class="middlealign"><%=recipientTeamName%></td>
                                                <td class="text-preserve-space"><%=data.bundle.getResponseAnswerHtml(responseEntry, question)%></td>
                                            </tr>        
                                        <%
                                            }
                                                                                        
                                            for (String possibleRecipientWithNoResponse : possibleRecipientsForQuestion) {
                                            	if (questionDetails.shouldShowNoResponseText(giverEmail, possibleRecipientWithNoResponse, question)) {
                                        %>
                                                        <tr class="pending_response_row">
                                               <%
                                               	        if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, possibleRecipientWithNoResponse).isEmpty()) {
                                               %>
                                                            <td class="middlealign">
                                                                <div class="profile-pic-icon-click align-center" data-link="<%=data.getProfilePictureLink(possibleRecipientWithNoResponse)%>">
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
                                                                    <a class="student-profile-pic-view-link btn-link">
                                                                        No Photo
                                                                    </a>
                                                                </div>
                                                            </td>
                                                    <%
                                                    	}
                                                    %>
                                                        <td class="middlealign color_neutral"><%=data.bundle.getFullNameFromRoster(possibleRecipientWithNoResponse)%></td>
                                                        <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(possibleRecipientWithNoResponse)%></td>
                                                        <td class="text-preserve-space color_neutral"><%=questionDetails.getNoResponseTextInHtml(giverEmail, possibleRecipientWithNoResponse, data.bundle, question)%></td>
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
            // print out the "missing response" rows for the last giving team
            Set<String> teamMembersWithoutReceivingResponses = new HashSet<String>(teamMembersEmail);
            teamMembersWithoutReceivingResponses.removeAll(teamMembersWithResponses);
            
            List<String> teamMembersList = new ArrayList<String>(teamMembersWithoutReceivingResponses);
            Collections.sort(teamMembersList);
            for (String email : teamMembersList) {
        %>
                <div class="panel panel-primary">
                <div class="panel-heading">
                    From: 
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
                    <div class="pull-right">
                    <% 
                        boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(email), 
                                                                                            data.feedbackSessionName, 
                                                                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                        String disabledAttribute = !isAllowedToModerate? "disabled=\"disabled\"" : "";
                    %>
                            <form class="inline" method="post" action="<%=data.getInstructorEditStudentFeedbackLink() %>" target="_blank"> 
                            
                                <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" <%=disabledAttribute%> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
                                <input type="hidden" name="courseid" value="<%=data.courseId %>">
                                <input type="hidden" name="fsname" value="<%= data.feedbackSessionName%>">
                                <input type="hidden" name="moderatedstudent" value=<%= email%>>
                            
                            </form>
                        &nbsp;
                        <div class="display-icon" style="display:inline;">
                            <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right'></span>
                        </div>                
                    </div>
                </div>
                <div class='panel-collapse collapse in'>
                    <div class="panel-body"> There are no responses given by this user 
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
                    
                    Set<String> teamsWithNoResponseGiven = new HashSet<String>(teamsInSection);
                    teamsWithNoResponseGiven.removeAll(receivingTeams);
                    
                    
                        List<String> teamsWithNoResponseGivenList = new ArrayList<String>(teamsWithNoResponseGiven);
                        Collections.sort(teamsWithNoResponseGivenList);
                        for (String teamWithNoResponseGiven: teamsWithNoResponseGivenList) {
                            if (groupByTeamEnabled) {
             %>
                          <div class="panel panel-warning">
                              <div class="panel-heading">
                                <div class="inline panel-heading-text">
                                  <strong> <%=teamWithNoResponseGiven%></strong>
                              </div>
                                  <span class="glyphicon pull-right glyphicon-chevron-up"></span>
                              </div>
                              <div class="panel-collapse collapse in" id="panelBodyCollapse-2" style="height: auto;">
                                  <div class="panel-body background-color-warning">
                                  <%
                            }
                                  	List<String> teamMembers = new ArrayList<String>(data.bundle.getTeamMembersFromRoster(teamWithNoResponseGiven));
                                    Collections.sort(teamMembers);
                                
                                    for (String teamMember : teamMembers) {
                                  %>
                                      	     <div class="panel panel-primary">
                                                <div class="panel-heading">
                                                    From: 
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
                                                    <div class="inline panel-heading-text">
                                                        <strong><%=data.bundle.getFullNameFromRoster(teamMember)%></strong>
                                                        <a class="link-in-dark-bg" href="mailTo:<%=teamMember%>"  >[<%=teamMember%>]</a>
                                                    </div>
                                                    <%
                                                    	}
                                                    %>
                                                    <div class="pull-right">
                                                    <% 
                                                        boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(teamMember), 
                                                                                                                        data.feedbackSessionName, 
                                                                                                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                                                        String disabledAttribute = !isAllowedToModerate? "disabled=\"disabled\"" : "";
                                                    %>
                                                            <form class="inline" method="post" action="<%=data.getInstructorEditStudentFeedbackLink() %>" target="_blank"> 
                                                            
                                                                <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" <%=disabledAttribute%> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
                                                                <input type="hidden" name="courseid" value="<%=data.courseId %>">
                                                                <input type="hidden" name="fsname" value="<%= data.feedbackSessionName%>">
                                                                <input type="hidden" name="moderatedstudent" value=<%= teamMember%>>
                                                            
                                                            </form>

                                                        &nbsp;
                                                        <div class="display-icon" style="display:inline;">
                                                            <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right'></span>
                                                        </div>                
                                                    </div>
                                                </div>
                                                <div class='panel-collapse collapse in'>
                                                    <div class="panel-body"> There are no responses given by this user 
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
                                                        From: 
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
                                                        <div class="pull-right">
                                                        <% 
                                                            boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(teamMember), 
                                                                                                                            data.feedbackSessionName, 
                                                                                                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                                                            String disabledAttribute = !isAllowedToModerate? "disabled=\"disabled\"" : "";
                                                        %>
                                                                <form class="inline" method="post" action="<%=data.getInstructorEditStudentFeedbackLink() %>" target="_blank"> 
                                                                    <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" <%=disabledAttribute %> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
                                                                    <input type="hidden" name="courseid" value="<%=data.courseId %>">
                                                                    <input type="hidden" name="fsname" value="<%= data.feedbackSessionName%>">
                                                                <input type="hidden" name="moderatedstudent" value=<%= teamMember%>>
                                                            
                                                                </form>
                                                        
                                                            &nbsp;
                                                            <div class="display-icon" style="display:inline;">
                                                                <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right'></span>
                                                            </div>                
                                                        </div>
                                                        
                                                        
                                                    </div>
                                                    <div class='panel-collapse collapse in'>
                                                        <div class="panel-body"> There are no responses given by this user 
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