<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResponseStatus" %>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
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
    <script type="text/javascript" src="/js/feedbackResponseComments.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByGRQ.js"></script>
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
                                        <div class="col-sm-9">
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
                                        <div class="col-sm-9">
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
                                <div class="col-sm-9">
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
            <%      }
                } else {
            %>

            <%
                String currentTeam = null;
                boolean newTeam = false;
                String currentSection = null;
                boolean newSection = false;
                int sectionIndex = -1;
                int teamIndex = -1;
            %>
            <%
                Map<String, Map<String, List<FeedbackResponseAttributes>>> allResponses = data.bundle.getResponsesSortedByGiver(groupByTeamEnabled);
                Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;

                int giverIndex = data.startIndex;
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

            <% if(currentSection == null || newSection == true){
                    currentSection = firstResponse.giverSection;
                    newSection = false;
                    sectionIndex++;
            %>
                    <div class="panel panel-success">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-sm-9">
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
                if(groupByTeamEnabled == true && (currentTeam==null || newTeam==true)) {
                    currentTeam = data.bundle.getTeamNameForEmail(targetEmail);
                    if(currentTeam.equals("")){
                        currentTeam = data.bundle.getNameForEmail(targetEmail);
                    }
                    teamIndex++;
                    newTeam = false;
            %>
                    <div class="panel panel-warning">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-sm-9">
                                    <strong><%=currentTeam%></strong>                     
                                </div>
                                <div class="col-sm-3">
                                    <div class="pull-right">
                                        <a class="btn btn-warning btn-xs" id="collapse-panels-button-team-<%=teamIndex%>" data-toggle="tooltip" title="Collapse or expand all student panels. You can also click on the panel heading to toggle each one individually.">
                                            <%= shouldCollapsed ? "Expand " : "Collapse " %> Students
                                        </a>
                                        &nbsp;
                                        <span class='glyphicon <%= !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %>'></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class='panel-collapse collapse <%= shouldCollapsed ? "" : "in"%>'>
                        <div class="panel-body background-color-warning">
                        
            <%
                }
            %>

            <div class="panel panel-primary">
                <div class="panel-heading">
                    From: 
                    <% if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, targetEmail).isEmpty()) { %>
                        <div class="middlealign profile-pic-icon-hover inline" data-link="<%=data.getProfilePictureLink(targetEmail)%>">
                            <strong><%=responsesFromGiver.getKey()%></strong>
                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                            <a class="link-in-dark-bg" href="mailTo:<%= targetEmail%> " <%=mailtoStyleAttr%>>[<%=targetEmailDisplay%>]</a>
                        </div>
                    <% } else {%>
                        <strong><%=responsesFromGiver.getKey()%></strong>
                    <% } %>
			 <span class='glyphicon <%= !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %> pull-right'></span>
               </div>
                <div class='panel-collapse collapse <%= shouldCollapsed ? "" : "in"%>'>
                <div class="panel-body">
                <%
                    int recipientIndex = 0;
                    for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesFromGiverToRecipient : responsesFromGiver.getValue().entrySet()) {
                        recipientIndex++;
                        String recipientEmail = responsesFromGiverToRecipient.getValue().get(0).recipientEmail;
                %>
                    <div class="row <%=recipientIndex == 1? "": "border-top-gray"%>">
                            <div class="col-md-2">
                                <div class="col-md-12">
                                    To:
                                    <% if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, recipientEmail).isEmpty()) { %>
                                        <div class="middlealign profile-pic-icon-hover inline-block" data-link="<%=data.getProfilePictureLink(recipientEmail)%>">
                                            <strong><%=responsesFromGiverToRecipient.getKey()%></strong>
                                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                        </div>
                                    <% } else {%>
                                        <strong><%=responsesFromGiverToRecipient.getKey()%></strong>
                                    <% } %> 
                                </div>
                                <div class="col-md-12 text-muted small"><br>
                                    From:
                                    <% if (validator.getInvalidityInfo(FieldValidator.FieldType.EMAIL, targetEmail).isEmpty()) { %>
                                        <div class="middlealign profile-pic-icon-hover inline-block" data-link="<%=data.getProfilePictureLink(targetEmail)%>">
                                            <%=responsesFromGiver.getKey()%>
                                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                        </div>
                                    <% } else {%>
                                            <%=responsesFromGiver.getKey()%>
                                    <% } %>
                                </div>
                            </div>
                            <div class="col-md-10">
                    <%
                        int qnIndx = 1;
                        for (FeedbackResponseAttributes singleResponse : responsesFromGiverToRecipient.getValue()) {
                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                            FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
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
                                            <div id="plainCommentText-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"><%=comment.commentText.getValue()%></div>
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

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>
