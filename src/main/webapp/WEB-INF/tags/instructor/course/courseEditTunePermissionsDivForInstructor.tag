<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ attribute name="instructorPanel" type="teammates.ui.template.CourseEditInstructorPanel" required="true" %>

<div id="tunePermissionsDivForInstructor${instructorPanel.index}" style="display: none;">
  <div class="form-group">
    <div class="col-xs-12">
      <div class="panel panel-info">
        <div class="panel-heading">
          <strong>In general, this instructor can</strong>
        </div>

        <div class="panel-body">
          <c:forEach items="${instructorPanel.permissionInputGroup1}" var="permissionCheckbox">
            <div class="col-sm-3">
              <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
            </div>
          </c:forEach>
          <br>
          <br>

          <div class="col-sm-6 border-right-gray">
            <c:forEach items="${instructorPanel.permissionInputGroup2}" var="permissionCheckbox">
              <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
              <br>
            </c:forEach>
          </div>

          <div class="col-sm-5 col-sm-offset-1">
            <c:forEach items="${instructorPanel.permissionInputGroup3}" var="permissionCheckbox">
              <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
              <br>
            </c:forEach>
          </div>
        </div>
      </div>

      <c:if test="${not empty instructorPanel.sectionRows}" >
        <c:forEach items="${instructorPanel.sectionRows}" var="sectionRow">
          <course:courseEditTuneSectionPermissionsDiv
              instructorIndex="${instructorPanel.index}"
              panelIndex="${sectionRow.panelIndex}"
              sectionRow="${sectionRow}"/>
        </c:forEach>

        <a class="small show-tune-section-permissions" id="addSectionLevelForInstructor${instructorPanel.index}" href="javascript:;"
            data-instructorindex="${instructorPanel.index}" data-panelindex="${instructorPanel.firstBlankSectionRowIndex}">
          Give different permissions for a specific section
        </a>
      </c:if>
    </div>
  </div>
</div>
