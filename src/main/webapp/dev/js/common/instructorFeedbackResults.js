import {
    bindPublishButtons,
    bindStudentPhotoHoverLink,
    bindStudentPhotoLink,
    bindUnpublishButtons,
    selectElementContents,
} from './instructor';

import {
    prepareRemindModal,
} from './remindModal';

import {
    setStatusMessage,
} from './statusMessage';

import {
    hideSingleCollapse,
    showSingleCollapse,
    toggleSingleCollapse,
} from './ui';

function submitFormAjax() {
    const formObject = $('#csvToHtmlForm');
    const formData = formObject.serialize();
    const content = $('#fsModalTable');
    const ajaxStatus = $('#ajaxStatus');

    const retryButtonHtml = '<button class="btn btn-info" id="instructorFeedbackResultsRetryButton"> retry</button>';
    $('#instructorFeedbackResultsRetryButton').on('click', submitFormAjax);

    $.ajax({
        type: 'POST',
        url: `/page/instructorFeedbackResultsPage?${formData}`,
        beforeSend() {
            content.html('<img src="/images/ajax-loader.gif">');
        },
        error() {
            ajaxStatus.html('Failed to load results table. Please try again.');
            content.html(retryButtonHtml);
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    ajaxStatus.html(data.errorMessage);
                    content.html(retryButtonHtml);
                } else {
                    const table = data.sessionResultsHtmlTableAsString;
                    content.html(`<small>${table}</small>`);
                    ajaxStatus.html(data.ajaxStatus);
                    selectElementContents(content.get(0));
                }
                setStatusMessage(data.statusForAjax);
            }, 500);
        },
    });
}

// Show/hide stats
function showHideStats() {
    if ($('#show-stats-checkbox').is(':checked')) {
        $('.resultStatistics').show();
    } else {
        $('.resultStatistics').hide();
    }
}

// Toggles Rubric Questions excluding self
function toggleExcludingSelfResultsForRubricStatistics(checkbox) {
    if ($(checkbox).prop('checked')) {
        $(checkbox).closest('.rubricStatistics')
                .find('.table-body-including-self')
                .addClass('hidden');
        $(checkbox).closest('.rubricStatistics')
                .find('.table-body-excluding-self')
                .removeClass('hidden');
    } else {
        $(checkbox).closest('.rubricStatistics')
                .find('.table-body-including-self')
                .removeClass('hidden');
        $(checkbox).closest('.rubricStatistics')
                .find('.table-body-excluding-self')
                .addClass('hidden');
    }
}

/**
 * @return {DOM} the element that needs to be clicked to trigger AJAX-loading of data to the panel,
 *         identified by the presence of ajax_auto or ajax-response-auto class(not both) attached to the
 *         element.
 */
function getElementToClickForAjaxLoading(panel) {
    let $elementWithAjaxClass = $(panel).parent().children('.ajax_auto');
    if ($elementWithAjaxClass.length === 0) {
        $elementWithAjaxClass = $(panel).parent().children('.ajax-response-auto');
    }
    return $elementWithAjaxClass;
}

/**
 * Expands {@code panel}. If the panel data is not loaded, load it by Ajax.
 * @param {int} timeOut - is in milliseconds
 */
function expandPanel(panel, timeOut, isAjaxLoadedPanel) {
    const isPanelAlreadyExpanded = $(panel).hasClass('in');
    if (isPanelAlreadyExpanded) {
        return;
    }

    if (isAjaxLoadedPanel) { // Might need to load the panel data by Ajax.
        const $elementToClickForAjaxLoading = getElementToClickForAjaxLoading(panel);
        const needToLoadThePanelDataByClickingOnElement = $elementToClickForAjaxLoading.length !== 0;
        if (needToLoadThePanelDataByClickingOnElement) {
            // When clicked, the panel data is loaded, and ajax_auto or ajax-response-auto class will
            // be removed from the element.
            $elementToClickForAjaxLoading.click();

            // Panel is already expanded as a result of clicking.
            return;
        }
    }

    // Expands this panel.
    setTimeout(showSingleCollapse, timeOut, panel);
}

