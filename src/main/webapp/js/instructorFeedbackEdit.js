/**
 * This function is called on edit page load.
 */
function readyFeedbackEditPage(){
    // Hide option tables
    $('.visibilityOptions').hide();
    
    // Bind submit text links
    $('#fsSaveLink').click(function(){
        $('#form_editfeedbacksession').submit();
    });
    $('a[id|=questionsavechangestext]').click(function(){
        $(this).parents('form.form_question').submit();
    });
    
    // Bind submit actions
    $('form[id|=form_editquestion]').submit(function(event) {
        if($(this).attr('editStatus') == "mustDeleteResponses") {
            if (confirm("Editing these fields will result in all existing responses for" +
                    " this question to be deleted. Are you sure you want to continue?") == false) {
                event.stopImmediatePropagation();
                return false;
            }
        }
    });
    $('form.form_question').submit(function(){
        return checkFeedbackQuestion(this);		
    });

    // Bind destructive changes
    $('form[id|=form_editquestion]').find(":input").not('.nonDestructive').change(function() {
        var editStatus = $(this).parents('form').attr('editStatus');
        if(editStatus == "hasResponses") {
            $(this).parents('form').attr('editStatus', "mustDeleteResponses");
        }
    });
    
    // Copy Binding
    bindCopyButton();
    bindCopyEvents();

    // Additional formatting & bindings.
    disableEditFS();
    formatSessionVisibilityGroup();
    formatResponsesVisibilityGroup();
    formatNumberBoxes();
    formatCheckBoxes();
    formatQuestionNumbers();
    collapseIfPrivateSession();
    //document.onmousemove = positiontip;
}

/**
 * Disables the editing of feedback session details.
 */
function disableEditFS(){	
    // Save then disable fields
    getCustomDateTimeFields().each(function(){
        $(this).data('last', $(this).prop('disabled'));
    });
    $('#form_editfeedbacksession').
        find("text,input,button,textarea,select").prop('disabled', true);
}

/**
 * Enables the editing of feedback session details.
 */
function enableEditFS(){
    var $customDateTimeFields = getCustomDateTimeFields();

    $($customDateTimeFields).each(function(){
        $(this).prop('disabled',
                $(this).data('last'));
    });
    $('#form_editfeedbacksession').
        find("text,input,button,textarea,select").
        not($customDateTimeFields).
        not('.disabled').
        prop('disabled', false);
    $('#fsEditLink').hide();
    $('#fsSaveLink').show();
    $('#button_submit_edit').show();
}

function getCustomDateTimeFields(){
    return $('#'+FEEDBACK_SESSION_PUBLISHDATE).
                add('#'+FEEDBACK_SESSION_PUBLISHTIME).
                add('#'+FEEDBACK_SESSION_VISIBLEDATE).
                add('#'+FEEDBACK_SESSION_VISIBLETIME);
}

/**
 * Hides or show visibility checkboxes frame
 * @param elem is the anchor link being clicked on.
 */
function toggleVisibilityOptions(elem){
    $elementParent = $(elem).closest('form');
    $options = $elementParent.find('.visibilityOptions');
    $visibilityMessage = $elementParent.find('.visibilityMessage');

    //enable edit
    $elementParent.find('[id*="questionedittext"]').click();

    if($options.is(':hidden')) {
        giverType = $elementParent.find("select[name='givertype']");
        recipientType = $elementParent.find("select[name='recipienttype']");
        $options.show();
        $visibilityMessage.hide();
        feedbackGiverUpdateVisibilityOptions(giverType);
        feedbackRecipientUpdateVisibilityOptions(recipientType);
    } else {
        $options.hide();
        $visibilityMessage.show();
    }
}

/**
 * Enables editing of question fields and enables the "save changes" button for
 * the given question number, while hiding the edit link. Does the opposite for all other questions.
 * @param number
 */
function enableEdit(qnNumber, maxQuestions) {
    var i = 1;
    while (i < maxQuestions+1) {
        if (qnNumber == i) {
            enableQuestion(i);
        } else {
            disableQuestion(i);
        }
        i++;
    }
    
    return false;
}

/**
 * Enables question fields and "save changes" button for the given question number,
 * and hides the edit link.
 * @param number
 */
