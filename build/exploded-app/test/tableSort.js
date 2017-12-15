/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// identity function for calling harmony imports with the correct context
/******/ 	__webpack_require__.i = function(value) { return value; };
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 39);
/******/ })
/************************************************************************/
/******/ ({

/***/ 1:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
/**
 * Tests whether the passed object is an actual date
 * with an accepted format
 *
 * Allowed formats : http://dygraphs.com/date-formats.html
 *
 * TEAMMATES currently follows the RFC2822 / IETF date syntax
 * e.g. 02 Apr 2012, 23:59
 *
 * @param date
 * @returns boolean
 */
function isDate(date) {
    return !isNaN(Date.parse(date));
}

/**
* Function to test if param is a numerical value
* @param num
* @returns boolean
*/
function isNumber(num) {
    return (typeof num === 'string' || typeof num === 'number') && !isNaN(num - 0) && num !== '';
}

/**
 * Checks if element is within browser's viewport.
 * @return true if it is within the viewport, false otherwise
 * @see http://stackoverflow.com/q/123999
 */
function isWithinView(element) {
    var baseElement = $(element)[0]; // unwrap jquery element
    var rect = baseElement.getBoundingClientRect();

    var $viewport = $(window);

    // makes the viewport size slightly larger to account for rounding errors
    var tolerance = 0.25;
    return rect.top >= 0 - tolerance // below the top of viewport
    && rect.left >= 0 - tolerance // within the left of viewport
    && rect.right <= $viewport.width() + tolerance // within the right of viewport
    && rect.bottom <= $viewport.height() + tolerance // above the bottom of viewport
    ;
}

exports.isDate = isDate;
exports.isNumber = isNumber;
exports.isWithinView = isWithinView;

/***/ }),

/***/ 13:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


var _sortBy = __webpack_require__(5);

/**
 * Polyfills the String.prototype.includes function finalized in ES6 for browsers that do not yet support the function.
 */
/* eslint-disable no-extend-native */ // necessary for polyfills
if (!String.prototype.includes) {
    String.prototype.includes = function (search, startParam) {
        var start = typeof startParam === 'number' ? startParam : 0;

        if (start + search.length > this.length) {
            return false;
        }
        return this.indexOf(search, start) !== -1;
    };
}
/* eslint-enable no-extend-native */

/**
 * Checks if the current device is touch based device
 * Reference: https://github.com/Modernizr/Modernizr/blob/master/feature-detects/touchevents.js
 */
function isTouchDevice() {
    return ('ontouchstart' in window || window.DocumentTouch) && document instanceof window.DocumentTouch;
}

$(document).on('click', '.toggle-sort', function (e) {
    var $button = $(e.currentTarget); // the button clicked on

    var comparatorStringOrNull = $button.data('toggle-sort-comparator');
    var extractorStringOrNull = $button.data('toggle-sort-extractor');

    (0, _sortBy.toggleSort)($button, comparatorStringOrNull, extractorStringOrNull);
});

$(document).on('ajaxComplete ready', function () {
    /**
     * Initializing then disabling is better than simply
     * not initializing for mobile due to some tooltips-specific
     * code that throws errors.
    */
    var $tooltips = $('[data-toggle="tooltip"]');
    $tooltips.tooltip({
        html: true,
        container: 'body'
    });
    if (isTouchDevice()) {
        $tooltips.tooltip('disable');
    }

    /**
     * Underlines all span elements with tool-tips except for
     * the ones without a text value. This is to exclude elements
     * such as 'icons' from underlining.
    */
    $('span[data-toggle="tooltip"]').each(function () {
        var textValue = $(this).text().replace(/\s/g, '');
        if (textValue) {
            $(this).addClass('tool-tip-decorate');
        }
    });
});

/***/ }),

/***/ 39:
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(13);


/***/ }),

/***/ 5:
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.toggleSort = exports.getPointValue = exports.Comparators = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _helper = __webpack_require__(1);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

/**
 * Returns false if the parameter is either one of null or undefined.
 * Returns true otherwise.
 *
 * @param varToCheck
 */
function isDefined(varToCheck) {
    return varToCheck !== null && varToCheck !== undefined;
}

/**
 * To get point value from a formatted string
 *
 * @param s
 *     A table cell (td tag) that contains the formatted string
 * @param ditchZero
 *     Whether 0% should be treated as lower than -90 or not
 * @returns
 */
function getPointValue(s, ditchZero) {
    var s0 = s;
    var baseValue = 100;

    if (s0.indexOf('/') !== -1) {
        // magic expressions below as these cases will only be compared with
        // case E +(-)X% (0 <= X <= 100)
        if (s0.indexOf('S') !== -1) {
            return 2 * baseValue + 1; // Case N/S (feedback contribution not sure)
        }

        return 2 * baseValue + 2; // Case N/A
    }

    if (s0 === '0%') {
        // Case 0%
        if (ditchZero) {
            return 0;
        }
        return baseValue;
    }

    s0 = s0.replace('E', '').replace('%', ''); // Case E +(-)X%

    if (s0 === '') {
        return baseValue; // Case E
    }

    return baseValue + parseFloat(s0); // Other typical cases
}

