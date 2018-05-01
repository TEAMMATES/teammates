<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="studentFeedbackResults.jsp - Displays feedback session details" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="feedbackSession" type="teammates.common.datatransfer.attributes.FeedbackSessionAttributes" required="true" %>

<div class="well well-plain">
  <div class="panel-body">
    <div class="form-horizontal">
      <div class="panel-heading">
        <feedbackResults:displayFeedbackSessionInfo label="Course ID:">
          <c:out value="${feedbackSession.courseId}"/>
        </feedbackResults:displayFeedbackSessionInfo>

        <feedbackResults:displayFeedbackSessionInfo label="Session:">
          <c:out value="${feedbackSession.feedbackSessionName}"/>
        </feedbackResults:displayFeedbackSessionInfo>

        <feedbackResults:displayFeedbackSessionInfo label="Opening time:">
          ${feedbackSession.startTimeString}
        </feedbackResults:displayFeedbackSessionInfo>

        <feedbackResults:displayFeedbackSessionInfo label="Closing time:">
          ${feedbackSession.endTimeString}
        </feedbackResults:displayFeedbackSessionInfo>
      </div>
    </div>
  </div>
</div>
<br>
