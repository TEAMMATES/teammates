<%@ tag description="Panel for student comments" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ attribute name="commentsForStudentsTables" type="java.util.Collection" required="true" %>
<%@ attribute name="courseId" %>
<%@ attribute name="viewingDraft" %>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>${viewingDraft ? 'Comment drafts' : 'Comments for students'}</strong>
    </div>
    <div class="panel-body">
        <c:if test="${viewingDraft}">Your comments that are not finished:</c:if>
        <c:set var="commentIndex" value="${0}"/>
        <c:forEach items="${commentsForStudentsTables}" var="commentsForStudentsTable">
            <div class="panel panel-info student-record-comments${commentsForStudentsTable.extraClass}"
                 <c:if test="${empty commentsForStudentsTable.rows}">style="display: none;"</c:if>>
                <div class="panel-heading">
                    From <b>${commentsForStudentsTable.giverDetails}<c:if test="${not empty courseId}"> (${courseId})</c:if></b>
                </div>
                <ul class="list-group comments"> 
                    <c:forEach items="${commentsForStudentsTable.rows}" var="commentRow">
                        <c:set var="commentIndex" value="${commentIndex + 1}" />
                        <shared:comment comment="${commentRow}" commentIndex="${commentIndex}" />
                    </c:forEach>
                </ul>
            </div>
        </c:forEach>
    </div>
</div>