/* global prepareInstructorPages:false
          registerResponseCommentsEvent:false
          registerResponseCommentCheckboxEvent:false
          enableHoverToDisplayEditOptions:false
*/

$(document).ready(() => {
    prepareInstructorPages();

    registerResponseCommentsEvent();
    registerResponseCommentCheckboxEvent();
    enableHoverToDisplayEditOptions();
});
