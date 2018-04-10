/**
 * Sets the chevron to point upwards.
 */
function setChevronToUp(chevronContainer) {
    chevronContainer.removeClass('glyphicon-chevron-down');
    chevronContainer.addClass('glyphicon-chevron-up');
}

/**
 * Sets the chevron to point downwards.
 */
function setChevronToDown(chevronContainer) {
    chevronContainer.removeClass('glyphicon-chevron-up');
    chevronContainer.addClass('glyphicon-chevron-down');
}

/**
 * Sets the chevron of a panel from up to down or from down to up depending on its current state.
 * clickedElement must be at least the parent of the chevron.
 */
function toggleChevron(clickedElement) {
    const $clickedElement = $(clickedElement);
    const isChevronDown = $clickedElement.find('.glyphicon-chevron-down').length > 0;
    const $chevronContainer = $clickedElement.find('.glyphicon-chevron-up, .glyphicon-chevron-down');

    // clearQueue to clear the animation queue to prevent animation build up
    $chevronContainer.clearQueue();

    if (isChevronDown) {
        setChevronToUp($chevronContainer);
    } else {
        setChevronToDown($chevronContainer);
    }
}

/**
 * Shows panel's content and changes chevron to point up.
 */
function showSingleCollapse(e) {
    const heading = $(e).parent().children('.panel-heading');
    const glyphIcon = $(heading[0]).find('.glyphicon');
    setChevronToUp($(glyphIcon[0]));
    $(e).collapse('show');
    $(heading).find('a.btn').show();
}

/**
 * Hides panel's content and changes chevron to point down.
 */
function hideSingleCollapse(e) {
    const heading = $(e).parent().children('.panel-heading');
    const glyphIcon = $(heading[0]).find('.glyphicon');
    setChevronToDown($(glyphIcon[0]));
    $(e).collapse('hide');
    $(heading).find('a.btn').hide();
}

/**
 * Changes the state of the panel (collapsed/expanded).
 */
function toggleSingleCollapse(e) {
    if ($(e.target).is('a') || $(e.target).is('input')) {
        return;
    }
    const glyphIcon = $(this).find('.glyphicon');
    const className = $(glyphIcon[0]).attr('class');
    if (className.indexOf('glyphicon-chevron-up') === -1) {
        showSingleCollapse($(e.currentTarget).attr('data-target'));
    } else {
        hideSingleCollapse($(e.currentTarget).attr('data-target'));
    }
}

function addLoadingIndicator(button, loadingText) {
    button.html(loadingText);
    button.prop('disabled', true);
    button.append('<img src="/images/ajax-loader.gif">');
}

function removeLoadingIndicator(button, displayText) {
    button.empty();
    button.html(displayText);
    button.prop('disabled', false);
}

/**
 * Highlights all words of searchKey (case insensitive), in a particular section
 * Format of the string  higlight plugin uses - ( ['string1','string2',...] )
 * @param searchKeyId - Id of searchKey input field
 * @param sectionToHighlight - sections to higlight separated by ',' (comma)
 *                             Example- '.panel-body, #panel-data, .sub-container'
 */
function highlightSearchResult(searchKeyId, sectionToHighlight) {
    const searchKey = $(searchKeyId).val().trim();
    // split search key string on symbols and spaces and add to searchKeyList
    let searchKeyList = [];
    if (searchKey.charAt(0) === '"' && searchKey.charAt(searchKey.length - 1) === '"') {
        searchKeyList.push(searchKey.replace(/"/g, '').trim());
    } else {
        $.each(searchKey.split(/[ "'.-]/), function () {
            searchKeyList.push($.trim(this));
        });
    }
    // remove empty elements from searchKeyList
    searchKeyList = searchKeyList.filter(n => n !== '');
    $(sectionToHighlight).highlight(searchKeyList);
}

// Toggle the visibility of additional question information for the specified question.
function toggleAdditionalQuestionInfo(identifier) {
    const $questionButton = $(`#questionAdditionalInfoButton-${identifier}`);

    if ($questionButton.text() === $questionButton.attr('data-more')) {
        $questionButton.text($questionButton.attr('data-less'));
    } else {
        $questionButton.text($questionButton.attr('data-more'));
    }

    $(`#questionAdditionalInfo-${identifier}`).toggle();
}

/**
 * Disallow non-numeric entries
 * [Source: http://stackoverflow.com/questions/995183/how-to-allow-only-numeric-0-9-in-html-inputbox-using-jquery]
 */
function disallowNonNumericEntries(element, decimalPointAllowed, negativeAllowed) {
    element.on('keydown', (event) => {
        const key = event.which;
        // Allow: backspace, delete, tab, escape, enter
        if ([46, 8, 9, 27, 13].indexOf(key) !== -1
                // Allow: Ctrl+A
                || (key === 65 && event.ctrlKey)
                // Allow: home, end, left, right
                || (key >= 35 && key <= 39)
                // Allow dot if decimal point is allowed
                || (decimalPointAllowed && key === 190)
                // Allow hyphen if negative is allowed
                // Code differs by browser (FF/Opera:109, IE/Chrome:189)
                // see http://www.javascripter.net/faq/keycodes.htm
                || (negativeAllowed && (key === 189 || key === 109))) {
            // let it happen, don't do anything
            return;
        }
        // Ensure that it is a number and stop the keypress
        if (event.shiftKey || ((key < 48 || key > 57) && (key < 96 || key > 105))) {
            event.preventDefault();
            event.stopPropagation();
        }
    });
}

export {
    addLoadingIndicator,
    disallowNonNumericEntries,
    hideSingleCollapse,
    highlightSearchResult,
    removeLoadingIndicator,
    showSingleCollapse,
    toggleAdditionalQuestionInfo,
    toggleChevron,
    toggleSingleCollapse,
};
