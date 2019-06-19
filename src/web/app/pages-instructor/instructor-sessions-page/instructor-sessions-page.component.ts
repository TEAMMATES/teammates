import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, map, switchMap, tap } from 'rxjs/operators';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionsService, TemplateSession } from '../../../services/feedback-sessions.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  LOCAL_DATE_TIME_FORMAT,
  TimeResolvingResult,
  TimezoneService,
} from '../../../services/timezone.service';
import {
  Course,
  Courses,
  FeedbackQuestion,
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessions,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { DEFAULT_INSTRUCTOR_PRIVILEGE } from '../../../types/instructor-privilege';
import {
  DateFormat,
  SessionEditFormMode,
  SessionEditFormModel,
  TimeFormat,
} from '../../components/session-edit-form/session-edit-form-model';
import {
  CopySessionResult,
  SessionsTableColumn,
  SessionsTableHeaderColorScheme,
  SessionsTableRowModel,
  SortBy,
  SortOrder,
} from '../../components/sessions-table/sessions-table-model';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorSessionModalPageComponent } from '../instructor-session-modal-page.component';
import { CopyFromOtherSessionsResult } from './copy-from-other-sessions-modal/copy-from-other-sessions-modal-model';
import {
  CopyFromOtherSessionsModalComponent,
} from './copy-from-other-sessions-modal/copy-from-other-sessions-modal.component';
import {
  SessionPermanentDeletionConfirmModalComponent,
} from './session-permanent-deletion-confirm-modal/session-permanent-deletion-confirm-modal.component';
import {
  SessionsPermanentDeletionConfirmModalComponent,
} from './sessions-permanent-deletion-confirm-modal/sessions-permanent-deletion-confirm-modal.component';

interface RecycleBinFeedbackSessionRowModel {
  feedbackSession: FeedbackSession;
}

/**
 * Instructor feedback sessions list page.
 */
@Component({
  selector: 'tm-instructor-sessions-page',
  templateUrl: './instructor-sessions-page.component.html',
  styleUrls: ['./instructor-sessions-page.component.scss'],
})
export class InstructorSessionsPageComponent extends InstructorSessionModalPageComponent implements OnInit {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  SessionEditFormMode: typeof SessionEditFormMode = SessionEditFormMode;
  SessionsTableColumn: typeof SessionsTableColumn = SessionsTableColumn;
  SessionsTableHeaderColorScheme: typeof SessionsTableHeaderColorScheme = SessionsTableHeaderColorScheme;

  // url params
  user: string = '';
  courseId: string = '';

  // data
  courseCandidates: Course[] = [];
  templateSessions: TemplateSession[] = [];

  // models
  sessionEditFormModel: SessionEditFormModel = {
    courseId: '',
    timeZone: 'UTC',
    courseName: '',
    feedbackSessionName: '',
    instructions: 'Please answer all the given questions.',

    submissionStartTime: { hour: 0, minute: 0 },
    submissionStartDate: { year: 0, month: 0, day: 0 },
    submissionEndTime: { hour: 0, minute: 0 },
    submissionEndDate: { year: 0, month: 0, day: 0 },
    gracePeriod: 0,

    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTime: { hour: 0, minute: 0 },
    customSessionVisibleDate: { year: 0, month: 0, day: 0 },

    responseVisibleSetting: ResponseVisibleSetting.LATER,
    customResponseVisibleTime: { hour: 0, minute: 0 },
    customResponseVisibleDate: { year: 0, month: 0, day: 0 },

    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,

    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,

    templateSessionName: '',

    isSaving: false,
    isEditable: true,
    hasVisibleSettingsPanelExpanded: false,
    hasEmailSettingsPanelExpanded: false,
  };

  sessionsTableRowModels: SessionsTableRowModel[] = [];
  sessionsTableRowModelsSortBy: SortBy = SortBy.NONE;
  sessionsTableRowModelsSortOrder: SortOrder = SortOrder.ASC;

