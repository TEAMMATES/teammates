import { Component, OnInit } from '@angular/core';
import { QuestionResponse } from './question-response';
import {
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../../../../types/api-output';
import {
  DEFAULT_RUBRIC_QUESTION_DETAILS,
  DEFAULT_RUBRIC_RESPONSE_DETAILS,
} from '../../../../types/default-question-structs';

/**
 * Rubric question response.
 */
@Component({
  selector: 'tm-rubric-question-response',
  templateUrl: './rubric-question-response.component.html',
  styleUrls: ['./rubric-question-response.component.scss'],
})
export class RubricQuestionResponseComponent
    extends QuestionResponse<FeedbackRubricResponseDetails, FeedbackRubricQuestionDetails>
    implements OnInit {

  answers: any[] = [];

  constructor() {
    super(DEFAULT_RUBRIC_RESPONSE_DETAILS(), DEFAULT_RUBRIC_QUESTION_DETAILS());
  }

  ngOnInit(): void {
    for (const chosenIndex of this.responseDetails.answer) {
      const chosenChoice: string = chosenIndex === -1 ? 'No Response' : this.questionDetails.rubricChoices[chosenIndex];
      this.answers.push({
        index: chosenIndex + 1,
        answer: chosenChoice,
      });
    }
  }

}
