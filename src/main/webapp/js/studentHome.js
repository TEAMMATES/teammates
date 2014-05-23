$(document).ready(function(){
    $('table.table').each(function(){
        sortTable($(this),2,null,true);
    });
});