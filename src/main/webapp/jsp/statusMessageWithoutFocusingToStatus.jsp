<%@ page import="teammates.ui.controller.PageData" %>
<%@ page import="teammates.common.util.Const" %>
<%
	boolean isError = Boolean.parseBoolean((String)request.getAttribute(Const.ParamsNames.ERROR));
%>
<%
	String statusMessage = (String)request.getAttribute(Const.ParamsNames.STATUS_MESSAGE);
%>
<%
	if(!statusMessage.isEmpty()) {
%>
	<div id="statusMessage"
		style="display: block;<%if(isError) out.print(" background-color: rgb(255, 153, 153);");%>">
		<%=statusMessage%></div>
<%	} else { %>
	<div id="statusMessage" style="display: none;"></div>
<%	} %>