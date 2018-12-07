<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Body header (top of page)" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="title" required="true" %>
<div id="topOfPage"></div>
<c:if test="${not empty title}">
  <h1>${title}</h1>
  <br>
</c:if>
