/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// identity function for calling harmony imports with the correct context
/******/ 	__webpack_require__.i = function(value) { return value; };
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 40);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
 * Contains constants to be used across the application.
 */

// Status message type
var StatusType = {
    SUCCESS: 'success',
    INFO: 'info',
    WARNING: 'warning',
    DANGER: 'danger',
    PRIMARY: 'primary',
    isValidType: function isValidType(type) {
        return type === StatusType.SUCCESS || type === StatusType.INFO || type === StatusType.PRIMARY || type === StatusType.WARNING || type === StatusType.DANGER;
    }
};
StatusType.DEFAULT = StatusType.INFO;

var ParamsNames = {
    COURSE_ID: 'courseid',
    COURSE_NAME: 'coursename',
    COURSE_TIME_ZONE: 'coursetimezone',
    FEEDBACK_SESSION_NAME: 'fsname',
    FEEDBACK_SESSION_STARTDATE: 'startdate',
    FEEDBACK_SESSION_STARTTIME: 'starttime',
    FEEDBACK_SESSION_TIMEZONE: 'timezone',
    FEEDBACK_SESSION_VISIBLEDATE: 'visibledate',
    FEEDBACK_SESSION_VISIBLETIME: 'visibletime',
    FEEDBACK_SESSION_PUBLISHDATE: 'publishdate',
    FEEDBACK_SESSION_PUBLISHTIME: 'publishtime',
    FEEDBACK_SESSION_SESSIONVISIBLEBUTTON: 'sessionVisibleFromButton',
    FEEDBACK_SESSION_RESULTSVISIBLEBUTTON: 'resultsVisibleFromButton',
    FEEDBACK_QUESTION_CONSTSUMOPTION: 'constSumOption',
    FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE: 'constSumOptionTable',
    FEEDBACK_QUESTION_CONSTSUMPOINTS: 'constSumPoints',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION: 'constSumPointsForEachOption',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT: 'constSumPointsForEachRecipient',
    FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS: 'constSumToRecipients',
    FEEDBACK_QUESTION_MCQCHOICE: 'mcqOption',
    FEEDBACK_QUESTION_MSQCHOICE: 'msqOption',
    FEEDBACK_QUESTION_NUMBEROFCHOICECREATED: 'noofchoicecreated',
    FEEDBACK_QUESTION_NUMSCALE_MIN: 'numscalemin',
    FEEDBACK_QUESTION_NUMSCALE_MAX: 'numscalemax',
    FEEDBACK_QUESTION_NUMSCALE_STEP: 'numscalestep',
    FEEDBACK_QUESTION_RANKOPTION: 'rankOption',
    FEEDBACK_QUESTION_RANKOPTIONTABLE: 'rankOptionTable',
    FEEDBACK_QUESTION_RANKTORECIPIENTS: 'rankToRecipients',
    FEEDBACK_QUESTION_SHOWRESPONSESTO: 'showresponsesto',
    FEEDBACK_QUESTION_SHOWGIVERTO: 'showgiverto',
    FEEDBACK_QUESTION_SHOWRECIPIENTTO: 'showrecipientto',
    FEEDBACK_QUESTION_TEXT: 'questiontext',
    FEEDBACK_QUESTION_TYPE: 'questiontype',
    FEEDBACK_QUESTION_EDITTEXT: 'questionedittext',
    FEEDBACK_QUESTION_EDITTYPE: 'questionedittype',
    FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE: 'numofrecipientstype',
    FEEDBACK_QUESTION_RECIPIENTTYPE: 'recipienttype',
    FEEDBACK_QUESTION_DESCRIPTION: 'questiondescription',
    FEEDBACK_QUESTION_DISCARDCHANGES: 'questiondiscardchanges',
    FEEDBACK_QUESTION_SAVECHANGESTEXT: 'questionsavechangestext',
    FEEDBACK_SESSION_ENABLE_EDIT: 'editsessiondetails'
};

var Const = {

    ModalDialog: {

        UNREGISTERED_STUDENT: {
            header: 'Register for TEAMMATES',
            text: 'You have to register using a google account in order to access this page. ' + 'Would you like to proceed and register?'
        }

    },

    StatusMessages: {

        INSTRUCTOR_DETAILS_LENGTH_INVALID: 'Instructor Details must have 3 columns'

    }

};

exports.Const = Const;
exports.ParamsNames = ParamsNames;
exports.StatusType = StatusType;

/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
 * Tests whether the passed object is an actual date
 * with an accepted format
 *
 * Allowed formats : http://dygraphs.com/date-formats.html
 *
 * TEAMMATES currently follows the RFC2822 / IETF date syntax
 * e.g. 02 Apr 2012, 23:59
 *
 * @param date
 * @returns boolean
 */
function isDate(date) {
    return !isNaN(Date.parse(date));
}

/**
* Function to test if param is a numerical value
* @param num
* @returns boolean
*/
function isNumber(num) {
    return (typeof num === 'string' || typeof num === 'number') && !isNaN(num - 0) && num !== '';
}

/**
 * Checks if element is within browser's viewport.
 * @return true if it is within the viewport, false otherwise
 * @see http://stackoverflow.com/q/123999
 */
function isWithinView(element) {
    var baseElement = $(element)[0]; // unwrap jquery element
    var rect = baseElement.getBoundingClientRect();

    var $viewport = $(window);

    // makes the viewport size slightly larger to account for rounding errors
    var tolerance = 0.25;
    return rect.top >= 0 - tolerance // below the top of viewport
    && rect.left >= 0 - tolerance // within the left of viewport
    && rect.right <= $viewport.width() + tolerance // within the right of viewport
    && rect.bottom <= $viewport.height() + tolerance // above the bottom of viewport
    ;
}

exports.isDate = isDate;
exports.isNumber = isNumber;
exports.isWithinView = isWithinView;

/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.showModalConfirmationWithCancel = exports.showModalConfirmation = exports.showModalAlert = undefined;

var _const = __webpack_require__(0);

/**
 * Wrapper for Bootbox.js (available at http://bootboxjs.com/)
 * "Bootbox.js is a small JavaScript library which allows you to create programmatic dialog boxes using
 *  Bootstrap modals"
 */

var DEFAULT_OK_TEXT = 'OK'; /* global bootbox:false */

var DEFAULT_CANCEL_TEXT = 'Cancel';
var DEFAULT_YES_TEXT = 'Yes';
var DEFAULT_NO_TEXT = 'No';

function applyStyleToModal(modal, statusType) {
    modal.find('.modal-header').addClass('alert-' + (statusType || _const.StatusType.DEFAULT)).find('.modal-title').addClass('icon-' + (statusType || _const.StatusType.DEFAULT));
}

/**
 * Custom alert dialog to replace default alert() function
 * Required params: titleText and messageText
 * Optional params: okButtonText (defaults to "OK")
 *                  statusType (defaults to StatusType.DEFAULT)
 */
function showModalAlert(titleText, messageText, okButtonText, statusType) {
    var modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            okay: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: 'modal-btn-ok btn-' + (statusType || _const.StatusType.DEFAULT)
            }
        }
    });
    applyStyleToModal(modal, statusType);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText and okCallback
 * Optional params: cancelCallBack (defaults to null)
 *                  okButtonText (defaults to "OK")
 *                  cancelButtonText (defaults to "Cancel")
 *                  statusType (defaults to StatusType.INFO)
 */
function showModalConfirmation(titleText, messageText, okCallback, cancelCallback, okButtonText, cancelButtonText, statusType) {
    var modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelCallback || null
            },
            ok: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: 'modal-btn-ok btn-' + (statusType || _const.StatusType.DEFAULT),
                callback: okCallback
            }
        }
    });
    applyStyleToModal(modal, statusType);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText, yesButtonCallback and noButtonCallback
 * Optional params: cancelButtonCallBack (defaults to null)
 *                  yesButtonText (defaults to "Yes")
 *                  noButtonText (defaults to "No")
 *                  canelButtonText (defaults to "Cancel")
 *                  statusType (defaults to StatusType.INFO)
 */
function showModalConfirmationWithCancel(titleText, messageText, yesButtonCallback, noButtonCallback, cancelButtonCallback, yesButtonText, noButtonText, cancelButtonText, statusType) {
    var modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            yes: {
                label: yesButtonText || DEFAULT_YES_TEXT,
                className: 'modal-btn-ok btn-' + (statusType || _const.StatusType.DEFAULT),
                callback: yesButtonCallback
            },
            no: {
                label: noButtonText || DEFAULT_NO_TEXT,
                className: 'modal-btn-ok btn-' + (statusType || _const.StatusType.DEFAULT),
                callback: noButtonCallback
            },
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelButtonCallback || null
            }
        }
    });
    applyStyleToModal(modal, statusType);
}

exports.showModalAlert = showModalAlert;
exports.showModalConfirmation = showModalConfirmation;
exports.showModalConfirmationWithCancel = showModalConfirmationWithCancel;

/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.sendRemindersToStudents = exports.setupFsCopyModal = exports.selectElementContents = exports.prepareInstructorPages = exports.executeCopyCommand = exports.bindUnpublishButtons = exports.bindStudentPhotoLink = exports.bindStudentPhotoHoverLink = exports.bindRemindButtons = exports.bindPublishButtons = exports.bindDeleteButtons = exports.attachEventToDeleteAllStudentLink = exports.attachEventToDeleteStudentLink = undefined;

var _bootboxWrapper = __webpack_require__(2);

var _scrollTo = __webpack_require__(4);

var _const = __webpack_require__(0);

/*
 * This JavaScript file is included in all instructor pages. Functions here
 * should be common to some/all instructor pages.
 */

// -----------------------------------------------------------------------------

function setupFsCopyModal() {
    $('#fsCopyModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var actionlink = button.data('actionlink');
        var courseid = button.data('courseid');
        var fsname = button.data('fsname');
        var appUrl = window.location.origin;
        var currentPage = window.location.href.substring(appUrl.length); // use the page's relative URL

        $.ajax({
            type: 'GET',
            url: actionlink + '&courseid=' + encodeURIComponent(courseid) + '&fsname=' + encodeURIComponent(fsname) + '&currentPage=' + encodeURIComponent(currentPage),
            beforeSend: function beforeSend() {
                $('#fscopy_submit').prop('disabled', true);
                $('#courseList').html('Loading possible destination courses. Please wait ...<br>' + "<img class='margin-center-horizontal' src='/images/ajax-loader.gif'/>");
            },
            error: function error() {
                $('#courseList').html("<p id='fs-copy-modal-error'>Error retrieving course list." + 'Please close the dialog window and try again.</p>');
            },
            success: function success(data) {
                $('#courseList').html(data);
                // If the user alt-clicks, the form does not send any parameters and results in an error.
                // Prevent default form submission and submit using jquery.
                $('#fscopy_submit').off('click').on('click', function (e) {
                    $('#fscopy_submit').prop('disabled', true);
                    e.preventDefault();
                    $('#fscopy_submit').closest('form').submit();
                });
                $('#fscopy_submit').prop('disabled', false);
            }
        });
    });

    $('#instructorCopyModalForm').submit(function (e) {
        e.preventDefault();
        var $this = $(this);

        var $copyModalStatusMessage = $('#feedback-copy-modal-status');

        $.ajax({
            type: 'POST',
            url: $this.prop('action'),
            data: $this.serialize(),
            beforeSend: function beforeSend() {
                $copyModalStatusMessage.removeClass('alert alert-danger');
                $copyModalStatusMessage.html('<img src="/images/ajax-loader.gif" class="margin-center-horizontal">');
            },
            error: function error() {
                $copyModalStatusMessage.addClass('alert alert-danger');
                $copyModalStatusMessage.text('There was an error during submission. ' + 'Please close the dialog window and try again.');
            },
            success: function success(data) {
                var isError = data.errorMessage !== '';
                if (!isError && data.redirectUrl) {
                    window.location.href = data.redirectUrl;
                } else {
                    $copyModalStatusMessage.addClass('alert alert-danger');
                    $copyModalStatusMessage.text(data.errorMessage);
                    $('#fscopy_submit').prop('disabled', false);
                }
            }
        });
    });
}

// Student Profile Picture
// --------------------------------------------------------------------------

/**
 * updates all the student names that show profile picture
 * on hover with the resolved link after one instance of the name
 * has been loaded<br>
 * Helps to avoid clicking view photo when hovering over names of
 * students whose picture has already been loaded elsewhere in the page
 * @param link
 * @param resolvedLink
 */
function updateHoverShowPictureEvents(actualLink, resolvedLink) {
    $('.profile-pic-icon-hover[data-link="' + actualLink + '"]').attr('data-link', '').off('mouseenter mouseleave').popover('destroy').popover({
        html: true,
        trigger: 'manual',
        placement: 'top',
        content: function content() {
            return '<img class="profile-pic" src="' + resolvedLink + '">';
        }
    }).mouseenter(function () {
        $(this).popover('show');
        $(this).siblings('.popover').on('mouseleave', function () {
            $(this).siblings('.profile-pic-icon-hover').popover('hide');
        });
        $(this).mouseleave(function () {
            // this is so that the user can hover over the
            // pop-over photo without hiding the photo
            setTimeout(function (obj) {
                if ($(obj).siblings('.popover').find(':hover').length === 0) {
                    $(obj).popover('hide');
                }
            }, 200, this);
        });
    }).children('img[src=""]').attr('src', resolvedLink);
}

/**
 * @param elements:
 * identifier that points to elements with
 * class: student-profile-pic-view-link
 */
function bindStudentPhotoLink(elements) {
    $(elements).on('click', function (e) {
        var event = e || window.event;

        event.cancelBubble = true;

        if (event.stopPropagation) {
            event.stopPropagation();
        }

        var actualLink = $(this).parent().attr('data-link');
        var $loadingImage = $('<img>').attr('src', '/images/ajax-loader.gif').addClass('center-block margin-top-7px');

        $(this).siblings('img').attr('src', actualLink).load(function () {
            var resolvedLink = $(this).attr('src');

            $loadingImage.remove();

            $(this).removeClass('hidden').parent().attr('data-link', '').popover({
                html: true,
                trigger: 'manual',
                placement: 'top',
                content: function content() {
                    return '<img class="profile-pic" src="' + resolvedLink + '">';
                }
            }).mouseenter(function () {
                $(this).popover('show');
                $(this).siblings('.popover').on('mouseleave', function () {
                    $(this).siblings('.profile-pic-icon-click').popover('hide');
                });
                $(this).mouseleave(function () {
                    // this is so that the user can hover over the
                    // pop-over photo without hiding the photo
                    setTimeout(function (obj) {
                        if ($(obj).siblings('.popover').find(':hover').length === 0) {
                            $(obj).popover('hide');
                        }
                    }, 200, this);
                });
            });

            updateHoverShowPictureEvents(actualLink, resolvedLink);
        });

        var $imageCell = $(this).closest('td');
        $(this).remove();
        $imageCell.append($loadingImage);
    });
}

/**
 * completes the loading cycle for showing profile picture
 * for a onhover event
 * @param link
 * @param resolvedLink
 */
function loadProfilePictureForHoverEvent(obj) {
    obj.children('img')[0].src = obj.attr('data-link');

    var $loadingImage = $('<img>').attr('src', '/images/ajax-loader.gif');

    // load the pictures in all similar links
    obj.children('img').load(function () {
        var actualLink = $(this).parent().attr('data-link');
        var resolvedLink = $(this).attr('src');

        $loadingImage.remove();

        updateHoverShowPictureEvents(actualLink, resolvedLink);

        // this is to show the picture immediately for the one
        // the user just clicked on
        $(this).parent().popover('show')
        // this is to handle the manual hide action of the popover
        .siblings('.popover').on('mouseleave', function () {
            $(this).siblings('.profile-pic-icon-hover').popover('hide');
        });
    });

    obj.popover('destroy').popover({
        html: true,
        trigger: 'manual',
        placement: 'top',
        content: function content() {
            return $loadingImage.get(0).outerHTML;
        }
    });
    obj.popover('show');
}

/**
 * @param elements:
 * identifier that points to elements with
 * class: profile-pic-icon-hover
 */
function bindStudentPhotoHoverLink(elements) {
    $(elements).mouseenter(function () {
        $(this).popover('show');
        $(this).siblings('.popover').on('mouseleave', function () {
            $(this).siblings('.profile-pic-icon-hover').popover('hide');
        });
    }).mouseleave(function () {
        // this is so that the user can hover over the
        // pop-over without accidentally hiding the 'view photo' link
        setTimeout(function (obj) {
            if ($(obj).siblings('.popover').find('.profile-pic').length !== 0 || $(obj).siblings('.popover').find(':hover').length === 0) {
                $(obj).popover('hide');
            }
        }, 200, this);
    });

    // bind the default popover event for the
    // show picture onhover events
    $(elements).popover({
        html: true,
        trigger: 'manual',
        placement: 'top',
        content: function content() {
            $('body').on('click', '.cursor-pointer', function (event) {
                var toLoad = $(event.currentTarget).closest('.popover').siblings('.profile-pic-icon-hover');
                loadProfilePictureForHoverEvent(toLoad);
            });
            return '<a class="cursor-pointer">View Photo</a>';
        }
    });
}

