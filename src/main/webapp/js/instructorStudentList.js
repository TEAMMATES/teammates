$(document).ready(function(){
	
	//On page load, if the searchKey param exist, applyFilters.
	if($("#searchbox").val()){
		applyFilters();
	}
	
	//Binding for live search
	$('input#searchbox').keyup(function(e){
		applyFilters();
	});
	
	//Binding for "Show More Options" check box.
	$("#option_check").change(function(){
		if(this.checked){
			$("#optionsTable").show();
		} else{
			$("#optionsTable").hide();
		}
	});
	
	//Binding for "Show Emails" check box.
	$("#show_email").change(function(){
		if(this.checked){
			$("#emails").show();
		} else{
			$("#emails").hide();
		}
		filterEmails();
	});
	
	//Binding for changes in the Courses checkboxes.
	$("input[id^=course_check]").change(function(){
		var $courseIdx = $(this).attr("id").split('-')[1];
		
		//Check/hide all teams that is in this course
		if(this.checked){
			$("input[id^=team_check-"+$courseIdx+"]").prop("checked", true);
			$("input[id^=team_check-"+$courseIdx+"]").parent().show();
		} else{
			$("input[id^=team_check-"+$courseIdx+"]").prop("checked", false);
			$("input[id^=team_check-"+$courseIdx+"]").parent().hide();
		}
		
		//If all the courses are selected, check the "Select All" option
		if($(".course_check:checked").length == $(".course_check").length){
			$("#course_all").prop("checked", true);
		} else{
			$("#course_all").prop("checked", false);
		}
		
		//If none of of the courses are selected, hide the team's "Select All" option
		if($(".course_check:checked").length == 0){
			$("#team_all").parent().hide();
		} else{
			$("#team_all").parent().show();
		}
		
		//If all the currently visible teams are selected, check the "Select All" option
		//This is necessary here because we show/hide the team's "Select All" previously
		if($(".team_check:visible:checked").length == $(".team_check:visible").length){
			$("#team_all").prop("checked", true);
		} else{
			$("#team_all").prop("checked", false);
		}
		
		applyFilters();
	});
	
	//Binding for Teams checkboxes.
	$("input[id^=team_check]").change(function(){
		
		//If all the currently visible teams are selected, check the "Select All" option
		if($(".team_check:visible:checked").length == $(".team_check:visible").length){
			$("#team_all").prop("checked", true);
		} else{
			$("#team_all").prop("checked", false);
		}
		
		applyFilters();
	});
	
	//Binding for "Select All" course option
	$("#course_all").change(function(){
		if(this.checked){
			$("#team_all").prop("checked", true);
			$("#team_all").parent().show();
			$("input[id^=course_check]").prop("checked", true);
			$("input[id^=team_check-]").prop("checked", true);
			$("input[id^=team_check-]").parent().show();
		} else{
			$("#team_all").prop("checked", false);
			$("#team_all").parent().hide();
			$("input[id^=course_check]").prop("checked", false);
			$("input[id^=team_check-]").prop("checked", false);
			$("input[id^=team_check-]").parent().hide();
		}
		applyFilters();
	});
	
	//Binding for "Select All" team option
	$("#team_all").change(function(){
		$("input[id^=team_check]:visible").prop("checked", this.checked);
		applyFilters();
	});
});


/**
 * Apply search filters for course followed by teams, lastly by name
 * Apply display filter for email
 */
function applyFilters(){
	$('.student_row').show();
	$('.student_email').show();
	filterCourse();
	filterTeam();
	filterName();
	filterEmails();
	
	//Give message if there are no result
	if($('.backgroundBlock:visible').length == 0){
		setStatusMessage("Your search filters returned no results");
	} else{
		clearStatusMessage();
	}
}

/**
 * Hide courses that are not selected 
 */
function filterCourse(){
	$("input[id^=course_check]").each(function(){
		var $courseIdx = $(this).attr("id").split('-')[1];
		if(this.checked){
			$("#course-" + $courseIdx).show();
		} else{
			$("#course-" + $courseIdx).hide();
		}
	});
}

/**
 * Hide teams that are not selected
 */
function filterTeam(){
	$("input[id^=team_check]").each(function(){
		var $courseIdx = $(this).attr("id").split('-')[1];
		var $teamIdx = $(this).attr("id").split('-')[2];
		if(this.checked){
			$("#studentteam-c" + $courseIdx + "\\." + $teamIdx).parent().show();
		} else{
			$("#studentteam-c" + $courseIdx + "\\." + $teamIdx).parent().hide();
		}
	});
}


/**
 * Search function that hide unrelated items.
 * Currently features to:
 * - 1 student name
 * - case insensitive
 * - exact matching
 */
function filterName($key){
	
	if($key == undefined){
		$key = $('#searchbox').val();
	}

	if($key == null || $key == ""){
		return;
	}else{
		$('.student_row').each(function(){
			if($(this).is(':not(:containsIN('+$key+'))')){
				$(this).hide();
			}
		});
		
		//If a table only contains the header, then we can hide the course.
		$('.dataTable tbody').each(function(){
			if($(this).children(':visible').length == 1) {
				$(this).parent().parent().hide();
			}
		});
	}
}

/**
 * Hide student email view based on search key
 * Uses the hidden attributes of the student_row inside dataTable
 */
function filterEmails(){

	var uniqueEmails={};
	$('.student_row').each(function(){
		var $elementId = $(this).attr('id');
		var $studentId = $elementId.split('-')[1];
		var $emailElement = $("#student_email-" + $studentId.replace('.','\\.'));
		var $emailText = $emailElement.text();

		if($(this).is(':hidden') || uniqueEmails[$emailText]){
			$emailElement.hide();
		} else {
			uniqueEmails[$emailText] = true;
			$emailElement.show();
		}
	});
}


/**
 * Custom function containsIN, for case insensitive matching
 * TODO: expand to fuzzy search
 */
$.extend($.expr[":"], {
	"containsIN": function(elem, i, match, array) {
		return (elem.textContent || elem.innerText || "").toLowerCase().indexOf((match[3] || "").toLowerCase()) >= 0;
	}
});

/**
 * Function that shows confirmation dialog for removing a student from a course
 * @param studentName
 * @returns
 */
function toggleDeleteStudentConfirmation(courseId, studentName) {
	return confirm("Are you sure you want to remove " + studentName + " from " +
			"the course " + courseId + "?");
}
