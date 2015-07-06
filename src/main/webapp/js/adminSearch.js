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
	
	$(".recentActionButton").click(function(e){
		e.stopPropagation();
	});
	
	$('input').click(function() {
		 this.select();
	});
	
	$(".resetGoogleIdButton").click(function(e){
		e.stopPropagation();
	});

});

function submitResetGoogleIdAjaxRequest(studentCourseId, studentEmail, wrongGoogleId, button){
	var params = "studentemail=" + studentEmail
			     + "&courseid=" + studentCourseId
			     + "&googleid=" + wrongGoogleId;
	
	
	var googleIdEntry = $(button).parent().parent().children().find(".homePageLink");
	var originalButton = $(button).html();
	
	var originalGoogleIdEntry = $(googleIdEntry).html();
	
	$.ajax({
        type : 'POST',
        url :   "/admin/adminStudentGoogleIdReset?" + params,
        beforeSend : function() {
        	$(button).html("<img src='/images/ajax-loader.gif'/>");
        },
        error : function() {
        	$(button).html("An Error Occurred, Please Retry");      	
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {   	               	
                	if(data.isGoogleIdReset){
                		googleIdEntry.html("");
                		$(button).hide();
                	} else {
                		googleIdEntry.html(originalGoogleIdEntry);
                		$(button).html(originalButton);
                	}
                	
                	
                } else {
                	$(button).html("An Error Occurred, Please Retry");      	
                }
            	               
                $("#statusMessage").html(data.statusForAjax);

            },500);
        }
    });
}

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