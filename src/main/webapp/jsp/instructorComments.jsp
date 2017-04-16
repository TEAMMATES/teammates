<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/comments" prefix="comments" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<c:set var="cssIncludes">
    <link type="text/css" href="/stylesheets/omniComment.css" rel="stylesheet">
</c:set>
<c:set var="jsIncludes">
    <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
    <script type="text/javascript" src="/js/richTextEditor.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/feedbackResponseComments.js"></script>
    <script type="text/javascript" src="/js/instructorComments.js"></script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Comments from Instructors" cssIncludes="${cssIncludes}" jsIncludes="${jsIncludes}">
    <comments:search instructorSearchLink="${data.instructorSearchLink}" displayArchive="${data.displayArchive}" instructorCommentsLink="${data.instructorCommentsLink}" commentsForStudentsTables="${data.commentsForStudentsTables}" feedbackSessions="${data.feedbackSessions}" />
    <br>
    <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
    <c:choose>
    <c:when test="${not empty data.coursePagination.coursePaginationList}">
        <shared:pagination coursePagination="${data.coursePagination}"/>
        <div class="well well-plain">
            <div class="row">
                <h4 class="col-sm-9 text-color-primary">
                    <strong>
                        ${data.viewingDraft ? "Drafts" : data.courseName}
                    </strong>
                </h4>
                <comments:commentsNotifyPanel courseId="${data.courseId}" numberOfPendingComments="${data.numberOfPendingComments}" />
            </div>
            <div id="no-comment-panel" style="${empty data.commentsForStudentsTables and empty data.feedbackSessions ? '' : 'display:none;'}">
                <br>
                <div class="panel">
                    <div class="panel-body">
                        You don't have any comment in this course.
                    </div>
                </div>
            </div>
            <c:set var="panelIdx" value="0" />
            <c:if test="${not empty data.commentsForStudentsTables}"> <%--check student comments starts--%> 
                <c:set var="panelIdx" value="${panelIdx + 1}" />
                <div id="panel_display-${panelIdx}">
                    <br>
                    <shared:commentsPanel courseId="${data.courseId}" commentsForStudentsTables="${data.commentsForStudentsTables}" viewingDraft="${data.viewingDraft}" />
                </div>
            </c:if> <%--check student comments ends --%>
            <c:set var="fsIdx" value="0" />
            <c:forEach items="${data.feedbackSessions}" var="fs" varStatus="fsIdx"> <%--FeedbackSession loop starts--%>
                <c:set var="fsName" value="${fs.sessionName}" />
                <c:set var="panelIdx" value="${panelIdx + 1}"/>
                <div id="panel_display-${panelIdx}">
                    <br>
                    <comments:commentsFromFeedbackSessions courseId="${data.courseId}" fsName="${fsName}" fsIdx="${fsIdx.index + 1}" panelIdx="${panelIdx}" />
                </div>
            </c:forEach> <%-- FeedbackSession loop ends --%>
        </div>
        <shared:pagination coursePagination="${data.coursePagination}"/>
    </c:when>
    <c:otherwise>
        <div id="statusMessage" class="alert alert-warning">
            There is no comment to display
        </div>
    </c:otherwise>
    </c:choose>
</ti:instructorPage>
