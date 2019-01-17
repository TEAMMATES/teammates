<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="feedbackSubmissionEdit.jsp - Displays feedback session details" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="feedbackSession" type="teammates.common.datatransfer.attributes.FeedbackSessionAttributes" required="true" %>

<div class="well well-plain" id="course1">
  <div class="panel-body">
    <div class="form-horizontal">
      <div class="panel-heading">
        <div class="form-group">
          <label class="col-sm-2 control-label">Course ID:</label>
          <div class="col-sm-10">
            <p class="form-control-static">
              <c:out value="${feedbackSession.courseId}"/>
            </p>
          </div>
        </div>

        <div class="form-group">
          <label class="col-sm-2 control-label">Session:</label>
          <div class="col-sm-10">
            <p class="form-control-static">
              <c:out value="${feedbackSession.feedbackSessionName}"/>
            </p>
          </div>
        </div>

        <div class="form-group">
          <label class="col-sm-2 control-label">Opening time:</label>
          <div class="col-sm-10">
            <p class="form-control-static" data-date-stamp="${feedbackSession.startTimeInIso8601UtcFormat}">${feedbackSession.startTimeString}</p>
          </div>
        </div>

        <div class="form-group">
          <label class="col-sm-2 control-label">Closing time:</label>
          <div class="col-sm-10">
            <p class="form-control-static" id="end-time" data-date-stamp="${feedbackSession.endTimeInIso8601UtcFormat}">${feedbackSession.endTimeString}</p>
          </div>
        </div>

        <div class="form-group">
          <label class="col-sm-2 control-label">Instructions:</label>
          <div class="col-sm-10">
            <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
            <p class="form-control-static text-preserve-space">${feedbackSession.instructionsString}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<br>
