import { Component, Input, OnInit } from '@angular/core';
import { FeedbackRubricQuestionDetails, FeedbackRubricResponseDetails } from '../../../../types/api-output';
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
  imports: [],
})
export class RubricQuestionResponseComponent implements OnInit {
  @Input() responseDetails: FeedbackRubricResponseDetails = DEFAULT_RUBRIC_RESPONSE_DETAILS();
  @Input() questionDetails: FeedbackRubricQuestionDetails = DEFAULT_RUBRIC_QUESTION_DETAILS();
  @Input() isStudentPage = false;

  answers: any[] = [];

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
