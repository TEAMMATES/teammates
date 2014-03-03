<%@ page import="teammates.common.util.Config"%>
<%@ page import="teammates.common.datatransfer.UserType"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%
	PageData data = (PageData)request.getAttribute("data"); 
	String version = Config.inst().getAppVersion();
	String institute = "";
	//Set institute only if both helper and account are available. 
	//  helper is not available for pages such as generic error pages.
	//  account may not be available for admin.
	if((data!= null) && (data.account != null) && (data.account.institute != null)){
		institute = "[for <span class=\"color_white\">"+data.account.institute+"</span>]";
	}
%>
<div id="contentFooter">
	<span class="floatleft">[<span class="color_white">TEAMMATES </span>V<%=version%>]</span>
	<%=institute%>
	<span class="floatright">[Send <span class="color_white"><a href="../contact.html" target="_blank">Feedback</a></span>]</span>
</div>