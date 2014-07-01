function toggleContent(id) {

	var duration = 500;

	$("#table_" + id).slideToggle("slow");

	var pill = $("#pill_" + id).attr("class");

	if (pill == 'active') {
		$("#pill_" + id).attr("class", " ");
		jQuery('#badge_' + id).fadeIn(duration);
	} else {
		$("#pill_" + id).attr("class", "active");
		jQuery('#badge_' + id).fadeOut(duration);
	}

}

function openAllSections(count) {

	for (var i = 1; i <= count; i++) {

		var pill = $("#pill_" + i).attr("class");
		if (pill != 'active') {
			toggleContent(i);
		}
	}

}

function closeAllSections(count) {

	for (var i = 1; i <= count; i++) {

		var pill = $("#pill_" + i).attr("class");
		if (pill == 'active') {
			toggleContent(i);
		}
	}

}


jQuery(document).ready(function() {

	var offset = 220;
	var duration = 500;
	jQuery(window).scroll(function() {
		if (jQuery(this).scrollTop() > offset) {
			jQuery('.back-to-top-left').fadeIn(duration);
			jQuery('.back-to-top-right').fadeIn(duration);
		} else {
			jQuery('.back-to-top-left').fadeOut(duration);
			jQuery('.back-to-top-right').fadeOut(duration);
		}
	});

	jQuery('.back-to-top-left').click(function(event) {
		event.preventDefault();
		jQuery('html, body').animate({
			scrollTop : 0
		}, duration);
		return false;
	});

	jQuery('.back-to-top-right').click(function(event) {
		event.preventDefault();
		jQuery('html, body').animate({
			scrollTop : 0
		}, duration);
		return false;
	});

});

function toggleFilter() {
	$("#timeFramePanel").slideToggle("slow");
	
	var button = $("#detailButton").attr("class");
	
	if(button == "glyphicon glyphicon-chevron-down"){
	$("#detailButton").attr("class","glyphicon glyphicon-chevron-up");
	$("#referenceText").text("Hide Filter");
	}else{
		$("#detailButton").attr("class","glyphicon glyphicon-chevron-down");
		$("#referenceText").text("Show Filter");
	}
}

$(function() {
	$("#timeFramePanel").toggle();
});
