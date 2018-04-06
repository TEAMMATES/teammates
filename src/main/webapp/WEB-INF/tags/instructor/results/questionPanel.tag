<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - by question" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="isShowingResponses" type="java.lang.Boolean" required="true" %>
<%@ attribute name="questionIndex" type="java.lang.Integer"%>
<%@ attribute name="questionPanel" type="teammates.ui.template.InstructorFeedbackResultsQuestionTable" required="true" %>

<div class="panel ${questionPanel.panelClass}">
  <div class="panel-heading${questionPanel.ajaxClass}">
    <c:if test="${questionPanel.collapsible}">
      <form style="display:none;" id="seeMore-${questionPanel.question.questionNumber}" class="seeMoreForm-${questionPanel.question.questionNumber}" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>">
        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${questionPanel.courseId}">
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${questionPanel.feedbackSessionName}">
        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM%>" value="${data.groupByTeam}">
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE%>" value="${data.sortType}">
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS%>" value="on" id="showStats-${questionPanel.question.questionNumber}">
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES%>" value="${data.missingResponsesShown}">
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>" value="${questionPanel.question.feedbackQuestionId}">
        <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION%>" value="${fn:escapeXml(data.selectedSection)}">
      </form>
      <div class='display-icon pull-right'>
        <span class="glyphicon ${ isShowingResponses ? 'glyphicon-chevron-up' : 'glyphicon-chevron-down'} pull-right"></span>
      </div>
    </c:if>
    <c:choose>
      <c:when test="${questionPanel.boldQuestionNumber}">
        <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD%>" class="inline">
          <div id="DownloadQuestion-${questionPanel.question.questionNumber}" class="inline">
            <input id="button_download-${questionPanel.question.questionNumber}" type="submit"
                class="btn-link text-bold padding-0 color-inherit" data-toggle="tooltip" title="Download Question Results"
                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                value="Question ${questionPanel.question.questionNumber}:">
          </div>
          <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME%>" value="${questionPanel.feedbackSessionName}">
          <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${questionPanel.courseId}">
          <input type="hidden" name="<%=Const.ParamsNames.SECTION_NAME %>" value="${fn:escapeXml(data.selectedSection)}">
          <input type="hidden" id="statsShownCheckBox" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_SHOWSTATS %>" value="${showStats}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES %>" value="${data.missingResponsesShown}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID%>" value="${questionPanel.question.feedbackQuestionId}">
          <input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_NUMBER%>" value="${questionPanel.question.questionNumber}">
        </form>
        <div class="inline panel-heading-text">
          <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
          <span class="text-preserve-space">${fn:escapeXml(questionPanel.questionText)}${questionPanel.additionalInfoText}</span>
        </div>
      </c:when>
      <c:otherwise>
        Question ${questionPanel.question.questionNumber}:
        <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
        <span class="text-preserve-space">${fn:escapeXml(questionPanel.questionText)}${questionPanel.additionalInfoText}</span>
      </c:otherwise>
    </c:choose>
  </div>
  <div <c:if test="${questionPanel.collapsible}">class="panel-collapse collapse"</c:if>>
    <div class="panel-body padding-0" <c:if test="${questionIndex != null}">id="questionBody-${questionIndex}"</c:if>>

      <c:if test="${!questionPanel.hasResponses}">
        <div class="col-sm-12 no-response">
          <i class="text-muted">There are no responses for this question or you may not have the permission to see the response</i>
        </div>
      </c:if>

      <c:if test="${questionPanel.hasResponses}">
        <div class="resultStatistics">
          ${questionPanel.questionStatisticsTable}
        </div>
        <c:if test="${questionPanel.showResponseRows}">
          <div class="table-responsive">
            <table class="table fixed-table-layout table-striped table-bordered data-table margin-0">
              <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                <tr>
                  <c:forEach items="${questionPanel.columns}" var="thElement">
                    <th ${thElement.attributesToString}>
                      ${thElement.content}
                      <c:if test="${questionPanel.isColumnSortable[thElement.content]}"><span class="icon-sort unsorted"></span></c:if>
                    </th>
                  </c:forEach>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${questionPanel.responses}" var="responseRow" varStatus="status">
                  <results:responseRow responseRow="${responseRow}" questionIndex="${questionIndex}"
                      responseIndex="${status.count}"/>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>
      </c:if>

    </div>
  </div>
</div>
