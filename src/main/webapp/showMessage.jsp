<%@ page import="teammates.api.Common" %>
<html>
<head>
	<script>
		if(typeof opener !== 'undefined'){
			opener.setStatusMessage("<%= request.getParameter(Common.PARAM_STATUS_MESSAGE) %>",<%= request.getParameter(Common.PARAM_ERROR) %>);
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
			var nextUrl = "<%= Common.PAGE_COORD_EVAL %>";
			if(user!==null){
				nextUrl += "?user="+user;
			}
			window.location.href = nextUrl;
		}
	</script>
</head>
</html>