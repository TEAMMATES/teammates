<%@ tag description="instructorHome - Course table panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseTable" type="teammates.ui.template.CourseTable" required="true" %>
<%@ attribute name="index" required="true" %>
<div class="panel panel-primary" id="course${index}">
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
                        <a data-toggle="tooltip" data-placement="top"
                           <c:forEach items="${button.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach>>
                            ${button.content}
                        </a>
                    </c:forEach>
                </span>
            </div>
        </div>
    </div>
    <jsp:doBody />
</div>