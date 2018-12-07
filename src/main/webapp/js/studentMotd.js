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
/******/ 	return __webpack_require__(__webpack_require__.s = 38);
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/main/webapp/dev/js/main/studentMotd.js":
/*!****************************************************!*\
  !*** ./src/main/webapp/dev/js/main/studentMotd.js ***!
  \****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
eval("\n\n/**\n * Contains functions related to student MOTD.\n */\nfunction fetchMotd(motdUrl, motdContentSelector, motdContainerSelector) {\n    $.ajax({\n        type: 'GET',\n        url: window.location.origin + '/' + motdUrl,\n        success: function success(data) {\n            var bodyContent = data.match(/<body[^>]*>[\\s\\S]*<\\/body>/gi);\n            $(motdContentSelector).html(bodyContent);\n        },\n        error: function error() {\n            $(motdContainerSelector).html('');\n        }\n    });\n}\n\nfunction bindCloseMotdButton(btnSelector, motdContainerSelector) {\n    $(document).on('click', btnSelector, function () {\n        $(motdContainerSelector).hide();\n    });\n}\n\n$(document).ready(function () {\n    var motdUrl = $('#motd-url').val();\n    fetchMotd(motdUrl, '#student-motd', '#student-motd-container');\n    bindCloseMotdButton('#btn-close-motd', '#student-motd-container');\n});//# sourceURL=[module]\n//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiLi9zcmMvbWFpbi93ZWJhcHAvZGV2L2pzL21haW4vc3R1ZGVudE1vdGQuanMuanMiLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vc3JjL21haW4vd2ViYXBwL2Rldi9qcy9tYWluL3N0dWRlbnRNb3RkLmpzPzkyYWQiXSwic291cmNlc0NvbnRlbnQiOlsiLyoqXG4gKiBDb250YWlucyBmdW5jdGlvbnMgcmVsYXRlZCB0byBzdHVkZW50IE1PVEQuXG4gKi9cbmZ1bmN0aW9uIGZldGNoTW90ZChtb3RkVXJsLCBtb3RkQ29udGVudFNlbGVjdG9yLCBtb3RkQ29udGFpbmVyU2VsZWN0b3IpIHtcbiAgICAkLmFqYXgoe1xuICAgICAgICB0eXBlOiAnR0VUJyxcbiAgICAgICAgdXJsOiBgJHt3aW5kb3cubG9jYXRpb24ub3JpZ2lufS8ke21vdGRVcmx9YCxcbiAgICAgICAgc3VjY2VzcyhkYXRhKSB7XG4gICAgICAgICAgICBjb25zdCBib2R5Q29udGVudCA9IGRhdGEubWF0Y2goLzxib2R5W14+XSo+W1xcc1xcU10qPFxcL2JvZHk+L2dpKTtcbiAgICAgICAgICAgICQobW90ZENvbnRlbnRTZWxlY3RvcikuaHRtbChib2R5Q29udGVudCk7XG4gICAgICAgIH0sXG4gICAgICAgIGVycm9yKCkge1xuICAgICAgICAgICAgJChtb3RkQ29udGFpbmVyU2VsZWN0b3IpLmh0bWwoJycpO1xuICAgICAgICB9LFxuICAgIH0pO1xufVxuXG5mdW5jdGlvbiBiaW5kQ2xvc2VNb3RkQnV0dG9uKGJ0blNlbGVjdG9yLCBtb3RkQ29udGFpbmVyU2VsZWN0b3IpIHtcbiAgICAkKGRvY3VtZW50KS5vbignY2xpY2snLCBidG5TZWxlY3RvciwgKCkgPT4ge1xuICAgICAgICAkKG1vdGRDb250YWluZXJTZWxlY3RvcikuaGlkZSgpO1xuICAgIH0pO1xufVxuXG4kKGRvY3VtZW50KS5yZWFkeSgoKSA9PiB7XG4gICAgY29uc3QgbW90ZFVybCA9ICQoJyNtb3RkLXVybCcpLnZhbCgpO1xuICAgIGZldGNoTW90ZChtb3RkVXJsLCAnI3N0dWRlbnQtbW90ZCcsICcjc3R1ZGVudC1tb3RkLWNvbnRhaW5lcicpO1xuICAgIGJpbmRDbG9zZU1vdGRCdXR0b24oJyNidG4tY2xvc2UtbW90ZCcsICcjc3R1ZGVudC1tb3RkLWNvbnRhaW5lcicpO1xufSk7XG4iXSwibWFwcGluZ3MiOiI7O0FBQUE7OztBQUdBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFUQTtBQVdBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBIiwic291cmNlUm9vdCI6IiJ9\n//# sourceURL=webpack-internal:///./src/main/webapp/dev/js/main/studentMotd.js\n");

/***/ }),

/***/ 38:
/*!**********************************************************!*\
  !*** multi ./src/main/webapp/dev/js/main/studentMotd.js ***!
  \**********************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! /home/alessandra/Documenti/LINGI2401/teammates/src/main/webapp/dev/js/main/studentMotd.js */"./src/main/webapp/dev/js/main/studentMotd.js");


/***/ })

/******/ });