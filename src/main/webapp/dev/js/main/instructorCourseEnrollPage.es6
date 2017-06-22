import { showModalAlert } from '../common/bootboxWrapper.es6';
import { StatusType } from '../common/const.es6';
import { prepareInstructorPages } from '../common/instructor.es6';

function isUserTyping(str) {
    return str.indexOf('\t') === -1 && str.indexOf('|') === -1;
}

window.isUserTyping = isUserTyping;

const loadUpFunction = function () {
    const typingErrMsg = 'Please use | character ( shift+\\ ) to seperate fields, or copy from your existing spreadsheet.';
    let notified = false;

    const ENTER_KEYCODE = 13;
    let enrolTextbox = $('#enrollstudents');
    if (enrolTextbox.length) {
        enrolTextbox = enrolTextbox[0];
        $(enrolTextbox).keydown((e) => {
            const keycode = e.which || e.keyCode;
            if (keycode === ENTER_KEYCODE) {
                if (isUserTyping(e.target.value) && !notified) {
                    notified = true;
                    showModalAlert('Invalid separator', typingErrMsg, null, StatusType.WARNING);
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

$(document).ready(() => {
    prepareInstructorPages();
});

export {
    isUserTyping,
};

/* Handsontable Implementation code starts here */

$('#toggle-interface').click((e) => {
    $(e.target).text($(e.target).text() == 'Textarea Interface' 
        ? 'Spreadsheet Interface' : 'Textarea Interface');
    $('.student-data-textarea, #student-data-spreadsheet').toggle();
});

var container = document.getElementById('spreadsheet'),
    searchFiled = document.getElementById('search_field'),
    data;

function firstRowRenderer(instance, td, row, col, prop, value, cellProperties) {
    Handsontable.renderers.TextRenderer.apply(this, arguments);
    td.style.fontWeight = 'bold';
    td.style.color = 'green';
    td.style.background = '#CEC';
}

var hot = new Handsontable(container, {
    data: data,
    rowHeaders: true,
    colHeaders: true,
    contextMenu: true,
    contextMenuCopyPaste: {
        swfPath: '/js/ZeroClipboard.swf'
    },
    columnSorting: true,
    manualColumnResize: true,
    sortIndicator: true,
    search: {
        searchResultClass: 'customClass'
    },
    className: "htCenter",
    maxRows: 100,
    maxCols: 100,
    stretchH: 'all',
    minSpareRows: 5,
});

Handsontable.dom.addEvent(searchFiled, 'keyup', function (event) {
    var queryResult = hot.search.query(this.value);
    hot.render();
});

Handsontable.hooks.add('afterChange', function(changes, source) {
    
    var spreadsheetData = hot.getSourceData(),
        dataPushToTextarea = "";
    
    for(var i=0; i < spreadsheetData.length; i++) {
        
        var countEmptyColumns = 0, rowData = "";

        console.log(spreadsheetData[i].length);
        
        for(var j=0; j < spreadsheetData[i].length; j++) {
            
            rowData += spreadsheetData[i][j];
            

            if((spreadsheetData[i][j] === "" || spreadsheetData[i][j] === null) && j < 3) {
                
                countEmptyColumns++;
            }

            if(j < spreadsheetData[i].length - 1) {
                rowData += " | ";
            }
        }

        
        if(countEmptyColumns < 3) {

            dataPushToTextarea += (rowData + '\n');  
        }

        console.log(dataPushToTextarea);
    }

    $('#enrollstudents').text(dataPushToTextarea);

});