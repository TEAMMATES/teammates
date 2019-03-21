var seleniumArguments = arguments;
seleniumArguments[0].addEventListener(seleniumArguments[1], function onchange() {
  this.removeEventListener(seleniumArguments[1], onchange);
  document.body.setAttribute(seleniumArguments[2], true);
});
document.body.setAttribute(seleniumArguments[2], false);
