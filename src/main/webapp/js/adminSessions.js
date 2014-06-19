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

function sessionToggleSort(divElement, colIdx, comparator) {

	if ($(divElement).attr("class") == "non-sorted-alpha") {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).attr("class", "ascending-alpha");
		$(divElement).parent().find('span').attr("class",
				"glyphicon glyphicon-sort");
		$(divElement).find('span').attr("class",
				"glyphicon glyphicon-sort-by-alphabet");

	} else if ($(divElement).attr("class") == "ascending-alpha") {
		sortTable(divElement, colIdx, comparator, false);
		$(divElement).attr("class", "descending-alpha");
		$(divElement).parent().find('span').attr("class",
				"glyphicon glyphicon-sort");
		$(divElement).find('span').attr("class",
				"glyphicon glyphicon-sort-by-alphabet-alt");

	} else if ($(divElement).attr("class") == "descending-alpha") {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).attr("class", "ascending-alpha");
		$(divElement).parent().find('span').attr("class",
				"glyphicon glyphicon-sort");
		$(divElement).find('span').attr("class",
				"glyphicon glyphicon-sort-by-alphabet");

	} else if ($(divElement).attr("class") == "non-sorted") {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).attr("class", "ascending");
		$(divElement).parent().find('span').attr("class",
				"glyphicon glyphicon-sort");
		$(divElement).find('span').attr("class",
				"glyphicon glyphicon-sort-by-attributes");

	} else if ($(divElement).attr("class") == "ascending") {
		sortTable(divElement, colIdx, comparator, false);
		$(divElement).attr("class", "descending");
		$(divElement).parent().find('span').attr("class",
				"glyphicon glyphicon-sort");
		$(divElement).find('span').attr("class",
				"glyphicon glyphicon-sort-by-attributes-alt");

	} else {
		sortTable(divElement, colIdx, comparator, true);
		$(divElement).attr("class", "ascending");
		$(divElement).parent().find('span').attr("class",
				"glyphicon glyphicon-sort");
		$(divElement).find('span').attr("class",
				"glyphicon glyphicon-sort-by-attributes");
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

	jQuery('.hoverMenu1').hover(

	function(event) {

		var leftMargin = jQuery('.hoverMenu1').css("left");

		if (!jQuery('.hoverMenu1').is(':animated')) {

			event.preventDefault();
			jQuery('.hoverMenu1').animate({
				"left" : "0em",
				backgroundColor : "rgba(235, 235, 235, 0.80)"
			}, duration);

		}
		return false;
	},

	function(event) {

		event.preventDefault();
		jQuery('.hoverMenu1').animate({
			left : "-5.5em",
			backgroundColor : "rgba(206, 63, 63, 0.80)"

		}, duration);

		return false;
	}

	);

	jQuery('.hoverMenu2').hover(

	function(event) {

		if (!jQuery('.hoverMenu2').is(':animated')) {

			event.preventDefault();
			jQuery('.hoverMenu2').animate({
				"left" : "0em",
				backgroundColor : "rgba(235, 235, 235, 0.80)"
			}, duration);
		}
		return false;
	},

	function(event) {

		event.preventDefault();
		jQuery('.hoverMenu2').animate({
			left : "-7em",
			backgroundColor : "rgba(3, 12, 14, 0.8)"

		}, duration);

		return false;
	}

	);

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

$(function() {
	$(".table-responsive").toggle();

});
