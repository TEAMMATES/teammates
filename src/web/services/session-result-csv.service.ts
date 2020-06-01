import { Injectable } from '@angular/core';
import {
  InstructorSessionResultSectionType,
} from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import {
  SectionTypeDescriptionPipe,
} from '../app/pages-instructor/instructor-session-result-page/section-type-description.pipe';
import {
  FeedbackQuestion,
  QuestionOutput,
  ResponseOutput,
  SessionResults,
} from '../types/api-output';
import { FeedbackQuestionDetailsFactory } from '../types/question-details-impl/feedback-question-details-factory';
import { FeedbackResponseDetailsFactory } from '../types/response-details-impl/feedback-response-details-factory';
import { CsvHelper } from './csv-helper';
import { FeedbackResponsesService } from './feedback-responses.service';
import { StringHelper } from './string-helper';

/**
 * Service to generate CSV for a feedback session result.
 */
@Injectable({
  providedIn: 'root',
})
export class SessionResultCsvService {

  constructor(private feedbackResponsesService: FeedbackResponsesService) { }

  /**
   * Generates CSV string for a session result.
   */
  getCsvForSessionResult(result: SessionResults,
      isIndicatingMissingResponses: boolean, isShownStats: boolean,
      sectionName?: string, sectionDetail?: InstructorSessionResultSectionType): string {
    const csvRows: string[][] = [];

    csvRows.push(['Course', result.feedbackSession.courseId]);
    csvRows.push(['Session Name', result.feedbackSession.feedbackSessionName]);

    if (sectionName) {
      csvRows.push(['Section Name', sectionName]);
    }
    if (sectionDetail) {
      const descriptionPipe: SectionTypeDescriptionPipe = new SectionTypeDescriptionPipe();
      csvRows.push(['Section View Detail', descriptionPipe.transform(sectionDetail)]);
    }

    this.generateEmptyRow(csvRows);
    this.generateEmptyRow(csvRows);

    // sort questions by question number
    result.questions.sort((a: QuestionOutput, b: QuestionOutput) =>
        a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber);
    // filter responses based on settings
    for (const question of result.questions) {
      const currQuestion: QuestionOutput = JSON.parse(JSON.stringify(question));
      currQuestion.allResponses = currQuestion.allResponses.filter((response: ResponseOutput) => {
        if (sectionName && sectionDetail) {
          return this.feedbackResponsesService.isFeedbackResponsesDisplayedOnSection(
              response, sectionName, sectionDetail);
        }
        return true;
      });
      csvRows.push(...this.generateCsvRowsForQuestion(currQuestion, isIndicatingMissingResponses, isShownStats));
    }

    return CsvHelper.convertCsvContentsToCsvString(csvRows);
  }

  /**
   * Generates CSV rows for a question.
   */
  private generateCsvRowsForQuestion(question: QuestionOutput,
                                      isIndicatingMissingResponses: boolean, isShownStats: boolean): string[][] {
    const csvRows: string[][] = [];

    csvRows.push([`Question ${question.feedbackQuestion.questionNumber}`, question.feedbackQuestion.questionBrief]);
    this.generateEmptyRow(csvRows);

    // TODO add stats
    if (isShownStats) {
      const statsRows: string[][] = this.getQuestionStats();
      if (statsRows.length > 0) {
        csvRows.push(['Summary Statistics']);
        csvRows.push(...statsRows);
        this.generateEmptyRow(csvRows);
        this.generateEmptyRow(csvRows);
      }
    }

    if (!isIndicatingMissingResponses) {
      // remove missing responses if not to show them
      question.allResponses = question.allResponses.filter((response: ResponseOutput) => !response.isMissingResponse);
    }

    const header: string[] = ['Team', "Giver's Full Name", "Giver's Last Name", "Giver's Email", "Recipient's Team",
      "Recipient's Full Name", "Recipient's Last Name", "Recipient's Email",
      ...this.getQuestionSpecificHeaders(question.feedbackQuestion)];
    csvRows.push(header);

    // TODO add participant comment header

    // TODO add instructor comments header

    // sort the responses by giver then recipient
    question.allResponses.sort((responseA: ResponseOutput, responseB: ResponseOutput): number => {
      return responseA.giver.localeCompare(responseB.giver)
          || responseA.recipient.localeCompare(responseB.recipient);
    });

    for (const response of question.allResponses) {
      const giverTeamName: string = StringHelper.removeExtraSpace(response.giverTeam);
      const giverName: string = StringHelper.removeExtraSpace(response.giver);
      const giverLastName: string = response.giverLastName ? StringHelper.removeExtraSpace(response.giverLastName) : '';
      const giverEmail: string = response.giverEmail ? StringHelper.removeExtraSpace(response.giverEmail) : '';
      const recipientTeamName: string = StringHelper.removeExtraSpace(response.recipientTeam);
      const recipientName: string = StringHelper.removeExtraSpace(response.recipient);
      const recipientLastName: string =
          response.recipientLastName ? StringHelper.removeExtraSpace(response.recipientLastName) : '';
      const recipientEmail: string =
          response.recipientEmail ? StringHelper.removeExtraSpace(response.recipientEmail) : '';

      let responseAnswers: string[][] = [];
      if (response.isMissingResponse) {
        responseAnswers = this.getMissingResponseAnswers(question.feedbackQuestion);
      } else {
        responseAnswers = this.getResponseAnswers(response, question.feedbackQuestion);
      }
      for (const responseAnswer of responseAnswers) {
        const currRow: string[] = [giverTeamName, giverName, giverLastName, giverEmail,
          recipientTeamName, recipientName, recipientLastName, recipientEmail, ...responseAnswer];

        // TODO add participant comment

        // TODO add instructor comments

        csvRows.push(currRow);
      }
    }

    this.generateEmptyRow(csvRows);
    this.generateEmptyRow(csvRows);
    return csvRows;
  }

  /**
   * Gets question stats for a question.
   */
  private getQuestionStats(): string[][] {
    // TODO add stats
    return [];
  }

  /**
   * Gets answer(s) for a missing response in CSV version.
   */
  private getMissingResponseAnswers(question: FeedbackQuestion): string[][] {
    return FeedbackQuestionDetailsFactory
        .fromApiOutput(question.questionDetails)
        .getMissingResponseCsvAnswers();
  }

  /**
   * Gets answer(s) for a response in CSV version.
   */
  private getResponseAnswers(response: ResponseOutput, question: FeedbackQuestion): string[][] {
    return FeedbackResponseDetailsFactory
        .fromApiOutput(response.responseDetails)
        .getResponseCsvAnswers(question.questionDetails);
  }

  /**
   * Gets specific header names according to the question.
   */
  private getQuestionSpecificHeaders(question: FeedbackQuestion): string[] {
    return FeedbackQuestionDetailsFactory
        .fromApiOutput(question.questionDetails)
        .getQuestionCsvHeaders();
  }

  /**
   * Generates an empty row in the {@code csvRows}.
   */
  private generateEmptyRow(csvRows: string[][]): void {
    csvRows.push([]);
  }
}
