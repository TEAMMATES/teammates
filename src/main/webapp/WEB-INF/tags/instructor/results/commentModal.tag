<%@ tag description="instructorFeedbackResultsTop - Filter Panel Edit Modal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="response" type="teammates.ui.template.InstructorFeedbackResultsResponseRow" required="true" %>
<%@ attribute name="questionIndex" type="java.lang.Integer"%>
<%@ attribute name="responseIndex" type="java.lang.Integer"%>

<div class="modal fade" id="commentModal" role="dialog">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Add Comment:</h4>
      </div>
      hello
      ${response.commentsOnResponses}
      <div class="modal-body">
        <ul class="list-group" id="responseCommentTable-${responseIndex}-${questionIndex}"
          style="${not empty response.commentsOnResponses ? 'margin-top:15px;': 'display:none'}">
        <c:forEach items="${response.commentsOnResponses}" var="responseComment" varStatus="status">
          <shared:feedbackResponseCommentRow frc="${responseComment}" firstIndex="${responseIndex}"
              secondIndex="1" thirdIndex="${questionIndex}" frcIndex="${status.count}"/>
        </c:forEach>
        <shared:feedbackResponseCommentAdd frc="${response.addCommentButton}" firstIndex="${responseIndex}"
            secondIndex="0" thirdIndex="${questionIndex}"/>
      </ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>  
  </div>
</div>