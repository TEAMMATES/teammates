<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/courseStudentDetails" prefix="ticsd" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
    <script type="text/javascript" src="/js/richTextEditor.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorStudentRecords.js"></script>
</c:set>
<c:set var="bodyTitle">${fn:escapeXml(data.studentName)}'s Records<small class="muted"> - ${data.courseId}</small></c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" jsIncludes="${jsIncludes}" bodyTitle="${bodyTitle}">
    <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
    <input type="hidden" id="show-comment-box" value="${data.showCommentBox}">
    <div class="container-fluid">
        <c:if test="${not empty data.studentProfile}">
            <ticsd:studentProfile student="${data.studentProfile}"/>
            <ti:moreInfo student="${data.studentProfile}" />
        </c:if>
        <div class="row">
            <div class="col-md-12">
                <shared:commentsPanel commentsForStudentsTables="${data.commentsForStudentTable}" courseId="${data.courseId}" forRecordsPage="${true}" />
                <br>
                <c:forEach items="${data.sessionNames}" var="fsName" varStatus="fbIndex">
                    <div class="student_feedback panel panel-default"
                         id="studentFeedback-${fbIndex.index}" 
                         onclick="loadFeedbackSession('${data.courseId}', '${data.studentEmail}', '${data.googleId}','${fsName}', this)">
                        <div class="panel-heading student_feedback" data-target="#collapse-target-feedback-${fbIndex.index}" style="cursor: pointer;">
                            <div class="display-icon pull-right"><span class="glyphicon pull-right glyphicon-chevron-up"></span></div>
                            <span id="feedback_name-${fbIndex.index}">
                                <strong>Feedback Session : ${fsName}</strong>
                            </span>
                        </div>
                        <div class="placeholder-img-loading"></div>
                        <div class="panel-collapse collapse in" id="collapse-target-feedback-${fbIndex.index}">
                            <div class="panel-body" id="target-feedback-${fbIndex.index}"></div>
                        </div>
                    </div>
                    <br>                    
                </c:forEach>
            </div>
        </div>
    </div>
</ti:instructorPage>