function bindDeleteButtons() {
    $('body').on('click', '.session-delete-for-test', function (event) {
        event.preventDefault();

        var $button = $(event.target);
        var courseId = $button.data('courseid');
        var feedbackSessionName = $button.data('fsname');

        var messageText = 'Are you want to delete the feedback session ' + feedbackSessionName + ' in ' + courseId + '?';
        var okCallback = function okCallback() {
            window.location = $button.attr('href');
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm deleting feedback session', messageText, okCallback, null, null, null, _const.StatusType.DANGER);
    });
}

function bindCourseDeleteLinks() {
    $('body').on('click', '.course-delete-link', function (event) {
        event.preventDefault();

        var $clickedLink = $(event.target);
        var messageText = 'Are you sure you want to delete the course: ' + $clickedLink.data('courseId') + '? ' + 'This operation will delete all students and sessions in this course. ' + 'All instructors of this course will not be able to access it hereafter as well.';
        var okCallback = function okCallback() {
            window.location = $clickedLink.attr('href');
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm deleting course', messageText, okCallback, null, null, null, _const.StatusType.DANGER);
    });
}

function bindSessionDeleteLinks() {
    $('body').on('click', '#fsDeleteLink', function (event) {
        event.preventDefault();

        var $clickedLink = $(event.target);
        var messageText = 'Are you sure you want to delete the feedback session ' + $clickedLink.data('feedbackSessionName') + ' in ' + $clickedLink.data('courseId') + '?';
        var okCallback = function okCallback() {
            window.location = $clickedLink.attr('href');
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm deleting feedback session', messageText, okCallback, null, null, null, _const.StatusType.DANGER);
    });
}

function attachEventToDeleteStudentLink() {
    $(document).on('click', '.course-student-delete-link', function (event) {
        event.preventDefault();

        var $clickedLink = $(event.target);
        var messageText = 'Are you sure you want to remove ' + $clickedLink.data('studentName') + ' from the course ' + $clickedLink.data('courseId') + '?';
        var okCallback = function okCallback() {
            window.location = $clickedLink.attr('href');
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm deletion', messageText, okCallback, null, null, null, _const.StatusType.DANGER);
    });
}

function sendRemindersToStudents(urlLink) {
    var $statusMessage = $('#statusMessagesToUser');
    $.ajax({
        type: 'POST',
        url: urlLink,
        beforeSend: function beforeSend() {
            $statusMessage.html('<img src="/images/ajax-loader.gif">');
            $statusMessage.css('display', 'block');
        },
        error: function error() {
            $statusMessage.html('An error has occurred while sending reminder. Please try again.');
        },
        success: function success(data) {
            var statusToUser = $(data).find('#statusMessagesToUser').html();
            $statusMessage.html(statusToUser);

            (0, _scrollTo.scrollToElement)($statusMessage[0], { duration: 1000 });
        }
    });
}

function attachEventToDeleteAllStudentLink() {
    $('body').on('click', '.course-student-delete-all-link', function (event) {
        event.preventDefault();

        var $clickedLink = $(event.target);
        var messageText = 'Are you sure you want to remove all students\n                from the course ' + $clickedLink.data('courseId') + '?';
        var okCallback = function okCallback() {
            window.location = $clickedLink.attr('href');
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm deletion', messageText, okCallback, null, null, null, _const.StatusType.DANGER);
    });
}

function bindRemindButtons() {
    $('body').on('click', '.session-remind-inner-for-test, .session-remind-for-test', function (event) {
        event.preventDefault();

        var $button = $(event.target);
        var messageText = 'Send e-mails to remind students who have not submitted their feedback for ' + $button.data('fsname') + '?';
        var okCallback = function okCallback() {
            var urlLink = $button.attr('href');
            sendRemindersToStudents(urlLink);
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm sending reminders', messageText, okCallback, null, null, null, _const.StatusType.INFO);
    });
}

function bindPublishButtons() {
    $('body').on('click', '.session-publish-for-test', function (event) {
        event.preventDefault();

        var $button = $(this);
        var feedbackSessionName = $button.data('fsname');
        var messageText = 'Are you sure you want to publish the responses for the session "' + feedbackSessionName + '"?';

        var isSendingPublishedEmail = $button.data('sending-published-email');
        if (isSendingPublishedEmail) {
            messageText += ' An email will be sent to students to inform them that the responses are ready for viewing.';
        }

        var okCallback = function okCallback() {
            window.location = $button.attr('href');
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm publishing responses', messageText, okCallback, null, null, null, _const.StatusType.WARNING);
    });
}

function bindUnpublishButtons() {
    $('body').on('click', '.session-unpublish-for-test', function (event) {
        event.preventDefault();

        var $button = $(event.target);
        var messageText = 'Are you sure you want to unpublish the session "' + $button.data('fsname') + '"?\n                             An email will be sent to students to inform them that the session has been unpublished\n                             and the session responses will no longer be viewable by students.';
        var okCallback = function okCallback() {
            window.location = $button.attr('href');
        };

        (0, _bootboxWrapper.showModalConfirmation)('Confirm unpublishing responses', messageText, okCallback, null, null, null, _const.StatusType.WARNING);
    });
}

// --------------------------------------------------------------------------

/**
 * Selects contents inside an element.
 * @param {HTML DOM Object} elementNode The element to select contents from.
 */
function selectElementContents(elementNode) {
    var body = document.body;
    var range = void 0;
    if (document.createRange && window.getSelection) {
        range = document.createRange();
        var selection = window.getSelection();
        selection.removeAllRanges();
        try {
            range.selectNodeContents(elementNode);
            selection.addRange(range);
        } catch (e) {
            range.selectNode(elementNode);
            selection.addRange(range);
        }
    } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(elementNode);
        range.select();
    }
}

/**
 * Simulates the copy action in the right-click menu, typically 'Ctrl + C'.
 */
function executeCopyCommand() {
    document.execCommand('copy');
}

// Initial load-up
// -----------------------------------------------------------------------------

function prepareInstructorPages() {
    // bind the show picture onclick events
    bindStudentPhotoLink('.profile-pic-icon-click > .student-profile-pic-view-link');

    // bind the show picture onhover events
    bindStudentPhotoHoverLink('.profile-pic-icon-hover');

    // bind the event handler to show confirmation modal
    bindCourseDeleteLinks();
    bindSessionDeleteLinks();
}

exports.attachEventToDeleteStudentLink = attachEventToDeleteStudentLink;
exports.attachEventToDeleteAllStudentLink = attachEventToDeleteAllStudentLink;
exports.bindDeleteButtons = bindDeleteButtons;
exports.bindPublishButtons = bindPublishButtons;
exports.bindRemindButtons = bindRemindButtons;
exports.bindStudentPhotoHoverLink = bindStudentPhotoHoverLink;
exports.bindStudentPhotoLink = bindStudentPhotoLink;
exports.bindUnpublishButtons = bindUnpublishButtons;
exports.executeCopyCommand = executeCopyCommand;
exports.prepareInstructorPages = prepareInstructorPages;
exports.selectElementContents = selectElementContents;
exports.setupFsCopyModal = setupFsCopyModal;
exports.sendRemindersToStudents = sendRemindersToStudents;

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.scrollToTop = exports.scrollToElement = undefined;

var _helper = __webpack_require__(1);

/**
 * Scrolls the screen to a certain position.
 * @param scrollPos Position to scroll the screen to.
 * @param duration Duration of animation in ms. Scrolling is instant if omitted.
 *                 'fast and 'slow' are 600 and 200 ms respectively,
 *                 400 ms will be used if any other string is supplied.
 */
function scrollToPosition(scrollPos, duration) {
    if (duration === undefined || duration === null) {
        $(window).scrollTop(scrollPos);
    } else {
        $('html, body').animate({ scrollTop: scrollPos }, duration);
    }
}

/**
 * Scrolls to an element.
 * Possible options are as follows:
 *
 * @param element - element to scroll to
 * @param options - associative array with optional values:
 *                  * type: ['top'|'view'], defaults to 'top';
 *                          'top' scrolls to the top of the element,
 *                          'view' scrolls the element into view
 *                  * offset: offset from element to scroll to in px,
 *                            defaults to navbar / footer offset for scrolling from above or below
 *                  * duration: duration of animation,
 *                              defaults to 0 for scrolling without animation
 */
function scrollToElement(element, opts) {
    var defaultOptions = {
        type: 'top',
        offset: 0,
        duration: 0
    };

    var options = opts || {};
    var type = options.type || defaultOptions.type;
    var offset = options.offset || defaultOptions.offset;
    var duration = options.duration || defaultOptions.duration;

    var isViewType = type === 'view';
    if (isViewType && (0, _helper.isWithinView)(element)) {
        return;
    }

    var navbar = $('.navbar')[0];
    var navbarHeight = navbar ? navbar.offsetHeight : 0;
    var footer = $('#footerComponent')[0];
    var footerHeight = footer ? footer.offsetHeight : 0;
    var windowHeight = window.innerHeight - navbarHeight - footerHeight;

    var isElementTallerThanWindow = windowHeight < element.offsetHeight;
    var isFromAbove = window.scrollY < element.offsetTop;
    var isAlignedToTop = !isViewType || isElementTallerThanWindow || !isFromAbove;

    // default offset - from navbar / footer
    if (options.offset === undefined) {
        offset = isAlignedToTop ? navbarHeight * -1 : footerHeight * -1;
    }

    // adjust offset to bottom of element
    if (!isAlignedToTop) {
        offset *= -1;
        offset += element.offsetHeight - window.innerHeight;
    }

    var scrollPos = element.offsetTop + offset;

    scrollToPosition(scrollPos, duration);
}

/**
 * Scrolls the screen to top
 * @param duration Duration of animation in ms. Scrolling is instant if omitted.
 *                 'fast and 'slow' are 600 and 200 ms respectively,
 *                 400 ms will be used if any other string is supplied.
 */
function scrollToTop(duration) {
    scrollToPosition(0, duration);
}

exports.scrollToElement = scrollToElement;
exports.scrollToTop = scrollToTop;

/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.toggleSort = exports.getPointValue = exports.Comparators = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _helper = __webpack_require__(1);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/**
 * Returns false if the parameter is either one of null or undefined.
 * Returns true otherwise.
 *
 * @param varToCheck
 */
function isDefined(varToCheck) {
    return varToCheck !== null && varToCheck !== undefined;
}

/**
 * To get point value from a formatted string
 *
 * @param s
 *     A table cell (td tag) that contains the formatted string
 * @param ditchZero
 *     Whether 0% should be treated as lower than -90 or not
 * @returns
 */
function getPointValue(s, ditchZero) {
    var s0 = s;
    var baseValue = 100;

    if (s0.indexOf('/') !== -1) {
        // magic expressions below as these cases will only be compared with
        // case E +(-)X% (0 <= X <= 100)
        if (s0.indexOf('S') !== -1) {
            return 2 * baseValue + 1; // Case N/S (feedback contribution not sure)
        }

        return 2 * baseValue + 2; // Case N/A
    }

    if (s0 === '0%') {
        // Case 0%
        if (ditchZero) {
            return 0;
        }
        return baseValue;
    }

    s0 = s0.replace('E', '').replace('%', ''); // Case E +(-)X%

    if (s0 === '') {
        return baseValue; // Case E
    }

    return baseValue + parseFloat(s0); // Other typical cases
}

/**
 * Sorting comparison functions.
 */

var Comparators = function () {
    function Comparators() {
        _classCallCheck(this, Comparators);
    }

    _createClass(Comparators, null, [{
        key: 'sortBase',

        /**
         * The base comparator (ascending)
         * @returns 1 if x comes after y, -1 if x comes before y, 0 if they are the same
         */
        value: function sortBase(x, y) {
            // Text sorting
            if (x < y) {
                return -1;
            }
            return x > y ? 1 : 0;
        }

        /**
         * Comparator for numbers (integer, double) (ascending)
         * @returns +ve if x > y, -ve if x < y and 0 otherwise
         */

    }, {
        key: 'sortNum',
        value: function sortNum(x, y) {
            return x - y;
        }

        /**
         * Comparator for date. Allows for the same format as isDate()
         * @returns 1 if Date x is after y, 0 if same and -1 if before
         */

    }, {
        key: 'sortDate',
        value: function sortDate(x, y) {
            var x0 = Date.parse(x);
            var y0 = Date.parse(y);
            if (x0 > y0) {
                return 1;
            }
            return x0 < y0 ? -1 : 0;
        }

        /**
         * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
         * possibly a tag that surrounds it.
         */

    }, {
        key: 'sortByPoints',
        value: function sortByPoints(a, b) {
            var a0 = getPointValue(a, true);
            var b0 = getPointValue(b, true);
            if ((0, _helper.isNumber)(a0) && (0, _helper.isNumber)(b0)) {
                return Comparators.sortNum(a0, b0);
            }
            return Comparators.sortBase(a0, b0);
        }

        /**
         * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
         * surrounds it.
         */

    }, {
        key: 'sortByDiff',
        value: function sortByDiff(a, b) {
            var a0 = getPointValue(a, false);
            var b0 = getPointValue(b, false);
            if ((0, _helper.isNumber)(a0) && (0, _helper.isNumber)(b0)) {
                return Comparators.sortNum(a0, b0);
            }
            return Comparators.sortBase(a0, b0);
        }
    }, {
        key: 'getDefaultComparator',
        value: function getDefaultComparator(columnType) {
            var defaultComparator = void 0;

            if (columnType === 1) {
                defaultComparator = Comparators.sortNum;
            } else if (columnType === 2) {
                defaultComparator = Comparators.sortDate;
            } else {
                defaultComparator = Comparators.sortBase;
            }

            return defaultComparator;
        }
    }]);

    return Comparators;
}();

/**
 * Functions that pull data out of a table cell.
 */


var Extractors = function () {
    function Extractors() {
        _classCallCheck(this, Extractors);
    }

    _createClass(Extractors, null, [{
        key: 'textExtractor',
        value: function textExtractor($tableCell) {
            return $tableCell.text();
        }
    }, {
        key: 'tooltipExtractor',
        value: function tooltipExtractor($tableCell) {
            return $tableCell.find('span').attr('data-original-title');
        }
    }, {
        key: 'dateStampExtractor',
        value: function dateStampExtractor($tableCell) {
            return $tableCell.data('dateStamp');
        }
    }, {
        key: 'getDefaultExtractor',
        value: function getDefaultExtractor() {
            return Extractors.textExtractor;
        }
    }]);

    return Extractors;
}();

var TableButtonHelpers = function () {
    function TableButtonHelpers() {
        _classCallCheck(this, TableButtonHelpers);
    }

    _createClass(TableButtonHelpers, null, [{
        key: 'getEnclosingTable',

        /**
         * Given a button, get the innermost table enclosing it.
         */
        value: function getEnclosingTable($button) {
            return $($button.parents('table')[0]);
        }

        /**
         * Given a button and an index idx,
         * find the button's column position in the table
         * where the columns are treated as idx-indexed.
         */

    }, {
        key: 'getColumnPositionOfButton',
        value: function getColumnPositionOfButton($button, idx) {
            return $button.parent().children().index($button) + idx;
        }

        /**
         * Given a table, clear all the sort states.
         */

    }, {
        key: 'clearAllSortStates',
        value: function clearAllSortStates($table) {
            $table.find('.icon-sort').attr('class', 'icon-sort unsorted'); // clear the icons
            $table.find('.button-sort-ascending').removeClass('button-sort-ascending').addClass('button-sort-none');
            $table.find('.button-sort-descending').removeClass('button-sort-descending').addClass('button-sort-none');
        }

        /**
         * Given a button in table, set its state to sorted ascending.
         * Clear all other button states.
         */

    }, {
        key: 'setButtonToSortedAscending',
        value: function setButtonToSortedAscending($button) {
            this.clearAllSortStates(this.getEnclosingTable($button));
            $button.addClass('button-sort-ascending');
            $button.find('.icon-sort').attr('class', 'icon-sort sorted-ascending'); // set the icon to ascending
        }

        /**
         * Given a button in table, set its state to sorted descending.
         * Clear all other button states.
         */

    }, {
        key: 'setButtonToSortedDescending',
        value: function setButtonToSortedDescending($button) {
            this.clearAllSortStates(this.getEnclosingTable($button));
            $button.addClass('button-sort-descending');
            $button.find('.icon-sort').attr('class', 'icon-sort sorted-descending'); // set the icon to descending
        }
    }]);

    return TableButtonHelpers;
}();

// http://stackoverflow.com/questions/7558182/sort-a-table-fast-by-its-first-column-with-javascript-or-jquery
/**
 * Sorts a table based on certain column and comparator
 *
 * @param $table
 *     A jQuery object representing the table.
 * @param colIdx
 *     The column index (1-based) as key for the sort
 * @param comparatorOrNull
 *     Function to compare two elements.
 *     May be null.
 * @param extractorOrNull
 *     Function to pull out data from a table cell for comparison.
 *     May be null.
 * @param shouldSortAscending
 *     If this is true, sort in ascending order.
 *     Otherwise, sort in descending order
 * @param rowOffset
 *     Ignore rows above this when sorting. Start sorting from this row.
 *     The main use case for this is to ignore the first row, which usually contains the table headers.
 *     In that case, set rowOffset to 1 (thus ignoring row 0).
 */


function sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending, rowOffset) {
    var columnType = 0;
    var store = [];
    var $rowList = $('tr', $table);

    // Iterate through column's contents to decide which comparator to use
    for (var i = rowOffset; i < $rowList.length; i += 1) {
        if ($rowList[i].cells[colIdx - 1] === undefined) {
            continue;
        }

        var extractor = isDefined(extractorOrNull) ? extractorOrNull : Extractors.getDefaultExtractor();

        // $.trim trims leading/trailing whitespaces
        // $rowList[i].cells[colIdx - 1] is where we get the table cell from
        var textToCompare = $.trim(extractor($($rowList[i].cells[colIdx - 1])));

        // Store rows together with the innerText to compare
        store.push([textToCompare, $rowList[i], i]);

        if ((columnType === 0 || columnType === 1) && (0, _helper.isNumber)(textToCompare)) {
            columnType = 1;
        } else if ((columnType === 0 || columnType === 2) && (0, _helper.isDate)(textToCompare)) {
            columnType = 2;
        } else {
            columnType = 3;
        }
    }

    var comparator = isDefined(comparatorOrNull) ? comparatorOrNull : Comparators.getDefaultComparator(columnType);

    store.sort(function (x, y) {
        var compareResult = shouldSortAscending ? comparator(x[0].toUpperCase(), y[0].toUpperCase()) : comparator(y[0].toUpperCase(), x[0].toUpperCase());
        if (compareResult === 0) {
            return x[2] - y[2];
        }
        return compareResult;
    });

    // Must rewrap because .get() does not return a jQuery wrapped DOM node
    // and hence does not have the .children() function
    var $tbody = $($table.get(0)).children('tbody');

    if ($tbody.size < 1) {
        $tbody = $table;
    }

    // Must push to target tbody else it will generate a new tbody for the table
    for (var j = 0; j < store.length; j += 1) {
        $tbody.get(0).appendChild(store[j][1]);
    }

    store = null;
}

/**
 * Sorts a table
 * @param sortButtonClicked
 *     The jQuery object representing the sort button that was clicked.
 * @param comparatorStringOrNull
 *     String representing the function to compare 2 elements.
 *     May be null.
 * @param extractorStringOrNull
 *     String representing the function to pull out data from a table cell for comparison.
 *     May be null.
 */
function toggleSort($button, comparatorStringOrNull, extractorStringOrNull) {
    var isSortedAscending = $button.hasClass('button-sort-ascending');

    var $table = TableButtonHelpers.getEnclosingTable($button);
    var colIdx = TableButtonHelpers.getColumnPositionOfButton($button, 1);
    var comparatorOrNull = !isDefined(comparatorStringOrNull) ? null : Comparators[comparatorStringOrNull];
    var extractorOrNull = !isDefined(extractorStringOrNull) ? null : Extractors[extractorStringOrNull];
    var shouldSortAscending = !isSortedAscending;
    var rowToStartSortingFrom = 1; // <th> occupies row 0

    sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending, rowToStartSortingFrom);

    // update the button and icon states
    if (shouldSortAscending) {
        TableButtonHelpers.setButtonToSortedAscending($button);
    } else {
        TableButtonHelpers.setButtonToSortedDescending($button);
    }
}

exports.Comparators = Comparators;
exports.getPointValue = getPointValue;
exports.toggleSort = toggleSort;

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
 * Sets the chevron to point upwards.
 */
function setChevronToUp(chevronContainer) {
    chevronContainer.removeClass('glyphicon-chevron-down');
    chevronContainer.addClass('glyphicon-chevron-up');
}

/**
 * Sets the chevron to point downwards.
 */
function setChevronToDown(chevronContainer) {
    chevronContainer.removeClass('glyphicon-chevron-up');
    chevronContainer.addClass('glyphicon-chevron-down');
}

/**
 * Sets the chevron of a panel from up to down or from down to up depending on its current state.
 * clickedElement must be at least the parent of the chevron.
 */
function toggleChevron(clickedElement) {
    var $clickedElement = $(clickedElement);
    var isChevronDown = $clickedElement.find('.glyphicon-chevron-down').length > 0;
    var $chevronContainer = $clickedElement.find('.glyphicon-chevron-up, .glyphicon-chevron-down');

    // clearQueue to clear the animation queue to prevent animation build up
    $chevronContainer.clearQueue();

    if (isChevronDown) {
        setChevronToUp($chevronContainer);
    } else {
        setChevronToDown($chevronContainer);
    }
}

/**
 * Shows panel's content and changes chevron to point up.
 */
function showSingleCollapse(e) {
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    setChevronToUp($(glyphIcon[0]));
    $(e).collapse('show');
    $(heading).find('a.btn').show();
}

/**
 * Hides panel's content and changes chevron to point down.
 */
function hideSingleCollapse(e) {
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    setChevronToDown($(glyphIcon[0]));
    $(e).collapse('hide');
    $(heading).find('a.btn').hide();
}

/**
 * Changes the state of the panel (collapsed/expanded).
 */
function toggleSingleCollapse(e) {
    if ($(e.target).is('a') || $(e.target).is('input')) {
        return;
    }
    var glyphIcon = $(this).find('.glyphicon');
    var className = $(glyphIcon[0]).attr('class');
    if (className.indexOf('glyphicon-chevron-up') === -1) {
        showSingleCollapse($(e.currentTarget).attr('data-target'));
    } else {
        hideSingleCollapse($(e.currentTarget).attr('data-target'));
    }
}

function addLoadingIndicator(button, loadingText) {
    button.html(loadingText);
    button.prop('disabled', true);
    button.append('<img src="/images/ajax-loader.gif">');
}

function removeLoadingIndicator(button, displayText) {
    button.empty();
    button.html(displayText);
    button.prop('disabled', false);
}

/**
 * Highlights all words of searchKey (case insensitive), in a particular section
 * Format of the string  higlight plugin uses - ( ['string1','string2',...] )
 * @param searchKeyId - Id of searchKey input field
 * @param sectionToHighlight - sections to higlight separated by ',' (comma)
 *                             Example- '.panel-body, #panel-data, .sub-container'
 */
function highlightSearchResult(searchKeyId, sectionToHighlight) {
    var searchKey = $(searchKeyId).val().trim();
    // split search key string on symbols and spaces and add to searchKeyList
    var searchKeyList = [];
    if (searchKey.charAt(0) === '"' && searchKey.charAt(searchKey.length - 1) === '"') {
        searchKeyList.push(searchKey.replace(/"/g, '').trim());
    } else {
        $.each(searchKey.split(/[ "'.-]/), function () {
            searchKeyList.push($.trim(this));
        });
    }
    // remove empty elements from searchKeyList
    searchKeyList = searchKeyList.filter(function (n) {
        return n !== '';
    });
    $(sectionToHighlight).highlight(searchKeyList);
}

// Toggle the visibility of additional question information for the specified question.
function toggleAdditionalQuestionInfo(identifier) {
    var $questionButton = $('#questionAdditionalInfoButton-' + identifier);

    if ($questionButton.text() === $questionButton.attr('data-more')) {
        $questionButton.text($questionButton.attr('data-less'));
    } else {
        $questionButton.text($questionButton.attr('data-more'));
    }

    $('#questionAdditionalInfo-' + identifier).toggle();
}

/**
 * Disallow non-numeric entries
 * [Source: http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery]
 */
function disallowNonNumericEntries(element, decimalPointAllowed, negativeAllowed) {
    element.on('keydown', function (event) {
        var key = event.which;
        // Allow: backspace, delete, tab, escape, enter
        if ([46, 8, 9, 27, 13].indexOf(key) !== -1
        // Allow: Ctrl+A
        || key === 65 && event.ctrlKey
        // Allow: home, end, left, right
        || key >= 35 && key <= 39
        // Allow dot if decimal point is allowed
        || decimalPointAllowed && key === 190
        // Allow hyphen if negative is allowed
        // Code differs by browser (FF/Opera:109, IE/Chrome:189)
        // see http://www.javascripter.net/faq/keycodes.htm
        || negativeAllowed && (key === 189 || key === 109)) {
            // let it happen, don't do anything
            return;
        }
        // Ensure that it is a number and stop the keypress
        if (event.shiftKey || (key < 48 || key > 57) && (key < 96 || key > 105)) {
            event.preventDefault();
            event.stopPropagation();
        }
    });
}

exports.addLoadingIndicator = addLoadingIndicator;
exports.disallowNonNumericEntries = disallowNonNumericEntries;
exports.hideSingleCollapse = hideSingleCollapse;
exports.highlightSearchResult = highlightSearchResult;
exports.removeLoadingIndicator = removeLoadingIndicator;
exports.showSingleCollapse = showSingleCollapse;
exports.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;
exports.toggleChevron = toggleChevron;
exports.toggleSingleCollapse = toggleSingleCollapse;

/***/ }),
/* 7 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/* global bootbox:true */

/**
 * Ensures that a modal dialog is "opened" when clicking the selector.
 * It will not be actually opened, but rather will sent the contents of
 * the header and the body message to a designated space.
 *
 * <p>Button actions are not checked and are left for proper UI testing.
 *
 * @param {Object} assert The assert Object from the QUnit test function
 * @param {String} selector The selector of the object to be clicked
 * @param {String} header The expected header text of the modal dialog
 * @param {String} message The expected body message of the modal dialog
 */
function ensureCorrectModal(assert, selector, header, message) {
    $(selector).click();

    assert.equal($('#test-bootbox-modal-stub-title').html(), header, 'Header text should be correct.');
    assert.equal($('#test-bootbox-modal-stub-message').html(), message, 'Message text should be correct.');
}

/**
 * Clears the button used for testing bootbox event binding.
 */
function clearBootboxButtonClickEvent() {
    $(document).off('click', '#test-bootbox-button');
}

/**
 * Clears the div used for testing bootbox event binding.
 */
function clearBootboxModalStub() {
    $('#test-bootbox-modal-stub').html('');
}

var jQueryObjectStubForBootbox = {
    find: function find() {
        return this;
    },
    addClass: function addClass() {
        return this;
    }
};

bootbox.dialog = function (params) {
    $('#test-bootbox-modal-stub').html('<div id="test-bootbox-modal-stub-title">' + params.title + '</div>' + ('<div id="test-bootbox-modal-stub-message">' + params.message + '</div>'));
    return jQueryObjectStubForBootbox;
};

$.fn.ready = function () {
    // do not call the document ready functions as they are page-specific
};

$.ajax = function () {
    // do not actually make the AJAX request
};

exports.clearBootboxButtonClickEvent = clearBootboxButtonClickEvent;
exports.clearBootboxModalStub = clearBootboxModalStub;
exports.ensureCorrectModal = ensureCorrectModal;

/***/ }),
/* 8 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
 * Returns the value of a cookie given its name.
 * Returns null if the cookie is not set.
 */
function getCookie(cookieNameToFind) {
    var cookies = document.cookie.split('; ').map(function (s) {
        return s.split('=');
    });

    for (var i = 0; i < cookies.length; i += 1) {
        var cookieName = cookies[i][0];
        var cookieValue = cookies[i][1];

        // the cookie was found in the ith iteration
        if (cookieName === cookieNameToFind) {
            return cookieValue;
        }
    }

    // the cookie was not found
    return null;
}

function makeCsrfTokenParam() {
    var tokenParamName = 'token';
    var tokenCookieName = 'token';
    var tokenCookieValue = getCookie(tokenCookieName);

    return tokenParamName + '=' + tokenCookieValue;
}

exports.makeCsrfTokenParam = makeCsrfTokenParam;

/***/ }),
/* 9 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
 * Adds an event handler on all passed in divs to open datepicker on 'click'.
 * @assumption: all passed in divs are valid datepicker divs
 */
function triggerDatepickerOnClick(datepickerDivs) {
    $.each(datepickerDivs, function (i, datepickerDiv) {
        datepickerDiv.on('click', function () {
            if (!datepickerDiv.prop('disabled')) {
                datepickerDiv.datepicker('show');
            }
        });
    });
}

/**
 * @assumption: startDate has a valid value
 * @returns {Date} publishDate if it is valid and smaller than startDate, else startDate
 */
function getMaxDateForVisibleDate(startDate, publishDate) {
    var minDate = 0;

    if (publishDate === null || publishDate === undefined) {
        minDate = startDate;
    } else if (startDate > publishDate) {
        minDate = publishDate;
    } else {
        minDate = startDate;
    }

    return minDate;
}

/**
 * @assumption: visibleDate has a valid value
 * @returns {Date} visibleDate
 */
function getMinDateForPublishDate(visibleDate) {
    return visibleDate;
}

function prepareDatepickers() {
    var today = new Date();
    var yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    var tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    $('#startdate').datepicker({
        dateFormat: 'dd/mm/yy',
        showOtherMonths: true,
        gotoCurrent: true,
        defaultDate: today,
        onSelect: function onSelect() {
            var newVisibleDate = getMaxDateForVisibleDate($('#startdate').datepicker('getDate'), $('#publishdate').datepicker('getDate'));
            $('#visibledate').datepicker('option', 'maxDate', newVisibleDate);

            var newPublishDate = getMinDateForPublishDate($('#visibledate').datepicker('getDate'));
            $('#publishdate').datepicker('option', 'minDate', newPublishDate);
        }
    });

    $('#enddate').datepicker({
        dateFormat: 'dd/mm/yy',
        showOtherMonths: true,
        gotoCurrent: true,
        defaultDate: tomorrow
    });

    $('#visibledate').datepicker({
        dateFormat: 'dd/mm/yy',
        showOtherMonths: true,
        gotoCurrent: true,
        defaultDate: yesterday,
        maxDate: today,
        onSelect: function onSelect() {
            var newPublishDate = getMinDateForPublishDate($('#visibledate').datepicker('getDate'));
            $('#publishdate').datepicker('option', 'minDate', newPublishDate);
        }
    });

    $('#publishdate').datepicker({
        dateFormat: 'dd/mm/yy',
        showOtherMonths: true,
        gotoCurrent: true,
        defaultDate: tomorrow,
        onSelect: function onSelect() {
            var newVisibleDate = getMaxDateForVisibleDate($('#startdate').datepicker('getDate'), $('#publishdate').datepicker('getDate'));
            $('#visibledate').datepicker('option', 'maxDate', newVisibleDate);
        }
    });

    triggerDatepickerOnClick([$('#startdate'), $('#enddate'), $('#visibledate'), $('#publishdate')]);
}

exports.getMaxDateForVisibleDate = getMaxDateForVisibleDate;
exports.getMinDateForPublishDate = getMinDateForPublishDate;
exports.prepareDatepickers = prepareDatepickers;
exports.triggerDatepickerOnClick = triggerDatepickerOnClick;

/***/ }),
/* 10 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
function roundToThreeDp(num) {
    return parseFloat(num.toFixed(3));
}

function updateNumScalePossibleValues(questionNum) {
    var min = parseInt($('#minScaleBox-' + questionNum).val(), 10);
    var max = parseInt($('#maxScaleBox-' + questionNum).val(), 10);
    var step = parseFloat($('#stepBox-' + questionNum).val());

    if (max <= min) {
        max = min + 1;
        $('#maxScaleBox-' + questionNum).val(max);
    }

    step = roundToThreeDp(step);
    if (step === 0) {
        step = 0.001;
    }

    var $stepBox = $('#stepBox-' + questionNum);
    $stepBox.val(isNaN(step) ? '' : step);

    var possibleValuesCount = Math.floor(roundToThreeDp((max - min) / step)) + 1;
    var largestValueInRange = min + (possibleValuesCount - 1) * step;
    var $numScalePossibleValues = $('#numScalePossibleValues-' + questionNum);
    var possibleValuesString = void 0;
    if (roundToThreeDp(largestValueInRange) !== max) {
        $numScalePossibleValues.css('color', 'red');

        if (isNaN(min) || isNaN(max) || isNaN(step)) {
            possibleValuesString = '[Please enter valid numbers for all the options.]';
        } else {
            possibleValuesString = '[The interval ' + min.toString() + ' - ' + max.toString() + ' is not divisible by the specified increment.]';
        }

        $numScalePossibleValues.text(possibleValuesString);
        return false;
    }
    $numScalePossibleValues.css('color', 'black');
    possibleValuesString = '[Based on the above settings, acceptable responses are: ';

    // step is 3 d.p. at most, so round it after * 1000.
    if (possibleValuesCount > 6) {
        possibleValuesString += min.toString() + ', ' + (Math.round((min + step) * 1000) / 1000).toString() + ',\n            ' + (Math.round((min + 2 * step) * 1000) / 1000).toString() + ', ...,\n            ' + (Math.round((max - 2 * step) * 1000) / 1000).toString() + ',\n            ' + (Math.round((max - step) * 1000) / 1000).toString() + ', ' + max.toString();
    } else {
        possibleValuesString += min.toString();
        var cur = min + step;
        while (max - cur >= -1e-9) {
            possibleValuesString += ', ' + (Math.round(cur * 1000) / 1000).toString();
            cur += step;
        }
    }

    possibleValuesString += ']';
    $numScalePossibleValues.text(possibleValuesString);
    return true;
}

exports.roundToThreeDp = roundToThreeDp;
exports.updateNumScalePossibleValues = updateNumScalePossibleValues;

/***/ }),
/* 11 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
* Encodes a string for displaying in a HTML document.
* Uses an in-memory element created with jQuery.
* @param the string to be encoded
*/
function encodeHtmlString(stringToEncode) {
    return $('<div>').text(stringToEncode).html();
}

function escapeRegExp(string) {
    return string.replace(/([.*+?^=!:${}()|[\]/\\])/g, '\\$1');
}

/**
 * Helper function to replace all occurrences of a sub-string in a string.
 */
function replaceAll(string, find, replace) {
    return string.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

/**
 * Sanitizes special characters such as ' and \ to \' and \\ respectively
 */
function sanitizeForJs(rawString) {
    var string = rawString;
    string = replaceAll(string, '\\', '\\\\');
    string = replaceAll(string, '\'', '\\\'');
    return string;
}

exports.encodeHtmlString = encodeHtmlString;
exports.sanitizeForJs = sanitizeForJs;

/***/ }),
/* 12 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.setStatusMessageToForm = exports.setStatusMessage = exports.clearStatusMessages = exports.appendStatusMessage = undefined;

var _const = __webpack_require__(0);

var _scrollTo = __webpack_require__(4);

var DIV_STATUS_MESSAGE = '#statusMessagesToUser';

/**
 * Populates the status div with the message and the message status.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param status type
 * @return created status message div
 */
function populateStatusMessageDiv(message, status) {
    var $statusMessageDivToUser = $(DIV_STATUS_MESSAGE);
    var $statusMessageDivContent = $('<div></div>');

    // Default the status type to info if any invalid status is passed in
    var statusType = _const.StatusType.isValidType(status) ? status : _const.StatusType.INFO;

    $statusMessageDivContent.addClass('overflow-auto alert alert-' + statusType + ' icon-' + statusType + ' statusMessage');
    $statusMessageDivContent.html(message);

    $statusMessageDivToUser.empty();
    $statusMessageDivToUser.append($statusMessageDivContent);
    return $statusMessageDivToUser;
}

/**
 * Sets a status message and the message status.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param status type
 */
function setStatusMessage(message, status) {
    if (message === '' || message === undefined || message === null) {
        return;
    }
    var $statusMessageDivToUser = populateStatusMessageDiv(message, status);
    $statusMessageDivToUser.show();
    (0, _scrollTo.scrollToElement)($statusMessageDivToUser[0], { offset: -window.innerHeight / 2 });
}

/**
 * Sets a status message and the message status to a given form.
 * Default message type is info.
 *
 * @param message the text message to be shown to the user
 * @param status type
 * @param form form which should own the status
 */
function setStatusMessageToForm(message, status, form) {
    if (message === '' || message === undefined || message === null) {
        return;
    }
    // Copy the statusMessage and prepend to form
    var $copyOfStatusMessagesToUser = populateStatusMessageDiv(message, status).clone().show();
    $(DIV_STATUS_MESSAGE).remove();
    $(form).prepend($copyOfStatusMessagesToUser);
    var opts = {
        offset: -window.innerHeight / 8,
        duration: 1000
    };
    (0, _scrollTo.scrollToElement)($copyOfStatusMessagesToUser[0], opts);
}

/**
 * Appends the status messages panels into the current list of panels of status messages.
 * @param  messages the list of status message panels to be added (not just text)
 *
 */
function appendStatusMessage(messages) {
    var $statusMessagesToUser = $(DIV_STATUS_MESSAGE);

    $statusMessagesToUser.append($(messages));
    $statusMessagesToUser.show();
}

/**
 * Clears the status message div tag and hides it
 */
function clearStatusMessages() {
    var $statusMessagesToUser = $(DIV_STATUS_MESSAGE);

    $statusMessagesToUser.empty();
    $statusMessagesToUser.hide();
}

exports.appendStatusMessage = appendStatusMessage;
exports.clearStatusMessages = clearStatusMessages;
exports.setStatusMessage = setStatusMessage;
exports.setStatusMessageToForm = setStatusMessageToForm;

