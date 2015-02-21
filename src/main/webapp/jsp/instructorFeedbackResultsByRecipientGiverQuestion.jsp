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
    <script type="text/javascript" src="/js/feedbackResponseComments.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByRGQ.js"></script>
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
            	Map<String, Map<String, List<FeedbackResponseAttributes>>> allResponses = data.bundle.getResponsesSortedByRecipient(groupByTeamEnabled);
                Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;

                int recipientIndex = data.startIndex;
                
                Set<String> teamsInSection = new HashSet<String>();
                Set<String> receivingTeams = new HashSet<String>();
                
                Set<String> sectionsInCourse = data.bundle.rosterSectionTeamNameTable.keySet();
                Set<String> receivingSections = new HashSet<String>();
                
                
                for (Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>> responsesForRecipient : allResponses.entrySet()) {
                    recipientIndex++;
                    

                    Map<String, List<FeedbackResponseAttributes> > recipientData = responsesForRecipient.getValue();
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
                                Set<String> teamMembersWithoutReceivingResponses = teamMembersEmail;
                                teamMembersWithoutReceivingResponses.removeAll(teamMembersWithResponses);
                                
                                List<String> teamMembersWithNoResponses = new ArrayList<String>(teamMembersWithoutReceivingResponses);
                                Collections.sort(teamMembersWithNoResponses);
                                for (String email : teamMembersWithNoResponses) {
            %>
                                <div class="panel panel-primary">
                                <div class="panel-heading">
                                    To: 
                                    <% if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email).isEmpty()) { %>
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
                                currentTeam = data.bundle.getTeamNameForEmail(targetEmail);
                                if(currentTeam.equals("")){
                                    currentTeam = data.bundle.getNameForEmail(targetEmail);
                                }
                                teamMembersWithResponses = new HashSet<String>();                                
                                teamMembersEmail = data.bundle.getTeamMembersFromRoster(currentTeam);

                                teamIndex++;
                                newTeam = false;
                                
                                receivingTeams.add(currentTeam);
                                if (groupByTeamEnabled) {
            %>
                    <div class="panel panel-warning">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-sm-9 panel-heading-text">
                                    <strong><%=currentTeam%></strong>                     
                                </div>
                                <div class="col-sm-3">
                                    <div class="pull-right">
                                        <a class="btn btn-warning btn-xs" id="collapse-panels-button-team-<%=teamIndex%>" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">
                                            <%=shouldCollapsed ? "Expand " : "Collapse "%> Students
                                        </a>
                                        &nbsp;
                                        <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%>'></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class='panel-collapse collapse <%=shouldCollapsed ? "" : "in"%>'>
                        <div class="panel-body background-color-warning">
            <%
                                }
            	}
                
            %>


            <div class="panel panel-primary">
                <div class="panel-heading">
                    To: 
                    <%
                	    if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, targetEmail).isEmpty()) {
                    %>
                        <div class="middlealign profile-pic-icon-hover inline panel-heading-text" data-link="<%=data.getProfilePictureLink(targetEmail)%>">
                            <strong><%=responsesForRecipient.getKey()%></strong>
                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                            <a class="link-in-dark-bg" href="mailTo:<%=targetEmail%> " <%=mailtoStyleAttr%>>[<%=targetEmail%>]</a>
                        </div>
                    <%
                    	} else {
                    %>
                        <div class="inline panel-heading-text">
                            <strong><%=responsesForRecipient.getKey()%></strong>
                        </div>
                    <%
                    	}
                        teamMembersWithResponses.add(targetEmail);
                    %>
                    <span class='glyphicon <%=!shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right'></span>
                </div>
                <div class='panel-collapse collapse <%=shouldCollapsed ? "" : "in"%>'>
                <div class="panel-body">
                <%
                	int giverIndex = 0;
                    for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesForRecipientFromGiver : responsesForRecipient.getValue().entrySet()) {
                        giverIndex++;
                        String giverEmail = responsesForRecipientFromGiver.getValue().get(0).giverEmail;
                                        
                %>
                        <div class="row <%=giverIndex == 1? "": "border-top-gray"%>">
                            <div class="col-md-2">
                                <div class="col-md-12">
                                    From: 
                                    <%
                                    	if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, giverEmail).isEmpty()) {
                                    %>
                                            <div class="middlealign profile-pic-icon-hover inline-block" data-link="<%=data.getProfilePictureLink(giverEmail)%>">
                                                <strong><%=responsesForRecipientFromGiver.getKey()%></strong>
                                                <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                            </div>
                                    <%
                                    	} else {
                                    %>
                                       <strong><%=responsesForRecipientFromGiver.getKey()%></strong>
                                    <%
                                    	}
                                    %>
                                    
                                </div>
                                    
                                <div class="col-md-12 text-muted small"><br>
                                    To: 
                                <%
                                	if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, targetEmail).isEmpty()) {
                                %>
                                    <div class="middlealign profile-pic-icon-hover inline-block" data-link="<%=data.getProfilePictureLink(targetEmail)%>">
                                        <strong><%=responsesForRecipient.getKey()%></strong>
                                        <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                    </div>
                                <%
                                	} else {
                                %>
                                    <strong><%=responsesForRecipient.getKey()%></strong>
                                <%
                                	}
                                %> 
                                </div>
                            </div>
                            <div class="col-md-10">
                            <%
                            	int qnIndx = 1;
                                                            for (FeedbackResponseAttributes singleResponse : responsesForRecipientFromGiver.getValue()) {
                                                                FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                                                FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                            %>
                                    <div class="panel panel-info">
                                        <div class="panel-heading">Question <%=question.questionNumber%>: <span class="text-preserve-space"><%
                                                out.print(InstructorFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText));
                                                out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-"+giverIndex+"-recipient-"+recipientIndex));
                                        %></span></div>
                                        <div class="panel-body">
                                            <div style="clear:both; overflow: hidden">
                                                <div class="pull-left text-preserve-space"><%=data.bundle.getResponseAnswerHtml(singleResponse, question)%></div>
                                                <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" 
                                                    onclick="showResponseCommentAddForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>)"
                                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_ADD%>"
                                                    <% if (!data.instructor.isAllowedForPrivilege(singleResponse.giverSection,
                                                    		singleResponse.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                                                            || !data.instructor.isAllowedForPrivilege(singleResponse.recipientSection,
                                                                    singleResponse.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) { %>
                                                            disabled="disabled"
                                                    <% } %>
                                                    >
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
                                            <!-- frComment delete Form -->
                                            <form class="responseCommentDeleteForm pull-right">
                                                <a href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE%>" type="button" id="commentdelete-<%=responseCommentIndex %>" class="btn btn-default btn-xs icon-button" 
                                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_DELETE%>"
                                                    <% if (!data.instructor.email.equals(comment.giverEmail)
                                                            && (!data.instructor.isAllowedForPrivilege(singleResponse.giverSection,
                                                            singleResponse.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                                            || !data.instructor.isAllowedForPrivilege(singleResponse.recipientSection,
                                                                    singleResponse.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS))) { %>
                                                            disabled="disabled"
                                                    <% } %>
                                                    > 
                                                    <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                                </a>
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID%>" value="<%=comment.feedbackResponseId%>">
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%=comment.getId()%>">
                                                <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                                            </form>
                                            <a type="button" id="commentedit-<%=responseCommentIndex %>" class="btn btn-default btn-xs icon-button pull-right" 
                                                onclick="showResponseCommentEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>,<%=responseCommentIndex%>)"
                                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_EDIT%>"
                                                <% if (!data.instructor.email.equals(comment.giverEmail)
                                                        && (!data.instructor.isAllowedForPrivilege(singleResponse.giverSection,
                                                            singleResponse.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                                            || !data.instructor.isAllowedForPrivilege(singleResponse.recipientSection,
                                                                    singleResponse.feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS))) { %>
                                                            disabled="disabled"
                                                <% } %>
                                                >
                                                <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                            </a>
                                            </div>
                                            <!-- frComment Content -->
                                            <div id="plainCommentText-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"><%=comment.commentText.getValue() %></div>
                                            <!-- frComment Edit Form -->
                                            <form style="display:none;" id="responseCommentEditForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>" class="responseCommentEditForm">
                                                <div class="form-group">
                                                    <div class="form-group form-inline">
                                                        <div class="form-group text-muted">
                                                            You may change comment's visibility using the visibility options on the right hand side.
                                                        </div>
                                                        <a id="frComment-visibility-options-trigger-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"
                                                            class="btn btn-sm btn-info pull-right" onclick="toggleVisibilityEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>,<%=responseCommentIndex%>)">
                                                            <span class="glyphicon glyphicon-eye-close"></span>
                                                            Show Visibility Options
                                                        </a>
                                                    </div>
                                                    <div id="visibility-options-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>" class="panel panel-default"
                                                        style="display: none;">
                                                        <div class="panel-heading">Visibility Options</div>
                                                        <table class="table text-center" style="color:#000;"
                                                            style="background: #fff;">
                                                            <tbody>
                                                                <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see
                                                                        your comment</th>
                                                                    <th class="text-center">Can see
                                                                        your name</th>
                                                                </tr>
                                                                <tr id="response-giver-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what response giver can view">
                                                                            Response Giver</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox centered"
                                                                        name="receiverLeaderCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.GIVER%>"
                                                                        <%=data.isResponseCommentVisibleTo(comment, question, FeedbackParticipantType.GIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.GIVER%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(comment, question, FeedbackParticipantType.GIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% if(question.recipientType != FeedbackParticipantType.SELF
                                                                        && question.recipientType != FeedbackParticipantType.NONE
                                                                        && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)){ %>
                                                                <tr id="response-recipient-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what response recipient(s) can view">
                                                                            Response Recipient(s)</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox centered"
                                                                        name="receiverLeaderCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>"
                                                                        <%=data.isResponseCommentVisibleTo(comment, question, FeedbackParticipantType.RECEIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(comment, question, FeedbackParticipantType.RECEIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.giverType != FeedbackParticipantType.INSTRUCTORS
                                                                        && question.giverType != FeedbackParticipantType.SELF
                                                                        && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)){ %>
                                                                <tr id="response-giver-team-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what team members of response giver can view">
                                                                            Response Giver's Team Members</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentVisibleTo(comment, question, FeedbackParticipantType.OWN_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(comment, question, FeedbackParticipantType.OWN_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.recipientType != FeedbackParticipantType.INSTRUCTORS
                                                                        && question.recipientType != FeedbackParticipantType.SELF
                                                                        && question.recipientType != FeedbackParticipantType.NONE
                                                                        && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)){ %>
                                                                <tr id="response-recipient-team-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what team members of response recipient(s) can view">
                                                                            Response Recipient's Team Members</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentVisibleTo(comment, question, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(comment, question, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)){ %>
                                                                <tr id="response-students-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what other students in this course can view">
                                                                            Other students in this course</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"
                                                                        <%=data.isResponseCommentVisibleTo(comment, question, FeedbackParticipantType.STUDENTS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(comment, question, FeedbackParticipantType.STUDENTS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)){ %>
                                                                <tr id="response-instructors-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what instructors can view">
                                                                            Instructors</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>"
                                                                        <%=data.isResponseCommentVisibleTo(comment, question, FeedbackParticipantType.INSTRUCTORS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(comment, question, FeedbackParticipantType.INSTRUCTORS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                            </tbody>
                                                        </table>
                                                    </div>
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
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID%>" value="<%=comment.feedbackResponseId%>">
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%=comment.getId()%>">
                                                <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
                                                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                                                <input
                                                    type="hidden"
                                                    name="<%=Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO%>"
                                                    value="<%=data.getResponseCommentVisibilityString(comment, question)%>">
                                                <input
                                                    type="hidden"
                                                    name="<%=Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO%>"
                                                    value="<%=data.getResponseCommentGiverNameVisibilityString(comment, question)%>">
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
                                                    <div class="form-group form-inline">
                                                        <div class="form-group text-muted">
                                                            You may change comment's visibility using the visibility options on the right hand side.
                                                        </div>
                                                        <a id="frComment-visibility-options-trigger-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>"
                                                            class="btn btn-sm btn-info pull-right" onclick="toggleVisibilityEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>)">
                                                            <span class="glyphicon glyphicon-eye-close"></span>
                                                            Show Visibility Options
                                                        </a>
                                                    </div>
                                                    <div id="visibility-options-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>" class="panel panel-default"
                                                        style="display: none;">
                                                        <div class="panel-heading">Visibility Options</div>
                                                        <table class="table text-center" style="color:#000;"
                                                            style="background: #fff;">
                                                            <tbody>
                                                                <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see
                                                                        your comment</th>
                                                                    <th class="text-center">Can see
                                                                        your name</th>
                                                                </tr>
                                                                <tr id="response-giver-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what response giver can view">
                                                                            Response Giver</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox centered"
                                                                        name="receiverLeaderCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.GIVER%>"
                                                                        <%=data.isResponseCommentVisibleTo(question, FeedbackParticipantType.GIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.GIVER%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo( question, FeedbackParticipantType.GIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% if(question.recipientType != FeedbackParticipantType.SELF
                                                                        && question.recipientType != FeedbackParticipantType.NONE
                                                                        && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)){ %>
                                                                <tr id="response-recipient-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what response recipient(s) can view">
                                                                            Response Recipient(s)</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox centered"
                                                                        name="receiverLeaderCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>"
                                                                        <%=data.isResponseCommentVisibleTo(question, FeedbackParticipantType.RECEIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.RECEIVER%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(question, FeedbackParticipantType.RECEIVER)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.giverType != FeedbackParticipantType.INSTRUCTORS
                                                                        && question.giverType != FeedbackParticipantType.SELF
                                                                        && question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)){ %>
                                                                <tr id="response-giver-team-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what team members of response giver can view">
                                                                            Response Giver's Team Members</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentVisibleTo(question, FeedbackParticipantType.OWN_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.OWN_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(question, FeedbackParticipantType.OWN_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.recipientType != FeedbackParticipantType.INSTRUCTORS
                                                                        && question.recipientType != FeedbackParticipantType.SELF
                                                                        && question.recipientType != FeedbackParticipantType.NONE
                                                                        && question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)){ %>
                                                                <tr id="response-recipient-team-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what team members of response recipient(s) can view">
                                                                            Response Recipient's Team Members</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentVisibleTo(question, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox"
                                                                        value="<%=FeedbackParticipantType.RECEIVER_TEAM_MEMBERS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(question, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)){ %>
                                                                <tr id="response-students-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what other students in this course can view">
                                                                            Other students in this course</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"
                                                                        <%=data.isResponseCommentVisibleTo(question, FeedbackParticipantType.STUDENTS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.STUDENTS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(question, FeedbackParticipantType.STUDENTS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                                <% if(question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)){ %>
                                                                <tr id="response-instructors-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
                                                                    <td class="text-left">
                                                                        <div data-toggle="tooltip"
                                                                            data-placement="top" title=""
                                                                            data-original-title="Control what instructors can view">
                                                                            Instructors</div>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox answerCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>"
                                                                        <%=data.isResponseCommentVisibleTo(question, FeedbackParticipantType.INSTRUCTORS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                    <td><input
                                                                        class="visibilityCheckbox giverCheckbox"
                                                                        type="checkbox" value="<%=FeedbackParticipantType.INSTRUCTORS%>"
                                                                        <%=data.isResponseCommentGiverNameVisibleTo(question, FeedbackParticipantType.INSTRUCTORS)?"checked=\"checked\"":""%>>
                                                                    </td>
                                                                </tr>
                                                                <% } %>
                                                            </tbody>
                                                        </table>
                                                    </div>
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
                                                    <input
                                                        type="hidden"
                                                        name="<%=Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO%>"
                                                        value="<%=data.getResponseCommentVisibilityString(question)%>">
                                                    <input
                                                        type="hidden"
                                                        name="<%=Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO%>"
                                                        value="<%=data.getResponseCommentGiverNameVisibilityString(question)%>">
                                                </div>
                                            </form>
                                        </li>
                                    </ul></div></div>
                            <%
                                    qnIndx++;
                                }
                                if (responsesForRecipientFromGiver.getValue().isEmpty()) {
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

            // print out the "missing response" rows for the last team
            Set<String> teamMembersWithoutReceivingResponses = teamMembersEmail;
            teamMembersWithoutReceivingResponses.removeAll(teamMembersWithResponses);
            
            List<String> teamMembersWithNoResponses = new ArrayList<String>(teamMembersWithoutReceivingResponses);
            Collections.sort(teamMembersWithNoResponses);
            for (String email : teamMembersWithNoResponses) {
        %>
                <div class="panel panel-primary">
                <div class="panel-heading">
                    To: 
                    <%  if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, email).isEmpty()) { %>
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
                    Set<String> teamsWithNoResponseReceived = teamsInSection;
                    teamsWithNoResponseReceived.removeAll(receivingTeams);
                    
                    
                        List<String> teamsWithNoResponseReceivedList = new ArrayList<String>(teamsWithNoResponseReceived);
                        Collections.sort(teamsWithNoResponseReceivedList);
                        for (String teamWithNoResponseReceived: teamsWithNoResponseReceivedList) {
                            if (groupByTeamEnabled) {
        %>
                        <div class="panel panel-warning">
                            <div class="panel-heading panel-heading-text">
                                <strong> <%=teamWithNoResponseReceived%></strong>
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
                                                        <div class="inline panel-heading-text">
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
                                <div class="panel-heading panel-heading-text">
                                    <strong> <%=sectionWithNoResponseReceived%></strong>
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
                                                  <div class="panel-heading panel-heading-text">
                                                      <strong> <%=team%></strong>
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