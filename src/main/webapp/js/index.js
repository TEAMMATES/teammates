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
/******/ 	return __webpack_require__(__webpack_require__.s = 12);
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/main/webapp/dev/js/common/checkBrowserVersion.js":
/*!**************************************************************!*\
  !*** ./src/main/webapp/dev/js/common/checkBrowserVersion.js ***!
  \**************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nObject.defineProperty(exports, \"__esModule\", {\n    value: true\n});\n// Browser Compatibility and support\nvar MSIE = 'Microsoft Internet Explorer';\nvar MSIE_LOWEST_VERSION = 9;\nvar CHROME = 'Chrome';\nvar CHROME_LOWEST_VERSION = 15;\nvar FIREFOX = 'Firefox';\nvar FIREFOX_LOWEST_VERSION = 12;\nvar SAFARI = 'Safari';\nvar SAFARI_LOWEST_VERSION = 4;\n\n/**\n * Function to check browser version and alert if browser version is lower than supported\n * Adapted from http://www.javascripter.net/faq/browsern.htm\n *\n */\n\nfunction checkBrowserVersion() {\n    var nAgt = navigator.userAgent;\n    var browserName = navigator.appName;\n    var fullVersion = parseFloat(navigator.appVersion);\n    var majorVersion = parseInt(navigator.appVersion, 10);\n    var verOffset = void 0;\n    var supported = true;\n\n    /* eslint-disable no-negated-condition */ // usage of .contains() equivalent requires !==\n    if (nAgt.indexOf('MSIE') !== -1) {\n        // In MSIE, the true version is after \"MSIE\" in userAgent\n        verOffset = nAgt.indexOf('MSIE');\n        browserName = MSIE;\n        fullVersion = nAgt.substring(verOffset + 5);\n        majorVersion = parseInt(fullVersion, 10);\n        if (majorVersion < MSIE_LOWEST_VERSION) {\n            supported = false;\n        }\n    } else if (nAgt.indexOf('Chrome') !== -1) {\n        // In Chrome, the true version is after \"Chrome\"\n        verOffset = nAgt.indexOf('Chrome');\n        browserName = CHROME;\n        fullVersion = nAgt.substring(verOffset + 7);\n        majorVersion = parseInt(fullVersion, 10);\n        if (majorVersion < CHROME_LOWEST_VERSION) {\n            supported = false;\n        }\n    } else if (nAgt.indexOf('Safari') !== -1) {\n        // In Safari, the true version is after \"Safari\" or after \"Version\"\n        verOffset = nAgt.indexOf('Safari');\n        browserName = SAFARI;\n        fullVersion = nAgt.substring(verOffset + 7);\n        if (nAgt.indexOf('Version') !== -1) {\n            verOffset = nAgt.indexOf('Version');\n            fullVersion = nAgt.substring(verOffset + 8);\n        }\n        majorVersion = parseInt(fullVersion, 10);\n        if (majorVersion < SAFARI_LOWEST_VERSION) {\n            supported = false;\n        }\n    } else if (nAgt.indexOf('Firefox') !== -1) {\n        // In Firefox, the true version is after \"Firefox\"\n        verOffset = nAgt.indexOf('Firefox');\n        browserName = FIREFOX;\n        fullVersion = nAgt.substring(verOffset + 8);\n        majorVersion = parseInt(fullVersion, 10);\n        if (majorVersion < FIREFOX_LOWEST_VERSION) {\n            supported = false;\n        }\n    } else {\n        // In most other browsers, \"name/version\" is at the end of userAgent\n        browserName = 'Unsupported';\n        fullVersion = 0;\n        supported = false;\n    }\n    /* eslint-enable no-negated-condition */\n\n    if (!supported) {\n        var unsupportedBrowserErrorString = 'You are currently using ' + browserName + ' v.' + majorVersion + '. ' + 'This web browser is not officially supported by TEAMMATES. ' + 'In case this web browser does not display the webpage correctly, ' + 'you may wish to view it in the following supported browsers: <br>' + '<table>' + '<tr>' + ('<td width=\"50%\"> - ' + MSIE + ' ' + MSIE_LOWEST_VERSION + '+</td>') + ('<td> - ' + CHROME + ' ' + CHROME_LOWEST_VERSION + '+</td>') + '</tr>' + '<tr>' + ('<td> - ' + FIREFOX + ' ' + FIREFOX_LOWEST_VERSION + '+</td>') + ('<td> - ' + SAFARI + ' ' + SAFARI_LOWEST_VERSION + '+</td>') + '</tr>' + '</table>';\n\n        var message = $('#browserMessage');\n        message.css('display', 'block');\n        message.html(unsupportedBrowserErrorString);\n    }\n}\n\nexports.checkBrowserVersion = checkBrowserVersion;//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiLi9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL2NvbW1vbi9jaGVja0Jyb3dzZXJWZXJzaW9uLmpzLmpzIiwic291cmNlcyI6WyJ3ZWJwYWNrOi8vL3NyYy9tYWluL3dlYmFwcC9kZXYvanMvY29tbW9uL2NoZWNrQnJvd3NlclZlcnNpb24uanM/MmVhNSJdLCJzb3VyY2VzQ29udGVudCI6WyIvLyBCcm93c2VyIENvbXBhdGliaWxpdHkgYW5kIHN1cHBvcnRcbmNvbnN0IE1TSUUgPSAnTWljcm9zb2Z0IEludGVybmV0IEV4cGxvcmVyJztcbmNvbnN0IE1TSUVfTE9XRVNUX1ZFUlNJT04gPSA5O1xuY29uc3QgQ0hST01FID0gJ0Nocm9tZSc7XG5jb25zdCBDSFJPTUVfTE9XRVNUX1ZFUlNJT04gPSAxNTtcbmNvbnN0IEZJUkVGT1ggPSAnRmlyZWZveCc7XG5jb25zdCBGSVJFRk9YX0xPV0VTVF9WRVJTSU9OID0gMTI7XG5jb25zdCBTQUZBUkkgPSAnU2FmYXJpJztcbmNvbnN0IFNBRkFSSV9MT1dFU1RfVkVSU0lPTiA9IDQ7XG5cbi8qKlxuICogRnVuY3Rpb24gdG8gY2hlY2sgYnJvd3NlciB2ZXJzaW9uIGFuZCBhbGVydCBpZiBicm93c2VyIHZlcnNpb24gaXMgbG93ZXIgdGhhbiBzdXBwb3J0ZWRcbiAqIEFkYXB0ZWQgZnJvbSBodHRwOi8vd3d3LmphdmFzY3JpcHRlci5uZXQvZmFxL2Jyb3dzZXJuLmh0bVxuICpcbiAqL1xuXG5mdW5jdGlvbiBjaGVja0Jyb3dzZXJWZXJzaW9uKCkge1xuICAgIGNvbnN0IG5BZ3QgPSBuYXZpZ2F0b3IudXNlckFnZW50O1xuICAgIGxldCBicm93c2VyTmFtZSA9IG5hdmlnYXRvci5hcHBOYW1lO1xuICAgIGxldCBmdWxsVmVyc2lvbiA9IHBhcnNlRmxvYXQobmF2aWdhdG9yLmFwcFZlcnNpb24pO1xuICAgIGxldCBtYWpvclZlcnNpb24gPSBwYXJzZUludChuYXZpZ2F0b3IuYXBwVmVyc2lvbiwgMTApO1xuICAgIGxldCB2ZXJPZmZzZXQ7XG4gICAgbGV0IHN1cHBvcnRlZCA9IHRydWU7XG5cbiAgICAvKiBlc2xpbnQtZGlzYWJsZSBuby1uZWdhdGVkLWNvbmRpdGlvbiAqLyAvLyB1c2FnZSBvZiAuY29udGFpbnMoKSBlcXVpdmFsZW50IHJlcXVpcmVzICE9PVxuICAgIGlmIChuQWd0LmluZGV4T2YoJ01TSUUnKSAhPT0gLTEpIHtcbiAgICAgICAgLy8gSW4gTVNJRSwgdGhlIHRydWUgdmVyc2lvbiBpcyBhZnRlciBcIk1TSUVcIiBpbiB1c2VyQWdlbnRcbiAgICAgICAgdmVyT2Zmc2V0ID0gbkFndC5pbmRleE9mKCdNU0lFJyk7XG4gICAgICAgIGJyb3dzZXJOYW1lID0gTVNJRTtcbiAgICAgICAgZnVsbFZlcnNpb24gPSBuQWd0LnN1YnN0cmluZyh2ZXJPZmZzZXQgKyA1KTtcbiAgICAgICAgbWFqb3JWZXJzaW9uID0gcGFyc2VJbnQoZnVsbFZlcnNpb24sIDEwKTtcbiAgICAgICAgaWYgKG1ham9yVmVyc2lvbiA8IE1TSUVfTE9XRVNUX1ZFUlNJT04pIHtcbiAgICAgICAgICAgIHN1cHBvcnRlZCA9IGZhbHNlO1xuICAgICAgICB9XG4gICAgfSBlbHNlIGlmIChuQWd0LmluZGV4T2YoJ0Nocm9tZScpICE9PSAtMSkge1xuICAgICAgICAvLyBJbiBDaHJvbWUsIHRoZSB0cnVlIHZlcnNpb24gaXMgYWZ0ZXIgXCJDaHJvbWVcIlxuICAgICAgICB2ZXJPZmZzZXQgPSBuQWd0LmluZGV4T2YoJ0Nocm9tZScpO1xuICAgICAgICBicm93c2VyTmFtZSA9IENIUk9NRTtcbiAgICAgICAgZnVsbFZlcnNpb24gPSBuQWd0LnN1YnN0cmluZyh2ZXJPZmZzZXQgKyA3KTtcbiAgICAgICAgbWFqb3JWZXJzaW9uID0gcGFyc2VJbnQoZnVsbFZlcnNpb24sIDEwKTtcbiAgICAgICAgaWYgKG1ham9yVmVyc2lvbiA8IENIUk9NRV9MT1dFU1RfVkVSU0lPTikge1xuICAgICAgICAgICAgc3VwcG9ydGVkID0gZmFsc2U7XG4gICAgICAgIH1cbiAgICB9IGVsc2UgaWYgKG5BZ3QuaW5kZXhPZignU2FmYXJpJykgIT09IC0xKSB7XG4gICAgICAgIC8vIEluIFNhZmFyaSwgdGhlIHRydWUgdmVyc2lvbiBpcyBhZnRlciBcIlNhZmFyaVwiIG9yIGFmdGVyIFwiVmVyc2lvblwiXG4gICAgICAgIHZlck9mZnNldCA9IG5BZ3QuaW5kZXhPZignU2FmYXJpJyk7XG4gICAgICAgIGJyb3dzZXJOYW1lID0gU0FGQVJJO1xuICAgICAgICBmdWxsVmVyc2lvbiA9IG5BZ3Quc3Vic3RyaW5nKHZlck9mZnNldCArIDcpO1xuICAgICAgICBpZiAobkFndC5pbmRleE9mKCdWZXJzaW9uJykgIT09IC0xKSB7XG4gICAgICAgICAgICB2ZXJPZmZzZXQgPSBuQWd0LmluZGV4T2YoJ1ZlcnNpb24nKTtcbiAgICAgICAgICAgIGZ1bGxWZXJzaW9uID0gbkFndC5zdWJzdHJpbmcodmVyT2Zmc2V0ICsgOCk7XG4gICAgICAgIH1cbiAgICAgICAgbWFqb3JWZXJzaW9uID0gcGFyc2VJbnQoZnVsbFZlcnNpb24sIDEwKTtcbiAgICAgICAgaWYgKG1ham9yVmVyc2lvbiA8IFNBRkFSSV9MT1dFU1RfVkVSU0lPTikge1xuICAgICAgICAgICAgc3VwcG9ydGVkID0gZmFsc2U7XG4gICAgICAgIH1cbiAgICB9IGVsc2UgaWYgKG5BZ3QuaW5kZXhPZignRmlyZWZveCcpICE9PSAtMSkge1xuICAgICAgICAvLyBJbiBGaXJlZm94LCB0aGUgdHJ1ZSB2ZXJzaW9uIGlzIGFmdGVyIFwiRmlyZWZveFwiXG4gICAgICAgIHZlck9mZnNldCA9IG5BZ3QuaW5kZXhPZignRmlyZWZveCcpO1xuICAgICAgICBicm93c2VyTmFtZSA9IEZJUkVGT1g7XG4gICAgICAgIGZ1bGxWZXJzaW9uID0gbkFndC5zdWJzdHJpbmcodmVyT2Zmc2V0ICsgOCk7XG4gICAgICAgIG1ham9yVmVyc2lvbiA9IHBhcnNlSW50KGZ1bGxWZXJzaW9uLCAxMCk7XG4gICAgICAgIGlmIChtYWpvclZlcnNpb24gPCBGSVJFRk9YX0xPV0VTVF9WRVJTSU9OKSB7XG4gICAgICAgICAgICBzdXBwb3J0ZWQgPSBmYWxzZTtcbiAgICAgICAgfVxuICAgIH0gZWxzZSB7XG4gICAgICAgIC8vIEluIG1vc3Qgb3RoZXIgYnJvd3NlcnMsIFwibmFtZS92ZXJzaW9uXCIgaXMgYXQgdGhlIGVuZCBvZiB1c2VyQWdlbnRcbiAgICAgICAgYnJvd3Nlck5hbWUgPSAnVW5zdXBwb3J0ZWQnO1xuICAgICAgICBmdWxsVmVyc2lvbiA9IDA7XG4gICAgICAgIHN1cHBvcnRlZCA9IGZhbHNlO1xuICAgIH1cbiAgICAvKiBlc2xpbnQtZW5hYmxlIG5vLW5lZ2F0ZWQtY29uZGl0aW9uICovXG5cbiAgICBpZiAoIXN1cHBvcnRlZCkge1xuICAgICAgICBjb25zdCB1bnN1cHBvcnRlZEJyb3dzZXJFcnJvclN0cmluZyA9XG4gICAgICAgICAgICBgWW91IGFyZSBjdXJyZW50bHkgdXNpbmcgJHticm93c2VyTmFtZX0gdi4ke21ham9yVmVyc2lvbn0uIGBcbiAgICAgICAgICAgICsgJ1RoaXMgd2ViIGJyb3dzZXIgaXMgbm90IG9mZmljaWFsbHkgc3VwcG9ydGVkIGJ5IFRFQU1NQVRFUy4gJ1xuICAgICAgICAgICAgKyAnSW4gY2FzZSB0aGlzIHdlYiBicm93c2VyIGRvZXMgbm90IGRpc3BsYXkgdGhlIHdlYnBhZ2UgY29ycmVjdGx5LCAnXG4gICAgICAgICAgICArICd5b3UgbWF5IHdpc2ggdG8gdmlldyBpdCBpbiB0aGUgZm9sbG93aW5nIHN1cHBvcnRlZCBicm93c2VyczogPGJyPidcbiAgICAgICAgICAgICsgJzx0YWJsZT4nXG4gICAgICAgICAgICAgICAgKyAnPHRyPidcbiAgICAgICAgICAgICAgICAgICAgKyBgPHRkIHdpZHRoPVwiNTAlXCI+IC0gJHtNU0lFfSAke01TSUVfTE9XRVNUX1ZFUlNJT059KzwvdGQ+YFxuICAgICAgICAgICAgICAgICAgICArIGA8dGQ+IC0gJHtDSFJPTUV9ICR7Q0hST01FX0xPV0VTVF9WRVJTSU9OfSs8L3RkPmBcbiAgICAgICAgICAgICAgICArICc8L3RyPidcbiAgICAgICAgICAgICAgICArICc8dHI+J1xuICAgICAgICAgICAgICAgICAgICArIGA8dGQ+IC0gJHtGSVJFRk9YfSAke0ZJUkVGT1hfTE9XRVNUX1ZFUlNJT059KzwvdGQ+YFxuICAgICAgICAgICAgICAgICAgICArIGA8dGQ+IC0gJHtTQUZBUkl9ICR7U0FGQVJJX0xPV0VTVF9WRVJTSU9OfSs8L3RkPmBcbiAgICAgICAgICAgICAgICArICc8L3RyPidcbiAgICAgICAgICAgICsgJzwvdGFibGU+JztcblxuICAgICAgICBjb25zdCBtZXNzYWdlID0gJCgnI2Jyb3dzZXJNZXNzYWdlJyk7XG4gICAgICAgIG1lc3NhZ2UuY3NzKCdkaXNwbGF5JywgJ2Jsb2NrJyk7XG4gICAgICAgIG1lc3NhZ2UuaHRtbCh1bnN1cHBvcnRlZEJyb3dzZXJFcnJvclN0cmluZyk7XG4gICAgfVxufVxuXG5leHBvcnQge1xuICAgIGNoZWNrQnJvd3NlclZlcnNpb24sXG59O1xuIl0sIm1hcHBpbmdzIjoiOzs7OztBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7Ozs7OztBQU1BO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQWVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUVBIiwic291cmNlUm9vdCI6IiJ9\n//# sourceURL=webpack-internal:///./src/main/webapp/dev/js/common/checkBrowserVersion.js\n");

/***/ }),

