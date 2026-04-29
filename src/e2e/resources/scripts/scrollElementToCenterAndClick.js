var element = arguments[0];
var elementRect = element.getBoundingClientRect();
var viewportHeight = window.innerHeight || document.documentElement.clientHeight;
var fixedNavbar = document.querySelector('.navbar.fixed-top');
var fixedNavbarHeight = fixedNavbar ? fixedNavbar.getBoundingClientRect().height : 0;
var safeTop = fixedNavbarHeight + 20;
var centeredTop = (viewportHeight - elementRect.height) / 2;
var targetTop = Math.max(safeTop, centeredTop);
var targetScrollTop = elementRect.top + window.pageYOffset - targetTop;
var maxScrollTop = Math.max(
  0,
  Math.max(document.body.scrollHeight, document.documentElement.scrollHeight) - viewportHeight,
);

window.scrollTo(0, Math.min(Math.max(0, targetScrollTop), maxScrollTop));
