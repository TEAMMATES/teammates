<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.jsp.Helper" %>
<html>
<head>
	<script>
		opener.setStatusMessage("<%= request.getParameter(Common.PARAM_STATUS_MESSAGE) %>",<%= request.getParameter(Common.PARAM_ERROR) %>);
		top.close();
	</script>
</head>
</html>