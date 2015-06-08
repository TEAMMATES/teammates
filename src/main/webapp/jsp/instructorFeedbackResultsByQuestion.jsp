<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResponseStatus"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
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
                // map of (questionId > giverEmail > recipientEmail) > response
                Map<String, Map<String, Map<String, FeedbackResponseAttributes>>> responseBundle = data.bundle.getResponseBundle();

            	int questionIndex = -1;
                List<String> questionIds = data.bundle.getQuestionIdsSortedByQuestionNumber();
                for (String questionId : questionIds) {
                    questionIndex++;
                    FeedbackQuestionAttributes question = data.bundle.questions.get(questionId);

                    boolean isQuestionWithResponse = responseBundle.get(questionId) != null && responseBundle.get(questionId).size() > 0;
            %>
                <%
                    if (!isQuestionWithResponse) {
                %>
                        <div class="panel panel-default">
                <%
                    } else {
                %>
                        <div class="panel panel-info">
                <%
                    }
                %>
                    <div class="panel-heading<%=showAll ? "" : " ajax_submit"%>">
                        <form style="display:none;" id="seeMore-<%=question.questionNumber%>" class="seeMoreForm-<%=question.questionNumber%>" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
                            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>">
                            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="<%=data.groupByTeam%>">
                            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="<%=data.sortType%>">
                            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on" id="showStats-<%=question.questionNumber%>">
                            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" value="<%=question.questionNumber%>">
                        </form>
                        <div class='display-icon pull-right'>
                        <span class="glyphicon <%=showAll && !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right"></span>
                        </div>
                        <strong>Question <%=question.questionNumber%>: </strong>
                        <div class="inline panel-heading-text">
                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                            <span class="text-preserve-space"><%=data.bundle.getQuestionText(question.getId())%><%
                            	FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                                    out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, ""));
                            %></span>
                        </div>
                    </div>
                    <div class="panel-collapse collapse <%= showAll && !shouldCollapsed ? "in" : "" %>">
                    <div class="panel-body padding-0" id="questionBody-<%=questionIndex%>">
                    <%
                        if (showAll) {  
                            // display responses
                            if (isQuestionWithResponse) {
                    %>
                                <div class="resultStatistics">
                                    <%=questionDetails.getQuestionResultStatisticsHtml(data.bundle.filterResponsesForQuestion(responseBundle, question.getId()), question, data, data.bundle, "question")%>
                                </div>
                        <%
                            } else {
                        %>    
                                    <div class="col-sm-12">
                                        <i class="text-muted">There are no responses for this question.</i>
                                    </div>
                                </div>
                                </div>
                                </div>
                        <%
                                continue; // skip to next question
                            }
                        %>
                            
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
                                    <th>
                                        Actions
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                            <%

                            List<String> possibleGivers = data.bundle.getPossibleAndActualGivers(question);

                            for (String giver : possibleGivers) {
                                
                                List<String> recipientsForGiver = data.bundle.getPossibleAndActualReceivers(question, giver, responseBundle.get(questionId));
                                for (String recipient : recipientsForGiver) {
                                    if (!data.selectedSection.equals("All") 
                                            && !data.bundle.isParticipantInSection(giver, data.selectedSection) 
                                            && !data.bundle.isParticipantInSection(recipient, data.selectedSection)) {
                                        continue;
                                    }
                                    //initialise parameters used for modifying appearance
                                    String rowCssClass = "";
                                    String cellCssClass = "";

                                    boolean isShowingMissingResponseRow = false; 

                                    String giverName, giverTeamName;
                                    String recipientName, recipientTeamName;
                                    Boolean isGiverVisible = data.bundle.isGiverVisibleToInstructor(question);

                                    String giverDisplayableIdentifier;
                                    String recipientDisplayableIdentifier;

                                    FeedbackResponseAttributes responseForQuestionGiverRecipient = null;
                                    boolean isGiverAnonymous = !data.bundle.isGiverVisibleToInstructor(question);
                                    boolean isCurrentUserResponseGiver = data.instructor.email.equals(giver);
                                    if (isGiverAnonymous && !isCurrentUserResponseGiver) {
                                        FeedbackParticipantType participantType = question.giverType;
                                        giverDisplayableIdentifier = participantType.isTeam() ? 
                                                                     giver:
                                                                     data.bundle.getAnonEmail(participantType, data.bundle.getFullNameFromRoster(giver));
                                    } else {
                                        giverDisplayableIdentifier = giver;
                                    }
                                    boolean isRecipientAnonymous = !data.bundle.isRecipientVisibleToInstructor(question);
                                    if (isRecipientAnonymous && !isCurrentUserResponseGiver) {
                                        FeedbackParticipantType participantType = question.recipientType;
                                        recipientDisplayableIdentifier = participantType.isTeam() ? 
                                                                         recipient:
                                                                         data.bundle.getAnonEmail(participantType, data.bundle.getFullNameFromRoster(recipient));
                                    } else {
                                        recipientDisplayableIdentifier = recipient;
                                    }

                                    // obtain response from responseBundle
                                    boolean isResponseExist = responseBundle.containsKey(questionId) 
                                                              && responseBundle.get(questionId).containsKey(giverDisplayableIdentifier) 
                                                              && responseBundle.get(questionId).get(giverDisplayableIdentifier).
                                                                                           containsKey(recipientDisplayableIdentifier);
                                    if (!isResponseExist) {
                                        // set parameters for displaying 'missing' response row
                                        rowCssClass = "pending_response_row";
                                        cellCssClass = "class=\"middlealign color_neutral\"";

                                        // show 'missing' responses if both giver and recipient are anonymous to instructors in general
                                        if (data.bundle.isBothGiverAndReceiverVisibleToInstructor(question) && questionDetails.shouldShowNoResponseText(giver, recipient, question)) {

                                            isShowingMissingResponseRow = true;

                                            // prepare data for 'missing' response row
                                            giverName = data.bundle.isGiverVisibleToInstructor(question) ? 
                                                        data.bundle.getFullNameFromRoster(giverDisplayableIdentifier) :
                                                        data.bundle.getNameForEmail( giverDisplayableIdentifier);
                                            giverTeamName = data.bundle.isGiverVisibleToInstructor(question) ?
                                                            data.bundle.getTeamNameFromRoster(giverDisplayableIdentifier) :
                                                            data.bundle.getTeamNameForEmail(giverDisplayableIdentifier);

                                            recipientName = data.bundle.isRecipientVisibleToInstructor(question) ? 
                                                            data.bundle.getFullNameFromRoster(recipientDisplayableIdentifier) :
                                                            data.bundle.getNameForEmail(recipientDisplayableIdentifier);
                                            recipientTeamName = data.bundle.isRecipientVisibleToInstructor(question) ? 
                                                                data.bundle.getTeamNameFromRoster(recipientDisplayableIdentifier) :
                                                                data.bundle.getTeamNameForEmail(recipientDisplayableIdentifier);

                                        } else {
                                            // skip to next recipient
                                            // since there is no need to display any row for this (questionId, giver, recipient)
                                            // due to restricted visibility options
                                            continue;
                                        }
                                    } else {
                                        cellCssClass = "class = \"middlealign\"";

                                        // get response and prepare data for displaying response
                                        responseForQuestionGiverRecipient = responseBundle.get(questionId).
                                                                            get(giverDisplayableIdentifier).
                                                                            get(recipientDisplayableIdentifier);

                                        giverName = data.bundle.getGiverNameForResponse(question, responseForQuestionGiverRecipient);
                                        giverTeamName = data.bundle.getTeamNameForEmail(responseForQuestionGiverRecipient.giverEmail);

                                        recipientName = data.bundle.getRecipientNameForResponse(question, responseForQuestionGiverRecipient);
                                        recipientTeamName = data.bundle.getTeamNameForEmail(responseForQuestionGiverRecipient.recipientEmail);
                                    }
        

                                    // display contents of 1 response
                            %>
                                    <tr <%= rowCssClass.isEmpty() ? "" : "class=\"pending_response_row\"" %> >
                                        <td <%= cellCssClass %> >
                                            <%if (question.isGiverAStudent()) {%>
                                            <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(giverDisplayableIdentifier)%>">
                                                <%=giverName%>
                                                <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                            </div>             
                                            <%} else {%>   
                                            <%=giverName%> 
                                            <%}%>                                   
                                        </td>
                                        <td <%= cellCssClass %>><%=giverTeamName%></td>
                                        <td <%= cellCssClass %>>
                                            <%if (question.isRecipientAStudent()) {%>
                                            <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(recipientDisplayableIdentifier)%>">
                                                <%=recipientName%>
                                                <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                            </div>   
                                            <%} else {%> 
                                            <%=recipientName%> 
                                            <%}%>                                                   
                                        </td>
                                        <td <%= cellCssClass %>><%=recipientTeamName%></td>
                                        <%
                                            if (isShowingMissingResponseRow) {
                                        %>
                                                <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                <td class="text-preserve-space color_neutral"><%=questionDetails.getNoResponseTextInHtml(giverDisplayableIdentifier, recipientDisplayableIdentifier, data.bundle, question)%></td>
                                        <%
                                            }  else {
                                        %>
                                                <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                <td class="text-preserve-space"><%=data.bundle.getResponseAnswerHtml(responseForQuestionGiverRecipient, question)%></td>
                                        <%
                                            }

                                        %>
                                        <td>
                                            <% 
                                                // determine if 'moderate' button should be shown
                                                boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(
                                                                                                data.bundle.getSectionFromRoster(
                                                                                                                giverDisplayableIdentifier), 
                                                                                                data.feedbackSessionName, 
                                                                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                                                String disabledAttribute = (!isAllowedToModerate) ? "disabled=\"disabled\"" : "";
                                                if (isGiverVisible) {
                                            %>
                                                    <form class="inline" method="post" action="<%=data.getInstructorEditStudentFeedbackLink() %>" target="_blank"> 
                                                        <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" <%=disabledAttribute%> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
                                                        <input type="hidden" name="courseid" value="<%=data.courseId %>">
                                                        <input type="hidden" name="fsname" value="<%= data.feedbackSessionName%>">
                                                        <input type="hidden" name="moderatedquestion" value="<%= question.questionNumber%>">
                                                        <% if (giverDisplayableIdentifier.matches(Const.REGEXP_TEAM)) { %>
                                                               <input type="hidden" name="moderatedstudent" value="<%= giverDisplayableIdentifier.replace(Const.TEAM_OF_EMAIL_OWNER,"")%>">
                                                        <% } else { %>
                                                               <input type="hidden" name="moderatedstudent" value="<%= giverDisplayableIdentifier%>">
                                                        <% } %>
                                                    </form>
                                            <%  }  %>
                                        </td>
                                    </tr>
                            <%
                                    //end of displaying of 1 response
                                }  // end of recipient loop
                            } // end of giver loop
                            %>
                                </tbody>
                            </table>
                        </div>
                    <%
                        } // end of display responses
                    %>
                </div>
                </div>
            </div>
            <%
                }
            %>
            <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BOTTOM%>" />
            </div>    
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>