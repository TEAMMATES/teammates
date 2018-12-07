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
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
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
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 33);
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/main/webapp/dev/js/common/helper.js":
/*!*************************************************!*\
  !*** ./src/main/webapp/dev/js/common/helper.js ***!
  \*************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nObject.defineProperty(exports, \"__esModule\", {\n    value: true\n});\n/**\n * Tests whether the passed object is an actual date\n * with an accepted format\n *\n * Allowed formats : http://dygraphs.com/date-formats.html\n *\n * TEAMMATES currently follows the RFC2822 / IETF date syntax\n * e.g. 02 Apr 2012, 23:59\n *\n * @param date\n * @returns boolean\n */\nfunction isDate(date) {\n    return !Number.isNaN(Date.parse(date));\n}\n\n/**\n* Function to test if param is a numerical value\n* @param num\n* @returns boolean\n*/\nfunction isNumber(num) {\n    return (typeof num === 'string' || typeof num === 'number') && !Number.isNaN(num - 0) && num !== '';\n}\n\n/**\n * Checks if element is within browser's viewport.\n * @return true if it is within the viewport, false otherwise\n * @see http://stackoverflow.com/q/123999\n */\nfunction isWithinView(element) {\n    var baseElement = $(element)[0]; // unwrap jquery element\n    var rect = baseElement.getBoundingClientRect();\n\n    var $viewport = $(window);\n\n    // makes the viewport size slightly larger to account for rounding errors\n    var tolerance = 0.25;\n    return rect.top >= 0 - tolerance // below the top of viewport\n    && rect.left >= 0 - tolerance // within the left of viewport\n    && rect.right <= $viewport.width() + tolerance // within the right of viewport\n    && rect.bottom <= $viewport.height() + tolerance // above the bottom of viewport\n    ;\n}\n\n/**\n * Extracts the suffix that follows the prefix from the id. For example, commentDelete-1-1-0-1 => 1-1-0-1.\n * @param {Object} options required options\n * @param {string} options.idPrefix the prefix of the id\n * @param {string} options.id the id to extract from\n * @return {string} the suffix that uniquely identifies an element among elements with the same prefix\n */\nfunction extractIdSuffixFromId() {\n    var _ref = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {},\n        idPrefix = _ref.idPrefix,\n        id = _ref.id;\n\n    return new RegExp(idPrefix + '-(.*)').exec(id)[1];\n}\n\nexports.isDate = isDate;\nexports.isNumber = isNumber;\nexports.isWithinView = isWithinView;\nexports.extractIdSuffixFromId = extractIdSuffixFromId;//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiLi9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL2NvbW1vbi9oZWxwZXIuanMuanMiLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vc3JjL21haW4vd2ViYXBwL2Rldi9qcy9jb21tb24vaGVscGVyLmpzPzQ2OGIiXSwic291cmNlc0NvbnRlbnQiOlsiLyoqXG4gKiBUZXN0cyB3aGV0aGVyIHRoZSBwYXNzZWQgb2JqZWN0IGlzIGFuIGFjdHVhbCBkYXRlXG4gKiB3aXRoIGFuIGFjY2VwdGVkIGZvcm1hdFxuICpcbiAqIEFsbG93ZWQgZm9ybWF0cyA6IGh0dHA6Ly9keWdyYXBocy5jb20vZGF0ZS1mb3JtYXRzLmh0bWxcbiAqXG4gKiBURUFNTUFURVMgY3VycmVudGx5IGZvbGxvd3MgdGhlIFJGQzI4MjIgLyBJRVRGIGRhdGUgc3ludGF4XG4gKiBlLmcuIDAyIEFwciAyMDEyLCAyMzo1OVxuICpcbiAqIEBwYXJhbSBkYXRlXG4gKiBAcmV0dXJucyBib29sZWFuXG4gKi9cbmZ1bmN0aW9uIGlzRGF0ZShkYXRlKSB7XG4gICAgcmV0dXJuICFOdW1iZXIuaXNOYU4oRGF0ZS5wYXJzZShkYXRlKSk7XG59XG5cbi8qKlxuKiBGdW5jdGlvbiB0byB0ZXN0IGlmIHBhcmFtIGlzIGEgbnVtZXJpY2FsIHZhbHVlXG4qIEBwYXJhbSBudW1cbiogQHJldHVybnMgYm9vbGVhblxuKi9cbmZ1bmN0aW9uIGlzTnVtYmVyKG51bSkge1xuICAgIHJldHVybiAodHlwZW9mIG51bSA9PT0gJ3N0cmluZycgfHwgdHlwZW9mIG51bSA9PT0gJ251bWJlcicpICYmICFOdW1iZXIuaXNOYU4obnVtIC0gMCkgJiYgbnVtICE9PSAnJztcbn1cblxuLyoqXG4gKiBDaGVja3MgaWYgZWxlbWVudCBpcyB3aXRoaW4gYnJvd3NlcidzIHZpZXdwb3J0LlxuICogQHJldHVybiB0cnVlIGlmIGl0IGlzIHdpdGhpbiB0aGUgdmlld3BvcnQsIGZhbHNlIG90aGVyd2lzZVxuICogQHNlZSBodHRwOi8vc3RhY2tvdmVyZmxvdy5jb20vcS8xMjM5OTlcbiAqL1xuZnVuY3Rpb24gaXNXaXRoaW5WaWV3KGVsZW1lbnQpIHtcbiAgICBjb25zdCBiYXNlRWxlbWVudCA9ICQoZWxlbWVudClbMF07IC8vIHVud3JhcCBqcXVlcnkgZWxlbWVudFxuICAgIGNvbnN0IHJlY3QgPSBiYXNlRWxlbWVudC5nZXRCb3VuZGluZ0NsaWVudFJlY3QoKTtcblxuICAgIGNvbnN0ICR2aWV3cG9ydCA9ICQod2luZG93KTtcblxuICAgIC8vIG1ha2VzIHRoZSB2aWV3cG9ydCBzaXplIHNsaWdodGx5IGxhcmdlciB0byBhY2NvdW50IGZvciByb3VuZGluZyBlcnJvcnNcbiAgICBjb25zdCB0b2xlcmFuY2UgPSAwLjI1O1xuICAgIHJldHVybiAoXG4gICAgICAgIHJlY3QudG9wID49IDAgLSB0b2xlcmFuY2UgLy8gYmVsb3cgdGhlIHRvcCBvZiB2aWV3cG9ydFxuICAgICAgICAmJiByZWN0LmxlZnQgPj0gMCAtIHRvbGVyYW5jZSAvLyB3aXRoaW4gdGhlIGxlZnQgb2Ygdmlld3BvcnRcbiAgICAgICAgJiYgcmVjdC5yaWdodCA8PSAkdmlld3BvcnQud2lkdGgoKSArIHRvbGVyYW5jZSAvLyB3aXRoaW4gdGhlIHJpZ2h0IG9mIHZpZXdwb3J0XG4gICAgICAgICYmIHJlY3QuYm90dG9tIDw9ICR2aWV3cG9ydC5oZWlnaHQoKSArIHRvbGVyYW5jZSAvLyBhYm92ZSB0aGUgYm90dG9tIG9mIHZpZXdwb3J0XG4gICAgKTtcbn1cblxuLyoqXG4gKiBFeHRyYWN0cyB0aGUgc3VmZml4IHRoYXQgZm9sbG93cyB0aGUgcHJlZml4IGZyb20gdGhlIGlkLiBGb3IgZXhhbXBsZSwgY29tbWVudERlbGV0ZS0xLTEtMC0xID0+IDEtMS0wLTEuXG4gKiBAcGFyYW0ge09iamVjdH0gb3B0aW9ucyByZXF1aXJlZCBvcHRpb25zXG4gKiBAcGFyYW0ge3N0cmluZ30gb3B0aW9ucy5pZFByZWZpeCB0aGUgcHJlZml4IG9mIHRoZSBpZFxuICogQHBhcmFtIHtzdHJpbmd9IG9wdGlvbnMuaWQgdGhlIGlkIHRvIGV4dHJhY3QgZnJvbVxuICogQHJldHVybiB7c3RyaW5nfSB0aGUgc3VmZml4IHRoYXQgdW5pcXVlbHkgaWRlbnRpZmllcyBhbiBlbGVtZW50IGFtb25nIGVsZW1lbnRzIHdpdGggdGhlIHNhbWUgcHJlZml4XG4gKi9cbmZ1bmN0aW9uIGV4dHJhY3RJZFN1ZmZpeEZyb21JZCh7IGlkUHJlZml4LCBpZCB9ID0ge30pIHtcbiAgICByZXR1cm4gbmV3IFJlZ0V4cChgJHtpZFByZWZpeH0tKC4qKWApLmV4ZWMoaWQpWzFdO1xufVxuXG5leHBvcnQge1xuICAgIGlzRGF0ZSxcbiAgICBpc051bWJlcixcbiAgICBpc1dpdGhpblZpZXcsXG4gICAgZXh0cmFjdElkU3VmZml4RnJvbUlkLFxufTtcbiJdLCJtYXBwaW5ncyI6Ijs7Ozs7QUFBQTs7Ozs7Ozs7Ozs7O0FBWUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7Ozs7QUFLQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7OztBQUtBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQUE7QUFBQTtBQURBO0FBTUE7QUFDQTtBQUNBOzs7Ozs7O0FBT0E7QUFBQTtBQUFBO0FBQUE7QUFDQTtBQUFBO0FBQ0E7QUFDQTtBQUVBO0FBQ0E7QUFDQTtBQUNBIiwic291cmNlUm9vdCI6IiJ9\n//# sourceURL=webpack-internal:///./src/main/webapp/dev/js/common/helper.js\n");

/***/ }),

