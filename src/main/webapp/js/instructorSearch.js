$(function() { 
	$('.comments > .list-group-item').hover(
	   function(){
		$("a[type='button']", this).show();
	}, function(){
		$("a[type='button']", this).hide();
	});
	
	// highlight search string 
	highlightSearchResult('#searchBox', '.panel-body');
	
	// collapse and expand of Comments for students tab of instructorCommentsPage
    var panels = $('div.panel');
    bindCollapseEvents(panels, 0);

	$("div[id^=plainCommentText]").css("margin-left","15px");
});

/**
 * Function that shows confirmation dialog for removing a student from search result
 */
function toggleDeleteStudentConfirmation(courseId, studentName) {
    return confirm("Are you sure you want to remove " + studentName + " from " +
            "the course " + courseId + "?");
}
