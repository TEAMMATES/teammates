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
    ajaxDataToHandsontableData,
    getUpdatedData,
    displayNoExistingStudents,
    displayErrorExecutingAjax,
    getSpreadsheetLength,
    toggleStudentsPanel,
} from '../common/instructorEnroll';

const dataContainer = document.getElementById('existingDataSpreadsheet');
/* global Handsontable:false */
const dataHandsontable = new Handsontable(dataContainer, {
    readOnly: true,
    height: 400,
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

/**
 * Updates the student data from the spreadsheet when the user clicks "Enroll Students" button.
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
 * @param studentsData
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
 * @param displayIcon
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxStudentList(displayIcon) {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-spreadsheet-form');
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
 * Expands "Existing students" panel and loads existing students' data (if spreadsheet is not empty)
 * into the spreadsheet interface. Spreadsheet interface would be shown after expansion.
 * The panel will be be collapsed otherwise if the spreadsheet interface is already shown.
 */
/*  eslint no-unused-expressions: [2, { allowTernary: true }]   */
function expandCollapseExistingStudentsPanel() {
    const $panelHeading = $(this);
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayIcon = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    // perform AJAX only if existing students' spreadsheet is empty
    if (getSpreadsheetLength(dataHandsontable.getData()) === 0) {
        getAjaxStudentList(displayIcon)
                .then((data) => {
                (data.students.length === 0) ? displayNoExistingStudents(displayIcon) :
                    loadExistingStudentsData(data.students)
                            .then(() => {
                                toggleStudentsPanel($panelHeading, panelCollapse,
                                        displayIcon, toggleChevron);
                                dataHandsontable.render(); // needed as the view is buggy after collapsing the panel
                            });
                }).catch(() => {
                    displayErrorExecutingAjax(displayIcon);
                });
    } else {
        toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron);
        dataHandsontable.render(); // needed as the view is buggy after collapsing the panel);
    }
}

/**
 * Expands "New students" panel. Spreadsheet interface would be shown after expansion.
 * The panel will be be collapsed otherwise if the spreadsheet interface is already shown.
 */
function expandCollapseNewStudentsPanel() {
    const $panelHeading = $(this);
    const panelCollapse = $panelHeading.parent().children('.panel-collapse');
    const displayIcon = $panelHeading.children('.display-icon');
    const toggleChevron = $panelHeading.parent().find('.glyphicon-chevron-down, .glyphicon-chevron-up');

    toggleStudentsPanel($panelHeading, panelCollapse, displayIcon, toggleChevron);
    enrollHandsontable.render();

    if (panelCollapse.attr('class').indexOf('checked') === -1) { // if panel is not shown
        $('.enroll-students').hide();
    } else {
        $('.enroll-students').show();
    }
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

    $('#student-spreadsheet-form').submit(updateDataDump);
});
