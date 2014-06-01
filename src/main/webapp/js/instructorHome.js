$(document).ready(function(){
    
    //Click event binding for radio buttons
    var radiobuttons = $("label[name='sortby']");
    $.each(radiobuttons, function() {
        $(this).click(function (){
            var currentPath = window.location.pathname;
            var query = window.location.search.substring(1);
            var params = {};
            
            var param_values = query.split("&");
            for(var i=0;i<param_values.length;i++){
                var param_value = param_values[i].split("=");
                params[param_value[0]] = param_value[1];
            }

            if ("user" in params == false) {
                params["user"] = $("input[name='user']").val();
            }

            console.log(currentPath+"?user="+params["user"])+"&sortby="+$(this).attr("data");
            window.location.href = currentPath+"?user="+params["user"]+"&sortby="+$(this).attr("data");
        });
    });
});