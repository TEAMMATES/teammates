<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.common.datatransfer.UserType" %>
<%@ page import="teammates.ui.controller.PageData" %>

<%
    PageData data = (PageData)request.getAttribute("data");
    String version = Config.inst().getAppVersion();
    String institute = "";

    /** Set institute only if both helper and account are available.
     *  helper is not available for pages such as generic error pages.
     *  account may not be available for admin.
     */
    if ((data!= null) && (data.account != null) && (data.account.institute != null)) {
        institute = "<span class=\"color_white\">" + data.account.institute + "</span>";
    }
%>

<div id="footerComponent" class="container-fluid">
    <div class="container">
        <div class="row">
            <div class="col-md-2">
                <span>[<a href="/index.html">TEAMMATES</a> V<%= version %>]</span>
            </div>
            <div class="col-md-8">
                <%
                    if (institute != null && institute != "") {
                %>
                        [for <span class="highlight-white"><%= institute %></span>]
                <% } %>
            </div>
            <div class="col-md-2">
                <span>[Send <a class="link" href="../contact.html" target="_blank">Feedback</a>]</span>
            </div>
        </div>
    </div>
</div>
