/*
 * This Javascript file is included in all instructor pages. Functions here
 * should be common to some/all instructor pages.
 */

var emailsForShortNames = new Array(); 
var shortNames = new Array();

//Initial load-up
//-----------------------------------------------------------------------------

var onLoadFunction = function () {
    if(typeof doPageSpecificOnload !== 'undefined'){
        doPageSpecificOnload();
    };
    
    bindErrorImages(".profile-pic-icon-hover, .profile-pic-icon-click");
    // bind the show picture onclick events
    bindStudentPhotoLink(".profile-pic-icon-click > .student-profile-pic-view-link");
    // bind the show picture onhover events
    bindStudentPhotoHoverLink(".profile-pic-icon-hover");
    
};

if(window.addEventListener) {
	window.addEventListener('load', onLoadFunction);
} else {
	window.attachEvent('onload', onLoadFunction);
}


$(function() { 
    $("[data-toggle='tooltip']").tooltip({html: true, container: 'body'}); 
});

//-----------------------------------------------------------------------------


/**
 * Function that shows confirmation dialog for deleting a course
 * @param courseID
 * @returns
 */
function toggleDeleteCourseConfirmation(courseID) {
    return confirm("Are you sure you want to delete the course: " + courseID + "? " +
            "This operation will delete all students and evaluations in this course. " +
            "All instructors of this course will not be able to access it hereafter as well.");
}

/**
 * Pops up confirmation dialog whether to delete specified evaluation
 * @param courseID
 * @param name
 * @returns
 */
function toggleDeleteEvaluationConfirmation(courseID, name) {
    return confirm("Are you sure you want to delete the evaluation " + name + " in " + courseID + "?");
}

/**
 * Pops up confirmation dialog whether to delete specified evaluation
 * @param courseID
 * @param name
 * @returns
 */
function toggleDeleteFeedbackSessionConfirmation(courseID, name) {
    return confirm("Are you sure you want to delete the feedback session " + name + " in " + courseID + "?");
}

/**
 * Pops up confirmation dialog whether to publish the specified
 * evaluation
 * @param name
 */
function togglePublishEvaluation(name) {
    return confirm("Are you sure you want to publish the evaluation " + name + "?");
}

/**
 * Pops up confirmation dialog whether to unpublish the specified
 * evaluation
 * @param name
 */
function toggleUnpublishEvaluation(name){
    return confirm("Are you sure you want to unpublish the evaluation " + name + "?");
}

/**
 * Pops up confirmation dialog whether to remind students to fill in a specified
 * evaluation.
 * @param courseID
 * @param evaluationName
 */
function toggleRemindStudents(evaluationName) {
    return confirm("Send e-mails to remind students who have not submitted their evaluations for " + evaluationName + "?");
}




/**
 * Checks whether a team's name is valid
 * Used in instructorCourseEnroll page (through instructorCourseEnroll.js)
 * @param teamName
 * @returns {Boolean}
 */
function isStudentTeamNameValid(teamName) {
    return teamName.length<=TEAMNAME_MAX_LENGTH;
}

/**
 * To check whether a student's name and team name are valid
 * @param editName
 * @param editTeamName
 * @returns {Boolean}
 */
function isStudentInputValid(editName, editTeamName, editEmail) {
    if (editName == "" || editTeamName == "" || editEmail == "") {
        setStatusMessage(DISPLAY_FIELDS_EMPTY,true);
        return false;
    } else if (!isNameValid(editName)) {
        setStatusMessage(DISPLAY_NAME_INVALID,true);
        return false;
    } else if (!isStudentTeamNameValid(editTeamName)) {
        setStatusMessage(DISPLAY_STUDENT_TEAMNAME_INVALID,true);
        return false;
    } else if (!isEmailValid(editEmail)){
        setStatusMessage(DISPLAY_EMAIL_INVALID,true);
        return false;
    }
    return true;
}


// Student Profile Picture
//--------------------------------------------------------------------------

/**
 * @param elements:
 * 		identifier that points to elements with
 * class: profile-pic-icon-click or profile-pic-icon-hover 
 */
