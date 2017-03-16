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
            var newVisibleDate = getMaxDateForVisibleDate($('#startdate').datepicker('getDate'),
                    $('#publishdate').datepicker('getDate'));
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
        onSelect: function() {
            var newPublishDate = getMinDateForPublishDate($('#visibledate').datepicker('getDate'));
            $('#publishdate').datepicker('option', 'minDate', newPublishDate);
        }
    });

    $('#publishdate').datepicker({
        dateFormat: 'dd/mm/yy',
        showOtherMonths: true,
        gotoCurrent: true,
        defaultDate: tomorrow,
        onSelect: function() {
            var newVisibleDate = getMaxDateForVisibleDate($('#startdate').datepicker('getDate'),
                    $('#publishdate').datepicker('getDate'));
            $('#visibledate').datepicker('option', 'maxDate', newVisibleDate);
        }
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