/***/ "./src/main/webapp/dev/js/common/scrollTo.js":
/*!***************************************************!*\
  !*** ./src/main/webapp/dev/js/common/scrollTo.js ***!
  \***************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nObject.defineProperty(exports, \"__esModule\", {\n    value: true\n});\nexports.scrollToTop = exports.scrollToElement = undefined;\n\nvar _helper = __webpack_require__(/*! ./helper */ \"./src/main/webapp/dev/js/common/helper.js\");\n\n/**\n * Scrolls the screen to a certain position.\n * @param scrollPos Position to scroll the screen to.\n * @param duration Duration of animation in ms. Scrolling is instant if omitted.\n *                 'fast and 'slow' are 600 and 200 ms respectively,\n *                 400 ms will be used if any other string is supplied.\n */\nfunction scrollToPosition(scrollPos, duration) {\n    if (duration === undefined || duration === null) {\n        $(window).scrollTop(scrollPos);\n    } else {\n        $('html, body').animate({ scrollTop: scrollPos }, duration);\n    }\n}\n\n/**\n * Scrolls to an element.\n * Possible options are as follows:\n *\n * @param element - element to scroll to\n * @param options - associative array with optional values:\n *                  * type: ['top'|'view'], defaults to 'top';\n *                          'top' scrolls to the top of the element,\n *                          'view' scrolls the element into view\n *                  * offset: offset from element to scroll to in px,\n *                            defaults to navbar / footer offset for scrolling from above or below\n *                  * duration: duration of animation,\n *                              defaults to 0 for scrolling without animation\n */\nfunction scrollToElement(element, opts) {\n    var defaultOptions = {\n        type: 'top',\n        offset: 0,\n        duration: 0\n    };\n\n    var options = opts || {};\n    var type = options.type || defaultOptions.type;\n    var offset = options.offset || defaultOptions.offset;\n    var duration = options.duration || defaultOptions.duration;\n\n    var isViewType = type === 'view';\n    if (isViewType && (0, _helper.isWithinView)(element)) {\n        return;\n    }\n\n    var navbar = $('.navbar')[0];\n    var navbarHeight = navbar ? navbar.offsetHeight : 0;\n    var footer = $('#footerComponent')[0];\n    var footerHeight = footer ? footer.offsetHeight : 0;\n    var windowHeight = window.innerHeight - navbarHeight - footerHeight;\n\n    var isElementTallerThanWindow = windowHeight < element.offsetHeight;\n    var isFromAbove = window.scrollY < element.offsetTop;\n    var isAlignedToTop = !isViewType || isElementTallerThanWindow || !isFromAbove;\n\n    // default offset - from navbar / footer\n    if (options.offset === undefined) {\n        offset = isAlignedToTop ? navbarHeight * -1 : footerHeight * -1;\n    }\n\n    // adjust offset to bottom of element\n    if (!isAlignedToTop) {\n        offset *= -1;\n        offset += element.offsetHeight - window.innerHeight;\n    }\n\n    var scrollPos = element.offsetTop + offset;\n\n    scrollToPosition(scrollPos, duration);\n}\n\n/**\n * Scrolls the screen to top\n * @param duration Duration of animation in ms. Scrolling is instant if omitted.\n *                 'fast and 'slow' are 600 and 200 ms respectively,\n *                 400 ms will be used if any other string is supplied.\n */\nfunction scrollToTop(duration) {\n    scrollToPosition(0, duration);\n}\n\nexports.scrollToElement = scrollToElement;\nexports.scrollToTop = scrollToTop;//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiLi9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL2NvbW1vbi9zY3JvbGxUby5qcy5qcyIsInNvdXJjZXMiOlsid2VicGFjazovLy9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL2NvbW1vbi9zY3JvbGxUby5qcz82NzRjIl0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7XG4gICAgaXNXaXRoaW5WaWV3LFxufSBmcm9tICcuL2hlbHBlcic7XG5cbi8qKlxuICogU2Nyb2xscyB0aGUgc2NyZWVuIHRvIGEgY2VydGFpbiBwb3NpdGlvbi5cbiAqIEBwYXJhbSBzY3JvbGxQb3MgUG9zaXRpb24gdG8gc2Nyb2xsIHRoZSBzY3JlZW4gdG8uXG4gKiBAcGFyYW0gZHVyYXRpb24gRHVyYXRpb24gb2YgYW5pbWF0aW9uIGluIG1zLiBTY3JvbGxpbmcgaXMgaW5zdGFudCBpZiBvbWl0dGVkLlxuICogICAgICAgICAgICAgICAgICdmYXN0IGFuZCAnc2xvdycgYXJlIDYwMCBhbmQgMjAwIG1zIHJlc3BlY3RpdmVseSxcbiAqICAgICAgICAgICAgICAgICA0MDAgbXMgd2lsbCBiZSB1c2VkIGlmIGFueSBvdGhlciBzdHJpbmcgaXMgc3VwcGxpZWQuXG4gKi9cbmZ1bmN0aW9uIHNjcm9sbFRvUG9zaXRpb24oc2Nyb2xsUG9zLCBkdXJhdGlvbikge1xuICAgIGlmIChkdXJhdGlvbiA9PT0gdW5kZWZpbmVkIHx8IGR1cmF0aW9uID09PSBudWxsKSB7XG4gICAgICAgICQod2luZG93KS5zY3JvbGxUb3Aoc2Nyb2xsUG9zKTtcbiAgICB9IGVsc2Uge1xuICAgICAgICAkKCdodG1sLCBib2R5JykuYW5pbWF0ZSh7IHNjcm9sbFRvcDogc2Nyb2xsUG9zIH0sIGR1cmF0aW9uKTtcbiAgICB9XG59XG5cbi8qKlxuICogU2Nyb2xscyB0byBhbiBlbGVtZW50LlxuICogUG9zc2libGUgb3B0aW9ucyBhcmUgYXMgZm9sbG93czpcbiAqXG4gKiBAcGFyYW0gZWxlbWVudCAtIGVsZW1lbnQgdG8gc2Nyb2xsIHRvXG4gKiBAcGFyYW0gb3B0aW9ucyAtIGFzc29jaWF0aXZlIGFycmF5IHdpdGggb3B0aW9uYWwgdmFsdWVzOlxuICogICAgICAgICAgICAgICAgICAqIHR5cGU6IFsndG9wJ3wndmlldyddLCBkZWZhdWx0cyB0byAndG9wJztcbiAqICAgICAgICAgICAgICAgICAgICAgICAgICAndG9wJyBzY3JvbGxzIHRvIHRoZSB0b3Agb2YgdGhlIGVsZW1lbnQsXG4gKiAgICAgICAgICAgICAgICAgICAgICAgICAgJ3ZpZXcnIHNjcm9sbHMgdGhlIGVsZW1lbnQgaW50byB2aWV3XG4gKiAgICAgICAgICAgICAgICAgICogb2Zmc2V0OiBvZmZzZXQgZnJvbSBlbGVtZW50IHRvIHNjcm9sbCB0byBpbiBweCxcbiAqICAgICAgICAgICAgICAgICAgICAgICAgICAgIGRlZmF1bHRzIHRvIG5hdmJhciAvIGZvb3RlciBvZmZzZXQgZm9yIHNjcm9sbGluZyBmcm9tIGFib3ZlIG9yIGJlbG93XG4gKiAgICAgICAgICAgICAgICAgICogZHVyYXRpb246IGR1cmF0aW9uIG9mIGFuaW1hdGlvbixcbiAqICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZGVmYXVsdHMgdG8gMCBmb3Igc2Nyb2xsaW5nIHdpdGhvdXQgYW5pbWF0aW9uXG4gKi9cbmZ1bmN0aW9uIHNjcm9sbFRvRWxlbWVudChlbGVtZW50LCBvcHRzKSB7XG4gICAgY29uc3QgZGVmYXVsdE9wdGlvbnMgPSB7XG4gICAgICAgIHR5cGU6ICd0b3AnLFxuICAgICAgICBvZmZzZXQ6IDAsXG4gICAgICAgIGR1cmF0aW9uOiAwLFxuICAgIH07XG5cbiAgICBjb25zdCBvcHRpb25zID0gb3B0cyB8fCB7fTtcbiAgICBjb25zdCB0eXBlID0gb3B0aW9ucy50eXBlIHx8IGRlZmF1bHRPcHRpb25zLnR5cGU7XG4gICAgbGV0IG9mZnNldCA9IG9wdGlvbnMub2Zmc2V0IHx8IGRlZmF1bHRPcHRpb25zLm9mZnNldDtcbiAgICBjb25zdCBkdXJhdGlvbiA9IG9wdGlvbnMuZHVyYXRpb24gfHwgZGVmYXVsdE9wdGlvbnMuZHVyYXRpb247XG5cbiAgICBjb25zdCBpc1ZpZXdUeXBlID0gdHlwZSA9PT0gJ3ZpZXcnO1xuICAgIGlmIChpc1ZpZXdUeXBlICYmIGlzV2l0aGluVmlldyhlbGVtZW50KSkge1xuICAgICAgICByZXR1cm47XG4gICAgfVxuXG4gICAgY29uc3QgbmF2YmFyID0gJCgnLm5hdmJhcicpWzBdO1xuICAgIGNvbnN0IG5hdmJhckhlaWdodCA9IG5hdmJhciA/IG5hdmJhci5vZmZzZXRIZWlnaHQgOiAwO1xuICAgIGNvbnN0IGZvb3RlciA9ICQoJyNmb290ZXJDb21wb25lbnQnKVswXTtcbiAgICBjb25zdCBmb290ZXJIZWlnaHQgPSBmb290ZXIgPyBmb290ZXIub2Zmc2V0SGVpZ2h0IDogMDtcbiAgICBjb25zdCB3aW5kb3dIZWlnaHQgPSB3aW5kb3cuaW5uZXJIZWlnaHQgLSBuYXZiYXJIZWlnaHQgLSBmb290ZXJIZWlnaHQ7XG5cbiAgICBjb25zdCBpc0VsZW1lbnRUYWxsZXJUaGFuV2luZG93ID0gd2luZG93SGVpZ2h0IDwgZWxlbWVudC5vZmZzZXRIZWlnaHQ7XG4gICAgY29uc3QgaXNGcm9tQWJvdmUgPSB3aW5kb3cuc2Nyb2xsWSA8IGVsZW1lbnQub2Zmc2V0VG9wO1xuICAgIGNvbnN0IGlzQWxpZ25lZFRvVG9wID0gIWlzVmlld1R5cGUgfHwgaXNFbGVtZW50VGFsbGVyVGhhbldpbmRvdyB8fCAhaXNGcm9tQWJvdmU7XG5cbiAgICAvLyBkZWZhdWx0IG9mZnNldCAtIGZyb20gbmF2YmFyIC8gZm9vdGVyXG4gICAgaWYgKG9wdGlvbnMub2Zmc2V0ID09PSB1bmRlZmluZWQpIHtcbiAgICAgICAgb2Zmc2V0ID0gaXNBbGlnbmVkVG9Ub3AgPyBuYXZiYXJIZWlnaHQgKiAtMSA6IGZvb3RlckhlaWdodCAqIC0xO1xuICAgIH1cblxuICAgIC8vIGFkanVzdCBvZmZzZXQgdG8gYm90dG9tIG9mIGVsZW1lbnRcbiAgICBpZiAoIWlzQWxpZ25lZFRvVG9wKSB7XG4gICAgICAgIG9mZnNldCAqPSAtMTtcbiAgICAgICAgb2Zmc2V0ICs9IGVsZW1lbnQub2Zmc2V0SGVpZ2h0IC0gd2luZG93LmlubmVySGVpZ2h0O1xuICAgIH1cblxuICAgIGNvbnN0IHNjcm9sbFBvcyA9IGVsZW1lbnQub2Zmc2V0VG9wICsgb2Zmc2V0O1xuXG4gICAgc2Nyb2xsVG9Qb3NpdGlvbihzY3JvbGxQb3MsIGR1cmF0aW9uKTtcbn1cblxuLyoqXG4gKiBTY3JvbGxzIHRoZSBzY3JlZW4gdG8gdG9wXG4gKiBAcGFyYW0gZHVyYXRpb24gRHVyYXRpb24gb2YgYW5pbWF0aW9uIGluIG1zLiBTY3JvbGxpbmcgaXMgaW5zdGFudCBpZiBvbWl0dGVkLlxuICogICAgICAgICAgICAgICAgICdmYXN0IGFuZCAnc2xvdycgYXJlIDYwMCBhbmQgMjAwIG1zIHJlc3BlY3RpdmVseSxcbiAqICAgICAgICAgICAgICAgICA0MDAgbXMgd2lsbCBiZSB1c2VkIGlmIGFueSBvdGhlciBzdHJpbmcgaXMgc3VwcGxpZWQuXG4gKi9cbmZ1bmN0aW9uIHNjcm9sbFRvVG9wKGR1cmF0aW9uKSB7XG4gICAgc2Nyb2xsVG9Qb3NpdGlvbigwLCBkdXJhdGlvbik7XG59XG5cbmV4cG9ydCB7XG4gICAgc2Nyb2xsVG9FbGVtZW50LFxuICAgIHNjcm9sbFRvVG9wLFxufTtcbiJdLCJtYXBwaW5ncyI6Ijs7Ozs7OztBQUFBO0FBQ0E7QUFHQTs7Ozs7OztBQU9BO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7Ozs7Ozs7Ozs7Ozs7QUFjQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBSEE7QUFDQTtBQUtBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOzs7Ozs7QUFNQTtBQUNBO0FBQ0E7QUFDQTtBQUVBO0FBQ0EiLCJzb3VyY2VSb290IjoiIn0=\n//# sourceURL=webpack-internal:///./src/main/webapp/dev/js/common/scrollTo.js\n");

