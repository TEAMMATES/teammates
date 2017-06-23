<%@ tag description="instructorFeedbackResultsTop - Filter Panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="filterPanel" type="teammates.ui.template.InstructorFeedbackResultsFilterPanel" required="true" %>
<%@ attribute name="showAll" required="true" %>
<form class="form-horizontal" style="margin-bottom: 35px" role="form" method="post">
    <div class="panel panel-info margin-0">
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-12 col-md-5">
                    <div class="form-group">
                        <label for="viewSelectReadonly" class="col-sm-2 control-label">
                            View:
                        </label>
                        <div class="col-sm-10" data-toggle="tooltip" title="View results in different formats">
                            <select id="viewSelectReadonly" class="form-control"  disabled="disabled">
                                <option value="<%=Const.FeedbackSessionResults.QUESTION_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'question'}"> selected</c:if>>
                                    Group by - Question
                                </option>
                                <option value="<%=Const.FeedbackSessionResults.GRQ_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'giver-recipient-question'}"> selected</c:if>>
                                    Group by - Giver > Recipient > Question
                                </option>
                                <option value="<%=Const.FeedbackSessionResults.RGQ_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'recipient-giver-question'}"> selected</c:if>>
                                    Group by - Recipient > Giver > Question
                                </option>
                                <option value="<%=Const.FeedbackSessionResults.GQR_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'giver-question-recipient'}"> selected</c:if>>
                                    Group by - Giver > Question > Recipient
                                </option>
                                <option value="<%=Const.FeedbackSessionResults.RQG_SORT_TYPE%>"<c:if test="${empty filterPanel.sortType or filterPanel.sortType == 'recipient-question-giver'}"> selected</c:if>>
                                    Group by - Recipient > Question > Giver
                                </option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="col-sm-10 pull-right col-md-offset-0 col-md-2 margin-bottom-15px">
                  <div data-toggle="tooltip" title="Group results in the current view by team">
                      <div class="checkbox padding-top-0 min-height-0">
                          <label<c:if test="${filterPanel.sortType == 'question'}"> class="text-strike"</c:if>>
                              <input type="checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>"
                                      disabled="disabled"
                                      <c:if test="${filterPanel.groupedByTeam}">checked</c:if>
                                      <c:if test="${filterPanel.sortType != 'question'}">class="checkbox-group-by-team"</c:if>> Group by Teams
                          </label>
                      </div>
                  </div>
                  <div data-toggle="tooltip" title="Show statistics">
                      <div class="checkbox padding-top-0 min-height-0">
                          <label<c:if test="${filterPanel.sortType == 'recipient-giver-question' or filterPanel.sortType == 'giver-recipient-question'}"> class="text-strike"</c:if>>
                              <input type="checkbox" disabled="disabled"
                                      <c:if test="${filterPanel.statsShown}"> checked</c:if>> Show Statistics
                          </label>
                      </div>
                  </div>
                  <div data-toggle="tooltip" title="Indicate missing responses">
                      <div class="checkbox padding-top-0 min-height-0">
                          <input type="checkbox"
                                disabled="disabled"
                                value="true"
                                <c:if test="${filterPanel.missingResponsesShown}"> checked</c:if>>
                          Indicate Missing Responses
                      </div>
                  </div>
                </div>
            </div>
            <div class="row">
                <c:if test="${not empty filterPanel.sections}">
                    <div class="col-sm-12 col-md-5" data-toggle="tooltip" title="View results by sections">
                        <div class="form-group">
                            <label for="sectionSelectReadonly" class="col-sm-2 control-label">
                                Section:
                            </label>
                            <div class="col-sm-10">
                                <select id="sectionSelectReadonly" class="form-control" disabled="disabled">
                                    <option value="All"<c:if test="${filterPanel.allSectionsSelected}"> selected</c:if>>
                                        All
                                    </option>
                                    <c:forEach items="${filterPanel.sections}" var="section">
                                        <option value="${section}"<c:if test="${filterPanel.selectedSection == section}"> selected</c:if>>
                                            ${section}
                                        </option>
                                    </c:forEach>
                                    <option value="None"<c:if test="${filterPanel.noneSectionSelected}"> selected</c:if>>
                                        <%=Const.NO_SPECIFIC_RECIPIENT%>
                                    </option>
                                </select>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
            <div class="row">
                <div class="col-md-offset-10 col-md-2">
                    <button type="button" class="btn btn-primary btn-block" data-toggle="modal" data-target="#editModal">
                        Edit settings
                    </button>
                </div>
            </div>
        </div>
    </div>

    <div class="pull-right" style="margin-top:50px">
        <c:choose>
            <c:when test="${not showAll}">
                <div style="display:inline-block;" class="pull-right" data-toggle="tooltip" title="This button is disabled because this session contains more data than we can retrieve at one go. You can still expand one panel at a time by clicking on the panels below.">
                    <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" disabled>
                        Expand All ${filterPanel.sortType == 'question' ? 'Questions' : 'Sections'}
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <a class="btn btn-default btn-xs pull-right" id="collapse-panels-button" data-toggle="tooltip" title="Expand all panels. You can also click on the panel heading to toggle each one individually.">
                    Expand All ${filterPanel.sortType == 'question' ? 'Questions' : 'Sections'}
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</form>