function bindErrorImages(elements){
	$(elements).children('img').on('error', function() {
		if ($(this).attr('src') != "") {
			$(this).attr("src","../images/profile_picture_default.png");
		}
	});
}

/**
 * @param elements:
 * 		identifier that points to elements with
 * class: student-profile-pic-view-link
 */
function bindStudentPhotoLink(elements){
	$(elements).on('click', function(event) {
		if (!event) {
			var event = window.event;
		}
		event.cancelBubble = true;
		if (event.stopPropagation) {
			event.stopPropagation();
		}
		
		handleProfilePicShowEvent($(this));
	});
}

function handleProfilePicShowEvent(obj, callback) {
	var element = $(obj);
    var actualLink = element.parent().attr('data-link');
    var profileLink = element.parent().attr('data-profile-link');
    var studentEmail = element.parent().attr('data-student-email');
    var studentShortnameId = emailsForShortNames.indexOf(studentEmail);
    
    if (profileLink != null) {
    	var shortName = "<i>No short name</i>"; // default value
    	if (studentShortnameId != -1) {
    		shortName = shortNames[studentShortnameId];
    		if (element.siblings('img').attr('src') == "") {
	    		element.siblings('img').attr('src', actualLink)
		    	.load(function() {
		    		// resolved link can be default image
		    		var resolvedLink = $(this).attr('src');
		    		
		            initialisePopover($(this), resolvedLink, shortName);
		            updateHoverShowPictureEvents(actualLink, resolvedLink, shortName);
		            if (callback) {
		            	callback(shortName);
		            }
		    	});
    		} else {
    			element.siblings('img').each(function(index, img) {
    				var resolvedLink = $(this).attr('src');
        			initialisePopover($(this), resolvedLink, shortName);
        			if (callback) {
    	            	callback(shortName);
    	            }
    			});
    		}
    		element.remove();
    	} else {
    		$.getJSON(profileLink, function(data) {
		    	element.siblings('img').attr('src', actualLink)
		    	.load(function() {
		    		// resolved link can be default image
		    		var resolvedLink = $(this).attr('src');
		    		if (data.shortName != "") {
						shortName = data.shortName;
					}
		    		
		    		emailsForShortNames.push(studentEmail); 
					shortNames.push(shortName);
		    		
		            initialisePopover($(this), resolvedLink, shortName);
		            updateHoverShowPictureEvents(actualLink, resolvedLink, shortName);
		            if (callback) {
		            	callback(shortName);
		            }
		    	});
		    	element.remove();
	    	});
    	}
    } else {
    	element.siblings('img').attr('src', actualLink)
    	.load(function() {
    		// resolved link can be default image
    		var resolvedLink = $(this).attr('src');	    		
            initialisePopover($(this), resolvedLink, "");
            if (studentShortnameId == -1) {
            	updateHoverShowPictureEvents(actualLink, resolvedLink, "");
            } else {
            	updateHoverShowPictureEvents(actualLink, resolvedLink, shortNames[studentShortnameId]);
            }
    	});
    	element.remove();	    	
    }
}

function initialisePopover(subject, link, shortName) {
	subject
	.removeClass('hidden')
    .parent().attr('data-link', '')
    .popover({
    	html: true,
        trigger: 'manual',
        placement: 'top',
        content: function () {
        	return '<div class="align-center color_neutral"><img class="profile-pic" src="' + link + '" /><h4>' + shortName + '</h4></div>';
        }
    })
    .mouseenter(function() {
		$(this).popover('show');
    	$(this).siblings('.popover').on('mouseleave', function() {
    		$(this).siblings('.profile-pic-icon-click').popover("hide");
    	});
    	$(this).mouseleave(function() {
            $(this).popover("hide");
	    });
	});
}

/**
 * @param elements:
 * 		identifier that points to elements with
 * class: profile-pic-icon-hover
 */