/**
 * Sorting comparison functions.
 */

var Comparators = function () {
    function Comparators() {
        _classCallCheck(this, Comparators);
    }

    _createClass(Comparators, null, [{
        key: 'sortBase',

        /**
         * The base comparator (ascending)
         * @returns 1 if x comes after y, -1 if x comes before y, 0 if they are the same
         */
        value: function sortBase(x, y) {
            // Text sorting
            if (x < y) {
                return -1;
            }
            return x > y ? 1 : 0;
        }

        /**
         * Comparator for numbers (integer, double) (ascending)
         * @returns +ve if x > y, -ve if x < y and 0 otherwise
         */

    }, {
        key: 'sortNum',
        value: function sortNum(x, y) {
            return x - y;
        }

        /**
         * Comparator for date. Allows for the same format as isDate()
         * @returns 1 if Date x is after y, 0 if same and -1 if before
         */

    }, {
        key: 'sortDate',
        value: function sortDate(x, y) {
            var x0 = Date.parse(x);
            var y0 = Date.parse(y);
            if (x0 > y0) {
                return 1;
            }
            return x0 < y0 ? -1 : 0;
        }

        /**
         * Comparator to sort strings in format: E([+-]x%) | N/A | N/S | 0% with
         * possibly a tag that surrounds it.
         */

    }, {
        key: 'sortByPoints',
        value: function sortByPoints(a, b) {
            var a0 = getPointValue(a, true);
            var b0 = getPointValue(b, true);
            if ((0, _helper.isNumber)(a0) && (0, _helper.isNumber)(b0)) {
                return Comparators.sortNum(a0, b0);
            }
            return Comparators.sortBase(a0, b0);
        }

        /**
         * Comparator to sort strings in format: [+-]x% | N/A with possibly a tag that
         * surrounds it.
         */

    }, {
        key: 'sortByDiff',
        value: function sortByDiff(a, b) {
            var a0 = getPointValue(a, false);
            var b0 = getPointValue(b, false);
            if ((0, _helper.isNumber)(a0) && (0, _helper.isNumber)(b0)) {
                return Comparators.sortNum(a0, b0);
            }
            return Comparators.sortBase(a0, b0);
        }
    }, {
        key: 'getDefaultComparator',
        value: function getDefaultComparator(columnType) {
            var defaultComparator = void 0;

            if (columnType === 1) {
                defaultComparator = Comparators.sortNum;
            } else if (columnType === 2) {
                defaultComparator = Comparators.sortDate;
            } else {
                defaultComparator = Comparators.sortBase;
            }

            return defaultComparator;
        }
    }]);

    return Comparators;
}();

/**
 * Functions that pull data out of a table cell.
 */


var Extractors = function () {
    function Extractors() {
        _classCallCheck(this, Extractors);
    }

    _createClass(Extractors, null, [{
        key: 'textExtractor',
        value: function textExtractor($tableCell) {
            return $tableCell.text();
        }
    }, {
        key: 'tooltipExtractor',
        value: function tooltipExtractor($tableCell) {
            return $tableCell.find('span').attr('data-original-title');
        }
    }, {
        key: 'dateStampExtractor',
        value: function dateStampExtractor($tableCell) {
            return $tableCell.data('dateStamp');
        }
    }, {
        key: 'getDefaultExtractor',
        value: function getDefaultExtractor() {
            return Extractors.textExtractor;
        }
    }]);

    return Extractors;
}();

var TableButtonHelpers = function () {
    function TableButtonHelpers() {
        _classCallCheck(this, TableButtonHelpers);
    }

    _createClass(TableButtonHelpers, null, [{
        key: 'getEnclosingTable',

        /**
         * Given a button, get the innermost table enclosing it.
         */
        value: function getEnclosingTable($button) {
            return $($button.parents('table')[0]);
        }

        /**
         * Given a button and an index idx,
         * find the button's column position in the table
         * where the columns are treated as idx-indexed.
         */

    }, {
        key: 'getColumnPositionOfButton',
        value: function getColumnPositionOfButton($button, idx) {
            return $button.parent().children().index($button) + idx;
        }

        /**
         * Given a table, clear all the sort states.
         */

    }, {
        key: 'clearAllSortStates',
        value: function clearAllSortStates($table) {
            $table.find('.icon-sort').attr('class', 'icon-sort unsorted'); // clear the icons
            $table.find('.button-sort-ascending').removeClass('button-sort-ascending').addClass('button-sort-none');
            $table.find('.button-sort-descending').removeClass('button-sort-descending').addClass('button-sort-none');
        }

        /**
         * Given a button in table, set its state to sorted ascending.
         * Clear all other button states.
         */

    }, {
        key: 'setButtonToSortedAscending',
        value: function setButtonToSortedAscending($button) {
            this.clearAllSortStates(this.getEnclosingTable($button));
            $button.addClass('button-sort-ascending');
            $button.find('.icon-sort').attr('class', 'icon-sort sorted-ascending'); // set the icon to ascending
        }

        /**
         * Given a button in table, set its state to sorted descending.
         * Clear all other button states.
         */

    }, {
        key: 'setButtonToSortedDescending',
        value: function setButtonToSortedDescending($button) {
            this.clearAllSortStates(this.getEnclosingTable($button));
            $button.addClass('button-sort-descending');
            $button.find('.icon-sort').attr('class', 'icon-sort sorted-descending'); // set the icon to descending
        }
    }]);

    return TableButtonHelpers;
}();

