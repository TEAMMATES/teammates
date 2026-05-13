import { Component, Input, OnInit } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails, FeedbackRankOptionsResponseDetails } from '../../../../types/api-output';
import {
  DEFAULT_RANK_OPTIONS_QUESTION_DETAILS,
  DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';
import { RANK_OPTIONS_ANSWER_NOT_SUBMITTED } from '../../../../types/feedback-response-details';

interface RankOption {
  rank: number;
  option: string;
}

/**
 * Rank options question response.
 */
@Component({
  selector: 'tm-rank-options-question-response',
  templateUrl: './rank-options-question-response.component.html',
  imports: [],
})
export class RankOptionsQuestionResponseComponent implements OnInit {
  @Input() responseDetails: FeedbackRankOptionsResponseDetails = DEFAULT_RANK_OPTIONS_RESPONSE_DETAILS();
  @Input() questionDetails: FeedbackRankOptionsQuestionDetails = DEFAULT_RANK_OPTIONS_QUESTION_DETAILS();

  orderedAnswer: RankOption[] = [];

  ngOnInit(): void {
    let arrayOfRanks: RankOption[][] = [];
    for (let i = 0; i < this.questionDetails.options.length; i += 1) {
      const rank: number = this.responseDetails.answers[i];
      if (rank === RANK_OPTIONS_ANSWER_NOT_SUBMITTED) {
        continue;
      }
      arrayOfRanks[rank] = arrayOfRanks[rank] || [];
      arrayOfRanks[rank].push({
        rank,
        option: this.questionDetails.options[i],
      });
    }
    arrayOfRanks = arrayOfRanks.filter(Boolean);
    for (const answers of arrayOfRanks) {
      for (const answer of answers) {
        this.orderedAnswer.push(answer);
      }
    }
  }
}