function enableQuestion(number){
    $('#questionTable'+number).find('text,button,textarea,select,input').
        not('[name="receiverFollowerCheckbox"]').
        not('.disabled_radio').
        removeAttr("disabled", "disabled");
    $('#questionTable'+number).find('.removeOptionLink').show();
    $('#questionTable'+number).find('.addOptionLink').show();
    
    if($("#generateOptionsCheckbox-"+number).prop("checked")){
        $("#mcqChoiceTable-"+number).hide();
        $("#msqChoiceTable-"+number).hide();
        $("#mcqGenerateForSelect-"+number).prop("disabled", false);
        $("#msqGenerateForSelect-"+number).prop("disabled", false);
    } else {
        $("#mcqChoiceTable-"+number).show();
        $("#msqChoiceTable-"+number).show();
        $("#mcqGenerateForSelect-"+number).prop("disabled", true);
        $("#msqGenerateForSelect-"+number).prop("disabled", true);
    }

    if($("#constSumToRecipients-"+number).val() == "true"){
        $("#constSumOptionTable-"+number).hide();
        $("#constSumOption_Option-"+number).hide();
    } else {
        $("#constSumOptionTable-"+number).show();
        $("#constSumOption_Recipient-"+number).hide();
    }

    if($('#questionTable'+number).parent().find('input[name="questiontype"]').val()=='CONTRIB'){
        fixContribQnGiverRecipient(number);
        setContribQnVisibilityFormat(number);
    }
    
    $('#'+FEEDBACK_QUESTION_EDITTEXT+'-'+number).hide();
    $('#'+FEEDBACK_QUESTION_SAVECHANGESTEXT+'-'+number).show();
    $('#'+'button_question_submit-'+number).show();
    $('#'+FEEDBACK_QUESTION_EDITTYPE+'-'+number).value="edit";
    // $('#questionTable'+number).find('.visibilityOptionsLabel').click();
}

function enableNewQuestion(){
    var newQnSuffix = "New";
    var number = "-1"
    $('#questionTable'+newQnSuffix).find('text,button,textarea,select,input').
        not('[name="receiverFollowerCheckbox"]').
        not('.disabled_radio').
        removeAttr("disabled", "disabled");
    $('#questionTable'+newQnSuffix).find('.removeOptionLink').show();
    $('#questionTable'+newQnSuffix).find('.addOptionLink').show();
    


    if($("#generateOptionsCheckbox-"+number).prop("checked")){
        $("#mcqChoiceTable-"+number).hide();
        $("#msqChoiceTable-"+number).hide();
        $("#mcqGenerateForSelect-"+number).prop("disabled", false);
        $("#msqGenerateForSelect-"+number).prop("disabled", false);
    } else {
        $("#mcqChoiceTable-"+number).show();
        $("#msqChoiceTable-"+number).show();
        $("#mcqGenerateForSelect-"+number).prop("disabled", true);
        $("#msqGenerateForSelect-"+number).prop("disabled", true);
    }       
    
    $('#'+FEEDBACK_QUESTION_EDITTEXT+'-'+number).hide();
    $('#'+FEEDBACK_QUESTION_SAVECHANGESTEXT+'-'+number).show();
    $('#'+'button_question_submit-'+number).show();
    $('#'+FEEDBACK_QUESTION_EDITTYPE+'-'+number).value="edit";
    // $('#questionTable'+number).find('.visibilityOptionsLabel').click();
}

/**
 * Disable question fields and "save changes" button for the given question number,
 * and shows the edit link.
 * @param number
 */
function disableQuestion(number){
    $('#questionTable'+number).find('text,button,textarea,select,input').attr("disabled", "disabled");
    $('#questionTable'+number).find('#mcqAddOptionLink').hide();
    $('#questionTable'+number).find('#msqAddOptionLink').hide();
    $('#questionTable'+number).find('.removeOptionLink').hide();
    $('#'+FEEDBACK_QUESTION_EDITTEXT+'-'+number).show();
    $('#'+FEEDBACK_QUESTION_SAVECHANGESTEXT+'-'+number).hide();
    $('#'+'button_question_submit-'+number).hide();
}

/**
 * Pops up confirmation dialog whether to delete specified question
 * @param question number
 * @returns
 */
function deleteQuestion(number){
    if(number == -1){
        location.reload();
        return false;
    } else if (confirm("Are you sure you want to delete this question?")){
        document.getElementById(FEEDBACK_QUESTION_EDITTYPE+'-'+number).value="delete"; 
        document.getElementById('form_editquestion-'+number).submit();
        return true;
    } else {
        return false;
    }
}

/**
 * Formats all questions to hide the "Number of Recipients Box" 
 * when participant type is not STUDENTS OR TEAMS, and show
 * it when it is. Formats the label for the number box to fit
 * the selection as well.
 */
function formatNumberBoxes(){
    disallowNonNumericEntries($('input.numberOfEntitiesBox'), false, false);
    disallowNonNumericEntries($('input.minScaleBox'), false, true);
    disallowNonNumericEntries($('input.maxScaleBox'), false, true);
    disallowNonNumericEntries($('input.stepBox'), true, false);
    disallowNonNumericEntries($('input.pointsBox'), false, false);
    
    // Binds onChange of recipientType to modify numEntityBox visibility
    $("select[name="+FEEDBACK_QUESTION_RECIPIENTTYPE+"]").each(function(){
        qnNumber = $(this).prop("id").split('-')[1];
        if(qnNumber === undefined) qnNumber = '';
        value = $(this).val();
        formatNumberBox(value,qnNumber);
        tallyCheckboxes(qnNumber);
    }).change(function() {
        qnNumber = $(this).prop("id").split('-')[1];
        if(qnNumber === undefined) qnNumber = '';
        value = $(this).val();
        formatNumberBox(value,qnNumber);
        tallyCheckboxes(qnNumber);
    });
    
}

