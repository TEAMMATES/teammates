<%@ tag description="studentHome - Course table panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseTable" type="teammates.ui.template.CourseTable" required="true" %>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>
            [${courseTable.courseId}] : ${courseTable.courseName}
        </strong>
        <span class="pull-right">
            <c:forEach items="${courseTable.buttons}" var="button">
                <a class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top"
                   ${button.attributesToString}>
                    ${button.content}
                </a>
            </c:forEach>
        </span>
    </div>
    <jsp:doBody />
</div>