<%@ page import="teammates.ui.Helper" %>
<% Helper helper = (Helper)request.getAttribute("helper"); %>
<%	if(helper.statusMessage!=null) { %>
	<div id="statusMessage"
		style="display:block;<% if(helper.error) out.print("background:#FF9999"); %>">
		<%= helper.statusMessage %></div>
<%	} else { %>
	<div id="statusMessage" style="display: none"></div>
<%	} %>