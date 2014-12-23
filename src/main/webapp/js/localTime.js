$(document).ready(function() {    
    
    $(document).on("submit", "form", (function(eventObj){           
         var now = new Date();     
         $('<input />').attr('type', 'hidden')
         			   .attr('name',  "localTime")
                       .attr('value', now.toLocaleString())
                       .appendTo($(this));
         return true;
                
    }));
    
    $(document).on("click", "a", (function(){
    	var url = $(this).attr("href");
    	if(url != undefined && url != false){ 	
	    	var now = new Date();     
	    	var newUrl = replaceUrlParam(url, "localTime", now.toLocaleString());
	    	$(this).attr("href", newUrl);
    	}
    }));
    
});

function replaceUrlParam(url, paramName, paramValue){
    var pattern = new RegExp('('+paramName+'=).*?(&|$)');
    var newUrl=url;
    if(url.search(pattern)>=0){
        newUrl = url.replace(pattern,'$1' + paramValue + '$2');
    }
    else{
        newUrl = newUrl + (newUrl.indexOf('?')>0 ? '&' : '?') + paramName + '=' + paramValue; 
    }
    return newUrl;
}
