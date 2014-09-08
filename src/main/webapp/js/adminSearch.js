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

function adminSearchDiscloseAllStudents(){
	
	$(".fslink_student").slideDown();	
	$(".studentRow").attr("class", "studentRow active");
	
}

function adminSearchCollapseAllStudents(){
	$(".fslink_student").hide();
	$(".studentRow").attr("class", "studentRow");
}

function adminSearchDiscloseAllInstructors(){
	$(".fslink_instructor").slideDown();
	$(".instructorRow").attr("class", "instructorRow active");
}

function adminSearchCollapseAllInstructors(){
	$(".fslink_instructor").hide();
	$(".instructorRow").attr("class", "instructorRow");
}


onload = function() {
	$(".fslink").hide();
};