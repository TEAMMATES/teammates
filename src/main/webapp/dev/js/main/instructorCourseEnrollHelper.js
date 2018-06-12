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
 * Transforms the first uppercase letter of a string into a lowercase letter.
 * @param string
 * @returns {string} Handsontable column header in all lowercase letters
 */
function unCapitalizeFirstLetter(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
}

/**
 * Prepares students data to be filled in the spreadsheet interface. These data is stored in an array.
 * Facilitates the function loadData for the Handsontable instance.
 * @param studentsData
 * @param handsontableColHeader
 * @returns {Array} required student data
 */
function getExistingStudentsData(studentsData, handsontableColHeader) {
    const headers = handsontableColHeader.map(unCapitalizeFirstLetter);
    return studentsData.map(student => (headers.map(
            header => student[header])));
}

/**
 * Toggle the chevron image depending on the user's action.
 * @param panelCollapse
 * @param toggleChevron
 */
function toggleChevronImage(panelCollapse, toggleChevron) {
    if ($(panelCollapse).attr('class').indexOf('checked') === -1) {
        $(toggleChevron).addClass('glyphicon-chevron-down').removeClass('glyphicon-chevron-up');
    } else {
        $(toggleChevron).addClass('glyphicon-chevron-up').removeClass('glyphicon-chevron-down');
    }
}

/**
 * Shows the "Existing students" panel with the spreadsheet interface.
 * @param $panelHeading
 * @param panelCollapse
 */
function showExistingStudentsPanel($panelHeading, panelCollapse) {
    $(panelCollapse).collapse('show');
    $(panelCollapse[0]).addClass('checked');
}

/**
 * Hides the "Existing students" panel.
 * @param $panelHeading
 * @param panelCollapse
 */
function hideExistingStudentsPanel($panelHeading, panelCollapse) {
    $(panelCollapse[0]).collapse('hide');
    $panelHeading.addClass('ajax_submit');
    $(panelCollapse[0]).removeClass('checked');
}

/**
 * Displays a message informing the user that there are no existing students in the course.
 * @param displayIcon
 */
function displayNoExistingStudents(displayIcon) {
    let statusMsg = '[ No existing students in course. ]';
    statusMsg = `<strong style="margin-left: 1em; margin-right: 1em;">${statusMsg}</strong>`;
    displayIcon.html(statusMsg);
}

/**
 * Displays a message informing the user that an error surfaced during the AJAX request.
 * @param displayIcon
 */
function displayErrorExecutingAjax(displayIcon) {
    const warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
    let errorMsg = '[ Failed to load. Click here to retry. ]';
    errorMsg = `<strong style="margin-left: 1em; margin-right: 1em;">${errorMsg}</strong>`;
    displayIcon.html(warningSign + errorMsg);
}

/**
 * Shows/Hides the "Existing students" panel depending on the current state of the panel.
 * @param $panelHeading
 * @param panelCollapse
 * @param displayIcon
 * @param toggleChevron
 */
/*  eslint no-unused-expressions: [2, { allowTernary: true }]   */
function toggleExistingStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron) {
    $panelHeading.removeClass('ajax_submit');
    displayIcon.html('');

    ($(panelCollapse[0]).attr('class').indexOf('checked') === -1) ?
            showExistingStudentsPanel($panelHeading, panelCollapse) :
            hideExistingStudentsPanel($panelHeading, panelCollapse);

    toggleChevronImage(panelCollapse, toggleChevron);
}

export {
    getUpdatedHeaderString,
    getUserDataRows,
    getUpdatedData,
    getExistingStudentsData,
    hideExistingStudentsPanel,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    toggleExistingStudentsPanel,
    toggleChevronImage,
};
