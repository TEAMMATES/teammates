<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/home" prefix="home" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorHome.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Instructor Home" jsIncludes="${jsIncludes}">
    <home:search />
    <br />
    <t:statusMessage />
    <ti:remindParticularStudentsModal />
    <c:if test="${data.account.instructor}">
        <c:if test="${data.unarchivedCoursesCount > 1}">
            <div class="row">
                <home:sort />
            </div>
        </c:if>
        <br />
        <c:forEach items="${data.courseTables}" var="courseTable" varStatus="i">
            <home:coursePanel courseTable="${courseTable}" index="${i.index}">
                <home:courseTable sessionRows="${courseTable.rows}" />
            </home:coursePanel>
        </c:forEach>
        <ti:copyModal />
    </c:if>
</ti:instructorPage>