import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { saveAs } from 'file-saver';
import { TimezoneService } from '../../../../services/timezone.service';
import { FeedbackResponse } from '../../../../types/api-output';
import {
  FeedbackResponseDetailsFactory,
} from '../../../../types/response-details-impl/feedback-response-details-factory';
import {
  QuestionSubmissionFormModel,
} from '../../../components/question-submission-form/question-submission-form-model';

/**
 * Modal to inform the completion of the saving process
 */
@Component({
  selector: 'tm-saving-complete-modal',
  templateUrl: './saving-complete-modal.component.html',
  styleUrls: ['./saving-complete-modal.component.scss'],
})
export class SavingCompleteModalComponent {

  @Input()
  courseId: string = '';

  @Input()
  feedbackSessionName: string = '';

  @Input()
  feedbackSessionTimezone: string = '';

  @Input()
  personEmail: string = '';

  @Input()
  personName: string = '';

  @Input()
  requestIds: Record<string, string> = {};

  @Input()
  questions: QuestionSubmissionFormModel[] = [];

  @Input()
  answers: Record<string, FeedbackResponse[]> = {};

  @Input()
  notYetAnsweredQuestions: number[] = [];

  @Input()
  failToSaveQuestions: Record<number, string> = {}; // Map of question number to error message

  get hasFailToSaveQuestions(): boolean {
    return Object.keys(this.failToSaveQuestions).length !== 0;
  }

  get isAllQuestionSavingFailed(): boolean {
    return Object.keys(this.failToSaveQuestions).length === this.questions.length;
  }

  constructor(public activeModal: NgbActiveModal,
              private timezoneService: TimezoneService) {}

  downloadProofOfSubmission(): void {
    const time: number = new Date().getTime();
    const formattedTime: string = this.timezoneService.formatToString(
        time, this.feedbackSessionTimezone, 'YYYYMMDDHHmmSSZZ');

    const fileContent: string[] = [
      'TEAMMATES Proof of Submission',
      `${time}::${formattedTime}`,
      '==============================',
      `Submitted by: ${this.personName} [${this.personEmail}]`,
      `Course: ${this.courseId}`,
      `Session: ${this.feedbackSessionName}`,
      '',
      '',
      '',
    ];

    for (const question of this.questions) {
      fileContent.push(`${question.questionNumber}: ${question.questionBrief}`);
      fileContent.push(question.feedbackQuestionId);

      if (this.requestIds[question.feedbackQuestionId]) {
        // Question is either answered or skipped
        fileContent.push(this.requestIds[question.feedbackQuestionId]);

        if (this.answers[question.feedbackQuestionId]) {
          for (const answer of this.answers[question.feedbackQuestionId]) {
            fileContent.push(`> ${answer.recipientIdentifier}`);
            fileContent.push(FeedbackResponseDetailsFactory
                .fromApiOutput(answer.responseDetails)
                .getResponseCsvAnswers(question.questionDetails)
                .join(','));
          }
        }
      } else {
        fileContent.push('ERROR! The response submission to this question failed.');
      }

      fileContent.push('');
      fileContent.push('');
    }

    const blob: Blob = new Blob([fileContent.join('\r\n')], { type: 'text/plain' });
    saveAs(blob, `TEAMMATES Proof of Submission - ${time}`);
  }

}