// http://stackoverflow.com/questions/7558182/sort-a-table-fast-by-its-first-column-with-javascript-or-jquery
/**
 * Sorts a table based on certain column and comparator
 *
 * @param $table
 *     A jQuery object representing the table.
 * @param colIdx
 *     The column index (1-based) as key for the sort
 * @param comparatorOrNull
 *     Function to compare two elements.
 *     May be null.
 * @param extractorOrNull
 *     Function to pull out data from a table cell for comparison.
 *     May be null.
 * @param shouldSortAscending
 *     If this is true, sort in ascending order.
 *     Otherwise, sort in descending order
 * @param rowOffset
 *     Ignore rows above this when sorting. Start sorting from this row.
 *     The main use case for this is to ignore the first row, which usually contains the table headers.
 *     In that case, set rowOffset to 1 (thus ignoring row 0).
 */


function sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending, rowOffset) {
    var columnType = 0;
    var store = [];
    var $rowList = $('tr', $table);

    // Iterate through column's contents to decide which comparator to use
    for (var i = rowOffset; i < $rowList.length; i += 1) {
        if ($rowList[i].cells[colIdx - 1] === undefined) {
            continue;
        }

        var extractor = isDefined(extractorOrNull) ? extractorOrNull : Extractors.getDefaultExtractor();

        // $.trim trims leading/trailing whitespaces
        // $rowList[i].cells[colIdx - 1] is where we get the table cell from
        var textToCompare = $.trim(extractor($($rowList[i].cells[colIdx - 1])));

        // Store rows together with the innerText to compare
        store.push([textToCompare, $rowList[i], i]);

        if ((columnType === 0 || columnType === 1) && (0, _helper.isNumber)(textToCompare)) {
            columnType = 1;
        } else if ((columnType === 0 || columnType === 2) && (0, _helper.isDate)(textToCompare)) {
            columnType = 2;
        } else {
            columnType = 3;
        }
    }

    var comparator = isDefined(comparatorOrNull) ? comparatorOrNull : Comparators.getDefaultComparator(columnType);

    store.sort(function (x, y) {
        var compareResult = shouldSortAscending ? comparator(x[0].toUpperCase(), y[0].toUpperCase()) : comparator(y[0].toUpperCase(), x[0].toUpperCase());
        if (compareResult === 0) {
            return x[2] - y[2];
        }
        return compareResult;
    });

    // Must rewrap because .get() does not return a jQuery wrapped DOM node
    // and hence does not have the .children() function
    var $tbody = $($table.get(0)).children('tbody');

    if ($tbody.size < 1) {
        $tbody = $table;
    }

    // Must push to target tbody else it will generate a new tbody for the table
    for (var j = 0; j < store.length; j += 1) {
        $tbody.get(0).appendChild(store[j][1]);
    }

    store = null;
}

/**
 * Sorts a table
 * @param sortButtonClicked
 *     The jQuery object representing the sort button that was clicked.
 * @param comparatorStringOrNull
 *     String representing the function to compare 2 elements.
 *     May be null.
 * @param extractorStringOrNull
 *     String representing the function to pull out data from a table cell for comparison.
 *     May be null.
 */
function toggleSort($button, comparatorStringOrNull, extractorStringOrNull) {
    var isSortedAscending = $button.hasClass('button-sort-ascending');

    var $table = TableButtonHelpers.getEnclosingTable($button);
    var colIdx = TableButtonHelpers.getColumnPositionOfButton($button, 1);
    var comparatorOrNull = !isDefined(comparatorStringOrNull) ? null : Comparators[comparatorStringOrNull];
    var extractorOrNull = !isDefined(extractorStringOrNull) ? null : Extractors[extractorStringOrNull];
    var shouldSortAscending = !isSortedAscending;
    var rowToStartSortingFrom = 1; // <th> occupies row 0

    sortTable($table, colIdx, comparatorOrNull, extractorOrNull, shouldSortAscending, rowToStartSortingFrom);

    // update the button and icon states
    if (shouldSortAscending) {
        TableButtonHelpers.setButtonToSortedAscending($button);
    } else {
        TableButtonHelpers.setButtonToSortedDescending($button);
    }
}

exports.Comparators = Comparators;
exports.getPointValue = getPointValue;
exports.toggleSort = toggleSort;

/***/ })

/******/ });