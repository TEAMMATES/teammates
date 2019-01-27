<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ attribute name="addInstructorButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="addInstructorPanel" type="teammates.ui.template.CourseEditInstructorPanel" required="true" %>
<%@ attribute name="addInstructorCancelButton" type="teammates.ui.template.ElementTag" required="true" %>

<div class="align-center">
  <input value="Add New Instructor" ${addInstructorButton.attributesToString}>
  <input type="hidden" value="${addInstructorPanel.index}" id="new-instructor-index">
</div>

<div class="panel panel-primary" id="panelAddInstructor" style="display: none;">
  <div class="panel-heading">
    <strong>Instructor ${addInstructorPanel.index}:</strong>
    <div class="pull-right">
      <div class="display-icon" style="display:inline;"></div>
      <a ${addInstructorCancelButton.attributesToString}>
        ${addInstructorCancelButton.content}
      </a>
    </div>
  </div>

  <div class="panel-body fill-plain">
    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_ADD%>" name="formAddInstructor"
        class="form form-horizontal" id="formAddInstructor">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseId}">
      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
      <input type="hidden" name="<%=Const.ParamsNames.SESSION_TOKEN%>" value="${data.sessionToken}">

      <div id="instructorAddTable">
        <div class="form-group">
          <label class="col-sm-3 control-label">Name:</label>
          <div class="col-sm-9">
            <input class="form-control" type="text"
                name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" id="<%=Const.ParamsNames.INSTRUCTOR_NAME%>"
                data-toggle="tooltip" data-placement="top" title="Enter the name of the instructor."
                maxlength="<%=FieldValidator.PERSON_NAME_MAX_LENGTH%>" tabindex="8"/>
          </div>
        </div>

        <div class="form-group">
          <label class="col-sm-3 control-label">Email:</label>
          <div class="col-sm-9">
            <input class="form-control" type="text"
                name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" id="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>"
                data-toggle="tooltip" data-placement="top" title="Enter the Email of the instructor."
                maxlength="<%=FieldValidator.EMAIL_MAX_LENGTH%>" tabindex="9"/>
          </div>
        </div>

        <div id="accessControlEditDivForInstr${addInstructorPanel.index}">
          <div class="form-group">
            <label class="col-sm-3 control-label">
              <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT%>" value="true" checked
                  data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_TO_STUDENT%>">
              Display to students as:
            </label>
            <div class="col-sm-9">
              <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME%>"
                  placeholder="E.g.Co-lecturer, Teaching Assistant" value="Instructor"
                  data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.INSTRUCTOR_DISPLAYED_AS%>"/>
            </div>
          </div>

          <div class="form-group">
            <div class="col-sm-3">
              <label class="control-label pull-right">Access-level</label>
            </div>

            <div class="col-sm-9">
              <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
                  id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${addInstructorPanel.index}"
                  value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER%>"
                  checked>
              &nbsp;Co-owner: Can do everything
              <a href="javascript:;" class="view-role-details"
                  data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER %>">
                View Details
              </a>
              <br>

              <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
                  id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${addInstructorPanel.index}"
                  value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>" >
              &nbsp;Manager: Can do everything except for deleting/restoring the course
              <a href="javascript:;" class="view-role-details"
                  data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER%>">
                View Details
              </a>
              <br>

              <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
                  id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${addInstructorPanel.index}"
                  value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER%>">
              &nbsp;Observer: Can only view information(students, submissions, comments etc.).&nbsp;Cannot edit/delete/submit anything.
              <a href="javascript:;" class="view-role-details"
                  data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER %>">
                View Details
              </a>
              <br>

              <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
                  id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${addInstructorPanel.index}"
                  value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR%>">
              &nbsp;Tutor: Can view student details, give/view comments, submit/view responses for sessions
              <a href="javascript:;" class="view-role-details" data-role="<%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR %>">
                View Details
              </a>
              <br>

              <input type="radio" name="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>"
                  id="<%=Const.ParamsNames.INSTRUCTOR_ROLE_NAME%>forinstructor${addInstructorPanel.index}"
                  value="<%=Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM%>">
              &nbsp;Custom: No access by default. Any access needs to be granted explicitly.
            </div>
          </div>

          <div id="tunePermissionsDivForInstructor${addInstructorPanel.index}" style="display: none;">
            <div class="form-group">
              <div class="col-xs-12">
                <div class="panel panel-info">
                  <div class="panel-heading">
                    <strong>In general, this instructor can</strong>
                  </div>

                  <div class="panel-body">
                    <c:forEach items="${addInstructorPanel.permissionInputGroup1}" var="permissionCheckbox">
                      <div class="col-sm-3">
                        <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
                      </div>
                    </c:forEach>
                    <br>
                    <br>

                    <div class="col-sm-6 border-right-gray">
                      <c:forEach items="${addInstructorPanel.permissionInputGroup2}" var="permissionCheckbox">
                        <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
                        <br>
                      </c:forEach>
                    </div>

                    <div class="col-sm-5 col-sm-offset-1">
                      <c:forEach items="${addInstructorPanel.permissionInputGroup3}" var="permissionCheckbox">
                        <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
                        <br>
                      </c:forEach>
                    </div>
                  </div>
                </div>

                <c:forEach items="${addInstructorPanel.sectionRows}" var="sectionRow" varStatus="i">
                  <div id="tuneSectionPermissionsDiv${i.index}ForInstructor${addInstructorPanel.index}" style="display: none;">
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
                            <a href="javascript:;" data-instructorindex="${addInstructorPanel.index}" data-panelindex="${i.index}" class="pull-right hide-tune-section-permissions">
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
                        <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP%>${i.index}set" value="false"/>
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

                        <div id="tuneSessionPermissionsDiv${i.index}ForInstructor${addInstructorPanel.index}" class="row" style="display: none;">
                          <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP%>${i.index}sessionsset" value="false"/>
                          <table class="table table-striped">
                            <thead>
                              <tr>
                                <td>SessionName</td>
                                <td>Submit Responses and Add Comments</td>
                                <td>View Responses and Comments</td>
                                <td>Edit/Delete Responses/Comments by Others</td>
                              </tr>
                            </thead>
                            <tbody>
                              <c:if test="${empty sectionRow.feedbackSessions}">
                                <tr>
                                  <td colspan="4" class="text-center text-bold">No sessions in this course for you to configure</td>
                                </tr>
                              </c:if>

                              <c:forEach items="${sectionRow.feedbackSessions}" var="feedbackSession">
                                <tr>
                                  <td>${feedbackSession.feedbackSessionName}</td>
                                  <td class="align-center">
                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                                        + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP %>${i.index}feedback${feedbackSession.feedbackSessionName}" value="true"/>
                                  </td>
                                  <td class="align-center">
                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                                        + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP %>${i.index}feedback${feedbackSession.feedbackSessionName}" value="true"/>
                                  </td>
                                  <td class="align-center">
                                    <input type="checkbox" name="<%=Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                        + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP %>${i.index}feedback${feedbackSession.feedbackSessionName}" value="true"/>
                                  </td>
                                </tr>
                              </c:forEach>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>
                </c:forEach>
                <c:if test="${not empty addInstructorPanel.sectionRows}">
                  <a href="javascript:;"
                      data-instructorindex="${addInstructorPanel.index}" data-panelindex="0" class="small show-tune-section-permissions"
                      id="addSectionLevelForInstructor${addInstructorPanel.index}">
                    Give different permissions for a specific section
                  </a>
                </c:if>
              </div>
            </div>
          </div>
        </div>

        <div class="form-group">
          <div class="align-center">
            <input id="btnAddInstructor" type="submit" class="btn btn-primary" value="Add Instructor" tabindex="10">
          </div>
        </div>
      </div>
    </form>
  </div>
</div>
