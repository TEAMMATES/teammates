<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResultsTop - Filter Panel Edit Modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="responseRow" type="teammates.ui.template.InstructorFeedbackResultsResponseRow" required="true" %>
<%@ attribute name="questionIndex" type="java.lang.Integer"%>
<%@ attribute name="responseGiverIndex" type="java.lang.Integer"%>
<%@ attribute name="responseRecipientIndex" type="java.lang.Integer"%>

<div class="modal fade" id="commentModal-${responseRecipientIndex}-${responseGiverIndex}-${questionIndex}" role="dialog">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="${responseRecipientIndex}"
            data-giverindex="${responseGiverIndex}" data-qnindex="${questionIndex}">
          &times;
        </button>
        <h4 class="modal-title">Add Comment:</h4>
      </div>
      <div class="modal-body">
        <ul class="list-group" id="responseCommentTable-${responseRecipientIndex}-${responseGiverIndex}-${questionIndex}"
            style="${not empty responseRow.commentsOnResponses ? 'margin-top:15px;' : 'display:none'}">
          <c:forEach items="${responseRow.commentsOnResponses}" var="responseComment" varStatus="status">
            <shared:feedbackResponseCommentRow frc="${responseComment}" firstIndex="${responseRecipientIndex}"
                secondIndex="${responseGiverIndex}" thirdIndex="${questionIndex}"
                frcIndex="${status.count}"/>
          </c:forEach>
          <shared:feedbackResponseCommentAdd frc="${responseRow.addCommentButton}" firstIndex="${responseRecipientIndex}"
              secondIndex="${responseGiverIndex}" thirdIndex="${questionIndex}" isOnQuestionsPage="true"/>
        </ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="${responseRecipientIndex}"
            data-giverindex="${responseGiverIndex}" data-qnindex="${questionIndex}">
          Close
        </button>
      </div>
    </div>
  </div>
</div>
