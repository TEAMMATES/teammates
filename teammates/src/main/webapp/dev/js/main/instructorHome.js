import {
    linkAjaxForResponseRate,
} from '../common/ajaxResponseRate';

import {
    showModalConfirmation,
} from '../common/bootboxWrapper';

import {
    BootstrapContextualColors,
} from '../common/const';

import {
    bindDeleteButtons,
    bindPublishButtons,
    bindRemindButtons,
    bindUnpublishButtons,
    prepareInstructorPages,
    setupFsCopyModal,
} from '../common/instructor';

import {
    prepareRemindModal,
} from '../common/remindModal';

import {
    prepareResendPublishedEmailModal,
} from '../common/resendPublishedEmailModal';

import {
    showSingleCollapse,
    hideSingleCollapse,
} from '../common/ui';

const COURSE_PANELS_TO_AUTO_LOAD_COUNT = 3;

function bindCoursePanels() {
    const $panels = $('div.panel');
    let numPanels = 0;
    for (let i = 0; i < $panels.length; i += 1) {
        const $heading = $($panels[i]).children('.panel-heading');
        const $bodyCollapse = $($panels[i]).children('.panel-collapse');
        if ($heading.length !== 0 && $bodyCollapse.length !== 0) {
            $heading.data('target', `#panelBodyCollapse-${numPanels}`);
            $heading.attr('id', `panelHeading-${numPanels}`);
            $heading.css('cursor', 'pointer');
            $heading.data('state', 'up');
            $bodyCollapse.attr('id', `panelBodyCollapse-${numPanels}`);
        }
        numPanels += 1;
    }
}

/**
 * Changes the state of the course panel (collapsed/expanded).
 */
function toggleCourseVisibility(e) {
    const $targetElement = $(e.target);
    if ($targetElement.is('a') || $targetElement.is('input') || $targetElement.hasClass('dropdown-toggle')) {
        return;
    }
    const $panel = $(this);
    const $dropdowns = $panel.find('.dropdown');
    if ($panel.data('state') === 'up') {
        $dropdowns.show();
        showSingleCollapse($(e.currentTarget).data('target'));
        $panel.data('state', 'down');
    } else {
        $dropdowns.hide();
        hideSingleCollapse($(e.currentTarget).data('target'));
        $panel.data('state', 'up');
    }
}

/**
 * Updates the contents of course panel (collapse data, chevron icon)
 */
function updateCoursePanel(data, $panel, $panelCollapse) {
    const panelHeading = $(data).find('.panel-heading').html();
    $panel.find('.row').replaceWith(panelHeading);
    const chevronUp = '<span class="glyphicon glyphicon-chevron-down"></span>';
    const $updatedContent = $panel.find('.pull-right');
    $updatedContent.append(chevronUp);
    const $collapseData = $(data).find('.panel-body');
    $panelCollapse.html($collapseData[0]);
    $panel.removeClass('ajax_auto');
}

$(document).ready(() => {
    prepareInstructorPages();
    bindDeleteButtons();
    bindRemindButtons();
    bindPublishButtons();
    bindUnpublishButtons();
    bindCoursePanels();

    setupFsCopyModal();

    // Click event binding for radio buttons
    const $radioButtons = $('label[name="sortby"]');
    $.each($radioButtons, function () {
        $(this).click(function () {
            const currentPath = window.location.pathname;
            const query = window.location.search.substring(1);
            const params = {};

            const paramValues = query.split('&');
            for (let i = 0; i < paramValues.length; i += 1) {
                const paramValue = paramValues[i].split('=');
                [, params[paramValue[0]]] = paramValue;
            }

            if ('user' in params === false) {
                params.user = $('input[name="user"]').val();
            }

            window.location.href = `${currentPath}?user=${params.user}&sortby=${$(this).attr('data')}`;
        });
    });

    // Click event binding for course archive button
    $('body').on('click', '.course-archive-for-test', (event) => {
        event.preventDefault();
        const $clickedLink = $(event.currentTarget);

        const messageText = `Are you sure you want to archive ${$clickedLink.data('courseId')}? `
            + 'This action can be reverted by going to the "courses" tab and unarchiving the desired course(s).';
        const okCallback = function () {
            window.location = $clickedLink.attr('href');
        };

        showModalConfirmation('Confirm archiving course', messageText, okCallback, null,
                null, null, BootstrapContextualColors.INFO);
    });

    // AJAX loading of course panels
    const $coursePanels = $('.ajax_auto');
    $.each($coursePanels, function () {
        $(this).filter(function () {
            const isNotLoaded = $(this).parent().find('form').length;
            return isNotLoaded;
        }).click(function () {
            const $panel = $(this);
            const formData = $panel.parent().find('form').serialize();
            const content = $panel.find('.pull-right')[0];
            const $panelCollapse = $panel.parent().children('.panel-collapse');

            $.ajax({
                type: 'POST',
                url: `/page/instructorHomePage?${formData}`,
                beforeSend() {
                    $(content).html("<img src='/images/ajax-loader.gif'/>");
                },
                error() {
                    const warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
                    let errorMsg = '[ Failed to load. Click here to retry. ]';
                    errorMsg = `<strong style="margin-left: 1em; margin-right: 1em;">${errorMsg}</strong>`;
                    const chevronDown = '<span class="glyphicon glyphicon-chevron-down"></span>';
                    $(content).html(warningSign + errorMsg + chevronDown);
                },
                success(data) {
                    updateCoursePanel(data, $panel, $panelCollapse);

                    $panel.off('click');
                    // changing click event handler to avoid repeated ajax calls
                    $panel.click(toggleCourseVisibility);
                    $panel.trigger('click');

                    linkAjaxForResponseRate();
                },
            });
        });
    });

    // Automatically load top few course panels
    $coursePanels.slice(0, COURSE_PANELS_TO_AUTO_LOAD_COUNT).click();

    linkAjaxForResponseRate();

    prepareRemindModal();
    prepareResendPublishedEmailModal();
});
