<%@ tag description="instructorCourseStudentDetails / instructorStudentRecords - More Info Modal & Panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="student" type="teammates.ui.template.StudentProfile" required="true" %>
<%@ tag import="teammates.common.util.Const" %>
<c:set var="notFilled"><i class="text-muted"><%= Const.STUDENT_PROFILE_FIELD_NOT_FILLED %></i></c:set>
<div class="modal fade" id="studentProfileMoreInfo" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">${fn:escapeXml(student.name)}'s Profile - More Info</h4>
      </div>
      <div class="modal-body">
        <br>
        <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
        <p class="text-preserve-space height-fixed-md">${empty student.moreInfo ? notFilled : fn:escapeXml(student.moreInfo)}</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<div class="row">
  <div class="col-xs-12">
    <div class="panel panel-default">
      <div class="panel-body">
        <span data-toggle="modal" data-target="#studentProfileMoreInfo"
            class="text-muted pull-right glyphicon glyphicon-resize-full cursor-pointer"></span>
        <h5><strong>More Info:</strong></h5>
        <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
        <p class="text-preserve-space height-fixed-md">${empty student.moreInfo ? notFilled : fn:escapeXml(student.moreInfo)}</p>
      </div>
    </div>
  </div>
</div>
