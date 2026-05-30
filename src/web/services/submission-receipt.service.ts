import { Injectable, inject } from '@angular/core';
import { forkJoin, map, Observable, of } from 'rxjs';
import { FileSaveService } from './file-save.service';
import { FeedbackResponsesResponse, FeedbackResponsesService } from './feedback-responses.service';
import { TimezoneService } from './timezone.service';
import { FeedbackResponse } from '../types/api-output';
import { Intent } from '../types/api-request';
import {
  FeedbackResponseRecipient,
  QuestionSubmissionFormModel,
} from '../app/components/question-submission-form/question-submission-form-model';
import { FeedbackResponseDetailsFactory } from '../types/response-details-impl/feedback-response-details-factory';

interface QuestionResponses {
  question: QuestionSubmissionFormModel;
  responses: FeedbackResponse[];
}

export interface SubmissionReceiptRequest {
  questionSubmissionForms: QuestionSubmissionFormModel[];
  intent: Intent;
  key: string;
  moderatedPerson: string;
  feedbackSessionTimezone: string;
  personName: string;
  personEmail: string;
  courseName: string;
  courseId: string;
  feedbackSessionName: string;
}

@Injectable({
  providedIn: 'root',
})
export class SubmissionReceiptService {
  private readonly feedbackResponsesService = inject(FeedbackResponsesService);
  private readonly timezoneService = inject(TimezoneService);
  private readonly fileSaveService = inject(FileSaveService);

  /**
   * Downloads a submission receipt using latest responses from the server.
   * Returns true if there are submitted responses to include in the receipt.
   */
  downloadSubmissionReceipt(request: SubmissionReceiptRequest): Observable<boolean> {
    if (request.questionSubmissionForms.length === 0) {
      return of(false);
    }

    const responseRequests: Observable<QuestionResponses>[] = request.questionSubmissionForms.map(
      (question: QuestionSubmissionFormModel) =>
        this.feedbackResponsesService
          .getFeedbackResponse({
            questionId: question.feedbackQuestionId,
            intent: request.intent,
            key: request.key,
            moderatedPerson: request.moderatedPerson,
          })
          .pipe(
            map((response: FeedbackResponsesResponse) => ({
              question,
              responses: response.responses,
            })),
          ),
    );

    return forkJoin(responseRequests).pipe(
      map((questionsWithResponses: QuestionResponses[]) => {
        const sortedQuestionsWithResponses: QuestionResponses[] = [...questionsWithResponses].sort(
          (a: QuestionResponses, b: QuestionResponses) => a.question.questionNumber - b.question.questionNumber,
        );

        const answeredQuestionsCount: number = sortedQuestionsWithResponses.filter(
          (entry: QuestionResponses) => entry.responses.length > 0,
        ).length;

        if (answeredQuestionsCount === 0) {
          return false;
        }

        const generatedAtMs: number = Date.now();
        const generatedAtFormatted: string = this.timezoneService.formatToString(
          generatedAtMs,
          request.feedbackSessionTimezone,
          'ddd, DD MMM, YYYY, hh:mm A zz',
        );
        const timeForFilename: string = this.timezoneService.formatToString(
          generatedAtMs,
          request.feedbackSessionTimezone,
          'YYYYMMDDHHmmss',
        );

        const fileContent: string[] = [
          'TEAMMATES Submission Receipt',
          '============================',
          `Generated At: ${generatedAtFormatted}`,
          `Submitted by: ${request.personName} (${request.personEmail})`,
          `Course: ${request.courseName} (${request.courseId})`,
          `Session: ${request.feedbackSessionName}`,
          `Questions Answered: ${answeredQuestionsCount} of ${sortedQuestionsWithResponses.length}`,
          '============================',
          '',
        ];

        sortedQuestionsWithResponses.forEach((entry: QuestionResponses) => {
          this.appendQuestionContent(fileContent, entry);
        });

        const blob: Blob = new Blob([fileContent.join('\n')], { type: 'text/plain' });
        this.fileSaveService.saveFile(blob, `TEAMMATES Submission Receipt - ${timeForFilename}.txt`);
        return true;
      }),
    );
  }

  private appendQuestionContent(fileContent: string[], entry: QuestionResponses): void {
    const question: QuestionSubmissionFormModel = entry.question;
    const questionResponses: FeedbackResponse[] = [...entry.responses].sort(
      (a: FeedbackResponse, b: FeedbackResponse) => a.recipientIdentifier.localeCompare(b.recipientIdentifier),
    );

    fileContent.push(`Question ${question.questionNumber}`, `${question.questionBrief}`, '');

    if (questionResponses.length === 0) {
      fileContent.push('No submitted responses for this question.', '', '');
      return;
    }

    questionResponses.forEach((response: FeedbackResponse) => {
      const recipientLabel: string = this.getRecipientLabel(question, response);

      fileContent.push(
        `Recipient: ${recipientLabel}`,
        `Response ID: ${response.feedbackResponseId}`,
        `Answer: ${FeedbackResponseDetailsFactory.fromApiOutput(response.responseDetails)
          .getResponseCsvAnswers(question.questionDetails)
          .join(', ')}`,
      );

      if (response.giverComment) {
        fileContent.push(`Comment by giver: ${response.giverComment}`);
      }

      fileContent.push('');
    });

    fileContent.push('');
  }

  private getRecipientLabel(question: QuestionSubmissionFormModel, response: FeedbackResponse): string {
    const recipient: FeedbackResponseRecipient | undefined = question.recipientList.find(
      (item: FeedbackResponseRecipient) => item.recipientIdentifier === response.recipientIdentifier,
    );
    return recipient?.recipientName
      ? `${recipient.recipientName} [${response.recipientIdentifier}]`
      : response.recipientIdentifier;
  }
}
