import {
    bindBackToTopButtons,
} from '../common/administrator';

import {
    linkAjaxForResponseRate,
} from '../common/ajaxResponseRate';

import {
    prepareDatepickers,
} from '../common/datepicker';

function toggleContent(id) {
    const duration = 500;

    $(`#table_${id}`).slideToggle('slow');

    const pill = $(`#pill_${id}`).attr('class');

    if (pill === 'active') {
        $(`#pill_${id}`).attr('class', ' ');
        $(`#badge_${id}`).fadeIn(duration);
    } else {
        $(`#pill_${id}`).attr('class', 'active');
        $(`#badge_${id}`).fadeOut(duration);
    }
}

function openAllSections(count) {
    for (let i = 1; i <= count; i += 1) {
        const pill = $(`#pill_${i}`).attr('class');
        if (pill !== 'active') {
            toggleContent(i);
        }
    }
}

function closeAllSections(count) {
    for (let i = 1; i <= count; i += 1) {
        const pill = $(`#pill_${i}`).attr('class');
        if (pill === 'active') {
            toggleContent(i);
        }
    }
}

function toggleFilter() {
    $('#timeFramePanel').slideToggle('slow');

    const button = $('#detailButton').attr('class');

    if (button === 'glyphicon glyphicon-chevron-down') {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-up');
        $('#referenceText').text('Hide Filter');
    } else {
        $('#detailButton').attr('class', 'glyphicon glyphicon-chevron-down');
        $('#referenceText').text('Show Filter');
    }
}

$(document).ready(() => {
    $('#timeFramePanel').toggle();
    bindBackToTopButtons('.back-to-top-left, .back-to-top-right');
    linkAjaxForResponseRate();
    prepareDatepickers();

    $('#btn-open-all-sections').on('click', () => {
        openAllSections($('.institution-panel').length);
    });

    $('#btn-close-all-sections').on('click', () => {
        closeAllSections($('.institution-panel').length);
    });

    $('#btn-toggle-filter').on('click', () => {
        toggleFilter();
    });

    $(document).on('click', '.toggle-content', (e) => {
        const tableIndex = $(e.currentTarget).data('index');
        toggleContent(tableIndex);
    });
});
