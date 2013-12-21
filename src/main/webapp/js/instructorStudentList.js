/**
 * Function that shows confirmation dialog for removing a student from a course
 * @param studentName
 * @returns
 */
function toggleDeleteStudentConfirmation(courseId, studentName) {
	return confirm("Are you sure you want to remove " + studentName + " from " +
			"the course " + courseId + "?");
}

/**
 * Search function that hide unrelated items.
 * Currently features to:
 * - 1 student name / course name / course id
 * - case insensitive
 * - exact matching
 */
function search(){
	var $key = $('#searchbox').val();
	if($key == null || $key == ""){
		resetView();
	}else{
		resetView();
		//For every course: if the course name is not a superstring of the key,
		//check the students row, and hide students whose name is not a superstring of the key.
		//If the course id/name is a superstring, we want to display everything.
		$('.courseTitle').each(function(){
			if($(this).is(':not(:containsIN('+$key+'))')){
				$(this).parent().find('.student_row #studentname:not(:containsIN('+$key+'))').parent().hide();
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
 * Reset the StudentList view to display all modules and student names
 */
function resetView(){
	$('.backgroundBlock').show();
	$('.student_row').show();
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
 * Real-time document update when typing search term
 */
$(document).ready(function(){
	$('input#searchbox').keyup(function(e){
		search();
	});
});