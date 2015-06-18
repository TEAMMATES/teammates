<%@ tag description="instructorFeedbackResults - by question" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="java.util.Map"%>
<%@ tag import="java.util.List"%>
<%@ tag import="teammates.common.util.Const"%>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ tag import="teammates.common.datatransfer.FeedbackSessionResponseStatus"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ tag import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ tag import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="data" type="teammates.ui.controller.InstructorFeedbackResultsPageData" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="questionIndx" type="java.lang.Integer" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="questionPanel" type="teammates.ui.template.InstructorResultsQuestionTable" required="true" %>


<div class="panel ${questionPanel.className}">
    <div class="panel-heading<%=showAll ? "" : " ajax_submit"%>">
        <form style="display:none;" id="seeMore-${questionPanel.question.questionNumber}" class="seeMoreForm-${questionPanel.question.questionNumber}" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="<%=data.groupByTeam%>">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="<%=data.sortType%>">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on" id="showStats-${questionPanel.question.questionNumber}">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" value="${questionPanel.question.questionNumber}">
        </form>
        <div class='display-icon pull-right'>
        <span class="glyphicon <%= showAll && !shouldCollapsed ? "glyphicon-chevron-up" : "glyphicon-chevron-down"%> pull-right"></span>
        </div>
        <strong>Question ${questionPanel.question.questionNumber}: </strong>
        <div class="inline panel-heading-text">
            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
            <span class="text-preserve-space">${questionPanel.questionText}${questionPanel.additionalInfoText}
            </span>
        </div>
    </div>
    <div class="panel-collapse collapse <%= showAll && !shouldCollapsed ? "in" : "" %>">
    <div class="panel-body padding-0" id="questionBody-${questionIndex}">
        
        <c:if test="${questionPanel.questionHasResponses}">
            <div class="col-sm-12">
                <i class="text-muted">There are no responses for this question.</i>
            </div>
        </c:if>
        
        <c:if test="${showAll && !questionPanel.quesitonHasResponses}">
        <div class="resultStatistics">
            <%=questionDetails.getQuestionResultStatisticsHtml(responseEntries.getValue(), question, data, data.bundle, "question")%>
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
                        <th>
                            Actions
                        </th>
                    </tr>
                <thead>
                <tbody>
                    <%
                        if (responseEntries.getValue().size() > 0) {
                            
                          List<String> possibleGivers = data.bundle.getPossibleGivers(question);
                          
                          List<String> possibleReceivers = null;
                          boolean isNewGiver = true;
                          String prevGiver = "";
                        for(FeedbackResponseAttributes responseEntry: responseEntries.getValue()) {
                             if (!prevGiver.isEmpty() && !prevGiver.equals(responseEntry.giverEmail)) {
                                isNewGiver = true;   
                             }
                             
                          
                    %>
                                
                    <%
                                    String giverName = data.bundle.getGiverNameForResponse(question, responseEntry);
                                    String giverTeamName = data.bundle.getTeamNameForEmail(responseEntry.giverEmail);

                                    String recipientName = data.bundle.getRecipientNameForResponse(question, responseEntry);
                                    String recipientTeamName = data.bundle.getTeamNameForEmail(responseEntry.recipientEmail);
                                    
                                    Boolean isGiverVisible = data.bundle.isGiverVisible(responseEntry);

                                    if (!data.bundle.isGiverVisible(responseEntry) || !data.bundle.isRecipientVisible(responseEntry)) {
                                      possibleGivers.clear();
                                      if (possibleReceivers != null) {
                                        possibleReceivers.clear();
                                      }
                                    }
                                    
                                    if (isNewGiver) {
                                       
                                      if (possibleReceivers != null && !possibleReceivers.isEmpty()) {
                                         for (String possibleReceiver : possibleReceivers) {
                                             
                                             if (questionDetails.shouldShowNoResponseText(prevGiver, possibleReceiver, question)) {
                    %>
                                                <tr class="pending_response_row">
                                                    
                                                    <td class="middlealign color_neutral">
                                                        <%if (question.isGiverAStudent()) {%>
                                                        <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(prevGiver)%>">
                                                            <%=data.bundle.getFullNameFromRoster(prevGiver)%>
                                                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                        </div>                                
                                                        <%} else {%>                                                                    
                                                        <%=data.bundle.getFullNameFromRoster(prevGiver)%>                                               
                                                        <%}%>                                   
                                                    </td>                                                                    
                                                    <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(prevGiver)%></td>                                                                
                                                    <td class="middlealign color_neutral">
                                                        <%if (question.isRecipientAStudent()) {%>
                                                        <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(possibleReceiver)%>">
                                                            <%=data.bundle.getFullNameFromRoster(possibleReceiver)%>
                                                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                        </div>      
                                                        <%} else {%>                                                
                                                        <%=data.bundle.getFullNameFromRoster(possibleReceiver)%>
                                                        <%}%>             
                                                    </td>
                                                    <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(possibleReceiver)%></td>
                                                    <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                    <td class="text-preserve-space color_neutral"><%=questionDetails.getNoResponseTextInHtml(prevGiver, possibleReceiver, data.bundle, question)%></td>
                                                    <td>
                                                        <% 
                                                            boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(responseEntry.giverEmail), 
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
                                                            <% if (responseEntry.giverEmail.matches(Const.REGEXP_TEAM)) { %>
                                                            <input type="hidden" name="moderatedstudent" value="<%= responseEntry.giverEmail.replace(Const.TEAM_OF_EMAIL_OWNER,"")%>">
                                                            <% } else { %>
                                                            <input type="hidden" name="moderatedstudent" value="<%= responseEntry.giverEmail%>">
                                                            <% } %>
                                                        </form>
                                                        <% } %>
                                                    </td>
                                                </tr>
                    <%
                                                }
                                          }
                                      }
                                                
                                      if (question.giverType == FeedbackParticipantType.TEAMS) {
                                         possibleGivers.remove(data.bundle.getFullNameFromRoster(responseEntry.giverEmail));
                                         possibleReceivers = data.bundle.getPossibleRecipients(question, data.bundle.getFullNameFromRoster(responseEntry.giverEmail));
                                      } else {
                                         possibleGivers.remove(responseEntry.giverEmail);
                                         possibleReceivers = data.bundle.getPossibleRecipients(question, responseEntry.giverEmail);
                                      }
                                                
                                                
                                       isNewGiver = false;
                                    }
                    %>
                                <tr>
                                    <td class="middlealign">
                                        <%if (question.isGiverAStudent()) {%>
                                        <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(responseEntry.giverEmail)%>">
                                            <%=giverName%>
                                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                        </div>             
                                        <%} else {%>   
                                        <%=giverName%> 
                                        <%}%>                                   
                                    </td>
                                    <td class="middlealign"><%=giverTeamName%></td>
                                    <td class="middlealign">
                                        <%if (question.isRecipientAStudent()) {%>
                                        <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(responseEntry.recipientEmail)%>">
                                            <%=recipientName%>
                                            <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                        </div>   
                                        <%} else {%> 
                                        <%=recipientName%> 
                                        <%}%>                                                   
                                    </td>
                                    <td class="middlealign"><%=recipientTeamName%></td>
                                    <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                    <td class="text-preserve-space"><%=data.bundle.getResponseAnswerHtml(responseEntry, question)%></td>
                                    <td>
                                        <% 
                                            boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(responseEntry.giverEmail), 
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
                                            <% if (responseEntry.giverEmail.matches(Const.REGEXP_TEAM)) { %>
                                            <input type="hidden" name="moderatedstudent" value="<%= responseEntry.giverEmail.replace(Const.TEAM_OF_EMAIL_OWNER,"")%>">
                                            <% } else { %>
                                            <input type="hidden" name="moderatedstudent" value="<%= responseEntry.giverEmail%>">
                                            <% } %>
                                        </form>
                                        <% } %>
                                    </td>
                                </tr>        
                    <%
                                if (question.recipientType == FeedbackParticipantType.TEAMS) {
                                    possibleReceivers.remove(data.bundle.getFullNameFromRoster(responseEntry.recipientEmail)); 
                                } else {
                                    possibleReceivers.remove(responseEntry.recipientEmail);
                                }
                                prevGiver = responseEntry.giverEmail;
                        }
                                                                                                          
                                if (possibleReceivers != null && !possibleReceivers.isEmpty()) {
                                    // print missing responses to possible recipient from the last giver
                                    for (String possibleReceiver : possibleReceivers) {
                                        if (!data.selectedSection.equals("All") && !data.bundle.getSectionFromRoster(possibleReceiver).equals(data.selectedSection)) {
                                           continue;
                                        }
                                    
                                        if (questionDetails.shouldShowNoResponseText(prevGiver, possibleReceiver, question)) {
                    %>
                                            <tr class="pending_response_row">
                                                <td class="middlealign color_neutral">
                                                    <%if (question.isGiverAStudent()) {%>
                                                    <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(prevGiver)%>">
                                                        <%=data.bundle.getFullNameFromRoster(prevGiver)%>
                                                        <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                    </div>       
                                                    <%} else {%>
                                                    <%=data.bundle.getFullNameFromRoster(prevGiver)%>
                                                    <%}%>                                                            
                                                </td>
                                                <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(prevGiver)%></td>
                                                <td class="middlealign color_neutral">
                                                    <%if (question.isRecipientAStudent()) {%>
                                                    <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(possibleReceiver)%>">
                                                        <%=data.bundle.getFullNameFromRoster(possibleReceiver)%>
                                                        <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                    </div>  
                                                    <%} else {%>
                                                    <%=data.bundle.getFullNameFromRoster(possibleReceiver)%>
                                                    <%}%>                                                                 
                                                </td>
                                                <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(possibleReceiver)%></td>
                                                <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                <td class="text-preserve-space color_neutral"><%=questionDetails.getNoResponseTextInHtml(prevGiver, possibleReceiver, data.bundle, question)%></td>
                                                <td>
                                                    <% 
                                                        boolean isAllowedToModerate = data.instructor.isAllowedForPrivilege(data.bundle.getSectionFromRoster(prevGiver), 
                                                                                                                        data.feedbackSessionName, 
                                                                                                                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
                                                        String disabledAttribute = (!isAllowedToModerate) ? "disabled=\"disabled\"" : "";
                                                    %>
                                                    <form class="inline" method="post" action="<%=data.getInstructorEditStudentFeedbackLink() %>" target="_blank"> 
                                                        <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" <%=disabledAttribute%> data-toggle="tooltip" title="<%=Const.Tooltips.FEEDBACK_SESSION_MODERATE_FEEDBACK%>">
                                                        <input type="hidden" name="courseid" value="<%=data.courseId %>">
                                                        <input type="hidden" name="fsname" value="<%= data.feedbackSessionName%>">
                                                        <input type="hidden" name="moderatedquestion" value="<%= question.questionNumber%>">
                                                        <% if (prevGiver.matches(Const.REGEXP_TEAM)) { %>
                                                        <input type="hidden" name="moderatedstudent" value="<%= prevGiver.replace(Const.TEAM_OF_EMAIL_OWNER,"")%>">
                                                        <% } else { %>
                                                        <input type="hidden" name="moderatedstudent" value="<%= prevGiver%>">
                                                        <% } %>
                                                    </form>
                                                </td>
                                            </tr>
                    <%
                                        }
                                    }
                                    if (question.giverType == FeedbackParticipantType.TEAMS) {
                                        possibleGivers.remove(data.bundle.getNameForEmail(prevGiver)); 
                                    } else {
                                        possibleGivers.remove(prevGiver);
                                    }
                                        
                                }
                                      
                                if (possibleGivers != null && !possibleGivers.isEmpty()) {
                                    // print remaining possible givers and recipient pairs
                                    for (String possibleGiver : possibleGivers) {
                                    
                                        if (!data.selectedSection.equals("All") && !data.bundle.getSectionFromRoster(possibleGiver).equals(data.selectedSection)) {
                                            continue;
                                        }
                                        possibleReceivers = data.bundle.getPossibleRecipients(question, possibleGiver);
                                        for (String possibleReceiver : possibleReceivers) {
                                            if (!data.selectedSection.equals("All") && !data.bundle.getSectionFromRoster(possibleReceiver).equals(data.selectedSection)) {
                                               continue;
                                            }
                                            if (questionDetails.shouldShowNoResponseText(possibleGiver, possibleReceiver, question)) {
                    %>
                                              <tr class="pending_response_row">
                                                  <td class="middlealign color_neutral">
                                                      <%if (question.isGiverAStudent()) {%>
                                                      <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(possibleGiver)%>">
                                                          <%=data.bundle.getFullNameFromRoster(possibleGiver)%>
                                                          <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                      </div>
                                                      <%} else {%>
                                                      <%=data.bundle.getFullNameFromRoster(possibleGiver)%>
                                                      <%}%>                                                                   
                                                  </td>
                                                  <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(possibleGiver)%></td>
                                                  <td class="middlealign color_neutral">
                                                      <%if (question.isRecipientAStudent()) {%>
                                                      <div class="profile-pic-icon-hover" data-link="<%=data.getProfilePictureLink(possibleReceiver)%>">
                                                          <%=data.bundle.getFullNameFromRoster(possibleReceiver)%>
                                                          <img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                      </div>
                                                      <%} else {%>
                                                      <%=data.bundle.getFullNameFromRoster(possibleReceiver)%>
                                                      <%}%>                                                                   
                                                  </td>
                                                  <td class="middlealign color_neutral"><%=data.bundle.getTeamNameFromRoster(possibleReceiver)%></td>
                                                  <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                  <td class="text-preserve-space color_neutral"><%=questionDetails.getNoResponseTextInHtml(possibleGiver, possibleReceiver, data.bundle, question)%></td>
                                                  <td>
                                                  ${question.giverTypeIsATeam eq true}
                                                    <results:moderationsButton questionNumber="${question.questionNumber}" isGiverTeamGiver="${question.giverTypeIsATeam}" possibleGiver="${possibleGiver}" data="${data}" />
                                                  </td>
                                              </tr>
                    <% 
                                            }
                                        }
                                    }
                                }
                        } 
                    
                    %>
                </tbody>
            </table>
        </div>
        
    </div>
    </div>
</div>