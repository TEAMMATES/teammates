<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Body footer (bottom of page)" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Config" %>
<%@ attribute name="isAdmin" %>
<div id="footerComponent" class="container-fluid">
  <div class="container">
    <div class="row">
      <div class="col-md-13" <c:if test="${isAdmin}">id="adminInstitute"</c:if>>
        <div class="row">
          <span>
            <c:if test="${not empty data.account.institute}">serving: <span
                    class="highlight-white">${data.account.institute}</span></c:if>
            <br/>
            <a href="/">TEAMMATES</a> (v<%= Config.getAppVersion() %>) is sponsored by School of Computing, National
            University of Singapore
            [<a href="/contact.jsp">Become a sponsor</a>]
            [Send <a class="link" href="/contact.jsp" target="_blank" rel="noopener noreferrer">Feedback</a>]
          </span>
        </div>
      </div>
    </div>
  </div>
</div>
