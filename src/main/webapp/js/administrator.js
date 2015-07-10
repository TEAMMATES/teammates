/* 
 * This Javascript file is included in all administrator pages. Functions here 
 * should be common to the administrator pages.
 */

// AJAX
var xmlhttp = new getXMLObject();

// OPERATIONS
var OPERATION_ADMINISTRATOR_ADDINSTRUCTORINATOR = "administrator_addinstructor";
var OPERATION_ADMINISTRATOR_LOGOUT = "administrator_logout";

// PARAMETERS
var INSTRUCTOR_EMAIL = "instructoremail";
var INSTRUCTOR_GOOGLEID = "instructorid";
var INSTRUCTOR_NAME = "instructorname";
var INSTRUCTOR_INSTITUTION = "instructorinstitution";

function addInstructor(googleID, name, email, institution) {
    if (xmlhttp) {
        xmlhttp.open("POST", "teammates", false);
        xmlhttp.setRequestHeader("Content-Type",
                "application/x-www-form-urlencoded;");
        xmlhttp.send("operation=" + OPERATION_ADMINISTRATOR_ADDINSTRUCTORINATOR
                + "&" + INSTRUCTORINATOR_GOOGLEID + "=" + googleID + "&"
                + INSTRUCTORINATOR_NAME + "=" + name + "&"
                + INSTRUCTORINATOR_EMAIL + "=" + email + "&"
                + INSTRUCTORINATOR_INSTITUTION + "=" + institution);
    }
}

function verifyInstructorData() {
    var googleID = $('[name="' + INSTRUCTOR_GOOGLEID + '"]').val();
    googleID = sanitizeGoogleId(googleID);
    
    var name = $('[name="' + INSTRUCTOR_NAME + '"]').val().trim();
    var email = $('[name="' + INSTRUCTOR_EMAIL + '"]').val().trim();
    var institution = $('[name="' + INSTRUCTOR_INSTITUTION + '"]').val().trim();

    $('[name="' + INSTRUCTOR_GOOGLEID + '"]').val(googleID);
    $('[name="' + INSTRUCTOR_NAME + '"]').val(name);
    $('[name="' + INSTRUCTOR_EMAIL + '"]').val(email);
    $('[name="' + INSTRUCTOR_INSTITUTION + '"]').val(institution);

    if (googleID == "" || name == "" || email == "") {
        setStatusMessage(DISPLAY_FIELDS_EMPTY, true);
        return false;
    } else if (!isValidGoogleId(googleID)) {
        setStatusMessage(DISPLAY_GOOGLEID_INVALID, true);
        return false;
    } else if (!isEmailValid(email)) {
        setStatusMessage(DISPLAY_EMAIL_INVALID, true);
        return false;
    } else if (!isNameValid(name)) {
        setStatusMessage(DISPLAY_NAME_INVALID, true);
        return false;
    } else if (!isInstitutionValid(institution)) {
        setStatusMessage(DISPLAY_INSTITUTION_INVALID, true);
        return false;
    }

    return true;
}

function getXMLObject() {
    var xmlHttp = false;
    try {
        xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
        } catch (e2) {
            xmlHttp = false;
        }
    }

    if (!xmlHttp && typeof XMLHttpRequest != 'undefined') {
        xmlHttp = new XMLHttpRequest();
    }

    return xmlHttp;
}

function handleLogout() {
    if (xmlhttp.status == 200) {
        var url = xmlhttp.responseXML.getElementsByTagName("url")[0];
        window.location = url.firstChild.nodeValue;
    }
}

function isGoogleIDValid(googleID) {
    if (googleID.indexOf("\\") >= 0 || googleID.indexOf("'") >= 0
            || googleID.indexOf("\"") >= 0) {
        return false;
    } else if (googleID.match(/^[a-zA-Z0-9@ .-]*$/) == null) {
        return false;
    } else if (googleID.length > 29) {
        return false;
    }

    return true;
}

function logout() {
    if (xmlhttp) {
        xmlhttp.open("POST", "teammates", false);
        xmlhttp.setRequestHeader("Content-Type",
                "application/x-www-form-urlencoded;");
        xmlhttp.send("operation=" + OPERATION_ADMINISTRATOR_LOGOUT);
    }

    handleLogout();
}

function showHideErrorMessage(s) {
    $("#" + s).toggle();
}


function toggleDeleteAccountConfirmation(googleId) {
	var rawList = document.getElementById('courses_' + googleId).innerHTML;
	var list = rawList.replace(/<br>/g, "\n").trim() + "\n\n";

	return confirm("Are you sure you want to delete the account " + googleId
				   + "?\n\n" + list
				   + "This operation will delete ALL information about this account "
				   + "from the system.");
}

jQuery(document).ready(function() {
	var offset = 220;
	var duration = 500;
	jQuery(window).scroll(function() {
		if (jQuery(this).scrollTop() > offset) {
			jQuery('.back-to-top-left').fadeIn(duration);
			jQuery('.back-to-top-right').fadeIn(duration);
		} else {
			jQuery('.back-to-top-left').fadeOut(duration);
			jQuery('.back-to-top-right').fadeOut(duration);
		}
	});



	jQuery('.back-to-top-left').click(function(event) {
		event.preventDefault();
		jQuery('html, body').animate({
			scrollTop : 0
		}, duration);
		return false;
	});

	jQuery('.back-to-top-right').click(function(event) {
		event.preventDefault();
		jQuery('html, body').animate({
			scrollTop : 0
		}, duration);
		return false;
	});
});
