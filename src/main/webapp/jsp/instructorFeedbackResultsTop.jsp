<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
    InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
%>

<!--TODO: move to a better place-->
<script type="text/javascript" src="/js/instructorFeedbackResults.js"></script>
<!---->

<div class="well well-plain">
    <form class="form-horizontal" role="form">
        <div class="panel-heading">
          <div class="form-group">
            <label class="col-sm-2 control-label">Course:</label>
            <div class="col-sm-10">
              <p class="form-control-static"><%=InstructorFeedbackResultsPageData.sanitizeForHtml(data.bundle.feedbackSession.courseId)%></p>
            </div>
          </div>
          <div class="form-group">
            <label class="col-sm-2 control-label">Session:</label>
            <div class="col-sm-10">
              <p class="form-control-static"><%=InstructorFeedbackResultsPageData.sanitizeForHtml(data.bundle.feedbackSession.feedbackSessionName)%> <a
            href="<%=data.getInstructorFeedbackSessionEditLink(data.bundle.feedbackSession.courseId, data.bundle.feedbackSession.feedbackSessionName)%>">[Edit]</a></p>
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
              <p class="form-control-static">
                <%=data.getResultsVisibleFromText()%>
                <%boolean noResponses = data.bundle.responses.isEmpty();%>
                </p>
            </div>
          </div>
        </div>
      </form>
    <div class="col-sm-offset-5">
        <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>">
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

<form method="post" action="/page/instructorFeedbackResultsPage" class="form-horizontal" role="form">
    <div class="panel panel-info">
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-5">
                    <div class="form-group">
                        <label for="viewSelect" class="col-sm-2 control-label">
                            View:
                        </label>
                        <div class="col-sm-10">
                            <select id="viewSelect" class="form-control" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" onchange="this.form.submit()">
                                <option value="table" <%=(data.sortType!=null) ? data.sortType.equals("table") ? "selected=\"selected\"" : "" : ""%>>
                                    By - Question
                                </option>
                                <option value="giver" <%=(data.sortType!=null) ? data.sortType.equals("giver") ? "selected=\"selected\"" : "" : ""%>>
                                    Group - Giver > Recipient > Question
                                </option>
                                <option value="recipient" <%=(data.sortType!=null) ? data.sortType.equals("recipient") ? "selected=\"selected\"" : "" : "selected=\"selected\""%>>
                                    Group - Recipient > Giver > Question
                                </option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3 pull-right">
                    <div class="checkbox pull-right">
                        <label>
                            <input type="checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" onchange="this.form.submit()" <%=(data.groupByTeam==null) ? "" : "checked=\"checked\""%>> Group by Teams
                        </label>
                    </div>
                </div>
            </div>
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

<div class="row">
    <div class="col-sm-12">
        <button class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse()">
            Collapse All
        </button>
    </div>
</div>

<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

<% if (noResponses) { %>
    <div class="bold color_red align-center">There are no responses for this feedback session yet.</div>
<% } %>