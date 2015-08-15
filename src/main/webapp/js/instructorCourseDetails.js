$(document).ready(function() {
    if ($("#button_sortstudentsection").length) {
        toggleSort($("#button_sortstudentsection"), 1);
    } else {
        toggleSort($("#button_sortstudentteam"), 1);
    }
    
    //auto select the html table when modal is shown
    $('#studentTableWindow').on('shown.bs.modal', function (e) {
		selectElementContents( document.getElementById('detailsTable') );
    });
});


function submitFormAjax() {

	var formObject = $("#csvToHtmlForm");
	var formData = formObject.serialize();
	var content = $('#detailsTable');
	var ajaxStatus = $('#ajaxStatus');
	
	$.ajax({
        type : 'POST',
        url :   "/page/instructorCourseDetailsPage?" + formData,
        beforeSend : function() {
        	content.html("<img src='/images/ajax-loader.gif'/>");
        },
        error : function() {
        	ajaxStatus.html("Failed to load student table. Please try again.");
            content.html("<button class=\"btn btn-info\" onclick=\"submitFormAjax()\"> retry</button>");     	
        },
        success : function(data) {
            setTimeout(function() {
                if (!data.isError) {
                	var table = data.studentListHtmlTableAsString;                	             	
                	content.html("<small>" + table + "</small>");
                } else {
                    ajaxStatus.html(data.errorMessage);
                    content.html("<button class=\"btn btn-info\" onclick=\"submitFormAjax()\"> retry</button>");   
                }
                $("#statusMessage").html(data.statusForAjax);
            }, 500);
        }
    });
}


/**
 * Functions to trigger registration key sending to a specific student in the
 * course.
 * Currently no confirmation dialog is shown.
 * @param courseID
 * @param email
 */
function toggleSendRegistrationKey(courseID, email) {
    return confirm("Usually, there is no need to use this feature because TEAMMATES " 
                   + "sends an automatic invite to students at the opening time of each" 
                   + " session. Send a join request anyway?");
}

/**
 * Function to trigger registration key sending to every unregistered students
 * in the course.
 * @param courseID
 */
function toggleSendRegistrationKeysConfirmation(courseID) {
    return confirm("Usually, there is no need to use this feature because TEAMMATES" 
                   + " sends an automatic invite to students at the opening time of" 
                   + " each session. Send a join request to all yet-to-join students in " 
                   + courseID + " anyway?");
}

/**
 * Function that shows confirmation dialog for removing a student from a course
 * @param studentName
 * @param courseId
 * @returns
 */
function toggleDeleteStudentConfirmation(courseId, studentName) {
    return confirm("Are you sure you want to remove " + studentName + " from the course " + courseId + "?");
}

/**
 * function that select the whole table
 * @param el
 */
function selectElementContents(el) {
    var body = document.body, range, sel;
    if (document.createRange && window.getSelection) {
        range = document.createRange();
        sel = window.getSelection();
        sel.removeAllRanges();
        try {
            range.selectNodeContents(el);
            sel.addRange(range);
        } catch (e) {
            range.selectNode(el);
            sel.addRange(range);
        }
    } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(el);
        range.select();
    }
}

var isShowCommentBox = false