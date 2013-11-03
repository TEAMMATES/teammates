$(document).ready(function(){
	
	var responseRateClickHandler = function(e) {
		var hyperlinkObject = $(this).clone(),
		parentOfHyperlinkObject = $(this).parent();
		e.preventDefault();
		$.ajax({
			type : 'POST',
			url : 	hyperlinkObject.attr('href'),
			beforeSend : function() {
				parentOfHyperlinkObject.html("<img src='/images/ajax-loader.gif'/>");
			},
			error : function() {
				parentOfHyperlinkObject.html("Failed. ")
										.append(hyperlinkObject);
				hyperlinkObject.html("Try again?");
				hyperlinkObject.click(responseRateClickHandler);
				hyperlinkObject.mouseover(function() {
					ddrivetip("Error occured while trying to fetch response rate. Click to retry.");
				});
				hyperlinkObject.mouseout(function() {
					hideddrivetip();
				});
			},
			success :function(data) {
				setTimeout(function(){
					var type = (data.sessionDetails == undefined) ? "evaluationDetails" : "sessionDetails";
					parentOfHyperlinkObject.html(data[type].stats.submittedTotal +
							" / " + data[type].stats.expectedTotal);
				},500);
			}
		});
	};
	$("td[class*='t_session_response'] > a").click(responseRateClickHandler);
	
	$(".dataTable").each(function(idx) {
	    //this is bound to current object in question
	    var currentTable = $(this).has("tbody").length != 0 ? $(this).find("tbody") : $(this); 
	        store = null;
	    
	    var allRows = currentTable.find("tr:has(td)");
	    var recentElements = allRows.filter(function(i){
	        return $(allRows[i]).find("td[class*='recent']").length != 0;
	    }),
	        nonRecentElements = allRows.filter(function(i){
	        return $(allRows[i]).find("td[class*='recent']").length == 0;
	    });
	    
	    store = $.merge(recentElements,nonRecentElements);
	    
	    for(var i=0; i < store.length; i++ ) {
	        currentTable.get(0).appendChild(store[i]);
	    }
	});
	
	//recent class will only be appended to 'td' element with class 't_session_response'
	$(".dataTable .recent a").each(function(idx) {
	    var currentObject = $(this);
	    
	    currentObject.click();
	});
});