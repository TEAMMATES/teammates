<%@ page import="teammates.ui.controller.Helper" %>
<% Helper helper = (Helper)request.getAttribute("helper"); %>
<%	if(helper.statusMessage!=null) { %>
	<div id="statusMessage"
		style="display: block;<% if(helper.error) out.print(" background-color: rgb(255, 153, 153);"); %>">
		<%= helper.statusMessage %></div>
<%	} else { %>
	<div id="statusMessage" style="display: none;"></div>
<%	} %>