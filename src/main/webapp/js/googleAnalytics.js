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
/******/ 	return __webpack_require__(__webpack_require__.s = 11);
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/main/webapp/dev/js/main/googleAnalytics.js":
/*!********************************************************!*\
  !*** ./src/main/webapp/dev/js/main/googleAnalytics.js ***!
  \********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\n/* eslint-disable no-underscore-dangle */ // The variable name is determined by external library (googleAnalytics)\nvar _gaq = _gaq || []; // eslint-disable-line no-use-before-define\n_gaq.push(['_setAccount', 'UA-37652587-1']);\n_gaq.push(['_trackPageview']);\n\n(function () {\n    var ga = document.createElement('script');\n    ga.type = 'text/javascript';\n    ga.async = true;\n    // Always use the ssl version, if not test will fail as local testing uses non HTTPs by default\n    ga.src = 'https://ssl.google-analytics.com/ga.js';\n    var scripts = document.getElementsByTagName('script');\n    for (var i = 0; i < scripts.length; i += 1) {\n        var s = scripts.item(i);\n        if (s.src.endsWith('googleAnalytics.js')) {\n            s.parentNode.insertBefore(ga, s);\n            break;\n        }\n    }\n})();//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiLi9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL21haW4vZ29vZ2xlQW5hbHl0aWNzLmpzLmpzIiwic291cmNlcyI6WyJ3ZWJwYWNrOi8vL3NyYy9tYWluL3dlYmFwcC9kZXYvanMvbWFpbi9nb29nbGVBbmFseXRpY3MuanM/YTRkMSJdLCJzb3VyY2VzQ29udGVudCI6WyIvKiBlc2xpbnQtZGlzYWJsZSBuby11bmRlcnNjb3JlLWRhbmdsZSAqLyAvLyBUaGUgdmFyaWFibGUgbmFtZSBpcyBkZXRlcm1pbmVkIGJ5IGV4dGVybmFsIGxpYnJhcnkgKGdvb2dsZUFuYWx5dGljcylcbmNvbnN0IF9nYXEgPSBfZ2FxIHx8IFtdOyAvLyBlc2xpbnQtZGlzYWJsZS1saW5lIG5vLXVzZS1iZWZvcmUtZGVmaW5lXG5fZ2FxLnB1c2goWydfc2V0QWNjb3VudCcsICdVQS0zNzY1MjU4Ny0xJ10pO1xuX2dhcS5wdXNoKFsnX3RyYWNrUGFnZXZpZXcnXSk7XG5cbihmdW5jdGlvbiAoKSB7XG4gICAgY29uc3QgZ2EgPSBkb2N1bWVudC5jcmVhdGVFbGVtZW50KCdzY3JpcHQnKTtcbiAgICBnYS50eXBlID0gJ3RleHQvamF2YXNjcmlwdCc7XG4gICAgZ2EuYXN5bmMgPSB0cnVlO1xuICAgIC8vIEFsd2F5cyB1c2UgdGhlIHNzbCB2ZXJzaW9uLCBpZiBub3QgdGVzdCB3aWxsIGZhaWwgYXMgbG9jYWwgdGVzdGluZyB1c2VzIG5vbiBIVFRQcyBieSBkZWZhdWx0XG4gICAgZ2Euc3JjID0gJ2h0dHBzOi8vc3NsLmdvb2dsZS1hbmFseXRpY3MuY29tL2dhLmpzJztcbiAgICBjb25zdCBzY3JpcHRzID0gZG9jdW1lbnQuZ2V0RWxlbWVudHNCeVRhZ05hbWUoJ3NjcmlwdCcpO1xuICAgIGZvciAobGV0IGkgPSAwOyBpIDwgc2NyaXB0cy5sZW5ndGg7IGkgKz0gMSkge1xuICAgICAgICBjb25zdCBzID0gc2NyaXB0cy5pdGVtKGkpO1xuICAgICAgICBpZiAocy5zcmMuZW5kc1dpdGgoJ2dvb2dsZUFuYWx5dGljcy5qcycpKSB7XG4gICAgICAgICAgICBzLnBhcmVudE5vZGUuaW5zZXJ0QmVmb3JlKGdhLCBzKTtcbiAgICAgICAgICAgIGJyZWFrO1xuICAgICAgICB9XG4gICAgfVxufSgpKTtcbiJdLCJtYXBwaW5ncyI6Ijs7QUFBQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBIiwic291cmNlUm9vdCI6IiJ9\n//# sourceURL=webpack-internal:///./src/main/webapp/dev/js/main/googleAnalytics.js\n");

/***/ }),

/***/ 11:
/*!**************************************************************!*\
  !*** multi ./src/main/webapp/dev/js/main/googleAnalytics.js ***!
  \**************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! /home/alessandra/Documenti/LINGI2401/teammates/src/main/webapp/dev/js/main/googleAnalytics.js */"./src/main/webapp/dev/js/main/googleAnalytics.js");


/***/ })

/******/ });