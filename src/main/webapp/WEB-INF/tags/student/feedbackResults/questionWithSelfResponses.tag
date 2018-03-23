<%@ tag description="studentFeedbackResults.jsp - Student feedback results question with self-responses" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="questionWithSelfResponses" type="teammates.ui.template.StudentFeedbackResultsQuestionWithResponses" required="true" %>

<div class="panel panel-default">
  <div class="panel-heading">
    <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
    <h4>Question ${questionWithSelfResponses.questionDetails.questionIndex}: <span class="text-preserve-space"><c:out value="${questionWithSelfResponses.questionDetails.questionText}"/>${questionWithSelfResponses.questionDetails.additionalInfo}</span></h4>

    ${questionWithSelfResponses.questionDetails.questionResultStatistics}

    <c:if test="${questionWithSelfResponses.questionDetails.individualResponsesShownToStudents}">

      <c:forEach items="${questionWithSelfResponses.selfResponseTables}" var="selfResponseTables">
        <feedbackResults:selfResponseTable selfResponseTable="${selfResponseTables}"/>
      </c:forEach>

    </c:if>
  </div>
</div>
<br>
