<%@ tag description="StudentComments - Comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/comments" prefix="comments" %>
<%@ attribute name="commentsForStudentsTables" type="java.util.Collection" required="true" %>
<%@ attribute name="courseId" required="true" %>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>Comments for students</strong>
    </div>
    <div class="panel-body">
        <c:set var="commentIdx" value="${0}" />
        <c:forEach items="${commentsForStudentsTables}" var="commentsForStudentsTable">
            <div class="panel panel-info student-record-comments">
                <div class="panel-heading">
                    From <b>${commentsForStudentsTable.giverName} (${courseId})</b>
                </div>
                <ul class="list-group comments">
                    <c:forEach items="${commentsForStudentsTable.rows}" var="commentRow">
                        <c:set var="commentIdx" value="${commentIdx + 1}" />
                        <li class="list-group-item list-group-item-warning">
                            <div id="commentBar-${commentIdx}">
                                <span class="text-muted">
                                     To <b>${commentRow.recipientDetails}</b> [${commentRow.creationTime}] ${commentRow.editedAt}
                                </span>
                            </div>
                            <div id="plainCommentText${commentIdx}">${commentRow.comment.commentText}</div>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
    </div>
</div>