/**
 * Holds Handsontable settings, reference and other information for the spreadsheet interface.
 */
import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    ParamsNames,
} from '../common/const';

import {
    getUpdatedHeaderString,
    getUserDataRows,
    getExistingStudentsData,
    getUpdatedData,
} from './instructorCourseEnrollHelper';

const dataContainer = document.getElementById('dataSpreadsheet');
/* global Handsontable:false */
const dataHandsontable = new Handsontable(dataContainer, {
    readOnly: true,
    height: 500,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 5,
    stretchH: 'all',
    minSpareRows: 1,
});

const enrollContainer = document.getElementById('enrollSpreadsheet');
/* global Handsontable:false */
const enrollHandsontable = new Handsontable(enrollContainer, {
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
    const spreadsheetData = enrollHandsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(enrollHandsontable.getColHeader());
    const userDataRows = getUserDataRows(spreadsheetData);
    $('#enrollstudents').text(userDataRows === '' ?
            '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

/**
 * Loads existing student data into the spreadsheet interface.
 * @param data
 */
function loadExistingStudentsData(data) {
    const studentsData = data.students;

    if (studentsData.length === 0) {
        $('#ajaxStatusBox').html('<div class=\'overflow-auto alert alert-warning ' +
                'icon-warning statusMessage\'>No existing students in course.</div>');
        $('#data-spreadsheet').hide();
    } else {
        $('#ajaxStatusBox').html('<div class=\'overflow-auto alert alert-success ' +
                'icon-success statusMessage\'>Existing students have been successfully ' +
                'loaded into the spreadsheet interface.</div>');
        dataHandsontable.loadData(getExistingStudentsData(
                studentsData, dataHandsontable.getColHeader()));
    }
}

/**
 * Gets list of student data through an AJAX request.
 */
function getAjaxStudentList() {
    const $spreadsheetForm = $('#student-data-spreadsheet-form');

    $.ajax({
        type: 'POST',
        url: '/page/instructorCourseEnrollAjaxPage',
        cache: false,
        data: {
            courseid: $spreadsheetForm.children(`input[name="${ParamsNames.COURSE_ID}"]`).val(),
            user: $spreadsheetForm.children(`input[name="${ParamsNames.USER_ID}"]`).val(),
        },
        beforeSend() {
            $('#ajaxStatusBox').html('<div style="text-align: center">' +
                    '<img src=\'/images/ajax-loader.gif\'/></div>');
        },
        error() {
            $('#ajaxStatusBox').html('<div class=\'overflow-auto alert alert-danger ' +
                    'icon-danger statusMessage\'>Error loading existing students into spreadsheet interface. ' +
                    'To retry, <button id=\'retryGetAjaxStudentList\' type=\'button\''
                    + ' class=\'btn btn-danger btn-xs\'>Click here</button></div>');
        },
        success(data) {
            loadExistingStudentsData(data);
        },
    });
}

$(document).ready(() => {
    prepareInstructorPages();
    getAjaxStudentList();

    if ($('#enrollstudents').val()) {
        const allData = $('#enrollstudents').val().split('\n'); // data in the table including column headers (string format)

        const columnHeaders = allData[0].split('|');
        enrollHandsontable.updateSettings({
            colHeaders: columnHeaders,
        });

        const spreadsheetDataRows = allData.slice(1);
        if (spreadsheetDataRows.length > 0) {
            const data = getUpdatedData(spreadsheetDataRows);
            enrollHandsontable.loadData(data); // Reset all cells in the grid to contain data from the data array
        }
    }

    $('#ajaxStatusBox').on('click', '#retryGetAjaxStudentList', () => {
        getAjaxStudentList();
    });

    $('#addEmptyRows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        enrollHandsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#student-data-spreadsheet-form').submit(updateDataDump);
});
