/* global Handsontable:false */

/**
 * @Global Variables
 */
const container = document.getElementById('spreadsheet');
let columns = ['Section', 'Team', 'Name', 'Email', 'Comments'];

/**
 * Holds Handsontable settings, reference and other information for Spreadsheet.
 */
const handsontable = new Handsontable(container, {
    rowHeaders: true,
    colHeaders: columns,
    columnSorting: true,
    className: 'htCenter',
    manualColumnResize: true,
    sortIndicator: true,
    maxCols: 5,
    stretchH: 'all',
    minSpareRows: 2,
    manualColumnMove: true,
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
    let headerString = '';
    const colHeader = handsontable.getColHeader();
    for (let itr = 0; itr < colHeader.length; itr += 1) {
        headerString += colHeader[itr];
        if (itr < colHeader.length - 1) {
            headerString += '|';
        }
    }
    headerString += '\n';
    return headerString;
}

/**
 * Updates the Student data from the spreadsheet when any of the
 * following event occurs afterChange, afterColumnMove or afterRemoveRow.
 *
 * Push the output data into the textarea (used for form submission).
 *
 * Handles the width of column and height of row in case of overflow.
 */
function updateDataDump() {
    const spreadsheetData = handsontable.getData();
    let dataPushToTextarea = updateHeaderOrder();
    let countEmptyColumns = 0;
    let rowData = '';
    for (let row = 0; row < spreadsheetData.length; row += 1) {
        countEmptyColumns = 0; rowData = '';
        for (let col = 0; col < spreadsheetData[row].length; col += 1) {
            rowData += spreadsheetData[row][col] !== null ? spreadsheetData[row][col] : '';
            if ((spreadsheetData[row][col] === '' || spreadsheetData[row][col] === null)
             && col < spreadsheetData[row].length - 1) {
                countEmptyColumns += 1;
            }
            if (col < spreadsheetData[row].length - 1) {
                rowData += '|';
            }
        }
        if (countEmptyColumns < spreadsheetData[row].length - 1) {
            dataPushToTextarea += rowData;
            dataPushToTextarea += '\n';
        }
    }

    $('#enrollstudents').text(dataPushToTextarea);

    /* eslint-disable consistent-return */
    handsontable.updateSettings({
        modifyColWidth: (width) => {
            if (width > 165) {
                return 150;
            }
        },
        modifyRowHeight: (height) => {
            if (height > 20) {
                return 10;
            }
        },
    });
    /* eslint-enable consistent-return */
}

/**
 * Adds the listener to specified hook name and only for this Handsontable instance.
 */
function addHandsontableHooks() {
    const hooks = ['afterChange', 'afterColumnMove', 'afterRemoveRow'];

    for (let itr = 0; itr < hooks.length; itr += 1) {
        handsontable.addHook(hooks[itr], updateDataDump);
    }
}

$(document).ready(() => {
    addHandsontableHooks();

    if ($('#enrollstudents').val()) {
        const data = [];
        let splitData = $('#enrollstudents').val().split('\n');
        columns = splitData[0].split('|');
        splitData = splitData.slice(1, -1);
        if (splitData.length > 0) {
            for (let erow = 0; erow < splitData.length; erow += 1) {
                data.push(splitData[erow].split('|'));
            }
            handsontable.loadData(data);
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
