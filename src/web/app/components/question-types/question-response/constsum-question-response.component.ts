import { Component, OnInit } from '@angular/core';
import {
  FeedbackConstantSumDistributePointsType,
  FeedbackConstantSumQuestionDetails,
  FeedbackConstantSumResponseDetails,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { QuestionResponse } from './question-response';

/**
 * Constant sum question response.
 */
@Component({
  selector: 'tm-constsum-question-response',
  templateUrl: './constsum-question-response.component.html',
  styleUrls: ['./constsum-question-response.component.scss'],
})
export class ConstsumQuestionResponseComponent
    extends QuestionResponse<FeedbackConstantSumResponseDetails, FeedbackConstantSumQuestionDetails>
    implements OnInit {

  optionToAnswer: { [key: string]: number } = {};

  constructor() {
    super({
      answers: [],
      questionType: FeedbackQuestionType.CONSTSUM,
    }, {
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

  ngOnInit(): void {
    for (let i: number = 0; i < this.questionDetails.constSumOptions.length; i += 1) {
      this.optionToAnswer[this.questionDetails.constSumOptions[i]] = this.responseDetails.answers[i];
    }
  }

}
