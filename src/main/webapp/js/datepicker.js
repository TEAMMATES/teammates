'use strict';

$(document).ready(function() {
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
        onSelect: function() {
            var newVisibleDate = getMaxDateForVisibleDate($('#startdate').datepicker('getDate'));
            $('#visibledate').datepicker('option', 'maxDate', newVisibleDate);

            var newPublishDate = getMinDateForPublishDate($('#startdate').datepicker('getDate'));
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
        maxDate: today
    });

    $('#publishdate').datepicker({
        dateFormat: 'dd/mm/yy',
        showOtherMonths: true,
        gotoCurrent: true,
        defaultDate: tomorrow,
        minDate: today
    });

    triggerDatepickerOnClick([$('#startdate'), $('#enddate'), $('#visibledate'), $('#publishdate')]);

});

/**
 * Adds an event handler on all passed in divs to open datepicker on 'click'.
 * @assumption: all passed in divs are valid datepicker divs
 */
function triggerDatepickerOnClick(datepickerDivs) {
    $.each(datepickerDivs, function(i, datepickerDiv) {
        datepickerDiv.on('click', function() {
            if (!datepickerDiv.prop('disabled')) {
                datepickerDiv.datepicker('show');
            }
        });
    });
}

/**
 * @assumption: startDate has a valid value
 * @param {Date} startDate
 * @returns {Date} startDate
 */
function getMaxDateForVisibleDate(startDate) {
    return startDate;
}

/**
 * @assumption: startDate has a valid value
 * @param {Date} startDate
 * @returns {Date} startDate
 */
function getMinDateForPublishDate(startDate) {
    return startDate;
}
