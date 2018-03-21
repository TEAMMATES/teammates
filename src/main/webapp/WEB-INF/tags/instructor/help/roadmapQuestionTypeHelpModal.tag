<%@ tag description="instructorFeedbackEdit - Question Type Help Modal Roadmap" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/help" prefix="instructorHelp" %>

<div class="modal fade question-type-help-modal" id="questionTypeHelpModal_roadmap" tabindex="-1" role="dialog"
     aria-labelledby="roadmapQuestionHelpModalTitle" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span>
          <span class="sr-only">Close</span>
        </button>
        <a href="/instructorHelp.jsp#fbQuestionTypes"
           target="_blank" rel="noopener noreferrer"
           class="question-type-help-modal-glyphicon"
           data-toggle="tooltip" data-placement="top"
           title="Click to open the help page in a new tab">
          <i class="glyphicon glyphicon-info-sign"></i>
        </a>
        <h4 class="modal-title question-type-help-modal-title" id="fbQuestionTypes">
          Available Question Types
        </h4>

      </div>
      <div class="modal-body question-type-help-modal-body" id="questionTypeHelpModalBody_roadmap">
        <instructorHelp:roadmapQuestionTypeHelpBody />
      </div>
    </div>
  </div>
</div>