function bindStudentPhotoHoverLink(elements) {
	$(elements)
	.mouseenter(function() {
		$(this).popover('show');
    	$(this).siblings('.popover').on('mouseleave', function() {
    		$(this).siblings('.profile-pic-icon-hover').popover("hide");
    	});
	})
	.mouseleave(function() {
    	// this is so that the user can hover over the 
    	// pop-over without accidentally hiding the 'view photo' link
    	setTimeout(function(obj) {
    		if ($(obj).siblings('.popover').find('.profile-pic').length != 0 
    				|| !$(obj).siblings(".popover").is(":hover")) {
                $(obj).popover("hide");
            }
    	}, 200, this);
    });
	
	// bind the default popover event for the
	// show picture onhover events	
	$(elements).popover({
		html: true,
	    trigger: 'manual',
	    placement: 'top',
	    content: function () {
    		return '<a class="cursor-pointer" onclick="loadProfilePictureForHoverEvent($(this).closest(\'.popover\').siblings(\'.profile-pic-icon-hover\'))">'
    					+ 'View Photo</a>';
	    }});
}

/**
 * completes the loading cycle for showing profile picture 
 * for an onhover event
 * 
 * @param link
 * @param resolvedLink
 */
function loadProfilePictureForHoverEvent(obj) {
	$.getJSON(obj.attr('data-profile-link'), function(data) {
		var shortName = "";
		if (data != null) {
			if (data.shortName != "") {
				shortName = data.shortName;
			} else {
				shortName = "<i>No short name</i>";
			}
		}
		
		obj.children('img')[0].src = obj.attr('data-link');
		// load the pictures in all similar links
		obj.children('img').load(function() {
			
			var actualLink = $(this).parent().attr('data-link');
			var resolvedLink = $(this).attr('src');
	
			updateHoverShowPictureEvents(actualLink, resolvedLink, shortName);
			
			// this is to show the picture immediately for the one 
			// the user just clicked on
			$(this).parent()
				.popover('show')
				// this is to handle the manual hide action of the popover
				.siblings('.popover').on('mouseleave', function() {
		    		$(this).siblings('.profile-pic-icon-hover').popover("hide");
				});
			
		});
	});
}

/**
 * updates all the student names that show profile picture
 * on hover with the resolved link after one instance of the name
 * has been loaded<br>
 * Helps to avoid clicking view photo when hovering over names of 
 * students whose picture has already been loaded elsewhere in the page
 * @param link
 * @param resolvedLink
 */
function updateHoverShowPictureEvents(actualLink, resolvedLink, shortName) {
	
	$(".profile-pic-icon-click[data-comment][data-student-email][data-link='" + actualLink + "']")
	.children('.from-shortname')
	.html("From: " + shortName);
	$(".profile-pic-icon-click[data-comment][data-student-email][data-link='" + actualLink + "']")
	.children('.to-shortname')
	.html("To: " + shortName);
	$(".profile-pic-icon-click[data-comment][data-student-email][data-link='" + actualLink + "']")
	.children('.student-profile-pic-view-link').remove();

	$(".profile-pic-icon-hover[data-link='" + actualLink + "'], .profile-pic-icon-click[data-comment][data-student-email][data-link='" + actualLink + "']")
	.attr('data-link', "")
	.off( "mouseenter mouseleave" )
	.popover('destroy')
	.popover({
		html: true,
		trigger: 'manual',
		placement: 'top',
		content: function () {
			return '<div class="align-center color_neutral"><img class="profile-pic" src="' + resolvedLink + '" /><h4>' + shortName + '</h4></div>';
		}
	})
	.mouseenter(function() {
		$(this).popover('show');
    	$(this).siblings('.popover').on('mouseleave', function() {
    		$(this).siblings('.profile-pic-icon-hover').popover("hide");
    	});
    	$(this).mouseleave(function() {
    		$(this).popover("hide");
	    });
	})
	.children('img[src=""]').attr('src', resolvedLink);
	
	$(".profile-pic-icon-click[data-student-email][data-link='" + actualLink + "']")
	.attr("data-link", "")
	.children('img[src=""]').attr('src', resolvedLink);
}