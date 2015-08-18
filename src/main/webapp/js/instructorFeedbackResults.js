/*
    InstructorFeedbackResults.js
*/

/**
 * Selects the whole table
 * @param el
 */
function selectElementContents(el) {
    var body = document.body, range, sel;
    if (document.createRange && window.getSelection) {
        range = document.createRange();
        sel = window.getSelection();
        sel.removeAllRanges();
        try {
            range.selectNodeContents(el);
            sel.addRange(range);
        } catch (e) {
            range.selectNode(el);
            sel.addRange(range);
        }
    } else if (body.createTextRange) {
        range = body.createTextRange();
        range.moveToElementText(el);
        range.select();
    }
}

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
                if (!data.isError) {
                    var table = data.sessionResultsHtmlTableAsString;
                    content.html('<small>' + table + '</small>');
                    ajaxStatus.html(data.ajaxStatus);
                } else {
                    ajaxStatus.html(data.errorMessage);
                    content.html('<button class="btn btn-info" onclick="submitFormAjax()"> retry</button>');
                }
                $('#statusMessage').html(data.statusForAjax);
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
function filterResults(searchText) {
    // Reduce white spaces to only 1 white space
    searchText = (searchText.split('\\s+')).join(' ');

    // all panel text will be sorted in post-order 
    var allPanelText = $('#mainContent').find('div.panel-heading-text');

    // a stack that stores parent panels that are pending on 
    // the search result from the child panels to decide show/hide
    var showStack = new Array();

    // a stack that stores the parent panels that have been traversed so far
    var parentStack = new Array();

    for(var p = 0; p < allPanelText.length; p++) {
        var panelText = allPanelText[p];
        var panel = $(panelText).closest('div.panel');

        // find the number of child panels in the current panel
        var childrenSize = $(panel).find('div.panel-heading-text').not(panelText).length;
        var hasChild = childrenSize > 0;

        var panelParent = $(panel).parent().closest('div.panel');

        // reset traversed parent panel stack & pending parent panel stack 
        // to the parent of current panel
        while (parentStack.length > 0 && !parentStack[parentStack.length-1].is(panelParent)) {
            parentStack.pop();
            if (showStack.length > 0) {
                var s = showStack.pop();
                $(s).hide();
            }
        }

        // current panel text matches with the search text
        if ($(panelText).text().toLowerCase().indexOf(searchText) != -1) {
            // pop and show all parent panels from the showStack
            while (showStack.length > 0) {
                var s = showStack.pop();
                $(s).show();
            }

            // show current panel
            $(panel).show();

            // show all child panels of current panel
            if (hasChild) {
                for(var c = p + 1; c <= p + childrenSize; c++) {
                    var childPanel = $(allPanelText[c]).closest('div.panel');
                    $(childPanel).show();
                }

                // increment counter to skip child panels that have been shown
                p += childrenSize;
            }
        } else if (!hasChild) {
            // current panel text does not match with search text & current panel has no child panels
            $(panel).hide();
        } else {
            // current panel text does not match with search text & current panel has child panels
            // add current panel to pending parent panel stack
            showStack.push(panel);
        }

        if (hasChild) {
            // push current panel when it has child panels
            parentStack.push(panel);
        }
    }

    // hide panels that are still remain on the showStack
    while (showStack.length > 0) {
        var s = showStack.pop();
        $(s).hide();
    }
}

function updateResultsFilter() {
    filterResults($('#results-search-box').val());
}

