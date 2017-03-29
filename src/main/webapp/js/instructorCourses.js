'use strict';

$(document).ready(function() {
    $('#ajaxForCourses').trigger('submit');
    if (typeof moment !== 'undefined') {
        var $selectElement = $('#' + COURSE_TIME_ZONE);
        TimeZone.prepareTimeZoneInput($selectElement);
        TimeZone.autoDetectAndUpdateTimeZone($selectElement);
    }
});
