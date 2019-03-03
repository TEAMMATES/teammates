import { Component, OnInit } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackRankOptionsQuestionDetails,
  FeedbackRankOptionsResponseDetails,
} from '../../../../types/api-output';
import { QuestionResponse } from './question-response';

/**
 * Rank options question response.
 */
@Component({
  selector: 'tm-rank-options-question-response',
  templateUrl: './rank-options-question-response.component.html',
  styleUrls: ['./rank-options-question-response.component.scss'],
})
export class RankOptionsQuestionResponseComponent
    extends QuestionResponse<FeedbackRankOptionsResponseDetails, FeedbackRankOptionsQuestionDetails>
    implements OnInit {

  orderedAnswer: string[] = [];

  constructor() {
    super({
      answers: [],
      questionType: FeedbackQuestionType.RANK_OPTIONS,
    }, {
      minOptionsToBeRanked: Number.MIN_VALUE,
      maxOptionsToBeRanked: Number.MIN_VALUE,
      areDuplicatesAllowed: false,
      options: [],
      questionType: FeedbackQuestionType.RANK_OPTIONS,
      questionText: '',
    });
  }

  ngOnInit(): void {
    let arrayOfRanks: any[][] = [];
    for (let i: number = 0; i < this.questionDetails.options.length; i += 1) {
      const rank: number = this.responseDetails.answers[i];
      if (rank === -999) {
        continue;
      }
      arrayOfRanks[rank] = arrayOfRanks[rank] || [];
      arrayOfRanks[rank].push({
        rank,
        option: this.questionDetails.options[i],
      });
    }
    arrayOfRanks = arrayOfRanks.filter((answer: any[]) => answer);
    for (const answers of arrayOfRanks) {
      for (const answer of answers) {
        this.orderedAnswer.push(answer);
      }
    }
  }

}
