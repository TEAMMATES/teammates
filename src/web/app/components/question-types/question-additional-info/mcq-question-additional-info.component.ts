import { Component } from '@angular/core';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for MCQ questions.
 */
@Component({
  selector: 'tm-mcq-question-additional-info',
  templateUrl: './mcq-question-additional-info.component.html',
  styleUrls: ['./mcq-question-additional-info.component.scss'],
})
export class McqQuestionAdditionalInfoComponent extends QuestionAdditionalInfo<FeedbackMcqQuestionDetails> {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;

  constructor() {
    super({
      hasAssignedWeights: false,
      mcqWeights: [],
      mcqOtherWeight: 0,
      numOfMcqChoices: 0,
      mcqChoices: [],
      otherEnabled: false,
      generateOptionsFor: FeedbackParticipantType.NONE,
      questionType: FeedbackQuestionType.MCQ,
      questionText: '',
    });
  }

}
