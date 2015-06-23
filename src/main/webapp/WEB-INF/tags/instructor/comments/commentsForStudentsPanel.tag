<%@ tag description="Comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/comments" prefix="comments" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="commentsForStudentsTables" type="java.util.Collection" required="true" %>
<%@ attribute name="viewingDraft" required="true" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>${viewingDraft ? 'Comment drafts' : 'Comments for students'}</strong>
    </div>
    <div class="panel-body">
        ${viewingDraft ? 'Your comments that are not finished:' : ''}
        <c:set var="commentIdx" value="0" />
        <c:forEach items="${commentsForStudentsTables}" var="commentsForStudentsTable"> <%--recipient loop starts--%>
            <div class="panel panel-info student-record-comments ${commentsForStudentsTable.giverEmail == '0you' ? 'giver_display-by-you' : 'giver_display-by-others'}"
                <c:if test="${empty commentsForStudentsTable.rows}"> 
                    style="display: none;" 
                </c:if>>
                <div class="panel-heading">
                    From <b>${commentsForStudentsTable.giverName}</b>
                </div>
                <ul class="list-group comments">
                    <c:forEach items="${commentsForStudentsTable.rows}" var="commentRow"> <%--student comments loop starts--%>
                        <c:set var="commentIdx" value="${commentIdx + 1}" />
                        <li id="${commentRow.comment.commentId}"
                            class="list-group-item list-group-item-warning ${not empty commentRow.comment.showCommentTo ? 'status_display-public' : 'status_display-private'}">
                            <form method="post"
                                action="<%= Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT %>"
                                name="form_commentedit"
                                class="form_comment"
                                id="form_commentedit-${commentIdx}">
                                <div id="commentBar-${commentIdx}">
                                    
                                    <span class="text-muted">To ${commentRow.recipientDetails} on ${commentRow.creationTime} ${commentRow.editedAt}</span>
                                    <c:if test="${commentRow.instructorAllowedToModifyCommentInSection}"> <%-- comment edit/delete control starts --%>
                                        <a type="button"
                                            id="commentdelete-${commentIdx}"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            onclick="return deleteComment('${commentIdx}');"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            title=""
                                            data-original-title="<%= Const.Tooltips.COMMENT_DELETE %>"
                                            style="display: none;">
                                            <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                        </a> 
                                        <a type="button"
                                            id="commentedit-${commentIdx}"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            onclick="return enableEdit('${commentIdx}');"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            title=""
                                            data-original-title="<%= Const.Tooltips.COMMENT_EDIT %>"
                                            style="display: none;">
                                            <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                        </a>
                                    </c:if> <%-- comment edit/delete control ends --%>
                                    <c:if test="${not empty commentRow.comment.showCommentTo}">
                                        <span class="glyphicon glyphicon-eye-open" data-toggle="tooltip" style="margin-left: 5px;"
                                            data-placement="top"
                                            title="This comment is visible to ${commentRow.typeOfPeopleCanViewComment}"></span>
                                    </c:if>
                                    <c:if test="${commentRow.comment.pendingNotification}">
                                        <span class="glyphicon glyphicon-bell" data-toggle="tooltip" 
                                            data-placement="top"
                                            title="This comment is pending notification. i.e., you have not sent a notification about this comment yet"></span>
                                    </c:if>
                                </div>
                                <div id="plainCommentText${commentIdx}">${commentRow.comment.commentText}</div>
                                <c:if test="${commentRow.instructorAllowedToModifyCommentInSection}"> <%-- comment edit/delete control starts --%>
                                    <comments:visibilityOptions commentRow="${commentRow}" commentIdx="${commentIdx}" courseId="${courseId}"/>
                                </c:if> <%--comment edit/delete control ends --%>
                            </form>
                        </li>
                    </c:forEach> <%-- student comments loop ends --%>
                </ul>
            </div>
        </c:forEach> <%-- recipient loop ends --%>
    </div>
</div>