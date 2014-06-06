<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%
    InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData)request.getAttribute("data");
%>

<div class="well well-plain padding-0">
    <form class="form-horizontal" role="form" method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>">
        <div class="panel-heading">
          <div class="row">
          <div class="col-sm-4">
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
          </div>
          <div class="col-sm-6">
              <div class="form-group">
                <label class="col-sm-4 control-label">Session open:</label>
                <div class="col-sm-8">
                  <p class="form-control-static"><%=TimeHelper.formatTime(data.bundle.feedbackSession.startTime)%>&nbsp;&nbsp;&nbsp;<b>to</b>&nbsp;&nbsp;&nbsp;<%=TimeHelper.formatTime(data.bundle.feedbackSession.endTime)%></p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-4 control-label">Results visible from:</label>
                <div class="col-sm-8">
                  <p class="form-control-static">
                    <%=data.getResultsVisibleFromText()%>
                    <%boolean noResponses = data.bundle.responses.isEmpty();%>
                    </p>
                </div>
              </div>
          </div>
          <div class="col-sm-2">
              <div id="feedbackDataButtons">
                  <input id="button_download" type="submit" class="btn btn-primary pull-right"
                      name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                      value="Download results">
              </div>
              <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
              <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="<%=data.bundle.feedbackSession.feedbackSessionName%>">
              <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.bundle.feedbackSession.courseId%>">
          </div>
        </div>
        </div>
      </form>
    
</div>

<%
    if (noResponses == false) {
%>

<form method="post" action="/page/instructorFeedbackResultsPage" class="form-horizontal" role="form">
    <div class="panel panel-info margin-0">
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-5" data-toggle="tooltip" title="View results in different formats">
                    <div class="form-group">
                        <label for="viewSelect" class="col-sm-2 control-label">
                            View:
                        </label>
                        <div class="col-sm-10">
                            <select id="viewSelect" class="form-control" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" onchange="this.form.submit()">
                                <option value="question" <%=(data.sortType!=null) ? data.sortType.equals("question") ? "selected=\"selected\"" : "" : ""%>>
                                    Group by - Question
                                </option>
                                <option value="giver-recipient-question" <%=(data.sortType!=null) ? data.sortType.equals("giver-recipient-question") ? "selected=\"selected\"" : "" : ""%>>
                                    Group by - Giver > Recipient > Question
                                </option>
                                <option value="recipient-giver-question" <%=(data.sortType!=null) ? data.sortType.equals("recipient-giver-question") ? "selected=\"selected\"" : "" : ""%>>
                                    Group by - Recipient > Giver > Question
                                </option>
                                <option value="giver-question-recipient" <%=(data.sortType!=null) ? data.sortType.equals("giver-question-recipient") ? "selected=\"selected\"" : "" : ""%>>
                                    Group by - Giver > Question > Recipient
                                </option>
                                <option value="recipient-question-giver" <%=(data.sortType!=null) ? data.sortType.equals("recipient-question-giver") ? "selected=\"selected\"" : "" : "selected=\"selected\""%>>
                                    Group by - Recipient > Question > Giver
                                </option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="col-sm-5" data-toggle="tooltip" title="Search the results in the current view">
                    <div class="form-group">
                        <label for="viewSelect" class="col-sm-2 control-label">
                            Search:
                        </label>
                        <div class="col-sm-10">
                            <div class="input-group">
                                <input type="text" id="results-search-box" class="form-control" placeholder="Type here to search results">
                                <a class="input-group-addon btn btn-default"><span class="glyphicon glyphicon-search"></span></a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-2 pull-right" data-toggle="tooltip" title="Group results in the current view by team">
                    <div class="checkbox pull-right">
                        <label>
                            <input type="checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" onchange="this.form.submit()" <%=(data.groupByTeam==null) ? "" : "checked=\"checked\""%> <%=(data.sortType.equals("question")) ? "disabled=\"disabled\"" : "d"%>> Group by Teams
                        </label>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-7 pull-right">
                    <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse()" data-toggle="tooltip" title="Collapse or expand all panels. You can also click on the panel heading to toggle each one individually.">
                        Collapse All
                    </a>
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



<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

<% if (noResponses) { %>
    <div class="bold color_red align-center">There are no responses for this feedback session yet.</div>
<% } %>