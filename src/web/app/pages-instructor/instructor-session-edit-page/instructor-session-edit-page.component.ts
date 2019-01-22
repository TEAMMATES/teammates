import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize, map, switchMap, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { FeedbackQuestionsService, NewQuestionModel } from '../../../services/feedback-questions.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import {
  LOCAL_DATE_TIME_FORMAT,
  LocalDateTimeAmbiguityStatus,
  LocalDateTimeInfo,
  TimezoneService,
} from '../../../services/timezone.service';
import {
  QuestionEditFormMode,
  QuestionEditFormModel,
} from '../../components/question-types/question-types-session-edit/question-edit-form-model';
import {
  DateFormat,
  SessionEditFormMode,
  SessionEditFormModel,
  TimeFormat,
} from '../../components/session-edit-form/session-edit-form-model';
import { Course } from '../../course';
import { FeedbackParticipantType } from '../../feedback-participant-type';
import {
  FeedbackQuestion,
  FeedbackQuestionType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../feedback-question';
import {
  FeedbackSession,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../feedback-session';
import { Intent } from '../../Intent';
import { ErrorMessageOutput } from '../../message-output';
import { TemplateQuestionModalComponent } from './template-question-modal/template-question-modal.component';

interface FeedbackQuestionsResponse {
  questions: FeedbackQuestion[];
}

/**
 * Instructor feedback session edit page.
 */
@Component({
  selector: 'tm-instructor-session-edit-page',
  templateUrl: './instructor-session-edit-page.component.html',
  styleUrls: ['./instructor-session-edit-page.component.scss'],
})
export class InstructorSessionEditPageComponent implements OnInit {

  // enum
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  QuestionEditFormMode: typeof QuestionEditFormMode = QuestionEditFormMode;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;

  // url param
  user: string = '';
  courseId: string = '';
  feedbackSessionName: string = '';

  courseName: string = '';

  // models
  sessionEditFormModel: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: '',

    submissionStartTime: { hour: 0, minute: 0 },
    submissionStartDate: { year: 0, month: 0, day: 0 },
    submissionEndTime: { hour: 0, minute: 0 },
    submissionEndDate: { year: 0, month: 0, day: 0 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 0, minute: 0 },
    customSessionVisibleDate: { year: 0, month: 0, day: 0 },

    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTime: { hour: 0, minute: 0 },
    customResponseVisibleDate: { year: 0, month: 0, day: 0 },

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: '',

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    isSaving: false,
    isEditable: false,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  // to get the original question model
  feedbackQuestionModels: Map<string, FeedbackQuestion> = new Map();

  questionEditFormModels: QuestionEditFormModel[] = [];

  newQuestionEditFormModel: QuestionEditFormModel = {
    feedbackQuestionId: '',
    questionNumber: 0,
    questionBrief: '',
    questionDescription: '',

    isQuestionHasResponses: false,

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      recommendedLength: 0,
    },

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 1,

    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],

    isEditable: true,
    isSaving: false,
  };

  isAddingQuestionPanelExpanded: boolean = false;

  constructor(private route: ActivatedRoute, private router: Router, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService, private navigationService: NavigationService,
              private timezoneService: TimezoneService, private feedbackQuestionsService: FeedbackQuestionsService,
              private modalService: NgbModal) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;

      this.loadFeedbackSession();
      this.loadFeedbackQuestions();
    });
  }

  /**
   * Loads a feedback session.
   */
  loadFeedbackSession(): void {
    // load the course of the feedback session first
    this.httpRequestService.get('/course', { courseid: this.courseId })
        .subscribe((course: Course) => {
          this.courseName = course.courseName;

          // load feedback session
          const paramMap: { [key: string]: string } = {
            courseid: this.courseId,
            fsname: this.feedbackSessionName,
            intent: Intent.FULL_DETAIL,
          };
          this.httpRequestService.get('/session', paramMap)
              .subscribe((feedbackSession: FeedbackSession) => {
                this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession);
              }, (resp: ErrorMessageOutput) => {
                this.statusMessageService.showErrorMessage(resp.error.message);
              });
        });
  }

  /**
   * Gets the {@code sessionEditFormModel} with {@link FeedbackSession} entity.
   */
  getSessionEditFormModel(feedbackSession: FeedbackSession): SessionEditFormModel {
    const submissionStart: {date: DateFormat; time: TimeFormat} =
        this.getDateTimeAtTimezone(feedbackSession.submissionStartTimestamp, feedbackSession.timeZone);

    const submissionEnd: {date: DateFormat; time: TimeFormat} =
        this.getDateTimeAtTimezone(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone);

    const model: SessionEditFormModel = {
      courseId: feedbackSession.courseId,
      timeZone: feedbackSession.timeZone,
      courseName: this.courseName,
      feedbackSessionName: feedbackSession.feedbackSessionName,
      instructions: feedbackSession.instructions,

      submissionStartTime: submissionStart.time,
      submissionStartDate: submissionStart.date,
      submissionEndTime: submissionEnd.time,
      submissionEndDate: submissionEnd.date,
      gracePeriod: feedbackSession.gracePeriod,

      sessionVisibleSetting: feedbackSession.sessionVisibleSetting,
      customSessionVisibleTime: { hour: 0, minute: 0 },
      customSessionVisibleDate: { year: 0, month: 0, day: 0 },

      responseVisibleSetting: feedbackSession.responseVisibleSetting,
      customResponseVisibleTime: { hour: 0, minute: 0 },
      customResponseVisibleDate: { year: 0, month: 0, day: 0 },

      submissionStatus: feedbackSession.submissionStatus,
      publishStatus: feedbackSession.publishStatus,

      isClosingEmailEnabled: feedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: feedbackSession.isPublishedEmailEnabled,

      isSaving: false,
      isEditable: false,
      hasVisibleSettingsPanelExpanded: feedbackSession.sessionVisibleSetting !== SessionVisibleSetting.AT_OPEN
          || feedbackSession.responseVisibleSetting !== ResponseVisibleSetting.LATER,
      hasEmailSettingsPanelExpanded: !feedbackSession.isClosingEmailEnabled || !feedbackSession.isPublishedEmailEnabled,
    };

    if (feedbackSession.customSessionVisibleTimestamp) {
      const customSessionVisible: {date: DateFormat; time: TimeFormat} =
          this.getDateTimeAtTimezone(feedbackSession.customSessionVisibleTimestamp, feedbackSession.timeZone);
      model.customSessionVisibleTime = customSessionVisible.time;
      model.customSessionVisibleDate = customSessionVisible.date;
    }

    if (feedbackSession.customResponseVisibleTimestamp) {
      const customResponseVisible: {date: DateFormat; time: TimeFormat} =
          this.getDateTimeAtTimezone(feedbackSession.customResponseVisibleTimestamp, feedbackSession.timeZone);
      model.customResponseVisibleTime = customResponseVisible.time;
      model.customResponseVisibleDate = customResponseVisible.date;
    }

    return model;
  }

  /**
   * Get the local date and time of timezone from timestamp.
   */
  private getDateTimeAtTimezone(timestamp: number, timeZone: string): {date: DateFormat; time: TimeFormat} {
    const momentInstance: any = moment(timestamp).tz(timeZone);
    const date: DateFormat = {
      year: momentInstance.year(),
      month: momentInstance.month() + 1, // moment return 0-11 for month
      day: momentInstance.date(),
    };
    const time: TimeFormat = {
      minute: momentInstance.minute(),
      hour: momentInstance.hour(),
    };
    return {
      date,
      time,
    };
  }

  /**
   * Handles editing existing session event.
   */
  editExistingSessionHandler(): void {
    this.sessionEditFormModel.isSaving = true;
    const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };

    forkJoin(
        this.resolveLocalDateTime(this.sessionEditFormModel.submissionStartDate,
            this.sessionEditFormModel.submissionStartTime, this.sessionEditFormModel.timeZone,
            'Submission opening time'),
        this.resolveLocalDateTime(this.sessionEditFormModel.submissionEndDate,
            this.sessionEditFormModel.submissionEndTime, this.sessionEditFormModel.timeZone,
            'Submission closing time'),
        this.sessionEditFormModel.sessionVisibleSetting === SessionVisibleSetting.CUSTOM ?
            this.resolveLocalDateTime(this.sessionEditFormModel.customSessionVisibleDate,
                this.sessionEditFormModel.customSessionVisibleTime, this.sessionEditFormModel.timeZone,
                'Session visible time')
            : of(0),
        this.sessionEditFormModel.responseVisibleSetting === ResponseVisibleSetting.CUSTOM ?
            this.resolveLocalDateTime(this.sessionEditFormModel.customResponseVisibleDate,
                this.sessionEditFormModel.customResponseVisibleTime, this.sessionEditFormModel.timeZone,
                'Response visible time')
            : of(0),
    ).pipe(
        switchMap((vals: number[]) => {
          return this.httpRequestService.put('/session', paramMap, {
            instructions: this.sessionEditFormModel.instructions,

            submissionStartTimestamp: vals[0],
            submissionEndTimestamp: vals[1],
            gracePeriod: this.sessionEditFormModel.gracePeriod,

            sessionVisibleSetting: this.sessionEditFormModel.sessionVisibleSetting,
            customSessionVisibleTimestamp: vals[2],

            responseVisibleSetting: this.sessionEditFormModel.responseVisibleSetting,
            customResponseVisibleTimestamp: vals[3],

            isClosingEmailEnabled: this.sessionEditFormModel.isClosingEmailEnabled,
            isPublishedEmailEnabled: this.sessionEditFormModel.isPublishedEmailEnabled,
          });
        }),
        finalize(() => {
          this.sessionEditFormModel.isSaving = false;
        }),
    ).subscribe((feedbackSession: FeedbackSession) => {
      this.sessionEditFormModel = this.getSessionEditFormModel(feedbackSession);

      this.statusMessageService.showSuccessMessage('The feedback session has been updated.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Resolves the local date time to an UNIX timestamp.
   */
  private resolveLocalDateTime(
      date: DateFormat, time: TimeFormat, timeZone: string, fieldName: string): Observable<number> {
    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);

    const localDateTime: string = inst.format(LOCAL_DATE_TIME_FORMAT);
    return this.timezoneService.getResolveLocalDateTime(localDateTime, timeZone).pipe(
        tap((info: LocalDateTimeInfo) => {
          const DATE_FORMAT_WITHOUT_ZONE_INFO: any = 'ddd, DD MMM, YYYY hh:mm A';
          const DATE_FORMAT_WITH_ZONE_INFO: any = "ddd, DD MMM, YYYY hh:mm A z ('UTC'Z)";

          switch (info.resolvedStatus) {
            case LocalDateTimeAmbiguityStatus.UNAMBIGUOUS:
              break;
            case LocalDateTimeAmbiguityStatus.GAP:
              this.statusMessageService.showWarningMessage(
                  `The ${fieldName}, ${moment.format(DATE_FORMAT_WITHOUT_ZONE_INFO)},
                   falls within the gap period when clocks spring forward at the start of DST.
                   It was resolved to ${moment(info.resolvedTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)}.`);
              break;
            case LocalDateTimeAmbiguityStatus.OVERLAP:
              this.statusMessageService.showWarningMessage(
                  `The ${fieldName}, ${moment.format(DATE_FORMAT_WITHOUT_ZONE_INFO)},
                   falls within the overlap period when clocks fall back at the end of DST.
                   It can refer to ${moment(info.earlierInterpretationTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)}
                   or ${moment(info.laterInterpretationTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)} .
                   It was resolved to %s.`,
              );
              break;
            default:
          }
        }),
        map((info: LocalDateTimeInfo) => info.resolvedTimestamp));
  }

  /**
   * Handles deleting current feedback session.
   */
  deleteExistingSessionHandler(): void {
    const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };
    this.httpRequestService.put('/bin/session', paramMap).subscribe(() => {
      this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/sessions',
          'The feedback session has been deleted. You can restore it from the deleted sessions table below.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads feedback questions.
   */
  loadFeedbackQuestions(): void {
    const paramMap: { [key: string]: string } = {
      courseid: this.courseId,
      fsname: this.feedbackSessionName,
      intent: Intent.FULL_DETAIL,
    };
    this.httpRequestService.get('/questions', paramMap)
        .subscribe((response: FeedbackQuestionsResponse) => {
          response.questions.forEach((feedbackQuestion: FeedbackQuestion) => {
            this.questionEditFormModels.push(this.getQuestionEditFormModel(feedbackQuestion));
            this.feedbackQuestionModels.set(feedbackQuestion.feedbackQuestionId, feedbackQuestion);
          });
        }, (resp: ErrorMessageOutput) => this.statusMessageService.showErrorMessage(resp.error.message));
  }

  /**
   * Tracks the question edit form by feedback question id.
   *
   * @see https://angular.io/api/common/NgForOf#properties
   */
  trackQuestionEditFormByFn(_: any, item: QuestionEditFormModel): any {
    return item.feedbackQuestionId;
  }

  /**
   * Converts feedback question to the question edit form model.
   */
  private getQuestionEditFormModel(feedbackQuestion: FeedbackQuestion): QuestionEditFormModel {
    return {
      feedbackQuestionId: feedbackQuestion.feedbackQuestionId,

      questionNumber: feedbackQuestion.questionNumber,
      questionBrief: feedbackQuestion.questionBrief,
      questionDescription: feedbackQuestion.questionDescription,

      isQuestionHasResponses: false, // TODO use API to determine

      questionType: feedbackQuestion.questionType,
      questionDetails: this.deepCopy(feedbackQuestion.questionDetails),

      giverType: feedbackQuestion.giverType,
      recipientType: feedbackQuestion.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: feedbackQuestion.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo
          ? feedbackQuestion.customNumberOfEntitiesToGiveFeedbackTo : 1,

      showResponsesTo: feedbackQuestion.showResponsesTo,
      showGiverNameTo: feedbackQuestion.showGiverNameTo,
      showRecipientNameTo: feedbackQuestion.showRecipientNameTo,

      isEditable: false,
      isSaving: false,
    };
  }

  /**
   * Saves the existing question.
   */
  saveExistingQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const originalQuestionNumber: number =
        // tslint:disable-next-line:no-non-null-assertion
        this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!.questionNumber;

    questionEditFormModel.isSaving = true;
    const paramMap: { [key: string]: string } = { questionid: questionEditFormModel.feedbackQuestionId };
    this.httpRequestService.put('/question', paramMap, {
      questionNumber: questionEditFormModel.questionNumber,
      questionBrief: questionEditFormModel.questionBrief,
      questionDescription: questionEditFormModel.questionDescription,

      questionDetails: questionEditFormModel.questionDetails,
      questionType: questionEditFormModel.questionType,

      giverType: questionEditFormModel.giverType,
      recipientType: questionEditFormModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: questionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: questionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo,

      showResponsesTo: questionEditFormModel.showResponsesTo,
      showGiverNameTo: questionEditFormModel.showGiverNameTo,
      showRecipientNameTo: questionEditFormModel.showRecipientNameTo,
    })
        .pipe(
            finalize(() => {
              questionEditFormModel.isSaving = false;
            }),
        )
        .subscribe((updatedQuestion: FeedbackQuestion) => {
          this.questionEditFormModels[index] = this.getQuestionEditFormModel(updatedQuestion);
          this.feedbackQuestionModels.set(updatedQuestion.feedbackQuestionId, updatedQuestion);

          // shift question if needed
          if (originalQuestionNumber !== updatedQuestion.questionNumber) {
            // move question form
            this.moveQuestionForm(
                originalQuestionNumber - 1, updatedQuestion.questionNumber - 1);
            this.normalizeQuestionNumberInQuestionForms();
          }

          this.statusMessageService.showSuccessMessage('The changes to the question have been updated.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Moves question edit form from the original position to the new position.
   */
  private moveQuestionForm(originalPosition: number, newPosition: number): void {
    this.questionEditFormModels.splice(newPosition, 0,
        this.questionEditFormModels.splice(originalPosition, 1)[0]);
  }

  /**
   * Normalizes question number in question forms by setting question number in sequence (i.e. 1, 2, 3, 4 ...).
   */
  private normalizeQuestionNumberInQuestionForms(): void {
    for (let i: number = 1; i <= this.questionEditFormModels.length; i += 1) {
      const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[i - 1];
      questionEditFormModel.questionNumber = i;
      // tslint:disable-next-line:no-non-null-assertion
      this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!.questionNumber = i;
    }
  }

  /**
   * Discards the changes made to the existing question.
   */
  discardExistingQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const feedbackQuestion: FeedbackQuestion =
        // tslint:disable-next-line:no-non-null-assertion
        this.feedbackQuestionModels.get(questionEditFormModel.feedbackQuestionId)!;
    this.questionEditFormModels[index] = this.getQuestionEditFormModel(feedbackQuestion);
  }

  /**
   * Duplicates the question.
   */
  duplicateCurrentQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };

    questionEditFormModel.isSaving = true;
    this.httpRequestService.post('/question', paramMap, {
      questionNumber: this.questionEditFormModels.length + 1, // add the duplicated question at the end
      questionBrief: questionEditFormModel.questionBrief,
      questionDescription: questionEditFormModel.questionDescription,

      questionDetails: questionEditFormModel.questionDetails,
      questionType: questionEditFormModel.questionType,

      giverType: questionEditFormModel.giverType,
      recipientType: questionEditFormModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: questionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: questionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo,

      showResponsesTo: questionEditFormModel.showResponsesTo,
      showGiverNameTo: questionEditFormModel.showGiverNameTo,
      showRecipientNameTo: questionEditFormModel.showRecipientNameTo,
    })
        .pipe(
            finalize(() => {
              questionEditFormModel.isSaving = false;
            }),
        )
        .subscribe((newQuestion: FeedbackQuestion) => {
          this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
          this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
          this.statusMessageService.showSuccessMessage('The question has been duplicated below.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Deletes the existing question.
   */
  deleteExistingQuestionHandler(index: number): void {
    const questionEditFormModel: QuestionEditFormModel = this.questionEditFormModels[index];
    const paramMap: { [key: string]: string } = { questionid: questionEditFormModel.feedbackQuestionId };

    this.httpRequestService.delete('/question', paramMap).subscribe(
        () => {
          // remove form model
          this.feedbackQuestionModels.delete(questionEditFormModel.feedbackQuestionId);
          this.questionEditFormModels.splice(index, 1);
          this.normalizeQuestionNumberInQuestionForms();

          this.statusMessageService.showSuccessMessage('The question has been deleted.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Handles display of template question modal.
   */
  templateQuestionModalHandler(): void {
    this.modalService.open(TemplateQuestionModalComponent).result.then((questions: FeedbackQuestion[]) => {
      let questionNumber: number = this.questionEditFormModels.length; // append the questions at the end
      of(...questions).pipe(
          concatMap((question: FeedbackQuestion) => {
            questionNumber += 1;
            const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };
            return this.httpRequestService.post('/question', paramMap, {
              questionNumber,
              questionBrief: question.questionBrief,
              questionDescription: question.questionDescription,

              questionDetails: question.questionDetails,
              questionType: question.questionType,

              giverType: question.giverType,
              recipientType: question.recipientType,

              numberOfEntitiesToGiveFeedbackToSetting: question.numberOfEntitiesToGiveFeedbackToSetting,
              customNumberOfEntitiesToGiveFeedbackTo: question.customNumberOfEntitiesToGiveFeedbackTo,

              showResponsesTo: question.showResponsesTo,
              showGiverNameTo: question.showGiverNameTo,
              showRecipientNameTo: question.showRecipientNameTo,
            });
          }),
      ).subscribe((newQuestion: FeedbackQuestion) => {
        this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
        this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);
      }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); }, () => {
        if (questions.length === 1) {
          this.statusMessageService.showSuccessMessage('The question has been added to this feedback session.');
        } else {
          this.statusMessageService.showSuccessMessage('The questions have been added to this feedback session.');
        }
      });
    });
  }

  /**
   * Populates and shows new question edit form.
   */
  populateAndShowNewQuestionForm(type: FeedbackQuestionType): void {
    this.isAddingQuestionPanelExpanded = true;

    const newQuestionModel: NewQuestionModel =
        this.feedbackQuestionsService.getNewQuestionModel(type);

    this.newQuestionEditFormModel = {
      feedbackQuestionId: '',
      questionNumber: this.questionEditFormModels.length + 1,
      questionBrief: newQuestionModel.questionBrief,
      questionDescription: newQuestionModel.questionDescription,

      isQuestionHasResponses: false,

      questionType: newQuestionModel.questionType,
      questionDetails: newQuestionModel.questionDetails,

      giverType: newQuestionModel.giverType,
      recipientType: newQuestionModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: newQuestionModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: newQuestionModel.customNumberOfEntitiesToGiveFeedbackTo
          ? newQuestionModel.customNumberOfEntitiesToGiveFeedbackTo : 1,

      showResponsesTo: newQuestionModel.showResponsesTo,
      showGiverNameTo: newQuestionModel.showGiverNameTo,
      showRecipientNameTo: newQuestionModel.showRecipientNameTo,

      isEditable: true,
      isSaving: false,
    };
  }

  /**
   * Creates a new question.
   */
  createNewQuestionHandler(): void {
    const paramMap: { [key: string]: string } = { courseid: this.courseId, fsname: this.feedbackSessionName };

    this.newQuestionEditFormModel.isSaving = true;
    this.httpRequestService.post('/question', paramMap, {
      questionNumber: this.newQuestionEditFormModel.questionNumber,
      questionBrief: this.newQuestionEditFormModel.questionBrief,
      questionDescription: this.newQuestionEditFormModel.questionDescription,

      questionDetails: this.newQuestionEditFormModel.questionDetails,
      questionType: this.newQuestionEditFormModel.questionType,

      giverType: this.newQuestionEditFormModel.giverType,
      recipientType: this.newQuestionEditFormModel.recipientType,

      numberOfEntitiesToGiveFeedbackToSetting: this.newQuestionEditFormModel.numberOfEntitiesToGiveFeedbackToSetting,
      customNumberOfEntitiesToGiveFeedbackTo: this.newQuestionEditFormModel.customNumberOfEntitiesToGiveFeedbackTo,

      showResponsesTo: this.newQuestionEditFormModel.showResponsesTo,
      showGiverNameTo: this.newQuestionEditFormModel.showGiverNameTo,
      showRecipientNameTo: this.newQuestionEditFormModel.showRecipientNameTo,
    })
        .pipe(
            finalize(() => {
              this.newQuestionEditFormModel.isSaving = false;
            }),
        )
        .subscribe((newQuestion: FeedbackQuestion) => {
          this.questionEditFormModels.push(this.getQuestionEditFormModel(newQuestion));
          this.feedbackQuestionModels.set(newQuestion.feedbackQuestionId, newQuestion);

          this.moveQuestionForm(
              this.questionEditFormModels.length - 1, newQuestion.questionNumber - 1);
          this.normalizeQuestionNumberInQuestionForms();
          this.isAddingQuestionPanelExpanded = false;

          this.statusMessageService.showSuccessMessage('The question has been added to this feedback session.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Handles 'Done Editing' click event.
   */
  doneEditingHandler(): void {
    this.router.navigateByUrl('/web/instructor/sessions');
    // TODO focus on the row of current feedback session in the sessions page
  }

  /**
   * Handles question 'Help' link click event.
   */
  questionsHelpHandler(): void {
    window.open(`${environment.frontendUrl}/web/instructor/help`);
    // TODO scroll down to the question specific section in the help page
  }

  private deepCopy<T>(obj: T): T {
    return JSON.parse(JSON.stringify(obj));
  }
}