/***/ }),

/***/ "./src/main/webapp/dev/js/main/statusMessage.js":
/*!******************************************************!*\
  !*** ./src/main/webapp/dev/js/main/statusMessage.js ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nvar _scrollTo = __webpack_require__(/*! ../common/scrollTo */ \"./src/main/webapp/dev/js/common/scrollTo.js\");\n\n$(document).ready(function () {\n    var statusMessage = $('#statusMessagesToUser').get(0);\n    var navbarHeight = 0;\n    var extraPadding = 15;\n\n    var navbar = $('.navbar')[0];\n\n    if (navbar !== undefined) {\n        navbarHeight = navbar.offsetHeight;\n    }\n\n    (0, _scrollTo.scrollToElement)(statusMessage, {\n        type: 'view',\n        offset: (navbarHeight + extraPadding) * -1\n    });\n});//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiLi9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL21haW4vc3RhdHVzTWVzc2FnZS5qcy5qcyIsInNvdXJjZXMiOlsid2VicGFjazovLy9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL21haW4vc3RhdHVzTWVzc2FnZS5qcz9kODBmIl0sInNvdXJjZXNDb250ZW50IjpbImltcG9ydCB7XG4gICAgc2Nyb2xsVG9FbGVtZW50LFxufSBmcm9tICcuLi9jb21tb24vc2Nyb2xsVG8nO1xuXG4kKGRvY3VtZW50KS5yZWFkeSgoKSA9PiB7XG4gICAgY29uc3Qgc3RhdHVzTWVzc2FnZSA9ICQoJyNzdGF0dXNNZXNzYWdlc1RvVXNlcicpLmdldCgwKTtcbiAgICBsZXQgbmF2YmFySGVpZ2h0ID0gMDtcbiAgICBjb25zdCBleHRyYVBhZGRpbmcgPSAxNTtcblxuICAgIGNvbnN0IG5hdmJhciA9ICQoJy5uYXZiYXInKVswXTtcblxuICAgIGlmIChuYXZiYXIgIT09IHVuZGVmaW5lZCkge1xuICAgICAgICBuYXZiYXJIZWlnaHQgPSBuYXZiYXIub2Zmc2V0SGVpZ2h0O1xuICAgIH1cblxuICAgIHNjcm9sbFRvRWxlbWVudChzdGF0dXNNZXNzYWdlLCB7XG4gICAgICAgIHR5cGU6ICd2aWV3JyxcbiAgICAgICAgb2Zmc2V0OiAobmF2YmFySGVpZ2h0ICsgZXh0cmFQYWRkaW5nKSAqIC0xLFxuICAgIH0pO1xufSk7XG4iXSwibWFwcGluZ3MiOiI7O0FBQUE7QUFDQTtBQUdBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFGQTtBQUlBIiwic291cmNlUm9vdCI6IiJ9\n//# sourceURL=webpack-internal:///./src/main/webapp/dev/js/main/statusMessage.js\n");

/***/ }),

/***/ 33:
/*!************************************************************!*\
  !*** multi ./src/main/webapp/dev/js/main/statusMessage.js ***!
  \************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! /home/alessandra/Documenti/LINGI2401/teammates/src/main/webapp/dev/js/main/statusMessage.js */"./src/main/webapp/dev/js/main/statusMessage.js");


/***/ })

/******/ });