'use strict';

/* global setStatusMessage:false StatusType:false clearStatusMessages:false */
/* global appendStatusMessage:false toggleSort:false linkAjaxForCourseStats:false */

var isFetchingCourses = false;
var needsRetrying = false;

$(document).ready(function () {
    var ajaxRequest = function ajaxRequest(e) {
        if (isFetchingCourses) {
            return;
        }
        e.preventDefault();
        var formData = $(this).serialize();
        $.ajax({
            type: 'POST',
            cache: false,
            url: $(this).attr('action') + '?' + formData,
            beforeSend: function beforeSend() {
                $('#coursesList').html('<img height="75" width="75" class="margin-center-horizontal" src="/images/ajax-preload.gif"/>');
                isFetchingCourses = true;
            },
            error: function error() {
                isFetchingCourses = false;
                needsRetrying = true;
                $('#coursesList').html('');
                setStatusMessage('Courses could not be loaded. Click <a href="#" id="retryAjax">here</a> to retry.', StatusType.WARNING);
                $('#retryAjax').click(function (ev) {
                    ev.preventDefault();
                    $('#ajaxForCourses').trigger('submit');
                });
            },
            success: function success(data) {
                isFetchingCourses = false;
                if (needsRetrying) {
                    clearStatusMessages();
                    needsRetrying = false;
                }

                var statusMessages = $(data).find('.statusMessage');
                appendStatusMessage(statusMessages);

                var appendedCoursesTable = $(data).find('#coursesList').html();
                $('#coursesList').removeClass('align-center').html(appendedCoursesTable);
                toggleSort($('#button_sortcourseid'));
                linkAjaxForCourseStats();
            }
        });
    };
    $('#ajaxForCourses').submit(ajaxRequest);
});