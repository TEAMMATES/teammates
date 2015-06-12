<%@ tag description="studentCourseDetails.jsp - Displays a block of information on student course details page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/student/courseDetails" prefix="courseDetails" %>
<%@ attribute name="heading" fragment="true" %>
<%@ attribute name="id" required="true" %>

<div class="form-group">
    <label class="col-sm-3 control-label">
        <jsp:invoke fragment="heading"/>
    </label>
    <div class="col-sm-9">
        <p class="form-control-static" id="${id}">
            <jsp:doBody/>
        </p>
    </div>
</div>
