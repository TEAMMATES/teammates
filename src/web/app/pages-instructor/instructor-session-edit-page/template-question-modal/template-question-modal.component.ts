import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackQuestionsService, TemplateQuestion } from '../../../../services/feedback-questions.service';
import { FeedbackQuestion } from '../../../../types/api-output';
import {
  QuestionEditFormModel,
} from '../../../components/question-edit-form/question-edit-form-model';
import { collapseAnim } from '../../../components/teammates-common/collapse-anim';

interface TemplateQuestionModel {
  description: string;
  isShowDetails: boolean;
  isSelected: boolean;
  model: QuestionEditFormModel;
}

/**
 * Template question adding modal.
 */
@Component({
  templateUrl: './template-question-modal.component.html',
  styleUrls: ['./template-question-modal.component.scss'],
  animations: [collapseAnim],
})
export class TemplateQuestionModalComponent {

  templateQuestionModels: TemplateQuestionModel[] = [];

  constructor(public activeModal: NgbActiveModal, private feedbackQuestionsService: FeedbackQuestionsService) {
    this.templateQuestionModels = this.feedbackQuestionsService.getTemplateQuestions()
        .map((template: TemplateQuestion) => ({
          description: template.description,
          isShowDetails: false,
          isSelected: false,
          model: {
            feedbackQuestionId: '',
            questionNumber: template.question.questionNumber,
            questionBrief: template.question.questionBrief,
            questionDescription: template.question.questionDescription,

            isQuestionHasResponses: false,

            questionType: template.question.questionType,
            questionDetails: template.question.questionDetails,

            giverType: template.question.giverType,
            recipientType: template.question.recipientType,

            numberOfEntitiesToGiveFeedbackToSetting: template.question.numberOfEntitiesToGiveFeedbackToSetting,
            customNumberOfEntitiesToGiveFeedbackTo: template.question.customNumberOfEntitiesToGiveFeedbackTo || 1,

            showResponsesTo: template.question.showResponsesTo,
            showGiverNameTo: template.question.showGiverNameTo,
            showRecipientNameTo: template.question.showRecipientNameTo,

            isDeleting: false,
            isDuplicating: false,
            isEditable: false,
            isSaving: false,
            isCollapsed: false,
            isVisibilityChanged: false,
            isFeedbackPathChanged: false,
            isQuestionDetailsChanged: false,
          },
        }));
  }

  get hasAnyTemplateQuestionSelected(): boolean {
    return this.templateQuestionModels.some((model: TemplateQuestionModel) => model.isSelected);
  }

  /**
   * Gets the selected questions.
   */
  getSelectedQuestions(): FeedbackQuestion[] {
    return this.templateQuestionModels
            .filter((model: TemplateQuestionModel) => model.isSelected)
            .map((model: TemplateQuestionModel) => model.model)
            .map((model: QuestionEditFormModel) => ({
              feedbackQuestionId: '',
              questionNumber: 0,
              questionBrief: model.questionBrief,
              questionDescription: model.questionDescription,

              questionType: model.questionType,
              questionDetails: model.questionDetails,

              giverType: model.giverType,
              recipientType: model.recipientType,

              numberOfEntitiesToGiveFeedbackToSetting: model.numberOfEntitiesToGiveFeedbackToSetting,
              customNumberOfEntitiesToGiveFeedbackTo: model.customNumberOfEntitiesToGiveFeedbackTo,

              showResponsesTo: model.showResponsesTo,
              showGiverNameTo: model.showGiverNameTo,
              showRecipientNameTo: model.showRecipientNameTo,
            }));
  }
}
