<%@ tag description="instructorFeedbackResultsTop - Filter Panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="allSectionsSelected" required="true" %>
<%@ attribute name="showAll" required="true" %>
<%@ attribute name="shouldCollapse" required="true" %>
<%@ attribute name="groupedByTeam" required="true" %>
<%@ attribute name="resultsLink" required="true" %>
<%@ attribute name="sections" required="true" %>
<%@ attribute name="selectedSection" required="true" %>
<%@ attribute name="sortType" required="true" %>
<%@ attribute name="feedbackSession" type="teammates.common.datatransfer.FeedbackSessionAttributes" required="true" %>
<%@ attribute name="showStats" required="true" %>
<form class="form-horizontal" role="form" method="post" action="${resultsLink}">
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
                                <option value="question"<c:if test="${sortType == 'question'}"> selected="selected"</c:if>>
                                    Group by - Question
                                </option>
                                <option value="giver-recipient-question"<c:if test="${sortType == 'giver-recipient-question'}"> selected="selected"</c:if>>
                                    Group by - Giver > Recipient > Question
                                </option>
                                <option value="recipient-giver-question"<c:if test="${sortType == 'recipient-giver-question'}"> selected="selected"</c:if>>
                                    Group by - Recipient > Giver > Question
                                </option>
                                <option value="giver-question-recipient"<c:if test="${sortType == 'giver-question-recipient'}"> selected="selected"</c:if>>
                                    Group by - Giver > Question > Recipient
                                </option>
                                <option value="recipient-question-giver"<c:if test="${empty sortType or sortType == 'recipient-question-giver'}"> selected="selected"</c:if>>
                                    Group by - Recipient > Question > Giver
                                </option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="col-sm-5" data-toggle="tooltip" title="Filter the results in the current view">
                    <div class="form-group">
                        <label for="viewSelect" class="col-sm-2 control-label">
                            Filter:
                        </label>
                        <div class="col-sm-10">
                            <div class="input-group">
                                <input type="text" id="results-search-box" class="form-control" placeholder="${sortType == 'question' ? 'Type keywords from the question to filter' : 'Type student/team name/section name to filter'}" onchange="updateResultsFilter()">
                                <a class="input-group-addon btn btn-default"><span class="glyphicon glyphicon-search"></span></a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-2 pull-right">
                  <div class="col-sm-12" data-toggle="tooltip" title="Group results in the current view by team">
                      <div class="checkbox padding-top-0 min-height-0">
                          <label<c:if test="${sortType == 'question'}"> class="text-strike"</c:if>>
                              <input type="checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" id="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>"<c:if test="${groupedByTeam}"> checked="checked"</c:if><c:if test="${sortType != 'question'}"> onchange="this.form.submit()"</c:if>> Group by Teams
                          </label>
                      </div>
                  </div>
                  <div class="col-sm-12" data-toggle="tooltip" title="Show statistics">
                      <div class="checkbox padding-top-0 min-height-0">
                          <label<c:if test="${sortType == 'recipient-giver-question' or sortType == 'giver-recipient-question'}"> class="text-strike"</c:if>>
                              <input type="checkbox" id="show-stats-checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>"<c:if test="${not empty showStats}"> checked="checked"</c:if>> Show Statistics
                          </label>
                      </div>
                  </div>
                </div>
            </div>
            <div class="row">
                <c:if test="${not empty sections}">
                    <div class="col-sm-5" data-toggle="tooltip" title="View results by sections">
                        <div class="form-group">
                            <label for="sectionSelect" class="col-sm-2 control-label">
                                Section:
                            </label>
                            <div class="col-sm-10">
                                <select id="sectionSelect" class="form-control" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" onchange="this.form.submit()">
                                    <option value="All"<c:if test="${allSectionsSelected}"> selected="selected"</c:if>>
                                        All
                                    </option>
                                    <c:forEach items="${sections}" var="section">
                                        <option value="${section}"<c:if test="${selectedSection == section}"> selected="selected"</c:if>>
                                            ${section}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </c:if>
                <div class="col-sm-7 pull-right" style="padding-top:8px;">
                    <c:choose>
                        <c:when test="${not showAll}">
                            <div style="display:inline-block;" class="pull-right" data-toggle="tooltip" title="This button is disabled because this session contains more data than we can retrieve at one go. You can still expand one panel at a time by clicking on the panels below.">
                                <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse(this)" disabled="disabled">
                                    Expand ${sortType == 'question' ? 'Questions' : 'Sections'}
                                </a>
                            </div>
                        </c:when>
                        <c:when test="${shouldCollapse}">
                            <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse(this)" data-toggle="tooltip" title="Expand all panels. You can also click on the panel heading to toggle each one individually.">
                                Expand ${sortType == 'question' ? 'Questions' : 'Sections'}
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse(this)" data-toggle="tooltip" title="Collapse all panels. You can also click on the panel heading to toggle each one individually.">
                                Collapse ${sortType == 'question' ? 'Questions' : 'Sections'}
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
        value="${feedbackSession.feedbackSessionName}">
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
        value="${feedbackSession.courseId}">
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" 
        value="${data.account.googleId}">
</form>