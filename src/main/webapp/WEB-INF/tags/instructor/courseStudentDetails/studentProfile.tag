<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseStudentDetails / instructorStudentRecords - Student Profile" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="student" type="teammates.ui.template.StudentProfile" required="true" %>
<%@ tag import="teammates.common.util.Const" %>
<c:set var="none"><i class="text-muted"><%= Const.STUDENT_PROFILE_FIELD_NOT_FILLED %></i></c:set>
<c:set var="noneForGender"><span class="text-muted"><%= Const.STUDENT_PROFILE_FIELD_NOT_FILLED %></span></c:set>
<c:set var="other"><%= Const.GenderTypes.OTHER %></c:set>
<div class="row">
  <div class="col-xs-12">
    <div class="row" id="studentProfile">
      <div class="col-md-2 col-xs-3 block-center">
        <img src="${student.pictureUrl}" class="profile-pic pull-right">
      </div>
      <div class="col-md-10 col-sm-9 col-xs-8">
        <table class="table table-striped">
          <thead>
            <tr>
              <th colspan="2"><h4><span class="text-bold">Profile Details </span>(submitted by student)</h4></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="text-bold">Short Name (Gender)</td>
              <td>${empty student.shortName ? none : fn:escapeXml(student.shortName)}
                (<i> ${student.gender == other ? noneForGender : fn:escapeXml(student.gender)} </i>)
              </td>
            </tr>
            <tr>
              <td class="text-bold">Personal Email</td>
              <td>${empty student.email ? none : fn:escapeXml(student.email)}</td>
            </tr>
            <tr>
              <td class="text-bold">Institution</td>
              <td>${empty student.institute ? none : fn:escapeXml(student.institute)}</td>
            </tr>
            <tr>
              <td class="text-bold">Nationality</td>
              <td>${empty student.nationality ? none : fn:escapeXml(student.nationality)}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>
