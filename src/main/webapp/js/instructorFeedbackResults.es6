/* global selectElementContents:false,
          bindPublishButtons:false,
          bindUnpublishButtons:false,
          setStatusMessage:false,
          showSingleCollapse:false,
          hideSingleCollapse:false,
          toggleSingleCollapse:false
*/
/* exported     submitFormAjax,
                updateStatsCheckBox,
                getNextId,
                displayAjaxRetryMessageForPanelHeading,
                isEmptySection,
                removeSection
*/

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

// Filter functionality
// Filtering is done by searching the heading text in all panels (section, team name, student name)
//
// When the heading text of a panel is found to match the search text,
// all nested panels in the panel will be shown and parent panels of the panel will be shown as well
//
// When at least one of the nested panels in a panel is found to contain the search text in its title,
// the panel will be shown
function filterResults(rawSearchText) {
    // Reduce white spaces to only 1 white space
    const searchText = rawSearchText.split('\\s+').join(' ').toLowerCase();

    // all panel text will be sorted in post-order
    const allPanelText = $('#mainContent').find('div.panel-heading-text');

    // a stack that stores parent panels that are pending on
    // the search result from the child panels to decide show/hide
    const showStack = [];

    // a stack that stores the parent panels that have been traversed so far
    const parentStack = [];

    for (let p = 0; p < allPanelText.length; p += 1) {
        const panelText = allPanelText[p];
        const panel = $(panelText).closest('div.panel');

        // find the number of child panels in the current panel
        const childrenSize = $(panel).find('div.panel-heading-text').not(panelText).length;
        const hasChild = childrenSize > 0;

        const panelParent = $(panel).parent().closest('div.panel');

        // reset traversed parent panel stack & pending parent panel stack
        // to the parent of current panel
        while (parentStack.length > 0 && !parentStack[parentStack.length - 1].is(panelParent)) {
            parentStack.pop();
            if (showStack.length > 0) {
                $(showStack.pop()).hide();
            }
        }

        // current panel text matches with the search text
        if ($(panelText).text().toLowerCase().indexOf(searchText) !== -1) {
            // pop and show all parent panels from the showStack
            while (showStack.length > 0) {
                $(showStack.pop()).show();
            }

            // show current panel
            $(panel).show();

            // show all child panels of current panel
            if (hasChild) {
                for (let c = p + 1; c <= p + childrenSize; c += 1) {
                    const childPanel = $(allPanelText[c]).closest('div.panel');
                    $(childPanel).show();
                }

                // increment counter to skip child panels that have been shown
                p += childrenSize;
            }
        } else if (hasChild) {
            // current panel text does not match with search text & current panel has child panels
            // add current panel to pending parent panel stack
            showStack.push(panel);
        } else {
            // current panel text does not match with search text & current panel has no child panels
            $(panel).hide();
        }

        if (hasChild) {
            // push current panel when it has child panels
            parentStack.push(panel);
        }
    }

    // hide panels that are still remain on the showStack
    while (showStack.length > 0) {
        $(showStack.pop()).hide();
    }
}

function updateResultsFilter() {
    $('input[id=filterTextForDownload]').val($('#results-search-box').val());
    filterResults($('#results-search-box').val());
}

function updateStatsCheckBox() {
    $('input[id=statsShownCheckBox]').val($('#show-stats-checkbox').is(':checked'));
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

    const isButtonInExapandMode = $(expandCollapseButton).html().trim().startsWith(STRING_EXPAND);
    if (isButtonInExapandMode) {
        // The expand/collapse button on AJAX-loaded panels has id collapse-panels-button.
        const areAjaxLoadedPanels = $(expandCollapseButton).is($('#collapse-panels-button'));
        expandPanels(targetPanels, areAjaxLoadedPanels);
        replaceButtonHtmlAndTooltipText(expandCollapseButton, STRING_EXPAND, STRING_COLLAPSE);
    } else {
        collapsePanels(targetPanels);
        replaceButtonHtmlAndTooltipText(expandCollapseButton, STRING_COLLAPSE, STRING_EXPAND);
    }
}

function getNextId(e) {
    const id = $(e).attr('id');
    const nextId = `#panelBodyCollapse-${parseInt(id.split('-')[1], 10) + 1}`;
    return nextId;
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

function prepareInstructorFeedbackResultsPage() {
    const participantPanelType = 'div.panel.panel-primary,div.panel.panel-default';

    $('a[id^="collapse-panels-button-section-"]').on('click', (e) => {
        const isGroupByTeam = document.getElementById('frgroupbyteam').checked;
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

    $('#results-search-box').keyup(updateResultsFilter);

    // prevent submitting form when enter is pressed.
    $('#results-search-box').keypress((e) => {
        if (e.which === 13) {
            e.preventDefault();
        }
    });

    if ($('.panel-success').length >= 1 || $('.panel-info').length >= 1 || $('.panel-default').length >= 1) {
        $('#collapse-panels-button').show();
    } else {
        $('#collapse-panels-button').hide();
    }

    // Show/Hide statistics
    showHideStats();
    $('#show-stats-checkbox').change(showHideStats);

    // auto select the html table when modal is shown
    $('#fsResultsTableWindow').on('shown.bs.modal', () => {
        selectElementContents(document.getElementById('fsModalTable'));
    });

    const panels = $('div.panel');
    bindCollapseEvents(panels, 0);

    bindPublishButtons();
    bindUnpublishButtons();

    $('#button-print').on('click', () => {
        // Fix to hide the filter placeholder when it is empty.
        if ($('#results-search-box').val()) {
            $('#filter-box-parent-div').removeClass('hide-for-print');
        } else {
            $('#filter-box-parent-div').addClass('hide-for-print');
        }

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
                $(panelCollapse[0]).html(getAppendedResponseRateData(data));
                $(panelHeading).removeClass('ajax-response-submit');
                $(panelHeading).removeClass('ajax-response-auto');
                $(panelHeading).off('click');
                displayIcon.html('<span class="glyphicon glyphicon-chevron-down pull-right"></span>');
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
            },
        });
    };

    // ajax-response-submit requires the user to click on it to load the noResponsePanel,
    // ajax-response-auto automatically loads the noResponsePanel when the page is loaded
    const $responseRatePanel = $('.ajax-response-submit,.ajax-response-auto');
    $responseRatePanel.click(responseRateRequest);
}