/***/ }),
/* 13 */,
/* 14 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _const = __webpack_require__(0);

var _adminHome = __webpack_require__(35);

QUnit.module('AdminHome.js');

QUnit.assert.contains = function (context, toIdentify, message) {
    var actual = context.indexOf(toIdentify) > -1;
    this.pushResult({
        result: actual,
        actual: actual,
        expected: toIdentify,
        message: message
    });
};

QUnit.test('createRowForResultTable(shortName, name, email, institution, isSuccess, status)', function (assert) {
    var boolIndex = 4;
    var successClass = 'success';
    var failureClass = 'danger';
    function testCreateRowForResultTable(isSuccess) {
        var testProperties = ['test', 'testName', 'testMail', 'testInstitution', isSuccess, 'testStatus'];
        var result = _adminHome.createRowForResultTable.apply(undefined, testProperties);
        var expected = testProperties.slice(); // deep clone testProperties
        expected[boolIndex] = isSuccess ? successClass : failureClass;
        expected.forEach(function (property) {
            assert.contains(result, property, 'should contain ' + property);
        });
    }
    [true, false].forEach(testCreateRowForResultTable);
});

QUnit.test('test conversion from instructor list to pipe-separated string', function (assert) {
    assert.expect(1);
    var instructorList = [_adminHome.Instructor.create('testShortName1', 'testName1', 'testEmail1@email.com', 'testInstitution1'), _adminHome.Instructor.create('testShortName2', 'testName2', 'testEmail2@email.com', 'testInstitution2'), _adminHome.Instructor.create('testShortName3', 'testName3', 'testEmail3@email.com', 'testInstitution3')];
    var instructorStringExpected = 'testName1 | testEmail1@email.com | testInstitution1\n' + 'testName2 | testEmail2@email.com | testInstitution2\n' + 'testName3 | testEmail3@email.com | testInstitution3';
    assert.equal(_adminHome.Instructor.allToString(instructorList), instructorStringExpected);
});

QUnit.test('test conversion from pipe-separated string to instructor list', function (assert) {
    assert.expect(1);
    var instructorString = 'testName1 | testEmail1@email.com | testInstitution1\n' + 'testName2  |   testEmail2@email.com | testInstitution2\n' + '     \t       \n' + 'testName3| testEmail3@email.com   |  testInstitution3\n' + '      \t                      \n';
    var instructorListExpected = [_adminHome.Instructor.create('testName1', 'testName1', 'testEmail1@email.com', 'testInstitution1'), _adminHome.Instructor.create('testName2', 'testName2', 'testEmail2@email.com', 'testInstitution2'), _adminHome.Instructor.create('testName3', 'testName3', 'testEmail3@email.com', 'testInstitution3')];
    assert.deepEqual(_adminHome.Instructor.allFromString(instructorString), instructorListExpected);
});

QUnit.test('test conversion from tab- and pipe-separated string to instructor list', function (assert) {
    assert.expect(1);
    var instructorString = 'testName1 | testEmail1@email.com | testInstitution1\n' + 'testName2\ttestEmail2@email.com\ttestInstitution2\n' + '\n' + ' \t  \n' + 'testName3 | testEmail3@email.com | testInstitution3';
    var instructorListExpected = [_adminHome.Instructor.create('testName1', 'testName1', 'testEmail1@email.com', 'testInstitution1'), _adminHome.Instructor.create('testName2', 'testName2', 'testEmail2@email.com', 'testInstitution2'), _adminHome.Instructor.create('testName3', 'testName3', 'testEmail3@email.com', 'testInstitution3')];
    assert.deepEqual(_adminHome.Instructor.allFromString(instructorString), instructorListExpected);
});

QUnit.test('test conversion from erroneous pipe-separated string to instructor list', function (assert) {
    assert.expect(1);

    var str1 = 'testName1  |   testEmail1@email.com | testInstitution1';
    var str2 = 'testName2 ||||| testInstitution2';
    var str3 = 'testName3|  testEmail3@email.com     |testInstitution3';
    var str4 = 'testName4 | testEmail4@email.com   | testInstitution4 | ????';
    var str5 = 'testName5| testEmail5@email.com | testInstitution5';
    var str6 = 'testName6 testEmail6@email.com | testInstitution6';

    var instructorString = [str1, str2, str3, str4, str5, str6].join('\n');
    var instructorListExpected = [_adminHome.Instructor.create('testName1', 'testName1', 'testEmail1@email.com', 'testInstitution1'), new _adminHome.InstructorError(_const.Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str2), _adminHome.Instructor.create('testName3', 'testName3', 'testEmail3@email.com', 'testInstitution3'), new _adminHome.InstructorError(_const.Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str4), _adminHome.Instructor.create('testName5', 'testName5', 'testEmail5@email.com', 'testInstitution5'), new _adminHome.InstructorError(_const.Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str6)];
    assert.deepEqual(_adminHome.Instructor.allFromString(instructorString), instructorListExpected);
});

/***/ }),
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _const = __webpack_require__(0);

var _helper = __webpack_require__(1);

var _questionNumScale = __webpack_require__(10);

var _sanitizer = __webpack_require__(11);

var _sortBy = __webpack_require__(5);

var _statusMessage = __webpack_require__(12);

var _ui = __webpack_require__(6);

QUnit.module('common.js');

/**
 * Warning: This test must be the first because it tests on the visibility
 * of the elements. Testing later may push the elements out of view.
 */
QUnit.test('isWithinView()', function (assert) {
    var testDiv = $('#visible');

    // Applies style to visible element and asserts whether it should be visible
    function assertWithStyle(style, condition) {
        testDiv.attr('style', 'position: absolute;' + style);

        var testString = 'Element with style ' + style + (condition ? ' is within view' : ' is not within view');
        assert.equal((0, _helper.isWithinView)(testDiv), condition, testString);
    }
    assertWithStyle('top: 0%;', true);
    assertWithStyle('top: 100%;', false);
    assertWithStyle('bottom: 0%;', true);
    assertWithStyle('bottom: 100%;', false);
    assertWithStyle('top: 0%; left: 0%;', true);
    assertWithStyle('top: 0%; left: -100%;', false);
    assertWithStyle('top: 0%; right: -100%;', false);
    assertWithStyle('top: 0%; right: 0%;', true);
});

QUnit.test('isNumber(num)', function (assert) {
    assert.equal((0, _helper.isNumber)('-0.001'), true, 'Negative double');
    assert.equal((0, _helper.isNumber)('12.056'), true, 'Positive double');
    assert.equal((0, _helper.isNumber)('100356'), true, 'Positive integer');
    assert.equal((0, _helper.isNumber)('-237'), true, 'Negative integer');
    assert.equal((0, _helper.isNumber)('ABCDE'), false, 'Letters');
    assert.equal((0, _helper.isNumber)('$12.57'), false, 'With Dollar Sign');
    assert.equal((0, _helper.isNumber)('12A5'), false, 'Letter in Numbers');
    assert.equal((0, _helper.isNumber)('0'), true, 'zero');
    assert.equal((0, _helper.isNumber)('   124    '), true, 'With Spacing');
    assert.equal((0, _helper.isNumber)('   12   4    '), false, 'With Spacing between');
});

/**
 * Test the whether the passed object is an actual date
 * with an accepted format
 *
 * Allowed formats : http://dygraphs.com/date-formats.html
 *
 * TEAMMATES currently follows the RFC2822 / IETF date syntax
 * e.g. 02 Apr 2012, 23:59
 */
QUnit.test('isDate(date)', function (assert) {
    assert.equal((0, _helper.isDate)('12432567'), false, 'Numbers');
    assert.equal((0, _helper.isDate)('0/0/0'), true, '0/0/0 - valid date on Firefox, invalid on Chrome');
    assert.equal((0, _helper.isDate)('12/2/13'), true, '12/2/13 - valid format');
    assert.equal((0, _helper.isDate)('12/02/2013'), true, '12/02/2013 - valid format (mm/dd/yyyy)');
    assert.equal((0, _helper.isDate)('28/12/2013'), true, '28/12/2013 - valid format (dd/mm/yyyy)');
    assert.equal((0, _helper.isDate)('12/12/13'), true, '12/02/13 - valid format');
    assert.equal((0, _helper.isDate)('2013-12-12'), true, '2013-12-12 - valid format');
    assert.equal((0, _helper.isDate)('28-12-2013'), false, '28-12-2013 - invalid format (dd-mm-yyyy)');
    assert.equal((0, _helper.isDate)('2013-12-28'), true, '2013-12-28 - valid format (yyyy-mm-dd)');
    assert.equal((0, _helper.isDate)('01 03 2003'), true, '01 03 2003 - valid format');
    assert.equal((0, _helper.isDate)('A1/B3/C003'), false, 'A1/B3/C003 - invalid date');
    assert.equal((0, _helper.isDate)('Abcdef'), false, 'Invalid Date string');
    assert.equal((0, _helper.isDate)('02 Apr 2012, 23:59'), true, 'Valid Date string with time');
    assert.equal((0, _helper.isDate)('02 Apr 2012'), true, 'Valid Date string without time');
    assert.equal((0, _helper.isDate)('    12/12/01'), true, 'With Spacing in front');
    assert.equal((0, _helper.isDate)('12 12 01       '), true, 'With Spacing behind');
    assert.equal((0, _helper.isDate)('            12-12-01       '), false, 'With Spacing, invalid on Firefox and valid on Chrome');
    assert.equal((0, _helper.isDate)('a12-12-2001'), false, 'a12-12-2001 - not in proper format');
    assert.equal((0, _helper.isDate)('    a      12 12 2001'), false, '    a      12 12 2001 - not in proper format');
    assert.equal((0, _helper.isDate)('12/12/2001   a  '), false, '12/12/2001   a  - not in proper format');
});

QUnit.test('scrollToTop()', function (assert) {
    // N/A, trivial function
    assert.expect(0);
});

QUnit.test('Comparators.sortBase(x, y)', function (assert) {
    assert.equal(_sortBy.Comparators.sortBase('abc', 'abc'), 0, 'Same text');
    assert.equal(_sortBy.Comparators.sortBase('ABC', 'abc'), -1, 'Bigger text');
    assert.equal(_sortBy.Comparators.sortBase('abc', 'ABC'), 1, 'Smaller text');
    assert.equal(_sortBy.Comparators.sortBase('abc', 'efg'), -1, 'Different text');
    assert.equal(_sortBy.Comparators.sortBase('ABC', 'efg'), -1, 'Bigger text');
    assert.equal(_sortBy.Comparators.sortBase('abc', 'EFG'), 1, 'Smaller text');
});

QUnit.test('Comparators.sortNum(x, y)', function (assert) {
    assert.equal(_sortBy.Comparators.sortNum('1', '2'), -1, 'x=1, y=2');
    assert.equal(_sortBy.Comparators.sortNum('-10', '2'), -12, 'x=-10, y=2');
    assert.equal(_sortBy.Comparators.sortNum('3', '-1'), 4, 'x=3, y=-1');
    assert.equal(_sortBy.Comparators.sortNum('0.1', '0.1'), 0, 'x=0.1, y=0.1');
    assert.equal(_sortBy.Comparators.sortNum('-0.1', '0.1'), -0.2, 'x=-0.1, y=0.1');
    assert.equal(_sortBy.Comparators.sortNum('0.1', '-0.1'), 0.2, 'x=-0.1, y=-0.1');
});

QUnit.test('Comparators.sortDate(x, y)', function (assert) {
    assert.equal(_sortBy.Comparators.sortDate('25 April 1999', '23 April 1999'), 1, '25 April 1999 - 23 April 1999');
    assert.equal(_sortBy.Comparators.sortDate('25 April 1999 2:00', '25 April 1999 1:59'), 1, '25 April 1999 2:00PM - 25 April 1999 1:59PM');
    assert.equal(_sortBy.Comparators.sortDate('25 April 1999 2:00', '25 April 1999 2:00'), 0, '25 April 1999 2:00PM - 25 April 1999 2:00PM');
    assert.equal(_sortBy.Comparators.sortDate('25 April 1999 2:00', '25 April 1999 2:01'), -1, '25 April 1999 2:00PM - 25 April 1999 2:01PM');
});

QUnit.test('getPointValue(s, ditchZero)', function (assert) {
    // getPointValue() is used by the application itself, thus
    // the inputs are always valid.
    assert.equal((0, _sortBy.getPointValue)('N/S', false), 201, 'Case N/S (feedback contribution not sure)');

    assert.equal((0, _sortBy.getPointValue)('N/A', false), 202, 'Case N/A');

    assert.equal((0, _sortBy.getPointValue)('0%', true), 0, 'Case 0% ditchZero true');
    assert.equal((0, _sortBy.getPointValue)('0%', false), 100, 'Case 0% ditchZero false');
    assert.equal((0, _sortBy.getPointValue)('1%', true), 101, 'Case 1%');
    assert.equal((0, _sortBy.getPointValue)('-1%', true), 99, 'Case -1%');

    assert.equal((0, _sortBy.getPointValue)('E -1%', false), 99, 'Case E -1%');
    assert.equal((0, _sortBy.getPointValue)('E +1%', false), 101, 'Case E +1%');
    assert.equal((0, _sortBy.getPointValue)('E +100%', false), 200, 'Case E +100%');
    assert.equal((0, _sortBy.getPointValue)('E -100%', false), 0, 'Case E -100%');

    assert.equal((0, _sortBy.getPointValue)('E', false), 100, 'Case E');

    assert.equal((0, _sortBy.getPointValue)('0', false), 100, 'Integer 0');
    assert.equal((0, _sortBy.getPointValue)('-1', false), 99, 'Integer -1');
    assert.equal((0, _sortBy.getPointValue)('1', false), 101, 'Integer 1');

    function isCloseEnough(numberA, numberB) {
        var tolerance = 0.0001;
        return Math.abs(numberA - numberB) < tolerance;
    }
    assert.ok(isCloseEnough((0, _sortBy.getPointValue)('0.0', false), 100), 'Float 0');
    assert.ok(isCloseEnough((0, _sortBy.getPointValue)('0.1', false), 100.1), 'Float 0.1');
    assert.ok(isCloseEnough((0, _sortBy.getPointValue)('-0.1', false), 99.9), 'Float -0.1');
    assert.ok(isCloseEnough((0, _sortBy.getPointValue)('1.91', false), 101.91), 'Float 1.91');
    assert.ok(isCloseEnough((0, _sortBy.getPointValue)('-1.22', false), 98.78), 'Float -1.22');
    assert.ok(isCloseEnough((0, _sortBy.getPointValue)('3.833333', false), 103.833333), 'Float 3.833333');
    assert.ok(isCloseEnough((0, _sortBy.getPointValue)('-3.833333', false), 96.166667), 'Float -3.833333');
});

QUnit.test('Comparators.sortByPoints(a, b)', function (assert) {
    assert.ok(_sortBy.Comparators.sortByPoints('N/S', 'N/A') < 0, 'Case N/S less than N/A');
    assert.ok(_sortBy.Comparators.sortByPoints('N/S', 'E') > 0, 'N/S more than E');
    assert.ok(_sortBy.Comparators.sortByPoints('N/A', 'E +1%') > 0, 'N/A more than E +(-)X%');
    assert.ok(_sortBy.Comparators.sortByPoints('N/S', 'E -1%') > 0, 'N/S more than E +(-)X%');

    assert.ok(_sortBy.Comparators.sortByPoints('0%', '0%') === 0, 'Case 0% equal 0%');
    assert.ok(_sortBy.Comparators.sortByPoints('-1%', '0%') > 0, 'Case 0% less than every X%');
    assert.ok(_sortBy.Comparators.sortByPoints('1%', '0%') > 0, 'Case 0% less than every X%');
    assert.ok(_sortBy.Comparators.sortByPoints('3%', '-2%') > 0, 'Case 3% more than -2%');

    assert.ok(_sortBy.Comparators.sortByPoints('E +1%', 'E') > 0, 'Case E +1% more than E');
    assert.ok(_sortBy.Comparators.sortByPoints('E -1%', 'E') < 0, 'Case E -1% less than E');
    assert.ok(_sortBy.Comparators.sortByPoints('E +33%', 'E -23%') > 0, 'Case E +33% more than E -23%');

    assert.ok(_sortBy.Comparators.sortByPoints('0', '-1') > 0, 'Case Integer 0 more than -1');
    assert.ok(_sortBy.Comparators.sortByPoints('1', '0') > 0, 'Case Integer 1 more than 0');
    assert.ok(_sortBy.Comparators.sortByPoints('2', '-3') > 0, 'Case Integer 2 more than -3');

    assert.ok(_sortBy.Comparators.sortByPoints('0.0', '1.0') < 0, 'Case Float 0.0 less than 1.0');
    assert.ok(_sortBy.Comparators.sortByPoints('0.3', '-1.1') > 0, 'Case Float 0.3 more than -1.1');
    assert.ok(_sortBy.Comparators.sortByPoints('0.3', '0.33338') < 0, 'Case Float 0.3 less than 0.33338');
    assert.ok(_sortBy.Comparators.sortByPoints('-4.33333', '-4.5') > 0, 'Case Float -4.33333 more than -4.5');

    assert.ok(_sortBy.Comparators.sortByPoints('NotNumber', 'Random') === 0, 'Equality for NaN');
});

QUnit.test('setStatusMessage(message,status)', function (assert) {
    $('body').append('<div id="statusMessagesToUser"></div>');
    var message = 'Status Message';

    // isError = false: class = overflow-auto alert alert-warning
    // isError = true: class = overflow-auto alert alert-danger

    function getExpectedClasses(statusType) {
        return 'overflow-auto alert alert-' + statusType + ' icon-' + statusType + ' statusMessage';
    }

    (0, _statusMessage.setStatusMessage)(message);
    assert.equal($('#statusMessagesToUser .statusMessage').html(), message, 'Normal status message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === getExpectedClasses(_const.StatusType.DEFAULT), 'Default message status without specifying status of message (info)');
    (0, _statusMessage.clearStatusMessages)();

    (0, _statusMessage.setStatusMessage)(message, _const.StatusType.INFO);
    assert.equal($('#statusMessagesToUser .statusMessage').html(), message, 'Normal status message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === getExpectedClasses(_const.StatusType.INFO), 'Info message status by specifying status of message (StatusType.INFO)');
    (0, _statusMessage.clearStatusMessages)();

    (0, _statusMessage.setStatusMessage)(message, _const.StatusType.SUCCESS);
    assert.equal($('#statusMessagesToUser .statusMessage').html(), message, 'Normal status message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === getExpectedClasses(_const.StatusType.SUCCESS), 'Success message status by specifying status of message (StatusType.SUCCESS)');
    (0, _statusMessage.clearStatusMessages)();

    (0, _statusMessage.setStatusMessage)(message, _const.StatusType.WARNING);
    assert.equal($('#statusMessagesToUser .statusMessage').html(), message, 'Normal status message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === getExpectedClasses(_const.StatusType.WARNING), 'Warning message status by specifying status of message (StatusType.WARNING)');
    (0, _statusMessage.clearStatusMessages)();

    (0, _statusMessage.setStatusMessage)(message, _const.StatusType.DANGER);
    assert.equal($('#statusMessagesToUser .statusMessage').html(), message, 'Normal status message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === getExpectedClasses(_const.StatusType.DANGER), 'Danger message status by specifying status of message (StatusType.DANGER)');
    (0, _statusMessage.clearStatusMessages)();

    (0, _statusMessage.setStatusMessage)('');
    assert.equal($('#statusMessagesToUser .statusMessage').html(), undefined, 'Empty message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === undefined, 'Empty message without status');
    (0, _statusMessage.clearStatusMessages)();

    (0, _statusMessage.setStatusMessage)('', _const.StatusType.INFO);
    assert.equal($('#statusMessagesToUser .statusMessage').html(), undefined, 'Empty message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === undefined, 'Empty message with status (any status will be the same)');
    (0, _statusMessage.clearStatusMessages)();

    (0, _statusMessage.setStatusMessage)(message, 'random');
    assert.equal($('#statusMessagesToUser .statusMessage').html(), message, 'Normal status message');
    assert.ok($('#statusMessagesToUser .statusMessage').attr('class') === getExpectedClasses(_const.StatusType.DEFAULT), 'Message with random status (defaulted to info)');
});

QUnit.test('clearStatusMessages()', function (assert) {
    (0, _statusMessage.clearStatusMessages)();
    assert.equal($('#statusMessagesToUser').html(), '', 'Status message cleared');
    assert.ok($('#statusMessagesToUser').css('background-color') === 'rgba(0, 0, 0, 0)' || $('#statusMessagesToUser').css('background-color') === 'transparent', 'No background');
});

QUnit.test('checkEvaluationForm()', function (assert) {
    // N/A, requires elements in the page
    assert.expect(0);
});

QUnit.test('addLoadingIndicator()', function (assert) {
    var $fixture = $('#qunit-fixture');
    $fixture.append('<button>Submit</button>');

    var $button = $('button', $fixture);
    var buttonText = 'Loading';
    (0, _ui.addLoadingIndicator)($button, buttonText);

    assert.equal($button.text(), buttonText, 'Button text changes to ' + buttonText);
    assert.equal($button.find('img').attr('src'), '/images/ajax-loader.gif', 'Loading gif appended');
    assert.ok($button.is(':disabled'), 'Button disabled');
});

QUnit.test('removeLoadingIndicator()', function (assert) {
    var $fixture = $('#qunit-fixture');
    $fixture.append('<button>Submit</button>');

    var $button = $('button', $fixture);
    var buttonText = 'Complete';
    (0, _ui.removeLoadingIndicator)($button, buttonText);

    assert.equal($button.text(), buttonText, 'Button text changes to ' + buttonText);
    assert.equal($button.find('img').length, 0, 'Loading gif removed');
    assert.notOk($button.is(':disabled'), 'Button enabled');
});

QUnit.test('roundToThreeDp(num)', function (assert) {
    assert.equal((0, _questionNumScale.roundToThreeDp)(0), 0, 'Zero test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(1), 1, 'Positive integer test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(-1), -1, 'Negative integer test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(1.001), 1.001, 'Three dp positive number test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(-1.001), -1.001, 'Three dp negative number test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(1.0015), 1.002, 'Four dp positive number rounding up test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(1.0011), 1.001, 'Four dp negative number rounding down test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(-1.0015), -1.002, 'Four dp positive number rounding "up" test');
    assert.equal((0, _questionNumScale.roundToThreeDp)(-1.0011), -1.001, 'Four dp negative number rounding "down" test');
});

QUnit.test('sanitizeForJs(string)', function (assert) {
    assert.equal((0, _sanitizer.sanitizeForJs)(''), '', 'sanitization for empty string');
    assert.equal((0, _sanitizer.sanitizeForJs)('Will o\' Wisp'), 'Will o\\\' Wisp', 'sanitization for single quote');
    assert.equal((0, _sanitizer.sanitizeForJs)('Will o\'\'\'\'\'\\\\ Wisp'), 'Will o\\\'\\\'\\\'\\\'\\\'\\\\\\\\ Wisp', 'sanitization for single quote and slash \\');
});

/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _datepicker = __webpack_require__(9);

QUnit.module('datepicker.js');

QUnit.test('triggerDatepickerOnClick(datepickerDivs)', function (assert) {
        assert.expect(2);

        $('#date-picker-div').datepicker();
        assert.equal($('.ui-datepicker-calendar:visible').length, 0, 'Datepicker is hidden initially');
        (0, _datepicker.triggerDatepickerOnClick)([$('#date-picker-div')]);
        $('#date-picker-div').click();

        assert.equal($('.ui-datepicker-calendar:visible').length, 1, 'Displays datepickers after trigger');
});

QUnit.test('getMaxDateForVisibleDate(startDate, publishDate)', function (assert) {
        assert.expect(5);

        var startDate = new Date(2017, 3, 19, 2, 31, 0, 0);
        var publishDate = new Date(2017, 3, 19, 2, 30, 0, 0);

        assert.equal((0, _datepicker.getMaxDateForVisibleDate)(startDate, null), startDate, 'Returns startDate when publishDate is null');
        assert.equal((0, _datepicker.getMaxDateForVisibleDate)(startDate, undefined), startDate, 'Returns startDate when publishDate is undefined');
        assert.equal((0, _datepicker.getMaxDateForVisibleDate)(startDate, publishDate), publishDate, 'Returns publishDate when startDate > publishDate');
        assert.equal((0, _datepicker.getMaxDateForVisibleDate)(startDate, startDate), startDate, 'Returns startDate when startDate = publishDate');

        startDate = new Date(2017, 3, 19, 2, 29, 0, 0);

        assert.equal((0, _datepicker.getMaxDateForVisibleDate)(startDate, publishDate), startDate, 'Returns startDate when startDate < publishDate');
});

QUnit.test('getMinDateForPublishDate(visibleDate)', function (assert) {
        assert.expect(1);

        var visibleDate = new Date(2017, 3, 19, 2, 31, 0, 0);

        assert.equal((0, _datepicker.getMinDateForPublishDate)(visibleDate), visibleDate, 'Returns visibleDate');
});

/***/ }),
/* 17 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _instructorCourseEnrollPage = __webpack_require__(37);

QUnit.module('instructorCourseEnrollPage.js');

QUnit.test('isUserTyping(strText)', function (assert) {
    assert.equal((0, _instructorCourseEnrollPage.isUserTyping)('no separator \n'), true, 'Manually typing, no pipe');
    assert.equal((0, _instructorCourseEnrollPage.isUserTyping)('field1 | field2 \n'), false, 'Manually typing, with pipe');
    assert.equal((0, _instructorCourseEnrollPage.isUserTyping)('navie \tquahuynh@gmail.com \tcoder \n'), false, 'Copied from spreadsheet');
});

/***/ }),
/* 18 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _instructorFeedbackEdit = __webpack_require__(38);

QUnit.module('instructorFeedbacks.js');

QUnit.test('extractQuestionNumFromEditFormId(id)', function (assert) {
    // Tests that extracting question number from form is correct.
    for (var i = 1; i < 1000; i += 1) {
        var id = 'form_editquestion-' + i;
        assert.equal((0, _instructorFeedbackEdit.extractQuestionNumFromEditFormId)(id), i);
    }
});

/***/ }),
/* 19 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _instructor = __webpack_require__(3);

QUnit.module('instructor.js');

QUnit.test('executeCopyCommand()', function (assert) {
    // override execCommand with mock
    var browserImplementation = document.execCommand;
    document.execCommand = function (command) {
        assert.equal(command, 'copy', 'Copy command is executed');
    };

    (0, _instructor.executeCopyCommand)();

    // restore back the original execCommand
    document.execCommand = browserImplementation;
});

QUnit.test('selectElementContents(el)', function (assert) {
    window.getSelection().removeAllRanges();

    var $contentsToSelect = $('#team_all');
    (0, _instructor.selectElementContents)($contentsToSelect.get(0));

    var selectedContents = window.getSelection().toString();
    assert.equal(selectedContents, $contentsToSelect.text().replace(/ /gi, ''), 'Contents are selected');

    window.getSelection().removeAllRanges();
});

/***/ }),
/* 20 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _CommonTestFunctions = __webpack_require__(7);

var _const = __webpack_require__(0);

var _student = __webpack_require__(33);

QUnit.module('student.js');

QUnit.test('bindLinkInUnregisteredPage(selector)', function (assert) {
    (0, _CommonTestFunctions.clearBootboxButtonClickEvent)();
    (0, _student.bindLinksInUnregisteredPage)('#test-bootbox-button');
    (0, _CommonTestFunctions.ensureCorrectModal)(assert, '#test-bootbox-button', _const.Const.ModalDialog.UNREGISTERED_STUDENT.header, _const.Const.ModalDialog.UNREGISTERED_STUDENT.text);
    (0, _CommonTestFunctions.clearBootboxModalStub)();
});

/***/ }),
/* 21 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _index = __webpack_require__(36);

QUnit.module('index.js');

QUnit.test('submissionCounter(currDate, baseDate)', function (assert) {
    var currentDate = new Date(2013, 11, 21);
    var baseDate = new Date(2014, 11, 12);
    var submissionPerHour = 2;
    var baseCount = 36000;
    var errorMsg = 'Thousands of';
    // Current date being null should result in default message
    assert.equal((0, _index.submissionCounter)(null, baseDate, submissionPerHour, baseCount), errorMsg);
    // Base date being null should result in default message
    assert.equal((0, _index.submissionCounter)(currentDate, null, submissionPerHour, baseCount), errorMsg);
    // Base date being ahead of current date should result in default message
    assert.equal((0, _index.submissionCounter)(currentDate, baseDate, submissionPerHour, baseCount), errorMsg);
    // Check the function with base and current date being in same month
    var baseDate1 = new Date(2013, 11, 20);
    assert.equal((0, _index.submissionCounter)(currentDate, baseDate1, submissionPerHour, baseCount), '36,048');
    // Check the function with valid base and current date.
    // The resulting value should be (current date -  base date)* submission per hour + base count
    var baseDate2 = new Date(2013, 10, 30);
    assert.equal((0, _index.submissionCounter)(currentDate, baseDate2, submissionPerHour, baseCount), '37,008');
    // Check the function with a result that requires multiple ','
    var baseDate3 = new Date(2016, 10, 30);
    var currentDate2 = new Date(2076, 11, 21);
    assert.equal((0, _index.submissionCounter)(currentDate2, baseDate3, submissionPerHour, baseCount), '1,088,928');
});

/***/ }),
/* 22 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var AssertionFailedError = function (_Error) {
    _inherits(AssertionFailedError, _Error);

    function AssertionFailedError() {
        _classCallCheck(this, AssertionFailedError);

        return _possibleConstructorReturn(this, (AssertionFailedError.__proto__ || Object.getPrototypeOf(AssertionFailedError)).apply(this, arguments));
    }

    return AssertionFailedError;
}(Error);

function assert(condition, message) {
    if (!condition) {
        throw new AssertionFailedError(message || 'Assertion Failed');
    }
}

function assertDefined(expr, message) {
    assert(expr !== undefined && expr != null, message);
}

exports.AssertionFailedError = AssertionFailedError;
exports.assert = assert;
exports.assertDefined = assertDefined;

/***/ }),
/* 23 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
// Browser Compatibility and support
var MSIE = 'Microsoft Internet Explorer';
var MSIE_LOWEST_VERSION = 9;
var CHROME = 'Chrome';
var CHROME_LOWEST_VERSION = 15;
var FIREFOX = 'Firefox';
var FIREFOX_LOWEST_VERSION = 12;
var SAFARI = 'Safari';
var SAFARI_LOWEST_VERSION = 4;

/**
 * Function to check browser version and alert if browser version is lower than supported
 * Adapted from http://www.javascripter.net/faq/browsern.htm
 *
 */

