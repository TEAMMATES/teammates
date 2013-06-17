<%@ page import="teammates.ui.controller.PageData" %>
<%@ page import="teammates.common.Common" %>
<% boolean isError = Boolean.parseBoolean((String)request.getAttribute(Common.PARAM_ERROR)); %>
<% String statusMessage = (String)request.getAttribute(Common.PARAM_STATUS_MESSAGE); %>
<%
	if(!statusMessage.isEmpty()) {
%>
	<div id="statusMessage"
		style="display: block;<%if(isError) out.print(" background-color: rgb(255, 153, 153);");%>">
		<%=statusMessage%></div>
	<script type="text/javascript">
    	document.getElementById( 'statusMessage' ).scrollIntoView();
    </script>
<%	} else { %>
	<div id="statusMessage" style="display: none;"></div>
<%	} %>