import {
    showModalConfirmation,
} from '../common/bootboxWrapper';

import {
    BootstrapContextualColors,
} from '../common/const';

/**
 * Retrieves updated column header order and generates a header string.
 *
 * Example: Changes this array ['Section', 'Team', 'Name', 'Email', 'Comments']
 * into a string = "Section|Team|Name|Email|Comments\n"
 *
 * @param handsontableColHeader
 * @returns {string} updated header string
 */
function getUpdatedHeaderString(handsontableColHeader) {
    const colHeaders = handsontableColHeader.join('|');
    return colHeaders.concat('\n');
}

/**
 * Retrieves user data rows rows in the spreadsheet interface and transforms it into a string.
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
 * @param spreadsheetData
 * @returns {string} user data rows
 */
function getUserDataRows(spreadsheetData) {
    // needs to check for '' as an initial empty row with null values will be converted to e.g. "||||" after .map
    return spreadsheetData.filter(row => (!row.every(cell => cell === null || cell === '')))
            .map(row => row.join('|'))
            .join('\n');
}

/**
 * Pushes data from spreadsheetDataRows into an array.
 * Facilitates the function loadData for the Handsontable instance.
 *
 * @param spreadsheetDataRows
 * @returns {Array} updated data
 */
function getUpdatedData(spreadsheetDataRows) {
    return spreadsheetDataRows.map(row => row.split('|'));
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
    getUpdatedData,
    showPasteModalBox,
};
