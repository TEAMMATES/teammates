$(document).ready(function(){
	//show on hover for comment
	commentToolBarAppearOnHover();
	
	//open or close show more options
	$('#option-check').click(function(){
		if($('#option-check').is(':checked')){
			$('#more-options').show();
		} else {
			$('#more-options').hide();
		}
	});
	
	//Binding for "Display Archived Courses" check box.
    $("#displayArchivedCourses_check").change(function(){
        var urlToGo = $(location).attr('href');
        if(this.checked){
            gotoUrlWithParam(urlToGo, "displayarchive", "true");
        } else{
            gotoUrlWithParam(urlToGo, "displayarchive", "false");
        }
    });
    
    /**
     * Go to the url with appended param and value pair
     */
    function gotoUrlWithParam(url, param, value){
        var paramValuePair = param + "=" + value;
        if(!url.contains("?")){
            window.location.href = url + "?" + paramValuePair;
        } else if(!url.contains(param)){
            window.location.href = url + "&" + paramValuePair;
        } else if(url.contains(paramValuePair)){
            window.location.href = url;
        } else{
            var urlWithoutParam = removeParamInUrl(url, param);
            gotoUrlWithParam(urlWithoutParam, param, value);
        }
    }

    /**
     * Remove param and its value pair in the given url
     * Return the url withour param and value pair
     */
    function removeParamInUrl(url, param){
        var indexOfParam = url.indexOf("?" + param);
        indexOfParam = indexOfParam == -1? url.indexOf("&" + param): indexOfParam;
        var indexOfAndSign = url.indexOf("&", indexOfParam + 1);
        var urlBeforeParam = url.substr(0, indexOfParam);
        var urlAfterParamValue = indexOfAndSign == -1? "": url.substr(indexOfAndSign);
        return urlBeforeParam + urlAfterParamValue;
    }
    
    /**
     * Check whether a string contains the substr or not
     */
    String.prototype.contains = function(substr) { return this.indexOf(substr) != -1; };
});

function commentToolBarAppearOnHover(){
	$('.comments > .list-group-item').hover(
			function(){
			$("a[type='button']", this).show();
		}, function(){
			$("a[type='button']", this).hide();
		});
}