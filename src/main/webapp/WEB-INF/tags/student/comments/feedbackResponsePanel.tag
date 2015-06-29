<%@ tag description="StudentComments - Feedback response" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/comments" prefix="comments" %>
<%@ attribute name="feedbackResponseRow" type="teammates.ui.template.ResponseRow" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="qnIdx" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<tr>
    <td><b>From:</b> ${feedbackResponseRow.giverName}
        <b>To:</b> ${feedbackResponseRow.recipientName}
    </td>
</tr>
<tr>
    <td><strong>Response:
    </strong>${feedbackResponseRow.response}
    </td>
</tr>
<tr class="active">
    <td>Comment(s):
    </td>
</tr>
<tr>
    <td>
        <ul class="list-group comments" id="responseCommentTable-${fsIdx}-${qnIdx}-${responseIndex}"
            style="${not empty feedbackResponseRow.feedbackResponseCommentRows  ? '' : 'display:none'}">
            <c:forEach items="${feedbackResponseRow.feedbackResponseCommentRows}" var="feedbackResponseCommentRow" varStatus="k">
                <c:set var="responseCommentIndex" value="${k.index + 1}" />
                <comments:feedbackResponseCommentPanel feedbackResponseCommentRow="${feedbackResponseCommentRow}"
                 fsIdx="${fsIdx}" qnIdx="${qnIdx}" responseIndex="${responseIndex}" 
                 responseCommentIndex="${responseCommentIndex}"/>
            </c:forEach>
        </ul>
    </td>
</tr>