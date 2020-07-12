import { DOCUMENT } from '@angular/common';
import { AfterViewInit, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { PageScrollService } from 'ngx-page-scroll-core';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, finalize, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponseCommentService } from '../../../services/feedback-response-comment.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  ConfirmationResponse,
  ConfirmationResult,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionRecipient,
  FeedbackQuestionRecipients,
  FeedbackResponse, FeedbackResponseComment,
  FeedbackSession,
  FeedbackSessionSubmissionStatus,
  Instructor,
  NumberOfEntitiesToGiveFeedbackToSetting,
  RegkeyValidity,
  Student,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { CommentRowModel } from '../../components/comment-box/comment-row/comment-row.component';
import {
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from '../../components/question-submission-form/question-submission-form-model';
import { ErrorMessageOutput } from '../../error-message-output';
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

/**
 * A collection of feedback responses.
 */
export interface FeedbackResponsesResponse {
  responses: FeedbackResponse[];
}

/**
 * Feedback session submission page.
 */
@Component({
  selector: 'tm-session-submission-page',
  templateUrl: './session-submission-page.component.html',
  styleUrls: ['./session-submission-page.component.scss'],
})
export class SessionSubmissionPageComponent implements OnInit, AfterViewInit {

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
  feedbackSessionTimezone: string = '';
  feedbackSessionSubmissionStatus: FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus.OPEN;

  intent: Intent = Intent.STUDENT_SUBMISSION;

  questionSubmissionForms: QuestionSubmissionFormModel[] = [];

  shouldSendConfirmationEmail: boolean = true;

  isSavingResponses: boolean = false;
  isSubmissionFormsDisabled: boolean = false;

  isModerationHintExpanded: boolean = false;
  moderatedQuestionId: string = '';

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService,
              private feedbackQuestionsService: FeedbackQuestionsService,
              private feedbackResponsesService: FeedbackResponsesService,
              private feedbackSessionsService: FeedbackSessionsService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private modalService: NgbModal,
              private pageScrollService: PageScrollService,
              private authService: AuthService,
              private navigationService: NavigationService,
              private commentService: FeedbackResponseCommentService,
              @Inject(DOCUMENT) private document: any) {
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
      this.moderatedQuestionId = queryParams.moderatedquestionId ? queryParams.moderatedquestionId : '';

      if (this.previewAsPerson) {
        // disable submission in the preview mode
        this.isSubmissionFormsDisabled = true;
      }

      const nextUrl: string = `${window.location.pathname}${window.location.search}`;
      this.authService.getAuthUser(undefined, nextUrl).subscribe((auth: AuthInfo) => {
        const isPreviewOrModeration: boolean = !!(auth.user && (this.moderatedPerson || this.previewAsPerson));
        if (this.regKey && !isPreviewOrModeration) {
          this.authService.getAuthRegkeyValidity(this.regKey, this.intent).subscribe((resp: RegkeyValidity) => {
            if (resp.isValid) {
              if (auth.user) {
                // The logged in user matches the registration key; redirect to the logged in URL

                this.navigationService.navigateByURLWithParamEncoding(this.router, '/web/student/sessions/result',
                    { courseid: this.courseId, fsname: this.feedbackSessionName });
              } else {
                // There is no logged in user for valid, unused registration key; load information based on the key

                this.loadPersonName();
                this.loadFeedbackSession();
              }
            } else if (!auth.user) {
              // If there is no logged in user for a valid, used registration key, redirect to login page
              window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
            } else {
              this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                  'You are not authorized to view this page.');
            }
          }, () => {
            this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                'You are not authorized to view this page.');
          });
        } else if (auth.user) {
          // Load information based on logged in user
          // This will also cover moderation/preview cases
          this.loadPersonName();
          this.loadFeedbackSession();
        } else {
          this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
              'You are not authorized to view this page.');
        }
      }, () => {
        this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
            'You are not authorized to view this page.');
      });
    });
  }

  ngAfterViewInit(): void {
    if (!this.moderatedQuestionId) {
      return;
    }
    setTimeout(() => {
      this.pageScrollService.scroll({
        document: this.document,
        scrollTarget: `#${this.moderatedQuestionId}`,
        scrollOffset: 70,
      });
    }, 500);
  }

  /**
   * Loads the name of the person invovled in the submission.
   */
  loadPersonName(): void {
    switch (this.intent) {
      case Intent.STUDENT_SUBMISSION:
        this.studentService.getStudent(
            this.courseId,
            this.moderatedPerson || this.previewAsPerson,
            this.regKey,
        ).subscribe((student: Student) => {
          this.personName = student.name;
        });
        break;
      case Intent.INSTRUCTOR_SUBMISSION:
        this.instructorService.getInstructor({
          courseId: this.courseId,
          feedbackSessionName: this.feedbackSessionName,
          intent: this.intent,
          key: this.regKey,
          moderatedPerson: this.moderatedPerson,
          previewAs: this.previewAsPerson,
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
    const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
    this.feedbackSessionsService.getFeedbackSession({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).subscribe((feedbackSession: FeedbackSession) => {
      this.feedbackSessionInstructions = feedbackSession.instructions;
      this.formattedSessionOpeningTime = this.timezoneService
          .formatToString(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone, TIME_FORMAT);

      this.formattedSessionClosingTime = this.timezoneService
          .formatToString(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone, TIME_FORMAT);

      this.feedbackSessionSubmissionStatus = feedbackSession.submissionStatus;
      this.feedbackSessionTimezone = feedbackSession.timeZone;

          // don't show alert modal in moderation
      if (!this.moderatedPerson) {
        switch (feedbackSession.submissionStatus) {
          case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
            this.isSubmissionFormsDisabled = true;
            this.modalService.open(FeedbackSessionNotOpenModalComponent);
            break;
          case FeedbackSessionSubmissionStatus.OPEN:
            // closing in 15 minutes
            if (feedbackSession.submissionEndTimestamp - Date.now() < 15 * 60 * 1000) {
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
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Loads feedback questions to submit.
   */
  loadFeedbackQuestions(): void {
    this.feedbackQuestionsService.getFeedbackQuestions({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).subscribe((response: FeedbackQuestionsResponse) => {
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
    }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message));
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
    this.feedbackQuestionsService.loadFeedbackQuestionRecipients({
      questionId: model.feedbackQuestionId,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).subscribe((response: FeedbackQuestionRecipients) => {
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
    }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message));
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
    this.feedbackResponsesService.getFeedbackResponse({
      questionId: model.feedbackQuestionId,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
    }).subscribe((existingResponses: FeedbackResponsesResponse) => {
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

      // load comments
      this.loadParticipantComment(model);
    }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message));
  }

  /**
   * Loads all comments given by feedback participants.
   */
  loadParticipantComment(model: QuestionSubmissionFormModel): void {
    const loadCommentRequests: Observable<any>[] = [];
    model.recipientSubmissionForms.forEach(
        (recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
          if (!recipientSubmissionFormModel.responseId) {
            return;
          }
          loadCommentRequests.push(
          this.commentService
              .loadParticipantComment(recipientSubmissionFormModel.responseId, this.intent, {
                key: this.regKey,
                moderatedperson: this.moderatedPerson,
              }).pipe(
                  tap((comment?: FeedbackResponseComment) => {
                    if (comment) {
                      recipientSubmissionFormModel.commentByGiver = this.getCommentModel(comment);
                    }
                  }),
              ));
        });
    forkJoin(loadCommentRequests).subscribe(() => {
      // comment loading success
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Gets the comment model for a given comment.
   */
  getCommentModel(comment: FeedbackResponseComment): CommentRowModel {
    return {
      originalComment: comment,
      commentEditFormModel: {
        commentText: comment.commentText,
        // the participant comment shall not use custom visibilities
        isUsingCustomVisibilities: false,
        showCommentTo: [],
        showGiverNameTo: [],
      },
      timezone: this.feedbackSessionTimezone,
      isEditing: false,
    };
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
    const failToSaveQuestions: Record<number, string> = {}; // Map of question number to error message
    const savingRequests: Observable<any>[] = [];

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
              savingRequests.push(this.feedbackResponsesService.deleteFeedbackResponse({
                responseId: recipientSubmissionFormModel.responseId,
                intent: this.intent,
                key: this.regKey,
                moderatedPerson: this.moderatedPerson,
              }).pipe(
                  tap(() => {
                    // clear inputs
                    recipientSubmissionFormModel.responseId = '';
                    recipientSubmissionFormModel.commentByGiver = undefined;
                  }),
                  catchError((error: any) => {
                    this.statusMessageService.showErrorToast((error as ErrorMessageOutput).error.message);
                    failToSaveQuestions[questionSubmissionFormModel.questionNumber] =
                        (error as ErrorMessageOutput).error.message;
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
                      }),
                      switchMap(() => this.createCommentRequest(recipientSubmissionFormModel)),
                      catchError((error: any) => {
                        this.statusMessageService.showErrorToast((error as ErrorMessageOutput).error.message);
                        failToSaveQuestions[questionSubmissionFormModel.questionNumber] =
                            (error as ErrorMessageOutput).error.message;
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
                      }),
                      switchMap(() => this.createCommentRequest(recipientSubmissionFormModel)),
                      catchError((error: any) => {
                        this.statusMessageService.showErrorToast((error as ErrorMessageOutput).error.message);
                        failToSaveQuestions[questionSubmissionFormModel.questionNumber]
                            = (error as ErrorMessageOutput).error.message;
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
        switchMap(() => {
          if (Object.keys(failToSaveQuestions).length === 0) {
            this.statusMessageService.showSuccessToast('All responses submitted successfully!');
          } else {
            this.statusMessageService.showErrorToast('Some responses are not saved successfully');
          }

          if (notYetAnsweredQuestions.size !== 0) {
            // TODO use showInfoMessage
            this.statusMessageService.showSuccessToast(
                `Note that some questions are yet to be answered. They are:
                ${ Array.from(notYetAnsweredQuestions.values()) }.`);
          }

          return this.feedbackSessionsService.confirmSubmission({
            courseId: this.courseId,
            feedbackSessionName: this.feedbackSessionName,
            sendSubmissionEmail: String(this.shouldSendConfirmationEmail),
            intent: this.intent,
            key: this.regKey,
            moderatedPerson: this.moderatedPerson,
          });
        }),
    ).pipe(
        finalize(() => {
          this.isSavingResponses = false;

          const modalRef: NgbModalRef = this.modalService.open(SavingCompleteModalComponent);
          modalRef.componentInstance.notYetAnsweredQuestions = Array.from(notYetAnsweredQuestions.values()).join(', ');
          modalRef.componentInstance.failToSaveQuestions = failToSaveQuestions;
          modalRef.componentInstance.hasSubmissionConfirmationError = hasSubmissionConfirmationError;
        }),
    ).subscribe((response: ConfirmationResponse) => {
      switch (response.result) {
        case ConfirmationResult.SUCCESS:
          break;
        case ConfirmationResult.SUCCESS_BUT_EMAIL_FAIL_TO_SEND:
          this.statusMessageService.showErrorToast(
              `Submission confirmation email failed to send: ${response.message}`);
          break;
        default:
          this.statusMessageService.showErrorToast(`Unknown result ${response.result}`);
      }
      hasSubmissionConfirmationError = false;
      this.shouldSendConfirmationEmail = false;
    }, (resp: ErrorMessageOutput) => {
      hasSubmissionConfirmationError = true;
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Creates comment request.
   */
  createCommentRequest(recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel): Observable<any> {
    if (!recipientSubmissionFormModel.responseId) {
      // responseId not set, cannot set comment
      return of({});
    }
    if (!recipientSubmissionFormModel.commentByGiver) {
      // comment not given, do nothing
      return of({});
    }

    if (!recipientSubmissionFormModel.commentByGiver.originalComment) {
      // comment is new

      if (recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText === '') {
        // new comment is empty
        recipientSubmissionFormModel.commentByGiver = undefined;
        return of({});
      }

      // create new comment
      return this.commentService.createComment({
        commentText: recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText,
        // we ignore the fields in comment edit model as participant comment
        // will follow visibilities from question by design
        showCommentTo: [],
        showGiverNameTo: [],
      }, recipientSubmissionFormModel.responseId, this.intent, {
        key: this.regKey,
        moderatedperson: this.moderatedPerson,
      }).pipe(
          tap((comment: FeedbackResponseComment) => {
            recipientSubmissionFormModel.commentByGiver = this.getCommentModel(comment);
          }),
      );
    }

    // existing comment

    if (recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText === '') {
      // comment is empty, create delete request
      return this.commentService.deleteComment(
          recipientSubmissionFormModel.commentByGiver.originalComment.feedbackResponseCommentId, this.intent, {
            key: this.regKey,
            moderatedperson: this.moderatedPerson,
          })
          .pipe(
              tap(() => {
                recipientSubmissionFormModel.commentByGiver = undefined;
              }));
    }

    // update comment
    return this.commentService.updateComment({
      commentText: recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText,
      // we ignore the fields in comment edit model as participant comment
      // will follow visibilities from question by design
      showCommentTo: [],
      showGiverNameTo: [],
    }, recipientSubmissionFormModel.commentByGiver.originalComment.feedbackResponseCommentId, this.intent, {
      key: this.regKey,
      moderatedperson: this.moderatedPerson,
    }).pipe(
        tap((comment: FeedbackResponseComment) => {
          recipientSubmissionFormModel.commentByGiver = this.getCommentModel(comment);
        }),
    );
  }

  /**
   * Deletes a comment by participants.
   */
  deleteParticipantComment(questionIndex: number, responseIdx: number): void {
    const recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel =
        this.questionSubmissionForms[questionIndex].recipientSubmissionForms[responseIdx];

    if (!recipientSubmissionFormModel.commentByGiver || !recipientSubmissionFormModel.commentByGiver.originalComment) {
      return;
    }

    this.commentService.deleteComment(
        recipientSubmissionFormModel.commentByGiver.originalComment.feedbackResponseCommentId, this.intent, {
          key: this.regKey,
          moderatedperson: this.moderatedPerson,
        }).subscribe(() => {
          recipientSubmissionFormModel.commentByGiver = undefined;
          this.statusMessageService.showSuccessToast('Your comment has been deleted!');
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Updates a comment by participants.
   */
  updateParticipantComment(questionIndex: number, responseIdx: number): void {
    const recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel =
        this.questionSubmissionForms[questionIndex].recipientSubmissionForms[responseIdx];

    if (!recipientSubmissionFormModel.commentByGiver || !recipientSubmissionFormModel.commentByGiver.originalComment) {
      return;
    }

    this.commentService.updateComment({
      commentText: recipientSubmissionFormModel.commentByGiver.commentEditFormModel.commentText,
      // we ignore the fields in comment edit model as participant comment
      // will follow visibilities from question by design
      showCommentTo: [],
      showGiverNameTo: [],
    }, recipientSubmissionFormModel.commentByGiver.originalComment.feedbackResponseCommentId, this.intent, {
      key: this.regKey,
      moderatedperson: this.moderatedPerson,
    }).subscribe(
        (comment: FeedbackResponseComment) => {
          recipientSubmissionFormModel.commentByGiver = this.getCommentModel(comment);
          this.statusMessageService.showSuccessToast('Your comment has been saved!');
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }
}