function toggleCollapse(e, panels) {
    var expand = 'Expand';
    var collapse = 'Collapse';
    
    if ($(e).html().trim().startsWith(expand)) {
        panels = panels || $('div.panel-collapse');
        isExpandingAll = true;
        var i = 0;
        for (var idx = 0; idx < panels.length; idx++) {
            if ($(panels[idx]).attr('class').indexOf('in') == -1) {

                // The timeout value '50' is being used in InstructorFeedbackResultsPage.verifyAllResultsPanelBodyVisibility()
                // and InstructorFeedbackResultsPageUiTest.testPanelsCollapseExpand()
                // Therefore, when changing this timeout value, please update the waiting times accordingly
                setTimeout(showSingleCollapse, 50 * i, panels[idx]);
                i++;
            }
        }
        var htmlString = $(e).html();
        htmlString = htmlString.replace(expand, collapse);
        $(e).html(htmlString);
        var tooltipString = $(e).attr('data-original-title').replace(expand, collapse);
        $(e).attr('title', tooltipString).tooltip('fixTitle').tooltip('show');
    } else {
        panels = panels || $('div.panel-collapse');
        isCollapsingAll = true;
        var i = 0;
        for (var idx = 0; idx < panels.length; idx++) {
            if ($(panels[idx]).attr('class').indexOf('in') != -1) {
                setTimeout(hideSingleCollapse, 100 * i, panels[idx]);
                i++;
            }
        }
        var htmlString = $(e).html();
        htmlString = htmlString.replace(collapse, expand);
        $(e).html(htmlString);
        var tooltipString = $(e).attr('data-original-title').replace(collapse, expand);
        $(e).attr('title', tooltipString).tooltip('fixTitle').tooltip('show');
    }
}

function showSingleCollapse(e) {
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-down');
    $(glyphIcon[0]).addClass('glyphicon-chevron-up');
    $(e).collapse('show');
    $(heading).find('a.btn').show();
}

function hideSingleCollapse(e) {
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-up');
    $(glyphIcon[0]).addClass('glyphicon-chevron-down');
    $(e).collapse('hide');
    $(heading).find('a.btn').hide();
}

function toggleSingleCollapse(e) {
    if (!$(e.target).is('a') && !$(e.target).is('input')) {
        var glyphIcon = $(this).find('.glyphicon');
        var className = $(glyphIcon[0]).attr('class');
        if (className.indexOf('glyphicon-chevron-up') != -1) {
            hideSingleCollapse($(e.currentTarget).attr('data-target'));
        } else {
            showSingleCollapse($(e.currentTarget).attr('data-target'));
        }
    }
}

function getNextId(e) {
    var id = $(e).attr('id');
    var nextId = '#panelBodyCollapse-' + (parseInt(id.split('-')[1]) + 1);
    return nextId;
}

function bindCollapseEvents(panels, numPanels) {
    for (var i = 0 ; i < panels.length ; i++) {
        var heading = $(panels[i]).children('.panel-heading');
        var bodyCollapse = $(panels[i]).children('.panel-collapse');
        if (heading.length != 0 && bodyCollapse.length != 0) {
            numPanels++;
            // $(heading[0]).attr('data-toggle', 'collapse');
            // Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            if ($(heading[0]).attr('class') == 'panel-heading') {
                $(heading[0]).click(toggleSingleCollapse);
            }
            $(heading[0]).attr('data-target', '#panelBodyCollapse-' + numPanels);
            $(heading[0]).attr('id', 'panelHeading-' + numPanels);
            $(heading[0]).css('cursor', 'pointer');
            $(bodyCollapse[0]).attr('id', 'panelBodyCollapse-' + numPanels);
        }
    }
    return numPanels;
}

window.onload = function() {
    var participantPanelType = 'div.panel.panel-primary,div.panel.panel-default';

    $('a[id^="collapse-panels-button-section-"]').on('click', function() {
        var isGroupByTeam = document.getElementById('frgroupbyteam').checked;
        var childPanelType;
        if (isGroupByTeam) {
            childPanelType = 'div.panel.panel-warning';
        } else {
            childPanelType = participantPanelType;
        }
        var panels = $(this).closest('.panel-success').children('.panel-collapse').find(childPanelType).children('.panel-collapse');
        toggleCollapse(this, panels);
    });

    $('a[id^="collapse-panels-button-team-"]').on('click', function() {
        var panels = $(this).closest('.panel-warning').children('.panel-collapse').find(participantPanelType).children('.panel-collapse');
        toggleCollapse(this, panels);
    });
};

// Set on ready events
$(document).ready(function() {
    $('#results-search-box').keyup(function(e) {
        updateResultsFilter();
    });

    // prevent submitting form when enter is pressed.
    $('#results-search-box').keypress(function(e) {
        if (e.which == 13) {
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
    $('#fsResultsTableWindow').on('shown.bs.modal', function (e) {
        selectElementContents(document.getElementById('fsModalTable'));
    });

    var panels = $('div.panel');
    bindCollapseEvents(panels, 0);
});
