<%@ tag description="feedbackSubmissionForm.tag - Submit button" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="preview" type="java.lang.Boolean" required="true" %>
<%@ attribute name="submittable" type="java.lang.Boolean" required="true" %>

<c:choose>
    <c:when test="${preview or (not submittable)}">
        <input disabled="disabled" type="submit" class="btn btn-primary"
               id="response_submit_button" data-toggle="tooltip"
               data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>"
               value="Submit Feedback" style="background: #66727A;">
    </c:when>
    <c:otherwise>
        <input type="submit" class="btn btn-primary"
               id="response_submit_button" data-toggle="tooltip"
               data-placement="top" title="<%=Const.Tooltips.FEEDBACK_SESSION_EDIT_SAVE%>"
               value="Submit Feedback">
    </c:otherwise>
</c:choose>