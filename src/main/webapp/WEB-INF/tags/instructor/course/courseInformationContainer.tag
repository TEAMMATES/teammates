<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseDetails - Course Information Board" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseDetails" type="teammates.common.datatransfer.CourseDetailsBundle" required="true" %>
<%@ attribute name="instructors" type="java.util.Collection" required="true" %>
<%@ attribute name="courseRemindButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="courseDeleteAllButton" type="teammates.ui.template.ElementTag" required="true" %>

<div class="well well-plain" id="courseInformationHeader">
  <div class="form form-horizontal">
    <course:courseInformation courseDetails="${courseDetails}" instructors="${instructors}" />

    <c:if test="${courseDetails.stats.studentsTotal > 1}">
      <course:studentInformationButtons courseDetails="${courseDetails}" courseRemindButton="${courseRemindButton}" courseDeleteAllButton="${courseDeleteAllButton}"/>
    </c:if>
  </div>
</div>