  isRecycleBinExpanded: boolean = false;
  recycleBinFeedbackSessionRowModels: RecycleBinFeedbackSessionRowModel[] = [];
  recycleBinFeedbackSessionRowModelsSortBy: SortBy = SortBy.NONE;
  recycleBinFeedbackSessionRowModelsSortOrder: SortOrder = SortOrder.ASC;

  constructor(router: Router,
              httpRequestService: HttpRequestService,
              statusMessageService: StatusMessageService,
              navigationService: NavigationService,
              feedbackSessionsService: FeedbackSessionsService,
              feedbackQuestionsService: FeedbackQuestionsService,
              modalService: NgbModal,
              studentService: StudentService,
              private route: ActivatedRoute,
              private timezoneService: TimezoneService) {
    super(router, httpRequestService, statusMessageService, navigationService,
        feedbackSessionsService, feedbackQuestionsService, modalService, studentService);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.courseId = queryParams.courseid;
    });

    this.templateSessions = this.feedbackSessionsService.getTemplateSessions();
    this.loadCandidatesCourse();
    this.loadFeedbackSessions();
    this.loadRecycleBinFeedbackSessions();
  }

  /**
   * Copies from other sessions.
   */
  copyFromOtherSessionsHandler(): void {
    const modalRef: NgbModalRef = this.modalService.open(CopyFromOtherSessionsModalComponent);
    // select the current course Id.
    modalRef.componentInstance.copyToCourseId = this.sessionEditFormModel.courseId;

    modalRef.componentInstance.courseCandidates = this.courseCandidates;
    modalRef.componentInstance.existingFeedbackSession =
        this.sessionsTableRowModels.map((model: SessionsTableRowModel) => model.feedbackSession);

    modalRef.result.then((result: CopyFromOtherSessionsResult) => {
      this.copyFeedbackSession(result.fromFeedbackSession, result.newFeedbackSessionName, result.copyToCourseId)
          .subscribe((createdFeedbackSession: FeedbackSession) => {
            this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/sessions/edit'
                + `?courseid=${createdFeedbackSession.courseId}&fsname=${createdFeedbackSession.feedbackSessionName}`,
                'The feedback session has been copied. Please modify settings/questions as necessary.');
          }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
    }, () => {});
  }

  /**
   * Loads courses owned by the current user.
   */
  loadCandidatesCourse(): void {
    this.httpRequestService.get('/courses', {
      entitytype: 'instructor',
      coursestatus: 'active',
    }).subscribe((courses: Courses) => {
      this.courseCandidates = courses.courses;

      this.initDefaultValuesForSessionEditForm();
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Sets default values for the session edit form.
   */
  initDefaultValuesForSessionEditForm(): void {
    // if specified in the query and is valid, select that course
    // otherwise, select the first course by default
    const course: Course | undefined = this.courseCandidates.find((c: Course) => c.courseId === this.courseId);
    if (course) {
      this.sessionEditFormModel.courseId = course.courseId;
      this.sessionEditFormModel.courseName = course.courseName;
      this.sessionEditFormModel.timeZone = course.timeZone;
    } else if (this.courseCandidates.length !== 0) {
      this.sessionEditFormModel.courseId = this.courseCandidates[0].courseId;
      this.sessionEditFormModel.courseName = this.courseCandidates[0].courseName;
      this.sessionEditFormModel.timeZone = this.courseCandidates[0].timeZone;
    }

    // select the first template session
    if (this.templateSessions.length !== 0) {
      this.sessionEditFormModel.templateSessionName = this.templateSessions[0].name;
    }

    // set opening time to near future
    const nearFuture: any = moment().add(1, 'hours');
    this.sessionEditFormModel.submissionStartDate = {
      year: nearFuture.year(),
      month: nearFuture.month() + 1, // moment return 0-11 for month
      day: nearFuture.date(),
    };
    this.sessionEditFormModel.submissionStartTime = {
      minute: nearFuture.hour() === 0 ? 59 : 0, // for 00:00 midnight, we use 23:59
      hour: nearFuture.hour() === 0 ? 23 : nearFuture.hour(),
    };
    // set the closing time to tomorrow
    const tomorrow: any = moment().add(1, 'days');
    this.sessionEditFormModel.submissionEndDate = {
      year: tomorrow.year(),
      month: tomorrow.month() + 1, // moment return 0-11 for month
      day: tomorrow.date(),
    };
    this.sessionEditFormModel.submissionEndTime = {
      minute: 59,
      hour: 23,
    };
  }

  /**
   * Redirects to page to create or unarchive courses.
   */
  createOrUnarchiveCourse(): void {
    this.router.navigateByUrl('/web/instructor/courses');
  }

  /**
   * Adds a new feedback session.
   */
  addNewSessionHandler(): void {
    this.sessionEditFormModel.isSaving = true;

    const resolvingResultMessages: string[] = [];
    forkJoin(
        this.resolveLocalDateTime(this.sessionEditFormModel.submissionStartDate,
            this.sessionEditFormModel.submissionStartTime, this.sessionEditFormModel.timeZone,
            'Submission opening time', resolvingResultMessages),
        this.resolveLocalDateTime(this.sessionEditFormModel.submissionEndDate,
            this.sessionEditFormModel.submissionEndTime, this.sessionEditFormModel.timeZone,
            'Submission closing time', resolvingResultMessages),
        this.sessionEditFormModel.sessionVisibleSetting === SessionVisibleSetting.CUSTOM ?
            this.resolveLocalDateTime(this.sessionEditFormModel.customSessionVisibleDate,
                this.sessionEditFormModel.customSessionVisibleTime, this.sessionEditFormModel.timeZone,
                'Session visible time', resolvingResultMessages)
            : of(0),
        this.sessionEditFormModel.responseVisibleSetting === ResponseVisibleSetting.CUSTOM ?
            this.resolveLocalDateTime(this.sessionEditFormModel.customResponseVisibleDate,
                this.sessionEditFormModel.customResponseVisibleTime, this.sessionEditFormModel.timeZone,
                'Response visible time', resolvingResultMessages)
            : of(0),
    ).pipe(
        switchMap((vals: number[]) => {
          return this.feedbackSessionsService.createFeedbackSession(this.sessionEditFormModel.courseId, {
            feedbackSessionName: this.sessionEditFormModel.feedbackSessionName,
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
    ).subscribe((feedbackSession: FeedbackSession) => {

      // begin to populate session with template
      const templateSession: TemplateSession | undefined =
          this.feedbackSessionsService.getTemplateSessions().find(
              (t: TemplateSession) => t.name === this.sessionEditFormModel.templateSessionName);
      if (!templateSession) {
        return;
      }
      of(...templateSession.questions).pipe(
          concatMap((question: FeedbackQuestion) => {
            return this.feedbackQuestionsService.createFeedbackQuestion(
                feedbackSession.courseId, feedbackSession.feedbackSessionName, {
                  questionNumber: question.questionNumber,
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
      ).subscribe(() => {}, (resp: ErrorMessageOutput) => {
        this.sessionEditFormModel.isSaving = false;
        this.statusMessageService.showErrorMessage(
            `The session is created but the template questions cannot be created: ${resp.error.message}`);
      }, () => {
        this.router.navigateByUrl(
            '/web/instructor/sessions/edit'
            + `?courseid=${feedbackSession.courseId}&fsname=${feedbackSession.feedbackSessionName}`)
            .then(() => {
              resolvingResultMessages.forEach((msg: string) => {
                this.statusMessageService.showWarningMessage(msg);
              });
              this.statusMessageService.showSuccessMessage('The feedback session has been added.'
                  + 'Click the "Add New Question" button below to begin adding questions for the feedback session.');
            });
      });
    }, (resp: ErrorMessageOutput) => {
      this.sessionEditFormModel.isSaving = false;
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Resolves the local date time to an UNIX timestamp.
   */
  private resolveLocalDateTime(date: DateFormat, time: TimeFormat, timeZone: string,
                               fieldName: string, resolvingResultMessages: string[]): Observable<number> {
    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);

    const localDateTime: string = inst.format(LOCAL_DATE_TIME_FORMAT);
    return this.timezoneService.getResolvedTimestamp(localDateTime, timeZone, fieldName).pipe(
        tap((result: TimeResolvingResult) => {
          if (result.message.length !== 0) {
            resolvingResultMessages.push(result.message);
          }
        }),
        map((result: TimeResolvingResult) => result.timestamp));
  }

  /**
   * Loads all feedback sessions that can be accessed by current user.
   */
  loadFeedbackSessions(): void {
    this.feedbackSessionsService.getFeedbackSessionsForInstructor()
        .subscribe((response: FeedbackSessions) => {
          response.feedbackSessions.forEach((session: FeedbackSession) => {
            const model: SessionsTableRowModel = {
              feedbackSession: session,
              responseRate: '',
              isLoadingResponseRate: false,

              instructorPrivilege: DEFAULT_INSTRUCTOR_PRIVILEGE,
            };
            this.sessionsTableRowModels.push(model);
            this.updateInstructorPrivilege(model);
          });
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Sorts the list of feedback session row.
   */
  sortSessionsTableRowModelsEvent(by: SortBy): void {
    this.sessionsTableRowModelsSortBy = by;
    // reverse the sort order
    this.sessionsTableRowModelsSortOrder =
        this.sessionsTableRowModelsSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.sessionsTableRowModels.sort(this.sortModelsBy(by, this.sessionsTableRowModelsSortOrder));
  }

  /**
   * Loads response rate of a feedback session.
   */
  loadResponseRateEventHandler(rowIndex: number): void {
    this.loadResponseRate(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Edits the feedback session.
   */
  editSessionEventHandler(rowIndex: number): void {
    this.editSession(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Moves the feedback session to the recycle bin.
   */
  moveSessionToRecycleBinEventHandler(rowIndex: number): void {
    const model: SessionsTableRowModel = this.sessionsTableRowModels[rowIndex];
    const paramMap: { [key: string]: string } = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };

    this.httpRequestService.put('/bin/session', paramMap)
        .subscribe((feedbackSession: FeedbackSession) => {
          this.sessionsTableRowModels.splice(this.sessionsTableRowModels.indexOf(model), 1);
          this.recycleBinFeedbackSessionRowModels.push({
            feedbackSession,
          });
          this.statusMessageService.showSuccessMessage('The feedback session has been deleted. '
              + 'You can restore it from the deleted sessions table below.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Edits the feedback session.
   */
  copySessionEventHandler(result: CopySessionResult): void {
    this.copySession(this.sessionsTableRowModels[result.sessionToCopyRowIndex], result);
  }

  /**
   * Submits the feedback session as instructor.
   */
  submitSessionAsInstructorEventHandler(rowIndex: number): void {
    this.submitSessionAsInstructor(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Views the result of a feedback session.
   */
  viewSessionResultEventHandler(rowIndex: number): void {
    this.viewSessionResult(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Publishes a feedback session.
   */
  publishSessionEventHandler(rowIndex: number): void {
    this.publishSession(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Unpublishes a feedback session.
   */
  unpublishSessionEventHandler(rowIndex: number): void {
    this.unpublishSession(this.sessionsTableRowModels[rowIndex]);
  }

  /**
   * Loads all feedback sessions in recycle bin that can be accessed by current user.
   */
  loadRecycleBinFeedbackSessions(): void {
    this.feedbackSessionsService.getFeedbackSessionsInRecycleBinForInstructor()
        .subscribe((response: FeedbackSessions) => {
          response.feedbackSessions.forEach((session: FeedbackSession) => {
            this.recycleBinFeedbackSessionRowModels.push({
              feedbackSession: session,
            });
          });
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Sorts the list of feedback session rows in recycle bin table.
   */
  sortRecycleBinFeedbackSessionRows(by: SortBy): void {
    this.recycleBinFeedbackSessionRowModelsSortBy = by;
    // reverse the sort order
    this.recycleBinFeedbackSessionRowModelsSortOrder =
        this.recycleBinFeedbackSessionRowModelsSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.recycleBinFeedbackSessionRowModels.sort(
        this.sortModelsBy(by, this.recycleBinFeedbackSessionRowModelsSortOrder));
  }

  /**
   * Restores a recycle bin feedback session.
   */
  restoreRecycleBinFeedbackSession(model: RecycleBinFeedbackSessionRowModel): void {
    const paramMap: { [key: string]: string } = {
      courseid: model.feedbackSession.courseId,
      fsname: model.feedbackSession.feedbackSessionName,
    };

    this.httpRequestService.delete('/bin/session', paramMap)
        .subscribe((feedbackSession: FeedbackSession) => {
          this.recycleBinFeedbackSessionRowModels.splice(
              this.recycleBinFeedbackSessionRowModels.indexOf(model), 1);
          const m: SessionsTableRowModel = {
            feedbackSession,
            responseRate: '',
            isLoadingResponseRate: false,
            instructorPrivilege: DEFAULT_INSTRUCTOR_PRIVILEGE,
          };
          this.sessionsTableRowModels.push(m);
          this.updateInstructorPrivilege(m);
          this.statusMessageService.showSuccessMessage('The feedback session has been restored.');
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Restores all feedback sessions in recycle bin.
   */
  restoreAllRecycleBinFeedbackSession(): void {
    const restoreRequests: Observable<FeedbackSession>[] = [];
    this.recycleBinFeedbackSessionRowModels.forEach((model: RecycleBinFeedbackSessionRowModel) => {
      const paramMap: { [key: string]: string } = {
        courseid: model.feedbackSession.courseId,
        fsname: model.feedbackSession.feedbackSessionName,
      };
      restoreRequests.push(this.httpRequestService.delete('/bin/session', paramMap));
    });

    forkJoin(restoreRequests).subscribe((restoredSessions: FeedbackSession[]) => {
      restoredSessions.forEach((session: FeedbackSession) => {
        this.recycleBinFeedbackSessionRowModels = [];
        const m: SessionsTableRowModel = {
          feedbackSession: session,
          responseRate: '',
          isLoadingResponseRate: false,
          instructorPrivilege: DEFAULT_INSTRUCTOR_PRIVILEGE,
        };
        this.sessionsTableRowModels.push(m);
        this.updateInstructorPrivilege(m);
      });
      this.statusMessageService.showSuccessMessage('All sessions have been restored.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Deletes the feedback session permanently.
   */
  permanentDeleteSession(model: RecycleBinFeedbackSessionRowModel): void {
    const modalRef: NgbModalRef = this.modalService.open(SessionPermanentDeletionConfirmModalComponent);
    modalRef.componentInstance.courseId = model.feedbackSession.courseId;
    modalRef.componentInstance.feedbackSessionName = model.feedbackSession.feedbackSessionName;

    modalRef.result.then(() => {
      const paramMap: { [key: string]: string } = {
        courseid: model.feedbackSession.courseId,
        fsname: model.feedbackSession.feedbackSessionName,
      };

      this.httpRequestService.delete('/session', paramMap).subscribe(() => {
        this.recycleBinFeedbackSessionRowModels.splice(
            this.recycleBinFeedbackSessionRowModels.indexOf(model), 1);
        this.statusMessageService.showSuccessMessage('The feedback session has been permanently deleted.');
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Deletes all feedback sessions in the recycle bin permanently.
   */
  permanentDeleteAllSessions(): void {
    const modalRef: NgbModalRef = this.modalService.open(SessionsPermanentDeletionConfirmModalComponent);
    modalRef.componentInstance.sessionsToDelete =
        this.recycleBinFeedbackSessionRowModels.map(
            (model: RecycleBinFeedbackSessionRowModel) => model.feedbackSession);

    modalRef.result.then(() => {
      const deleteRequests: Observable<any>[] = [];

      this.recycleBinFeedbackSessionRowModels.forEach((model: RecycleBinFeedbackSessionRowModel) => {
        const paramMap: { [key: string]: string } = {
          courseid: model.feedbackSession.courseId,
          fsname: model.feedbackSession.feedbackSessionName,
        };

        deleteRequests.push(this.httpRequestService.delete('/session', paramMap));
      });

      forkJoin(deleteRequests).subscribe(() => {
        this.recycleBinFeedbackSessionRowModels = [];
        this.statusMessageService.showSuccessMessage('All sessions have been permanently deleted.');
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    }, () => {});
  }
}
