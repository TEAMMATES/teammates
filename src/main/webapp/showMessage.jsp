<%@ page import="teammates.common.util.Const" %>
<html>
<head>
    <script>
        if(typeof opener != 'undefined' || typeof opener != null){
            opener.setStatusMessage("<%=request.getParameter(Const.ParamsNames.STATUS_MESSAGE)%>",<%=request.getParameter(Const.ParamsNames.ERROR)%>);
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
            var nextUrl = "<%=Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE%>";
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