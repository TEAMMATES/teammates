import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import moment from 'moment-timezone';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  FeedbackSession, ResponseCommentOutput,
  SessionResults,
} from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { Intent } from '../../Intent';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';

/**
 * Instructor feedback session result page.
 */
@Component({
  selector: 'tm-instructor-session-result-page',
  templateUrl: './instructor-session-result-page.component.html',
  styleUrls: ['./instructor-session-result-page.component.scss'],
})
export class InstructorSessionResultPageComponent implements OnInit {

  // enum
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;

  session: any = {};
  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';
  viewType: string = 'QUESTION';
  section: string = '';
  sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  groupByTeam: boolean = true;
  showStatistics: boolean = true;
  indicateMissingResponses: boolean = true;
  user: string = '';

  sectionsModel: { [key: string]: any } = {};
  isSectionsLoaded: boolean = false;
  questionsModel: { [key: string]: any } = {};
  isQuestionsLoaded: boolean = false;

  constructor(private httpRequestService: HttpRequestService, private route: ActivatedRoute,
      private timezoneService: TimezoneService, private statusMessageService: StatusMessageService) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      const paramMap: { [key: string]: string } = {
        courseid: queryParams.courseid,
        fsname: queryParams.fsname,
        intent: Intent.INSTRUCTOR_RESULT,
        user: this.user,
      };
      this.httpRequestService.get('/session', paramMap).subscribe((resp: FeedbackSession) => {
        const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
        this.session = resp;
        this.formattedSessionOpeningTime =
            moment(this.session.submissionStartTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.formattedSessionClosingTime =
            moment(this.session.submissionEndTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);

        const sectionsParamMap: { [key: string]: string } = {
          courseid: queryParams.courseid,
          user: this.user,
        };
        this.httpRequestService.get('/course/sections', sectionsParamMap).subscribe((resp2: any) => {
          for (const sectionName of resp2.sectionNames) {
            this.sectionsModel[sectionName] = {
              responses: [],
              hasPopulated: false,
            };
          }
          this.isSectionsLoaded = true;
        }, (resp2: any) => {
          this.statusMessageService.showErrorMessage(resp2.error.message);
        });

        this.httpRequestService.get('/questions', paramMap).subscribe((resp2: any) => {
          for (const question of resp2.questions) {
            question.responses = [];
            question.hasPopulated = false;
            this.questionsModel[question.feedbackQuestionId] = question;
          }
          this.isQuestionsLoaded = true;
        }, (resp2: any) => {
          this.statusMessageService.showErrorMessage(resp2.error.message);
        });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    });
  }

  /**
   * Loads all the responses and response statistics for the specified question.
   */
  loadQuestion(questionId: string): void {
    if (this.questionsModel[questionId].hasPopulated) {
      // Do not re-fetch data
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: this.session.courseId,
      fsname: this.session.feedbackSessionName,
      questionid: questionId,
      intent: Intent.INSTRUCTOR_RESULT,
    };
    this.httpRequestService.get('/result', paramMap).subscribe((resp: SessionResults) => {
      if (resp.questions.length) {
        const responses: any = resp.questions[0];
        this.questionsModel[questionId].responses = responses.allResponses;
        this.questionsModel[questionId].statistics = responses.questionStatistics;
        this.questionsModel[questionId].hasPopulated = true;
        // Map comments from ResponseCommentOutput to FeedbackResponseCommentModel
        this.questionsModel[questionId].responses.forEach((response: any) => {
          response.allComments = response.allComments.map((comment: ResponseCommentOutput) => {
            return {
              commentId: comment.commentId,
              createdAt: comment.createdAt,
              editedAt: comment.updatedAt,
              timeZone: comment.timezone,
              commentGiver: comment.commentGiver,
              commentText: comment.commentText,
              isEditable: true,
            };
          });
        });
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads all the responses and response statistics for the specified section.
   */
  loadSection(sectionName: string): void {
    if (this.sectionsModel[sectionName].hasPopulated) {
      // Do not re-fetch data
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: this.session.courseId,
      fsname: this.session.feedbackSessionName,
      frgroupbysection: sectionName,
      intent: Intent.INSTRUCTOR_RESULT,
    };
    this.httpRequestService.get('/result', paramMap).subscribe((resp: SessionResults) => {
      this.sectionsModel[sectionName].questions = resp.questions;
      this.sectionsModel[sectionName].hasPopulated = true;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Updates questionsModel when there's a change in the comments table.
   */
  commentsChangeHandler(responseToUpdate: any): void {
    for (const key of Object.keys(this.questionsModel)) {
      this.questionsModel[key].responses.forEach((response: any, index: number) => {
        if (response.responseId === responseToUpdate.responseId) {
          this.questionsModel[key].responses[index] = responseToUpdate;
          this.questionsModel = { ...this.questionsModel };
          return;
        }
      });
    }
  }
}
