<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="r" %>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<c:set var="showAll" value="${data.bundle.complete}" />
<c:set var="shouldCollapse" value="${fn:length(data.bundle.responses) > 500}" />
<c:set var="noResponses" value="${empty data.bundle.responses}" />
<c:set var="allSectionsSelected" value="${data.selectedSection == 'All'}" />

<r:sessionPanel sessionPanel="${data.sessionPanel}" />

<c:if test="${not (noResponses and allSectionsSelected and showAll)}">
<form class="form-horizontal" role="form" method="post" action="${data.instructorFeedbackSessionResultsLink}>">
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
                                <option value="question"<c:if test="${data.sortType == 'question'}"> selected="selected"</c:if>>
                                    Group by - Question
                                </option>
                                <option value="giver-recipient-question"<c:if test="${data.sortType == 'giver-recipient-question'}"> selected="selected"</c:if>>
                                    Group by - Giver > Recipient > Question
                                </option>
                                <option value="recipient-giver-question"<c:if test="${data.sortType == 'recipient-giver-question'}"> selected="selected"</c:if>>
                                    Group by - Recipient > Giver > Question
                                </option>
                                <option value="giver-question-recipient"<c:if test="${data.sortType == 'giver-question-recipient'}"> selected="selected"</c:if>>
                                    Group by - Giver > Question > Recipient
                                </option>
                                <option value="recipient-question-giver"<c:if test="${empty data.sortType or data.sortType == 'recipient-question-giver'}"> selected="selected"</c:if>>
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
                                <input type="text" id="results-search-box" class="form-control" placeholder="${data.sortType == 'question' ? 'Type keywords from the question to filter' : 'Type student/team name/section name to filter'}" onchange="updateResultsFilter()">
                                <a class="input-group-addon btn btn-default"><span class="glyphicon glyphicon-search"></span></a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-sm-2 pull-right">
                  <div class="col-sm-12" data-toggle="tooltip" title="Group results in the current view by team">
                      <div class="checkbox padding-top-0 min-height-0">
                          <label<c:if test="${data.sortType == 'question'}"> class="text-strike"</c:if>>
                              <input type="checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" id="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>"<c:if test="${not empty data.groupByTeam}"> checked="checked"</c:if><c:if test="${data.sortType != 'question'}"> onchange="this.form.submit()"</c:if>> Group by Teams
                          </label>
                      </div>
                  </div>
                  <div class="col-sm-12" data-toggle="tooltip" title="Show statistics">
                      <div class="checkbox padding-top-0 min-height-0">
                          <label<c:if test="${data.sortType == 'recipient-giver-question' or data.sortType == 'giver-recipient-question'}"> class="text-strike"</c:if>>
                              <input type="checkbox" id="show-stats-checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>"<c:if test="${not empty data.showStats}"> checked="checked"</c:if>> Show Statistics
                          </label>
                      </div>
                  </div>
                </div>
            </div>
            <div class="row">
                <c:if test="${not empty data.sections}">
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
                                    <c:forEach items="${datasections}" var="section">
                                        <option value='${section}'<c:if test="${data.selectedSection == section}"> selected="selected"</c:if>>
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
                                <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse(this)"<c:if test="${not showAll}"> disabled="disabled"</c:if>>
                                    Expand ${data.sortType == 'question' ? 'Questions' : 'Sections'}
                                </a>
                            </div>
                        </c:when>
                        <c:when test="${shouldCollapse}">
                            <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse(this)" data-toggle="tooltip" title="Expand all panels. You can also click on the panel heading to toggle each one individually.">
                                Expand ${data.sortType == 'question' ? 'Questions' : 'Sections'}
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" onclick="toggleCollapse(this)" data-toggle="tooltip" title="Collapse all panels. You can also click on the panel heading to toggle each one individually.">
                                Collapse ${data.sortType == 'question' ? 'Questions' : 'Sections'}
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
        value="${data.feedbackSessionName}">
    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
        value="${data.courseId}">
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" 
        value="${data.account.googleId}">
</form>
</c:if>
<br>
<t:statusMessage />
<br>
<c:if test="${noResponses and showAll}">
    <div class="bold color_red align-center">There are no responses for this feedback session yet or you do not have access to the responses collected so far.</div>
</c:if>
