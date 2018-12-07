<%@ tag import="teammates.common.util.Const" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Feedback Response Add Comment" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ attribute name="frc" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<%@ attribute name="qnIndex" required="true" %>

<c:set var="divId" value="${qnIndex}-${responseIndex}" />
<li class="list-group-item list-group-item-warning"
    id="showResponseCommentAddForm-${divId}" style="display: none;">
  <c:set var="textAreaId"><%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ADD_TEXT%></c:set>
  <shared:feedbackResponseCommentFormForFeedbackParticipant responseIndex="${responseIndex}"
      qnIndex="${qnIndex}" frc="${frc}" formType="Add" textAreaId="${textAreaId}"/>
</li>