function checkBrowserVersion() {
    var nAgt = navigator.userAgent;
    var browserName = navigator.appName;
    var fullVersion = parseFloat(navigator.appVersion);
    var majorVersion = parseInt(navigator.appVersion, 10);
    var verOffset = void 0;
    var supported = true;

    /* eslint-disable no-negated-condition */ // usage of .contains() equivalent requires !==
    if (nAgt.indexOf('MSIE') !== -1) {
        // In MSIE, the true version is after "MSIE" in userAgent
        verOffset = nAgt.indexOf('MSIE');
        browserName = MSIE;
        fullVersion = nAgt.substring(verOffset + 5);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < MSIE_LOWEST_VERSION) {
            supported = false;
        }
    } else if (nAgt.indexOf('Chrome') !== -1) {
        // In Chrome, the true version is after "Chrome"
        verOffset = nAgt.indexOf('Chrome');
        browserName = CHROME;
        fullVersion = nAgt.substring(verOffset + 7);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < CHROME_LOWEST_VERSION) {
            supported = false;
        }
    } else if (nAgt.indexOf('Safari') !== -1) {
        // In Safari, the true version is after "Safari" or after "Version"
        verOffset = nAgt.indexOf('Safari');
        browserName = SAFARI;
        fullVersion = nAgt.substring(verOffset + 7);
        if (nAgt.indexOf('Version') !== -1) {
            verOffset = nAgt.indexOf('Version');
            fullVersion = nAgt.substring(verOffset + 8);
        }
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < SAFARI_LOWEST_VERSION) {
            supported = false;
        }
    } else if (nAgt.indexOf('Firefox') !== -1) {
        // In Firefox, the true version is after "Firefox"
        verOffset = nAgt.indexOf('Firefox');
        browserName = FIREFOX;
        fullVersion = nAgt.substring(verOffset + 8);
        majorVersion = parseInt(fullVersion, 10);
        if (majorVersion < FIREFOX_LOWEST_VERSION) {
            supported = false;
        }
    } else {
        // In most other browsers, "name/version" is at the end of userAgent
        browserName = 'Unsupported';
        fullVersion = 0;
        supported = false;
    }
    /* eslint-enable no-negated-condition */

    if (!supported) {
        var unsupportedBrowserErrorString = 'You are currently using ' + browserName + ' v.' + majorVersion + '. ' + 'This web browser is not officially supported by TEAMMATES. ' + 'In case this web browser does not display the webpage correctly, ' + 'you may wish to view it in the following supported browsers: <br>' + '<table>' + '<tr>' + ('<td width="50%"> - ' + MSIE + ' ' + MSIE_LOWEST_VERSION + '+</td>') + ('<td> - ' + CHROME + ' ' + CHROME_LOWEST_VERSION + '+</td>') + '</tr>' + '<tr>' + ('<td> - ' + FIREFOX + ' ' + FIREFOX_LOWEST_VERSION + '+</td>') + ('<td> - ' + SAFARI + ' ' + SAFARI_LOWEST_VERSION + '+</td>') + '</tr>' + '</table>';

        var message = $('#browserMessage');
        message.css('display', 'block');
        message.html(unsupportedBrowserErrorString);
    }
}

exports.checkBrowserVersion = checkBrowserVersion;

/***/ }),
/* 24 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
var FeedbackPath = {
    attachEvents: function attachEvents() {
        var allDropdownOptions = $('.feedback-path-dropdown-option');
        FeedbackPath.attachEventsForAllOptions(allDropdownOptions);

        var commonOptions = allDropdownOptions.not('.feedback-path-dropdown-option-other');
        FeedbackPath.attachEventsForCommonOptions(commonOptions);

        var otherOption = $('.feedback-path-dropdown-option-other');
        FeedbackPath.attachEventsForOtherOption(otherOption);
    },
    attachEventsForAllOptions: function attachEventsForAllOptions(allDropdownOptions) {
        allDropdownOptions.on('click', function (event) {
            var clickedElem = $(event.target);
            var containingForm = FeedbackPath.getContainingForm(clickedElem);

            FeedbackPath.setDropdownText(clickedElem.data('pathDescription'), containingForm);
        });
    },
    attachEventsForCommonOptions: function attachEventsForCommonOptions(commonOptions) {
        commonOptions.on('click', function (event) {
            var clickedElem = $(event.target);
            var containingForm = FeedbackPath.getContainingForm(clickedElem);

            FeedbackPath.updateInputTags(clickedElem.data('giverType'), clickedElem.data('recipientType'), containingForm);
            FeedbackPath.hideOtherOption(containingForm);
        });
    },
    attachEventsForOtherOption: function attachEventsForOtherOption(otherOption) {
        otherOption.on('click', function (event) {
            var clickedElem = $(event.target);
            var containingForm = FeedbackPath.getContainingForm(clickedElem);

            FeedbackPath.showOtherOption(containingForm);
        });
    },
    showOtherOption: function showOtherOption(containingForm) {
        containingForm.find('.feedback-path-others').show();
    },
    hideOtherOption: function hideOtherOption(containingForm) {
        containingForm.find('.feedback-path-others').hide();
        containingForm.find('[class*= numberOfEntitiesElements]').hide();
    },
    updateInputTags: function updateInputTags(giverType, recipientType, containingForm) {
        containingForm.find('[id^=givertype]').val(giverType);
        containingForm.find('[id^=givertype]').trigger('change');

        containingForm.find('[id^=recipienttype]').val(recipientType);
        containingForm.find('[id^=recipienttype]').trigger('change');
    },
    getDropdownText: function getDropdownText(containingForm) {
        var feedbackPathDropdown = containingForm.find('.feedback-path-dropdown');
        return feedbackPathDropdown.find('button').html();
    },
    setDropdownText: function setDropdownText(text, containingForm) {
        var feedbackPathDropdown = containingForm.find('.feedback-path-dropdown');
        feedbackPathDropdown.find('button').html(text);
    },
    getContainingForm: function getContainingForm(elem) {
        return elem.closest('form');
    },
    isCommonOptionSelected: function isCommonOptionSelected(containingForm) {
        return containingForm.find('.feedback-path-dropdown > button').html().trim() !== 'Predefined combinations:';
    }
};

exports.FeedbackPath = FeedbackPath;

/***/ }),
/* 25 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.updateUncommonSettingsInfo = exports.showUncommonPanelsIfNotInDefaultValues = exports.formatSessionVisibilityGroup = exports.formatResponsesVisibilityGroup = exports.collapseIfPrivateSession = exports.bindUncommonSettingsEvents = undefined;

var _const = __webpack_require__(0);

function updateUncommonSettingsSessionVisibilityInfo() {
    var info = 'Session is visible at submission opening time, ' + 'responses are only visible when you publish the results.';

    $('#uncommonSettingsSessionResponsesVisibleInfoText').html(info);
}

function updateUncommonSettingsEmailSendingInfo() {
    var info = 'Emails are sent when session opens (within 15 mins), ' + '24 hrs before session closes and when results are published.';

    $('#uncommonSettingsSendEmailsInfoText').html(info);
}

function updateUncommonSettingsInfo() {
    updateUncommonSettingsSessionVisibilityInfo();
    updateUncommonSettingsEmailSendingInfo();
}

function isDefaultSessionResponsesVisibleSetting() {
    return $('#sessionVisibleFromButton_atopen').prop('checked') && $('#resultsVisibleFromButton_later').prop('checked');
}

function isDefaultSendEmailsSetting() {
    return $('#sendreminderemail_open').prop('checked') && $('#sendreminderemail_closing').prop('checked') && $('#sendreminderemail_published').prop('checked');
}

function showUncommonPanelsForSessionResponsesVisible() {
    var $sessionResponsesVisiblePanel = $('#sessionResponsesVisiblePanel');

    $('#uncommonSettingsSessionResponsesVisible').after($sessionResponsesVisiblePanel);
    $sessionResponsesVisiblePanel.show();
    $('#uncommonSettingsSessionResponsesVisibleInfoText').parent().hide();
}

function showUncommonPanelsForSendEmails() {
    var $sendEmailsForPanel = $('#sendEmailsForPanel');

    $('#uncommonSettingsSendEmails').after($sendEmailsForPanel);
    $sendEmailsForPanel.show();
    $('#uncommonSettingsSendEmailsInfoText').parent().hide();
}

function showUncommonPanelsIfNotInDefaultValues() {
    if (!isDefaultSessionResponsesVisibleSetting()) {
        showUncommonPanelsForSessionResponsesVisible();
    }

    if (!isDefaultSendEmailsSetting()) {
        showUncommonPanelsForSendEmails();
    }
}

function bindUncommonSettingsEvents() {
    $('#editUncommonSettingsSessionResponsesVisibleButton').click(showUncommonPanelsForSessionResponsesVisible);
    $('#editUncommonSettingsSendEmailsButton').click(showUncommonPanelsForSendEmails);
}

/**
 * Saves the (disabled) state of the element in attribute data-last.<br>
 * Toggles whether the given element {@code id} is disabled or not based on
 * {@code bool}.<br>
 * Disabled if true, enabled if false.
 */
function toggleDisabledAndStoreLast(id, bool) {
    $('#' + id).prop('disabled', bool);
    $('#' + id).data('last', $('#' + id).prop('disabled'));
}

/**
 * Collapses/hides unnecessary fields/cells/tables if private session option is selected.
 */
function collapseIfPrivateSession() {
    if ($('[name=' + _const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + ']').filter(':checked').val() === 'never') {
        $('#timeFramePanel, #instructionsRow, #responsesVisibleFromColumn').hide();
    } else {
        $('#timeFramePanel, #instructionsRow, #responsesVisibleFromColumn').show();
    }
}

/**
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatResponsesVisibilityGroup() {
    var $responsesVisibilityBtnGroup = $('[name=' + _const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON + ']');
    $responsesVisibilityBtnGroup.change(function () {
        if ($responsesVisibilityBtnGroup.filter(':checked').val() === 'custom') {
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, false);
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME, false);
        } else {
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, true);
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME, true);
        }
    });
}

/**
 * Hides / shows the 'Submissions Opening/Closing Time' and 'Grace Period' options
 * depending on whether a private session is selected.<br>
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatSessionVisibilityGroup() {
    var $sessionVisibilityBtnGroup = $('[name=' + _const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON + ']');
    $sessionVisibilityBtnGroup.change(function () {
        collapseIfPrivateSession();
        if ($sessionVisibilityBtnGroup.filter(':checked').val() === 'custom') {
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, false);
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME, false);
        } else {
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, true);
            toggleDisabledAndStoreLast(_const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME, true);
        }
    });
}

exports.bindUncommonSettingsEvents = bindUncommonSettingsEvents;
exports.collapseIfPrivateSession = collapseIfPrivateSession;
exports.formatResponsesVisibilityGroup = formatResponsesVisibilityGroup;
exports.formatSessionVisibilityGroup = formatSessionVisibilityGroup;
exports.showUncommonPanelsIfNotInDefaultValues = showUncommonPanelsIfNotInDefaultValues;
exports.updateUncommonSettingsInfo = updateUncommonSettingsInfo;

/***/ }),
/* 26 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.updateConstSumPointsValue = exports.removeConstSumOption = exports.hideConstSumOptionTable = exports.addConstSumOption = undefined;

var _const = __webpack_require__(0);

function updateConstSumPointsValue(questionNum) {
    if ($('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS + '-' + questionNum).val() < 1) {
        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS + '-' + questionNum).val(1);
    }
    if ($('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION + '-' + questionNum).val() < 1) {
        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION + '-' + questionNum).val(1);
    }
    if ($('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT + '-' + questionNum).val() < 1) {
        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT + '-' + questionNum).val(1);
    }
}

function addConstSumOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var curNumberOfChoiceCreated = parseInt($('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(), 10);

    $('\n    <div class="margin-bottom-7px" id="constSumOptionRow-' + curNumberOfChoiceCreated + '-' + questionNum + '">\n        <div class="input-group width-100-pc">\n            <input type="text" name="' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + '-' + curNumberOfChoiceCreated + '"\n                    id="' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + '-' + curNumberOfChoiceCreated + '-' + questionNum + '"\n                    class="form-control constSumOptionTextBox">\n            <span class="input-group-btn">\n                <button class="btn btn-default removeOptionLink" id="constSumRemoveOptionLink"\n                        onclick="removeConstSumOption(' + curNumberOfChoiceCreated + ', ' + questionNum + ')"\n                        tabindex="-1">\n                    <span class="glyphicon glyphicon-remove"></span>\n                </button>\n            </span>\n        </div>\n    </div>\n    ').insertBefore($('#constSumAddOptionRow-' + questionNum));

    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function hideConstSumOptionTable(questionNum) {
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE + '-' + questionNum).hide();
}

function removeConstSumOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var $thisRow = $('#constSumOptionRow-' + index + '-' + questionNum);

    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;

    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

exports.addConstSumOption = addConstSumOption;
exports.hideConstSumOptionTable = hideConstSumOptionTable;
exports.removeConstSumOption = removeConstSumOption;
exports.updateConstSumPointsValue = updateConstSumPointsValue;

/***/ }),
/* 27 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
function setDefaultContribQnVisibilityIfNeeded(questionNum) {
    // If visibility options have already been copied from the previous contrib question, skip
    var hasPreviousQuestion = $('.questionTable').size() >= 2;
    if (hasPreviousQuestion) {
        var previousQuestionType = $('input[name="questiontype"]').eq(-2).val();
        if (previousQuestionType === 'CONTRIB') {
            return;
        }
    }

    var $currentQuestionTable = $('#questionTable-' + questionNum);

    $currentQuestionTable.find('a[data-option-name="ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS"]').click();
}

function setContribQnVisibilityFormat(questionNum) {
    var $currentQuestionTable = $('#questionTable-' + questionNum);

    // Show only the two visibility options valid for contrib questions; hide the rest
    $currentQuestionTable.find('.visibility-options-dropdown-option').not('[data-option-name="ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS"]').not('[data-option-name="VISIBLE_TO_INSTRUCTORS_ONLY"]').parent().addClass('hidden');
    $currentQuestionTable.find('.visibility-options-dropdown .dropdown-menu .divider').addClass('hidden');

    // Format checkboxes 'Can See Answer' for recipient/giver's team members/recipient's team members must be the same.

    $currentQuestionTable.find('input.visibilityCheckbox').off('change');

    $currentQuestionTable.find('input.visibilityCheckbox').filter('.answerCheckbox').change(function () {
        if (!$(this).prop('checked')) {
            if ($(this).val() === 'RECEIVER' || $(this).val() === 'OWN_TEAM_MEMBERS' || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
                $currentQuestionTable.find('input.visibilityCheckbox').filter('input[class*="giverCheckbox"],input[class*="recipientCheckbox"]').filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]').prop('checked', false);
            } else {
                var visibilityOptionsRow = $(this).closest('tr');
                visibilityOptionsRow.find('input[class*="giverCheckbox"]').prop('checked', false);
                visibilityOptionsRow.find('input[class*="recipientCheckbox"]').prop('checked', false);
            }
        }

        if ($(this).val() === 'RECEIVER' || $(this).val() === 'OWN_TEAM_MEMBERS' || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox').filter('input[name=receiverFollowerCheckbox]').prop('checked', $(this).prop('checked'));
        }

        if ($(this).val() === 'RECEIVER' || $(this).val() === 'OWN_TEAM_MEMBERS' || $(this).val() === 'RECEIVER_TEAM_MEMBERS') {
            $currentQuestionTable.find('input.visibilityCheckbox').filter('.answerCheckbox').filter('[value="RECEIVER"],[value="OWN_TEAM_MEMBERS"],[value="RECEIVER_TEAM_MEMBERS"]').prop('checked', $(this).prop('checked'));
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="giverCheckbox"]').change(function () {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input.answerCheckbox').prop('checked', true).trigger('change');
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[class*="recipientCheckbox"]').change(function () {
        if ($(this).is(':checked')) {
            var visibilityOptionsRow = $(this).closest('tr');
            visibilityOptionsRow.find('input.answerCheckbox').prop('checked', true).trigger('change');
        }
    });

    $currentQuestionTable.find('input.visibilityCheckbox').filter('[name=receiverLeaderCheckbox]').change(function () {
        var visibilityOptionsRow = $(this).closest('tr');
        visibilityOptionsRow.find('input[name=receiverFollowerCheckbox]').prop('checked', $(this).prop('checked'));
    });
}

function fixContribQnGiverRecipient(questionNum) {
    var $giverType = $('#givertype-' + questionNum);
    var $recipientType = $('#recipienttype-' + questionNum);
    var $questionTable = $('#questionTable-' + questionNum);

    // Fix giver->recipient to be STUDENT->OWN_TEAM_MEMBERS_INCLUDING_SELF
    $giverType.find('option').not('[value="STUDENTS"]').hide();
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').hide();

    $giverType.find('option').not('[value="STUDENTS"]').prop('disabled', true);
    $recipientType.find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('disabled', true);

    $giverType.find('option').filter('[value="STUDENTS"]').prop('selected', true);
    $recipientType.find('option').filter('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').prop('selected', true);

    // simulate a click to update the text of the dropdown menu button
    $questionTable.find('.feedback-path-dropdown-option[data-giver-type="STUDENTS"]' + '[data-recipient-type="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').click();
    // the dropdown button is not an input tag and has no property "disabled", so .addClass is used
    $questionTable.find('.feedback-path-dropdown > button').addClass('disabled');
}

exports.fixContribQnGiverRecipient = fixContribQnGiverRecipient;
exports.setContribQnVisibilityFormat = setContribQnVisibilityFormat;
exports.setDefaultContribQnVisibilityIfNeeded = setDefaultContribQnVisibilityIfNeeded;

/***/ }),
/* 28 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.toggleMcqOtherOptionEnabled = exports.toggleMcqGeneratedOptions = exports.removeMcqOption = exports.changeMcqGenerateFor = exports.addMcqOption = undefined;

var _const = __webpack_require__(0);

function addMcqOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var curNumberOfChoiceCreated = parseInt($('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(), 10);

    $('\n    <div class="margin-bottom-7px" id="mcqOptionRow-' + curNumberOfChoiceCreated + '-' + questionNum + '">\n        <div class="input-group">\n            <span class="input-group-addon">\n                <input type="radio" disabled>\n            </span>\n            <input type="text" name="' + _const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + '-' + curNumberOfChoiceCreated + '"\n                    id="' + _const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + '-' + curNumberOfChoiceCreated + '-' + questionNum + '"\n                    class="form-control mcqOptionTextBox">\n            <span class="input-group-btn">\n                <button type="button" class="btn btn-default removeOptionLink" id="mcqRemoveOptionLink"\n                        onclick="removeMcqOption(' + curNumberOfChoiceCreated + ', ' + questionNum + ')" tabindex="-1">\n                    <span class="glyphicon glyphicon-remove"></span>\n                </button>\n            </span>\n        </div>\n    </div>\n    ').insertBefore($('#mcqAddOptionRow-' + questionNum));

    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function removeMcqOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var $thisRow = $('#mcqOptionRow-' + index + '-' + questionNum);

    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;

    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }
}

function toggleMcqGeneratedOptions(checkbox, questionNum) {
    if (checkbox.checked) {
        $('#mcqChoiceTable-' + questionNum).find('input[type=text]').prop('disabled', true);
        $('#mcqChoiceTable-' + questionNum).hide();
        $('#mcqGenerateForSelect-' + questionNum).prop('disabled', false);
        $('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
        $('#generatedOptions-' + questionNum).attr('value', $('#mcqGenerateForSelect-' + questionNum).prop('value'));
    } else {
        $('#mcqChoiceTable-' + questionNum).find('input[type=text]').prop('disabled', false);
        $('#mcqChoiceTable-' + questionNum).show();
        $('#mcqGenerateForSelect-' + questionNum).prop('disabled', true);
        $('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $('#generatedOptions-' + questionNum).attr('value', 'NONE');
    }
}

function toggleMcqOtherOptionEnabled(checkbox, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function changeMcqGenerateFor(questionNum) {
    $('#generatedOptions-' + questionNum).attr('value', $('#mcqGenerateForSelect-' + questionNum).prop('value'));
}

exports.addMcqOption = addMcqOption;
exports.changeMcqGenerateFor = changeMcqGenerateFor;
exports.removeMcqOption = removeMcqOption;
exports.toggleMcqGeneratedOptions = toggleMcqGeneratedOptions;
exports.toggleMcqOtherOptionEnabled = toggleMcqOtherOptionEnabled;

/***/ }),
/* 29 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.toggleMsqMinSelectableChoices = exports.toggleMsqMaxSelectableChoices = exports.toggleMsqOtherOptionEnabled = exports.toggleMsqGeneratedOptions = exports.removeMsqOption = exports.changeMsqGenerateFor = exports.bindMsqEvents = exports.addMsqOption = undefined;

var _const = __webpack_require__(0);

function isMaxSelectableChoicesEnabled(questionNum) {
    return $('#msqEnableMaxSelectableChoices-' + questionNum).prop('checked');
}

function isMinSelectableChoicesEnabled(questionNum) {
    return $('#msqEnableMinSelectableChoices-' + questionNum).prop('checked');
}

function isGenerateOptionsEnabled(questionNum) {
    return $('#generateMsqOptionsCheckbox-' + questionNum).prop('checked');
}

function getNumOfMsqOptions(questionNum) {
    return $('#msqChoiceTable-' + questionNum).children().length - 1;
}

function getMaxSelectableChoicesElement(questionNum) {
    return $('#msqMaxSelectableChoices-' + questionNum);
}

function getMaxSelectableChoicesValue(questionNum) {
    if (isMaxSelectableChoicesEnabled(questionNum)) {
        return parseInt(getMaxSelectableChoicesElement(questionNum).val(), 10);
    }

    // return infinity
    return Number.MAX_SAFE_INTEGER;
}

function setUpperLimitForMaxSelectableChoices(questionNum, upperLimit) {
    getMaxSelectableChoicesElement(questionNum).prop('max', upperLimit);
}

function setMaxSelectableChoices(questionNum, newVal) {
    if (newVal >= 2) {
        // No use if max selectable choices were 1
        getMaxSelectableChoicesElement(questionNum).val(newVal);
    }
}

function getMinSelectableChoicesElement(questionNum) {
    return $('#msqMinSelectableChoices-' + questionNum);
}

function getMinSelectableChoicesValue(questionNum) {
    if (isMinSelectableChoicesEnabled(questionNum)) {
        return parseInt(getMinSelectableChoicesElement(questionNum).val(), 10);
    }

    // return infinity
    return Number.MAX_SAFE_INTEGER;
}

function setMinSelectableChoices(questionNum, newVal) {
    if (newVal >= 1) {
        // No use if min selectable choices where 0
        getMinSelectableChoicesElement(questionNum).val(newVal);
    }
}

function setUpperLimitForMinSelectableChoices(questionNum, upperLimit) {
    getMinSelectableChoicesElement(questionNum).prop('max', upperLimit);
}

/**
 * Returns total number of options for the selected generate options type.
 * Eg. if 'instructors' is selected, returns number of instructors for feedback session.
 * Assumes that 'generateOptions' checkbox is checked.
 */
function getTotalOptionsForSelectedGenerateOptionsType(questionNum) {
    var category = $('#msqGenerateForSelect-' + questionNum).prop('value').toLowerCase();
    return $('#num-' + category).val();
}

function adjustMaxSelectableChoices(questionNum) {
    if (!isMaxSelectableChoicesEnabled(questionNum)) {
        return;
    }

    var upperLimit = isGenerateOptionsEnabled(questionNum) ? getTotalOptionsForSelectedGenerateOptionsType(questionNum) : getNumOfMsqOptions(questionNum);
    var currentVal = getMaxSelectableChoicesValue(questionNum);

    setUpperLimitForMaxSelectableChoices(questionNum, upperLimit);
    setMaxSelectableChoices(questionNum, Math.min(currentVal, upperLimit));
}

function adjustMinSelectableChoices(questionNum) {
    if (!isMinSelectableChoicesEnabled(questionNum)) {
        return;
    }

    var currentVal = getMinSelectableChoicesValue(questionNum);
    var upperLimit = Math.min(getMaxSelectableChoicesValue(questionNum), isGenerateOptionsEnabled(questionNum) ? getTotalOptionsForSelectedGenerateOptionsType(questionNum) : getNumOfMsqOptions(questionNum));

    setUpperLimitForMinSelectableChoices(questionNum, upperLimit);
    setMinSelectableChoices(questionNum, Math.min(currentVal, upperLimit));
}

function adjustMinMaxSelectableChoices(questionNum) {
    adjustMaxSelectableChoices(questionNum);
    adjustMinSelectableChoices(questionNum);
}

function addMsqOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var curNumberOfChoiceCreated = parseInt($('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(), 10);

    $('\n    <div class="margin-bottom-7px" id="msqOptionRow-' + curNumberOfChoiceCreated + '-' + questionNum + '">\n        <div class="input-group">\n            <span class="input-group-addon">\n                <input type="checkbox" disabled>\n            </span>\n            <input type="text" name="' + _const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + '-' + curNumberOfChoiceCreated + '"\n                    id="' + _const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + '-' + curNumberOfChoiceCreated + '-' + questionNum + '"\n                    class="form-control msqOptionTextBox">\n            <span class="input-group-btn">\n                <button type="button" class="btn btn-default removeOptionLink" id="msqRemoveOptionLink"\n                        onclick="removeMsqOption(' + curNumberOfChoiceCreated + ', ' + questionNum + ')" tabindex="-1">\n                    <span class="glyphicon glyphicon-remove"></span>\n                </button>\n            </span>\n        </div>\n    </div>\n    ').insertBefore($('#msqAddOptionRow-' + questionNum));

    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    adjustMinMaxSelectableChoices(questionNum);
}

function removeMsqOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var $thisRow = $('#msqOptionRow-' + index + '-' + questionNum);

    // count number of child rows the table have and - 1 because of add option button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;

    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }

    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqMaxSelectableChoices(questionNum) {
    var $checkbox = $('#msqEnableMaxSelectableChoices-' + questionNum);

    $('#msqMaxSelectableChoices-' + questionNum).prop('disabled', !$checkbox.prop('checked'));
    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqMinSelectableChoices(questionNum) {
    var $checkbox = $('#msqEnableMinSelectableChoices-' + questionNum);

    $('#msqMinSelectableChoices-' + questionNum).prop('disabled', !$checkbox.prop('checked'));
    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqGeneratedOptions(checkbox, questionNum) {
    if (checkbox.checked) {
        $('#msqChoiceTable-' + questionNum).find('input[type=text]').prop('disabled', true);
        $('#msqChoiceTable-' + questionNum).hide();
        $('#msqGenerateForSelect-' + questionNum).prop('disabled', false);
        $('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
        $('#generatedOptions-' + questionNum).attr('value', $('#msqGenerateForSelect-' + questionNum).prop('value'));
    } else {
        $('#msqChoiceTable-' + questionNum).find('input[type=text]').prop('disabled', false);
        $('#msqChoiceTable-' + questionNum).show();
        $('#msqGenerateForSelect-' + questionNum).prop('disabled', true);
        $('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $('#generatedOptions-' + questionNum).attr('value', 'NONE');
    }

    adjustMinMaxSelectableChoices(questionNum);
}

function toggleMsqOtherOptionEnabled(checkbox, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function changeMsqGenerateFor(questionNum) {
    $('#generatedOptions-' + questionNum).attr('value', $('#msqGenerateForSelect-' + questionNum).prop('value'));
    adjustMinMaxSelectableChoices(questionNum);
}

function bindMsqEvents() {
    $(document).on('change', 'input[name="msqMaxSelectableChoices"]', function (e) {
        var questionNum = $(e.target).closest('form').attr('data-qnnumber');
        adjustMinMaxSelectableChoices(questionNum);
    });

    $(document).on('change', 'input[name="msqMinSelectableChoices"]', function (e) {
        var questionNum = $(e.target).closest('form').attr('data-qnnumber');
        adjustMinMaxSelectableChoices(questionNum);
    });

    $(document).on('change', 'input[name*="msqEnableMaxSelectableChoices"]', function (e) {
        var questionNumber = $(e.target).closest('form').attr('data-qnnumber');
        toggleMsqMaxSelectableChoices(questionNumber);
    });

    $(document).on('change', 'input[name*="msqEnableMinSelectableChoices"]', function (e) {
        var questionNumber = $(e.target).closest('form').attr('data-qnnumber');
        toggleMsqMinSelectableChoices(questionNumber);
    });
}

exports.addMsqOption = addMsqOption;
exports.bindMsqEvents = bindMsqEvents;
exports.changeMsqGenerateFor = changeMsqGenerateFor;
exports.removeMsqOption = removeMsqOption;
exports.toggleMsqGeneratedOptions = toggleMsqGeneratedOptions;
exports.toggleMsqOtherOptionEnabled = toggleMsqOtherOptionEnabled;
exports.toggleMsqMaxSelectableChoices = toggleMsqMaxSelectableChoices;
exports.toggleMsqMinSelectableChoices = toggleMsqMinSelectableChoices;

/***/ }),
/* 30 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.toggleMinOptionsToBeRanked = exports.toggleMaxOptionsToBeRanked = exports.removeRankOption = exports.hideRankOptionTable = exports.bindRankEvents = exports.addRankOption = undefined;

var _const = __webpack_require__(0);

function isMinOptionsToBeRankedEnabled(qnNumber) {
    return $('#minOptionsToBeRankedEnabled-' + qnNumber).prop('checked');
}

function isMaxOptionsToBeRankedEnabled(qnNumber) {
    return $('#maxOptionsToBeRankedEnabled-' + qnNumber).prop('checked');
}

function getNumOfRankOptions(qnNumber) {
    return $('#rankOptionTable-' + qnNumber).children('div[id^="rankOptionRow"]').length;
}

function getMinOptionsToBeRankedBox(qnNumber) {
    return $('#minOptionsToBeRanked-' + qnNumber);
}

function getMaxOptionsToBeRankedBox(qnNumber) {
    return $('#maxOptionsToBeRanked-' + qnNumber);
}

function setUpperLimitForMinOptionsToBeRanked(qnNumber, upperLimit) {
    getMinOptionsToBeRankedBox(qnNumber).prop('max', upperLimit);
}

function setUpperLimitForMaxOptionsToBeRanked(qnNumber, upperLimit) {
    getMaxOptionsToBeRankedBox(qnNumber).prop('max', upperLimit);
}

function getMinOptionsToBeRanked(qnNumber) {
    if (isMinOptionsToBeRankedEnabled(qnNumber)) {
        return parseInt(getMinOptionsToBeRankedBox(qnNumber).val(), 10);
    }

    return Number.MAX_SAFE_INTEGER;
}

function getMaxOptionsToBeRanked(qnNumber) {
    if (isMaxOptionsToBeRankedEnabled(qnNumber)) {
        return parseInt(getMaxOptionsToBeRankedBox(qnNumber).val(), 10);
    }

    return Number.MAX_SAFE_INTEGER;
}

function setMinOptionsToBeRanked(qnNumber, newVal) {
    if (!isMinOptionsToBeRankedEnabled(qnNumber) || newVal < 1) {
        return;
    }

    getMinOptionsToBeRankedBox(qnNumber).val(newVal);
}

function setMaxOptionsToBeRanked(qnNumber, newVal) {
    if (!isMaxOptionsToBeRankedEnabled(qnNumber) || newVal < 1) {
        return;
    }

    getMaxOptionsToBeRankedBox(qnNumber).val(newVal);
}

function adjustMinOptionsToBeRanked(qnNumber) {
    if (!isMinOptionsToBeRankedEnabled(qnNumber)) {
        return;
    }

    var upperLimit = getNumOfRankOptions(qnNumber);
    var currentVal = Math.min(getMinOptionsToBeRanked(qnNumber), upperLimit);

    setUpperLimitForMinOptionsToBeRanked(qnNumber, upperLimit);
    setMinOptionsToBeRanked(qnNumber, currentVal);

    if (getMaxOptionsToBeRanked(qnNumber) < currentVal) {
        setMaxOptionsToBeRanked(qnNumber, currentVal);
    }
}

function adjustMaxOptionsToBeRanked(qnNumber) {
    if (!isMaxOptionsToBeRankedEnabled(qnNumber)) {
        return;
    }

    var upperLimit = getNumOfRankOptions(qnNumber);
    var currentVal = Math.min(getMaxOptionsToBeRanked(qnNumber), upperLimit);

    setUpperLimitForMaxOptionsToBeRanked(qnNumber, upperLimit);
    setMaxOptionsToBeRanked(qnNumber, currentVal);

    if (currentVal < getMinOptionsToBeRanked(qnNumber)) {
        setMinOptionsToBeRanked(qnNumber, currentVal);
    }
}

function adjustMinMaxOptionsToBeRanked(qnNumber) {
    adjustMaxOptionsToBeRanked(qnNumber);
    adjustMinOptionsToBeRanked(qnNumber);
}

function toggleMinOptionsToBeRanked(qnNumber) {
    var $maxOptionsToBeRanked = getMinOptionsToBeRankedBox(qnNumber);

    $maxOptionsToBeRanked.prop('disabled', !isMinOptionsToBeRankedEnabled(qnNumber));
    adjustMinMaxOptionsToBeRanked(qnNumber);
}

function toggleMaxOptionsToBeRanked(qnNumber) {
    var $maxOptionsToBeRanked = getMaxOptionsToBeRankedBox(qnNumber);

    $maxOptionsToBeRanked.prop('disabled', !isMaxOptionsToBeRankedEnabled(qnNumber));
    adjustMinMaxOptionsToBeRanked(qnNumber);
}

function addRankOption(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var curNumberOfChoiceCreated = parseInt($('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(), 10);

    $('\n    <div class="margin-bottom-7px" id="rankOptionRow-' + curNumberOfChoiceCreated + '-' + questionNum + '">\n        <div class="input-group">\n            <input type="text" name="' + _const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + '-' + curNumberOfChoiceCreated + '"\n                    id="' + _const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + '-' + curNumberOfChoiceCreated + '-' + questionNum + '"\n                    class="form-control rankOptionTextBox">\n            <span class="input-group-btn">\n                <button class="btn btn-default removeOptionLink" id="rankRemoveOptionLink"\n                        onclick="removeRankOption(' + curNumberOfChoiceCreated + ', ' + questionNum + ')" tabindex="-1">\n                    <span class="glyphicon glyphicon-remove"></span>\n                </button>\n            </span>\n        </div>\n    </div>\n    ').insertBefore($('#rankAddOptionRow-' + questionNum));

    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + questionNum).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    adjustMinMaxOptionsToBeRanked(questionNum);
}

function hideRankOptionTable(questionNum) {
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_RANKOPTIONTABLE + '-' + questionNum).hide();
}

function removeRankOption(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;
    var $thisRow = $('#rankOptionRow-' + index + '-' + questionNum);

    // count number of child rows the table have and - 1 because of 'add option' button
    var numberOfOptions = $thisRow.parent().children('div').length - 1;

    if (numberOfOptions <= 2) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }

    adjustMinMaxOptionsToBeRanked(questionNum);
}

function bindRankEvents() {
    $(document).on('change', 'input[name="minOptionsToBeRanked"]', function (e) {
        var questionNum = $(e.target).closest('form').attr('data-qnnumber');
        adjustMinOptionsToBeRanked(questionNum);
    });

    $(document).on('change', 'input[name="maxOptionsToBeRanked"]', function (e) {
        var questionNum = $(e.target).closest('form').attr('data-qnnumber');
        adjustMaxOptionsToBeRanked(questionNum);
    });

    $(document).on('change', 'input[name="minOptionsToBeRankedEnabled"]', function (e) {
        var questionNum = $(e.target).closest('form').attr('data-qnnumber');
        toggleMinOptionsToBeRanked(questionNum);
    });

    $(document).on('change', 'input[name="maxOptionsToBeRankedEnabled"]', function (e) {
        var questionNum = $(e.target).closest('form').attr('data-qnnumber');
        toggleMaxOptionsToBeRanked(questionNum);
    });
}

exports.addRankOption = addRankOption;
exports.bindRankEvents = bindRankEvents;
exports.hideRankOptionTable = hideRankOptionTable;
exports.removeRankOption = removeRankOption;
exports.toggleMaxOptionsToBeRanked = toggleMaxOptionsToBeRanked;
exports.toggleMinOptionsToBeRanked = toggleMinOptionsToBeRanked;

/***/ }),
/* 31 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.removeRubricRow = exports.removeRubricCol = exports.moveAssignWeightsCheckbox = exports.highlightRubricRow = exports.highlightRubricCol = exports.hasAssignedWeights = exports.disableCornerMoveRubricColumnButtons = exports.bindMoveRubricColButtons = exports.bindAssignWeightsCheckboxes = exports.addRubricRow = exports.addRubricCol = undefined;

var _bootboxWrapper = __webpack_require__(2);

var _const = __webpack_require__(0);

var _ui = __webpack_require__(6);

function getRubricChoiceElem(questionNum, col) {
    return $('#rubricChoice-' + questionNum + '-' + col);
}

function getRubricWeightElem(questionNum, col) {
    return $('#rubricWeight-' + questionNum + '-' + col);
}

function getRubricDescElem(questionNum, row, col) {
    return $('#rubricDesc-' + questionNum + '-' + row + '-' + col);
}

function swapRubricCol(questionNum, firstColIndex, secondColIndex) {
    var numberOfRows = parseInt($('#rubricNumRows-' + questionNum).val(), 10);
    var CHOICE = 'RUBRIC_CHOICE';
    var WEIGHT = 'RUBRIC_WEIGHT';
    var DESC = 'RUBRIC_DESC';
    var elemSelector = function elemSelector(type, col) {
        var row = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 0;

        if (type === CHOICE) {
            return getRubricChoiceElem(questionNum, col);
        } else if (type === WEIGHT) {
            return getRubricWeightElem(questionNum, col);
        } else if (type === DESC) {
            return getRubricDescElem(questionNum, row, col);
        }

        return null;
    };

    var swapValues = function swapValues(type) {
        var row = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

        var $a = elemSelector(type, firstColIndex, row);
        var $b = elemSelector(type, secondColIndex, row);
        var temp = $a.val();
        $a.val($b.val());
        $b.val(temp);
    };

    // swap rubric choices
    swapValues(CHOICE);

    // swap rubric weights
    swapValues(WEIGHT);

    // swap options filled
    for (var row = 0; row < numberOfRows; row += 1) {
        swapValues(DESC, row);
    }
}

function moveRubricColIfPossible(questionNum, firstColIndex, isMoveLeft) {
    if ($('#rubricEditTable-' + questionNum).length === 0 || $('.rubricCol-' + questionNum + '-' + firstColIndex).length === 0 || typeof isMoveLeft !== 'boolean') {
        // question and column should exist, isMoveLeft must be boolean
        return;
    }

    var $swapCell = $('#rubric-options-row-' + questionNum + ' .rubricCol-' + questionNum + '-' + firstColIndex);
    var rubricCellSelector = 'td[class*=\'rubricCol-' + questionNum + '\']';

    if (isMoveLeft && $swapCell.prev(rubricCellSelector).length === 0 || !isMoveLeft && $swapCell.next(rubricCellSelector).length === 0) {
        // trying to swap left most or right most column
        return;
    }

    var secondColIndex = void 0;

    if (isMoveLeft) {
        secondColIndex = $swapCell.prev(rubricCellSelector).attr('data-col');
    } else {
        secondColIndex = $swapCell.next(rubricCellSelector).attr('data-col');
    }

    swapRubricCol(questionNum, firstColIndex, secondColIndex);

    var $form = $('#form_editquestion-' + questionNum);

    if ($form.attr('editstatus') === 'hasResponses') {
        $form.attr('editstatus', 'mustDeleteResponses');
    }
}

function disableCornerMoveRubricColumnButtons(questionNum) {
    var $optionColumns = $('#rubric-options-row-' + questionNum + ' td[class*=\'rubricCol-\']');

    var disableMoveLeftOfFirstCol = function disableMoveLeftOfFirstCol() {
        var $leftmostCol = $optionColumns.first();
        var leftmostColIndex = $leftmostCol.attr('data-col');
        var $leftmostColLeftBtn = $leftmostCol.find('#rubric-move-col-left-' + questionNum + '-' + leftmostColIndex);

        $leftmostColLeftBtn.prop('disabled', true);
    };

    var disableMoveRightOfLastCol = function disableMoveRightOfLastCol() {
        var $rightmostCol = $optionColumns.last();
        var rightmostColIndex = $rightmostCol.attr('data-col');
        var $rightmostColRightBtn = $rightmostCol.find('#rubric-move-col-right-' + questionNum + '-' + rightmostColIndex);

        $rightmostColRightBtn.prop('disabled', true);
    };

    var enableMoveRightOfSecondLastCol = function enableMoveRightOfSecondLastCol() {
        var $secondlastCol = $optionColumns.last().prev();
        var secondlastColIndex = $secondlastCol.attr('data-col');
        var $secondlastColRightBtn = $secondlastCol.find('#rubric-move-col-right-' + questionNum + '-' + secondlastColIndex);

        $secondlastColRightBtn.prop('disabled', false);
    };

    if ($optionColumns.length < 2) {
        disableMoveLeftOfFirstCol(questionNum);
        disableMoveRightOfLastCol(questionNum);
        return;
    }

    disableMoveLeftOfFirstCol(questionNum);
    disableMoveRightOfLastCol(questionNum);
    enableMoveRightOfSecondLastCol(questionNum);
}

function addRubricRow(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var numberOfRows = parseInt($('#rubricNumRows-' + questionNum).val(), 10);
    var numberOfCols = parseInt($('#rubricNumCols-' + questionNum).val(), 10);

    var newRowNumber = numberOfRows + 1;

    var rubricRowBodyFragments = '';
    // Create numberOfCols of <td>'s
    for (var cols = 0; cols < numberOfCols; cols += 1) {
        if (!$('.rubricCol-' + questionNum + '-' + cols).length) {
            continue;
        }
        var rubricRowFragment = '<td class="align-center rubricCol-' + questionNum + '-' + cols + '">\n                <textarea class="form-control" rows="3" id="rubricDesc-' + questionNum + '-' + (newRowNumber - 1) + '-' + cols + '"\n                        name="rubricDesc-' + (newRowNumber - 1) + '-' + cols + '"></textarea>\n            </td>';
        rubricRowBodyFragments += rubricRowFragment;
    }

    // Create new rubric row
    var newRubricRow = '<tr id="rubricRow-' + questionNum + '-' + (newRowNumber - 1) + '">\n            <td>\n                <div class="col-sm-12 input-group">\n                    <span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-' + questionNum + '"\n                            id="rubricRemoveSubQuestionLink-' + questionNum + '-' + (newRowNumber - 1) + '"\n                            onclick="removeRubricRow(' + (newRowNumber - 1) + ', ' + questionNum + ')"\n                            onmouseover="highlightRubricRow(' + (newRowNumber - 1) + ', ' + questionNum + ', true)"\n                            onmouseout="highlightRubricRow(' + (newRowNumber - 1) + ', ' + questionNum + ', false)">\n                        <span class="glyphicon glyphicon-remove"></span>\n                    </span>\n                    <textarea class="form-control" rows="3" id="rubricSubQn-' + questionNum + '-' + (newRowNumber - 1) + '"\n                            name="rubricSubQn-' + (newRowNumber - 1) + '" required=""></textarea>\n                </div>\n            </td>\n            ' + rubricRowBodyFragments + '\n        </tr>';

    // Row to insert new row after
    var $secondLastRow = $('#rubricEditTable-' + questionNum + ' tbody tr:nth-last-child(2)');
    $(newRubricRow).insertAfter($secondLastRow);

    // Increment
    $('#rubricNumRows-' + questionNum).val(newRowNumber);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function addRubricCol(questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var numberOfRows = parseInt($('#rubricNumRows-' + questionNum).val(), 10);
    var numberOfCols = parseInt($('#rubricNumCols-' + questionNum).val(), 10);

    var newColNumber = numberOfCols + 1;

    // Insert header <th>
    var rubricHeaderFragment = '<th class="rubricCol-' + questionNum + '-' + (newColNumber - 1) + '">\n            <input type="text" class="col-sm-12 form-control" value=""\n                    id="rubricChoice-' + questionNum + '-' + (newColNumber - 1) + '"\n                    name="rubricChoice-' + (newColNumber - 1) + '">\n        </th>';

    // Insert after last <th>
    var lastTh = $('#rubricEditTable-' + questionNum).find('tr:first').children().last();
    $(rubricHeaderFragment).insertAfter(lastTh);

    // Insert weight <th>
    var rubricWeightFragment = '<th class="rubricCol-' + questionNum + '-' + (newColNumber - 1) + '">\n            <input type="number" class="form-control nonDestructive" value="0"\n                    id="rubricWeight-' + questionNum + '-' + (newColNumber - 1) + '"\n                    name="rubricWeight-' + (newColNumber - 1) + '" step="0.01">\n        </th>';

    // Insert after last <th>
    var lastWeightCell = $('#rubricWeights-' + questionNum + ' th:last');
    $(rubricWeightFragment).insertAfter(lastWeightCell);

    (0, _ui.disallowNonNumericEntries)($('#rubricWeight-' + questionNum + '-' + (newColNumber - 1)), true, true);

    // Create numberOfRows of <td>'s
    for (var rows = 0; rows < numberOfRows; rows += 1) {
        if (!$('#rubricRow-' + questionNum + '-' + rows).length) {
            continue;
        }
        // Insert body <td>'s
        var rubricRowFragment = '<td class="align-center rubricCol-' + questionNum + '-' + (newColNumber - 1) + '">\n                <textarea class="form-control" rows="3" id="rubricDesc-' + questionNum + '-' + rows + '-' + (newColNumber - 1) + '"\n                        name="rubricDesc-' + rows + '-' + (newColNumber - 1) + '"></textarea>\n            </td>';

        // Insert after previous <td>
        var lastTd = $('#rubricRow-' + questionNum + '-' + rows + ' td:last');
        $(rubricRowFragment).insertAfter(lastTd);
    }

    // Add options row at the end
    var optionsRow = '<td class="align-center rubricCol-' + questionNum + '-' + (newColNumber - 1) + '" data-col="' + (newColNumber - 1) + '">\n            <div class="btn-group">\n                <button type="button" class="btn btn-default" id="rubric-move-col-left-' + questionNum + '-' + (newColNumber - 1) + '"\n                        data-toggle="tooltip" data-placement="top" title="Move column left">\n                    <span class="glyphicon glyphicon-arrow-left"></span>\n                </button>\n                <button type="button" class="btn btn-default" id="rubricRemoveChoiceLink-' + questionNum + '-' + (newColNumber - 1) + '"\n                        onclick="removeRubricCol(' + (newColNumber - 1) + ', ' + questionNum + ')"\n                        onmouseover="highlightRubricCol(' + (newColNumber - 1) + ', ' + questionNum + ', true)"\n                        onmouseout="highlightRubricCol(' + (newColNumber - 1) + ', ' + questionNum + ', false)">\n                    <span class="glyphicon glyphicon-remove"></span>\n                </button>\n                <button type="button" class="btn btn-default" id="rubric-move-col-right-' + questionNum + '-' + (newColNumber - 1) + '"\n                        data-toggle="tooltip" data-placement="top" title="Move column right">\n                    <span class="glyphicon glyphicon-arrow-right"></span>\n                </button>\n            </div>\n        </td>';

    var $lastTd = $('#rubric-options-row-' + questionNum + ' td:last');
    $(optionsRow).insertAfter($lastTd);

    // Initialize tooltips and set click event handlers for move column buttons
    var $newColMoveLeftBtn = $('#rubric-move-col-left-' + questionNum + '-' + (newColNumber - 1));
    var $newColMoveRightBtn = $('#rubric-move-col-right-' + questionNum + '-' + (newColNumber - 1));

    $newColMoveLeftBtn.tooltip({ container: 'body' });
    $newColMoveRightBtn.tooltip({ container: 'body' });

    $newColMoveLeftBtn.click(function () {
        moveRubricColIfPossible(questionNum, newColNumber - 1, true);
    });

    $newColMoveRightBtn.click(function () {
        moveRubricColIfPossible(questionNum, newColNumber - 1, false);
    });

    // Increment
    $('#rubricNumCols-' + questionNum).val(newColNumber);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    disableCornerMoveRubricColumnButtons(questionNum);
}

function removeRubricRow(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var $thisRow = $('#rubricRow-' + questionNum + '-' + index);

    // count number of table rows from table body
    var numberOfRows = $thisRow.parent().children('tr').length - 1; // exclude options row

    var delStr = numberOfRows <= 1 ? 'clear' : 'delete';
    var messageText = 'Are you sure you want to ' + delStr + ' the row?';
    var okCallback = function okCallback() {
        if (numberOfRows <= 1) {
            $thisRow.find('textarea').val('');
        } else {
            $thisRow.remove();

            if ($(questionId).attr('editStatus') === 'hasResponses') {
                $(questionId).attr('editStatus', 'mustDeleteResponses');
            }
        }
    };
    (0, _bootboxWrapper.showModalConfirmation)('Confirm Deletion', messageText, okCallback, null, null, null, _const.StatusType.WARNING);
}

function removeRubricCol(index, questionNum) {
    var questionId = '#form_editquestion-' + questionNum;

    var $thisCol = $('.rubricCol-' + questionNum + '-' + index);

    // count number of table columns from table body
    var numberOfCols = $thisCol.first().parent().children().length - 1;

    var delStr = numberOfCols <= 1 ? 'clear' : 'delete';
    var messageText = 'Are you sure you want to ' + delStr + ' the column?';
    var okCallback = function okCallback() {
        if (numberOfCols <= 1) {
            $thisCol.find('input[id^="rubricChoice"], textarea').val('');
            $thisCol.find('input[id^="rubricWeight"]').val(0);
        } else {
            $thisCol.remove();
            disableCornerMoveRubricColumnButtons(questionNum);

            if ($(questionId).attr('editStatus') === 'hasResponses') {
                $(questionId).attr('editStatus', 'mustDeleteResponses');
            }
        }
    };
    (0, _bootboxWrapper.showModalConfirmation)('Confirm Deletion', messageText, okCallback, null, null, null, _const.StatusType.WARNING);
}

function highlightRubricRow(index, questionNum, highlight) {
    var $rubricRow = $('#rubricRow-' + questionNum + '-' + index);

    if (highlight) {
        $rubricRow.find('td').addClass('cell-selected-negative');
    } else {
        $rubricRow.find('td').removeClass('cell-selected-negative');
    }
}

function highlightRubricCol(index, questionNum, highlight) {
    var $rubricCol = $('.rubricCol-' + questionNum + '-' + index);

    if (highlight) {
        $rubricCol.addClass('cell-selected-negative');
    } else {
        $rubricCol.removeClass('cell-selected-negative');
    }
}

/**
 * Moves the "weights" checkbox to the weight row if it is checked, otherwise
 * moves it to the choice row
 *
 * @param checkbox the "weights" checkbox
 */
function moveAssignWeightsCheckbox(checkbox) {
    var $choicesRow = checkbox.closest('thead').find('tr').eq(0);
    var $weightsRow = checkbox.closest('thead').find('tr').eq(1);
    var $choicesRowFirstCell = $choicesRow.find('th').first();
    var $weightsRowFirstCell = $weightsRow.find('th').first();

    var $checkboxCellContent = checkbox.closest('th').children().detach();

    $choicesRowFirstCell.empty();
    $weightsRowFirstCell.empty();

    if (checkbox.prop('checked')) {
        $choicesRowFirstCell.append('Choices <span class="glyphicon glyphicon-arrow-right"></span>');
        $weightsRowFirstCell.append($checkboxCellContent);
        $weightsRowFirstCell.find('.glyphicon-arrow-right').show();
    } else {
        $choicesRowFirstCell.append($checkboxCellContent);
        $choicesRowFirstCell.find('.glyphicon-arrow-right').hide();
    }
}

/**
 * Attaches event handlers to "weights" checkboxes to toggle the visibility of
 * the input boxes for rubric weights and move the "weights" checkbox to the
 * appropriate location
 */
function bindAssignWeightsCheckboxes() {
    $('body').on('click', 'input[id^="rubricAssignWeights"]', function () {
        var $checkbox = $(this);

        $checkbox.closest('form').find('tr[id^="rubricWeights"]').toggle();

        moveAssignWeightsCheckbox($checkbox);
    });
}

/**
 * Attaches click event handlers move rubric column buttons to
 * all rubric questions. To be called in $(document).ready().
 */
function bindMoveRubricColButtons() {
    $('table[id^="rubricEditTable-"]').each(function () {
        var questionNum = $(this).closest('form').data('qnnumber');

        $('#rubric-options-row-' + questionNum + ' td[class*="rubricCol-' + questionNum + '"]').each(function () {
            var colNum = $(this).attr('data-col');

            $('#rubric-move-col-left-' + questionNum + '-' + colNum).click(function () {
                moveRubricColIfPossible(questionNum, colNum, true);
            });

            $('#rubric-move-col-right-' + questionNum + '-' + colNum).click(function () {
                moveRubricColIfPossible(questionNum, colNum, false);
            });
        });
    });
}

/**
 * @param questionNum
 *            the question number of the feedback question
 * @returns {Boolean} true if the weights are assigned by the user, otherwise false
 */
function hasAssignedWeights(questionNum) {
    return $('#rubricAssignWeights-' + questionNum).prop('checked');
}

exports.addRubricCol = addRubricCol;
exports.addRubricRow = addRubricRow;
exports.bindAssignWeightsCheckboxes = bindAssignWeightsCheckboxes;
exports.bindMoveRubricColButtons = bindMoveRubricColButtons;
exports.disableCornerMoveRubricColumnButtons = disableCornerMoveRubricColumnButtons;
exports.hasAssignedWeights = hasAssignedWeights;
exports.highlightRubricCol = highlightRubricCol;
exports.highlightRubricRow = highlightRubricRow;
exports.moveAssignWeightsCheckbox = moveAssignWeightsCheckbox;
exports.removeRubricCol = removeRubricCol;
exports.removeRubricRow = removeRubricRow;

/***/ }),
/* 32 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/* global tinymce:false */

/* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
var richTextEditorBuilder = {
    getDefaultConfiguration: function getDefaultConfiguration() {
        return {
            theme: 'modern',
            fontsize_formats: '8pt 9pt 10pt 11pt 12pt 14pt 16pt 18pt 20pt 24pt 26pt 28pt 36pt 48pt 72pt',
            font_formats: 'Andale Mono=andale mono,times;' + 'Arial=arial,helvetica,sans-serif;' + 'Arial Black=arial black,avant garde;' + 'Book Antiqua=book antiqua,palatino;' + 'Comic Sans MS=comic sans ms,sans-serif;' + 'Courier New=courier new,courier;' + 'Georgia=georgia,palatino;' + 'Helvetica=helvetica;' + 'Impact=impact,chicago;' + 'Symbol=symbol;' + 'Tahoma=tahoma,arial,helvetica,sans-serif;' + 'Terminal=terminal,monaco;' + 'Times New Roman=times new roman,times;' + 'Trebuchet MS=trebuchet ms,geneva;' + 'Verdana=verdana,geneva;' + 'Webdings=webdings;' + 'Wingdings=wingdings,zapf dingbats',

            relative_urls: false,
            convert_urls: false,
            remove_linebreaks: false,
            plugins: ['advlist autolink lists link image charmap print preview hr anchor pagebreak', 'searchreplace wordcount visualblocks visualchars code fullscreen', 'insertdatetime nonbreaking save table contextmenu directionality', 'emoticons template paste textcolor colorpicker textpattern'],

            toolbar1: 'insertfile undo redo | styleselect | bold italic underline | ' + 'alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',
            toolbar2: 'print preview | forecolor backcolor | fontsizeselect fontselect | emoticons | fullscreen',

            init_instance_callback: 'initEditorCallback'

        };
    },
    initEditor: function initEditor(selector, opts) {
        tinymce.init($.extend(this.getDefaultConfiguration(), {
            selector: selector
        }, opts));
    }
};
/* eslint-enable camelcase */

function setPlaceholderText(editor) {
    if (editor.getContent() === '') {
        tinymce.DOM.addClass(editor.bodyElement, 'empty');
    } else {
        tinymce.DOM.removeClass(editor.bodyElement, 'empty');
    }
}

function initEditorCallback(editor) {
    tinymce.DOM.addClass(editor.bodyElement, 'content-editor');
    setPlaceholderText(editor);

    editor.on('selectionchange', function () {
        setPlaceholderText(editor);
    });
}

window.initEditorCallback = initEditorCallback;

/**
 * Destroys an instance of TinyMCE rich-text editor.
 */
function destroyEditor(id) {
    if (typeof tinymce === 'undefined') {
        return;
    }
    var currentEditor = tinymce.get(id);
    if (currentEditor) {
        currentEditor.destroy();
    }
}

exports.destroyEditor = destroyEditor;
exports.richTextEditorBuilder = richTextEditorBuilder;

/***/ }),
/* 33 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.bindLinksInUnregisteredPage = undefined;

var _const = __webpack_require__(0);

var _bootboxWrapper = __webpack_require__(2);

/**
 * Contains functions common to the student pages.
 */
function bindLinksInUnregisteredPage(selector) {
    $(document).on('click', selector, function (e) {
        e.preventDefault();
        var $clickedLink = $(e.target);

        var header = _const.Const.ModalDialog.UNREGISTERED_STUDENT.header;
        var messageText = _const.Const.ModalDialog.UNREGISTERED_STUDENT.text;
        function okCallback() {
            window.location = $clickedLink.attr('href');
        }

        (0, _bootboxWrapper.showModalConfirmation)(header, messageText, okCallback, null, null, null, _const.StatusType.INFO);
    });
}

exports.bindLinksInUnregisteredPage = bindLinksInUnregisteredPage;

/***/ }),
/* 34 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.tallyCheckboxes = exports.showVisibilityCheckboxesIfCustomOptionSelected = exports.matchVisibilityOptionToFeedbackPath = exports.getVisibilityMessage = exports.formatCheckBoxes = exports.attachVisibilityDropdownEvent = exports.attachVisibilityCheckboxEvent = undefined;

var _const = __webpack_require__(0);

var ROW_RECIPIENT = 1;
var ROW_GIVER_TEAM = 2;
var ROW_RECIPIENT_TEAM = 3;
var ROW_OTHER_STUDENTS = 4;
var ROW_INSTRUCTORS = 5;

var NEW_QUESTION = -1;

// ////////////// //
// HELPER METHODS //
// ////////////// //

function setVisibilityDropdownMenuText(text, $containingForm) {
    var $visibilityDropdown = $containingForm.find('.visibility-options-dropdown');

    if (text === 'Custom visibility options...') {
        $visibilityDropdown.find('button').text('Custom visibility option:');
    } else {
        $visibilityDropdown.find('button').text(text);
    }
}

function uncheckAllVisibilityOptionCheckboxes($containingForm) {
    $containingForm.find('input.visibilityCheckbox').each(function (index, checkbox) {
        checkbox.checked = false;
    });
}

/**
 * Checks the checkboxes for recipient
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowRecipientToSee(checkboxClass, $containingForm) {
    $containingForm.find('input[type="checkbox"][value="RECEIVER"]' + checkboxClass).prop('checked', true);
}

/**
 * Checks the checkboxes for giver's team members
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowGiversTeamToSee(checkboxClass, $containingForm) {
    $containingForm.find('input[type="checkbox"][value="OWN_TEAM_MEMBERS"]' + checkboxClass).prop('checked', true);
}

/**
 * Checks the checkboxes for recipient's team members
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowRecipientsTeamToSee(checkboxClass, $containingForm) {
    $containingForm.find('input[type="checkbox"][value="RECEIVER_TEAM_MEMBERS"]' + checkboxClass).prop('checked', true);
}

/**
 * Checks the checkboxes for instructors
 * @param checkboxClass - the CSS class of the checkbox to be checked
 */
function allowInstructorToSee(checkboxClass, $containingForm) {
    $containingForm.find('input[type="checkbox"][value="INSTRUCTORS"]' + checkboxClass).prop('checked', true);
}

/**
 * Checks the visibility checkboxes according to the common visibility option as selected using the dropdown menu
 */
function checkCorrespondingCheckboxes(selectedOption, $containingForm) {
    switch (selectedOption) {
        case 'OTHER':
            return;
        case 'ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS':
            // recipient and instructor can see answer and recipient, but not giver name
            allowRecipientToSee('.answerCheckbox', $containingForm);
            allowRecipientToSee('.recipientCheckbox', $containingForm);

            allowInstructorToSee('.answerCheckbox', $containingForm);
            allowInstructorToSee('.recipientCheckbox', $containingForm);
            break;
        case 'ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS':
            // recipient can see answer and recipient, but not giver name
            allowRecipientToSee('.answerCheckbox', $containingForm);
            allowRecipientToSee('.recipientCheckbox', $containingForm);

            // instructor can see answer, recipient AND giver name
            allowInstructorToSee('.answerCheckbox', $containingForm);
            allowInstructorToSee('.giverCheckbox', $containingForm);
            allowInstructorToSee('.recipientCheckbox', $containingForm);
            break;
        case 'ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS':
            // recipient can see answer and recipient, but not giver name
            allowRecipientToSee('.answerCheckbox', $containingForm);
            allowRecipientToSee('.recipientCheckbox', $containingForm);

            // instructor can see answer, recipient AND giver name
            allowInstructorToSee('.answerCheckbox', $containingForm);
            allowInstructorToSee('.giverCheckbox', $containingForm);
            allowInstructorToSee('.recipientCheckbox', $containingForm);

            // recipient team (same as givers team) can see answer and recipient, but not giver name
            allowRecipientsTeamToSee('.answerCheckbox', $containingForm);
            allowGiversTeamToSee('.answerCheckbox', $containingForm);
            break;
        case 'VISIBLE_TO_INSTRUCTORS_ONLY':
            allowInstructorToSee('.answerCheckbox', $containingForm);
            allowInstructorToSee('.giverCheckbox', $containingForm);
            allowInstructorToSee('.recipientCheckbox', $containingForm);
            break;
        case 'VISIBLE_TO_RECIPIENT_AND_INSTRUCTORS':
            allowRecipientToSee('.answerCheckbox', $containingForm);
            allowRecipientToSee('.giverCheckbox', $containingForm);
            allowRecipientToSee('.recipientCheckbox', $containingForm);

            allowInstructorToSee('.answerCheckbox', $containingForm);
            allowInstructorToSee('.giverCheckbox', $containingForm);
            allowInstructorToSee('.recipientCheckbox', $containingForm);
            break;
        default:
            throw new Error('Unexpected common visibility option type');
    }
}

/**
 * Ensures the hidden checkbox for Recipient's Team Members can see answer is consistent with Recipient can see answer
 */
function fixCheckboxValuesForTeamContribQuestion($containingForm) {
    if ($containingForm.find('input[name="questiontype"]').val() !== 'CONTRIB') {
        return;
    }
    var recipientCanSeeAnswerCheckbox = $containingForm.find('input.visibilityCheckbox').filter('[name=receiverLeaderCheckbox]');
    var recipientTeamCanSeeAnswerCheckbox = $containingForm.find('input.answerCheckbox').filter('[value=RECEIVER_TEAM_MEMBERS]');

    if (recipientCanSeeAnswerCheckbox.prop('checked')) {
        recipientTeamCanSeeAnswerCheckbox.prop('checked', true);
    }
}

/**
 * Pushes the values of all checked check boxes for the specified question
 * into the appropriate feedback question parameters.
 * @returns questionNum
 */
function tallyCheckboxes(questionNum) {
    // update hidden parameters (the values in checkboxTypes)
    var checkboxTypes = {
        '.answerCheckbox': _const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO,
        '.giverCheckbox': _const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO,
        '.recipientCheckbox': _const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO
    };

    $.each(checkboxTypes, function (className, checkboxType) {
        var checked = [];
        $('#form_editquestion-' + questionNum).find(className + ':checked').each(function () {
            checked.push($(this).val());
        });
        $('[name=' + checkboxType + ']').val(checked.toString());
    });
}

/**
 * Binds each question's check box field such that the user
 * cannot select an invalid combination.
 */
function formatCheckBoxes() {
    $('input.answerCheckbox').change(function () {
        if (!$(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input.giverCheckbox').prop('checked', false);
            $editTabRows.find('input.recipientCheckbox').prop('checked', false);
        }
    });
    $('input.giverCheckbox').change(function () {
        if ($(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input.answerCheckbox').prop('checked', true).trigger('change');
        }
    });
    $('input.recipientCheckbox').change(function () {
        if ($(this).is(':checked')) {
            var $editTabRows = $(this).closest('tr');
            $editTabRows.find('input.answerCheckbox').prop('checked', true);
        }
    });
    $('input[name=receiverLeaderCheckbox]').change(function () {
        var $editTabRows = $(this).closest('tr');
        $editTabRows.find('input[name=receiverFollowerCheckbox]').prop('checked', $(this).prop('checked'));
    });
}

function enableRow($containingForm, row) {
    var $table = $containingForm.find('.visibilityOptions').find('table');
    $($table.children().children()[row]).show();
}

function disableRow($containingForm, row) {
    var $table = $containingForm.find('.visibilityOptions').find('table');
    var $row = $($table.children().children()[row]);
    $row.find('input[type="checkbox"]').each(function (index, checkbox) {
        checkbox.checked = false;
    });
    $row.hide();
}

function enableAllRows($containingForm) {
    var allRows = [ROW_RECIPIENT, ROW_GIVER_TEAM, ROW_RECIPIENT_TEAM, ROW_OTHER_STUDENTS, ROW_INSTRUCTORS];
    allRows.forEach(function (row) {
        enableRow($containingForm, row);
    });
}

function disableRowsAccordingToRecipient($containingForm) {
    var recipientType = $containingForm.find('select[name="recipienttype"]').val();
    switch (recipientType) {
        case 'SELF':
            // ROW_RECIPIENT is disabled because self-feedback is always visible to giver
            disableRow($containingForm, ROW_RECIPIENT);
            // ROW_RECIPIENT_TEAM is disabled because it is the same as ROW_GIVER_TEAM
            disableRow($containingForm, ROW_RECIPIENT_TEAM);
            break;
        case 'STUDENTS':
            // all options enabled when recipientType is STUDENTS (subject to options disabled by giverType)
            break;
        case 'OWN_TEAM':
            // ROW_RECIPIENT and ROW_RECIPIENT_TEAM are disabled because they are the same as ROW_GIVER_TEAM
            disableRow($containingForm, ROW_RECIPIENT);
            disableRow($containingForm, ROW_RECIPIENT_TEAM);
            break;
        case 'INSTRUCTORS':
            // ROW_RECIPIENT_TEAM is disabled because it is the same as ROW_INSTRUCTORS
            disableRow($containingForm, ROW_RECIPIENT_TEAM);
            break;
        case 'TEAMS':
            // ROW_RECIPIENT_TEAM is disabled because it is the same as ROW_RECIPIENT
            disableRow($containingForm, ROW_RECIPIENT_TEAM);
            break;
        case 'OWN_TEAM_MEMBERS':
        case 'OWN_TEAM_MEMBERS_INCLUDING_SELF':
            // ROW_RECIPIENT_TEAM is disabled for OWN_TEAM_MEMBERS and OWN_TEAM_MEMBERS_INCLUDING_SELF
            // because it is the same as ROW_GIVER_TEAM
            disableRow($containingForm, ROW_RECIPIENT_TEAM);
            break;
        case 'NONE':
            // ROW_RECIPIENT and ROW_RECIPIENT_TEAM are disabled because there are no recipients
            disableRow($containingForm, ROW_RECIPIENT);
            disableRow($containingForm, ROW_RECIPIENT_TEAM);
            break;
        default:
            throw new Error('Unexpected recipientType');
    }
}

function disableRowsAccordingToGiver($containingForm) {
    var giverType = $containingForm.find('select[name="givertype"]').val();
    switch (giverType) {
        case 'STUDENTS':
            // all options enabled when giverType is STUDENTS (subject to options disabled by recipientType)
            break;
        case 'SELF':
        case 'INSTRUCTORS':
            // ROW_GIVER_TEAM is disabled for SELF and INSTRUCTORS because it is the same as ROW_INSTRUCTORS
            disableRow($containingForm, ROW_GIVER_TEAM);
            break;
        case 'TEAMS':
            // ROW_GIVER_TEAM is disabled for TEAMS because giver can always see the response
            disableRow($containingForm, ROW_GIVER_TEAM);
            break;
        default:
            throw new Error('Unexpected giverType');
    }
}

function disableRowsForSpecificGiverRecipientCombinations($containingForm) {
    var giverType = $containingForm.find('select[name="givertype"]').val();
    var recipientType = $containingForm.find('select[name="recipienttype"]').val();

    if ((giverType === 'SELF' || giverType === 'INSTRUCTORS') && recipientType === 'SELF') {
        // ROW_RECIPIENT_TEAM is disbled because it is the same as ROW_INSTRUCTORS
        disableRow($containingForm, ROW_RECIPIENT_TEAM);
    } else if (giverType === 'TEAMS' && recipientType === 'OWN_TEAM_MEMBERS_INCLUDING_SELF') {
        // ROW_RECIPIENT is disbled because this is almost like a self-feedback where giver can always see the response
        disableRow($containingForm, ROW_RECIPIENT);
    }
}

// Meant to be declared outside to prevent unncessary AJAX calls
var previousFormDataMap = {};

/**
 * Updates the visibility checkboxes div to show/hide visibility option rows
 * according to the feedback path
 */
function updateVisibilityCheckboxesDiv($containingForm) {
    enableAllRows($containingForm);

    disableRowsAccordingToGiver($containingForm);
    disableRowsAccordingToRecipient($containingForm);
    disableRowsForSpecificGiverRecipientCombinations($containingForm);

    // handles edge case for Team Contribution Question:
    // normal behavior is that all hidden checkboxes are unchecked, but Team Contribution Question expect even the hidden
    // Recipient's Team Members can see answer checkbox to be checked
    fixCheckboxValuesForTeamContribQuestion($containingForm);
}

function showVisibilityCheckboxesIfCustomOptionSelected($containingForm) {
    var selectedOption = $containingForm.find('.visibility-options-dropdown > button').text().trim();
    var $visibilityCheckboxes = $containingForm.find('.visibilityOptions');
    if (selectedOption === 'Custom visibility option:') {
        updateVisibilityCheckboxesDiv($containingForm);
        $visibilityCheckboxes.show();
    } else {
        $visibilityCheckboxes.hide();
    }
}

function formatVisibilityMessageDivHtml(visibilityMessage) {
    var htmlString = 'This is the visibility hint as seen by the feedback giver:';
    htmlString += '<ul class="text-muted background-color-warning">';
    for (var i = 0; i < visibilityMessage.length; i += 1) {
        htmlString += '<li>' + visibilityMessage[i] + '</li>';
    }
    htmlString += '</ul>';
    return htmlString;
}

/**
 * Updates visibility message div with error message and add onclick event for re-loading the visibility message
 */
function showAjaxErrorMessage($containingForm) {
    var $visibilityMessageDiv = $containingForm.find('.visibilityMessage');

    var htmlString = 'This is the visibility hint as seen by the feedback giver:';
    htmlString += '<ul class="text-muted background-color-warning">';
    htmlString += '<li><a>Error loading visibility hint. Click here to retry.</a></li>';
    htmlString += '</ul>';

    $visibilityMessageDiv.html(htmlString);
    $visibilityMessageDiv.find('ul').on('click', function () {
        $visibilityMessageDiv.html('');
        updateVisibilityMessageDiv($containingForm); // eslint-disable-line no-use-before-define
    });
}

/**
 * Updates the visibility message div according to configurations in the
 * visibility checkboxes div (using AJAX)
 * @param $containingForm
 */
function updateVisibilityMessageDiv($containingForm) {
    var questionNum = $containingForm.find('[name=questionnum]').val();
    var newQuestionNum = $('input[name=questionnum]').last().val();

    if (questionNum === newQuestionNum) {
        tallyCheckboxes(NEW_QUESTION);
    } else {
        tallyCheckboxes(questionNum);
    }

    var formData = $containingForm.serialize();
    var $visibilityMessageDiv = $containingForm.find('.visibilityMessage');

    if (previousFormDataMap[questionNum] === formData) {
        return;
    }

    // empty current visibility message in the form
    $visibilityMessageDiv.html('');

    var url = '/page/instructorFeedbackQuestionvisibilityMessage';
    $.ajax({
        type: 'POST',
        url: url,
        data: formData,
        beforeSend: function beforeSend() {
            $visibilityMessageDiv.html("<img src='/images/ajax-loader.gif'/>");
        },
        success: function success(data) {
            // update stored form data
            previousFormDataMap[questionNum] = formData;

            $visibilityMessageDiv.html(formatVisibilityMessageDivHtml(data.visibilityMessage));
        },
        error: function error() {
            showAjaxErrorMessage($containingForm);
        }
    });
}

// ////////////// //
// EVENT HANDLERS //
// ////////////// //

function matchVisibilityOptionToFeedbackPath(selectedFeedbackPathOption) {
    var $containingForm = $(selectedFeedbackPathOption).closest('form');
    updateVisibilityCheckboxesDiv($containingForm);
}

function getVisibilityMessage(clickedButton) {
    var $containingForm = $(clickedButton).closest('form');
    updateVisibilityMessageDiv($containingForm);
}

/**
 * binds events to the visibility dropdown menu to
 *  - show/hide visibility checkboxes div
 *  - update dropdown button text to reflected selected option
 *  - update visibility message div
 */
function attachVisibilityDropdownEvent() {
    $('body').on('click', '.visibility-options-dropdown-option', function () {
        var $clickedElem = $(this);
        var selectedOption = $clickedElem.data('optionName');
        var $containingForm = $clickedElem.closest('form');

        setVisibilityDropdownMenuText($clickedElem.text(), $containingForm);

        var $editTab = $containingForm.find('.visibilityOptions');
        if (selectedOption === 'OTHER') {
            $editTab.show();
            updateVisibilityCheckboxesDiv($containingForm);
        } else {
            // only uncheck all checkboxes and update accordingly if a common option is selected
            uncheckAllVisibilityOptionCheckboxes($containingForm);
            checkCorrespondingCheckboxes(selectedOption, $containingForm);
            $editTab.hide();
        }

        updateVisibilityMessageDiv($containingForm);
    });
}

/**
 * binds click event of each visibility checkbox to update visibility message div
 */
function attachVisibilityCheckboxEvent() {
    $('body').on('change', '.visibilityCheckbox', function () {
        var $containingForm = $(this).closest('form');
        updateVisibilityMessageDiv($containingForm);
    });
}

exports.attachVisibilityCheckboxEvent = attachVisibilityCheckboxEvent;
exports.attachVisibilityDropdownEvent = attachVisibilityDropdownEvent;
exports.formatCheckBoxes = formatCheckBoxes;
exports.getVisibilityMessage = getVisibilityMessage;
exports.matchVisibilityOptionToFeedbackPath = matchVisibilityOptionToFeedbackPath;
exports.showVisibilityCheckboxesIfCustomOptionSelected = showVisibilityCheckboxesIfCustomOptionSelected;
exports.tallyCheckboxes = tallyCheckboxes;

/***/ }),
/* 35 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.createRowForResultTable = exports.InstructorError = exports.Instructor = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _const = __webpack_require__(0);

var _crypto = __webpack_require__(8);

var _sanitizer = __webpack_require__(11);

var _assert = __webpack_require__(22);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/**
 * Represents the result of an AJAX request made to add an instructor.
 * Encapsulates the instructor for which an add was attempted and the AJAX response (responseData).
 * The instructor can be either an actual Instructor or an InstructorError (which may arise
 * from a parse error when Instructor.createFromString is used), but never null.
 * The responseData is null exactly when the instructor is an InstructorError.
 *
 */
var InstructorAjaxResult = function () {
    function InstructorAjaxResult(instructor, responseData) {
        _classCallCheck(this, InstructorAjaxResult);

        (0, _assert.assertDefined)(instructor, 'InstructorAjaxResult cannot be constructed without instructor being defined.');
        (0, _assert.assert)(!instructor.isError() || responseData === null, 'Response data must be null if instantiating with an error.');
        this.instructor = instructor;
        this.responseData = responseData;
    }

    _createClass(InstructorAjaxResult, [{
        key: 'isError',
        value: function isError() {
            return this.instructor.isError();
        }
    }, {
        key: 'isAddFailed',
        value: function isAddFailed() {
            return !this.isError() && !this.responseData.isInstructorAddingResultForAjax;
        }
        /**
         * If The AJAX request never happened because the instructor itself is erroneous,
         * return the error message encapsulated in the InstructorError.
         * Otherwise, return the AJAX status message.
         */

    }, {
        key: 'getStatusMessage',
        value: function getStatusMessage() {
            if (this.isError()) {
                return this.instructor.getErrorMessage();
            }
            return this.responseData.statusForAjax;
        }
    }]);

    return InstructorAjaxResult;
}();

/**
 * Stands in for an Instructor whenever an error occurs.
 * The isError() method returns true here (but false in the Instructor class).
 * In the event that a string was used to construct the Instructor
 * but the construction failed, that string can be passed in to the InstructorError constructor.
 */


var InstructorError = function () {
    function InstructorError(message, originalString) {
        _classCallCheck(this, InstructorError);

        this.message = message;
        this.originalString = originalString;
    }

    _createClass(InstructorError, [{
        key: 'toString',
        value: function toString() {
            return this.originalString;
        }
        /* eslint-disable class-methods-use-this */

    }, {
        key: 'isError',
        value: function isError() {
            return true;
        }
        /* eslint-enable class-methods-use-this */

    }, {
        key: 'getErrorMessage',
        value: function getErrorMessage() {
            return this.message;
        }
    }]);

    return InstructorError;
}();

/**
 * Represents an instructor.
 * Contains the shortname, name, email and institution fields.
 * Static "constructor" functions are used since constructors cannot be overloaded in ES6.
 */


var Instructor = function () {
    function Instructor() {
        _classCallCheck(this, Instructor);
    }

    _createClass(Instructor, [{
        key: 'toString',
        value: function toString() {
            return this.name + ' | ' + this.email + ' | ' + this.institution;
        }
    }, {
        key: 'getParamString',
        value: function getParamString() {
            return $.param({
                instructorshortname: this.shortName,
                instructorname: this.name,
                instructoremail: this.email,
                instructorinstitution: this.institution
            });
        }
        /* eslint-disable class-methods-use-this */

    }, {
        key: 'isError',
        value: function isError() {
            return false;
        }
        /* eslint-enable class-methods-use-this */

    }], [{
        key: 'create',

        /**
         * Takes in several instructor attributes and constructs an instructor.
         */
        value: function create(shortName, name, email, institution) {
            var instructor = new Instructor();
            instructor.shortName = shortName;
            instructor.name = name;
            instructor.email = email;
            instructor.institution = institution;
            return instructor;
        }
        /**
         * Takes in a string in either of the following formats:
         *  NAME | EMAIL | INSTITUTION
         *  NAME\tEMAIL\tINSTITUTION
         * If the string is in the correct format, parses it and constructs an Instructor.
         * If the format is wrong, constructs and returns a InstructorError instead.
         */

    }, {
        key: 'createFromString',
        value: function createFromString(str) {
            var regexStringForPipeSeparator = '(?: *\\| *)';
            var regexStringForTabSeparator = '\\t+';
            var regexStringForSeparator = '(?:' + regexStringForTabSeparator + '|' + regexStringForPipeSeparator + ')';
            var regexStringForFields = '([^|\\t]+?)';

            var instructorMatchRegex = new RegExp('^' + [regexStringForFields, // name
            regexStringForSeparator, regexStringForFields, // email
            regexStringForSeparator, regexStringForFields].join('') + '$');

            var instructorData = str.match(instructorMatchRegex);
            if (instructorData === null) {
                return new InstructorError(_const.Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID, str);
            }

            var shortName = instructorData[1];
            var name = instructorData[1];
            var email = instructorData[2];
            var institution = instructorData[3];
            return Instructor.create(shortName, name, email, institution);
        }
    }, {
        key: 'allFromString',
        value: function allFromString(multipleInstructorsString) {
            return multipleInstructorsString.split('\n').map(function (str) {
                return str.trim();
            }) // remove trailing spaces, reduces whitespace-only lines to empty string
            .filter(function (str) {
                return str !== '';
            }) // get rid of any blank/whitespace-only lines
            .map(function (singleInstructorString) {
                return Instructor.createFromString(singleInstructorString);
            });
        }
    }, {
        key: 'allToString',
        value: function allToString(instructors) {
            return instructors.map(function (instructor) {
                return instructor.toString();
            }).join('\n');
        }
    }]);

    return Instructor;
}();

/**
 * Generates HTML text for a row containing instructor's information
 * and status of the action.
 *
 * @param {String} shortName
 * @param {String} name
 * @param {String} email
 * @param {String} institution
 * @param {bool} isSuccess is a flag to show the action is successful or not.
 * The color and status of the row is affected by its value.
 * @param {String} status
 * @returns {String} a HTML row of action result table
 */


