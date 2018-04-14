<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedback & instructorFeedbackEdit - feedback session form" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/feedbacks" prefix="feedbacks" %>

<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<%@ attribute name="fsForm" type="teammates.ui.template.FeedbackSessionsForm" required="true"%>
<%@ attribute name="fsEnableEdit" %>
<%@ attribute name="courseName" %>
<%@ attribute name="courseAttributes" type="java.util.List" %>
<%@ attribute name="fsAttributes" type="teammates.common.datatransfer.attributes.FeedbackSessionAttributes" %>

<div class="well well-plain">
  <form class="form-group" method="post"
      action="${fsForm.formSubmitAction}"
      id="form_feedbacksession"
      <c:if test="${not empty fsEnableEdit}">
        data-<%= Const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT %>="${fsEnableEdit}"
      </c:if>>

    <feedbacks:feedbackSessionsFormHeader fsForm="${fsForm}" />

    <br>
    <div class="panel panel-primary">
      <div class="panel-body">
        <div class="row">
          <div class="col-sm-12 col-md-6"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_COURSE %>"
              data-toggle="tooltip"
              data-placement="top">
            <div class="form-group<c:if test="${fsForm.showNoCoursesMessage}"> has-error</c:if>">
              <h5 class="col-sm-2 col-md-4">
                <label class="control-label" for="<%= Const.ParamsNames.COURSE_ID %>">
                  Course ID
                </label>
              </h5>
              <div class="col-sm-10 col-md-8">
                <c:choose>
                  <c:when test="${fsForm.courseIdEditable}">
                    <select class="form-control<c:if test="${fsForm.showNoCoursesMessage}"> text-color-red</c:if>"
                        name="<%= Const.ParamsNames.COURSE_ID %>"
                        id="<%= Const.ParamsNames.COURSE_ID %>">
                      <c:forEach items="${fsForm.coursesSelectField}" var="option">
                        <option ${option.attributesToString}>${option.content}</option>
                      </c:forEach>
                    </select>
                  </c:when>
                  <c:otherwise>
                    <h5 class="form-control-static font-weight-normal">
                      ${fsForm.courseId}
                    </h5>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
          <div class="col-sm-12 col-md-6 tablet-no-mobile-margin-top-20px"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_TIMEZONE %>"
              data-toggle="tooltip"
              data-placement="top">
            <div class="form-group">
              <h5 class="col-sm-2 col-md-4">
                <label class="control-label" for="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>">
                  Time Zone
                </label>
              </h5>
              <div class="col-sm-10 col-md-8">
                <h5 id="<%= Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE %>" class="form-control-static font-weight-normal"
                    data-time-zone="${fsForm.fsTimeZone}">
                  ${fsForm.fsTimeZone}
                </h5>
              </div>
            </div>
          </div>
        </div>
        <br class="hidden-xs">
        <div class="row">
          <div class="col-sm-12 col-md-6">
            <div class="form-group">
              <h5 class="col-sm-2 col-md-4">
                <label class="control-label" for="<%= Const.ParamsNames.COURSE_NAME %>">
                  Course name
                </label>
              </h5>
              <div class="col-sm-10 col-md-8">
                <c:choose>
                  <c:when test="${fsForm.courseIdEditable}">
                    <c:forEach items="${courseAttributes}" var="attributes">
                      <input class="course-attributes-data" id="${fn:escapeXml(attributes.id)}" type="hidden"
                          data-name="${fn:escapeXml(attributes.name)}" data-time-zone="${attributes.timeZone.id}">
                    </c:forEach>
                    <h5 id="<%= Const.ParamsNames.COURSE_NAME %>" class="form-control-static font-weight-normal">
                    </h5>
                  </c:when>
                  <c:otherwise>
                    <h5 id="<%= Const.ParamsNames.COURSE_NAME %>" class="form-control-static font-weight-normal">
                      ${courseName}
                    </h5>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
        </div>
        <br class="hidden-xs">
        <div class="row">
          <div class="col-sm-12 col-md-6"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_NAME %>"
              data-toggle="tooltip"
              data-placement="top">
            <div class="form-group">
              <h5 class="col-sm-2 col-md-4">
                <label class="control-label" for="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>">
                  Session name
                </label>
              </h5>
              <div class="col-sm-10 col-md-8">
                <c:choose>
                  <c:when test="${fsForm.fsNameEditable}">
                    <input class="form-control" type="text"
                        name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                        id="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>"
                        maxlength="<%= FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH %>"
                        placeholder="e.g. Feedback for Project Presentation 1"
                        value="${fsForm.fsName}">
                  </c:when>
                  <c:otherwise>
                    <h5 class="form-control-static font-weight-normal">
                      ${fsForm.fsName}
                    </h5>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
        </div>
        <br class="hidden-xs">
        <div class="row" id="instructionsRow">
          <div class="col-sm-12"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_INSTRUCTIONS %>"
              data-toggle="tooltip"
              data-placement="top">
            <div class="form-group">
              <h5 class="col-sm-2 margin-top-0">
                <label class="control-label" for="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>">
                  Instructions
                </label>
              </h5>
              <div class="col-sm-10">
                <div id="<%= Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS %>" class="panel panel-default panel-body">
                  ${fsForm.instructions}
                </div>
              </div>
            </div>
          </div>
        </div>
        <c:choose>
          <c:when test="${!fsForm.courseIdEditable}">
            <div class="row">
              <div class="col-sm-12 col-md-6">
                <div class="form-group">
                  <h5 class="col-sm-2 col-md-4">
                    <label class="control-label">
                      Submission Status
                    </label>
                  </h5>
                  <div class="col-sm-10 col-md-8">
                    <h5>${fsForm.submissionStatus}</h5>
                  </div>
                </div>
              </div>
              <div class="col-sm-12 col-md-6 tablet-no-mobile-margin-top-20px">
                <div class="form-group">
                  <h5 class="col-sm-2 col-md-4">
                    <label class="control-label">
                      Published Status
                    </label>
                  </h5>
                  <div class="col-sm-10 col-md-8">
                    <h5>${fsForm.publishedStatus}</h5>
                  </div>
                </div>
              </div>
            </div>
            <br class="hidden-xs">
          </c:when>
        </c:choose>
      </div>
    </div>
    <div class="panel panel-primary" id="timeFramePanel">
      <div class="panel-body">
        <div class="row">
          <div class="col-md-5"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_STARTDATE %>"
              data-toggle="tooltip"
              data-placement="top">
            <div class="row">
              <div class="col-xs-12">
                <label class="label-control" for="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>">
                  Submission opening time
                </label>
              </div>
            </div>
            <div class="row">
              <div class="col-xs-6">
                <input class="form-control col-sm-2" type="text"
                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>"
                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTDATE %>"
                    value="${fsForm.fsStartDate}"
                    placeholder="Date">
              </div>
              <div class="col-xs-6">
                <select class="form-control"
                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>"
                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_STARTTIME %>">
                  <c:forEach items="${fsForm.fsStartTimeOptions}" var="option">
                    <option ${option.attributesToString}>
                      ${option.content}
                    </option>
                  </c:forEach>
                </select>
              </div>
            </div>
          </div>
          <div class="col-md-5 border-left-gray"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_ENDDATE %>"
              data-toggle="tooltip"
              data-placement="top">
            <div class="row">
              <div class="col-xs-12">
                <label class="label-control" for="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>">
                  Submission closing time
                </label>
              </div>
            </div>
            <div class="row">
              <div class="col-xs-6">
                <input class="form-control col-sm-2" type="text"
                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>"
                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDDATE %>"
                    value="${fsForm.fsEndDate}"
                    placeHolder="Date">
              </div>
              <div class="col-xs-6">
                <select class="form-control"
                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>"
                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_ENDTIME %>">
                  <c:forEach items="${fsForm.fsEndTimeOptions}" var="option">
                    <option ${option.attributesToString}>
                      ${option.content}
                    </option>
                  </c:forEach>
                </select>
              </div>
            </div>
          </div>
          <div class="col-md-2 border-left-gray"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_INPUT_GRACEPERIOD %>"
              data-toggle="tooltip"
              data-placement="top">
            <div class="row">
              <div class="col-xs-12">
                <label class="control-label" for="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>">
                  Grace period
                </label>
              </div>
            </div>
            <div class="row">
              <div class="col-xs-12">
                <select class="form-control"
                    name="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>"
                    id="<%= Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD %>">
                  <c:forEach items="${fsForm.gracePeriodOptions}" var="option">
                    <option ${option.attributesToString}>
                      ${option.content}
                    </option>
                  </c:forEach>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div id="uncommonSettingsSection">
      <feedbacks:feedbackSessionsFormAdditionalSettings additionalSettings="${fsForm.additionalSettings}"/>

      <div id="uncommonSettingsSessionResponsesVisible" class="margin-bottom-15px text-muted">
        <span id="uncommonSettingsSessionResponsesVisibleInfoText"></span>
        <a class="edit-uncommon-settings-button enable-edit-fs"
            id="editUncommonSettingsSessionResponsesVisibleButton"
            data-edit="[Edit]" data-done="[Done]">
          [Change]
        </a>
      </div>

      <div id="uncommonSettingsSendEmails" class="margin-bottom-15px text-muted">
        <span id="uncommonSettingsSendEmailsInfoText"></span>
        <a class="edit-uncommon-settings-button enable-edit-fs"
            id="editUncommonSettingsSendEmailsButton"
            data-edit="[Edit]" data-done="[Done]">
          [Change]
        </a>
      </div>
    </div>

    <div class="form-group">
      <div class="row">
        <div class="col-md-offset-5 col-md-3">
          <button id="button_submit" type="submit" class="btn btn-primary"
              <c:if test="${fsForm.submitButtonDisabled}">disabled</c:if>
              <c:if test="${!fsForm.submitButtonVisible}"> style="display:none;" </c:if>>
            ${fsForm.submitButtonText}
          </button>
        </div>
      </div>
    </div>
    <c:if test="${fsForm.showNoCoursesMessage}">
      <div class="row">
        <div class="col-md-12 text-center">
          <b>You need to have an active(unarchived) course to create a session!</b>
        </div>
      </div>
    </c:if>
    <c:if test="${!fsForm.fsNameEditable}">
      <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${fsForm.fsName}">
    </c:if>
    <c:if test="${!fsForm.courseIdEditable}">
      <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${fsForm.courseId}">
    </c:if>
    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
    <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN%>" value="${data.sessionToken}">
  </form>
</div>
