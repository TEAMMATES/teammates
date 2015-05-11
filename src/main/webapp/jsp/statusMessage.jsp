<%@ page import="teammates.ui.controller.PageData" %>
<%@ page import="teammates.common.util.Const" %>
<%
    boolean isError = Boolean.parseBoolean((String)request.getAttribute(Const.ParamsNames.ERROR));
%>
<%
    String statusMessage = (String)request.getAttribute(Const.ParamsNames.STATUS_MESSAGE);
%>
<%
    if (!statusMessage.isEmpty()) {
%>
        <div id="statusMessage"
            <%
                if (isError) { 
                    out.print(" class=\"alert alert-danger\""); 
                } else {
                    out.print(" class=\"alert alert-warning\""); 
                }
            %>
        >
            <%= statusMessage %>
        </div>
        
        <script type="text/javascript">
            document.getElementById( 'statusMessage' ).scrollIntoView();
        </script>
<%    
    } else { 
%>
        <div id="statusMessage" style="display: none;"></div>
<%    
    } 
%>