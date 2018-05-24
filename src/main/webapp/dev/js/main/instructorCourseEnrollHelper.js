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
 * Changes cell value null to ''. Filters empty rows in the process.
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
    return spreadsheetData.map(row => (row.map(cell => (cell === null ? '' : cell))).join('|'))
            .filter(row => row !== '||||') // remove empty rows
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
    const data = [];
    spreadsheetDataRows.map(userDataRows => data.push(userDataRows.split('|')));
    return data;
}

export {
    getUpdatedHeaderString,
    getUserDataRows,
    getUpdatedData,
};
