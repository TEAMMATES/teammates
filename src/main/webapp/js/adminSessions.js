'use strict';

$(document).ready(function() {
    $('#timeFramePanel').toggle();
    AdminCommon.bindBackToTopButtons('.back-to-top-left, .back-to-top-right');
});

function toggleContent(id) {

    var duration = 500;

    $('#table_' + id).slideToggle('slow');

    var pill = $('#pill_' + id).attr('class');

    if (pill === 'active') {
        $('#pill_' + id).attr('class', ' ');
        $('#badge_' + id).fadeIn(duration);
    } else {
        $('#pill_' + id).attr('class', 'active');
        $('#badge_' + id).fadeOut(duration);
    }

}

function openAllSections(count) {

    for (var i = 1; i <= count; i++) {
        var pill = $('#pill_' + i).attr('class');
        if (pill !== 'active') {
            toggleContent(i);
        }
    }

}

function closeAllSections(count) {

    for (var i = 1; i <= count; i++) {
        var pill = $('#pill_' + i).attr('class');
        if (pill === 'active') {
            toggleContent(i);
        }
    }

}

function toggleFilter() {
    $('#timeFramePanel').slideToggle('slow');

    var button = $('#detailButton').attr('class');

    if (button === 'glyphicon glyphicon-chevron-down') {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-up');
        $('#referenceText').text('Hide Filter');
    } else {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-down');
        $('#referenceText').text('Show Filter');
    }
}
