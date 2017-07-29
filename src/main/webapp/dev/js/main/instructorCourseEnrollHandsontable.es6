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

function jsUcfirst(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function moveCommentToFirst(array) {
    const getCommentValue = array[2];
    array.splice(2, 1);
    array.unshift(getCommentValue);
    array.reverse();
    return array;
}

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

function addHandsontableHooks() {
    const hooks = ['afterChange', 'afterColumnMove', 'afterRemoveRow'];

    for (let itr = 0; itr < hooks.length; itr += 1) {
        handsontable.addHook(hooks[itr], updateDataDump);
    }
}

function fetchStudentList() {
    const $form = $('#enrollSubmitForm');
    const COURSE_ID = $form.children(`input[name="${ParamsNames.COURSE_ID}"]`).val();
    const USER = $form.children(`input[name="${ParamsNames.USER_ID}"]`).val();

    $.ajax({
        type: 'POST',
        cache: false,
        url: '/page/instructorCourseStudentList',
        data: {
            courseid: COURSE_ID,
            user: USER,
        },
        beforeSend() {
            $('#statusBox').html('<img src=\'/images/ajax-loader.gif\'/>');
        },
        error() {
            $('#statusBox').html('<button id=\'retryFetchingList\' type=\'button\''
                                    + ' class=\'btn btn-danger btn-xs\'>An Error Occurred, Please Retry</button>');
        },
        success(data) {
            $('#statusBox').hide();
            const objects = data.students;
            objects.forEach((object) => {
                let studentData = [];
                Object.keys(object).forEach((key) => {
                    if (columns.includes(jsUcfirst(key))) {
                        studentData.push(object[key]);
                    }
                });
                studentData = moveCommentToFirst(studentData);
                studentListData.push(studentData);
            });

            handsontable.loadData(studentListData);
        },
    });
}

$(document).ready(() => {
    fetchStudentList();
    addHandsontableHooks();

    $('#statusBox').on('click', '#retryFetchingList', () => {
        fetchStudentList();
    });

    $('#addEmptyRows').click(() => {
        const emptyRowsCount = $("input[name='number_of_rows']").val();
        handsontable.alter('insert_row', null, emptyRowsCount);
    });
});
