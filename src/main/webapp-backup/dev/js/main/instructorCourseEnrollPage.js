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
    spreadsheetDataRowsToHandsontableData,
    getSpreadsheetLength,
    toggleStudentsPanel,
    showPasteModalBox,
} from '../common/instructorEnroll';

const dataContainer = document.getElementById('existingDataSpreadsheet');
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
 * Pushes the output data into the textarea (used for form submission).
 */
function updateDataDump() {
    const spreadsheetData = enrollHandsontable.getData();
    const dataPushToTextarea = getUpdatedHeaderString(enrollHandsontable.getColHeader());
    const userDataRows = getUserDataRows(spreadsheetData);
    $('#enrollstudents').text(userDataRows === ''
            ? '' : dataPushToTextarea + userDataRows); // only pushes header string if userDataRows is not empty
}

/**
 * Loads existing student data into the spreadsheet interface.
 */
function loadExistingStudentsData(studentsData) {
    dataHandsontable.loadData(ajaxDataToHandsontableData(
            studentsData, dataHandsontable.getColHeader()));
}

/**
 * Gets list of student data through an AJAX request.
 * @returns {Promise} the state of the result from the AJAX request
 */
function getAjaxStudentList(ajaxPreloadImage) {
    return new Promise((resolve, reject) => {
        const $spreadsheetForm = $('#student-spreadsheet-form');
        $.ajax({
            type: 'POST',
            url: '/page/instructorCourseEnrollAjaxPage',
            cache: false,
            data: {
                courseid: $spreadsheetForm.children(`input[name="${ParamsNames.COURSE_ID}"]`).val(),
            },
            beforeSend() {
                ajaxPreloadImage.show();
            },
        }).done(resolve)
                .fail(reject);
    });
}

/**
 * Handles how the panels are displayed, including rendering the spreadsheet interface.
 */
function adjustStudentsPanelView(panelHeading, panelCollapse, handsontableInstance) {
    toggleStudentsPanel(panelHeading, panelCollapse);
    // needed as the view is buggy after collapsing the panel
    handsontableInstance.render();
}

/**
 * Expands "Existing students" panel. Spreadsheet interface would be shown after expansion.
 * An AJAX request would be called to load existing
 * students' data into the spreadsheet interface (if spreadsheet is not empty).
 * The panel will be collapsed otherwise if the spreadsheet interface is already shown.
 */
function existingStudentsPanelClickHandler(event) {
    const panelHeading = $(event.currentTarget);
    const panelCollapse = panelHeading.parent().children('.panel-collapse');

    const ajaxPreloadImage = panelHeading.find('#ajax-preload-image');
    const ajaxStatusMessage = panelHeading.find('.ajax-status-message');
    ajaxStatusMessage.hide(); // hide any status message from the previous state

    if (getSpreadsheetLength(dataHandsontable.getData()) !== 0) {
        adjustStudentsPanelView(panelHeading, panelCollapse, dataHandsontable);
        return;
    }
    // perform AJAX only if existing students' spreadsheet is empty
    getAjaxStudentList(ajaxPreloadImage)
            .then((data) => {
                ajaxPreloadImage.hide();
                if (data.students.length === 0) {
                    ajaxStatusMessage.show();
                    ajaxStatusMessage.text('No existing students in course.');
                } else {
                    loadExistingStudentsData(data.students);
                    adjustStudentsPanelView(panelHeading, panelCollapse, dataHandsontable);
                }
            }).catch(() => {
                ajaxPreloadImage.hide();
                ajaxStatusMessage.show();
                ajaxStatusMessage.text('Failed to load. Click here to retry.');
            });
}

$(document).ready(() => {
    prepareInstructorPages();
    $('#enroll-spreadsheet').on('click', (event) => {
        const panelHeading = $(event.currentTarget);
        const panelCollapse = panelHeading.parent().children('.panel-collapse');
        adjustStudentsPanelView(panelHeading, panelCollapse, enrollHandsontable);
        $('.enroll-students-spreadsheet-buttons').toggle();
    });
    $('#enroll-spreadsheet').trigger('click');

    $('#existing-data-spreadsheet').on('click', existingStudentsPanelClickHandler);

    if ($('#enrollstudents').val()) {
        const allData = $('#enrollstudents').val().split('\n'); // data in the table including column headers (string format)

        const columnHeaders = allData[0].split('|');
        enrollHandsontable.updateSettings({
            colHeaders: columnHeaders,
        });

        const spreadsheetDataRows = allData.slice(1);
        if (spreadsheetDataRows.length > 0) {
            const data = spreadsheetDataRowsToHandsontableData(spreadsheetDataRows);
            enrollHandsontable.loadData(data); // Reset all cells in the grid to contain data from the data array
        }
    }

    $('#button_add_empty_rows').click(() => {
        const emptyRowsCount = $('#number-of-rows').val();
        enrollHandsontable.alter('insert_row', null, emptyRowsCount);
    });

    $('#student-spreadsheet-form').submit(updateDataDump);
});
