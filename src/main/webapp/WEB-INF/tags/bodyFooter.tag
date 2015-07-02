<%@ tag description="Body footer (bottom of page)" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Config" %>
<div id="footerComponent" class="container-fluid">
    <div class="container">
        <div class="row">
            <div class="col-md-2">
                <span>[<a href="/index.html">TEAMMATES</a> V<%= Config.inst().getAppVersion() %>]</span>
            </div>
            <div class="col-md-8">
                <c:if test="${not empty data.account.institute}">[for <span class="highlight-white">${data.account.institute}</span>]</c:if>
            </div>
            <div class="col-md-2">
                <span>[Send <a class="link" href="../contact.html" target="_blank">Feedback</a>]</span>
            </div>
        </div>
    </div>
</div>