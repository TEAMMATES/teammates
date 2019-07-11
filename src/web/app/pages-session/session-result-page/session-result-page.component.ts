import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import moment from 'moment-timezone';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  FeedbackSession,
  QuestionOutput, ResponseCommentOutput,
  SessionResults,
} from '../../../types/api-output';
import { FeedbackResponseCommentModel } from '../../components/comment-box/comment-table/comment-table-model';
import { ErrorMessageOutput } from '../../error-message-output';
import { Intent } from '../../Intent';

/**
 * Feedback session result page.
 */
@Component({
  selector: 'tm-session-result-page',
  templateUrl: './session-result-page.component.html',
  styleUrls: ['./session-result-page.component.scss'],
})
export class SessionResultPageComponent implements OnInit {

  session: any = {};
  questions: QuestionOutput[] = [];
  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';

  constructor(private httpRequestService: HttpRequestService, private route: ActivatedRoute,
      private timezoneService: TimezoneService, private statusMessageService: StatusMessageService) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const paramMap: { [key: string]: string } = {
        courseid: queryParams.courseid,
        fsname: queryParams.fsname,
        intent: Intent.STUDENT_RESULT,
      };
      this.httpRequestService.get('/session', paramMap).subscribe((resp: FeedbackSession) => {
        const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
        this.session = resp;
        this.formattedSessionOpeningTime =
            moment(this.session.submissionStartTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.formattedSessionClosingTime =
              moment(this.session.submissionEndTimestamp).tz(this.session.timeZone).format(TIME_FORMAT);
        this.httpRequestService.get('/result', paramMap).subscribe((resp2: SessionResults) => {
          this.questions = resp2.questions.sort(
              (a: QuestionOutput, b: QuestionOutput) => a.questionNumber - b.questionNumber);

          // Map comments to FeedbackResponseCommentModel
          this.questions.forEach((question: any ) => {
                question.otherResponses.forEach((response: any) => {
                  response.allComments = response.allComments.map((comment: ResponseCommentOutput) => {
                    return this.mapComments(comment);
                  })
                });
                question.responsesToSelf.forEach((response: any) => {
                  response.allComments = response.allComments.map((comment: ResponseCommentOutput) => {
                    return this.mapComments(comment);
                  })
                });
                question.responsesFromSelf.forEach((response: any) => {
                  response.allComments = response.allComments.map((comment: ResponseCommentOutput) => {
                    return this.mapComments(comment);
                  })
                });
              }
          );
        }, (resp2: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp2.error.message);
        });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    });
  }

  /**
   * Maps comments
   */
  mapComments(comment: ResponseCommentOutput): FeedbackResponseCommentModel {
    return {
      commentId: comment.commentId,
      createdAt: comment.createdAt,
      editedAt: comment.updatedAt,
      timeZone: comment.timezone,
      commentGiver: comment.commentGiver,
      commentText: comment.commentText,
      isEditable: true,
    };
  }
}
