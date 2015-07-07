<%@ tag description="responseTable.tag - Responses given to a particular recipient" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="response" type="teammates.ui.template.FeedbackResultsResponse" required="true" %>

<tr class="resultSubheader">
    <td>
        <span class="bold"><b>From:</b></span> ${response.giverName}
    </td>
</tr>

<tr>
    <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
    <td class="text-preserve-space">${response.answer}</td>
</tr>
                                                            
<c:if test="${not empty response.comments}">
    <tr>
        <td>
            <ul class="list-group comment-list">                                                                           
                <c:forEach items="${response.comments}" var="comment">                                                                            
                    <li class="list-group-item list-group-item-warning" id="responseCommentRow-${comment.frcId}">
                        <div id="commentBar-${comment.frcId}">
                            <span class="text-muted">From: ${comment.giverEmail} [${comment.createdAt}] ${comment.editedAtText}</span>
                        </div>
                        <div id="plainCommentText-${comment.frcId}" style="margin-left: 15px;">${comment.commentText}</div>
                    </li>
                </c:forEach>                                                                                   
            </ul>
        </td>
    </tr>                                                            
</c:if>
                                                            