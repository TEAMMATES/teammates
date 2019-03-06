var element = arguments[0];
if (element.nodeName === 'INPUT' || element.nodeName === 'TEXTAREA') {
  if (element.readOnly) {
    return {
      errors: {
        detail: 'You may only edit editable elements'
      }
    };
  }
  if (element.disabled) {
    return {
      errors: {
        detail: 'You may only interact with enabled elements'
      }
    };
  }
  element.value='';
} else if (element.isContentEditable) {
  while(element.firstChild) {
    element.removeChild(element.firstChild);
  }
}
return {
  data: {
    detail: 'Success'
  }
};
