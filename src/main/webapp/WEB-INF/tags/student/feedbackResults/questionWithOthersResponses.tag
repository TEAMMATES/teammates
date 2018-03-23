<%@ tag description="studentFeedbackResults.jsp - Student feedback results question with others-responses" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="questionWithOthersResponses" type="teammates.ui.template.StudentFeedbackResultsQuestionWithResponses" required="true" %>

<div class="panel panel-default">
  <div class="panel-heading">
    <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
    <h4>Question ${questionWithOthersResponses.questionDetails.questionIndex}: <span class="text-preserve-space"><c:out value="${questionWithOthersResponses.questionDetails.questionText}"/>${questionWithOthersResponses.questionDetails.additionalInfo}</span></h4>

    ${questionWithOthersResponses.questionDetails.questionResultStatistics}

    <c:if test="${questionWithOthersResponses.questionDetails.individualResponsesShownToStudents}">

      <c:forEach items="${questionWithOthersResponses.othersResponseTables}" var="othersResponseTables">
        <feedbackResults:othersResponseTable othersResponseTable="${othersResponseTables}"/>
      </c:forEach>

    </c:if>
  </div>
</div>
<br>
