'use strict';

/* global COURSE_TIME_ZONE:false TimeZone:false */

$(document).ready(function () {
    $('#ajaxForCourses').trigger('submit');
    if (typeof moment !== 'undefined') {
        var $selectElement = $('#' + COURSE_TIME_ZONE);
        TimeZone.prepareTimeZoneInput($selectElement);
        TimeZone.autoDetectAndUpdateTimeZone($selectElement);
    }
});