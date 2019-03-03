import { Component } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for rubric questions.
 */
@Component({
  selector: 'tm-rubric-question-additional-info',
  templateUrl: './rubric-question-additional-info.component.html',
  styleUrls: ['./rubric-question-additional-info.component.scss'],
})
export class RubricQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackRubricQuestionDetails> {

  constructor() {
    super({
      hasAssignedWeights: false,
      numOfRubricChoices: 0,
      rubricChoices: [],
      numOfRubricSubQuestions: 0,
      rubricSubQuestions: [],
      rubricWeightsForEachCell: [],
      rubricDescriptions: [],
      questionType: FeedbackQuestionType.RUBRIC,
      questionText: '',
    });
  }

}
