<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="instructorIndex" required="true" %>
<%@ attribute name="panelIndex" required="true" %>
<%@ attribute name="sectionRow" type="teammates.ui.template.CourseEditSectionRow" required="true" %>

<div id="tuneSectionPermissionsDiv${panelIndex}ForInstructor${instructorIndex}" data-is-originally-displayed="${sectionRow.sectionSpecial}"
    <c:if test="${not sectionRow.sectionSpecial}">
      style="display: none;"
    </c:if> >
  <div class="panel panel-info">
    <div class="panel-heading">
      <div class="row">
        <div class="col-sm-2">
          <p><strong>But in section(s)</strong></p>
        </div>

        <div class="col-sm-9">
          <c:forEach items="${sectionRow.specialSections}" var="specialSectionSmallGroup">
            <div class="col-sm-12">
              <c:forEach items="${specialSectionSmallGroup}" var="specialSection">
                <div class="col-sm-4">
                  <input ${specialSection.attributesToString}>
                  ${specialSection.content}
                </div>
              </c:forEach>
            </div>
          </c:forEach>
        </div>

        <div class="col-sm-1">
          <a href="javascript:;" data-instructorindex="${instructorIndex}" data-panelindex="${panelIndex}"
              class="pull-right hide-tune-section-permissions">
            <span class="glyphicon glyphicon-trash"></span>
          </a>
        </div>
      </div>
      <br>

      <div class="row">
        <div class="col-sm-12">
          <p><strong> the instructor can only,</strong></p>
        </div>
      </div>

      <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP%>${panelIndex}set" value="${sectionRow.sectionSpecial}"/>
    </div>

    <div class="panel-body">
      <br>
      <div class="col-sm-6 border-right-gray">
        <c:forEach items="${sectionRow.permissionInputGroup2}" var="checkbox">
          <input ${checkbox.attributesToString} /> ${checkbox.content}
          <br>
        </c:forEach>
        <br>
      </div>

      <div class="col-sm-5 col-sm-offset-1">
        <c:forEach items="${sectionRow.permissionInputGroup3}" var="checkbox">
          <input ${checkbox.attributesToString} /> ${checkbox.content}
          <br>
        </c:forEach>
        <br>
      </div>

      <c:choose>
        <c:when test="${sectionRow.sessionsInSectionSpecial}">
          <a class="small col-sm-5 hide-tune-session-permissions"
              id="toggleSessionLevelInSection${sectionRow.panelIndex}ForInstructor${sectionRow.instructorIndex}"
              data-instructorindex="${sectionRow.instructorIndex}" data-panelindex="${sectionRow.panelIndex}"
              href="javascript:;">
            Hide session-level permissions
          </a>
        </c:when>
        <c:otherwise>
          <a class="small col-sm-5 show-tune-session-permissions"
              id="toggleSessionLevelInSection${sectionRow.panelIndex}ForInstructor${sectionRow.instructorIndex}"
              data-instructorindex="${sectionRow.instructorIndex}" data-panelindex="${sectionRow.panelIndex}"
              href="javascript:;">
            Give different permissions for sessions in this section
          </a>
        </c:otherwise>
      </c:choose>

      <course:courseEditTuneSessionPermissionsDiv
          instructorIndex="${instructorIndex}"
          panelIndex="${panelIndex}"
          sectionRow="${sectionRow}"/>
    </div>
  </div>
</div>
