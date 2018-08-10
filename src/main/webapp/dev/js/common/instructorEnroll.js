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
 * @returns {Array} updated data
 */
function getUpdatedData(spreadsheetDataRows) {
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
    console.log(handsontableColHeader);
    const headers = handsontableColHeader.map(unCapitalizeFirstLetter);
    console.log(headers);
    return studentsData.map(student => {
        const studentRow = headers.map(header => student[header]);
        studentRow.push('');
        return studentRow;
    });
}

/**
 * Toggle the chevron image depending on the user's action.
 */
function toggleChevronImage(panelCollapse, toggleChevron) {
    if ($(panelCollapse).attr('class').indexOf('checked') === -1) {
        $(toggleChevron).addClass('glyphicon-chevron-down').removeClass('glyphicon-chevron-up');
    } else {
        $(toggleChevron).addClass('glyphicon-chevron-up').removeClass('glyphicon-chevron-down');
    }
}

/**
 * Expands panel, showing the spreadsheet interface and its affiliated buttons.
 */
function expandStudentsPanel(panelCollapse, panelName) {
    $(panelCollapse).collapse('show');
    $(panelCollapse[0]).addClass('checked');

    if (panelName === 'New students') {
        $('.enroll-students').show();
    } else {
        $('.existing-students').show();
    }
}

/**
 * Collapses panel, hiding the spreadsheet interface and its affiliated buttons.
 */
function collapseStudentsPanel(panelCollapse, panelName) {
    $(panelCollapse[0]).collapse('hide');
    $(panelCollapse[0]).removeClass('checked');

    if (panelName === 'New students') {
        $('.enroll-students').hide();
    } else {
        $('.existing-students').hide();
    }
}

/**
 * Expands/Collapses the panel depending on the current state of the panel.
 */
function toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron, panelName) {
    displayIcon.html('');
    if ($(panelCollapse[0]).attr('class').indexOf('checked') === -1) {
        expandStudentsPanel(panelCollapse, panelName);
    } else {
        collapseStudentsPanel(panelCollapse, panelName);
    }
    toggleChevronImage(panelCollapse, toggleChevron);
}

/**
 * Displays a message informing the user that there are no existing students in the course.
 */
function displayNoExistingStudents(displayIcon) {
    const statusMsg = `
        <strong style="margin-left: 1em; margin-right: 1em;">
            [ No existing students in course. ]
        </strong>`;
    displayIcon.html(statusMsg);
}

/**
 * Displays a message informing the user that an error surfaced during the AJAX request.
 * @param displayIcon
 */
function displayErrorExecutingAjax(displayIcon) {
    const warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
    const errorMsg = `
        <strong style="margin-left: 1em; margin-right: 1em;">
            [ Failed to load. Click here to retry. ]
        </strong>`;
    displayIcon.html(warningSign + errorMsg);
}

/**
 * Displays a message informing the user that an error surfaced during the AJAX update request.
 * @param displayIcon
 */
function displayErrorExecutingAjaxUpdate(displayIcon) {
    const warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
    const errorMsg = `
        <strong style="margin-left: 1em; margin-right: 1em;">
            [ Failed to update. Check your internet connection. ]
        </strong>`;
    displayIcon.html(warningSign + errorMsg);
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
 * Returns the row entries that are different from the latest existing copy of students' data.
 * @returns {string} updated student row entries
 */
function getUpdatedStudentRows(dataSpreadsheetData, existingStudentsData) {
    return dataSpreadsheetData.filter((row, index) => (JSON.stringify(row)
            !== JSON.stringify(existingStudentsData[index])))
            .map(row => row.join('|'))
            .join('\n');
}

/**
 * Returns a list of new emails that would be updated.
 * @returns {string} list of new emails in separate lines.
 */
function getNewEmailList(submitText) {
    const newEmailColumnIndex = 5;
    return submitText.split('\n')
            .map(row => row.split('|'))
            .filter(row => row[newEmailColumnIndex] !== '')
            .map((row, index) => String(index + 1).concat('. ').concat(row[newEmailColumnIndex]))
            .join('<br>');
}

/**
 * Shows modal box after deleting selected students.
 */
function showDeleteSuccessModalBox() {
    const messageText = 'Selected students have been deleted. Expand the "Existing Students" panel for the latest copy of students.';

    const okCallback = () => {};
    showModalConfirmation('Successful deletion', messageText,
            okCallback, null, null, null, BootstrapContextualColors.SUCCESS);
}

export {
    getUpdatedHeaderString,
    getUserDataRows,
    getUpdatedData,
    ajaxDataToHandsontableData,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    displayErrorExecutingAjaxUpdate,
    getSpreadsheetLength,
    toggleStudentsPanel,
    collapseStudentsPanel,
    toggleChevronImage,
    getUpdatedStudentRows,
    getNewEmailList,
    showDeleteSuccessModalBox,
};
