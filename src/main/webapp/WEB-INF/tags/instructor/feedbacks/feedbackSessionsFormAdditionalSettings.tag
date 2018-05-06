<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - new feedback session form additional settings" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.common.util.EmailType" %>

<%@ attribute name="additionalSettings" type="teammates.ui.template.FeedbackSessionsAdditionalSettingsFormSegment" required="true"%>
<div class="panel panel-primary" style="display:none;" id="sessionResponsesVisiblePanel">
  <div class="panel-body">
    <div class="row">
      <div class="col-xs-12 col-md-6">
        <div class="row">
          <div class="col-xs-12"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLELABEL %>"
              data-toggle="tooltip"
              data-placement="top">
            <label class="label-control">Make session visible </label>
          </div>
        </div>
        <div class="row radio">
          <h5 class="col-xs-2"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_VISIBLEDATE %>"
              data-toggle="tooltip"
              data-placement="top">
            <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_custom">
              At
            </label>
            <input type="radio"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_custom"
                value="<%= Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM %>"
                <c:if test="${additionalSettings.sessionVisibleDateButtonChecked}">checked=""</c:if>>
          </h5>
          <div class="col-xs-5">
            <input class="form-control col-sm-2" type="text"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE %>"
                value="${additionalSettings.sessionVisibleDateValue}"
                <c:if test="${additionalSettings.sessionVisibleDateDisabled}">disabled</c:if>>
          </div>
          <div class="col-xs-5">
            <select class="form-control"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME %>"
                <c:if test="${additionalSettings.sessionVisibleDateDisabled}">disabled</c:if>>
              <c:forEach items="${additionalSettings.sessionVisibleTimeOptions}" var="option">
                <option ${option.attributesToString}>
                  ${option.content}
                </option>
              </c:forEach>
            </select>
          </div>
        </div>
        <div class="row radio">
          <div class="col-xs-12"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_SESSIONVISIBLEATOPEN %>"
              data-toggle="tooltip"
              data-placement="top">
            <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_atopen">
              Submission opening time
            </label>
            <input type="radio"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON %>_atopen"
                value="<%= Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN %>"
                <c:if test="${additionalSettings.sessionVisibleAtOpenChecked}">checked=""</c:if>>
          </div>
        </div>
      </div>

      <div class="col-xs-12 col-md-6 border-left-gray" id="responsesVisibleFromColumn">
        <div class="row">
          <div class="col-xs-12"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELABEL %>"
              data-toggle="tooltip"
              data-placement="top">
            <label class="label-control">Make responses visible</label>
          </div>
        </div>
        <div class="row radio">
          <h5 class="col-xs-2"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLECUSTOM %>"
              data-toggle="tooltip"
              data-placement="top">
            <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_custom">
              At
            </label>
            <input type="radio"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_custom"
                value="<%= Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM %>"
                <c:if test="${additionalSettings.responseVisibleDateChecked}">checked=""</c:if>>
          </h5>
          <div class="col-xs-5">
            <input class="form-control"
                type="text"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE %>"
                value="${additionalSettings.responseVisibleDateValue}"
                <c:if test="${additionalSettings.responseVisibleDateDisabled}">disabled</c:if>>
          </div>
          <div class="col-xs-5">
            <select class="form-control"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME %>"
                title="<%= Const.Tooltips.FEEDBACK_SESSION_PUBLISHDATE %>"
                data-toggle="tooltip"
                data-placement="top"
                <c:if test="${additionalSettings.responseVisibleDateDisabled}">disabled</c:if>>
              <c:forEach items="${additionalSettings.responseVisibleTimeOptions}" var="option">
                <option ${option.attributesToString}>
                  ${option.content}
                </option>
              </c:forEach>

            </select>
          </div>
        </div>
        <div class="row radio">
          <div class="col-xs-12"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLEATVISIBLE %>"
              data-toggle="tooltip"
              data-placement="top">
            <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_atvisible">
              Immediately
            </label>
            <input type="radio"
                name="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_atvisible"
                value="<%= Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE %>"
                <c:if test="${additionalSettings.responseVisibleImmediatelyChecked}">checked=""</c:if>>
          </div>
        </div>
        <div class="row radio">
          <div class="col-xs-12"
              title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTSVISIBLELATER %>"
              data-toggle="tooltip"
              data-placement="top">
            <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_later">
              Not now (publish manually)
            </label>
            <input type="radio" name="resultsVisibleFromButton"
                id="<%= Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON %>_later"
                value="<%= Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER %>"
                <c:if test="${additionalSettings.responseVisiblePublishManuallyChecked}">checked=""</c:if>>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="panel panel-primary" style="display:none;" id="sendEmailsForPanel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-12">
        <label class="control-label">Send emails for</label>
      </div>
    </div>
    <div class="row">
      <div class="col-md-3"
          title="<%= Const.Tooltips.FEEDBACK_SESSION_SENDOPENEMAIL %>"
          data-toggle="tooltip"
          data-placement="top">
        <div class="checkbox">
          <label>Session opening reminder</label>
          <input type="checkbox"
              name="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>"
              id="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_open"
              <c:if test="${additionalSettings.sendOpeningEmailChecked}">checked=""</c:if>
              value="<%= EmailType.FEEDBACK_OPENING.toString() %>" disabled>
        </div>
      </div>
      <div class="col-md-3"
          title="<%= Const.Tooltips.FEEDBACK_SESSION_SENDCLOSINGEMAIL %>"
          data-toggle="tooltip"
          data-placement="top">
        <div class="checkbox">
          <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_closing">
            Session closing reminder
          </label>
          <input type="checkbox"
              name="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>"
              id="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_closing"
              <c:if test="${additionalSettings.sendClosingEmailChecked}">checked=""</c:if>
              value="<%= EmailType.FEEDBACK_CLOSING.toString() %>">
        </div>
      </div>
      <div class="col-md-4"
          title="<%= Const.Tooltips.FEEDBACK_SESSION_SENDPUBLISHEDEMAIL %>"
          data-toggle="tooltip"
          data-placement="top">
        <div class="checkbox">
          <label for="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_published">
            Results published announcement
          </label>
          <input type="checkbox"
              name="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>"
              id="<%= Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL %>_published"
              <c:if test="${additionalSettings.sendPublishedEmailChecked}">checked=""</c:if>
              value="<%= EmailType.FEEDBACK_PUBLISHED.toString() %>">
        </div>
      </div>
    </div>
  </div>
</div>
