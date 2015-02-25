<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResponseStatus"%>
<%
    InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
    boolean showAll = data.bundle.isComplete;
    boolean shouldCollapsed = data.bundle.responses.size() > 500;
%>

<% if(data.selectedSection.equals("All")){ %>
  <div class="panel panel-warning">
      <div class="panel-heading<%= showAll ? "" : " ajax_response_rate_submit"%>">
          <form style="display:none;" id="responseRate" class="responseRateForm" 
                action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
              <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" 
                      value="<%=data.bundle.feedbackSession.courseId%>">
              <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" 
                      value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
              <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" 
                      value="<%=data.account.googleId%>">
              <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" 
                      value="-1">
          </form>
          <div class='display-icon pull-right'>
          <span class="glyphicon <%= showAll ? "glyphicon-chevron-up" : "glyphicon-chevron-down" %> pull-right"></span>
          </div>
          Participants who have not responded to any question</div>
      <div class="panel-collapse collapse <%= showAll ? "in" : "" %>" id="responseStatus">
  <% if(showAll) {
      // Only output the list of students who haven't responded when there are responses.
      FeedbackSessionResponseStatus responseStatus = data.bundle.responseStatus;
      if (data.selectedSection.equals("All") && !responseStatus.noResponse.isEmpty()) {
  %>          
          <div class="panel-body padding-0">
              <table class="table table-striped table-bordered margin-0">
                  <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                      <tr>
                      <th id="button_sortFromTeam" class="button-sort-ascending"
                          onclick="toggleSort(this,1)" style="width: 15%;">Team<span
                          class="icon-sort unsorted"></span>
                      </th>
                      <th id="button_sortTo" class="button-sort-none"
                          onclick="toggleSort(this,2)" style="width: 15%;">Name<span 
                          class="icon-sort unsorted"></span>
                      </th>                            
                      </tr>
                  </thead>
                  <tbody>
                  <% 
                      List<String> students = responseStatus.getStudentsWhoDidNotRespondToAnyQuestion();
                      for (String studentEmail : students) {
                          String studentName = responseStatus.emailNameTable.get(studentEmail);
                          if(studentName == null){
                              // Skip invalid student name
                              continue;
                          }
                  %>
                      <tr>
                          <td>
                          <% String teamName = responseStatus.emailTeamNameTable.get(studentEmail);
                              if(teamName == null){
                                  // Assign empty string to team name
                                  // This is only for instructors, which they do not have a team name
                                  teamName = Const.USER_TEAM_FOR_INSTRUCTOR;
                          %>
                              <i><%=teamName%> </i>
                          <%       
                              }else{
                          %>
                              <%=teamName%>
                          <% 
                              } 
                          %>
                          </td>
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
              All students have responded to some questions in this session.
          </div>
  <%
          }
      } 
  %>
      </div>
      </div>
  <% } %>