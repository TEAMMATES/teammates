'use strict';

$(document).ready(function() {
    var participantPanelType = 'div.panel.panel-primary,div.panel.panel-default';

    $('a[id^="collapse-panels-button-section-"]').on('click', function() {
        var isGroupByTeam = document.getElementById('frgroupbyteam').checked;
        var childPanelType;
        if (isGroupByTeam) {
            childPanelType = 'div.panel.panel-warning';
        } else {
            childPanelType = participantPanelType;
        }
        var panels = $(this).closest('.panel-success')
                            .children('.panel-collapse')
                            .find(childPanelType)
                            .children('.panel-collapse');
        expandOrCollapsePanels(this, panels);
    });

    $('.panel.panel-success').on('click', 'a[id^="collapse-panels-button-team-"]', function() {
        var panels = $(this).closest('.panel-warning')
                            .children('.panel-collapse')
                            .find(participantPanelType)
                            .children('.panel-collapse');
        expandOrCollapsePanels(this, panels);
    });

    $('#results-search-box').keyup(function() {
        updateResultsFilter();
    });

    // prevent submitting form when enter is pressed.
    $('#results-search-box').keypress(function(e) {
        if (e.which === 13) {
            return false;
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
    $('#fsResultsTableWindow').on('shown.bs.modal', function() {
        selectElementContents(document.getElementById('fsModalTable'));
    });

    var panels = $('div.panel');
    bindCollapseEvents(panels, 0);

    bindPublishButtons();
    bindUnpublishButtons();

    $('#button-print').on('click', function() {
        // Fix to hide the filter placeholder when it is empty.
        if ($('#results-search-box').val()) {
            $('#filter-box-parent-div').removeClass('hide-for-print');
        } else {
            $('#filter-box-parent-div').addClass('hide-for-print');
        }

        $('#mainContent').printThis({
            importCSS: true,
            importStyle: true,
            loadCSS: '/stylesheets/printview.css'
        });
    });
});

function submitFormAjax() {
    var formObject = $('#csvToHtmlForm');
    var formData = formObject.serialize();
    var content = $('#fsModalTable');
    var ajaxStatus = $('#ajaxStatus');
    $.ajax({
        type: 'POST',
        url: '/page/instructorFeedbackResultsPage?' + formData,
        beforeSend: function() {
            content.html('<img src="/images/ajax-loader.gif">');
        },
        error: function() {
            ajaxStatus.html('Failed to load results table. Please try again.');
            content.html('<button class="btn btn-info" onclick="submitFormAjax()"> retry</button>');
        },
        success: function(data) {
            setTimeout(function() {
                if (data.isError) {
                    ajaxStatus.html(data.errorMessage);
                    content.html('<button class="btn btn-info" onclick="submitFormAjax()"> retry</button>');
                } else {
                    var table = data.sessionResultsHtmlTableAsString;
                    content.html('<small>' + table + '</small>');
                    ajaxStatus.html(data.ajaxStatus);
                }
                setStatusMessage(data.statusForAjax);
            }, 500);
        }
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
    var searchText = rawSearchText.split('\\s+').join(' ').toLowerCase();

    // all panel text will be sorted in post-order
    var allPanelText = $('#mainContent').find('div.panel-heading-text');

    // a stack that stores parent panels that are pending on
    // the search result from the child panels to decide show/hide
    var showStack = [];

    // a stack that stores the parent panels that have been traversed so far
    var parentStack = [];

    for (var p = 0; p < allPanelText.length; p++) {
        var panelText = allPanelText[p];
        var panel = $(panelText).closest('div.panel');

        // find the number of child panels in the current panel
        var childrenSize = $(panel).find('div.panel-heading-text').not(panelText).length;
        var hasChild = childrenSize > 0;

        var panelParent = $(panel).parent().closest('div.panel');

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
                for (var c = p + 1; c <= p + childrenSize; c++) {
                    var childPanel = $(allPanelText[c]).closest('div.panel');
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
 * Expands or collapses all panels when collapse/expand panels button is clicked.
 * @param {DOM} expandCollapseButton - The button that was clicked to invoke {@code #expandOrCollapsePanels}.
 * @param {DOM} panels - The panels to be expanded/collapsed. Not defined if {@code expandCollapseButton}
 *         is collapse panels button.
 */
function expandOrCollapsePanels(expandCollapseButton, panels) {
    var STRING_EXPAND = 'Expand';
    var STRING_COLLAPSE = 'Collapse';
    // {@code panels} is not defined when {@code expandCollapseButton} is collapse panels button. We
    // need to define corresponding {@code targetPanels}.
    var targetPanels = panels || $('div.panel-collapse');

    var isButtonInExapandMode = $(expandCollapseButton).html().trim().startsWith(STRING_EXPAND);
    if (isButtonInExapandMode) {
        // The expand/collapse button on AJAX-loaded panels has id collapse-panels-button.
        var areAjaxLoadedPanels = $(expandCollapseButton).is($('#collapse-panels-button'));
        expandPanels(targetPanels, areAjaxLoadedPanels);
        replaceButtonHtmlAndTooltipText(expandCollapseButton, STRING_EXPAND, STRING_COLLAPSE);
    } else {
        collapsePanels(targetPanels);
        replaceButtonHtmlAndTooltipText(expandCollapseButton, STRING_COLLAPSE, STRING_EXPAND);
    }
}

/**
 * Expands all panels. Loads panel data if missing.
 */
function expandPanels(panels, areAjaxLoadedPanels) {
    var BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS = 50;

    for (var idx = 0; idx < panels.length; idx++) {
        expandPanel(panels[idx], idx * BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS, areAjaxLoadedPanels);
    }
}

/**
 * Expands {@code panel}. If the panel data is not loaded, load it by Ajax.
 * @param {int} timeOut - is in milliseconds
 */
function expandPanel(panel, timeOut, isAjaxLoadedPanel) {
    var isPanelAlreadyExpanded = $(panel).hasClass('in');
    if (isPanelAlreadyExpanded) {
        return;
    }

    if (isAjaxLoadedPanel) { // Might need to load the panel data by Ajax.
        var $elementToClickForAjaxLoading = getElementToClickForAjaxLoading(panel);
        var needToLoadThePanelDataByClickingOnElement = $elementToClickForAjaxLoading.length !== 0;
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
 * @return {DOM} the element that needs to be clicked to trigger AJAX-loading of data to the panel,
 *         identified by the presence of ajax_auto or ajax-response-auto class(not both) attached to the
 *         element.
 */
function getElementToClickForAjaxLoading(panel) {
    var $elementWithAjaxClass = $(panel).parent().children('.ajax_auto');
    if ($elementWithAjaxClass.length === 0) {
        $elementWithAjaxClass = $(panel).parent().children('.ajax-response-auto');
    }
    return $elementWithAjaxClass;
}

function collapsePanels(panels) {
    var BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS = 100;

    for (var idx = 0; idx < panels.length; idx++) {
        var isPanelAlreadyCollapsed = !$(panels[idx]).hasClass('in');
        if (isPanelAlreadyCollapsed) {
            continue;
        }
        // Collapses this panel.
        setTimeout(hideSingleCollapse, idx * BASE_TIMEOUT_UNIT_IN_MILLI_SECONDS, panels[idx]);
    }
}

function replaceButtonHtmlAndTooltipText(button, from, to) {
    // Replaces html text of the {@code button}.
    var htmlString = $(button).html();
    htmlString = htmlString.replace(from, to);
    $(button).html(htmlString);

    // Replaces tooltip text of the {@code button}.
    var tooltipString = $(button).attr('data-original-title').replace(from, to);
    $(button).attr('title', tooltipString).tooltip('fixTitle').tooltip('show');
}

function getNextId(e) {
    var id = $(e).attr('id');
    var nextId = '#panelBodyCollapse-' + (parseInt(id.split('-')[1]) + 1);
    return nextId;
}

function bindCollapseEvents(panels, nPanels) {
    var numPanels = nPanels;
    for (var i = 0; i < panels.length; i++) {
        var heading = $(panels[i]).children('.panel-heading');
        var bodyCollapse = $(panels[i]).children('.panel-collapse');
        if (heading.length !== 0 && bodyCollapse.length !== 0) {
            numPanels++;
            // $(heading[0]).attr('data-toggle', 'collapse');
            // Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            if ($(heading[0]).attr('class') === 'panel-heading') {
                $(heading[0]).click(toggleSingleCollapse);
            }

            var sectionIndex = '';

            /*
             * There is one call of bindCollapseEvents() for outer section panels and one call per request
             * for every section's content.
             * For outer section panels we add prefix in format "-section-<sectionIndex>".
             * For panels inside section we add prefix in format "-<sectionIndex>-". This is done in order
             * to have fixed IDs for panels regardless of asynchronous execution of requests.
            */
            var isSectionPanel = true;
            var sectionBody = $(heading).next().find('[id^="sectionBody-"]');

            if (sectionBody.length === 0) {
                sectionBody = $(heading).parents('[id^="sectionBody-"]');
                isSectionPanel = false;
            }

            if (sectionBody.length !== 0) {
                sectionIndex = sectionBody.attr('id').match(/sectionBody-(\d+)/)[1] + '-';
                if (isSectionPanel) {
                    sectionIndex = 'section-' + sectionIndex;
                }
            }

            $(heading[0]).attr('data-target', '#panelBodyCollapse-' + sectionIndex + numPanels);
            $(heading[0]).attr('id', 'panelHeading-' + sectionIndex + numPanels);
            $(heading[0]).css('cursor', 'pointer');
            $(bodyCollapse[0]).attr('id', 'panelBodyCollapse-' + sectionIndex + numPanels);
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
    var ajaxErrorStart = '<div class="ajax-error">';
    var warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
    var errorMsg = '[ Failed to load. Click here to retry. ]';
    errorMsg = '<strong style="margin-left: 1em; margin-right: 1em;">' + errorMsg + '</strong>';
    var chevronDown = '<span class="glyphicon glyphicon-chevron-down"></span>';
    var ajaxErrorEnd = '</div>';
    $element.html(ajaxErrorStart + warningSign + errorMsg + chevronDown + ajaxErrorEnd);
}

function isEmptySection(content) {
    var panelsInSection = content.find('div.panel');

    return panelsInSection.length === 0;
}

function removeSection(id) {
    var $heading = $('[id^=panelHeading-section-' + id + ']');

    $heading.parent().remove();
}
