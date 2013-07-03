<%@ page import="teammates.common.util.Constants" %>
<html>
<head>
	<script>
		if(typeof opener != 'undefined' || typeof opener != null){
			opener.setStatusMessage("<%=request.getParameter(Constants.PARAM_STATUS_MESSAGE)%>",<%=request.getParameter(Constants.PARAM_ERROR)%>);
			top.close();
			opener.scrollToTop();
		} else {
			var curURL = window.location.href;
			var splitted = curURL.split("?");
			var user = null;
			if(splitted.length>1){
				var param = splitted[1].split("&");
				for(var i=0; i<param.length; i++){
					var pair = param[i].split("=");
					if(pair[0]=="user"){
						user = pair[1];
					}
				}
			}
			var nextUrl = "<%=Constants.ACTION_INSTRUCTOR_EVALS%>";
			if(user!==null){
				nextUrl += "?user="+user;
			}
			window.location.href = nextUrl;
		}
	</script>
</head>
<body>
This browser window is expected to close automatically (if JavaSrcipt is enabled). If it doesn't, you may close it manually.
</body>
</html>