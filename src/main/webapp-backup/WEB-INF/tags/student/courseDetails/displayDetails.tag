<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="studentCourseDetails.jsp - Displays a block of information on student course details page" pageEncoding="UTF-8" %>
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
