<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="teammates.common.util.Const" %>
<c:set var="contactPage" value="<%= Const.ViewURIs.CONTACT %>" />
<% response.setStatus(500);%>
<t:errorPage>
    <div class="row">
        <div class="alert alert-warning col-md-4 col-md-offset-4">
            <img src="/images/error.png" style="margin: 0px 10px 10px 0px; height: 90px; float: left;">
            <p style="text-align: left;">
                Server failed to respond within a reasonable time. <br>
                This may be due to an unusually high load at this time.<br> 
                Please try again in a few minutes. If the problem persists,<br>
                please inform TEAMMATES <a href="${contactPage}">support team</a>. 
            </p>
        </div>
    </div>
</t:errorPage>