$(document).ready(function() {

	$("#rebuildButton").click(function() {
		
		$(this).val("true");
	});
	
	$("#searchButton").click(function() {		
		$("#rebuildButton").val("false");
	});

});
