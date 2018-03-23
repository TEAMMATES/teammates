<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="studentHome - Course table panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseTable" type="teammates.ui.template.CourseTable" required="true" %>
<div class="panel panel-primary">
  <div class="panel-heading">
    <strong>
      [${courseTable.courseId}] : ${fn:escapeXml(courseTable.courseName)}
    </strong>
    <span class="pull-right">
      <c:forEach items="${courseTable.buttons}" var="button">
        <a class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top" ${button.attributesToString}>
          ${button.content}
        </a>
      </c:forEach>
    </span>
  </div>
  <jsp:doBody />
</div>
