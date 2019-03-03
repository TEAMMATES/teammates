import { Component, OnInit } from '@angular/core';
import {
  FeedbackQuestionType,
  FeedbackRubricQuestionDetails,
  FeedbackRubricResponseDetails,
} from '../../../../types/api-output';
import { QuestionResponse } from './question-response';

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
    super({
      answer: [],
      questionType: FeedbackQuestionType.RUBRIC,
    }, {
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
