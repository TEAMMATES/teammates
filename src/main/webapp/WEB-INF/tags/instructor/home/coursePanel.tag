<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorHome - Course table panel" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseTable" type="teammates.ui.template.CourseTable" required="true" %>
<%@ attribute name="index" required="true" %>
<c:set var="isNotLoaded" value="${empty courseTable.buttons}" />
<div class="panel panel-primary" id="course-${index}">
  <div class="panel-heading ajax_auto" <c:if test="${isNotLoaded}"> style="cursor: pointer;"</c:if>>
    <div class="row">
      <div class="col-sm-6">
        <strong>
          [${courseTable.courseId}] : ${fn:escapeXml(courseTable.courseName)}
        </strong>
      </div>
      <div class="mobile-margin-top-10px col-sm-6">
        <span class="mobile-no-pull pull-right">
          <c:forEach items="${courseTable.buttons}" var="button">
            <c:choose>
              <c:when test="${fn:length(button.nestedElements) gt 0}">
                <div class="dropdown courses-table-dropdown">
                  <button class="btn btn-primary btn-xs dropdown-toggle" type="button" data-toggle="dropdown">
                    ${button.content}
                    <span class="caret dropdown-toggle margin-left-3px"></span>
                  </button>
                  <ul class="dropdown-menu">
                    <c:forEach items="${button.nestedElements}" var="menuItem">
                      <li>
                        <a data-toggle="tooltip" data-placement="left" ${menuItem.attributesToString}>
                          ${menuItem.content}
                        </a>
                      </li>
                    </c:forEach>
                  </ul>
                </div>
              </c:when>
              <c:otherwise>
                <a data-toggle="tooltip" data-placement="left" ${button.attributesToString}>
                  ${button.content}
                </a>
              </c:otherwise>
            </c:choose>
          </c:forEach>
          <c:if test="${isNotLoaded}">
            <span class="glyphicon glyphicon-chevron-down"></span>
          </c:if>
        </span>
      </div>
    </div>
  </div>
  <c:if test="${isNotLoaded}">
    <form>
      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
      <input type="hidden" name="<%=Const.ParamsNames.COURSE_TO_LOAD%>" value="${courseTable.courseId}">
      <input type="hidden" name="index" value="${index}">
    </form>
  </c:if>
  <jsp:doBody />
  <div class="panel-collapse collapse">
    <div class="panel-body padding-0">
    </div>
  </div>
</div>
