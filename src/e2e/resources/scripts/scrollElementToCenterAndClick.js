var elementRect = arguments[0].getBoundingClientRect();
var elementAbsoluteTop = elementRect.top + window.pageYOffset;
var center = elementAbsoluteTop - (window.innerHeight / 2);
window.scrollTo(0, center);
