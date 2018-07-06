/**
 * Holds Handsontable settings, reference and other information for the spreadsheet interface.
 */
/* global Handsontable:false */
import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    ParamsNames,
} from '../common/const';

import {
    getUpdatedHeaderString,
    getUserDataRows,
    ajaxDataToHandsontableData,
    getUpdatedData,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    getSpreadsheetLength,
    toggleStudentsPanel,
    showUpdateModalBox,
    getUpdatedStudentRows,
} from '../common/instructorEnroll';

const dataContainer = document.getElementById('existingDataSpreadsheet');
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
 * Pushes the output data into the textarea (used for form submission).
 */
function updateEnrollDataDump() {
    const enrollSpreadsheetData = enrollHandsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(enrollHandsontable.getColHeader());
    const userDataRows = getUserDataRows(enrollSpreadsheetData);
    $('#enrollstudents').text(userDataRows === ''
            ? '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

/**
 * Compares the current data in dataHandsontable with the data that is loaded from the initial AJAX request.
 * The rows that are different would be marked as student entries to update in the 'massupdatestudents' textarea.
 */
function updateExistingStudentsDataDump() {
    $('#massupdatestudents').text(
            getUpdatedStudentRows(dataHandsontable.getData(), existingStudentsData));
}

/**
 * Loads existing student data into the spreadsheet interface.
 * @returns {Promise} confirmation that existing students data has been loaded into the spreadsheet.
 */
function loadExistingStudentsData(studentsData) {
    return new Promise((resolve) => {
        resolve(dataHandsontable.loadData(ajaxDataToHandsontableData(
                studentsData, dataHandsontable.getColHeader())));
    });
}

/**
 * Gets list of student data through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxStudentList(displayIcon) {
    return new Promise((resolve, reject) => {
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
        })
                .done(resolve)
                .fail(reject);
    });
}

/**
 * Updates settings of dataHandsontable so that the "Email" column is read-only.
 */
function updateDataHandsontableSettings() {
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
 * Expands "Existing students" panel and loads existing students' data (if spreadsheet is not empty)
 * into the spreadsheet interface. Spreadsheet interface would be shown after expansion.
 * The panel will be collapsed otherwise if the spreadsheet interface is already shown.
 */
function expandCollapseExistingStudentsPanel() {
    const $panelHeading = $(this);
    const panelName = 'Existing students';
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayIcon = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    // perform AJAX only if existing students' spreadsheet is empty
    if (getSpreadsheetLength(dataHandsontable.getData()) === 0) {
        getAjaxStudentList(displayIcon)
                .then((data) => {
                    updateDataHandsontableSettings();
                    if (data.students.length === 0) {
                        displayNoExistingStudents(displayIcon);
                    } else {
                        loadExistingStudentsData(data.students)
                                .then(() => {
                                    toggleStudentsPanel($panelHeading, panelCollapse,
                                            displayIcon, toggleChevron, panelName);

                                    // needed as the view is buggy after collapsing the panel
                                    dataHandsontable.render();

                                    // keep a copy of the current existing students data upon AJAX load
                                    existingStudentsData = dataHandsontable.getData();
                                });
                    }
                }).catch(() => {
                    displayErrorExecutingAjax(displayIcon);
                });
    } else {
        toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron);
        dataHandsontable.render(); // needed as the view is buggy after collapsing the panel
    }
}

/**
 * Expands "New students" panel. Spreadsheet interface would be shown after expansion.
 * The panel will be be collapsed otherwise if the spreadsheet interface is already shown.
 */
function expandCollapseNewStudentsPanel() {
    const $panelHeading = $(this);
    const panelName = 'New students';
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayIcon = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron, panelName);
    enrollHandsontable.render();
}

$(document).ready(() => {
    prepareInstructorPages();
    $('#enroll-spreadsheet').on('click', expandCollapseNewStudentsPanel);
    $('#enroll-spreadsheet').trigger('click');

    $('#existing-data-spreadsheet').click(expandCollapseExistingStudentsPanel);

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

    $('#button_add_empty_rows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        enrollHandsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#button_enroll').click(updateEnrollDataDump);
    $('#button_updatestudents').bind('click', (event) => {
        updateExistingStudentsDataDump();
        showUpdateModalBox($('#massupdatestudents').text(), event);
    });
});
