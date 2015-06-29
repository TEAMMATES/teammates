<%@ tag description="feedbackResponse.tag - Feedback response comment"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ attribute name="fsIndx" required="true" %>
<%@ attribute name="qnIndx" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<%@ attribute name="responseCommentIndex" required="true" %>
<%@ attribute name="feedbackResponseCommentRow" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>

<li class="list-group-item list-group-item-warning"
    id="responseCommentRow-${fsIndx}-${qnIndx}-${responseIndex}-${responseCommentIndex}">
    
    <div id="commentBar-${fsIndx}-${qnIndx}-${responseIndex}-${responseCommentIndex}">    
        <span class="text-muted"> 
            From: <b>${feedbackResponseCommentRow.giverDetails}</b>
            on ${feedbackResponseCommentRow.creationTime}
        </span> 
        
        <a type="button" target="_blank" class="btn btn-default btn-xs icon-button pull-right"
           data-toggle="tooltip" data-placement="top" style="display:none;"
           <c:forEach items="${feedbackResponseCommentRow.editButton.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach>>
               <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>                                       
        </a>
    </div>

    <div id="plainCommentText-${fsIndx}-${qnIndx}-${responseIndex}-${responseCommentIndex}">
        ${feedbackResponseCommentRow.comment}
    </div>
</li>