<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Admin sessions - feedback session row" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="feedbackSessionRow" type="teammates.ui.template.AdminFeedbackSessionRow" required="true"%>
<tr>
  <td>${feedbackSessionRow.sessionStatusForShow}</td>
  <td><strong>[${feedbackSessionRow.courseId}]</strong>&nbsp;${feedbackSessionRow.feedbackSessionName}</td>
  <c:choose>
    <c:when test="${not empty feedbackSessionRow.feedbackSessionStatsLink}">
      <td class="session-response-for-test">
        <a oncontextmenu="return false;" href="${feedbackSessionRow.feedbackSessionStatsLink}">Show</a>
      </td>
    </c:when>
    <c:otherwise>
      <td class="session-response-for-test">
        <p>Not Available</p>
      </td>
    </c:otherwise>
  </c:choose>
  <td data-date-stamp="${feedbackSessionRow.sessionStartTimeIso8601Utc}">${feedbackSessionRow.sessionStartTime}</td>
  <td data-date-stamp="${feedbackSessionRow.sessionEndTimeIso8601Utc}">${feedbackSessionRow.sessionEndTime}</td>
  <td><a target="_blank" rel="noopener noreferrer" ${feedbackSessionRow.instructorHomePageViewLink}>${feedbackSessionRow.creatorEmail}</a></td>
</tr>
