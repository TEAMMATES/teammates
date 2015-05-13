$(document).ready(function() {
    function setupFsCopyModal() {
        $('#fsCopyModal').on('show.bs.modal', function(event) {
            var button = $(event.relatedTarget); // Button that triggered the modal
            var actionlink = button.data('actionlink');
            var courseid = button.data('courseid');
            var fsname = button.data('fsname');
            
            $.ajax({
                type: 'GET',
                url: actionlink,
                beforeSend: function() {
                    $('#courseList').html("<img class='margin-center-horizontal' src='/images/ajax-loader.gif'/>");
                },
                error: function() {
                    $('#courseList').html('Error retrieving course list.' + 
                        'Please close the dialog window and try again.');
                },
                success: function(data) {
                    var htmlToAppend = "";
                    var coursesTable = data.courses;
                    
                    htmlToAppend += "<div class=\"form-group\">" +
                    "<label for=\"copiedfsname\" class=\"control-label\"> Name for copied sessions </label>" + 
                    "<input class=\"form-control\" id=\"copiedfsname\" type=\"text\" name=\"copiedfsname\" value=\"" + 
                    fsname + 
                    "\"></div>";
                    
                    for (var i = 0; i < coursesTable.length; i++) {
                        htmlToAppend += "<div class=\"checkbox\">";
                        htmlToAppend += "<label><input type=\"checkbox\" name=\"copiedcoursesid\"";
                        if (String(coursesTable[i].id) === courseid) {
                            htmlToAppend += "value=\"" + coursesTable[i].id + "\"> [" + "<span class=\"text-color-red\">" + coursesTable[i].id + "</span>" + "] : " + coursesTable[i].name;
                            htmlToAppend += "<br><span class=\"text-color-red small\">{Session currently in this course}</span>";
                        } else {
                            htmlToAppend += "value=\"" + coursesTable[i].id + "\"> [" + coursesTable[i].id + "] : " + coursesTable[i].name;
                        }
                        htmlToAppend +=  "</label></div>";
                    }
                    htmlToAppend += "<input type=\"hidden\" name=\"courseid\" value=\"" + courseid + "\">";
                    htmlToAppend += "<input type=\"hidden\" name=\"fsname\" value=\"" + fsname + "\">";
                    
                    $('#courseList').html(htmlToAppend);
                }
            });
        });
    }

    setupFsCopyModal();
    
    //Click event binding for radio buttons
    var radiobuttons = $("label[name='sortby']");
    
    $.each(radiobuttons, function() {
        $(this).click(function (){
            var currentPath = window.location.pathname;
            var query = window.location.search.substring(1);
            var params = {};
            
            var param_values = query.split("&");
            for(var i=0;i<param_values.length;i++){
                var param_value = param_values[i].split("=");
                params[param_value[0]] = param_value[1];
            }

            if ("user" in params == false) {
                params["user"] = $("input[name='user']").val();
            }

            console.log(currentPath+"?user="+params["user"])+"&sortby="+$(this).attr("data");
            window.location.href = currentPath+"?user="+params["user"]+"&sortby="+$(this).attr("data");
        });
    });
});