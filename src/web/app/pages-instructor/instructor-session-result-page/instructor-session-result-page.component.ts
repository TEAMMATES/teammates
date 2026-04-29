import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModalRef, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { concatMap, finalize } from 'rxjs/operators';
import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';
import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';
import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';
import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';
import { SectionTabModel, QuestionTabModel } from './instructor-session-tab.model';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackSessionActionsService } from '../../../services/feedback-session-actions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { FileSaveService } from '../../../services/file-save.service';
import { InstructorCommentService } from '../../../services/instructor-comment.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  CourseSectionNames,
  FeedbackQuestions,
  FeedbackSession,
  FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  Instructor,
  Instructors,
  QuestionOutput,
  ResponseOutput, ResponseVisibleSetting,
  SessionResults, SessionVisibleSetting,
  Student,
  Students,
} from '../../../types/api-output';
import { Intent } from '../../../types/api-request';
import { AjaxLoadingComponent } from '../../components/ajax-loading/ajax-loading.component';
import { CommentToCommentRowModelPipe } from '../../components/comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import {
  PreviewSessionResultPanelComponent,
} from '../../components/preview-session-result-panel/preview-session-result-panel.component';
import { QuestionStatistics } from '../../components/question-types/question-statistics/question-statistics';
import {
  ReminderResponseModel,
} from '../../components/sessions-table/send-reminders-to-respondents-modal/send-reminders-to-respondents-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';
import { ViewResultsPanelComponent } from '../../components/view-results-panel/view-results-panel.component';
import { ErrorMessageOutput } from '../../error-message-output';

const TIME_FORMAT = 'ddd, DD MMM, YYYY, hh:mm A zz';

/**
 * Instructor feedback session result page.
 */
@Component({
  selector: 'tm-instructor-session-result-page',
  templateUrl: './instructor-session-result-page.component.html',
  styleUrls: ['./instructor-session-result-page.component.scss'],
  imports: [
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    TeammatesRouterDirective,
    NgbTooltip,
    AjaxLoadingComponent,
    ViewResultsPanelComponent,
    InstructorSessionResultQuestionViewComponent,
    InstructorSessionResultGrqViewComponent,
    InstructorSessionResultRgqViewComponent,
    InstructorSessionResultGqrViewComponent,
    InstructorSessionResultRqgViewComponent,
    InstructorSessionNoResponsePanelComponent,
    PreviewSessionResultPanelComponent,
],
  providers: [
    CommentsToCommentTableModelPipe,
    CommentToCommentRowModelPipe,
    InstructorCommentService,
  ],
})
export class InstructorSessionResultPageComponent implements OnInit {

  // enum
  InstructorSessionResultSectionType: typeof InstructorSessionResultSectionType = InstructorSessionResultSectionType;
  InstructorSessionResultViewType: typeof InstructorSessionResultViewType = InstructorSessionResultViewType;

  formattedSessionOpeningTime = '';
  formattedSessionClosingTime = '';
  formattedResultVisibleFromTime = '';

  courseId = '';
  fsName = '';
  feedbackSessionId = '';
  viewType: string = InstructorSessionResultViewType.QUESTION;
  section = '';
  sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  groupByTeam = true;
  showStatistics = true;
  indicateMissingResponses = true;

  // below are two models contain similar and duplicate data
  // they are for different views
  sectionsModel: Record<string, SectionTabModel> = {};
  isSectionsLoaded = false;
  hasSectionsLoadingFailed = false;
  questionsModel: Record<string, QuestionTabModel> = {};
  isQuestionsLoaded = false;
  hasQuestionsLoadingFailed = false;
  isNoResponseStudentsLoaded = false;

  isFeedbackSessionLoading = false;
  hasFeedbackSessionLoadingFailed = false;
  isDownloadingResults = false;

  noResponseStudents: Student[] = [];
  isNoResponsePanelLoaded = false;
  hasNoResponseLoadingFailed = false;

  allStudentsInCourse: Student[] = [];
  emailOfStudentToPreview = '';
  allInstructorsInCourse: Instructor[] = [];
  emailOfInstructorToPreview = '';