/**
 * Hides/shows the "Number of Recipients Box" of the question 
 * depending on the participant type and formats the label text for it.
 * @param value, qnNumber
 */
function formatNumberBox(value, qnNumber) {
    if (value == "STUDENTS" || value == "TEAMS") {
        $("div.numberOfEntitiesElements"+qnNumber).show();
        if(value == "STUDENTS") {
            $("span#"+FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text_inner-"+qnNumber).html("students");
        } else {
            $("span#"+FEEDBACK_QUESTION_NUMBEROFENTITIES+"_text_inner-"+qnNumber).html("teams");
        }
    } else {
        $("div.numberOfEntitiesElements"+qnNumber).hide();
    }
    tallyCheckboxes(qnNumber);
}

/**
 * Pushes the values of all checked check boxes for the specified question
 * into the appropriate feedback question parameters.
 * @returns qnNumber
 */
function tallyCheckboxes(qnNumber){
    var checked = [];
    $('.answerCheckbox'+qnNumber+':checked').each(function () {
        checked.push($(this).val());
    });
    $("[name="+FEEDBACK_QUESTION_SHOWRESPONSESTO+"]").val(checked.toString());
    checked = [];
    $('.giverCheckbox'+qnNumber+":checked").each(function () {
         checked.push($(this).val());
    });
    $("[name="+FEEDBACK_QUESTION_SHOWGIVERTO+"]").val(checked.toString());
    checked = [];
    $('.recipientCheckbox'+qnNumber+':checked').each(function () {
         checked.push($(this).val());
    });
    $("[name="+FEEDBACK_QUESTION_SHOWRECIPIENTTO+"]").val(checked.toString());
}

/**
 * Shows the new question div frame and scrolls to it
 */
function showNewQuestionFrame(type){
    copyOptions();
    prepareQuestionForm(type);
    $('#questionTableNew').show();
    enableNewQuestion();
    $('#addNewQuestionTable').hide();
    $('#empty_message').hide();
    $('html, body').animate({scrollTop: $('#frameBodyWrapper')[0].scrollHeight}, 1000);
    $('#questionTableNew').find('.visibilityOptions').hide();
    getVisibilityMessage($('#questionTableNew').find('.visibilityMessageButton'));
}

function prepareQuestionForm(type) {
    switch(type){
    case "TEXT":
        $("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_TEXT);
        $('#mcqForm').hide();
        $('#msqForm').hide();
        $('#numScaleForm').hide();
        $('#constSumForm').hide();
        break;
    case "MCQ":
        $("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+"--1").val(2);
        $("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_MCQ);
        $('#mcqForm').show();
        $('#msqForm').hide();
        $('#numScaleForm').hide();
        $('#constSumForm').hide();
        break;
    case "MSQ":
        $("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+"--1").val(2);
        $("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_MSQ);
        $('#mcqForm').hide();
        $('#msqForm').show();
        $('#numScaleForm').hide();
        $('#constSumForm').hide();
        break;
    case "NUMSCALE":
        $("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_NUMSCALE);
        $('#mcqForm').hide();
        $('#msqForm').hide();
        $('#numScaleForm').show();
        $('#'+FEEDBACK_QUESTION_TEXT).attr("placeholder","e.g. Rate the class from 1 (very bad) to 5 (excellent)");
        $('#constSumForm').hide();
        break;
    case "CONSTSUM_OPTION":
        $("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+"--1").val(2);
        $("#"+FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS+"--1").val("false");
        $("#constSumOption_Recipient"+"--1").hide();
        $("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION);
        $('#mcqForm').hide();
        $('#msqForm').hide();
        $('#numScaleForm').hide();
        $('#constSumForm').show();
        $('#questionTypeChoice').val('CONSTSUM');
        break;
    case "CONSTSUM_RECIPIENT":
        $("#"+FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS+"--1").val("true");
        $("#constSumOption_Option"+"--1").hide();
        hideConstSumOptionTable(-1);
        $("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT);
        $('#mcqForm').hide();
        $('#msqForm').hide();
        $('#numScaleForm').hide();
        $('#constSumForm').show();
        $('#questionTypeChoice').val('CONSTSUM');
        break;
    case "CONTRIB":
        $("#questionTypeHeader").append(FEEDBACK_QUESTION_TYPENAME_CONTRIB);
        $('#mcqForm').hide();
        $('#msqForm').hide();
        $('#numScaleForm').hide();
        $('#constSumForm').hide();
        fixContribQnGiverRecipient();
        setDefaultContribQnVisibility();
        setContribQnVisibilityFormat();
        break;
    }
}

function setDefaultContribQnVisibility(questionNumber){
    var idSuffix = questionNumber ? (questionNumber) : "New";
    var idSuffix2 = questionNumber ? questionNumber : "";

    $('#questionTable'+idSuffix).find('input.visibilityCheckbox').prop('checked', false);
    //All except STUDENTS can see answer
    $('#questionTable'+idSuffix).find('input.visibilityCheckbox')
                                .filter('[class*="answerCheckbox'+idSuffix2+'"]')
                                .not('[value="STUDENTS"]').prop('checked', true);
    //Only instructor can see giver
    $('#questionTable'+idSuffix).find('input.visibilityCheckbox')
                                .filter('[class*="giverCheckbox'+idSuffix2+'"]')
                                .filter('[value="INSTRUCTORS"]').prop('checked', true);
    //Recipient and instructor can see recipient
    $('#questionTable'+idSuffix).find('input.visibilityCheckbox')
                                .filter('[class*="recipientCheckbox'+idSuffix2+'"]')
                                .filter('[value="INSTRUCTORS"],[value="RECEIVER"]').prop('checked', true);

}

function setContribQnVisibilityFormat(questionNumber){

    var idSuffix = questionNumber ? (questionNumber) : "New";
    var idSuffix2 = questionNumber ? questionNumber : "";

    //Format checkboxes 'Can See Answer' for recipient/giver's team members/recipient's team members must be the same.

    $('#questionTable'+idSuffix).find('input.visibilityCheckbox').off('change');
    
    $('#questionTable'+idSuffix).find('input.visibilityCheckbox').filter("[class*='answerCheckbox']").change(function() {
        if ($(this).prop('checked') == false) {
            if($(this).val() == 'RECEIVER' || $(this).val() == 'OWN_TEAM_MEMBERS' || $(this).val() == 'RECEIVER_TEAM_MEMBERS'){
                $('#questionTable'+idSuffix).find('input.visibilityCheckbox')
                                            .filter("input[class*='giverCheckbox'],input[class*='recipientCheckbox']")
                                            .filter("[value='RECEIVER'],[value='OWN_TEAM_MEMBERS'],[value='RECEIVER_TEAM_MEMBERS']")
                                            .prop('checked', false);
            } else {
                $(this).parent().parent().find("input[class*='giverCheckbox']").prop('checked',false);
                $(this).parent().parent().find("input[class*='recipientCheckbox']").prop('checked',false);
            }
            
        }
        
        if($(this).val() == 'RECEIVER' || $(this).val() == 'OWN_TEAM_MEMBERS' || $(this).val() == 'RECEIVER_TEAM_MEMBERS'){
            $('#questionTable'+idSuffix).find('input.visibilityCheckbox')
                                        .filter("input[name=receiverFollowerCheckbox]")
                                        .prop('checked', $(this).prop('checked'));
        }

        if($(this).val() == "RECEIVER" || $(this).val() == "OWN_TEAM_MEMBERS" || $(this).val() == "RECEIVER_TEAM_MEMBERS"){
            $('#questionTable'+idSuffix).find('input.visibilityCheckbox')
                                        .filter("[class*='answerCheckbox']")
                                        .filter("[value='RECEIVER'],[value='OWN_TEAM_MEMBERS'],[value='RECEIVER_TEAM_MEMBERS']")
                                        .prop('checked',$(this).prop('checked'));
        }
    });
    $('#questionTable'+idSuffix).find('input.visibilityCheckbox').filter("[class*='giverCheckbox']").change(function() {
        if ($(this).is(':checked')) {
            $query = $(this).parent().parent().find("input[class*='answerCheckbox']");
            $query.prop('checked',true);
            $query.trigger('change');
        }
    });
    $('#questionTable'+idSuffix).find('input.visibilityCheckbox').filter("[class*='recipientCheckbox']").change(function() {
        if ($(this).is(':checked')) {
            $query = $(this).parent().parent().find("input[class*='answerCheckbox']");
            $query.prop('checked',true);
            $query.trigger('change');
        }
    });
    $('#questionTable'+idSuffix).find('input.visibilityCheckbox').filter("[name=receiverLeaderCheckbox]").change(function (){
        $(this).parent().parent().find("input[name=receiverFollowerCheckbox]").
                                prop('checked', $(this).prop('checked'));
    });

}

function fixContribQnGiverRecipient(questionNumber){
    var idSuffix;
    idSuffix = questionNumber ? ("-" + questionNumber) : "";

    //Fix giver->recipient to be STUDENT->OWN_TEAM_MEMBERS_INCLUDING_SELF
    $('#givertype'+idSuffix).find('option').not('[value="STUDENTS"]').hide();
    $('#recipienttype'+idSuffix).find('option').not('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').hide();
    $('#givertype'+idSuffix).find('option').filter('[value="STUDENTS"]').attr('selected','selected');
    $('#recipienttype'+idSuffix).find('option').filter('[value="OWN_TEAM_MEMBERS_INCLUDING_SELF"]').attr('selected','selected');
}

function hideConstSumOptionTable(questionNumber){
    var idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }
    $("#"+FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE+idSuffix).hide();
}

function addMcqOption(questionNumber) {
    idOfQuestion = '#form_editquestion-' + questionNumber;
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }
    
    var curNumberOfChoiceCreated = parseInt($("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val());
        
    $(    "<div id=\"mcqOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
        +   "<div class=\"input-group\">"
        +       "<span class=\"input-group-addon\">"
        +          "<input type=\"radio\" disabled=\"disabled\">"
        +       "</span>"
        +       "<input type=\"text\" name=\""+FEEDBACK_QUESTION_MCQCHOICE+"-"+curNumberOfChoiceCreated+"\" "
        +               "id=\""+FEEDBACK_QUESTION_MCQCHOICE+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"form-control mcqOptionTextBox\">"
        +       "<span class=\"input-group-btn\">"
        +           "<button type=\"button\" class=\"btn btn-default removeOptionLink\" id=\"mcqRemoveOptionLink\" "
        +                   "onclick=\"removeMcqOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
        +               "<span class=\"glyphicon glyphicon-remove\"></span>"
        +           "</button>"
        +       "</span>"
        +   "</div>"
        + "</div>"
    ).insertBefore($("#mcqAddOptionRow" + idSuffix));

    $("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val(curNumberOfChoiceCreated+1);
    
    if($(idOfQuestion).attr('editStatus') == "hasResponses") {
        $(idOfQuestion).attr('editStatus', "mustDeleteResponses");
    }
}

function addMsqOption(questionNumber) {
    idOfQuestion = '#form_editquestion-' + questionNumber;
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    var curNumberOfChoiceCreated = parseInt($("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val());
        
    $(   "<div id=\"msqOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
        +   "<div class=\"input-group\">"
        +       "<span class=\"input-group-addon\">"
        +          "<input type=\"checkbox\" disabled=\"disabled\">"
        +       "</span>"
        +       "<input type=\"text\" name=\""+FEEDBACK_QUESTION_MSQCHOICE+"-"+curNumberOfChoiceCreated+"\" "
        +               "id=\""+FEEDBACK_QUESTION_MSQCHOICE+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"form-control msqOptionTextBox\">"
        +       "<span class=\"input-group-btn\">"
        +           "<button type=\"button\" class=\"btn btn-default removeOptionLink\" id=\"msqRemoveOptionLink\" "
        +                   "onclick=\"removeMcqOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
        +               "<span class=\"glyphicon glyphicon-remove\"></span>"
        +           "</button>"
        +       "</span>"
        +   "</div>"
        + "</div>"
    ).insertBefore($("#msqAddOptionRow" + idSuffix));

    $("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val(curNumberOfChoiceCreated+1);
    
    if($(idOfQuestion).attr('editStatus') == "hasResponses") {
        $(idOfQuestion).attr('editStatus', "mustDeleteResponses");
    }
}

function addConstSumOption(questionNumber) {
    idOfQuestion = '#form_editquestion-' + questionNumber;
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }
    
    var curNumberOfChoiceCreated = parseInt($("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val());
        
    $(    "<div id=\"constSumOptionRow-"+curNumberOfChoiceCreated+idSuffix+"\">"
        +   "<div class=\"input-group\">"
        +       "<input type=\"text\" name=\""+FEEDBACK_QUESTION_CONSTSUMOPTION+"-"+curNumberOfChoiceCreated+"\" "
        +               "id=\""+FEEDBACK_QUESTION_CONSTSUMOPTION+"-"+curNumberOfChoiceCreated+idSuffix+"\" class=\"form-control constSumOptionTextBox\">"
        +       "<span class=\"input-group-btn\">"
        +           "<button class=\"btn btn-default removeOptionLink\" id=\"constSumRemoveOptionLink\" "
        +                   "onclick=\"removeConstSumOption("+curNumberOfChoiceCreated+","+questionNumber+")\" tabindex=\"-1\">"
        +               "<span class=\"glyphicon glyphicon-remove\"></span>"
        +           "</button>"
        +       "</span>"
        +   "</div>"
        + "</div>"
    ).insertBefore($("#constSumAddOptionRow" + idSuffix));

    $("#"+FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+idSuffix).val(curNumberOfChoiceCreated+1);
    
    if($(idOfQuestion).attr('editStatus') == "hasResponses") {
        $(idOfQuestion).attr('editStatus', "mustDeleteResponses");
    }
}

function removeMcqOption(index, questionNumber) {
    idOfQuestion = '#form_editquestion-' + questionNumber;
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    $("#mcqOptionRow-"+index+idSuffix).remove();
    
    if($(idOfQuestion).attr('editStatus') == "hasResponses") {
        $(idOfQuestion).attr('editStatus', "mustDeleteResponses");
    }
}

function removeMsqOption(index, questionNumber) {
    idOfQuestion = '#form_editquestion-' + questionNumber;
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    $("#msqOptionRow-"+index+idSuffix).remove();
    
    if($(idOfQuestion).attr('editStatus') == "hasResponses") {
        $(idOfQuestion).attr('editStatus', "mustDeleteResponses");
    }
}

function removeConstSumOption(index, questionNumber) {
    idOfQuestion = '#form_editquestion-' + questionNumber;
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    $("#constSumOptionRow-"+index+idSuffix).remove();
    
    if($(idOfQuestion).attr('editStatus') == "hasResponses") {
        $(idOfQuestion).attr('editStatus', "mustDeleteResponses");
    }
}

function toggleMcqGeneratedOptions(checkbox, questionNumber) {
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    if (checkbox.checked) {
        $("#mcqChoiceTable"+idSuffix).find("input[type=text]").prop('disabled', true);
        $("#mcqChoiceTable"+idSuffix).hide();
        $("#mcqGenerateForSelect"+idSuffix).prop("disabled", false);
        $("#generatedOptions"+idSuffix).attr("value", 
                $("#mcqGenerateForSelect"+idSuffix).prop("value"));
    } else {
        $("#mcqChoiceTable"+idSuffix).find("input[type=text]").prop("disabled", false);
        $("#mcqChoiceTable"+idSuffix).show();
        $("#mcqGenerateForSelect"+idSuffix).prop("disabled", true);
        $("#generatedOptions"+idSuffix).attr("value", "NONE");
    }
}

function changeMcqGenerateFor(questionNumber) {
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    $("#generatedOptions"+idSuffix).attr("value", 
            $("#mcqGenerateForSelect"+idSuffix).prop("value"));
}

function toggleMsqGeneratedOptions(checkbox, questionNumber) {
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    if (checkbox.checked) {
        $("#msqChoiceTable"+idSuffix).find("input[type=text]").prop('disabled', true);
        $("#msqChoiceTable"+idSuffix).hide();
        $("#msqGenerateForSelect"+idSuffix).prop("disabled", false);
        $("#generatedOptions"+idSuffix).attr("value",
                $("#msqGenerateForSelect"+idSuffix).prop("value"));
    } else {
        $("#msqChoiceTable"+idSuffix).find("input[type=text]").prop("disabled", false);
        $("#msqChoiceTable"+idSuffix).show();
        $("#msqGenerateForSelect"+idSuffix).prop("disabled", true);
        $("#generatedOptions"+idSuffix).attr("value", "NONE");
    }
}

function changeMsqGenerateFor(questionNumber) {
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }

    $("#generatedOptions"+idSuffix).attr("value", 
            $("#msqGenerateForSelect"+idSuffix).prop("value"));
}

function updateNumScalePossibleValues(questionNumber) {
    idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }
    
    var min = parseInt($("#minScaleBox"+idSuffix).val());
    var max = parseInt($("#maxScaleBox"+idSuffix).val());
    var step = parseFloat($("#stepBox"+idSuffix).val());
    
    if (max <= min) {
        max = min + 1;
        $("#maxScaleBox"+idSuffix).val(max);
    }
    
    if (step < 0.001) {
        step = 0.001;
        $("#stepBox"+idSuffix).val(step);
    }
    
    var cur = min + step;
    var largestValueInRange = min;
    var possibleValuesCount = 1;
    while ((max - cur) >= -1e-9) {
        largestValueInRange = cur;
        cur += step;
        possibleValuesCount++;
    }

    var possibleValuesString = "";
    if (Math.round(largestValueInRange*1000)/1000 != max) {
        $("#numScalePossibleValues"+idSuffix).css("color","red");
        possibleValuesString = "[The interval " + min.toString() + " - " + max.toString() + " is not divisible by the specified increment.]";

        if(min.toString() == "NaN" || max.toString() == "NaN" || step.toString() == "NaN"){
            possibleValuesString = "[Please enter valid numbers for all the options.]"
        }

        $("#numScalePossibleValues"+idSuffix).text(possibleValuesString);
        return false;
    } else {
        $("#numScalePossibleValues"+idSuffix).css("color","black");
        possibleValuesString = "[Based on the above settings, acceptable responses are: ";
        if (possibleValuesCount > 6) {
            possibleValuesString += min.toString() + ", "
                                    + (Math.round((min + step)*1000)/1000).toString() + ", "
                                    + (Math.round((min + 2*step)*1000)/1000).toString() + ", ..., "
                                    + (Math.round((max - 2*step)*1000)/1000).toString() + ", "
                                    + (Math.round((max - step)*1000)/1000).toString() + ", "
                                    + max.toString();		
        } else {
            possibleValuesString += min.toString();
            cur = min + step;
            while ((max - cur) >= -1e-9) {
                possibleValuesString += ", " + (Math.round(cur*1000)/1000).toString();
                cur += step;
            }
        }
        possibleValuesString += "]";
        $("#numScalePossibleValues"+idSuffix).text(possibleValuesString);
        return true;
    }
}

function updateConstSumPointsValue(questionNumber){
    var idSuffix = (questionNumber > 0) ? ("-" + questionNumber) : "";
    if(questionNumber == -1){
        idSuffix = "--1";
    }
    if($("#"+FEEDBACK_QUESTION_CONSTSUMPOINTS+idSuffix).val() < 1){
        $("#"+FEEDBACK_QUESTION_CONSTSUMPOINTS+idSuffix).val(1);
    }
}

/**
 * Binds each question's check box field such that the user
 * cannot select an invalid combination.
 */
function formatCheckBoxes() {
    $(document).ready(function() {
        // TODO: change class -> name?
        $("input[class*='answerCheckbox']").change(function() {
            if ($(this).prop('checked') == false) {
                $(this).parent().parent().find("input[class*='giverCheckbox']").prop('checked',false);
                $(this).parent().parent().find("input[class*='recipientCheckbox']").prop('checked',false);
            }
        });
        $("input[class*='giverCheckbox']").change(function() {
            if ($(this).is(':checked')) {
                $query = $(this).parent().parent().find("input[class*='answerCheckbox']");
                $query.prop('checked',true);
                $query.trigger('change');
            }
        });
        $("input[class*='recipientCheckbox']").change(function() {
            if ($(this).is(':checked')) {
                $(this).parent().parent().find("input[class*='answerCheckbox']").prop('checked',true);
            }
        });
        $("input[name=receiverLeaderCheckbox]").change(function (){
            $(this).parent().parent().find("input[name=receiverFollowerCheckbox]").
                                    prop('checked', $(this).prop('checked'));
        });
    });
}

/**
 * Copy options(Feedback giver, recipient, and all check boxes 
 * from the previous question
 */
function copyOptions() {
    //There's no need to previous question to copy options from.
    if($("div[class*='questionTable']").size() < 2){
        return;
    }
    
    //FEEDBACK GIVER SETUP
    var $prevGiver = $("select[name='givertype']").eq(-2);
    var currGiver = $("select[name='givertype']").last();
    
    $(currGiver).val($prevGiver.val());
    
    //FEEDBACK RECIPIENT SETUP
    var $prevRecipient = $("select[name='recipienttype']").eq(-2);
    var currRecipient = $("select[name='recipienttype']").last();
    
    $(currRecipient).val($prevRecipient.val());
    
    //NUMBER OF RECIPIENT SETUP
    formatNumberBox($(currRecipient).val(), '');
    var $prevRadioButtons = $("table[class*='questionTable']").eq(-2).find("input[name='numofrecipientstype']");
    var $currRadioButtons = $("table[class*='questionTable']").last().find("input[name='numofrecipientstype']");
    
    $currRadioButtons.each(function (index){
        $(this).prop('checked', $prevRadioButtons.eq(index).prop('checked'));
    });
    
    var $prevNumOfRecipients = $("input[name='numofrecipients']").eq(-2);
    var $currNumOfRecipients = $("input[name='numofrecipients']").last();
    
    $currNumOfRecipients.val($prevNumOfRecipients.val());
    
    //CHECK BOXES SETUP
    var $prevTable = $(".dataTable").eq(-2).find('.visibilityCheckbox');
    var $currTable = $(".dataTable").last().find('.visibilityCheckbox');
    
    $currTable.each(function (index) {
        $(this).prop('checked', $prevTable.eq(index).prop('checked'));
    });
    feedbackGiverUpdateVisibilityOptions(currGiver);
    feedbackRecipientUpdateVisibilityOptions(currRecipient);
}

function enableRow(el,row){
    var visibilityOptions = ($(el).closest('form').find('.visibilityOptions'));
    var table = visibilityOptions.find('table');
    var tdElements = $($(table).children().children()[row]).children();
    if($(tdElements).parent().prop("tagName") == "tr"){
        return;
    }
    $(tdElements).unwrap().wrapAll("<tr>");

}

function disableRow(el,row){
    var visibilityOptions = ($(el).closest('form').find('.visibilityOptions'));
    var table = visibilityOptions.find('table');
    var tdElements = $($(table).children().children()[row]).children();
    if($(tdElements).parent().prop("tagName") == "hide"){
        return; 
    }
    $(tdElements).unwrap().wrapAll("<hide>");
    $(tdElements).parent().hide();

}

function feedbackRecipientUpdateVisibilityOptions(el){
    if($(el).val() == "OWN_TEAM" || $(el).val() == "TEAMS" || $(el).val() == "INSTRUCTORS" || $(el).val() == "OWN_TEAM_MEMBERS"){
        enableRow(el, 1);
        disableRow(el, 3);
        return;
    }else if($(el).val() == "NONE"){
        disableRow(el, 3);
        disableRow(el, 1);
        return;
    }
    
    enableRow(el, 1);
    enableRow(el, 3);
}

function feedbackGiverUpdateVisibilityOptions(el){
    if($(el).val() == "INSTRUCTORS" || $(el).val() == "TEAMS"){
        disableRow(el, 2);
        return;
    }
    enableRow(el, 2);
}

/**
 * Sets the correct initial question number from the value field
 */
function formatQuestionNumbers(){
    var $questions = $("div[class*='questionTable']");
    
    $questions.each(function (index){
        var $selector = $(this).find('.questionNumber');
        $selector.val(index+1);
        if(index != $questions.size()-1){
            $selector.prop('disabled', true);
        }
    });
}

function getQuestionLink(qnNumber) {
    var courseid = $("input[name='courseid']").val();
    var fsname = toParameterFormat($("input[name='fsname']").val());
    
    var questionId = $("#form_editquestion-" + qnNumber)
                        .find("input[name='questionid']").val();
    
    var giverType = $("#givertype-" + qnNumber).val();
    
    var actionUrl = (giverType == "STUDENTS" || giverType == "TEAMS") 
                        ? "/page/studentFeedbackQuestionSubmissionEditPage"
                        : "/page/instructorFeedbackQuestionSubmissionEditPage";
    
    var questionLink =  window.location.protocol + "//" 
                        + window.location.host + actionUrl
                        + "?courseid=" + courseid 
                        + "&fsname=" + fsname 
                        + "&questionid=" + questionId;
    
    setStatusMessage("Link for question " + qnNumber + ": " + questionLink, false);
}

function toParameterFormat(str) {
    return str.replace(/\s/g,"+");
}

function bindCopyButton() {
    $('#button_copy').on('click', function(e){
        e.preventDefault();
        
        var questionRows = $("#copyTableModal >tbody>tr");
        if(questionRows.length == 0){
            setStatusMessage(FEEDBACK_QUESTION_COPY_INVALID, true);
        } else {
            setStatusMessage("", false);
            $('#copyModal').modal('show');
        }
       
        return false;
    });

    $('#button_copy_submit').on('click', function(e){
        e.preventDefault();

        var index = 0;
        var hasRowSelected = false;

        $('#copyTableModal >tbody>tr').each(function(){
            var input = $(this).children('input:first');
            if(typeof input == 'undefined'){
                return true;
            }
            if($(this).hasClass('row-selected')){
                $(input).attr('name', 'questionid-' + index++);
                hasRowSelected = true;
            }
        });

        if(!hasRowSelected){
            setStatusMessage('No questions are selected to be copied', true);
            $('#copyModal').modal('hide');
        } else {
            $('#copyModalForm').submit();
        }


        return false;
    });
}

var numRowsSelected = 0;

function bindCopyEvents() {

    $('#copyTableModal >tbody>tr').on('click', function(e){
        e.preventDefault();
        
        if($(this).hasClass('row-selected')){
            $(this).removeClass('row-selected');
            $(this).children('td:first').html('<input type="checkbox">');
            numRowsSelected--;
        } else {
            $(this).addClass('row-selected');
            $(this).children('td:first').html('<input type="checkbox" checked="checked">');
            numRowsSelected++;
        }

        if(numRowsSelected <= 0){
            $('#button_copy_submit').prop('disabled', true);
        } else {
            $('#button_copy_submit').prop('disabled', false);
        }

        return false;
    });
}

function toggleVisibilityMessage(elem){
    $elementParent = $(elem).closest('form');
    $options = $elementParent.find('.visibilityOptions');
    $visibilityMessage = $elementParent.find('.visibilityMessage');

    giverType = $elementParent.find("select[name='givertype']");
    recipientType = $elementParent.find("select[name='recipienttype']");

    $options.hide();
    $visibilityMessage.html("");
    $disabledInputs = $elementParent.find('input:disabled, select:disabled');
    $disabledInputs.prop('disabled', false);

    feedbackGiverUpdateVisibilityOptions(giverType);
    feedbackRecipientUpdateVisibilityOptions(recipientType);

    getVisibilityMessage(elem);
    $disabledInputs.prop('disabled', true);
}

function getVisibilityMessage(buttonElem){
    var form = $(buttonElem).closest("form");
    var url = "/page/instructorFeedbackQuestionvisibilityMessage";

    eval($(form).attr('onsubmit'));

    var data = $(form[0]).serialize();

    $.ajax({
            type: "POST",
            url: url,
            data: $(form[0]).serialize(),
            success: function(data)
            {
                $(form).find('.visibilityMessage').html(formatVisibilityMessageHtml(data.visibilityMessage));
                $(form).find('.visibilityOptions').hide();
                $(form).find('.visibilityMessage').show();
            },
            error: function(jqXHR, textStatus, errorThrown) 
            {
                console.log('AJAX request failed');
            }
        });

}

function formatVisibilityMessageHtml(visibilityMessage){
    var htmlString = "This is the visibility as seen by the feedback giver.";
    htmlString += "<ul class='background-color-warning'>";
    for(var i=0 ; i<visibilityMessage.length ; i++){
        htmlString += "<li>" + visibilityMessage[i] + "</li>";
    }
    htmlString += "</ul>";
    return htmlString;
}