<div id="editModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Edit settings</h4>
            </div>
            <div class="modal-body">
                <form role="form" method="post" action="${filterPanel.resultsLink}">
                    <div class="row">
                        <div class="col-md-7">
                            <div class="form-group">
                                <label for="viewSelect" class="control-label">
                                    View:
                                </label>
                                <div data-toggle="tooltip" title="View results in different formats">
                                    <select id="viewSelect" class="form-control" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>">
                                        <option value="<%=Const.FeedbackSessionResults.QUESTION_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'question'}"> selected</c:if>>
                                            Group by - Question
                                        </option>
                                        <option value="<%=Const.FeedbackSessionResults.GRQ_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'giver-recipient-question'}"> selected</c:if>>
                                            Group by - Giver > Recipient > Question
                                        </option>
                                        <option value="<%=Const.FeedbackSessionResults.RGQ_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'recipient-giver-question'}"> selected</c:if>>
                                            Group by - Recipient > Giver > Question
                                        </option>
                                        <option value="<%=Const.FeedbackSessionResults.GQR_SORT_TYPE%>"<c:if test="${filterPanel.sortType == 'giver-question-recipient'}"> selected</c:if>>
                                            Group by - Giver > Question > Recipient
                                        </option>
                                        <option value="<%=Const.FeedbackSessionResults.RQG_SORT_TYPE%>"<c:if test="${empty filterPanel.sortType or filterPanel.sortType == 'recipient-question-giver'}"> selected</c:if>>
                                            Group by - Recipient > Question > Giver
                                        </option>
                                    </select>
                                </div>
                            </div>
                            <c:if test="${not empty filterPanel.sections}">
                                <div data-toggle="tooltip" title="View results by sections">
                                    <div class="form-group">
                                        <label for="sectionSelect" class="control-label">
                                            Section:
                                        </label>
                                        <select id="sectionSelect" class="form-control" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>">
                                            <option value="All"<c:if test="${filterPanel.allSectionsSelected}"> selected</c:if>>
                                                All
                                            </option>
                                            <c:forEach items="${filterPanel.sections}" var="section">
                                                <option value="${section}"<c:if test="${filterPanel.selectedSection == section}"> selected</c:if>>
                                                        ${section}
                                                </option>
                                            </c:forEach>
                                            <option value="None"<c:if test="${filterPanel.noneSectionSelected}"> selected</c:if>>
                                                <%=Const.NO_SPECIFIC_RECIPIENT%>
                                            </option>
                                        </select>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                        <div class="col-md-5">
                            <label class="control-label">
                              Additional settings:
                            </label>
                            <div data-toggle="tooltip" title="Group results in the current view by team">
                                <div class="checkbox">
                                    <label<c:if test="${filterPanel.sortType == 'question'}"> class="text-strike"</c:if>>
                                        <input type="checkbox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>"
                                               id="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>"
                                               <c:if test="${filterPanel.groupedByTeam}">checked</c:if>
                                               <c:if test="${filterPanel.sortType != 'question'}">class="checkbox-group-by-team"</c:if>> Group by Teams
                                    </label>
                                </div>
                            </div>
                            <div data-toggle="tooltip" title="Show statistics">
                                <div class="checkbox">
                                    <label<c:if test="${filterPanel.sortType == 'recipient-giver-question' or filterPanel.sortType == 'giver-recipient-question'}"> class="text-strike"</c:if>>
                                        <input type="checkbox" id="show-stats-checkbox"
                                               name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>"
                                        <c:if test="${filterPanel.statsShown}"> checked</c:if>> Show Statistics
                                    </label>
                                </div>
                            </div>
                            <div data-toggle="tooltip" title="Indicate missing responses">
                                <div class="checkbox">
                                    <input type="checkbox"
                                           id="indicate-missing-responses-checkbox"
                                           value="true"
                                           name="<%=Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES%>"
                                    <c:if test="${filterPanel.missingResponsesShown}"> checked</c:if>>
                                    Indicate Missing Responses
                                </div>
                            </div>
                        </div>
                    </div>

                    <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>"
                           value="${filterPanel.feedbackSessionName}">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
                           value="${filterPanel.courseId}">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>"
                           value="${data.account.googleId}">

                    <hr>
                    <div class="row">
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary">Submit</button>
                        </div>
                        <div class="col-md-offset-8 col-md-2">
                            <button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