/**
 * Expands all panels. Loads panel data if missing.
 */
function expandPanels(panels, areAjaxLoadedPanels) {
    const BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS = 50;

    for (let idx = 0; idx < panels.length; idx += 1) {
        expandPanel(panels[idx], idx * BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS, areAjaxLoadedPanels);
    }
}

function collapsePanels(panels) {
    const BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS = 100;

    for (let idx = 0; idx < panels.length; idx += 1) {
        const isPanelAlreadyCollapsed = !$(panels[idx]).hasClass('in');
        if (isPanelAlreadyCollapsed) {
            continue;
        }
        // Collapses this panel.
        setTimeout(hideSingleCollapse, idx * BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS, panels[idx]);
    }
}

function replaceButtonHtmlAndTooltipText(button, from, to) {
    // Replaces html text of the {@code button}.
    let htmlString = $(button).html();
    htmlString = htmlString.replace(from, to);
    $(button).html(htmlString);

    // Replaces tooltip text of the {@code button}.
    const tooltipString = $(button).attr('data-original-title').replace(from, to);
    $(button).attr('title', tooltipString).tooltip('fixTitle').tooltip('show');
}

/**
 * Expands or collapses all panels when collapse/expand panels button is clicked.
 * @param {DOM} expandCollapseButton - The button that was clicked to invoke {@code #expandOrCollapsePanels}.
 * @param {DOM} panels - The panels to be expanded/collapsed. Not defined if {@code expandCollapseButton}
 *         is collapse panels button.
 */
function expandOrCollapsePanels(expandCollapseButton, panels) {
    const STRING_EXPAND = 'Expand';
    const STRING_COLLAPSE = 'Collapse';
    // {@code panels} is not defined when {@code expandCollapseButton} is collapse panels button. We
    // need to define corresponding {@code targetPanels}.
    const targetPanels = panels || $('div.panel-collapse');

    const isButtonInExpandMode = $(expandCollapseButton).html().trim().startsWith(STRING_EXPAND);
    if (isButtonInExpandMode) {
        // The expand/collapse button on AJAX-loaded panels has id collapse-panels-button.
        const areAjaxLoadedPanels = $(expandCollapseButton).is($('#collapse-panels-button'));
        expandPanels(targetPanels, areAjaxLoadedPanels);
        replaceButtonHtmlAndTooltipText(expandCollapseButton, STRING_EXPAND, STRING_COLLAPSE);
    } else {
        collapsePanels(targetPanels);
        replaceButtonHtmlAndTooltipText(expandCollapseButton, STRING_COLLAPSE, STRING_EXPAND);
    }
}

function bindCollapseEvents(panels, nPanels) {
    let numPanels = nPanels;
    for (let i = 0; i < panels.length; i += 1) {
        const heading = $(panels[i]).children('.panel-heading');
        const bodyCollapse = $(panels[i]).children('.panel-collapse');
        if (heading.length !== 0 && bodyCollapse.length !== 0) {
            numPanels += 1;
            // Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            if ($(heading[0]).attr('class') === 'panel-heading') {
                $(heading[0]).click(toggleSingleCollapse);
            }

            let sectionIndex = '';

            /*
             * There is one call of bindCollapseEvents() for outer section panels and one call per request
             * for every section's content.
             * For outer section panels we add prefix in format "-section-<sectionIndex>".
             * For panels inside section we add prefix in format "-<sectionIndex>-". This is done in order
             * to have fixed IDs for panels regardless of asynchronous execution of requests.
            */
            let isSectionPanel = true;
            let sectionBody = $(heading).next().find('[id^="sectionBody-"]');

            if (sectionBody.length === 0) {
                sectionBody = $(heading).parents('[id^="sectionBody-"]');
                isSectionPanel = false;
            }

            if (sectionBody.length !== 0) {
                sectionIndex = `${sectionBody.attr('id').match(/sectionBody-(\d+)/)[1]}-`;
                if (isSectionPanel) {
                    sectionIndex = `section-${sectionIndex}`;
                }
            }

            $(heading[0]).attr('data-target', `#panelBodyCollapse-${sectionIndex}${numPanels}`);
            $(heading[0]).attr('id', `panelHeading-${sectionIndex}${numPanels}`);
            $(heading[0]).css('cursor', 'pointer');
            $(bodyCollapse[0]).attr('id', `panelBodyCollapse-${sectionIndex}${numPanels}`);
        }
    }
    return numPanels;
}

