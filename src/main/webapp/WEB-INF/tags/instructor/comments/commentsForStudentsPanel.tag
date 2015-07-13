<%@ tag description="Comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
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
                    From <b>${commentsForStudentsTable.giverName} (${courseId})</b>
                </div>
                <ul class="list-group comments">
                    <c:forEach items="${commentsForStudentsTable.rows}" var="comment"> <%--student comments loop starts--%>
                        <c:set var="commentIdx" value="${commentIdx + 1}" />
                        <shared:comment comment="${comment}" commentIndex="${commentIdx}" />
                    </c:forEach> <%-- student comments loop ends --%>
                </ul>
            </div>
        </c:forEach> <%-- recipient loop ends --%>
    </div>
</div>