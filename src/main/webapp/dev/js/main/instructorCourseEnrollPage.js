/**
 * Holds Handsontable settings, reference and other information for the spreadsheet interface.
 */
/* global Handsontable:false */
import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    getUpdatedHeaderString,
    getUserDataRows,
    getUpdatedData,
    showPasteModalBox,
} from './instructorCourseEnrollHelper';

const container = document.getElementById('spreadsheet');

const handsontable = new Handsontable(container, {
    height: 500,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    manualColumnMove: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 5,
    maxRows: 100,
    stretchH: 'all',
    minSpareRows: 1,
    contextMenu: [
        'row_above',
        'row_below',
        'remove_row',
        'undo',
        'redo',
        {
            key: 'paste',
            name: 'Paste',
            callback: showPasteModalBox,
        },
        'make_read_only',
        'alignment',
    ],
});

/**
 * Updates the student data from the spreadsheet when the user clicks "Enroll Students" button.
 *
 * Pushes the output data into the textarea (used for form submission).
 */
function updateDataDump() {
    const spreadsheetData = handsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(handsontable.getColHeader());
    const userDataRows = getUserDataRows(spreadsheetData);
    $('#enrollstudents').text(userDataRows === ''
            ? '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

$(document).ready(() => {
    prepareInstructorPages();

    if ($('#enrollstudents').val()) {
        const allData = $('#enrollstudents').val().split('\n'); // data in the table including column headers (string format)

        const columnHeaders = allData[0].split('|');
        handsontable.updateSettings({
            colHeaders: columnHeaders,
        });

        const spreadsheetDataRows = allData.slice(1);
        if (spreadsheetDataRows.length > 0) {
            const data = getUpdatedData(spreadsheetDataRows);
            handsontable.loadData(data); // Reset all cells in the grid to contain data from the data array
        }
    }

    $('#addEmptyRows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        handsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#student-data-spreadsheet-form').submit(updateDataDump);
});