/**
 * For ajax error handling.
 * Given an element in the panel heading, replaces the HTML content of the element with an error message prompting
 * the user to retry.
 */
function displayAjaxRetryMessageForPanelHeading($element) {
    const ajaxErrorStart = '<div class="ajax-error">';
    const warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
    const errorMsgCenter = '[ Failed to load. Click here to retry. ]';
    const errorMsg = `<strong style="margin-left: 1em; margin-right: 1em;">${errorMsgCenter}</strong>`;
    const chevronDown = '<span class="glyphicon glyphicon-chevron-down"></span>';
    const ajaxErrorEnd = '</div>';
    $element.html(ajaxErrorStart + warningSign + errorMsg + chevronDown + ajaxErrorEnd);
}

function isEmptySection(content) {
    const panelsInSection = content.find('div.panel');

    return panelsInSection.length === 0;
}

function removeSection(id) {
    const $heading = $(`[id^=panelHeading-section-${id}]`);

    $heading.parent().remove();
}

function getAppendedResponseRateData(data) {
    const appendedResponseStatus = $(data).find('#responseStatus').html();
    $(data).remove();
    return appendedResponseStatus;
}

function toggleNoResponsePanel(e) {
    const $targetElement = $(e.target);
    if ($targetElement.is('a') || $targetElement.is('input')) {
        return;
    }
    const $panel = $(this);
    const $remindButton = $panel.find('.remind-no-response');
    if ($panel.data('state') === 'up') {
        $remindButton.show();
        showSingleCollapse($(e.currentTarget).data('target'));
        $panel.data('state', 'down');
    } else {
        $remindButton.hide();
        hideSingleCollapse($(e.currentTarget).data('target'));
        $panel.data('state', 'up');
    }
}

function prepareInstructorFeedbackResultsPage() {
    const participantPanelType = 'div.panel.panel-primary,div.panel.panel-default';

    $('a[id^="collapse-panels-button-section-"]').on('click', (e) => {
        const isGroupByTeam = $('#frgroupbyteam').prop('checked');
        const childPanelType = isGroupByTeam ? 'div.panel.panel-warning' : participantPanelType;
        const panels = $(e.currentTarget).closest('.panel-success')
                .children('.panel-collapse')
                .find(childPanelType)
                .children('.panel-collapse');
        expandOrCollapsePanels(e.currentTarget, panels);
    });

    $('.panel.panel-success').on('click', 'a[id^="collapse-panels-button-team-"]', (e) => {
        const panels = $(e.currentTarget).closest('.panel-warning')
                .children('.panel-collapse')
                .find(participantPanelType)
                .children('.panel-collapse');
        expandOrCollapsePanels(e.currentTarget, panels);
    });

    if ($('.panel-success').length >= 1 || $('.panel-info').length >= 1 || $('.panel-default').length >= 1) {
        $('#collapse-panels-button').show();
    } else {
        $('#collapse-panels-button').hide();
    }

    // Show/Hide statistics
    showHideStats();
    $('#show-stats-checkbox').change(showHideStats);

    const panels = $('div.panel');
    bindCollapseEvents(panels, 0);

    bindPublishButtons();
    bindUnpublishButtons();

    $('#button-print').on('click', () => {
        $('#mainContent').printThis({
            importCSS: true,
            importStyle: true,
            loadCSS: '/stylesheets/printview.css',
        });
    });

    const responseRateRequest = function (e) {
        const panelHeading = $(this);
        const displayIcon = $(this).children('.display-icon');
        const formObject = $(this).children('form');
        const panelCollapse = $(this).parent().children('.panel-collapse');
        const formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type: 'POST',
            url: `${$(formObject[0]).attr('action')}?${formData}`,
            beforeSend() {
                displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
                // submitButton.html('<img src="/images/ajax-loader.gif">');
            },
            error() {
                displayAjaxRetryMessageForPanelHeading(displayIcon);
            },
            success(data) {
                const remindButtonContent = $(data).find('.remind-no-response')[0];
                $(panelCollapse[0]).html(getAppendedResponseRateData(data));
                const $panelHeading = $(panelHeading);
                $panelHeading.removeClass('ajax-response-submit');
                $panelHeading.removeClass('ajax-response-auto');
                $panelHeading.off('click');
                displayIcon.html(remindButtonContent);
                displayIcon.append('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');
                $panelHeading.data('state', 'up');
                $panelHeading.click(toggleNoResponsePanel);
                $panelHeading.trigger('click');
                prepareRemindModal();
            },
        });
    };

    // ajax-response-submit requires the user to click on it to load the noResponsePanel,
    // ajax-response-auto automatically loads the noResponsePanel when the page is loaded
    const $responseRatePanel = $('.ajax-response-submit,.ajax-response-auto');
    $responseRatePanel.click(responseRateRequest);

    $('#collapse-panels-button').on('click', (e) => {
        expandOrCollapsePanels(e.currentTarget);
    });

    $('#btn-select-element-contents').on('click', () => {
        selectElementContents($('#fsModalTable').get(0));
    });

    $('#btn-display-table').on('click', () => {
        submitFormAjax();
    });
}

