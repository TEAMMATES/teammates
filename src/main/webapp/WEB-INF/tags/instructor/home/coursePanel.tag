<%@ tag description="instructorHome - Course table panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseTable" type="teammates.ui.template.CourseTable" required="true" %>
<%@ attribute name="index" required="true" %>
<c:set var="isNotLoaded" value="${empty courseTable.buttons}" />
<div class="panel panel-primary" id="course-${index}"<c:if test="${isNotLoaded}"> style="cursor: pointer;"</c:if>>
    <div class="panel-heading">
        <div class="row">
            <div class="col-md-6">
                <strong>
                    [${courseTable.courseId}] : ${courseTable.courseName}
                </strong>
            </div>
            <div class="col-md-6">
                <span class="pull-right">
                    <c:forEach items="${courseTable.buttons}" var="button">
                        <a data-toggle="tooltip" data-placement="top" ${button.attributesToString}>
                            ${button.content}
                        </a>
                    </c:forEach>
                    <c:if test="${isNotLoaded}">
                        <span class="glyphicon glyphicon-chevron-down"></span>
                    </c:if>
                </span>
            </div>
        </div>
    </div>
    <c:if test="${isNotLoaded}">
        <form>
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_TO_LOAD%>" value="${courseTable.courseId}">
            <input type="hidden" name="index" value="${index}">
        </form>
    </c:if>
    <jsp:doBody />
</div>