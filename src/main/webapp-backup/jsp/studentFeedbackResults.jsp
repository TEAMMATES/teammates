<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/studentFeedbackResults.js"></script>
</c:set>

<ts:studentPage title="Feedback Results" jsIncludes="${jsIncludes}">
  <c:if test="${empty data.account.googleId}">
    <div id="registerMessage" class="alert alert-info">
      ${data.registerMessage}
    </div>
  </c:if>

  <feedbackResults:feedbackSessionDetailsPanel feedbackSession="${data.bundle.feedbackSession}"/>
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>

  <c:forEach items="${data.feedbackResultsQuestionsWithResponses}" var="questionWithResponses">
    <feedbackResults:questionWithResponses questionWithResponses="${questionWithResponses}"/>
  </c:forEach>

  <c:if test="${empty data.feedbackResultsQuestionsWithResponses}">
    <div class="col-sm-12" style="color: red">
      There are currently no responses for you for this feedback session.
    </div>
  </c:if>
</ts:studentPage>