const seeMoreRequest = function (e) {
    const $panelHeading = $(this);
    if ($('#show-stats-checkbox').is(':checked')) {
        $panelHeading.find('[id^="showStats-"]').val('on');
    } else {
        $panelHeading.find('[id^="showStats-"]').val('off');
    }

    const displayIcon = $panelHeading.find('.display-icon');
    const formObject = $panelHeading.children('form');
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const panelBody = $(panelCollapse[0]).children('.panel-body');
    const formData = formObject.serialize();
    e.preventDefault();
    $.ajax({
        type: 'POST',
        cache: false,
        url: `${$(formObject[0]).attr('action')}?${formData}`,
        beforeSend() {
            displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
        },
        error() {
            displayAjaxRetryMessageForPanelHeading(displayIcon);
        },
        success(data) {
            const $sectionBody = $(panelBody[0]);

            if (typeof data === 'undefined') {
                $sectionBody.html('The results is too large to be viewed. '
                    + 'Please choose to view the results by questions or download the results.');
            } else {
                const $appendedSection = $(data).find('#sectionBody-0');

                if (isEmptySection($appendedSection)) {
                    $sectionBody.html('There are no responses for this section yet '
                        + 'or you do not have access to the responses collected so far.');
                }

                $(data).remove();
                if (typeof $appendedSection === 'undefined') {
                    $sectionBody.html('There are no responses for this section yet '
                        + 'or you do not have access to the responses collected so far.');
                } else {
                    $sectionBody.html($appendedSection.html());
                }
            }

            // bind the show picture onclick events
            bindStudentPhotoLink($sectionBody.find('.profile-pic-icon-click > .student-profile-pic-view-link'));
            // bind the show picture onhover events
            bindStudentPhotoHoverLink($sectionBody.find('.profile-pic-icon-hover'));

            $panelHeading.removeClass('ajax_auto');
            $panelHeading.off('click');
            displayIcon.html('<span class="glyphicon glyphicon-chevron-down"></span>');
            const childrenPanels = $sectionBody.find('div.panel');
            bindCollapseEvents(childrenPanels, 0);

            $panelHeading.click(toggleSingleCollapse);
            $panelHeading.trigger('click');
            showHideStats();
        },
    });
};

export {
    bindCollapseEvents,
    displayAjaxRetryMessageForPanelHeading,
    isEmptySection,
    prepareInstructorFeedbackResultsPage,
    removeSection,
    showHideStats,
    seeMoreRequest,
    toggleExcludingSelfResultsForRubricStatistics,
};
