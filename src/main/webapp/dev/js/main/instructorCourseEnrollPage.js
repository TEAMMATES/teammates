import {
    showModalAlert,
} from '../common/bootboxWrapper';

import {
    BootstrapContextualColors,
} from '../common/const';

import {
    prepareInstructorPages,
} from '../common/instructor';

function isUserTyping(str) {
    return str.indexOf('\t') === -1 && str.indexOf('|') === -1;
}

window.isUserTyping = isUserTyping;

const loadUpFunction = function () {
    const typingErrMsg = 'Please use | character ( shift+\\ ) to separate fields, or copy from your existing spreadsheet.';
    let notified = false;

    const ENTER_KEYCODE = 13;
    let enrolTextbox = $('#enrollstudents');
    if (enrolTextbox.length) {
        [enrolTextbox] = enrolTextbox;
        $(enrolTextbox).keydown((e) => {
            const keycode = e.which || e.keyCode;
            if (keycode === ENTER_KEYCODE) {
                if (isUserTyping(e.currentTarget.value) && !notified) {
                    notified = true;
                    showModalAlert('Invalid separator', typingErrMsg, null, BootstrapContextualColors.WARNING);
                }
            }
        });
    }
};

if (window.addEventListener) {
    window.addEventListener('load', loadUpFunction);
} else {
    window.attachEvent('load', loadUpFunction);
}

const container = document.getElementById('spreadsheet');

/**
 * Holds Handsontable settings, reference and other information for Spreadsheet.
 */
/* global Handsontable:false */
const handsontable = new Handsontable(container, {
    manualColumnResize: true,
    manualRowResize: true,
    manualColumnMove: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
    columnSorting: true,
    className: 'htCenter',
    sortIndicator: true,
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
        'make_read_only',
        'alignment',
    ],
});

/**
 * Updates column header order and generates a header string.
 *
 * Example: Change this array ['Section', 'Team', 'Name', 'Email', 'Comments']
 * into a string = "Section | Team | Name | Email | Comments"
 */
function updateHeaderOrder() {
    const colHeaders = handsontable.getColHeader().join('|');
    return colHeaders.concat('\n');
}

/**
 * Updates the student data from the spreadsheet when any of the
 * following event occurs afterChange, afterColumnMove or afterRemoveRow.
 *
 * Pushes the output data into the textarea (used for form submission).
 *
 */
function updateDataDump() {
    const spreadsheetData = handsontable.getData();
    let dataPushToTextarea = updateHeaderOrder();

    for (let row = 0; row < spreadsheetData.length; row += 1) {
        let countEmptyColumns = 0;
        let rowData = '';
        const spreadsheetDataRow = spreadsheetData[row];

        for (let col = 0; col < spreadsheetDataRow.length; col += 1) {
            rowData += spreadsheetDataRow[col] || '';
            if (col < spreadsheetDataRow.length - 1) {
                rowData += '|';
                if (!spreadsheetDataRow[col]) {
                    countEmptyColumns += 1;
                }
            }
        }

        if (countEmptyColumns < spreadsheetData[row].length - 1) {
            dataPushToTextarea += rowData.concat('\n');
        }
    }

    $('#enrollstudents').text(dataPushToTextarea);
}

/**
 * Adds the listener to specified hook name and only for this Handsontable instance.
 */
function addHandsontableHooks() {
    const hooks = ['afterChange', 'afterColumnMove', 'afterRemoveRow'];

    hooks.forEach((event) => {
        handsontable.addHook(event, updateDataDump);
    });
}

$(document).ready(() => {
    prepareInstructorPages();
    addHandsontableHooks();

    if ($('#enrollstudents').val()) {
        const data = [];
        const allData = $('#enrollstudents').val().split('\n'); // data in the table including column headers
        const columns = allData[0].split('|');
        const spreadsheetDataRows = allData.slice(1, -1);

        if (spreadsheetDataRows.length > 0) {
            for (let userDataRows = 0; userDataRows < spreadsheetDataRows.length; userDataRows += 1) {
                data.push(spreadsheetDataRows[userDataRows].split('|'));
            }
            handsontable.loadData(data); // Reset all cells in the grid to contain data from the data array
        }

        handsontable.updateSettings({
            colHeaders: columns,
        });
    }

    $('#addEmptyRows').click(() => {
        const emptyRowsCount = $("input[name='number_of_rows']").val();
        handsontable.alter('insert_row', null, emptyRowsCount);
    });
});

export {
    isUserTyping,
};