/***/ "./src/main/webapp/dev/js/main/index.js":
/*!**********************************************!*\
  !*** ./src/main/webapp/dev/js/main/index.js ***!
  \**********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\nObject.defineProperty(exports, \"__esModule\", {\n    value: true\n});\nexports.submissionCounter = undefined;\n\nvar _checkBrowserVersion = __webpack_require__(/*! ../common/checkBrowserVersion */ \"./src/main/webapp/dev/js/common/checkBrowserVersion.js\");\n\n// TESTIMONIALS\n/* eslint-disable max-len */ // testimonials are better left off as is\nvar TESTIMONIALS = ['Congratulations for creating and managing such a wonderful and useful tool. I am planning to use for all the subjects I am teaching from now after getting fantastic feedback about this tool from my students. <br>- Faculty user, Australia', 'I just wanted to let you know that TEAMMATES has been a great success!  Students love it. <br>-Faculty user, USA', 'I had such a great experience with TEAMMATES in the previous semester that I am back for more! <br>-Faculty user, Pakistan', 'Thank you for this. I think it is brilliant. <br>-Faculty user, Canada', 'I found the TEAMMATES system really easy to use. On the whole a very positive experience. Using TEAMMATES certainly helps with one of the main potential problems of group-based assessments. <br>-Faculty user, Singapore', 'I find it really great and so simple to use. <br>-Faculty user, Austria', 'These peer evaluations will be perfect for classes.  I can already see that this is going to be an excellent tool as I need the teams to evaluate each other on a weekly basis.  Adding a new evaluation item and the questions/response criteria is so easy through your system. <br>-Faculty user, USA', 'Thank you for building such a wonderful tool. <br>-Faculty user, Canada', 'I would absolutely recommend TEAMMATES. I haven\\'t seen anything that\\'s better, as well as being open source. It works very well for us. <br>-Faculty user, UK', 'I just started exploring TEAMMATES and am very impressed. Wish I discovered it earlier. <br>-Faculty user, Singapore'];\n/* eslint-enable max-len */\nvar LOOP_INTERVAL = '5000'; // in milliseconds\nvar CURRENT_TESTIMONIAL = 0;\n\n// Format large number with commas\nfunction formatNumber(n) {\n    var number = String(n);\n    var expression = /(\\d+)(\\d{3})/;\n    while (expression.test(number)) {\n        number = number.replace(expression, '$1,$2');\n    }\n    return number;\n}\n\nfunction submissionCounter(currentDate, baseDate, submissionPerHour, baseCount) {\n    var errorMsg = 'Thousands of';\n    if (!currentDate || !baseDate) {\n        return errorMsg;\n    }\n    var currBaseDateDifference = currentDate - baseDate;\n    if (currBaseDateDifference < 0) {\n        return errorMsg;\n    }\n\n    var hr = currBaseDateDifference / 60 / 60 / 1000; // convert from millisecond to hour\n    var numberOfSubmissions = Math.floor(hr * submissionPerHour);\n    numberOfSubmissions += baseCount;\n    return formatNumber(numberOfSubmissions);\n}\n\n// looping through all the testimonials\nfunction loopTestimonials() {\n    var tc = $('#testimonialContainer');\n\n    // intended null checking and early return, to prevent constant failures in JavaScript tests\n    if (tc.length === 0) {\n        return;\n    }\n\n    tc.html(TESTIMONIALS[CURRENT_TESTIMONIAL]);\n    CURRENT_TESTIMONIAL = (CURRENT_TESTIMONIAL + 1) % TESTIMONIALS.length;\n}\n\n// Setting submission count at page load\n$('document').ready(function () {\n    // Parameters for the estimation calculation\n    var baseDate = new Date('Apr 8, 2018 00:00:00'); // The date the parameters were adjusted\n    var baseCount = 10000000; // The submission count on the above date\n    var submissionPerHour = 128; // The rate at which the submission count is growing\n\n    // set the submission count in the page\n    var currentDate = new Date();\n    $('#submissionsNumber').html(submissionCounter(currentDate, baseDate, submissionPerHour, baseCount));\n\n    loopTestimonials();\n    window.setInterval(loopTestimonials, LOOP_INTERVAL);\n\n    (0, _checkBrowserVersion.checkBrowserVersion)();\n});\n\nexports.submissionCounter = submissionCounter;//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiLi9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL21haW4vaW5kZXguanMuanMiLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vc3JjL21haW4vd2ViYXBwL2Rldi9qcy9tYWluL2luZGV4LmpzPzZhNDkiXSwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHtcbiAgICBjaGVja0Jyb3dzZXJWZXJzaW9uLFxufSBmcm9tICcuLi9jb21tb24vY2hlY2tCcm93c2VyVmVyc2lvbic7XG5cbi8vIFRFU1RJTU9OSUFMU1xuLyogZXNsaW50LWRpc2FibGUgbWF4LWxlbiAqLyAvLyB0ZXN0aW1vbmlhbHMgYXJlIGJldHRlciBsZWZ0IG9mZiBhcyBpc1xuY29uc3QgVEVTVElNT05JQUxTID0gW1xuICAgICdDb25ncmF0dWxhdGlvbnMgZm9yIGNyZWF0aW5nIGFuZCBtYW5hZ2luZyBzdWNoIGEgd29uZGVyZnVsIGFuZCB1c2VmdWwgdG9vbC4gSSBhbSBwbGFubmluZyB0byB1c2UgZm9yIGFsbCB0aGUgc3ViamVjdHMgSSBhbSB0ZWFjaGluZyBmcm9tIG5vdyBhZnRlciBnZXR0aW5nIGZhbnRhc3RpYyBmZWVkYmFjayBhYm91dCB0aGlzIHRvb2wgZnJvbSBteSBzdHVkZW50cy4gPGJyPi0gRmFjdWx0eSB1c2VyLCBBdXN0cmFsaWEnLFxuICAgICdJIGp1c3Qgd2FudGVkIHRvIGxldCB5b3Uga25vdyB0aGF0IFRFQU1NQVRFUyBoYXMgYmVlbiBhIGdyZWF0IHN1Y2Nlc3MhICBTdHVkZW50cyBsb3ZlIGl0LiA8YnI+LUZhY3VsdHkgdXNlciwgVVNBJyxcbiAgICAnSSBoYWQgc3VjaCBhIGdyZWF0IGV4cGVyaWVuY2Ugd2l0aCBURUFNTUFURVMgaW4gdGhlIHByZXZpb3VzIHNlbWVzdGVyIHRoYXQgSSBhbSBiYWNrIGZvciBtb3JlISA8YnI+LUZhY3VsdHkgdXNlciwgUGFraXN0YW4nLFxuICAgICdUaGFuayB5b3UgZm9yIHRoaXMuIEkgdGhpbmsgaXQgaXMgYnJpbGxpYW50LiA8YnI+LUZhY3VsdHkgdXNlciwgQ2FuYWRhJyxcbiAgICAnSSBmb3VuZCB0aGUgVEVBTU1BVEVTIHN5c3RlbSByZWFsbHkgZWFzeSB0byB1c2UuIE9uIHRoZSB3aG9sZSBhIHZlcnkgcG9zaXRpdmUgZXhwZXJpZW5jZS4gVXNpbmcgVEVBTU1BVEVTIGNlcnRhaW5seSBoZWxwcyB3aXRoIG9uZSBvZiB0aGUgbWFpbiBwb3RlbnRpYWwgcHJvYmxlbXMgb2YgZ3JvdXAtYmFzZWQgYXNzZXNzbWVudHMuIDxicj4tRmFjdWx0eSB1c2VyLCBTaW5nYXBvcmUnLFxuICAgICdJIGZpbmQgaXQgcmVhbGx5IGdyZWF0IGFuZCBzbyBzaW1wbGUgdG8gdXNlLiA8YnI+LUZhY3VsdHkgdXNlciwgQXVzdHJpYScsXG4gICAgJ1RoZXNlIHBlZXIgZXZhbHVhdGlvbnMgd2lsbCBiZSBwZXJmZWN0IGZvciBjbGFzc2VzLiAgSSBjYW4gYWxyZWFkeSBzZWUgdGhhdCB0aGlzIGlzIGdvaW5nIHRvIGJlIGFuIGV4Y2VsbGVudCB0b29sIGFzIEkgbmVlZCB0aGUgdGVhbXMgdG8gZXZhbHVhdGUgZWFjaCBvdGhlciBvbiBhIHdlZWtseSBiYXNpcy4gIEFkZGluZyBhIG5ldyBldmFsdWF0aW9uIGl0ZW0gYW5kIHRoZSBxdWVzdGlvbnMvcmVzcG9uc2UgY3JpdGVyaWEgaXMgc28gZWFzeSB0aHJvdWdoIHlvdXIgc3lzdGVtLiA8YnI+LUZhY3VsdHkgdXNlciwgVVNBJyxcbiAgICAnVGhhbmsgeW91IGZvciBidWlsZGluZyBzdWNoIGEgd29uZGVyZnVsIHRvb2wuIDxicj4tRmFjdWx0eSB1c2VyLCBDYW5hZGEnLFxuICAgICdJIHdvdWxkIGFic29sdXRlbHkgcmVjb21tZW5kIFRFQU1NQVRFUy4gSSBoYXZlblxcJ3Qgc2VlbiBhbnl0aGluZyB0aGF0XFwncyBiZXR0ZXIsIGFzIHdlbGwgYXMgYmVpbmcgb3BlbiBzb3VyY2UuIEl0IHdvcmtzIHZlcnkgd2VsbCBmb3IgdXMuIDxicj4tRmFjdWx0eSB1c2VyLCBVSycsXG4gICAgJ0kganVzdCBzdGFydGVkIGV4cGxvcmluZyBURUFNTUFURVMgYW5kIGFtIHZlcnkgaW1wcmVzc2VkLiBXaXNoIEkgZGlzY292ZXJlZCBpdCBlYXJsaWVyLiA8YnI+LUZhY3VsdHkgdXNlciwgU2luZ2Fwb3JlJyxcbl07XG4vKiBlc2xpbnQtZW5hYmxlIG1heC1sZW4gKi9cbmNvbnN0IExPT1BfSU5URVJWQUwgPSAnNTAwMCc7IC8vIGluIG1pbGxpc2Vjb25kc1xubGV0IENVUlJFTlRfVEVTVElNT05JQUwgPSAwO1xuXG4vLyBGb3JtYXQgbGFyZ2UgbnVtYmVyIHdpdGggY29tbWFzXG5mdW5jdGlvbiBmb3JtYXROdW1iZXIobikge1xuICAgIGxldCBudW1iZXIgPSBTdHJpbmcobik7XG4gICAgY29uc3QgZXhwcmVzc2lvbiA9IC8oXFxkKykoXFxkezN9KS87XG4gICAgd2hpbGUgKGV4cHJlc3Npb24udGVzdChudW1iZXIpKSB7XG4gICAgICAgIG51bWJlciA9IG51bWJlci5yZXBsYWNlKGV4cHJlc3Npb24sICckMSwkMicpO1xuICAgIH1cbiAgICByZXR1cm4gbnVtYmVyO1xufVxuXG5mdW5jdGlvbiBzdWJtaXNzaW9uQ291bnRlcihjdXJyZW50RGF0ZSwgYmFzZURhdGUsIHN1Ym1pc3Npb25QZXJIb3VyLCBiYXNlQ291bnQpIHtcbiAgICBjb25zdCBlcnJvck1zZyA9ICdUaG91c2FuZHMgb2YnO1xuICAgIGlmICghY3VycmVudERhdGUgfHwgIWJhc2VEYXRlKSB7XG4gICAgICAgIHJldHVybiBlcnJvck1zZztcbiAgICB9XG4gICAgY29uc3QgY3VyckJhc2VEYXRlRGlmZmVyZW5jZSA9IGN1cnJlbnREYXRlIC0gYmFzZURhdGU7XG4gICAgaWYgKGN1cnJCYXNlRGF0ZURpZmZlcmVuY2UgPCAwKSB7XG4gICAgICAgIHJldHVybiBlcnJvck1zZztcbiAgICB9XG5cbiAgICBjb25zdCBociA9IGN1cnJCYXNlRGF0ZURpZmZlcmVuY2UgLyA2MCAvIDYwIC8gMTAwMDsgLy8gY29udmVydCBmcm9tIG1pbGxpc2Vjb25kIHRvIGhvdXJcbiAgICBsZXQgbnVtYmVyT2ZTdWJtaXNzaW9ucyA9IE1hdGguZmxvb3IoaHIgKiBzdWJtaXNzaW9uUGVySG91cik7XG4gICAgbnVtYmVyT2ZTdWJtaXNzaW9ucyArPSBiYXNlQ291bnQ7XG4gICAgcmV0dXJuIGZvcm1hdE51bWJlcihudW1iZXJPZlN1Ym1pc3Npb25zKTtcbn1cblxuLy8gbG9vcGluZyB0aHJvdWdoIGFsbCB0aGUgdGVzdGltb25pYWxzXG5mdW5jdGlvbiBsb29wVGVzdGltb25pYWxzKCkge1xuICAgIGNvbnN0IHRjID0gJCgnI3Rlc3RpbW9uaWFsQ29udGFpbmVyJyk7XG5cbiAgICAvLyBpbnRlbmRlZCBudWxsIGNoZWNraW5nIGFuZCBlYXJseSByZXR1cm4sIHRvIHByZXZlbnQgY29uc3RhbnQgZmFpbHVyZXMgaW4gSmF2YVNjcmlwdCB0ZXN0c1xuICAgIGlmICh0Yy5sZW5ndGggPT09IDApIHtcbiAgICAgICAgcmV0dXJuO1xuICAgIH1cblxuICAgIHRjLmh0bWwoVEVTVElNT05JQUxTW0NVUlJFTlRfVEVTVElNT05JQUxdKTtcbiAgICBDVVJSRU5UX1RFU1RJTU9OSUFMID0gKENVUlJFTlRfVEVTVElNT05JQUwgKyAxKSAlIFRFU1RJTU9OSUFMUy5sZW5ndGg7XG59XG5cbi8vIFNldHRpbmcgc3VibWlzc2lvbiBjb3VudCBhdCBwYWdlIGxvYWRcbiQoJ2RvY3VtZW50JykucmVhZHkoKCkgPT4ge1xuICAgIC8vIFBhcmFtZXRlcnMgZm9yIHRoZSBlc3RpbWF0aW9uIGNhbGN1bGF0aW9uXG4gICAgY29uc3QgYmFzZURhdGUgPSBuZXcgRGF0ZSgnQXByIDgsIDIwMTggMDA6MDA6MDAnKTsgLy8gVGhlIGRhdGUgdGhlIHBhcmFtZXRlcnMgd2VyZSBhZGp1c3RlZFxuICAgIGNvbnN0IGJhc2VDb3VudCA9IDEwMDAwMDAwOyAvLyBUaGUgc3VibWlzc2lvbiBjb3VudCBvbiB0aGUgYWJvdmUgZGF0ZVxuICAgIGNvbnN0IHN1Ym1pc3Npb25QZXJIb3VyID0gMTI4OyAvLyBUaGUgcmF0ZSBhdCB3aGljaCB0aGUgc3VibWlzc2lvbiBjb3VudCBpcyBncm93aW5nXG5cbiAgICAvLyBzZXQgdGhlIHN1Ym1pc3Npb24gY291bnQgaW4gdGhlIHBhZ2VcbiAgICBjb25zdCBjdXJyZW50RGF0ZSA9IG5ldyBEYXRlKCk7XG4gICAgJCgnI3N1Ym1pc3Npb25zTnVtYmVyJykuaHRtbChzdWJtaXNzaW9uQ291bnRlcihjdXJyZW50RGF0ZSwgYmFzZURhdGUsIHN1Ym1pc3Npb25QZXJIb3VyLCBiYXNlQ291bnQpKTtcblxuICAgIGxvb3BUZXN0aW1vbmlhbHMoKTtcbiAgICB3aW5kb3cuc2V0SW50ZXJ2YWwobG9vcFRlc3RpbW9uaWFscywgTE9PUF9JTlRFUlZBTCk7XG5cbiAgICBjaGVja0Jyb3dzZXJWZXJzaW9uKCk7XG59KTtcblxuZXhwb3J0IHtcbiAgICBzdWJtaXNzaW9uQ291bnRlcixcbn07XG4iXSwibWFwcGluZ3MiOiI7Ozs7Ozs7QUFBQTtBQUNBO0FBR0E7QUFDQTtBQUNBO0FBWUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBRUEiLCJzb3VyY2VSb290IjoiIn0=\n//# sourceURL=webpack-internal:///./src/main/webapp/dev/js/main/index.js\n");

/***/ }),

/***/ 12:
/*!****************************************************!*\
  !*** multi ./src/main/webapp/dev/js/main/index.js ***!
  \****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! /home/alessandra/Documenti/LINGI2401/teammates/src/main/webapp/dev/js/main/index.js */"./src/main/webapp/dev/js/main/index.js");


/***/ })

/******/ });