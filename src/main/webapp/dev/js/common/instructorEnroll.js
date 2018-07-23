/**
 * Retrieves updated column header order and generates a header string.
 *
 * Example: Changes this array ['Section', 'Team', 'Name', 'Email', 'Comments']
 * into a string = "Section|Team|Name|Email|Comments\n"
 *
 * @returns {string} updated header string
 */
function getUpdatedHeaderString(handsontableColHeader) {
    const colHeaders = handsontableColHeader.join('|');
    return colHeaders.concat('\n');
}

/**
 * Retrieves user data rows in the spreadsheet interface and transforms it into a string.
 *
 * Null value from cell is changed to empty string after .join(). Filters empty rows in the process.
 *
 * Example:
 * 2 by 5 spreadsheetData (before)
 * ['TestSection1', 'Team1', 'null', 'test1@xample.com', 'test1comments']
 * ['TestSection2', null, 'TestName2', 'test2@example.com', null]
 *
 * 2 by 5 spreadsheetData (after)
 * "TestSection1|Team1||test1@xample.com|test1comments\n
 *  TestSection2||TestName2|test2@example.com|\n"
 *
 * @returns {string} user data rows
 */
function getUserDataRows(spreadsheetData) {
    // needs to check for '' as an initial empty row with null values will be converted to e.g. "||||" after .map
    return spreadsheetData.filter(row => (!row.every(cell => cell === null || cell === '')))
            .map(row => row.join('|'))
            .join('\n');
}

/**
 * Converts spreadsheetDataRows to a suitable format required by Handsontable.
 * Facilitates the function loadData for the Handsontable instance.
 * @returns {Array} updated data
 */
function spreadsheetDataRowsToHandsontableData(spreadsheetDataRows) {
    return spreadsheetDataRows.map(row => row.split('|'));
}

/**
 * Transforms the first uppercase letter of a string into a lowercase letter.
 * @returns {string} string in all lowercase letters
 */
function unCapitalizeFirstLetter(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}

/**
 * Converts returned AJAX data to a suitable format required by Handsontable.
 * @returns {Array} student data
 */
function ajaxDataToHandsontableData(studentsData, handsontableColHeader) {
    const headers = handsontableColHeader.map(unCapitalizeFirstLetter);
    return studentsData.map(student => (headers.map(
            header => student[header])));
}

/**
 * Toggle the chevron image depending on the user's action.
 */
function toggleChevronImage(isExpanded, toggleChevron) {
    if (isExpanded) { // panel is shown
        $(toggleChevron).addClass('glyphicon-chevron-up').removeClass('glyphicon-chevron-down');
    } else {
        $(toggleChevron).addClass('glyphicon-chevron-down').removeClass('glyphicon-chevron-up');
    }
}

/**
 * Expands panel, showing the spreadsheet interface.
 */
function expandStudentsPanel(panelCollapse) {
    panelCollapse.collapse('show');
}

/**
 * Collapses panel, hiding the spreadsheet interface.
 */
function collapseStudentsPanel(panelCollapse) {
    panelCollapse.collapse('hide');
}

/**
 * Expands/Collapses the panel depending on the current state of the panel.
 */
function toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron) {
    let isPanelExpanded = false;
    displayIcon.html('');
    if (panelCollapse.hasClass('in')) { // panel is shown
        collapseStudentsPanel(panelCollapse);
    } else {
        expandStudentsPanel(panelCollapse);
        isPanelExpanded = true;
    }
    toggleChevronImage(isPanelExpanded, toggleChevron);
    return isPanelExpanded;
}

/**
 * Displays a message informing the user that there are no existing students in the course.
 */
function displayNoExistingStudents(displayStatus) {
    displayStatus.html('<div class="ajax-error-message">'
            + '<strong>[ No existing students in course. ]</strong>'
            + '</div>');
}

/**
 * Displays a message informing the user that an error surfaced during the AJAX request.
 * @param displayIcon
 */
function displayErrorExecutingAjax(displayStatus) {
    displayStatus.html('<div class="ajax-error-message">'
            + '<span class="glyphicon glyphicon-warning-sign"></span>'
            + ' <strong>[ Failed to load. Click here to retry. ]</strong>'
            + '</div>');
}

/**
 * Returns the length of the current spreadsheet. Rows with all null values are filtered.
 * @returns {int} length of current spreadsheet
 */
function getSpreadsheetLength(dataHandsontable) {
    return dataHandsontable
            .filter(row => (!row.every(cell => cell === null)))
            .length;
}

export {
    getUpdatedHeaderString,
    getUserDataRows,
    spreadsheetDataRowsToHandsontableData,
    ajaxDataToHandsontableData,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    getSpreadsheetLength,
    toggleStudentsPanel,
};
