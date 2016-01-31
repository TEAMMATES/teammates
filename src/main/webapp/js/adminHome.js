function createRowForResultTable(shortName, name, email, institution, isSuccess, status) {
    var result = "<td>" + shortName + "</td>";
    result += "<td>" + name + "</td>";
    result += "<td>" + email + "</td>";
    result += "<td>" + institution + "</td>";
    if (isSuccess) {
        result += "<td>Success</td>";
    } else {
        result += "<td>Fail</td>";
    }
    result += "<td>" + status + "</td>";
    if (isSuccess) {
        result = "<tr class=\"success\">" + result + "</tr>";
    } else {
        result = "<tr class=\"danger\">" + result + "</tr>";
    }
    return result;
}

var paramsCounter = 0;
var paramsList = [];    // list of parameter strings that will be sent via ajax
var instructorDetailsList = [];
var isInputFromFirstPanel = false;

function disableAddInstructorForm() {
    $(".addInstructorBtn").each(function() {
        $(this).html("<img src='/images/ajax-loader.gif'/>");
    });
    $(".addInstructorFormControl").each(function() {
        $(this).prop('disabled', true);
    });
    
}

function enableAddInstructorForm() {
    $(".addInstructorBtn").each(function() {
        $(this).html("Add Instructor");
    });
    $(".addInstructorFormControl").each(function() {
        $(this).prop('disabled', false);
    });
}

function addInstructorByAjaxRecursively() {
    $.ajax({
        type : 'POST',
        url :   "/admin/adminInstructorAccountAdd?" + paramsList[paramsCounter],
        beforeSend : function() {
            disableAddInstructorForm();
        },
        error : function() {
            var rowText = createRowForResultTable("-", "-", "-", "-", false, "Cannot send Ajax Request!");
            $("#addInstructorResultTable tbody").append(rowText);
            if (isInputFromFirstPanel) {
                var instructorsToBeRetried = $("#addInstructorDetailsSingleLine").val() + instructorDetailsList[paramsCounter] + "\n";
                $("#addInstructorDetailsSingleLine").val(instructorsToBeRetried);
            }
            paramsCounter++;
            var panelHeader = "<strong>Result (" + paramsCounter + "/" + paramsList.length + ")</strong>";
            $("#addInstructorResultPanel div.panel-heading").html(panelHeader);
            if (paramsCounter < paramsList.length) {
                addInstructorByAjaxRecursively();
            } else {
                enableAddInstructorForm();
            }
        },
        success : function(data) {
            var rowText = createRowForResultTable(data.instructorShortName, data.instructorName, 
                                                  data.instructorEmail,data.instructorInstitution,
                                                  data.instructorAddingResultForAjax, data.statusForAjax);
            $("#addInstructorResultTable tbody").append(rowText);
            if ((!data.instructorAddingResultForAjax) && (isInputFromFirstPanel)) {
                var instructorsToBeRetried = $("#addInstructorDetailsSingleLine").val() + instructorDetailsList[paramsCounter] + "\n";
                $("#addInstructorDetailsSingleLine").val(instructorsToBeRetried);
            }
            paramsCounter++;
            var panelHeader = "<strong>Result (" + paramsCounter + "/" + paramsList.length + ")</strong>";
            $("#addInstructorResultPanel div.panel-heading").html(panelHeader);
            if (paramsCounter < paramsList.length) {
                addInstructorByAjaxRecursively();
            } else {
                enableAddInstructorForm();
            }
        }
    });
}

function addInstructorByAjax() {
    $("#addInstructorResultPanel").show();    // show the hidden panel
    
    var multipleLineText = $("#addInstructorDetailsSingleLine").val();    // get input from the first panel
    multipleLineText = multipleLineText.trim();
    
    if (multipleLineText.length == 0) {
        var instructorDetails = $("#instructorName").val() + "|" + $("#instructorEmail").val() + "|" + $("#instructorInstitution").val();
        instructorDetailsList = [instructorDetails];
        var params = "instructorshortname=" + $("#instructorShortName").val() +
                     "&instructorname=" + $("#instructorName").val() +
                     "&instructoremail=" + $("#instructorEmail").val() +
                     "&instructorinstitution=" + $("#instructorInstitution").val();
        paramsList = [params];
        isInputFromFirstPanel = false;
    } else {
        instructorDetailsList = multipleLineText.split("\n");
        paramsList = [];
        for(var i = 0; i < instructorDetailsList.length; i++) {
            instructorDetailsList[i] = instructorDetailsList[i].replace(/\t/g,"|");
            paramsList[i] = "instructordetailssingleline=" + instructorDetailsList[i];
        }
        isInputFromFirstPanel = true;
    }
    paramsCounter = 0;
    $("#addInstructorResultTable tbody").html("");    // clear table
    $("#addInstructorDetailsSingleLine").val("");    // clear input form
    $("#addInstructorResultPanel div.panel-heading").html("<strong>Result</strong>");    // clear panel header
    if (paramsList.length > 0) {
        addInstructorByAjaxRecursively();
    }
}