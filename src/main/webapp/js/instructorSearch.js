$(function() { 
	$('.comments > .list-group-item').hover(
	   function(){
		$("a[type='button']", this).show();
	}, function(){
		$("a[type='button']", this).hide();
	});
	
	$("div[id^=plainCommentText]").css("margin-left","15px");
});

/**
 * Function that shows confirmation dialog for removing a student from search result
 */
function toggleDeleteStudentConfirmation(courseId, studentName) {
    return confirm("Are you sure you want to remove " + studentName + " from " +
            "the course " + courseId + "?");
}
