<%@ page import="teammates.common.BuildProperties"%>
<%@ page import="teammates.common.datatransfer.UserType"%>
<%@ page import="teammates.ui.controller.Helper"%>
<% 
	Helper helper = (Helper)request.getAttribute("helper"); 
	String version = BuildProperties.getAppVersion();
	String institute = "";
	//Set institute only if both helper and account are available. 
	//  helper is not available for pages such as generic error pages.
	//  account may not be available for admin.
	if((helper!= null) && (helper.account != null)){
		institute = "[for <span class=\"color_white\">"+helper.account.institute+"</span>]";
	}
%>
<div id="contentFooter">
	<span class="floatleft">[TEAMMATES <span class="color_white">V<%=version%></span>]</span>
	<%=institute%>
	<span class="floatright">[Send <span class="color_white"><a href="../contact.html" target="_blank">Feedback</a></span>]</span>
</div>