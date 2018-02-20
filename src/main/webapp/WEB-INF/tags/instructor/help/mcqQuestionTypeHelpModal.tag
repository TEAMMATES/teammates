<%@ tag description="instructorFeedbackEdit - Question Type Help Modal For Essay" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/help" prefix="instructorHelp" %>

<div class="modal fade" id="questionTypeHelpModal_mcq" tabindex="-1" role="dialog"
     aria-labelledby="mcqQuestionHelpModalTitle" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="fbEssay">
          Multiple-Choice (single answer) Question
        </h4>
      </div>
      <div class="modal-body question-type-help" id="questionTypeHelpModalBody_mcq">
        <instructorHelp:mcqQuestionTypeHelpBody />
      </div>
    </div>
  </div>
</div>

