<%@ page import="teammates.common.util.Const"%>

<div>

    <input type="hidden" name="<%=Const.ParamsNames.LOCAL_TIME%>" class="currentLocalTime">

    <script type="text/javascript">
    
    $("form").submit(function(e){
    	var now = new Date();
    	$(".currentLocalTime").val(now.toLocaleString());
    });
    
    </script>
</div>