function createRowForResultTable(shortName, name, email, institution, isSuccess, status) {
    return '\n    <tr class="' + (isSuccess ? 'success' : 'danger') + '">\n        <td>' + (0, _sanitizer.encodeHtmlString)(shortName) + '</td>\n        <td>' + (0, _sanitizer.encodeHtmlString)(name) + '</td>\n        <td>' + (0, _sanitizer.encodeHtmlString)(email) + '</td>\n        <td>' + (0, _sanitizer.encodeHtmlString)(institution) + '</td>\n        <td>' + (isSuccess ? 'Success' : 'Fail') + '</td>\n        <td>' + status + '</td>\n    </tr>\n    ';
}

/**
 * Disables the Add Instructor form.
 */
function disableAddInstructorForm() {
    $('.addInstructorBtn').each(function () {
        $(this).html("<img src='/images/ajax-loader.gif'/>");
    });
    $('.addInstructorFormControl').each(function () {
        $(this).prop('disabled', true);
    });
}

/**
 * Enables the Add Instructor form.
 */
function enableAddInstructorForm() {
    $('.addInstructorBtn').each(function () {
        $(this).html('Add Instructor');
    });
    $('.addInstructorFormControl').each(function () {
        $(this).prop('disabled', false);
    });
}

/**
 * Is called as part of the AJAX call used to add instructors.
 * Updates the table that displays whether adding the instructor was successful.
 * In the event of an error, preserves the text entered into the forms so that a
 * retry can be attempted.
 * Takes in the total number of instructors and the number of instructors processed so far,
 * and updates the view to show progress.
 */
function updateInstructorAddStatus(ajaxResult, numInstructors, numInstructorsProcessed) {
    var shortName = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorShortName;
    var name = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorName;
    var email = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorEmail;
    var institution = ajaxResult.isError() ? '-' : ajaxResult.responseData.instructorInstitution;
    var isSuccess = !ajaxResult.isError() && !ajaxResult.isAddFailed();
    var status = ajaxResult.getStatusMessage();

    var rowText = createRowForResultTable(shortName, name, email, institution, isSuccess, status);
    $('#addInstructorResultTable tbody').append(rowText);
    var panelHeader = '<strong>Result (' + numInstructorsProcessed + '/' + numInstructors + ')</strong>';
    $('#addInstructorResultPanel div.panel-heading').html(panelHeader);
}

/**
 * Takes in a list of instructor objects and an error handling function.
 * Adds the instructors one by one recursively, in the following manner:
 *  Base Case      - If there are no instructors left to add, the postProcess function (if defined)
 *                   is called on the AJAX responses. Then re-enable the instructor add form.
 *  Recursive Case - Add the first instructor, note if the addition failed and make a recursive call to
 *                   add the remaining instructors in the AJAX callback function.
 * This is done as it is the simplest solution that sidesteps race conditions and
 * does not involve busy waiting in the main thread.
 */
function addInstructors(instructors, postProcess) {
    var ajaxResults = [];

    var numInstructors = instructors.length;
    var numInstructorsProcessed = 0;

    /* eslint-disable no-shadow */
    var addInstructorsHelper = function addInstructorsHelper(instructors) {
        if (instructors.length === 0) {
            if (postProcess) {
                postProcess(ajaxResults);
            }
            enableAddInstructorForm();
            return;
        }

        var firstInstructor = instructors[0];
        var remainingInstructors = instructors.slice(1);

        numInstructorsProcessed += 1;

        if (firstInstructor.isError()) {
            var parseErrorResult = new InstructorAjaxResult(firstInstructor, null);
            ajaxResults.push(parseErrorResult);
            updateInstructorAddStatus(parseErrorResult, numInstructors, numInstructorsProcessed);
            addInstructorsHelper(remainingInstructors);
            return;
        }

        $.ajax({
            type: 'POST',
            url: '/admin/adminInstructorAccountAdd?' + (0, _crypto.makeCsrfTokenParam)() + '&' + firstInstructor.getParamString(),
            beforeSend: disableAddInstructorForm,
            error: function error() {
                var ajaxErrorMsg = 'Cannot send Ajax Request!';
                var ajaxErrorResult = new InstructorAjaxResult(new InstructorError(ajaxErrorMsg), null);
                ajaxResults.push(ajaxErrorResult);
                updateInstructorAddStatus(ajaxErrorResult, numInstructors, numInstructorsProcessed);
                addInstructorsHelper(remainingInstructors);
            },
            success: function success(data) {
                var ajaxResult = new InstructorAjaxResult(firstInstructor, data);
                ajaxResults.push(ajaxResult);
                updateInstructorAddStatus(ajaxResult, numInstructors, numInstructorsProcessed);
                addInstructorsHelper(remainingInstructors);
            }
        });
    };
    /* eslint-enable no-shadow */

    addInstructorsHelper(instructors);
}

function addInstructorFromFirstFormByAjax() {
    var $instructorsAddTextArea = $('#addInstructorDetailsSingleLine');

    var instructors = Instructor.allFromString($instructorsAddTextArea.val());
    var postProcess = function postProcess(ajaxResults) {
        var failedInstructors = ajaxResults.filter(function (ajaxResult) {
            return ajaxResult.isError() || ajaxResult.isAddFailed();
        }).map(function (ajaxResult) {
            return ajaxResult.instructor;
        });
        $instructorsAddTextArea.val(Instructor.allToString(failedInstructors));
    };

    $('#addInstructorResultPanel').show(); // show the hidden panel
    $('#addInstructorResultTable tbody').html(''); // clear table
    $('#addInstructorDetailsSingleLine').val(''); // clear input form
    $('#addInstructorResultPanel div.panel-heading').html('<strong>Result</strong>'); // clear panel header

    addInstructors(instructors, postProcess);
}

function addInstructorFromSecondFormByAjax() {
    $('#addInstructorResultPanel').show(); // show the hidden panel
    $('#addInstructorResultTable tbody').html(''); // clear table
    $('#addInstructorResultPanel div.panel-heading').html('<strong>Result</strong>'); // clear panel header

    var instructorToAdd = Instructor.create($('#instructorShortName').val(), $('#instructorName').val(), $('#instructorEmail').val(), $('#instructorInstitution').val());

    addInstructors([instructorToAdd]);
}

$(document).ready(function () {
    $('#btnAddInstructorDetailsSingleLineForm').on('click', function () {
        addInstructorFromFirstFormByAjax();
    });

    $('#btnAddInstructor').on('click', function () {
        addInstructorFromSecondFormByAjax();
    });
});

exports.Instructor = Instructor;
exports.InstructorError = InstructorError;
exports.createRowForResultTable = createRowForResultTable;

/***/ }),
/* 36 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.submissionCounter = undefined;

var _checkBrowserVersion = __webpack_require__(23);

// TESTIMONIALS
/* eslint-disable max-len */ // testimonials are better left off as is
var TESTIMONIALS = ['Congratulations for creating and managing such a wonderful and useful tool. I am planning to use for all the subjects I am teaching from now after getting fantastic feedback about this tool from my students. <br>- Faculty user, Australia', 'I just wanted to let you know that TEAMMATES has been a great success!  Students love it. <br>-Faculty user, USA', 'I had such a great experience with TEAMMATES in the previous semester that I am back for more! <br>-Faculty user, Pakistan', 'Thank you for this. I think it is brilliant. <br>-Faculty user, Canada', 'I found the TEAMMATES system really easy to use. On the whole a very positive experience. Using TEAMMATES certainly helps with one of the main potential problems of group-based assessments. <br>-Faculty user, Singapore', 'I find it really great and so simple to use. <br>-Faculty user, Austria', 'These peer evaluations will be perfect for classes.  I can already see that this is going to be an excellent tool as I need the teams to evaluate each other on a weekly basis.  Adding a new evaluation item and the questions/response criteria is so easy through your system. <br>-Faculty user, USA', 'Thank you for building such a wonderful tool. <br>-Faculty user, Canada', 'I would absolutely recommend TEAMMATES. I haven\'t seen anything that\'s better, as well as being open source. It works very well for us. <br>-Faculty user, UK', 'I just started exploring TEAMMATES and am very impressed. Wish I discovered it earlier. <br>-Faculty user, Singapore'];
/* eslint-enable max-len */
var LOOP_INTERVAL = '5000'; // in milliseconds
var CURRENT_TESTIMONIAL = 0;

// Format large number with commas
function formatNumber(n) {
    var number = String(n);
    var expression = /(\d+)(\d{3})/;
    while (expression.test(number)) {
        number = number.replace(expression, '$1,$2');
    }
    return number;
}

function submissionCounter(currentDate, baseDate, submissionPerHour, baseCount) {
    var errorMsg = 'Thousands of';
    if (!currentDate || !baseDate) {
        return errorMsg;
    }
    var currBaseDateDifference = currentDate - baseDate;
    if (currBaseDateDifference < 0) {
        return errorMsg;
    }

    var hr = currBaseDateDifference / 60 / 60 / 1000; // convert from millisecond to hour
    var numberOfSubmissions = Math.floor(hr * submissionPerHour);
    numberOfSubmissions += baseCount;
    return formatNumber(numberOfSubmissions);
}

// looping through all the testimonials
function loopTestimonials() {
    var tc = $('#testimonialContainer');

    // intended null checking and early return, to prevent constant failures in JavaScript tests
    if (tc.length === 0) {
        return;
    }

    tc.html(TESTIMONIALS[CURRENT_TESTIMONIAL]);
    CURRENT_TESTIMONIAL = (CURRENT_TESTIMONIAL + 1) % TESTIMONIALS.length;
}

// Setting submission count at page load
$('document').ready(function () {
    // Parameters for the estimation calculation
    var baseDate = new Date('May 04, 2017 00:00:00'); // The date the parameters were adjusted
    var baseCount = 6500000; // The submission count on the above date
    var submissionPerHour = 128; // The rate at which the submission count is growing

    // set the submission count in the page
    var currentDate = new Date();
    $('#submissionsNumber').html(submissionCounter(currentDate, baseDate, submissionPerHour, baseCount));

    loopTestimonials();
    window.setInterval(loopTestimonials, LOOP_INTERVAL);

    (0, _checkBrowserVersion.checkBrowserVersion)();
});

exports.submissionCounter = submissionCounter;

/***/ }),
/* 37 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.isUserTyping = undefined;

var _bootboxWrapper = __webpack_require__(2);

var _const = __webpack_require__(0);

var _instructor = __webpack_require__(3);

function isUserTyping(str) {
    return str.indexOf('\t') === -1 && str.indexOf('|') === -1;
}

window.isUserTyping = isUserTyping;

var loadUpFunction = function loadUpFunction() {
    var typingErrMsg = 'Please use | character ( shift+\\ ) to seperate fields, or copy from your existing spreadsheet.';
    var notified = false;

    var ENTER_KEYCODE = 13;
    var enrolTextbox = $('#enrollstudents');
    if (enrolTextbox.length) {
        enrolTextbox = enrolTextbox[0];
        $(enrolTextbox).keydown(function (e) {
            var keycode = e.which || e.keyCode;
            if (keycode === ENTER_KEYCODE) {
                if (isUserTyping(e.target.value) && !notified) {
                    notified = true;
                    (0, _bootboxWrapper.showModalAlert)('Invalid separator', typingErrMsg, null, _const.StatusType.WARNING);
                }
            }
        });
    }
};

if (window.addEventListener) {
    window.addEventListener('load', loadUpFunction);
} else {
    window.attachEvent('load', loadUpFunction);
}

$(document).ready(function () {
    (0, _instructor.prepareInstructorPages)();
});

exports.isUserTyping = isUserTyping;

/***/ }),
/* 38 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.extractQuestionNumFromEditFormId = undefined;

var _bootboxWrapper = __webpack_require__(2);

var _const = __webpack_require__(0);

var _crypto = __webpack_require__(8);

var _datepicker = __webpack_require__(9);

var _feedbackPath = __webpack_require__(24);

var _helper = __webpack_require__(1);

var _instructor = __webpack_require__(3);

var _instructorFeedbacks = __webpack_require__(25);

var _questionConstSum = __webpack_require__(26);

var _questionContrib = __webpack_require__(27);

var _questionMcq = __webpack_require__(28);

var _questionMsq = __webpack_require__(29);

var _questionNumScale = __webpack_require__(10);

var _questionRank = __webpack_require__(30);

var _questionRubric = __webpack_require__(31);

var _richTextEditor = __webpack_require__(32);

var _scrollTo = __webpack_require__(4);

var _statusMessage = __webpack_require__(12);

var _ui = __webpack_require__(6);

var _visibilityOptions = __webpack_require__(34);

/* global tinymce:false */

var NEW_QUESTION = -1;

var WARNING_DISCARD_CHANGES = 'Warning: Any unsaved changes will be lost';
var CONFIRM_DISCARD_CHANGES = 'Are you sure you want to discard your unsaved edits?';
var CONFIRM_DISCARD_NEW_QNS = 'Are you sure you want to discard this question?';

var WARNING_DELETE_QNS = 'Warning: Deleted question cannot be recovered';
var CONFIRM_DELETE_QNS = 'Are you sure you want to delete this question?';

var WARNING_EDIT_DELETE_RESPONSES = 'Warning: Existing responses will be deleted by your action';
var CONFIRM_EDIT_DELETE_RESPONSES = '<p>Editing these fields will result in <strong>all existing responses for this question to be deleted.</strong></p>' + '<p>Are you sure you want to continue?</p>';

var FEEDBACK_QUESTION_TYPENAME_TEXT = 'Essay question';
var FEEDBACK_QUESTION_TYPENAME_MCQ = 'Multiple-choice (single answer)';
var FEEDBACK_QUESTION_TYPENAME_MSQ = 'Multiple-choice (multiple answers)';
var FEEDBACK_QUESTION_TYPENAME_NUMSCALE = 'Numerical-scale question';
var FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION = 'Distribute points (among options) question';
var FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT = 'Distribute points (among recipients) question';
var FEEDBACK_QUESTION_TYPENAME_CONTRIB = 'Team contribution question';
var FEEDBACK_QUESTION_TYPENAME_RUBRIC = 'Rubric question';
var FEEDBACK_QUESTION_TYPENAME_RANK_OPTION = 'Rank options question';
var FEEDBACK_QUESTION_TYPENAME_RANK_RECIPIENT = 'Rank recipients question';

var DISPLAY_FEEDBACK_QUESTION_COPY_INVALID = 'There are no questions to be copied.';
var DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID = 'Please enter the maximum number of recipients each respondents should give feedback to.';
var DISPLAY_FEEDBACK_QUESTION_TEXTINVALID = 'Please enter a valid question. The question text cannot be empty.';
var DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID = 'Please enter valid options. The min/max/step cannot be empty.';
var DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID = 'Please enter valid options. The interval is not divisible by the specified increment.';
var DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID = 'Feedback session visible date must not be empty';
var DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID = 'Feedback session publish date must not be empty';

var questionsBeforeEdit = [];

function getCustomDateTimeFields() {
    return $('#' + _const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE).add('#' + _const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME).add('#' + _const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE).add('#' + _const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME);
}

function extractQuestionNumFromEditFormId(id) {
    return parseInt(id.substring('form_editquestion-'.length, id.length), 10);
}

function getQuestionNumFromEditForm(form) {
    if ($(form).attr('name') === 'form_addquestions') {
        return -1;
    }
    return extractQuestionNumFromEditFormId($(form).attr('id'));
}

/**
 * Check whether the feedback question input is valid
 * @param form
 * @returns {Boolean}
 */
function checkFeedbackQuestion(form) {
    var recipientType = $(form).find('select[name|=' + _const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE + ']').find(':selected').val();
    if (recipientType === 'STUDENTS' || recipientType === 'TEAMS') {
        if ($(form).find('[name|=' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE + ']:checked').val() === 'custom' && !$(form).find('.numberOfEntitiesBox').val()) {
            (0, _statusMessage.setStatusMessageToForm)(DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID, _const.StatusType.DANGER, form);
            return false;
        }
    }
    if (!$(form).find('[name=' + _const.ParamsNames.FEEDBACK_QUESTION_TEXT + ']').val()) {
        (0, _statusMessage.setStatusMessageToForm)(DISPLAY_FEEDBACK_QUESTION_TEXTINVALID, _const.StatusType.DANGER, form);
        return false;
    }
    if ($(form).find('[name=' + _const.ParamsNames.FEEDBACK_QUESTION_TYPE + ']').val() === 'NUMSCALE') {
        if (!$(form).find('[name=' + _const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + ']').val() || !$(form).find('[name=' + _const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + ']').val() || !$(form).find('[name=' + _const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP + ']').val()) {
            (0, _statusMessage.setStatusMessageToForm)(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID, _const.StatusType.DANGER, form);
            return false;
        }
        var qnNum = getQuestionNumFromEditForm(form);
        if ((0, _questionNumScale.updateNumScalePossibleValues)(qnNum)) {
            return true;
        }
        (0, _statusMessage.setStatusMessageToForm)(DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID, _const.StatusType.DANGER, form);
        return false;
    }
    return true;
}

function checkEditFeedbackSession(form) {
    if (form.visibledate.getAttribute('disabled')) {
        if (!form.visibledate.value) {
            (0, _statusMessage.setStatusMessageToForm)(DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID, _const.StatusType.DANGER, form);
            return false;
        }
    }
    if (form.publishdate.getAttribute('disabled')) {
        if (!form.publishdate.value) {
            (0, _statusMessage.setStatusMessageToForm)(DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID, _const.StatusType.DANGER, form);
            return false;
        }
    }

    return true;
}

/**
 * Disables the editing of feedback session details.
 */
function disableEditFS() {
    // Save then disable fields
    getCustomDateTimeFields().each(function () {
        $(this).data('last', $(this).prop('disabled'));
    });
    $('#form_feedbacksession').find('text,input,button,textarea,select').prop('disabled', true);

    if (typeof _richTextEditor.richTextEditorBuilder !== 'undefined') {
        (0, _richTextEditor.destroyEditor)('instructions');
        _richTextEditor.richTextEditorBuilder.initEditor('#instructions', {
            inline: true,
            readonly: true
        });
    }

    $('#fsEditLink').show();
    $('#fsSaveLink').hide();
    $('#button_submit').hide();
}

function bindFeedbackSessionEditFormSubmission() {
    $('#form_feedbacksession').submit(function (event) {
        // Prevent form submission
        event.preventDefault();

        // populate hidden input
        if (typeof tinymce !== 'undefined') {
            tinymce.get('instructions').save();
        }
        var $form = $(event.target);
        // Use Ajax to submit form data
        $.ajax({
            url: '/page/instructorFeedbackEditSave?' + (0, _crypto.makeCsrfTokenParam)(),
            type: 'POST',
            data: $form.serialize(),
            beforeSend: function beforeSend() {
                (0, _statusMessage.clearStatusMessages)();
            },
            success: function success(result) {
                if (result.hasError) {
                    (0, _statusMessage.setStatusMessage)(result.statusForAjax, _const.StatusType.DANGER);
                } else {
                    (0, _statusMessage.setStatusMessage)(result.statusForAjax, _const.StatusType.SUCCESS);
                    disableEditFS();
                }
            }
        });
    });
}

/**
 * Disable question fields and "save changes" button for the given question number,
 * and shows the edit link.
 * @param questionNum
 */
function disableQuestion(questionNum) {
    if (typeof _richTextEditor.richTextEditorBuilder !== 'undefined') {
        (0, _richTextEditor.destroyEditor)(_const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + questionNum);
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        _richTextEditor.richTextEditorBuilder.initEditor('#' + _const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + questionNum, {
            inline: true,
            readonly: true
        });
        /* eslint-enable camelcase */
    }
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + questionNum).addClass('well');

    var $currentQuestionTable = $('#questionTable-' + questionNum);

    $currentQuestionTable.find('text,button,textarea,select,input').prop('disabled', true);

    $currentQuestionTable.find('[id^="mcqAddOptionLink-"]').hide();
    $currentQuestionTable.find('[id^="msqAddOptionLink-"]').hide();
    $currentQuestionTable.find('.removeOptionLink').hide();

    /* Check whether generate options for students/instructors/teams is selected
       If so, hide 'add Other option' */
    if ($currentQuestionTable.find('#generateMcqOptionsCheckbox-' + questionNum).prop('checked')) {
        $currentQuestionTable.find('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
    } else if ($currentQuestionTable.find('#generateMsqOptionsCheckbox-' + questionNum).prop('checked')) {
        $currentQuestionTable.find('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
    } else {
        $currentQuestionTable.find('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $currentQuestionTable.find('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
    }

    $currentQuestionTable.find('#rubricAddChoiceLink-' + questionNum).hide();
    $currentQuestionTable.find('#rubricAddSubQuestionLink-' + questionNum).hide();
    $currentQuestionTable.find('.rubricRemoveChoiceLink-' + questionNum).hide();
    $currentQuestionTable.find('.rubricRemoveSubQuestionLink-' + questionNum).hide();

    (0, _questionRubric.moveAssignWeightsCheckbox)($currentQuestionTable.find('input[id^="rubricAssignWeights"]'));

    if (!(0, _questionRubric.hasAssignedWeights)(questionNum)) {
        $currentQuestionTable.find('#rubricWeights-' + questionNum).hide();
    }

    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT + '-' + questionNum).show();
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + questionNum).hide();
    $('#button_question_submit-' + questionNum).hide();
}

/**
 * Disables all questions
 */
function disableAllQuestions() {
    var numQuestions = $('.questionTable').length;
    for (var i = 0; i < numQuestions; i += 1) {
        disableQuestion(i);
    }
}

/**
 * Enables the editing of feedback session details.
 */
function enableEditFS() {
    var $customDateTimeFields = getCustomDateTimeFields();

    $customDateTimeFields.each(function () {
        $(this).prop('disabled', $(this).data('last'));
    });

    // instructors should not be able to prevent Session Opening reminder from getting sent
    // as students without accounts need to receive the session opening email to respond
    var $sessionOpeningReminder = $('#sendreminderemail_open');

    $('#form_feedbacksession').find('text,input,button,textarea,select').not($customDateTimeFields).not($sessionOpeningReminder).not('.disabled').prop('disabled', false);

    if (typeof _richTextEditor.richTextEditorBuilder !== 'undefined') {
        (0, _richTextEditor.destroyEditor)('instructions');
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        _richTextEditor.richTextEditorBuilder.initEditor('#instructions', {
            inline: true
        });
        /* eslint-enable camelcase */
    }
    $('#fsEditLink').hide();
    $('#fsSaveLink').show();
    $('#button_submit').show();
}

/**
 * Creates a copy of the original question before any new edits
 * @param questionNum
 */
function backupQuestion(questionNum) {
    questionsBeforeEdit[questionNum] = questionsBeforeEdit[questionNum] || $('#questionTable-' + questionNum + ' > .panel-body').html();
}

/**
 * Enables question fields and "save changes" button for the given question number,
 * and hides the edit link.
 * @param questionNum
 */
function enableQuestion(questionNum) {
    if (typeof _richTextEditor.richTextEditorBuilder !== 'undefined') {
        (0, _richTextEditor.destroyEditor)(_const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + questionNum);
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        _richTextEditor.richTextEditorBuilder.initEditor('#' + _const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + questionNum, {
            inline: true
        });
        /* eslint-enable camelcase */
    }
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + questionNum).removeClass('well');

    var $currentQuestionTable = $('#questionTable-' + questionNum);

    $currentQuestionTable.find('text,button,textarea,select,input').not('[name="receiverFollowerCheckbox"]').not('.disabled_radio').prop('disabled', false);

    $currentQuestionTable.find('.removeOptionLink').show();
    $currentQuestionTable.find('.addOptionLink').show();

    $currentQuestionTable.find('#rubricAddChoiceLink-' + questionNum).show();
    $currentQuestionTable.find('#rubricAddSubQuestionLink-' + questionNum).show();
    $currentQuestionTable.find('.rubricRemoveChoiceLink-' + questionNum).show();
    $currentQuestionTable.find('.rubricRemoveSubQuestionLink-' + questionNum).show();

    if ($('#generateMcqOptionsCheckbox-' + questionNum).prop('checked')) {
        $('#mcqChoiceTable-' + questionNum).hide();
        $('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
        $('#mcqGenerateForSelect-' + questionNum).prop('disabled', false);
    } else if ($('#generateMsqOptionsCheckbox-' + questionNum).prop('checked')) {
        $('#msqChoiceTable-' + questionNum).hide();
        $('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').hide();
        $('#msqGenerateForSelect-' + questionNum).prop('disabled', false);
    } else {
        $('#mcqChoiceTable-' + questionNum).show();
        $('#msqChoiceTable-' + questionNum).show();
        $('#mcqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $('#msqOtherOptionFlag-' + questionNum).closest('.checkbox').show();
        $('#mcqGenerateForSelect-' + questionNum).prop('disabled', true);
        $('#msqGenerateForSelect-' + questionNum).prop('disabled', true);
    }

    (0, _questionMsq.toggleMsqMaxSelectableChoices)(questionNum);
    (0, _questionMsq.toggleMsqMinSelectableChoices)(questionNum);
    if ($('#constSumToRecipients-' + questionNum).val() === 'true') {
        $('#constSumOptionTable-' + questionNum).hide();
        $('#constSumOption_Option-' + questionNum).hide();
        $('#constSumOption_Recipient-' + questionNum).show();
    } else {
        $('#constSumOptionTable-' + questionNum).show();
        $('#constSumOption_Recipient-' + questionNum).hide();
    }

    $('#constSumOption_distributeUnevenly-' + questionNum).prop('disabled', false);

    if ($('#questionTable-' + questionNum).parent().find('input[name="questiontype"]').val() === 'CONTRIB') {
        (0, _questionContrib.fixContribQnGiverRecipient)(questionNum);
        (0, _questionContrib.setContribQnVisibilityFormat)(questionNum);
    }

    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT + '-' + questionNum).hide();
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + questionNum).show();
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_DISCARDCHANGES + '-' + questionNum).show();
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('edit');
    $('#button_question_submit-' + questionNum).show();
    (0, _questionRank.toggleMaxOptionsToBeRanked)(questionNum);
    (0, _questionRank.toggleMinOptionsToBeRanked)(questionNum);

    var $currentQuestionForm = $currentQuestionTable.closest('form');
    (0, _visibilityOptions.showVisibilityCheckboxesIfCustomOptionSelected)($currentQuestionForm);
    (0, _questionRubric.disableCornerMoveRubricColumnButtons)(questionNum);
}

/**
* Enables editing of question fields and enables the "save changes" button for
 * the given question number, while hiding the edit link. Does the opposite for all other questions.
 * @param questionNum
 */
function enableEdit(questionNum, maxQuestions) {
    var i = maxQuestions;
    while (i) {
        if (questionNum === i) {
            backupQuestion(i);
            enableQuestion(i);
        } else {
            disableQuestion(i);
        }
        i -= 1;
    }

    return false;
}

