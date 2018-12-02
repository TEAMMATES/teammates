<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Admin sessions - filter bar" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="filter" type="teammates.ui.template.AdminFilter" required="true"%>
<div class="form-group">
  <a href="javascript:;" class="btn btn-link center-block" id="btn-toggle-filter">
    <span id="referenceText">Show filter</span>
    <br>
    <span class="glyphicon glyphicon-chevron-down" id="detailButton"></span>
  </a>
</div>
<form method="get" action="<%=Const.ActionURIs.ADMIN_SESSIONS_PAGE%>">
  <div class="panel panel-primary" id="timeFramePanel">
    <div class="panel-body">
      <div class="row">
        <div class="col-md-4"
            title="<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>"
            data-toggle="tooltip" data-placement="top">
          <div class="row">
            <div class="col-md-6">
              <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>" class="label-control">
                From
              </label>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6">
              <input class="form-control col-sm-2"
                  type="text"
                  name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                  id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                  value="${filter.rangeStart}"
                  placeholder="Date">
            </div>
            <div class="col-md-3">
              <select class="form-control"
                  name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTHOUR%>"
                  id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTHOUR%>">
                <c:forEach items="${filter.rangeStartHourOptions}" var="hourOption">
                  ${hourOption}
                </c:forEach>
              </select>
            </div>
            <div class="col-md-3">
              <select class="form-control"
                  name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTMINUTE%>"
                  id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTMINUTE%>">
                <c:forEach items="${filter.rangeStartMinuteOptions}" var="minuteOption">
                  ${minuteOption}
                </c:forEach>
              </select>
            </div>
          </div>
        </div>
        <div class="col-md-4 border-left-gray"
            title="<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>"
            data-toggle="tooltip" data-placement="top">
          <div class="row">
            <div class="col-md-6">
              <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>" class="label-control">To</label>
            </div>
          </div>
          <div class="row">
            <div class="col-md-6">
              <input class="form-control col-sm-2"
                  type="text"
                  name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                  id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                  value="${filter.rangeEnd}"
                  placeHolder="Date">
            </div>
            <div class="col-md-3">
              <select class="form-control"
                  name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDHOUR%>"
                  id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDHOUR%>">
                <c:forEach items="${filter.rangeEndHourOptions}" var="hourOption">
                  ${hourOption}
                </c:forEach>
              </select>
            </div>
            <div class="col-md-3">
              <select class="form-control"
                  name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDMINUTE%>"
                  id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDMINUTE%>">
                <c:forEach items="${filter.rangeEndMinuteOptions}" var="minuteOption">
                  ${minuteOption}
                </c:forEach>
              </select>
            </div>
          </div>
        </div>
        <div class="col-md-4 border-left-gray">
          <div class="row">
            <div class="col-md-12">
              <label for="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>" class="control-label">
                Time Zone
              </label>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6">
              <select class="form-control"
                  name="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
                  id="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>">
                <c:forEach items="${filter.timeZoneOptions}" var="timeZoneOption">
                  ${timeZoneOption}
                </c:forEach>
              </select>
            </div>
            <div class="col-sm-6">
              <button type="submit" class="btn btn-primary btn-block">
                Filter by Range
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</form>
