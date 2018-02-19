<%@ tag description="instructorFeedbackEdit - Question Type Help Modal For Essay" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/help" prefix="instructorHelp" %>

<div class="modal fade" id="questionTypeHelpModal_essay" tabindex="-1" role="dialog"
     aria-labelledby="essayQuestionHelpModalTitle" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="fbEssay">
          Essay Question
        </h4>
      </div>
      <div class="modal-body question-type-help" id="questionTypeHelpModalBody_essay">
        <instructorHelp:essayQuestionTypeHelpBody />
      </div>
    </div>
  </div>
</div>

