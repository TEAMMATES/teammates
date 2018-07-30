import {
    showModalConfirmation,
} from './bootboxWrapper';

import {
    BootstrapContextualColors,
} from './const';

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
 * Expands/Collapses the panel depending on the current state of the panel.
 */
function toggleStudentsPanel(panelHeading, panelCollapse) {
    const toggleChevron = panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');
    if (panelCollapse.hasClass('in')) { // panel is shown
        panelCollapse.collapse('hide');
        toggleChevron.addClass('glyphicon-chevron-down').removeClass('glyphicon-chevron-up');
        return;
    }
    panelCollapse.collapse('show');
    toggleChevron.addClass('glyphicon-chevron-up').removeClass('glyphicon-chevron-down');
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

/**
 * Shows modal box when user clicks on the 'paste' option in the Handsontable context menu.
 */
function showPasteModalBox() {
    const messageText = 'Pasting data through the context menu is not supported due to browser restrictions. '
            + 'Please use <kbd>Ctrl + V</kbd> or <kbd>⌘ + V</kbd> to paste your data instead.';

    const okCallback = () => {};
    showModalConfirmation('Pasting data through the context menu', messageText,
            okCallback, null, null, null, BootstrapContextualColors.WARNING);
}

export {
    getUpdatedHeaderString,
    getUserDataRows,
    spreadsheetDataRowsToHandsontableData,
    ajaxDataToHandsontableData,
    getSpreadsheetLength,
    toggleStudentsPanel,
    showPasteModalBox,
};
