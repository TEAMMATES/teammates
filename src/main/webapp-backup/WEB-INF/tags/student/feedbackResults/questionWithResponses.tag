<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="studentFeedbackResults.jsp - Student feedback results question with responses" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="questionWithResponses" type="teammates.ui.template.StudentFeedbackResultsQuestionWithResponses" required="true" %>

<div class="panel panel-default">
  <div class="panel-heading">
    <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
    <h4>Question ${questionWithResponses.questionDetails.questionIndex}: <span class="text-preserve-space"><c:out value="${questionWithResponses.questionDetails.questionText}"/>${questionWithResponses.questionDetails.additionalInfo}</span></h4>

    ${questionWithResponses.questionDetails.questionResultStatistics}

    <c:if test="${questionWithResponses.questionDetails.individualResponsesShownToStudents}">

      <c:forEach items="${questionWithResponses.othersResponseTables}" var="othersResponseTable">
        <feedbackResults:responseTable responseTable="${othersResponseTable}"/>
      </c:forEach>

      <c:if test="${!questionWithResponses.isSelfResponseTablesEmpty}">

        <h5><b>Your own responses:</b></h5>

        <c:forEach items="${questionWithResponses.selfResponseTables}" var="selfResponseTable">
          <feedbackResults:responseTable responseTable="${selfResponseTable}"/>
        </c:forEach>

      </c:if>

    </c:if>
  </div>
</div>
<br>
