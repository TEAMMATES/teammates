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
    hideExistingStudentsPanel,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    toggleExistingStudentsPanel,
    toggleChevronImage,
} from './instructorCourseEnrollHelper';

const dataContainer = document.getElementById('dataSpreadsheet');
/* global Handsontable:false */
const dataHandsontable = new Handsontable(dataContainer, {
    height: 400,
    autoWrapRow: true,
    preventOverflow: 'horizontal',
    manualColumnResize: true,
    manualRowResize: true,
    rowHeaders: true,
    colHeaders: ['Section', 'Team', 'Name', 'Email', 'Comments', 'Fill in the new email here'],
    columnSorting: true,
    sortIndicator: true,
    minRows: 20,
    maxCols: 6,
    stretchH: 'all',
});

const enrollContainer = document.getElementById('enrollSpreadsheet');
/* global Handsontable:false */
const enrollHandsontable = new Handsontable(enrollContainer, {
    className: 'enroll-handsontable',
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

let existingStudentsData = null;

/**
 * Updates the student data from the spreadsheet when the user clicks "Enroll Students" button.
 *
 * Pushes the output data into the textarea (used for form submission).
 */
function updateEnrollDataDump() {
    const enrollSpreadsheetData = enrollHandsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(enrollHandsontable.getColHeader());
    const userDataRows = getUserDataRows(enrollSpreadsheetData);
    $('#enrollstudents').text(userDataRows === '' ?
            '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

/**
 * Loads existing student data into the spreadsheet interface.
 * @param studentsData
 * @param $panelHeading
 * @param panelCollapse
 * @param displayIcon
 * @param toggleChevron
 */
function loadExistingStudentsData(studentsData, $panelHeading, panelCollapse, displayIcon, toggleChevron) {
    dataHandsontable.loadData(getExistingStudentsData(
            studentsData, dataHandsontable.getColHeader()));
    toggleExistingStudentsPanel($panelHeading, panelCollapse,
            displayIcon, toggleChevron);
    dataHandsontable.render(); // needed as the view is buggy after collapsing the panel
    existingStudentsData = dataHandsontable.getData();
}

/**
 * Gets list of student data through an AJAX request.
 * @param $panelHeading
 * @param panelCollapse
 * @param displayIcon
 * @param toggleChevron
 */
/*  eslint no-unused-expressions: [2, { allowTernary: true }]   */
function getAjaxStudentList($panelHeading, panelCollapse, displayIcon, toggleChevron) {
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
            displayIcon.html('<img height="25" width="25" src="/images/ajax-preload.gif">');
        },
        error() {
            displayErrorExecutingAjax(displayIcon);
        },
        success(data) {
            (data.students.length === 0) ? displayNoExistingStudents(displayIcon) :
            loadExistingStudentsData(data.students, $panelHeading, panelCollapse, displayIcon, toggleChevron);
        },
    });

    dataHandsontable.updateSettings({
        columns: [
            { colHeader: 'Section' },
            { colHeader: 'Team' },
            { colHeader: 'Name' },
            { colHeader: 'Email', readOnly: true },
            { colHeader: 'Comments' },
            { colHeader: 'Fill in the new email here' },
        ],
    });
}

/**
 * Function to collapse "Existing students" panel and loads existing students' data when the user clicks on it.
 */
function collapseExistingStudentsPanel() {
    const $panelHeading = $(this);
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayIcon = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');
    const updateButton = $('#button_updatestudents');

    if ($panelHeading.attr('class').indexOf('ajax_submit') === -1) { // if panel is shown
        updateButton.hide();
        hideExistingStudentsPanel($panelHeading, panelCollapse);
        toggleChevronImage(panelCollapse, toggleChevron);
    } else {
        updateButton.show();
        getAjaxStudentList($panelHeading, panelCollapse, displayIcon, toggleChevron);
    }
}

function updateExistingStudentsDataDump() {
    const dataSpreadsheetData = dataHandsontable.getData();
    const submitText = dataSpreadsheetData.filter((row, index) => (JSON.stringify(row) !==
            JSON.stringify(existingStudentsData[index])))
            .map(row => row.join('|'))
            .join('\n');

    $('#massupdatestudents').text(submitText === '' ? '' : submitText);
}

$(document).ready(() => {
    prepareInstructorPages();

    $('.ajax_submit').click(collapseExistingStudentsPanel);

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

    $('#addEmptyRows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        enrollHandsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#button_enroll').submit(updateEnrollDataDump);
    $('#button_updatestudents').click(updateExistingStudentsDataDump);
});
