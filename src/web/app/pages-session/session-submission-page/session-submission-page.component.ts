import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, finalize, switchMap, tap } from 'rxjs/operators';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionRecipient,
  FeedbackQuestionRecipients,
  FeedbackResponse, FeedbackResponseComment,
  FeedbackResponseComments,
  FeedbackSession,
  FeedbackSessionSubmissionStatus,
  Instructor,
  NumberOfEntitiesToGiveFeedbackToSetting,
  Student,
} from '../../../types/api-output';
import { FeedbackResponseCommentModel } from '../../components/comment-box/comment-table/comment-table-model';
import {
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from '../../components/question-submission-form/question-submission-form-model';
import { ErrorMessageOutput } from '../../error-message-output';
import { Intent } from '../../Intent';
import {
  FeedbackSessionClosedModalComponent,
} from './feedback-session-closed-modal/feedback-session-closed-modal.component';
import {
  FeedbackSessionClosingSoonModalComponent,
} from './feedback-session-closing-soon-modal/feedback-session-closing-soon-modal.component';
import {
  FeedbackSessionDeletedModalComponent,
} from './feedback-session-deleted-modal/feedback-session-deleted-modal.component';
import {
  FeedbackSessionNotOpenModalComponent,
} from './feedback-session-not-open-modal/feedback-session-not-open-modal.component';
import {
  SavingCompleteModalComponent,
} from './saving-complete-modal/saving-complete-modal.component';

interface FeedbackQuestionsResponse {
  questions: FeedbackQuestion[];
}

interface FeedbackResponsesResponse {
  responses: FeedbackResponse[];
}

/**
 * The result of the confirmation.
 */
enum ConfirmationResult {
  /**
   * The submission has been confirmed successfully.
   */
  SUCCESS = 'SUCCESS',

  /**
   * The submission has been confirmed but the confirmation email failed to send.
   */
  SUCCESS_BUT_EMAIL_FAIL_TO_SEND = 'SUCCESS_BUT_EMAIL_FAIL_TO_SEND',
}

interface ConfirmationResponse {
  result: ConfirmationResult;
  message: string;
}

/**
 * Feedback session submission page.
 */
@Component({
  selector: 'tm-session-submission-page',
  templateUrl: './session-submission-page.component.html',
  styleUrls: ['./session-submission-page.component.scss'],
})
export class SessionSubmissionPageComponent implements OnInit {

  // enum
  FeedbackSessionSubmissionStatus: typeof FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus;
  Intent: typeof Intent = Intent;

  courseId: string = '';
  feedbackSessionName: string = '';
  regKey: string = '';

  moderatedPerson: string = '';
  previewAsPerson: string = '';
  // the name of the person involved
  // (e.g. the student name for unregistered student, the name of instructor being moderated)
  personName: string = '';

  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';
  feedbackSessionInstructions: string = '';
  feedbackSessionSubmissionStatus: FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus.OPEN;

  intent: Intent = Intent.STUDENT_SUBMISSION;

  questionSubmissionForms: QuestionSubmissionFormModel[] = [];

  shouldSendConfirmationEmail: boolean = true;

  isSavingResponses: boolean = false;
  isSubmissionFormsDisabled: boolean = false;

  isModerationHintExpanded: boolean = false;

  constructor(private route: ActivatedRoute, private router: Router, private statusMessageService: StatusMessageService,
              private httpRequestService: HttpRequestService, private timezoneService: TimezoneService,
              private feedbackResponsesService: FeedbackResponsesService, private modalService: NgbModal) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.data.pipe(
        tap((data: any) => {
          this.intent = data.intent;
        }),
        switchMap(() => this.route.queryParams),
    ).subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.regKey = queryParams.key ? queryParams.key : '';
      this.moderatedPerson = queryParams.moderatedperson ? queryParams.moderatedperson : '';
      this.previewAsPerson = queryParams.previewas ? queryParams.previewas : '';

      if (this.previewAsPerson) {
        // disable submission in the preview mode
        this.isSubmissionFormsDisabled = true;
      }
      this.loadPersonName();
      this.loadFeedbackSession();
    });
  }

  /**
   * Loads the name of the person invovled in the submission.
   */
  loadPersonName(): void {
    switch (this.intent) {
      case Intent.STUDENT_SUBMISSION:
        const paramMap: { [key: string]: string } = {
          courseid: this.courseId,
          key: this.regKey,
          studentemail: this.moderatedPerson || this.previewAsPerson,
        };

        this.httpRequestService.get('/student', paramMap)
            .subscribe((student: Student) => {
              this.personName = student.name;
            });
        break;
      case Intent.INSTRUCTOR_SUBMISSION:
        this.httpRequestService.get('/instructor', {
          courseid: this.courseId,
          fsname: this.feedbackSessionName,
          intent: this.intent,
          key: this.regKey,
          moderatedperson: this.moderatedPerson,
          previewas: this.previewAsPerson,
        }).subscribe((instructor: Instructor) => {
          this.personName = instructor.name;
        });
        break;
      default:
    }
  }

  /**
   * Redirects to join course link for unregistered student.
   */
  joinCourseForUnregisteredStudent(): void {
    this.router.navigateByUrl(`/web/join?entitytype=student&key=${this.regKey}`);
  }

  /**
   * Loads the feedback session information.
   */
  loadFeedbackSession(): void {
    const paramMap: { [key: string]: string } = {
      courseid: this.courseId,
      fsname: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedperson: this.moderatedPerson,
      previewas: this.previewAsPerson,
    };
    const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
    this.httpRequestService.get('/session', paramMap)
        .subscribe((feedbackSession: FeedbackSession) => {
          this.feedbackSessionInstructions = feedbackSession.instructions;
          this.formattedSessionOpeningTime =
              moment(feedbackSession.submissionStartTimestamp)
                  .tz(feedbackSession.timeZone).format(TIME_FORMAT);

          const submissionEndTime: any = moment(feedbackSession.submissionEndTimestamp);
          this.formattedSessionClosingTime = submissionEndTime
              .tz(feedbackSession.timeZone).format(TIME_FORMAT);

          this.feedbackSessionSubmissionStatus = feedbackSession.submissionStatus;

          // don't show alert modal in moderation
          if (!this.moderatedPerson) {
            switch (feedbackSession.submissionStatus) {
              case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
                this.isSubmissionFormsDisabled = true;
                this.modalService.open(FeedbackSessionNotOpenModalComponent);
                break;
              case FeedbackSessionSubmissionStatus.OPEN:
                // closing in 15 minutes
                if (moment.utc().add(15, 'minutes').isAfter(submissionEndTime)) {
                  this.modalService.open(FeedbackSessionClosingSoonModalComponent);
                }
                break;
              case FeedbackSessionSubmissionStatus.CLOSED:
                this.isSubmissionFormsDisabled = true;
                this.modalService.open(FeedbackSessionClosedModalComponent);
                break;
              case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
              default:
            }
          }

          this.loadFeedbackQuestions();
        }, (resp: ErrorMessageOutput) => {
          if (resp.status === 404) {
            this.modalService.open(FeedbackSessionDeletedModalComponent);
          }
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Loads feedback questions to submit.
   */
  loadFeedbackQuestions(): void {
    const paramMap: { [key: string]: string } = {
      courseid: this.courseId,
      fsname: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedperson: this.moderatedPerson,
      previewas: this.previewAsPerson,
    };
    this.httpRequestService.get('/questions', paramMap)
        .subscribe((response: FeedbackQuestionsResponse) => {
          response.questions.forEach((feedbackQuestion: FeedbackQuestion) => {
            const model: QuestionSubmissionFormModel = {
              feedbackQuestionId: feedbackQuestion.feedbackQuestionId,

              questionNumber: feedbackQuestion.questionNumber,
              questionBrief: feedbackQuestion.questionBrief,
              questionDescription: feedbackQuestion.questionDescription,

              giverType: feedbackQuestion.giverType,
              recipientType: feedbackQuestion.recipientType,
              recipientList: [],
              recipientSubmissionForms: [],

              questionType: feedbackQuestion.questionType,
              questionDetails: feedbackQuestion.questionDetails,

              numberOfEntitiesToGiveFeedbackToSetting: feedbackQuestion.numberOfEntitiesToGiveFeedbackToSetting,
              customNumberOfEntitiesToGiveFeedbackTo: feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo
                  ? feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo : 0,

              showGiverNameTo: feedbackQuestion.showGiverNameTo,
              showRecipientNameTo: feedbackQuestion.showRecipientNameTo,
              showResponsesTo: feedbackQuestion.showResponsesTo,
            };
            this.questionSubmissionForms.push(model);
            this.loadFeedbackQuestionRecipientsForQuestion(model);
          });
        }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorMessage(resp.error.message));
  }

  /**
   * Tracks the question submission form by feedback question id.
   *
   * @see https://angular.io/api/common/NgForOf#properties
   */
  trackQuestionSubmissionFormByFn(_: any, item: QuestionSubmissionFormModel): any {
    return item.feedbackQuestionId;
  }

  /**
   * Loads the feedback question recipients for the question.
   */
  loadFeedbackQuestionRecipientsForQuestion(model: QuestionSubmissionFormModel): void {
    const paramMap: { [key: string]: string } = {
      questionid: model.feedbackQuestionId,
      intent: this.intent,
      key: this.regKey,
      moderatedperson: this.moderatedPerson,
      previewas: this.previewAsPerson,
    };
    this.httpRequestService.get('/question/recipients', paramMap)
        .subscribe((response: FeedbackQuestionRecipients) => {
          response.recipients.forEach((recipient: FeedbackQuestionRecipient) => {
            model.recipientList.push({
              recipientIdentifier: recipient.identifier,
              recipientName: recipient.name,
            });
          });

          if (this.previewAsPerson) {
            // don't load responses in preview mode
            // generate a list of empty response box
            model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
              model.recipientSubmissionForms.push({
                recipientIdentifier:
                    this.getQuestionSubmissionFormMode(model) === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT
                        ? '' : recipient.recipientIdentifier,
                responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
                responseId: '',
              });
            });
          } else {
            this.loadFeedbackResponses(model);
          }
        }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorMessage(resp.error.message));
  }

  /**
   * Gets the form mode of the question submission form.
   */
  getQuestionSubmissionFormMode(model: QuestionSubmissionFormModel): QuestionSubmissionFormMode {
    const isNumberOfEntitiesToGiveFeedbackToSettingLimited: boolean
        = (model.recipientType === FeedbackParticipantType.STUDENTS
        || model.recipientType === FeedbackParticipantType.TEAMS
        || model.recipientType === FeedbackParticipantType.INSTRUCTORS)
        && model.numberOfEntitiesToGiveFeedbackToSetting === NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM
        && model.recipientList.length > model.customNumberOfEntitiesToGiveFeedbackTo;

    return isNumberOfEntitiesToGiveFeedbackToSettingLimited
        ? QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT : QuestionSubmissionFormMode.FIXED_RECIPIENT;
  }

  /**
   * Loads the responses of the feedback question to {@recipientSubmissionForms} in the model.
   */
  loadFeedbackResponses(model: QuestionSubmissionFormModel): void {
    const paramMap: { [key: string]: string } = {
      questionid: model.feedbackQuestionId,
      intent: this.intent,
      key: this.regKey,
      moderatedperson: this.moderatedPerson,
    };
    this.httpRequestService.get('/responses', paramMap).subscribe((existingResponses: FeedbackResponsesResponse) => {
      // if student does not have any responses (i.e. first time answering), then enable sending of confirmation email
      this.shouldSendConfirmationEmail = this.shouldSendConfirmationEmail && existingResponses.responses.length === 0;

      if (this.getQuestionSubmissionFormMode(model) === QuestionSubmissionFormMode.FIXED_RECIPIENT) {
        // need to generate a full list of submission forms
        model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
          const matchedExistingResponse: FeedbackResponse | undefined =
              existingResponses.responses.find(
                  (response: FeedbackResponse) => response.recipientIdentifier === recipient.recipientIdentifier);
          model.recipientSubmissionForms.push({
            recipientIdentifier: recipient.recipientIdentifier,
            responseDetails: matchedExistingResponse
                ? matchedExistingResponse.responseDetails
                : this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
            responseId: matchedExistingResponse ? matchedExistingResponse.feedbackResponseId : '',
          });
        });
      }

      if (this.getQuestionSubmissionFormMode(model) === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT) {
        // need to generate limited number of submission forms
        let numberOfRecipientSubmissionFormsNeeded: number =
            model.customNumberOfEntitiesToGiveFeedbackTo - existingResponses.responses.length;

        existingResponses.responses.forEach((response: FeedbackResponse) => {
          model.recipientSubmissionForms.push({
            recipientIdentifier: response.recipientIdentifier,
            responseDetails: response.responseDetails,
            responseId: response.feedbackResponseId,
          });
        });

        // generate empty submission forms
        while (numberOfRecipientSubmissionFormsNeeded > 0) {
          model.recipientSubmissionForms.push({
            recipientIdentifier: '',
            responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
            responseId: '',
          });
          numberOfRecipientSubmissionFormsNeeded -= 1;
        }
      }
      this.loadCommentsForResponses(model, existingResponses.responses);
    }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorMessage(resp.error.message));
  }

  /**
   * Checks whether there is any submission forms in the current page.
   */
  get hasAnyResponseToSubmit(): boolean {
    return this.questionSubmissionForms
        .some((model: QuestionSubmissionFormModel) => model.recipientSubmissionForms.length !== 0);
  }

  /**
   * Saves all feedback response.
   *
   * <p>All empty feedback response will be deleted; For non-empty responses, update/create them if necessary.
   */
  saveFeedbackResponses(): void {
    const notYetAnsweredQuestions: Set<number> = new Set();
    const failToSaveQuestions: Set<number> = new Set();
    const savingRequests: Observable<any>[] = [];
    const savingCommentRequests: Observable<any>[] = [];

    this.questionSubmissionForms.forEach((questionSubmissionFormModel: QuestionSubmissionFormModel) => {
      let isQuestionFullyAnswered: boolean = true;

      questionSubmissionFormModel.recipientSubmissionForms
          .forEach((recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
            const isFeedbackResponseDetailsEmpty: boolean =
                this.feedbackResponsesService.isFeedbackResponseDetailsEmpty(
                    questionSubmissionFormModel.questionType, recipientSubmissionFormModel.responseDetails);
            isQuestionFullyAnswered = isQuestionFullyAnswered && !isFeedbackResponseDetailsEmpty;

            if (recipientSubmissionFormModel.responseId !== '' && isFeedbackResponseDetailsEmpty) {
              // existing response but empty details -> delete response
              savingRequests.push(this.httpRequestService.delete('/response', {
                responseid: recipientSubmissionFormModel.responseId,
                intent: this.intent,
                key: this.regKey,
                moderatedperson: this.moderatedPerson,
              }).pipe(
                  tap(() => {
                    recipientSubmissionFormModel.responseId = '';
                  }),
                  catchError((error: any) => {
                    this.statusMessageService.showErrorMessage((error as ErrorMessageOutput).error.message);
                    failToSaveQuestions.add(questionSubmissionFormModel.questionNumber);
                    return of(error);
                  }),
              ));
            }

            if (recipientSubmissionFormModel.responseId !== '' && !isFeedbackResponseDetailsEmpty) {
              // existing response and details is not empty -> update response
              savingRequests.push(
                  this.feedbackResponsesService.updateFeedbackResponse(recipientSubmissionFormModel.responseId, {
                    intent: this.intent,
                    key: this.regKey,
                    moderatedperson: this.moderatedPerson,
                  }, {
                    recipientIdentifier: recipientSubmissionFormModel.recipientIdentifier,
                    questionType: questionSubmissionFormModel.questionType,
                    responseDetails: recipientSubmissionFormModel.responseDetails,
                  }).pipe(
                      tap((resp: FeedbackResponse) => {
                        recipientSubmissionFormModel.responseId = resp.feedbackResponseId;
                        recipientSubmissionFormModel.responseDetails = resp.responseDetails;
                        recipientSubmissionFormModel.recipientIdentifier = resp.recipientIdentifier;

                        savingCommentRequests.push(this.createCommentChangeRequest(recipientSubmissionFormModel)
                            .pipe(
                                catchError((error: any) => {
                                  this.statusMessageService.showErrorMessage(
                                      (error as ErrorMessageOutput).error.message);
                                  failToSaveQuestions.add(questionSubmissionFormModel.questionNumber);
                                  return of(error);
                                }),
                            ),
                        );

                      }),
                      catchError((error: any) => {
                        this.statusMessageService.showErrorMessage((error as ErrorMessageOutput).error.message);
                        failToSaveQuestions.add(questionSubmissionFormModel.questionNumber);
                        return of(error);
                      }),
                  ));
            }

            if (recipientSubmissionFormModel.responseId === '' && !isFeedbackResponseDetailsEmpty) {
              // new response and the details is not empty -> create response
              savingRequests.push(
                  this.feedbackResponsesService.createFeedbackResponse(questionSubmissionFormModel.feedbackQuestionId, {
                    intent: this.intent,
                    key: this.regKey,
                    moderatedperson: this.moderatedPerson,
                  }, {
                    recipientIdentifier: recipientSubmissionFormModel.recipientIdentifier,
                    questionType: questionSubmissionFormModel.questionType,
                    responseDetails: recipientSubmissionFormModel.responseDetails,
                  }).pipe(
                      tap((resp: FeedbackResponse) => {
                        recipientSubmissionFormModel.responseId = resp.feedbackResponseId;
                        recipientSubmissionFormModel.responseDetails = resp.responseDetails;
                        recipientSubmissionFormModel.recipientIdentifier = resp.recipientIdentifier;

                        savingCommentRequests.push(this.createCommentChangeRequest(recipientSubmissionFormModel)
                            .pipe(
                                catchError((error: any) => {
                                  this.statusMessageService.showErrorMessage(
                                      (error as ErrorMessageOutput).error.message);
                                  failToSaveQuestions.add(questionSubmissionFormModel.questionNumber);
                                  return of(error);
                                }),
                            ),
                        );
                      }),
                      catchError((error: any) => {
                        this.statusMessageService.showErrorMessage((error as ErrorMessageOutput).error.message);
                        failToSaveQuestions.add(questionSubmissionFormModel.questionNumber);
                        return of(error);
                      }),
                  ));
            }

          });

      if (!isQuestionFullyAnswered) {
        notYetAnsweredQuestions.add(questionSubmissionFormModel.questionNumber);
      }
    });

    this.isSavingResponses = true;
    let hasSubmissionConfirmationError: boolean = false;
    forkJoin(savingRequests).pipe(
        // Save comments after responses are saved.
        switchMap(() => {
          return forkJoin(savingCommentRequests);
        }),
        switchMap(() => {
          if (failToSaveQuestions.size === 0) {
            this.statusMessageService.showSuccessMessage('All responses submitted successfully!');
          } else {
            this.statusMessageService.showErrorMessage('Some responses are not saved successfully');
          }

          if (notYetAnsweredQuestions.size !== 0) {
            // TODO use showInfoMessage
            this.statusMessageService.showSuccessMessage(
                `Note that some questions are yet to be answered. They are:
                ${ Array.from(notYetAnsweredQuestions.values()) }.`);
          }

          return this.httpRequestService.post('/submission/confirmation', {
            courseid: this.courseId,
            fsname: this.feedbackSessionName,
            sendsubmissionemail: String(this.shouldSendConfirmationEmail),
            intent: this.intent,
            key: this.regKey,
            moderatedperson: this.moderatedPerson,
          });
        }),
    ).pipe(
        finalize(() => {
          this.isSavingResponses = false;

          const modalRef: NgbModalRef = this.modalService.open(SavingCompleteModalComponent);
          modalRef.componentInstance.notYetAnsweredQuestions = Array.from(notYetAnsweredQuestions.values()).join(', ');
          modalRef.componentInstance.failToSaveQuestions = Array.from(failToSaveQuestions.values()).join(', ');
          modalRef.componentInstance.hasSubmissionConfirmationError = hasSubmissionConfirmationError;
        }),
    ).subscribe((response: ConfirmationResponse) => {
      switch (response.result) {
        case ConfirmationResult.SUCCESS:
          break;
        case ConfirmationResult.SUCCESS_BUT_EMAIL_FAIL_TO_SEND:
          this.statusMessageService.showErrorMessage(
              `Submission confirmation email failed to send: ${response.message}`);
          break;
        default:
          this.statusMessageService.showErrorMessage(`Unknown result ${response.result}`);
      }
      hasSubmissionConfirmationError = false;
      this.shouldSendConfirmationEmail = false;
    }, (resp: ErrorMessageOutput) => {
      hasSubmissionConfirmationError = true;
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Creates a request to change a comment.
   * The request can be either of the following: DELETE, CREATE or UPDATE.
   */
  private createCommentChangeRequest(
      recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel): Observable<any> {

    if (!recipientSubmissionFormModel.comment) {
      return of({});
    }

    // Create delete comment request if it has an empty comment text.
    if (recipientSubmissionFormModel.comment.commentText === '') {
      return this.httpRequestService.delete('/responsecomment', {
        responsecommentid: recipientSubmissionFormModel.comment.commentId.toString(),
      });

    }

    // If existing comment, create update request.
    if (recipientSubmissionFormModel.comment.commentId) {
      return this.updateComment(recipientSubmissionFormModel.comment.commentId,
          recipientSubmissionFormModel.comment.commentText).pipe(
            tap((resp: FeedbackResponseComment) => {
              recipientSubmissionFormModel.comment = {
                commentId: resp.feedbackResponseCommentId,
                createdAt: resp.createdAt,
                editedAt: resp.updatedAt,
                commentGiver: resp.commentGiver,
                commentText: resp.commentText,
                isEditable: true,
              };
            }),
          );
    }

    // If new comment, create save request.
    return this.saveComment(recipientSubmissionFormModel.responseId,
        recipientSubmissionFormModel.comment.commentText).pipe(
          tap((resp: FeedbackResponseComment) => {
            recipientSubmissionFormModel.comment = {
              commentId: resp.feedbackResponseCommentId,
              createdAt: resp.createdAt,
              editedAt: resp.updatedAt,
              commentGiver: resp.commentGiver,
              commentText: resp.commentText,
              isEditable: true,
            };
          }),
    );
  }

  /**
   * Deletes a comment.
   */
  deleteComment(questionIndex: number, deleteCommentData: any): void {
    const comment: FeedbackResponseCommentModel | undefined =
        this.questionSubmissionForms[questionIndex].recipientSubmissionForms[deleteCommentData.recipientIndex].comment;

    if (!comment) {
      return;
    }

    this.httpRequestService.delete('/responsecomment', {
      responsecommentid: comment.commentId.toString(),
    }).subscribe(() => {
      this.questionSubmissionForms[questionIndex].recipientSubmissionForms[deleteCommentData.recipientIndex]
          .comment = undefined;
    });
  }

  /**
   * Saves a comment.
   */
  saveComment(responseId: string, commentText: string): Observable<any> {
    return this.httpRequestService.post('/responsecomment', {
      responseid: responseId,
      intent: this.intent,
    }, {
      commentText,
      showCommentTo: [],
      showGiverNameTo: [],
    });
  }

  /**
   * Updates a comment.
   */
  updateComment(commentId: number, commentText: string): Observable<any> {
    return this.httpRequestService.put('/responsecomment', {
      responsecommentid: commentId.toString(),
      intent: this.intent,
    }, {
      commentText,
      showCommentTo: [],
      showGiverNameTo: [],
    });
  }

  /**
   * Loads comments for responses
   */
  loadCommentsForResponses(model: QuestionSubmissionFormModel, feedbackResponses: FeedbackResponse[]): void {
    feedbackResponses.forEach((feedbackResponse: FeedbackResponse) => {
      if (feedbackResponse.feedbackResponseId !== '') {
        this.loadCommentsForResponse(model, feedbackResponse.feedbackResponseId);
      }
    });
  }

  /**
   * Loads comments for a feedback response.
   */
  loadCommentsForResponse(model: QuestionSubmissionFormModel, responseId: string): void {
    let commentModel: FeedbackResponseCommentModel;

    this.httpRequestService.get('/responsecomment', {
      responseid: responseId,
      intent: this.intent,
    }).subscribe((comments: FeedbackResponseComments) => {

      // For submission, responsecomment/GET will return a list of a single comment.
      const comment: FeedbackResponseComment = comments.comments[0];

      if (!comment) {
        return;
      }

      commentModel = {
        commentId: comment.feedbackResponseCommentId,
        createdAt: comment.createdAt,
        editedAt: comment.updatedAt,
        commentGiver: comment.commentGiver,
        commentText: comment.commentText,
        isEditable: true,
      };

      const recipientSubmissionFormIndex: number = model.recipientSubmissionForms.findIndex(
          (rsf: FeedbackResponseRecipientSubmissionFormModel) => rsf.responseId === responseId);

      const updatedForms: FeedbackResponseRecipientSubmissionFormModel[] = model.recipientSubmissionForms.slice();

      updatedForms[recipientSubmissionFormIndex] = {
        ...updatedForms[recipientSubmissionFormIndex],
        comment: commentModel,
      };

      model.recipientSubmissionForms = updatedForms;
    });
  }
}
