/* global Handsontable:false */

const container = document.getElementById('spreadsheet');
let columns = ['Section', 'Team', 'Name', 'Email', 'Comment'];

$('#toggle-interface').click((e) => {
    $(e.target).text($(e.target).text() === 'Textarea Interface'
        ? 'Spreadsheet Interface' : 'Textarea Interface');
    $('.student-data-textarea, #student-data-spreadsheet').toggle();
});

const handsontable = new Handsontable(container, {
    rowHeaders: true,
    colHeaders: columns,
    contextMenu: [
        'row_above',
        'row_below',
        'remove_row',
        '---------',
        'undo',
        'redo',
        '---------',
        'make_read_only',
        'alignment',
    ],
    columnSorting: true,
    className: 'htCenter',
    manualColumnResize: true,
    sortIndicator: true,
    maxRows: 100,
    maxCols: 100,
    stretchH: 'all',
    minSpareRows: 2,
    manualColumnMove: true,
});

function updateHeaderOrder() {
    const colHeader = handsontable.getColHeader();
    let headerString = '';
    for (let itr = 0; itr < colHeader.length; itr += 1) {
        headerString += colHeader[itr];
        if (itr < colHeader.length - 1) {
            headerString += ' | ';
        }
    }
    headerString += '\n';
    return headerString;
}

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
                rowData += ' | ';
            }
        }
        if (countEmptyColumns < spreadsheetData[row].length - 1) {
            dataPushToTextarea += rowData;
            dataPushToTextarea += '\n';
        }
    }
    $('#enrollstudents').text(dataPushToTextarea);
}

(() => {
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
})();

const hooks = ['afterChange', 'afterColumnMove', 'afterRemoveRow'];

for (let itr = 0; itr < hooks.length; itr += 1) {
    handsontable.addHook(hooks[itr], updateDataDump);
}
