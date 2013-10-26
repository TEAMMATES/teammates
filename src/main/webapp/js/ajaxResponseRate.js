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
});