function enableNewQuestion() {
    if (typeof _richTextEditor.richTextEditorBuilder !== 'undefined') {
        (0, _richTextEditor.destroyEditor)(_const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + NEW_QUESTION);
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        _richTextEditor.richTextEditorBuilder.initEditor('#' + _const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION + '-' + NEW_QUESTION, {
            inline: true
        });
        /* eslint-enable camelcase */
    }

    var $newQuestionTable = $('#questionTable-' + NEW_QUESTION);

    $newQuestionTable.find('text,button,textarea,select,input').not('[name="receiverFollowerCheckbox"]').not('.disabled_radio').prop('disabled', false);
    $newQuestionTable.find('.removeOptionLink').show();
    $newQuestionTable.find('.addOptionLink').show();

    $newQuestionTable.find('#rubricAddChoiceLink-' + NEW_QUESTION).show();
    $newQuestionTable.find('#rubricAddSubQuestionLink-' + NEW_QUESTION).show();

    // If instructor had assigned rubric weights before,
    // then display the weights row, otherwise hide it.
    if ((0, _questionRubric.hasAssignedWeights)(NEW_QUESTION)) {
        $newQuestionTable.find('#rubricWeights-' + NEW_QUESTION).show();
    } else {
        $newQuestionTable.find('#rubricWeights-' + NEW_QUESTION).hide();
    }

    $newQuestionTable.find('.rubricRemoveChoiceLink-' + NEW_QUESTION).show();
    $newQuestionTable.find('.rubricRemoveSubQuestionLink-' + NEW_QUESTION).show();

    (0, _questionRubric.moveAssignWeightsCheckbox)($newQuestionTable.find('#rubricAssignWeights-' + NEW_QUESTION));

    if ($('#generateOptionsCheckbox-' + NEW_QUESTION).prop('checked')) {
        $('#mcqChoiceTable-' + NEW_QUESTION).hide();
        $('#msqChoiceTable-' + NEW_QUESTION).hide();
        $('#mcqGenerateForSelect-' + NEW_QUESTION).prop('disabled', false);
        $('#msqGenerateForSelect-' + NEW_QUESTION).prop('disabled', false);
    } else {
        $('#mcqChoiceTable-' + NEW_QUESTION).show();
        $('#msqChoiceTable-' + NEW_QUESTION).show();
        $('#mcqGenerateForSelect-' + NEW_QUESTION).prop('disabled', true);
        $('#msqGenerateForSelect-' + NEW_QUESTION).prop('disabled', true);
    }

    (0, _questionMsq.toggleMsqMaxSelectableChoices)(NEW_QUESTION);
    (0, _questionMsq.toggleMsqMinSelectableChoices)(NEW_QUESTION);
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT + '-' + NEW_QUESTION).hide();
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + NEW_QUESTION).show();
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE + '-' + NEW_QUESTION).val('edit');
    $('#button_question_submit-' + NEW_QUESTION).show();
    (0, _questionRubric.disableCornerMoveRubricColumnButtons)(NEW_QUESTION);
    (0, _questionRank.toggleMaxOptionsToBeRanked)(NEW_QUESTION);
    (0, _questionRank.toggleMinOptionsToBeRanked)(NEW_QUESTION);
}

/**
 * Pops up confirmation dialog whether to delete specified question
 * @param question questionNum
 * @returns
 */
function deleteQuestion(questionNum) {
    if (questionNum === NEW_QUESTION) {
        window.location.reload();
        return false;
    }

    var okCallback = function okCallback() {
        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('delete');
        $('#form_editquestion-' + questionNum).submit();
    };
    (0, _bootboxWrapper.showModalConfirmation)(WARNING_DELETE_QNS, CONFIRM_DELETE_QNS, okCallback, null, null, null, _const.StatusType.DANGER);
    return false;
}

function hideNewQuestionAndShowNewQuestionForm() {
    $('#questionTable-' + NEW_QUESTION).hide();
    $('#addNewQuestionTable').show();

    // re-enables all feedback path options, which may have been hidden by team contribution question
    $('#givertype-' + NEW_QUESTION).find('option').show().prop('disabled', false);
    $('#recipienttype-' + NEW_QUESTION).find('option').show().prop('disabled', false);
    $('#questionTable-' + NEW_QUESTION).find('.feedback-path-dropdown > button').removeClass('disabled');
    $('#questionTable-' + NEW_QUESTION).find('.visibility-options-dropdown .dropdown-menu li').removeClass('hidden');
    _feedbackPath.FeedbackPath.attachEvents();
}

function getQuestionNum($elementInQuestionForm) {
    var $questionForm = $elementInQuestionForm.closest('form');
    var cssId = $questionForm.attr('id');
    if (cssId.endsWith('-' + NEW_QUESTION)) {
        return NEW_QUESTION;
    }
    var splitCssId = cssId.split('-');
    return splitCssId[splitCssId.length - 1];
}

/**
 * Hides/shows the "Number of Recipients Box" of the question
 * depending on the participant type and formats the label text for it.
 * @param participantType, questionNum
 */
function formatNumberBox(participantType, questionNum) {
    var $questionForm = $('#form_editquestion-' + questionNum);
    var $numberOfEntitiesBox = $questionForm.find('.numberOfEntitiesElements');

    if (participantType === 'STUDENTS' || participantType === 'TEAMS') {
        $numberOfEntitiesBox.show();

        var $numberOfEntitiesLabel = $numberOfEntitiesBox.find('.number-of-entities-inner-text');
        $numberOfEntitiesLabel.html(participantType === 'STUDENTS' ? 'students' : 'teams');
    } else {
        $numberOfEntitiesBox.hide();
    }

    (0, _visibilityOptions.tallyCheckboxes)(questionNum);
}

var updateVisibilityOfNumEntitiesBox = function updateVisibilityOfNumEntitiesBox() {
    var questionNum = getQuestionNum($(this));
    var participantType = $(this).val();
    formatNumberBox(participantType, questionNum);
};

/**
 * Discards new changes made and restores the original question
 * @param questionNum
 */
function restoreOriginal(questionNum) {
    if (questionNum === NEW_QUESTION) {
        hideNewQuestionAndShowNewQuestionForm();
    } else {
        $('#questionTable-' + questionNum + ' > .panel-body').html(questionsBeforeEdit[questionNum]);

        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTEXT + '-' + questionNum).show();
        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_SAVECHANGESTEXT + '-' + questionNum).hide();
        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_DISCARDCHANGES + '-' + questionNum).hide();
        $('#' + _const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE + '-' + questionNum).val('');
        $('#button_question_submit-' + questionNum).hide();
        $('#questionnum-' + questionNum).val(questionNum);
        $('#questionnum-' + questionNum).prop('disabled', true);
    }

    // re-attach events for form elements
    $('#' + _const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE + '-' + questionNum).change(updateVisibilityOfNumEntitiesBox);
    _feedbackPath.FeedbackPath.attachEvents();
}

/**
 * Allows users to discard unsaved edits to the question
 */
function discardChanges(questionNum) {
    var confirmationMsg = questionNum === NEW_QUESTION ? CONFIRM_DISCARD_NEW_QNS : CONFIRM_DISCARD_CHANGES;
    var okCallback = function okCallback() {
        restoreOriginal(questionNum);
    };
    (0, _bootboxWrapper.showModalConfirmation)(WARNING_DISCARD_CHANGES, confirmationMsg, okCallback, null, null, null, _const.StatusType.WARNING);
}

/**
 * 1. Disallow non-numeric input to all inputs expecting numbers
 * 2. Initialize the visibility of 'Number of Recipients Box' according to the participant type (visible only
 * when participant type is STUDENTS OR TEAMS)
 * 3. Bind onChange of recipientType to modify numEntityBox visibility
 */
function formatNumberBoxes() {
    (0, _ui.disallowNonNumericEntries)($('input.numberOfEntitiesBox'), false, false);
    (0, _ui.disallowNonNumericEntries)($('input.minScaleBox'), false, true);
    (0, _ui.disallowNonNumericEntries)($('input.maxScaleBox'), false, true);
    (0, _ui.disallowNonNumericEntries)($('input.stepBox'), true, false);
    (0, _ui.disallowNonNumericEntries)($('input.pointsBox'), false, false);
    (0, _ui.disallowNonNumericEntries)($('input[id^="rubricWeight"]'), true, true);

    $('select[name=' + _const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE + ']').each(updateVisibilityOfNumEntitiesBox).change(updateVisibilityOfNumEntitiesBox);
}

function hideAllNewQuestionForms() {
    $('#textForm').hide();
    $('#mcqForm').hide();
    $('#msqForm').hide();
    $('#numScaleForm').hide();
    $('#constSumForm').hide();
    $('#rubricForm').hide();
    $('#contribForm').hide();
    $('#rankOptionsForm').hide();
    $('#rankRecipientsForm').hide();
}

function prepareQuestionForm(type) {
    hideAllNewQuestionForms();

    switch (type) {
        case 'TEXT':
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_TEXT);

            $('#textForm').show();
            break;
        case 'MCQ':
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_MCQ);

            $('#mcqForm').show();
            break;
        case 'MSQ':
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_MSQ);

            $('#msqForm').show();
            break;
        case 'NUMSCALE':
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_NUMSCALE);

            $('#numScaleForm').show();
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_TEXT).attr('placeholder', 'e.g. Rate the class from 1 (very bad) to 5 (excellent)');
            break;
        case 'CONSTSUM_OPTION':
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS + '-' + NEW_QUESTION).val('false');
            $('#constSumOption_Recipient-' + NEW_QUESTION).hide();
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION);

            $('#constSumForm').show();
            break;
        case 'CONSTSUM_RECIPIENT':
            {
                var optionText = $('#constSum_labelText-' + NEW_QUESTION).text();
                var tooltipText = $('#constSum_tooltipText-' + NEW_QUESTION).attr('data-original-title');

                $('#' + _const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS + '-' + NEW_QUESTION).val('true');
                $('#constSumOption_Option-' + NEW_QUESTION).hide();
                $('#constSumOption_Recipient-' + NEW_QUESTION).show();
                (0, _questionConstSum.hideConstSumOptionTable)(NEW_QUESTION);
                $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT);

                $('#constSumForm').show();
                $('#constSum_labelText-' + NEW_QUESTION).text(optionText.replace('option', 'recipient'));
                $('#constSum_tooltipText-' + NEW_QUESTION).attr('data-original-title', tooltipText.replace('option', 'recipient'));
                break;
            }
        case 'CONTRIB':
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_CONTRIB);

            $('#contribForm').show();
            (0, _questionContrib.fixContribQnGiverRecipient)(NEW_QUESTION);
            (0, _questionContrib.setContribQnVisibilityFormat)(NEW_QUESTION);
            (0, _questionContrib.setDefaultContribQnVisibilityIfNeeded)(NEW_QUESTION);
            break;
        case 'RUBRIC':
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_RUBRIC);

            $('#rubricForm').show();
            break;
        case 'RANK_OPTIONS':
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + '-' + NEW_QUESTION).val(2);
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS + '-' + NEW_QUESTION).val('false');
            $('#rankOption_Recipient-' + NEW_QUESTION).hide();
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_RANK_OPTION);

            $('#rankOptionsForm').show();
            break;
        case 'RANK_RECIPIENTS':
            $('#' + _const.ParamsNames.FEEDBACK_QUESTION_RANKTORECIPIENTS + '-' + NEW_QUESTION).val('true');
            $('#rankOption_Option-' + NEW_QUESTION).hide();
            (0, _questionRank.hideRankOptionTable)(NEW_QUESTION);
            $('#questionTypeHeader').html(FEEDBACK_QUESTION_TYPENAME_RANK_RECIPIENT);

            $('#rankRecipientsForm').show();
            break;
        default:
            // do nothing if the question type is not recognized, which should not happen
            break;
    }
}

/**
 * Copy options (Feedback giver, recipient, and all check boxes)
 * from the previous question
 */
function copyOptions(newType) {
    // If there is one or less questions, there's no need to copy.
    if ($('.questionTable').size() < 2) {
        return;
    }

    var prevType = $('input[name="questiontype"]').eq(-2).val();

    // Don't copy from non-contrib to contrib question, as these have special restrictions
    if (newType === 'CONTRIB' && prevType !== 'CONTRIB') {
        return;
    }

    // Feedback giver setup
    var $prevGiver = $('select[name="givertype"]').eq(-2);
    var $currGiver = $('select[name="givertype"]').last();

    $currGiver.val($prevGiver.val());

    // Feedback recipient setup
    var $prevRecipient = $('select[name="recipienttype"]').eq(-2);
    var $currRecipient = $('select[name="recipienttype"]').last();

    $currRecipient.val($prevRecipient.val());

    // Hide other feedback path options and update common feedback path dropdown text if a common option is selected
    var $prevQuestionForm = $('form[id^="form_editquestion-"]').eq(-2);
    var $newQuestionForm = $('#form_editquestion-' + NEW_QUESTION);

    var isPrevQuestionUsingCommonOption = _feedbackPath.FeedbackPath.isCommonOptionSelected($prevQuestionForm);
    if (isPrevQuestionUsingCommonOption) {
        _feedbackPath.FeedbackPath.hideOtherOption($newQuestionForm);
        var prevQuestionSelectedOption = _feedbackPath.FeedbackPath.getDropdownText($prevQuestionForm);
        _feedbackPath.FeedbackPath.setDropdownText(prevQuestionSelectedOption, $newQuestionForm);
    } else {
        _feedbackPath.FeedbackPath.showOtherOption($newQuestionForm);
        _feedbackPath.FeedbackPath.setDropdownText('Predefined combinations:', $newQuestionForm);
    }

    // Number of recipient setup
    formatNumberBox($currRecipient.val(), NEW_QUESTION);
    var $prevRadioButtons = $('.questionTable').eq(-2).find('input[name="numofrecipientstype"]');
    var $currRadioButtons = $('.questionTable').last().find('input[name="numofrecipientstype"]');

    $currRadioButtons.each(function (index) {
        $(this).prop('checked', $prevRadioButtons.eq(index).prop('checked'));
    });

    var $prevNumOfRecipients = $('input[name="numofrecipients"]').eq(-2);
    var $currNumOfRecipients = $('input[name="numofrecipients"]').last();

    $currNumOfRecipients.val($prevNumOfRecipients.val());

    // Check boxes setup
    var $prevTable = $('.dataTable').eq(-2).find('.visibilityCheckbox');
    var $currTable = $('.dataTable').last().find('.visibilityCheckbox');

    $currTable.each(function (index) {
        $(this).prop('checked', $prevTable.eq(index).prop('checked'));
    });

    // Hide visibility options and update common visibility options dropdown text if a common option is selected
    var prevQuestionVisibilityOption = $prevQuestionForm.find('.visibility-options-dropdown > button').text();
    $newQuestionForm.find('.visibility-options-dropdown > button').text(prevQuestionVisibilityOption);

    var isCommonVisibilityOptionSelected = prevQuestionVisibilityOption.trim() !== 'Custom visibility option:';
    if (isCommonVisibilityOptionSelected) {
        $newQuestionForm.find('.visibilityOptions').hide();
    } else {
        $newQuestionForm.find('.visibilityOptions').show();
    }

    (0, _visibilityOptions.matchVisibilityOptionToFeedbackPath)($currGiver);
}

/**
 * Sets the correct initial question number from the value field
 */
function formatQuestionNumbers() {
    var $questions = $('.questionTable');

    $questions.each(function (index) {
        var $selector = $(this).find('.questionNumber');
        $selector.val(index + 1);
        if (index !== $questions.size() - 1) {
            $selector.prop('disabled', true);
        }
    });
}

/**
 * Adds event handler to load 'copy question' modal contents by ajax.
 */
function setupQuestionCopyModal() {
    $('#copyModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var actionlink = button.data('actionlink');
        var courseid = button.data('courseid');
        var fsname = button.data('fsname');

        var $questionCopyStatusMessage = $('#question-copy-modal-status');
        $.ajax({
            type: 'GET',
            url: actionlink + '&courseid=' + encodeURIComponent(courseid) + '&fsname=' + encodeURIComponent(fsname),
            beforeSend: function beforeSend() {
                $('#button_copy_submit').prop('disabled', true);
                $('#copyTableModal').remove();
                $questionCopyStatusMessage.removeClass('alert alert-danger');
                $questionCopyStatusMessage.html('Loading possible questions to copy. Please wait ...<br>' + "<img class='margin-center-horizontal' src='/images/ajax-loader.gif'/>");
            },
            error: function error() {
                $questionCopyStatusMessage.html('Error retrieving questions. Please close the dialog window and try again.');
                $questionCopyStatusMessage.addClass('alert alert-danger');
            },
            success: function success(data) {
                var $questionRows = $(data).find('tbody > tr');
                if ($questionRows.length) {
                    $('#copyModalForm').prepend(data);
                    $questionCopyStatusMessage.html('');
                } else {
                    $questionCopyStatusMessage.addClass('alert alert-danger');
                    $questionCopyStatusMessage.prepend('<br>').html(DISPLAY_FEEDBACK_QUESTION_COPY_INVALID);
                }
            }
        });
    });
}

function bindCopyButton() {
    $('#button_copy_submit').click(function (e) {
        e.preventDefault();

        var index = 0;
        var hasRowSelected = false;

        $('#copyTableModal > tbody > tr').each(function () {
            var $this = $(this);
            var questionIdInput = $this.children('input:first');

            if (!questionIdInput.length) {
                return;
            }
            if ($this.hasClass('row-selected')) {
                $(questionIdInput).attr('name', 'questionid-' + index);
                $this.find('input.courseid').attr('name', 'courseid-' + index);
                $this.find('input.fsname').attr('name', 'fsname-' + index);

                index += 1;
                hasRowSelected = true;
            }
        });

        if (hasRowSelected) {
            $('#copyModalForm').submit();
        } else {
            (0, _statusMessage.setStatusMessage)('No questions are selected to be copied', _const.StatusType.DANGER);
            $('#copyModal').modal('hide');
        }

        return false;
    });
}

var numRowsSelected = 0;

function bindCopyEvents() {
    $('body').on('click', '#copyTableModal > tbody > tr', function (e) {
        e.preventDefault();

        if ($(this).hasClass('row-selected')) {
            $(this).removeClass('row-selected');
            $(this).children('td:first').html('<input type="checkbox">');
            numRowsSelected -= 1;
        } else {
            $(this).addClass('row-selected');
            $(this).children('td:first').html('<input type="checkbox" checked>');
            numRowsSelected += 1;
        }

        var $button = $('#button_copy_submit');

        $button.prop('disabled', numRowsSelected <= 0);

        return false;
    });
}

function hideOption($containingSelect, value) {
    $containingSelect.find('option[value="' + value + '"]').hide();
}

function setRecipientSelectToFirstVisibleOption($recipientSelect) {
    $recipientSelect.find('option').each(function (e) {
        var $recipientOption = $(this);
        if ($recipientOption.css('display') !== 'none') {
            $recipientSelect.val($recipientOption.val());
            e.preventDefault();
        }
    });
}

function hideInvalidRecipientTypeOptions($giverSelect) {
    var giverType = $giverSelect.val();
    var $recipientSelect = $giverSelect.closest('.form_question').find('select[name="recipienttype"]');
    var recipientType = $recipientSelect.val();
    switch (giverType) {
        case 'STUDENTS':
            // all recipientType options enabled
            break;
        case 'SELF':
        case 'INSTRUCTORS':
            hideOption($recipientSelect, 'OWN_TEAM_MEMBERS');
            hideOption($recipientSelect, 'OWN_TEAM_MEMBERS_INCLUDING_SELF');
            if (recipientType === 'OWN_TEAM_MEMBERS' || recipientType === 'OWN_TEAM_MEMBERS_INCLUDING_SELF') {
                setRecipientSelectToFirstVisibleOption($recipientSelect);
            }
            break;
        case 'TEAMS':
            hideOption($recipientSelect, 'OWN_TEAM');
            hideOption($recipientSelect, 'OWN_TEAM_MEMBERS');
            if (recipientType === 'OWN_TEAM' || recipientType === 'OWN_TEAM_MEMBERS') {
                setRecipientSelectToFirstVisibleOption($recipientSelect);
            }
            break;
        default:
            throw new Error('Unexpected giverType');
    }
}

function bindParticipantSelectChangeEvents() {
    $('body').on('change', 'select[name="givertype"]', function () {
        var $recipientSelect = $(this).closest('.form_question').find('select[name="recipienttype"]');
        $recipientSelect.find('option').show();
        hideInvalidRecipientTypeOptions($(this));
    });
}

function hideInvalidRecipientTypeOptionsForAllPreviouslyAddedQuestions() {
    $('.form_question').not('[name="form_addquestions"]').find('select[name="givertype"]').each(function () {
        hideInvalidRecipientTypeOptions($(this));
    });
}

function hideInvalidRecipientTypeOptionsForNewlyAddedQuestion() {
    hideInvalidRecipientTypeOptions($('form[name="form_addquestions"]').find('select[name="givertype"]'));
}

/**
 * Shows the new question div frame and scrolls to it
 */
function showNewQuestionFrame(type) {
    $('#questiontype').val(type);

    copyOptions(type);
    prepareQuestionForm(type);
    $('#questionTable-' + NEW_QUESTION).show();
    hideInvalidRecipientTypeOptionsForNewlyAddedQuestion();
    enableNewQuestion();

    $('#addNewQuestionTable').hide();
    $('#empty_message').hide();
    (0, _scrollTo.scrollToElement)($('#questionTable-' + NEW_QUESTION)[0], { duration: 1000 });

    (0, _visibilityOptions.getVisibilityMessage)($('#questionTable-' + NEW_QUESTION));
}

function prepareDescription(form) {
    var questionNum = getQuestionNum(form);
    var content = tinymce.get('questiondescription-' + questionNum).getContent();
    form.find('input[name=questiondescription]').val(content);
    form.find('input[name=questiondescription-' + questionNum + ']').prop('disabled', true);
}

/**
 * This function is called on edit page load.
 */
function readyFeedbackEditPage() {
    $(document).on('click', '.enable-edit-fs', function () {
        return enableEditFS();
    });

    // Disable all questions
    disableAllQuestions();

    // Bind submit text links
    $('a[id|=questionsavechangestext]').click(function () {
        var form = $(this).parents('form.form_question');
        prepareDescription(form);

        $(this).parents('form.form_question').submit();
    });

    // Bind submit actions
    $('form[id|=form_editquestion]').submit(function (event) {
        prepareDescription($(event.currentTarget));
        if ($(this).attr('editStatus') === 'mustDeleteResponses') {
            event.preventDefault();
            var okCallback = function okCallback() {
                event.currentTarget.submit();
            };
            (0, _bootboxWrapper.showModalConfirmation)(WARNING_EDIT_DELETE_RESPONSES, CONFIRM_EDIT_DELETE_RESPONSES, okCallback, null, null, null, _const.StatusType.DANGER);
        }
    });

    $('form.form_question').submit(function () {
        (0, _ui.addLoadingIndicator)($('#button_submit_add'), 'Saving ');
        var formStatus = checkFeedbackQuestion(this);
        if (!formStatus) {
            (0, _ui.removeLoadingIndicator)($('#button_submit_add'), 'Save Question');
        }
        return formStatus;
    });

    // Bind destructive changes
    $('form[id|=form_editquestion]').find(':input').not('.nonDestructive').not('.visibilityCheckbox').change(function () {
        var editStatus = $(this).parents('form').attr('editStatus');
        if (editStatus === 'hasResponses') {
            $(this).parents('form').attr('editStatus', 'mustDeleteResponses');
        }
    });

    $('#add-new-question-dropdown > li').click(function () {
        showNewQuestionFrame($(this).data('questiontype'));
    });

    // Copy Binding
    bindCopyButton();
    bindCopyEvents();
    setupQuestionCopyModal();

    // Additional formatting & bindings.
    if ($('#form_feedbacksession').data('' + _const.ParamsNames.FEEDBACK_SESSION_ENABLE_EDIT) === true) {
        enableEditFS();
    } else {
        disableEditFS();
    }
    (0, _instructorFeedbacks.formatSessionVisibilityGroup)();
    (0, _instructorFeedbacks.formatResponsesVisibilityGroup)();
    formatNumberBoxes();
    (0, _visibilityOptions.formatCheckBoxes)();
    formatQuestionNumbers();
    (0, _instructorFeedbacks.collapseIfPrivateSession)();

    (0, _instructor.setupFsCopyModal)();

    (0, _questionRubric.bindAssignWeightsCheckboxes)();
    (0, _questionMsq.bindMsqEvents)();
    (0, _questionRubric.bindMoveRubricColButtons)();
    (0, _questionRank.bindRankEvents)();

    // Bind feedback session edit form submission
    bindFeedbackSessionEditFormSubmission();
}

/**
 * Adds hover event handler on menu options which
 * toggles a tooltip over the submenu options
 */
function setTooltipTriggerOnFeedbackPathMenuOptions() {
    $('.dropdown-submenu').hover(function () {
        $(this).children('.dropdown-menu').tooltip('toggle');
    });
}

$(document).ready(function () {
    (0, _instructor.prepareInstructorPages)();

    (0, _datepicker.prepareDatepickers)();

    readyFeedbackEditPage();
    (0, _instructorFeedbacks.bindUncommonSettingsEvents)();
    bindParticipantSelectChangeEvents();
    (0, _instructorFeedbacks.updateUncommonSettingsInfo)();
    (0, _instructorFeedbacks.showUncommonPanelsIfNotInDefaultValues)();
    _feedbackPath.FeedbackPath.attachEvents();
    hideInvalidRecipientTypeOptionsForAllPreviouslyAddedQuestions();
    (0, _visibilityOptions.attachVisibilityDropdownEvent)();
    (0, _visibilityOptions.attachVisibilityCheckboxEvent)();
    setTooltipTriggerOnFeedbackPathMenuOptions();

    $('#fsSaveLink').on('click', function (e) {
        checkEditFeedbackSession(e.target.form);
    });

    $(document).on('change', '.participantSelect', function (e) {
        (0, _visibilityOptions.matchVisibilityOptionToFeedbackPath)(e.target);
        (0, _visibilityOptions.getVisibilityMessage)(e.target);
    });

    $(document).on('click', '.btn-discard-changes', function (e) {
        discardChanges($(e.target).data('qnnumber'));
    });

    $(document).on('click', '.btn-edit-qn', function (e) {
        var maxQuestions = parseInt($('#num-questions').val(), 10);
        enableEdit($(e.target).data('qnnumber'), maxQuestions);
    });

    $(document).on('click', '.btn-delete-qn', function (e) {
        deleteQuestion($(e.target).data('qnnumber'));
    });

    $(document).on('submit', '.tally-checkboxes', function (e) {
        (0, _visibilityOptions.tallyCheckboxes)($(e.target).data('qnnumber'));
    });
});

window.updateConstSumPointsValue = _questionConstSum.updateConstSumPointsValue;
window.addConstSumOption = _questionConstSum.addConstSumOption;
window.removeConstSumOption = _questionConstSum.removeConstSumOption;
window.addMcqOption = _questionMcq.addMcqOption;
window.removeMcqOption = _questionMcq.removeMcqOption;
window.toggleMcqGeneratedOptions = _questionMcq.toggleMcqGeneratedOptions;
window.toggleMcqOtherOptionEnabled = _questionMcq.toggleMcqOtherOptionEnabled;
window.changeMcqGenerateFor = _questionMcq.changeMcqGenerateFor;
window.addMsqOption = _questionMsq.addMsqOption;
window.removeMsqOption = _questionMsq.removeMsqOption;
window.toggleMsqGeneratedOptions = _questionMsq.toggleMsqGeneratedOptions;
window.toggleMsqOtherOptionEnabled = _questionMsq.toggleMsqOtherOptionEnabled;
window.changeMsqGenerateFor = _questionMsq.changeMsqGenerateFor;
window.updateNumScalePossibleValues = _questionNumScale.updateNumScalePossibleValues;
window.addRankOption = _questionRank.addRankOption;
window.removeRankOption = _questionRank.removeRankOption;
window.addRubricRow = _questionRubric.addRubricRow;
window.removeRubricRow = _questionRubric.removeRubricRow;
window.highlightRubricRow = _questionRubric.highlightRubricRow;
window.addRubricCol = _questionRubric.addRubricCol;
window.removeRubricCol = _questionRubric.removeRubricCol;
window.highlightRubricCol = _questionRubric.highlightRubricCol;

window.isWithinView = _helper.isWithinView;

exports.extractQuestionNumFromEditFormId = extractQuestionNumFromEditFormId;

/***/ }),
/* 39 */,
/* 40 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(14);
__webpack_require__(15);
__webpack_require__(7);
__webpack_require__(16);
__webpack_require__(17);
__webpack_require__(18);
__webpack_require__(19);
__webpack_require__(20);
module.exports = __webpack_require__(21);


/***/ })
/******/ ]);