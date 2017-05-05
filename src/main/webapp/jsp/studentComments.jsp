<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/comments" prefix="comments" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/student.js"></script>
    <script type="text/javascript" src="/js/studentComments.js"></script>
</c:set>
<ts:studentPage bodyTitle="Comments" pageTitle="TEAMMATES - Student" jsIncludes="${jsIncludes}">
    <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
    <br>
    <c:choose>
        <c:when test="${not empty data.coursePagination.coursePaginationList}">
            <shared:pagination coursePagination="${data.coursePagination}" />
            <div class="well well-plain">
                <div class="text-color-primary">
                    <h4>
                        <strong> ${data.courseName}
                        </strong>
                    </h4>
                </div>
                <div id="no-comment-panel" style="${empty data.commentsForStudentsTables && empty data.feedbackSessionRows ? '' : 'display:none;'}">
                    <br>
                    <div class="panel">
                        <div class="panel-body">
                            You don't have any comment in this course.
                        </div>
                    </div>
                </div>
                <c:if test="${not empty data.commentsForStudentsTables}">
                    <br>
                    <shared:commentsPanel commentsForStudentsTables="${data.commentsForStudentsTables}" courseId="${data.courseId}" />
                </c:if>
                <c:forEach items="${data.feedbackSessionRows}" var="feedbackSessionRow" varStatus="i">
                    <br>
                    <comments:feedbackSessionPanel feedbackSessionRow="${feedbackSessionRow}" fsIdx="${i.index + 1}"/>
                </c:forEach>
            </div>
            <shared:pagination coursePagination="${data.coursePagination}" />
        </c:when>
        <c:otherwise>
            <div id="statusMessage" class="alert alert-warning">
                There is no comment to display
            </div>
        </c:otherwise>
    </c:choose>
</ts:studentPage>
