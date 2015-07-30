<%@ tag description="Feedback Response Add Comment" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseComment" required="true" %>
<%@ attribute name="firstIndex" %>
<%@ attribute name="secondIndex" %>
<%@ attribute name="thirdIndex" %>
<c:set var="divId" value="${firstIndex}-${secondIndex}-${thirdIndex}" />
<c:set var="divIdAsJsParams" value="${firstIndex},${secondIndex},${thirdIndex}" />
<c:set var="submitLink"><%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD %></c:set>
<li class="list-group-item list-group-item-warning"
    id="showResponseCommentAddForm-${divId}" style="display: none;">
    <shared:feedbackResponseCommentForm frc="${frc}"
                                        divId="${divId}"
                                        divIdAsJsParams="${divIdAsJsParams}"
                                        formType="Add"
                                        textAreaId="responseCommentAddForm"
                                        submitLink="${submitLink}"
                                        buttonText="Add" />
</li>
