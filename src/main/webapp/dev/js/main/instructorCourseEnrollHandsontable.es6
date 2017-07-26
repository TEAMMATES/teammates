/* global Handsontable:false */

import {
    ParamsNames,
} from '../common/const.es6';

const container = document.getElementById('spreadsheet');
const columns = ['Section', 'Team', 'Name', 'Email', 'Comments'];
const studentListData = [];

const handsontable = new Handsontable(container, {
    rowHeaders: true,
    colHeaders: columns,
    columnSorting: true,
    className: 'htCenter',
    manualColumnResize: true,
    sortIndicator: true,
    maxRows: 100,
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

$(document).ready(() => {
    const $form = $('#enrollSubmitForm');
    const COURSE_ID = $form.children(`input[name="${ParamsNames.COURSE_ID}"]`).val();
    const USER = $form.children(`input[name="${ParamsNames.USER_ID}"]`).val();

    $.ajax({
        type: 'POST',
        url: '/page/instructorCourseStudentList',
        data: {
            courseid: COURSE_ID,
            user: USER,
        },
        success(data) {
            const objects = data.students;
            objects.forEach((object) => {
                const studentData = [];
                studentData.push(object.section);
                studentData.push(object.team);
                studentData.push(object.name);
                studentData.push(object.email);
                studentData.push(object.comments);
                studentListData.push(studentData);
            });
            handsontable.loadData(studentListData);
        },
    });

    $('#addEmptyRows').click(() => {
        const emptyRowsCount = $("input[name='number_of_rows']").val();
        handsontable.alter('insert_row', null, emptyRowsCount);
    });
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

const hooks = ['afterChange', 'afterColumnMove', 'afterRemoveRow'];

for (let itr = 0; itr < hooks.length; itr += 1) {
    handsontable.addHook(hooks[itr], updateDataDump);
}
