import { Component } from '@angular/core';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionAdditionalInfo } from './question-additional-info';

/**
 * Additional info for constant sum questions.
 */
@Component({
  selector: 'tm-constsum-question-additional-info',
  templateUrl: './constsum-question-additional-info.component.html',
  styleUrls: ['./constsum-question-additional-info.component.scss'],
})
export class ConstsumQuestionAdditionalInfoComponent
    extends QuestionAdditionalInfo<FeedbackConstantSumQuestionDetails> {

  constructor() {
    super({
      numOfConstSumOptions: 0,
      constSumOptions: ['', ''],
      distributeToRecipients: false,
      pointsPerOption: false,
      forceUnevenDistribution: false,
      distributePointsFor: FeedbackConstantSumDistributePointsType.NONE,
      points: 100,
      questionType: FeedbackQuestionType.CONSTSUM,
      questionText: '',
    });
  }

}
