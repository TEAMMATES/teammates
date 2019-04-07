import { Component, Input, OnInit } from '@angular/core';
import { FeedbackRankOptionsQuestionDetails } from '../../../../types/api-output';
import { DEFAULT_RANK_OPTIONS_QUESTION_DETAILS } from '../../../../types/default-question-structs';

/**
 * Instruction of rank options question.
 */
@Component({
  selector: 'tm-rank-options-question-instruction',
  templateUrl: './rank-options-question-instruction.component.html',
  styleUrls: ['./rank-options-question-instruction.component.scss'],
})
export class RankOptionsQuestionInstructionComponent implements OnInit {

  @Input()
  questionDetails: FeedbackRankOptionsQuestionDetails = DEFAULT_RANK_OPTIONS_QUESTION_DETAILS();

  constructor() { }

  ngOnInit(): void {
  }

}
