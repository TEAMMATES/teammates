/*
 * This Javascript file is included in all administrator pages. Functions here
 * should be common to the administrator pages.
 */

// AJAX
var xmlhttp = getXMLObject();

// OPERATIONS
var OPERATION_ADMINISTRATOR_ADDINSTRUCTORINATOR = 'administrator_addinstructor';
var OPERATION_ADMINISTRATOR_LOGOUT = 'administrator_logout';

// PARAMETERS
var INSTRUCTOR_EMAIL = 'instructoremail';
var INSTRUCTOR_GOOGLEID = 'instructorid';
var INSTRUCTOR_NAME = 'instructorname';
var INSTRUCTOR_INSTITUTION = 'instructorinstitution';

function addInstructor(googleID, name, email, institution) {
    if (xmlhttp) {
        xmlhttp.open('POST', 'teammates', false);
        xmlhttp.setRequestHeader('Content-Type',
                'application/x-www-form-urlencoded;');
        xmlhttp.send('operation=' + OPERATION_ADMINISTRATOR_ADDINSTRUCTORINATOR
                + '&' + INSTRUCTORINATOR_GOOGLEID + '=' + googleID + '&'
                + INSTRUCTORINATOR_NAME + '=' + name + '&'
                + INSTRUCTORINATOR_EMAIL + '=' + email + '&'
                + INSTRUCTORINATOR_INSTITUTION + '=' + institution);
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

    if (googleID === '' || name === '' || email === '') {
        setStatusMessage(DISPLAY_FIELDS_EMPTY, StatusType.DANGER);
        return false;
    } else if (!isValidGoogleId(googleID)) {
        setStatusMessage(DISPLAY_GOOGLEID_INVALID, StatusType.DANGER);
        return false;
    } else if (!isEmailValid(email)) {
        setStatusMessage(DISPLAY_EMAIL_INVALID, StatusType.DANGER);
        return false;
    } else if (!isNameValid(name)) {
        setStatusMessage(DISPLAY_NAME_INVALID, StatusType.DANGER);
        return false;
    } else if (!isInstitutionValid(institution)) {
        setStatusMessage(DISPLAY_INSTITUTION_INVALID, StatusType.DANGER);
        return false;
    }

    return true;
}

function getXMLObject() {
    var xmlHttp = false;
    try {
        xmlHttp = new ActiveXObject('Msxml2.XMLHTTP');
    } catch (e) {
        try {
            xmlHttp = new ActiveXObject('Microsoft.XMLHTTP');
        } catch (e2) {
            xmlHttp = false;
        }
    }

    if (!xmlHttp && typeof XMLHttpRequest !== 'undefined') {
        xmlHttp = new XMLHttpRequest();
    }

    return xmlHttp;
}

function handleLogout() {
    if (xmlhttp.status === 200) {
        var url = xmlhttp.responseXML.getElementsByTagName('url')[0];
        window.location = url.firstChild.nodeValue;
    }
}

function isGoogleIDValid(googleID) {
    if (googleID.indexOf('\\') >= 0 || googleID.indexOf("'") >= 0
            || googleID.indexOf('"') >= 0) {
        return false;
    } else if (googleID.match(/^[a-zA-Z0-9@ .-]*$/) === null) {
        return false;
    } else if (googleID.length > 29) {
        return false;
    }

    return true;
}

function logout() {
    if (xmlhttp) {
        xmlhttp.open('POST', 'teammates', false);
        xmlhttp.setRequestHeader('Content-Type',
                'application/x-www-form-urlencoded;');
        xmlhttp.send('operation=' + OPERATION_ADMINISTRATOR_LOGOUT);
    }

    handleLogout();
}

function showHideErrorMessage(s) {
    $('#' + s).toggle();
}

$(document).ready(function() {
    var offset = 220;
    var duration = 500;
    $(window).scroll(function() {
        if ($(this).scrollTop() > offset) {
            $('.back-to-top-left').fadeIn(duration);
            $('.back-to-top-right').fadeIn(duration);
        } else {
            $('.back-to-top-left').fadeOut(duration);
            $('.back-to-top-right').fadeOut(duration);
        }
    });

    $('.back-to-top-left').click(function(event) {
        event.preventDefault();
        $('html, body').animate({
            scrollTop: 0
        }, duration);
        return false;
    });

    $('.back-to-top-right').click(function(event) {
        event.preventDefault();
        $('html, body').animate({
            scrollTop: 0
        }, duration);
        return false;
    });
    
    $('.admin-delete-account-link').on('click', function(event) {
        event.preventDefault();

        var $clickedLink = $(event.target);
        var googleId = $clickedLink.data('googleId');
        var existingCourses = document.getElementById('courses_' + googleId).innerHTML;

        var messageText = 'Are you sure you want to delete the account ' + googleId + '?'
                          + '<br><br>' + existingCourses
                          + '<br><br>This operation will delete ALL information about this account from the system.';

        var okCallback = function() {
            window.location = $clickedLink.attr('href');
        };

        BootboxWrapper.showModalConfirmation('Confirm deletion', messageText, okCallback, null,
                BootboxWrapper.DEFAULT_OK_TEXT, BootboxWrapper.DEFAULT_CANCEL_TEXT, StatusType.DANGER);
    });
});
