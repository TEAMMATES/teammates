$(document).ready(function() {

	$("#rebuildButton").click(function() {

		$(this).val("true");
	});

	$("#searchButton").click(function() {
		$("#rebuildButton").val("false");
	});

	$(".studentRow").click(function() {

		var rawId = $(this).attr("id");

		$(".fslink" + rawId).toggle();

	});

});

onload = function() {
	$(".fslink").hide();
};