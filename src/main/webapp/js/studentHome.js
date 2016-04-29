$(document).ready(function() {
    $('.table').each(function() {
        util.sortTable($(this),2,null,true, 1);
    });
});