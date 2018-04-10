<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Body footer (bottom of page)" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Config" %>
<%@ attribute name="isAdmin" %>
<div id="footerComponent" class="container-fluid">
  <div class="container">
    <div class="row">
      <div class="col-md-2">
        <span>[<a href="/">TEAMMATES</a> V<%= Config.getAppVersion() %>]</span>
      </div>
      <div class="col-md-8" <c:if test="${isAdmin}">id="adminInstitute"</c:if>>
        <c:if test="${not empty data.account.institute}">[for <span class="highlight-white">${data.account.institute}</span>]</c:if>
      </div>
      <div class="col-md-2">
        <span>[Send <a class="link" href="/contact.jsp" target="_blank" rel="noopener noreferrer">Feedback</a>]</span>
      </div>
    </div>
  </div>
</div>
