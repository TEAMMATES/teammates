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
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  AuthInfo,
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionRecipient,
  FeedbackQuestionRecipients,
  FeedbackResponse,
  FeedbackResponseComment,
  FeedbackResponses,
  FeedbackSession,
  FeedbackSessionSubmissionStatus,
  Instructor,
  NumberOfEntitiesToGiveFeedbackToSetting,
  RegkeyValidity,
  Student,
} from '../../../types/api-output';
import { FeedbackResponseRequest, Intent } from '../../../types/api-request';
import { DEFAULT_NUMBER_OF_RETRY_ATTEMPTS } from '../../../types/default-retry-attempts';
import { CommentRowModel } from '../../components/comment-box/comment-row/comment-row.component';
import { ErrorReportComponent } from '../../components/error-report/error-report.component';
import {
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from '../../components/question-submission-form/question-submission-form-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';
import { SavingCompleteModalComponent } from './saving-complete-modal/saving-complete-modal.component';

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
  loggedInUser: string = '';

  moderatedPerson: string = '';
  previewAsPerson: string = '';
  // the name of the person involved
  // (e.g. the student name for unregistered student, the name of instructor being moderated)
  personName: string = '';
  personEmail: string = '';

  formattedSessionOpeningTime: string = '';
  formattedSessionClosingTime: string = '';
  feedbackSessionInstructions: string = '';
  feedbackSessionTimezone: string = '';
  feedbackSessionSubmissionStatus: FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus.OPEN;

  intent: Intent = Intent.STUDENT_SUBMISSION;

  questionSubmissionForms: QuestionSubmissionFormModel[] = [];

  isSavingResponses: boolean = false;
  isSubmissionFormsDisabled: boolean = false;

  isModerationHintExpanded: boolean = false;
  moderatedQuestionId: string = '';

  isFeedbackSessionLoading: boolean = true;
  isFeedbackSessionQuestionsLoading: boolean = true;
  hasFeedbackSessionQuestionsLoadingFailed: boolean = false;
  isFeedbackSessionQuestionResponsesLoading: boolean = true;
  retryAttempts: number = DEFAULT_NUMBER_OF_RETRY_ATTEMPTS;

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
              private ngbModal: NgbModal,
              private simpleModalService: SimpleModalService,
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
        if (auth.user) {
          this.loggedInUser = auth.user.id;
        }
        if (this.regKey && !isPreviewOrModeration) {
          this.authService.getAuthRegkeyValidity(this.regKey, this.intent).subscribe((resp: RegkeyValidity) => {
            if (resp.isAllowedAccess) {
              if (resp.isUsed) {
                // The logged in user matches the registration key; redirect to the logged in URL

                this.navigationService.navigateByURLWithParamEncoding(this.router, '/web/student/sessions/submission',
                    { courseid: this.courseId, fsname: this.feedbackSessionName });
              } else {
                // Valid, unused registration key; load information based on the key
                this.loadPersonName();
                this.loadFeedbackSession();
              }
            } else if (resp.isValid) {
              // At this point, registration key must already be used, otherwise access would be granted
              if (this.loggedInUser) {
                // Registration key belongs to another user who is not the logged in user
                this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                    'You are not authorized to view this page.');
              } else {
                // There is no logged in user for a valid, used registration key, redirect to login page
                window.location.href = `${this.backendUrl}${auth.studentLoginUrl}`;
              }
            } else {
              // The registration key is invalid
              this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                  'You are not authorized to view this page.');
            }
          }, () => {
            this.navigationService.navigateWithErrorMessage(this.router, '/web/front',
                'You are not authorized to view this page.');
          });
        } else if (this.loggedInUser) {
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
          this.personEmail = student.email;
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
          this.personEmail = instructor.email;
        });
        break;
      default:
    }
  }

  /**
   * Redirects to join course link for unregistered student.
   */
  joinCourseForUnregisteredStudent(): void {
    this.navigationService.navigateByURL(this.router, '/web/join', { entitytype: 'student', key: this.regKey });
  }

  /**
   * Loads the feedback session information.
   */
  loadFeedbackSession(): void {
    this.isFeedbackSessionLoading = true;
    const TIME_FORMAT: string = 'ddd, DD MMM, YYYY, hh:mm A zz';
    this.feedbackSessionsService.getFeedbackSession({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).pipe(finalize(() => this.isFeedbackSessionLoading = false))
      .subscribe((feedbackSession: FeedbackSession) => {
        this.feedbackSessionInstructions = feedbackSession.instructions;
        this.formattedSessionOpeningTime = this.timezoneService
          .formatToString(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone, TIME_FORMAT);

        this.formattedSessionClosingTime = this.timezoneService
          .formatToString(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone, TIME_FORMAT);

        this.feedbackSessionSubmissionStatus = feedbackSession.submissionStatus;
        this.feedbackSessionTimezone = feedbackSession.timeZone;

        // don't show alert modal in moderation
        if (!this.moderatedPerson) {
          let modalContent: string;
          switch (feedbackSession.submissionStatus) {
            case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
              this.isSubmissionFormsDisabled = true;
              modalContent = `<p><strong>The feedback session is currently not open for submissions.</strong></p>
                <p>You can view the questions and any submitted responses
                for this feedback session but cannot submit new responses.</p>`;
              this.simpleModalService.openInformationModal(
                'Feedback Session Not Open', SimpleModalType.WARNING, modalContent);
              break;
            case FeedbackSessionSubmissionStatus.OPEN:
              // closing in 15 minutes
              if (feedbackSession.submissionEndTimestamp - Date.now() < 15 * 60 * 1000) {
                modalContent = 'Warning: you have less than 15 minutes before the submission deadline expires!';
                this.simpleModalService.openInformationModal(
                  'Feedback Session Will Be Closing Soon!', SimpleModalType.WARNING, modalContent);
              }
              break;
            case FeedbackSessionSubmissionStatus.CLOSED:
              this.isSubmissionFormsDisabled = true;
              modalContent = `<p><strong>Feedback Session is Closed</strong></p>
                <p>You can view the questions and any submitted responses
                for this feedback session but cannot submit new responses.</p>`;
              this.simpleModalService.openInformationModal(
                'Feedback Session Closed', SimpleModalType.WARNING, modalContent);
              break;
            case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
            default:
          }
        }

        this.loadFeedbackQuestions();
      }, (resp: ErrorMessageOutput) => {
        if (resp.status === 404) {
          this.simpleModalService.openInformationModal('Feedback Session Does Not Exist!', SimpleModalType.DANGER,
            'The session does not exist (most likely deleted by the instructor after the submission link was sent).');
          this.navigationService.navigateWithErrorMessage(this.router, '/web/student/home', resp.error.message);
        }
      });
  }

  /**
   * Loads feedback questions to submit.
   */
  loadFeedbackQuestions(): void {
    this.isFeedbackSessionQuestionsLoading = true;
    this.questionSubmissionForms = [];
    this.feedbackQuestionsService.getFeedbackQuestions({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
      previewAs: this.previewAsPerson,
    }).pipe(finalize(() => this.isFeedbackSessionQuestionsLoading = false))
        .subscribe((response: FeedbackQuestionsResponse) => {
          this.isFeedbackSessionQuestionResponsesLoading = response.questions.length !== 0;
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
        }, (resp: ErrorMessageOutput) => {
          this.handleError(resp);
        });
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
        const formMode: QuestionSubmissionFormMode = this.getQuestionSubmissionFormMode(model);
        model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
          if (formMode === QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT &&
            model.recipientSubmissionForms.length >=
            model.customNumberOfEntitiesToGiveFeedbackTo) {
            return;
          }

          let recipientIdentifier: string = '';
          if (formMode !== QuestionSubmissionFormMode.FLEXIBLE_RECIPIENT) {
            recipientIdentifier = recipient.recipientIdentifier;
          }

          model.recipientSubmissionForms.push({
            recipientIdentifier,
            responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
            responseId: '',
            isValid: true,
          });
        });
        this.isFeedbackSessionQuestionResponsesLoading = false;
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
    this.isFeedbackSessionQuestionResponsesLoading = true;
    this.feedbackResponsesService.getFeedbackResponse({
      questionId: model.feedbackQuestionId,
      intent: this.intent,
      key: this.regKey,
      moderatedPerson: this.moderatedPerson,
    }).pipe(finalize(() => this.isFeedbackSessionQuestionResponsesLoading = false))
      .subscribe((existingResponses: FeedbackResponsesResponse) => {
        if (this.getQuestionSubmissionFormMode(model) === QuestionSubmissionFormMode.FIXED_RECIPIENT) {
          // need to generate a full list of submission forms
          model.recipientList.forEach((recipient: FeedbackResponseRecipient) => {
            const matchedExistingResponse: FeedbackResponse | undefined =
              existingResponses.responses.find(
                  (response: FeedbackResponse) => response.recipientIdentifier === recipient.recipientIdentifier);
            const submissionForm: FeedbackResponseRecipientSubmissionFormModel = {
              recipientIdentifier: recipient.recipientIdentifier,
              responseDetails: matchedExistingResponse
                ? matchedExistingResponse.responseDetails
                : this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
              responseId: matchedExistingResponse ? matchedExistingResponse.feedbackResponseId : '',
              isValid: true,
            };
            if (matchedExistingResponse && matchedExistingResponse.giverComment) {
              submissionForm.commentByGiver = this.getCommentModel(matchedExistingResponse.giverComment);
            }
            model.recipientSubmissionForms.push(submissionForm);
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
              isValid: true,
            });
          });

          // generate empty submission forms
          while (numberOfRecipientSubmissionFormsNeeded > 0) {
            model.recipientSubmissionForms.push({
              recipientIdentifier: '',
              responseDetails: this.feedbackResponsesService.getDefaultFeedbackResponseDetails(model.questionType),
              responseId: '',
              isValid: true,
            });
            numberOfRecipientSubmissionFormsNeeded -= 1;
          }
        }
      }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorToast(resp.error.message));
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
    const requestIds: Record<string, string> = {};
    const answers: Record<string, FeedbackResponse[]> = {};
    const failToSaveQuestions: Record<number, string> = {}; // Map of question number to error message
    const savingRequests: Observable<any>[] = [];

    this.questionSubmissionForms.forEach((questionSubmissionFormModel: QuestionSubmissionFormModel) => {
      let isQuestionFullyAnswered: boolean = true;

      const responses: FeedbackResponseRequest[] = [];

      questionSubmissionFormModel.recipientSubmissionForms
          .forEach((recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
            if (!recipientSubmissionFormModel.isValid) {
              failToSaveQuestions[questionSubmissionFormModel.questionNumber] =
                  'Invalid responses provided. Please check question constraints.';
              return;
            }
            const isFeedbackResponseDetailsEmpty: boolean =
                this.feedbackResponsesService.isFeedbackResponseDetailsEmpty(
                    questionSubmissionFormModel.questionType, recipientSubmissionFormModel.responseDetails);
            isQuestionFullyAnswered = isQuestionFullyAnswered && !isFeedbackResponseDetailsEmpty;

            if (!isFeedbackResponseDetailsEmpty) {
              responses.push({
                recipient: recipientSubmissionFormModel.recipientIdentifier,
                responseDetails: recipientSubmissionFormModel.responseDetails,
              });
            }
          });

      savingRequests.push(
          this.feedbackResponsesService.submitFeedbackResponses(questionSubmissionFormModel.feedbackQuestionId, {
            intent: this.intent,
            key: this.regKey,
            moderatedperson: this.moderatedPerson,
          }, {
            responses,
          }).pipe(
              tap((resp: FeedbackResponses) => {
                const responsesMap: Record<string, FeedbackResponse> = {};
                resp.responses.forEach((response: FeedbackResponse) => {
                  responsesMap[response.recipientIdentifier] = response;
                  answers[questionSubmissionFormModel.feedbackQuestionId] =
                      answers[questionSubmissionFormModel.feedbackQuestionId] || [];
                  answers[questionSubmissionFormModel.feedbackQuestionId].push(response);
                });
                requestIds[questionSubmissionFormModel.feedbackQuestionId] = resp.requestId || '';

                questionSubmissionFormModel.recipientSubmissionForms
                    .forEach((recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) => {
                      if (responsesMap[recipientSubmissionFormModel.recipientIdentifier]) {
                        const correspondingResp: FeedbackResponse =
                            responsesMap[recipientSubmissionFormModel.recipientIdentifier];
                        recipientSubmissionFormModel.responseId = correspondingResp.feedbackResponseId;
                        recipientSubmissionFormModel.responseDetails = correspondingResp.responseDetails;
                        recipientSubmissionFormModel.recipientIdentifier = correspondingResp.recipientIdentifier;
                      } else {
                        recipientSubmissionFormModel.responseId = '';
                        recipientSubmissionFormModel.commentByGiver = undefined;
                      }
                    });
              }),
              switchMap(() =>
                  forkJoin(questionSubmissionFormModel.recipientSubmissionForms
                      .map((recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) =>
                          this.createCommentRequest(recipientSubmissionFormModel))),
              ),
              catchError((error: ErrorMessageOutput) => {
                failToSaveQuestions[questionSubmissionFormModel.questionNumber] = error.error.message;
                return of(error);
              }),
          ),
      );

      if (!isQuestionFullyAnswered) {
        notYetAnsweredQuestions.add(questionSubmissionFormModel.questionNumber);
      }
    });

    this.isSavingResponses = true;
    forkJoin(savingRequests).pipe(
        finalize(() => {
          this.isSavingResponses = false;

          const modalRef: NgbModalRef = this.ngbModal.open(SavingCompleteModalComponent);
          modalRef.componentInstance.requestIds = requestIds;
          modalRef.componentInstance.courseId = this.courseId;
          modalRef.componentInstance.feedbackSessionName = this.feedbackSessionName;
          modalRef.componentInstance.feedbackSessionTimezone = this.feedbackSessionTimezone;
          modalRef.componentInstance.personEmail = this.personEmail;
          modalRef.componentInstance.personName = this.personName;
          modalRef.componentInstance.questions = this.questionSubmissionForms;
          modalRef.componentInstance.answers = answers;
          modalRef.componentInstance.notYetAnsweredQuestions = Array.from(notYetAnsweredQuestions.values());
          modalRef.componentInstance.failToSaveQuestions = failToSaveQuestions;
        }),
    ).subscribe();
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

  retryLoadingFeedbackSessionQuestions(): void {
    this.hasFeedbackSessionQuestionsLoadingFailed = false;
    if (this.retryAttempts >= 0) {
      this.retryAttempts -= 1;
    }
    this.loadFeedbackQuestions();
  }

  handleError(resp: ErrorMessageOutput): void {
    this.hasFeedbackSessionQuestionsLoadingFailed = true;
    if (this.retryAttempts < 0) {
      const report: NgbModalRef = this.ngbModal.open(ErrorReportComponent);
      report.componentInstance.requestId = resp.error.requestId;
      report.componentInstance.errorMessage = resp.error.message;
    } else {
      this.statusMessageService.showErrorToast(resp.error.message);
    }
  }
}
