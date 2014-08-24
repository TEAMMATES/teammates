$(document).ready(function() {

	$("#rebuildButton").click(function() {

		$(this).val("true");
	});

	$("#searchButton").click(function() {
		$("#rebuildButton").val("false");
	});

	$(".studentRow").click(function() {

		var rawId = $(this).attr("id");
		if($(this).attr("class") == "studentRow active"){
			$(this).attr("class", "studentRow");
		} else{
			$(this).attr("class", "studentRow active");
		}
		$(".fslink" + rawId).toggle();

	});
	
	$(".instructorRow").click(function() {

		var rawId = $(this).attr("id");
		if($(this).attr("class") == "instructorRow active"){
			$(this).attr("class", "instructorRow");
		} else{
			$(this).attr("class", "instructorRow active");
		}
		$(".fslink" + rawId).toggle();

	});
	
	$(".homePageLink").click(function(e){		
		e.stopPropagation();
	});
	
	$(".detailsPageLink").click(function(e){		
		e.stopPropagation();
	});
	
	$('input').click(function() {
		 this.select();
	});

});

function adminSearchDiscloseAll(){
	
	$(".fslink").slideDown();	
	$(".studentRow").attr("class", "studentRow active");
	$(".instructorRow").attr("class", "instructorRow active");
	
}

function adminSearchCollapseAll(){
	$(".fslink").hide();
	$(".studentRow").attr("class", "studentRow");
	$(".instructorRow").attr("class", "instructorRow");
}

onload = function() {
	$(".fslink").hide();
};