  FeedbackSessionPublishStatus: typeof FeedbackSessionPublishStatus = FeedbackSessionPublishStatus;
  isExpandAll = false;

  session: FeedbackSession = {
    feedbackSessionId: '',
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  @ViewChild(InstructorSessionNoResponsePanelComponent) noResponsePanel?:
    InstructorSessionNoResponsePanelComponent;

  constructor(private feedbackSessionsService: FeedbackSessionsService,
              private feedbackSessionActionsService: FeedbackSessionActionsService,
              private feedbackQuestionsService: FeedbackQuestionsService,
              private courseService: CourseService,
              private fileSaveService: FileSaveService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private route: ActivatedRoute,
              private timezoneService: TimezoneService,
              private simpleModalService: SimpleModalService,
              private commentsToCommentTableModel: CommentsToCommentTableModelPipe,
              private navigationService: NavigationService,
              private statusMessageService: StatusMessageService,
              public commentService: InstructorCommentService) {
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.fsName = queryParams.fsname;
      this.feedbackSessionId = queryParams.fsid;
      this.loadFeedbackSessionResults(this.courseId, this.feedbackSessionId);
    });
  }

  loadFeedbackSessionResults(courseId: string, feedbackSessionId: string): void {
    this.hasQuestionsLoadingFailed = false;
    this.hasSectionsLoadingFailed = false;
    this.hasFeedbackSessionLoadingFailed = false;
    this.isFeedbackSessionLoading = true;
    this.feedbackSessionsService.getFeedbackSession({
      feedbackSessionId,
      intent: Intent.INSTRUCTOR_RESULT,
    }).subscribe({
      next: (feedbackSession: FeedbackSession) => {
        this.session = feedbackSession;
        this.feedbackSessionId = feedbackSession.feedbackSessionId!;
        this.formattedSessionOpeningTime = this.timezoneService
            .formatToString(this.session.submissionStartTimestamp, this.session.timeZone, TIME_FORMAT);
        this.formattedSessionClosingTime = this.timezoneService
            .formatToString(this.session.submissionEndTimestamp, this.session.timeZone, TIME_FORMAT);
        if (this.session.responseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
          if (this.session.sessionVisibleSetting === SessionVisibleSetting.AT_OPEN) {
            this.formattedResultVisibleFromTime = this.timezoneService
                .formatToString(this.session.submissionStartTimestamp, this.session.timeZone, TIME_FORMAT);
          } else if (this.session.sessionVisibleFromTimestamp) {
            this.formattedResultVisibleFromTime = this.timezoneService
                .formatToString(this.session.sessionVisibleFromTimestamp, this.session.timeZone, TIME_FORMAT);
          }
        } else if (this.session.resultVisibleFromTimestamp) {
          this.formattedResultVisibleFromTime = this.timezoneService
              .formatToString(this.session.resultVisibleFromTimestamp, this.session.timeZone, TIME_FORMAT);
        } else {
          this.formattedResultVisibleFromTime = 'Not applicable';
        }
        this.isFeedbackSessionLoading = false;

        // load section tabs
        this.courseService.getCourseSectionNames(courseId)
            .subscribe({
              next: (courseSectionNames: CourseSectionNames) => {
                this.sectionsModel['None'] = {
                  questions: [],
                  hasPopulated: false,
                  isTabExpanded: false,
                };
                for (const sectionName of courseSectionNames.sectionNames) {
                  this.sectionsModel[sectionName] = {
                    questions: [],
                    hasPopulated: false,
                    isTabExpanded: false,
                  };
                }
                this.isSectionsLoaded = true;
              },
              error: (resp: ErrorMessageOutput) => {
                this.hasSectionsLoadingFailed = true;
                this.statusMessageService.showErrorToast(resp.error.message);
              },
            });

        // load question tabs
        this.feedbackQuestionsService.getFeedbackQuestions({
          feedbackSessionId,
          intent: Intent.INSTRUCTOR_RESULT,
        }).subscribe({
          next: (feedbackQuestions: FeedbackQuestions) => {
            for (const question of feedbackQuestions.questions) {
              this.questionsModel[question.feedbackQuestionId] = {
                question,
                responses: [],
                statistics: '',
                hasPopulated: false,
                isTabExpanded: false,
              };
            }
            this.isQuestionsLoaded = true;
          },
          error: (resp: ErrorMessageOutput) => {
            this.hasQuestionsLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });

        // load all students in course
        this.studentService.getStudentsFromCourse({
          courseId,
        }).subscribe({
          next: (allStudents: Students) => {
            this.allStudentsInCourse = allStudents.students;

            // sort the student list based on team name and student name
            this.allStudentsInCourse.sort((a: Student, b: Student): number => {
              const teamNameCompare: number = a.teamName.localeCompare(b.teamName);
              if (teamNameCompare === 0) {
                return a.name.localeCompare(b.name);
              }
              return teamNameCompare;
            });

            // select the first student
            if (this.allStudentsInCourse.length >= 1) {
              this.emailOfStudentToPreview = this.allStudentsInCourse[0].email;
            }

            this.loadNoResponseStudents(feedbackSessionId);
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });

        // load all instructors in course
        this.instructorService.loadInstructors({
          courseId: this.courseId,
          intent: Intent.FULL_DETAIL,
        }).subscribe({
          next: (instructors: Instructors) => {
            this.allInstructorsInCourse = instructors.instructors;

            // sort the instructor list based on name
            this.allInstructorsInCourse.sort((a: Instructor, b: Instructor): number => {
              return a.name.localeCompare(b.name);
            });

            // select the first instructor
            if (this.allInstructorsInCourse.length >= 1) {
              this.emailOfInstructorToPreview = this.allInstructorsInCourse[0].email;
            }
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });

        // load current instructor name
        this.instructorService.getInstructor({
          courseId,
          intent: Intent.FULL_DETAIL,
        }).subscribe((instructor: Instructor) => {
          this.commentService.currInstructorName = instructor.name;
        });
      },
      error: (resp: ErrorMessageOutput) => {
        this.isFeedbackSessionLoading = false;
        this.hasFeedbackSessionLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  loadNoResponseStudents(feedbackSessionId: string): void {
    this.hasNoResponseLoadingFailed = false;
    // load no response students
    this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({
      feedbackSessionId,
    }).subscribe({
      next: (feedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet) => {
        // TODO team is missing
        this.noResponseStudents = this.allStudentsInCourse.filter((student: Student) =>
            !feedbackSessionSubmittedGiverSet.giverIdentifiers.includes(student.email));
        this.isNoResponseStudentsLoaded = true;
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasNoResponseLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
    this.isNoResponsePanelLoaded = true;
  }

  /**
   * Toggles the question tab in per question view.
   */
  toggleQuestionTab(questionId: string): void {
    this.questionsModel[questionId].isTabExpanded = !this.questionsModel[questionId].isTabExpanded;
    if (this.questionsModel[questionId].isTabExpanded) {
      this.loadQuestionTab(questionId);
    }
  }

  /**
   * Loads all the responses and response statistics for the specified question.
   */
  loadQuestionTab(questionId: string): void {
    if (this.questionsModel[questionId].hasPopulated) {
      // Do not re-fetch data
      return;
    }

    const missingRespMap: Map<string, ResponseOutput> = new Map();
    const tmpMap: Map<string, ResponseOutput> = new Map();

    if (this.hasSectionsLoadingFailed) {
      // the page would not render properly
      return;
    }
    of(...Object.keys(this.sectionsModel)).pipe(
        concatMap((sectionName: string) => {
          return this.feedbackSessionsService.getFeedbackSessionResults({
            questionId,
            feedbackSessionId: this.session.feedbackSessionId,
            intent: Intent.FULL_DETAIL,
            groupBySection: sectionName,
            sectionByGiverReceiver: 'both',
          });
        }),
    ).subscribe(
      {
        next: (resp: SessionResults) => {
          if (!resp.questions.length) {
            return;
          }
          const responses: QuestionOutput = resp.questions[0];
          responses.allResponses.forEach((response: ResponseOutput) =>
              (response.isMissingResponse
                  ? missingRespMap.set(response.responseId, response)
                  : tmpMap.set(response.responseId, response)));
          this.questionsModel[questionId].statistics = QuestionStatistics.appendStats(
              this.questionsModel[questionId].statistics,
              responses.questionStatistics);

          this.preprocessComments(responses.allResponses);
        },
        complete: () => {
          tmpMap.forEach((response: ResponseOutput) =>
              this.questionsModel[questionId].responses.push(response));
          missingRespMap.forEach((response: ResponseOutput) =>
              this.questionsModel[questionId].responses.push(response));
          this.questionsModel[questionId].errorMessage = '';
          this.questionsModel[questionId].hasPopulated = true;
        },
        error: (resp: ErrorMessageOutput) => {
          this.questionsModel[questionId].errorMessage = resp.error.message;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      },
    );
  }

  /**
   * Toggles the section tab in per section view.
   */
  toggleSectionTab(sectionName: string): void {
    this.sectionsModel[sectionName].isTabExpanded = !this.sectionsModel[sectionName].isTabExpanded;
    if (this.sectionsModel[sectionName].isTabExpanded) {
      this.loadSectionTab(sectionName);
    }
  }

  /**
   * Loads all the responses and response statistics for the specified section.
   */
  loadSectionTab(sectionName: string): void {
    if (this.sectionsModel[sectionName].hasPopulated) {
      // Do not re-fetch data
      return;
    }
    this.feedbackSessionsService.getFeedbackSessionResults({
      feedbackSessionId: this.session.feedbackSessionId,
      intent: Intent.FULL_DETAIL,
      groupBySection: sectionName,
    })
    .subscribe(
      {
        next: (resp: SessionResults) => {
          this.sectionsModel[sectionName].questions = resp.questions;

          // sort questions by question number
          resp.questions.sort((a: QuestionOutput, b: QuestionOutput) =>
            a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber);
          resp.questions.forEach((question: QuestionOutput) => {
            this.preprocessComments(question.allResponses);
          });
        },
        complete: () => {
          this.sectionsModel[sectionName].hasPopulated = true;
          this.sectionsModel[sectionName].errorMessage = '';
        },
        error: (resp: ErrorMessageOutput) => {
          this.sectionsModel[sectionName].errorMessage = resp.error.message;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      },
    );
  }

  /**
   * Preprocesses the comments from instructor.
   *
   * <p>The instructor comment will be moved to map {@code instructorCommentTableModel}. The original
   * instructor comments associated with the response will be deleted.
   */
  preprocessComments(responses: ResponseOutput[]): void {
    responses.forEach((response: ResponseOutput) => {
      this.commentService.instructorCommentTableModel[response.responseId] =
         this.commentsToCommentTableModel.transform(response.instructorComments, false, this.session.timeZone);
      this.commentService.sortComments(this.commentService.instructorCommentTableModel[response.responseId]);
      // clear the original comments for safe as instructorCommentTableModel will become the single point of truth
      response.instructorComments = [];
    });
  }

  /**
   * Handle publish result button event.
   */
  publishResultHandler(): void {
    const isPublished: boolean = this.session.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;
    let modalRef: NgbModalRef;
    if (isPublished) {
      const modalContent =
          `An email will be sent to students to inform them that the session has been unpublished
          and the session responses will no longer be viewable by students.`;
      modalRef = this.simpleModalService.openConfirmationModal(
          `Unpublish this session <strong>${this.session.feedbackSessionName}</strong>?`,
          SimpleModalType.WARNING, modalContent);
    } else {
      const modalContent =
          'An email will be sent to students to inform them that the responses are ready for viewing.';
      modalRef = this.simpleModalService.openConfirmationModal(
          `Publish this session <strong>${this.session.feedbackSessionName}</strong>?`,
          SimpleModalType.WARNING, modalContent);
    }

    modalRef.result.then(() => {
      const response: Observable<any> = isPublished
          ? this.feedbackSessionsService.unpublishFeedbackSession(
            this.session.feedbackSessionId,
          )
          : this.feedbackSessionsService.publishFeedbackSession(
            this.session.feedbackSessionId,
          );

      response.subscribe({
        next: (res: FeedbackSession) => {
          this.session = res;
          if (this.session.resultVisibleFromTimestamp) {
            this.formattedResultVisibleFromTime = this.timezoneService
                .formatToString(this.session.resultVisibleFromTimestamp, this.session.timeZone, TIME_FORMAT);
            this.statusMessageService.showSuccessToast('The feedback session has been published. '
                + 'Please allow up to 1 hour for all the notification emails to be sent out.');
          } else {
            this.formattedResultVisibleFromTime = 'Not applicable';
            this.statusMessageService.showSuccessToast('The feedback session has been unpublished.');
          }
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

  /**
   * Handle print view button event.
   */
  printViewHandler(): void {
    window.print();
  }

  /**
   * Handle download results button event.
   */
  downloadResultHandler(): void {
    this.isDownloadingResults = true;
    of(this.feedbackSessionActionsService.downloadSessionResult(
      this.courseId,
      this.session.feedbackSessionName,
      this.session.feedbackSessionId,
      Intent.FULL_DETAIL,
      this.indicateMissingResponses,
      this.showStatistics,
      Object.values(this.questionsModel).map((questionTabModel: QuestionTabModel) => questionTabModel.question),
      this.section.length === 0 ? undefined : this.section,
      this.section.length === 0 ? undefined : this.sectionType,
    )).pipe(finalize(() => {
      this.isDownloadingResults = false;
    }))
      .subscribe();
  }

  downloadQuestionResultHandler(question: { questionNumber: number, questionId: string }): void {
    const filename =
        `${this.session.courseId}_${this.session.feedbackSessionName}_question${question.questionNumber}.csv`;

    this.feedbackSessionsService.downloadSessionResults(
        this.session.feedbackSessionId,
        Intent.FULL_DETAIL,
        this.indicateMissingResponses,
        this.showStatistics,
        question.questionId,
    ).subscribe({
      next: (resp: string) => {
        const blob: any = new Blob([resp], { type: 'text/csv' });
        this.fileSaveService.saveFile(blob, filename);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Handle expand all questions button event.
   */
  toggleExpandAllHandler(): void {
    if (this.isExpandAll) {
      this.collapseAllTabs();
    } else {
      this.expandAllTabs();
    }
  }

  /**
   * Expands the tab of all sections.
   */
  expandAllTabs(): void {
    this.isExpandAll = true;

    if (this.noResponsePanel !== undefined) {
      this.noResponsePanel.expandTab();
    }

    if (this.viewType === InstructorSessionResultViewType.QUESTION) {
      for (const questionId of Object.keys(this.questionsModel)) {
        this.questionsModel[questionId].isTabExpanded = true;
        this.loadQuestionTab(questionId);
      }
      return;
    }

    for (const sectionName of Object.keys(this.sectionsModel)) {
      this.sectionsModel[sectionName].isTabExpanded = true;
      this.loadSectionTab(sectionName);
    }
  }

  /**
   * Collapses the tab of all sections.
   */
  collapseAllTabs(): void {
    this.isExpandAll = false;

    if (this.noResponsePanel !== undefined) {
      this.noResponsePanel.collapseTab();
    }

    if (this.viewType === InstructorSessionResultViewType.QUESTION) {
      for (const questionId of Object.keys(this.questionsModel)) {
        this.questionsModel[questionId].isTabExpanded = false;
      }
      return;
    }

    for (const sectionName of Object.keys(this.sectionsModel)) {
      this.sectionsModel[sectionName].isTabExpanded = false;
    }
  }

  /**
   * Handles the sending of reminders to students.
   */
  sendReminderToStudents(reminderResponse: ReminderResponseModel): void {
    this.feedbackSessionsService
      .remindFeedbackSessionSubmissionForRespondents(this.session.feedbackSessionId, {
        usersToRemind: reminderResponse.respondentsToSend.map((m) => m.id),
        isSendingCopyToInstructor: reminderResponse.isSendingCopyToInstructor,
      })
        .subscribe({
          next: () => {
            this.statusMessageService.showSuccessToast(
                'Reminder e-mails have been sent out to those students and instructors. '
                + 'Please allow up to 1 hour for all the notification emails to be sent out.');

          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  navigateToIndividualSessionResultPage(): void {
    this.navigationService.navigateByURL('/web/instructor/sessions/result',
        {
          courseid: this.courseId,
          fsname: this.fsName,
          fsid: this.feedbackSessionId,
        });
  }

}
