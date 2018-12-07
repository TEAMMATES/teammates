<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Student Message of the day" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Config" %>
<c:set var="motdUrl" value="<%= Config.STUDENT_MOTD_URL %>" />
<c:if test="${not empty motdUrl}">
  <div id="student-motd-wrapper">
    <input type="hidden" id="motd-url" value="<c:out value="${motdUrl}" />">
    <script type="text/javascript" src="/js/studentMotd.js" defer></script>
    <div class="theme-showcase" id="student-motd-container">
      <div class="row">
        <div class="col-sm-12">
          <div class="panel panel-default">
            <div class="panel-body padding-top-0">
              <div class="row">
                <div class="col-sm-12">
                  <p class="padding-15px margin-0">
                    <b class="text-color-gray">TEAMMATES Message of the day</b>
                    &nbsp;
                    <button type="button" id="btn-close-motd" class="close" aria-label="Close">
                      <span aria-hidden="true">&times;</span>
                    </button>
                  </p>
                </div>
              </div>
              <div class="row">
                <div class="col-sm-12" id="student-motd">
                  <%-- Message of the day loads here --%>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</c:if>
