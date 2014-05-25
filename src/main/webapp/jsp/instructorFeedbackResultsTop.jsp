<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
    InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
%>

<div class="well well-plain">
    <form class="form-horizontal" role="form">
        <div class="panel-heading">
          <div class="form-group">
            <label class="col-sm-2 control-label">Course:</label>
            <div class="col-sm-10">
              <p class="form-control-static"><%=InstructorFeedbackResultsPageData.sanitizeForHtml(data.bundle.feedbackSession.courseId)%> <a
            href="<%=data.getInstructorFeedbackSessionEditLink(data.bundle.feedbackSession.courseId, data.bundle.feedbackSession.feedbackSessionName)%>">[Edit]</a></p>
            </div>
          </div>
          <div class="form-group">
            <label class="col-sm-2 control-label">Session:</label>
            <div class="col-sm-10">
              <p class="form-control-static"><%=InstructorFeedbackResultsPageData.sanitizeForHtml(data.bundle.feedbackSession.feedbackSessionName)%></p>
            </div>
          </div>
          <div class="form-group">
            <label class="col-sm-2 control-label">Opening time:</label>
            <div class="col-sm-10">
              <p class="form-control-static"><%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%></p>
            </div>
          </div>
          <div class="form-group">
            <label class="col-sm-2 control-label">Closing time:</label>
            <div class="col-sm-10">
              <p class="form-control-static"><%=TimeHelper.formatTime(data.bundle.feedbackSession.endTime)%></p>
            </div>
          </div>
          <div class="form-group">
            <label class="col-sm-2 control-label">Results visible from:</label>
            <div class="col-sm-10">
              <p class="form-control-static"><%
                if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
                                if (data.bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)) {
            %>
                        <%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%>
                    <%
                        } else if (data.bundle.feedbackSession.sessionVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
                    %>
                        Never
                    <%
                        } else {
                    %>
                        <%=TimeHelper.formatTime(data.bundle.feedbackSession.sessionVisibleFromTime)%>
                    <%
                        }
                    %>
            <%
                } else if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_LATER)) {
            %>
                I want to manually publish the results.
            <%
                } else if (data.bundle.feedbackSession.resultsVisibleFromTime.equals(Const.TIME_REPRESENTS_NEVER)) {
            %>
                Never
            <%
                } else {
            %>
                <%=TimeHelper.formatTime(data.bundle.feedbackSession.resultsVisibleFromTime)%>
            <%
                } 
                        boolean noResponses = data.bundle.responses.isEmpty();
            %></p>
            </div>
          </div>
        </div>
      </form>
    <div class="col-sm-offset-5">
            <form method="post"
                      action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>">
            <div id="feedbackDataButtons">
            <input id="button_download" type="submit" class="btn btn-primary"
                    name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                    value="Download results">
            </div>
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
            <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>">
            </form>
    </div>
</div>

<%
    if (noResponses == false) {
%>
<form method="post"
    action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
    <div class="panel panel-info">
        <div class="panel-body">
        <table class="col-md-offset-2">
        <tr>
            <td class="col-sm-5"><input type="radio"
                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="giver"
                onclick="this.form.submit()"
                <%=(data.sortType!=null) ? data.sortType.equals("giver") ? "checked=\"checked\"" : "" : ""%>><span
                > Sort by giver</span></td>
            <td class="col-sm-5"><input type="radio"
                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="recipient"
                onclick="this.form.submit()"
                <%=(data.sortType!=null) ? data.sortType.equals("recipient") ? "checked=\"checked\"" : "" : "checked=\"checked\""%>><span
                > Sort by recipient</span></td>
            <td><input type="radio"
                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="table"
                onclick="this.form.submit()"
                <%=(data.sortType!=null) ? data.sortType.equals("table") ? "checked=\"checked\"" : "" : ""%>><span
                > View as table</span></td>
        </tr>
        </table>
        </div>
    </div>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
        value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
        value="<%=data.bundle.feedbackSession.courseId%>">
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" 
        value="<%=data.account.googleId%>">
</form>
<%
    }
%>
<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

<% if (noResponses) { %>
    <div class="bold color_red centeralign">There are no responses for this feedback session yet.</div>
<% } %>