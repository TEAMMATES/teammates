import { Component, Input, OnInit, ViewChild, inject } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { of } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';
import { InstructorSessionResultGqrViewComponent } from './instructor-session-result-gqr-view.component';
import { InstructorSessionResultGrqViewComponent } from './instructor-session-result-grq-view.component';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';
import { InstructorSessionResultRgqViewComponent } from './instructor-session-result-rgq-view.component';
import { InstructorSessionResultRqgViewComponent } from './instructor-session-result-rqg-view.component';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';
import {
  SectionTabModel,
  QuestionTabModel,
  NO_SPECIFIC_SECTION_ID,
  NO_SPECIFIC_SECTION_NAME,
} from './instructor-session-tab.model';
import { CourseService } from '../../../services/course.service';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { FeedbackSessionActionsService } from '../../../services/feedback-session-actions.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { FileSaveService } from '../../../services/file-save.service';
import { InstructorCommentEventData, InstructorCommentService } from '../../../services/instructor-comment.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TimezoneService } from '../../../services/timezone.service';
import {
  FeedbackQuestions,
  FeedbackSession,
  FeedbackSessionView,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  Instructor,
  Instructors,
  QuestionOutput,
  ResponseOutput,
  ResponseVisibleSetting,
  SessionResults,
  Student,
  Students,
} from '../../../types/api-output';
import { AjaxLoadingComponent } from '../../components/ajax-loading/ajax-loading.component';
import { CommentTableModel } from '../../components/comment-box/comment-table/comment-table.model';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PreviewSessionResultPanelComponent } from '../../components/preview-session-result-panel/preview-session-result-panel.component';
import { ReminderResponseModel } from '../../components/sessions-table/send-reminders-to-respondents-modal/send-reminders-to-respondents-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { RouterLink } from '@angular/router';
import { ViewResultsPanelComponent } from '../../components/view-results-panel/view-results-panel.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { commentToReadOnlyComment } from '../../utils/comment-to-comment-table.util';

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
    RouterLink,
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
  providers: [CommentsToCommentTableModelPipe],
})
export class InstructorSessionResultPageComponent implements OnInit {
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly feedbackSessionActionsService = inject(FeedbackSessionActionsService);
  private readonly feedbackQuestionsService = inject(FeedbackQuestionsService);
  private readonly feedbackResponsesService = inject(FeedbackResponsesService);
  private readonly courseService = inject(CourseService);
  private readonly fileSaveService = inject(FileSaveService);
  private readonly studentService = inject(StudentService);
  private readonly instructorService = inject(InstructorService);
  private readonly timezoneService = inject(TimezoneService);
  private readonly simpleModalService = inject(SimpleModalService);
  private readonly navigationService = inject(NavigationService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly commentService = inject(InstructorCommentService);

  // enum
  InstructorSessionResultSectionType!: typeof InstructorSessionResultSectionType;
  InstructorSessionResultViewType!: typeof InstructorSessionResultViewType;

  formattedSessionOpeningTime = '';
  formattedSessionClosingTime = '';
  formattedResultVisibleFromTime = '';

  courseId = '';
  fsName = '';
  viewType: string = InstructorSessionResultViewType.QUESTION;
  section = '';
  sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  groupByTeam = true;
  showStatistics = true;
  indicateMissingResponses = true;

  instructorCommentTableModel: Record<string, CommentTableModel> = {};

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
  userIdOfStudentToPreview = '';
  allInstructorsInCourse: Instructor[] = [];
  userIdOfInstructorToPreview = '';
  currentInstructorId = '';

  FeedbackSessionPublishStatus!: typeof FeedbackSessionPublishStatus;
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
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };

  @Input({ required: true }) feedbackSessionId!: string;

  @ViewChild(InstructorSessionNoResponsePanelComponent) noResponsePanel?: InstructorSessionNoResponsePanelComponent;

  constructor() {
    this.InstructorSessionResultSectionType = InstructorSessionResultSectionType;
    this.InstructorSessionResultViewType = InstructorSessionResultViewType;
    this.FeedbackSessionPublishStatus = FeedbackSessionPublishStatus;
    this.timezoneService.getTzVersion(); // import timezone service to load timezone data
  }

  ngOnInit(): void {
    this.loadFeedbackSessionResults(this.feedbackSessionId);
  }

  loadFeedbackSessionResults(feedbackSessionId: string): void {
    this.hasQuestionsLoadingFailed = false;
    this.hasSectionsLoadingFailed = false;
    this.hasFeedbackSessionLoadingFailed = false;
    this.isFeedbackSessionLoading = true;
    this.feedbackSessionsService
      .getFeedbackSession({
        feedbackSessionId,
      })
      .subscribe({
        next: (feedbackSessionView: FeedbackSessionView) => {
          const feedbackSession = feedbackSessionView.feedbackSession;
          this.session = feedbackSession;
          this.feedbackSessionId = feedbackSession.feedbackSessionId!;
          this.courseId = feedbackSession.courseId;
          this.fsName = feedbackSession.feedbackSessionName;
          this.formattedSessionOpeningTime = this.timezoneService.formatToString(
            this.session.submissionStartTimestamp,
            this.session.timeZone,
            TIME_FORMAT,
          );
          this.formattedSessionClosingTime = this.timezoneService.formatToString(
            this.session.submissionEndTimestamp,
            this.session.timeZone,
            TIME_FORMAT,
          );
          if (this.session.responseVisibleSetting === ResponseVisibleSetting.AT_VISIBLE) {
            this.formattedResultVisibleFromTime = this.timezoneService.formatToString(
              this.session.submissionStartTimestamp,
              this.session.timeZone,
              TIME_FORMAT,
            );
          } else if (this.session.resultVisibleFromTimestamp) {
            this.formattedResultVisibleFromTime = this.timezoneService.formatToString(
              this.session.resultVisibleFromTimestamp,
              this.session.timeZone,
              TIME_FORMAT,
            );
          } else {
            this.formattedResultVisibleFromTime = 'Not applicable';
          }
          this.instructorService.getOwnInstructor({ courseId: this.courseId }).subscribe({
            next: (instructor: Instructor) => {
              this.currentInstructorId = instructor.userId;
              this.isFeedbackSessionLoading = false;
              this.loadPageData(feedbackSessionId);
            },
            error: (resp: ErrorMessageOutput) => {
              this.isFeedbackSessionLoading = false;
              this.hasFeedbackSessionLoadingFailed = true;
              this.statusMessageService.showErrorToast(resp.error.message);
            },
          });
        },
        error: (resp: ErrorMessageOutput) => {
          this.isFeedbackSessionLoading = false;
          this.hasFeedbackSessionLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  private loadPageData(feedbackSessionId: string): void {
    // load section tabs
    this.courseService.getCourseSections(this.courseId).subscribe({
      next: (courseSections) => {
        this.sectionsModel[NO_SPECIFIC_SECTION_ID] = {
          section: {
            sectionId: NO_SPECIFIC_SECTION_ID,
            sectionName: NO_SPECIFIC_SECTION_NAME,
          },
          questions: [],
          hasPopulated: false,
          isTabExpanded: false,
        };
        for (const section of courseSections.sections) {
          this.sectionsModel[section.sectionId] = {
            section,
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
    this.feedbackQuestionsService
      .getFeedbackQuestions({
        feedbackSessionId,
      })
      .subscribe({
        next: (feedbackQuestions: FeedbackQuestions) => {
          for (const question of feedbackQuestions.questions) {
            this.questionsModel[question.feedbackQuestionId] = {
              question,
              responses: [],
              statistics: undefined,
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
    this.studentService
      .getStudents({
        courseIds: [this.courseId],
      })
      .subscribe({
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
            this.userIdOfStudentToPreview = this.allStudentsInCourse[0].userId;
          }

          this.loadNoResponseStudents(feedbackSessionId);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });

    // load all instructors in course
    this.instructorService.loadInstructors({ courseId: this.courseId }).subscribe({
      next: (instructors: Instructors) => {
        this.allInstructorsInCourse = instructors.instructors;

        // sort the instructor list based on name
        this.allInstructorsInCourse.sort((a: Instructor, b: Instructor): number => {
          return a.name.localeCompare(b.name);
        });

        // select the first instructor
        if (this.allInstructorsInCourse.length >= 1) {
          this.userIdOfInstructorToPreview = this.allInstructorsInCourse[0].userId;
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  loadNoResponseStudents(feedbackSessionId: string): void {
    this.hasNoResponseLoadingFailed = false;
    // load no response students
    this.feedbackSessionsService
      .getFeedbackSessionSubmittedGiverSet({
        feedbackSessionId,
      })
      .subscribe({
        next: (feedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet) => {
          this.noResponseStudents = this.allStudentsInCourse.filter((student: Student) =>
            feedbackSessionSubmittedGiverSet.studentNonGivers.includes(student.userId),
          );
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

    this.feedbackSessionsService
      .getCourseSessionResults({
        questionId,
        feedbackSessionId: this.session.feedbackSessionId,
      })
      .subscribe({
        next: (resp: SessionResults) => {
          if (!resp.questions.length) {
            this.questionsModel[questionId].errorMessage = '';
            this.questionsModel[questionId].hasPopulated = true;
            return;
          }
          const responses: QuestionOutput = resp.questions[0];
          this.questionsModel[questionId].responses.push(...responses.allResponses);
          this.questionsModel[questionId].statistics = responses.questionStatistics;
          this.preprocessComments(responses.allResponses);
          this.questionsModel[questionId].errorMessage = '';
          this.questionsModel[questionId].hasPopulated = true;
        },
        error: (resp: ErrorMessageOutput) => {
          this.questionsModel[questionId].errorMessage = resp.error.message;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Toggles the section tab in per section view.
   */
  toggleSectionTab(sectionId: string): void {
    this.sectionsModel[sectionId].isTabExpanded = !this.sectionsModel[sectionId].isTabExpanded;
    if (this.sectionsModel[sectionId].isTabExpanded) {
      this.loadSectionTab(sectionId);
    }
  }

  /**
   * Loads all the responses and response statistics for the specified section.
   */
  loadSectionTab(sectionId: string): void {
    if (this.sectionsModel[sectionId].hasPopulated) {
      // Do not re-fetch data
      return;
    }
    this.feedbackSessionsService
      .getCourseSessionResults({
        feedbackSessionId: this.session.feedbackSessionId,
      })
      .subscribe({
        next: (resp: SessionResults) => {
          // sort questions by question number
          resp.questions.sort(
            (a: QuestionOutput, b: QuestionOutput) =>
              a.feedbackQuestion.questionNumber - b.feedbackQuestion.questionNumber,
          );
          resp.questions.forEach((question: QuestionOutput) => {
            question.allResponses = question.allResponses.filter((response: ResponseOutput) =>
              this.feedbackResponsesService.isFeedbackResponsesDisplayedOnSection(
                response,
                sectionId,
                InstructorSessionResultSectionType.EITHER,
              ),
            );
            this.preprocessComments(question.allResponses);
          });
          this.sectionsModel[sectionId].questions = resp.questions;
        },
        complete: () => {
          this.sectionsModel[sectionId].hasPopulated = true;
          this.sectionsModel[sectionId].errorMessage = '';
        },
        error: (resp: ErrorMessageOutput) => {
          this.sectionsModel[sectionId].errorMessage = resp.error.message;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  get selectedSectionName(): string {
    return this.section.length === 0 ? '' : this.getSectionName(this.section);
  }

  private getSectionName(sectionId: string): string {
    return this.sectionsModel[sectionId]?.section.sectionName ?? sectionId;
  }

  /**
   * Preprocesses the comments from instructor.
   *
   * <p>The instructor comment will be moved to map {@code instructorCommentTableModel}. The original
   * instructor comments associated with the response will be deleted.
   */
  preprocessComments(responses: ResponseOutput[]): void {
    responses.forEach((response: ResponseOutput) => {
      this.instructorCommentTableModel[response.responseId] = commentToReadOnlyComment(
        response.instructorComments,
        false,
        this.session.timeZone,
        this.currentInstructorId,
      );
      this.commentService.sortComments(this.instructorCommentTableModel[response.responseId]);
      // clear the original comments for safe as instructorCommentTableModel will become the single point of truth
      response.instructorComments = [];
    });
  }

  /**
   * Handles saving a new instructor comment.
   */
  saveNewCommentEventHandler(responseId: string): void {
    this.commentService.saveNewComment({
      responseId,
      timezone: this.session.timeZone,
      instructorCommentTableModel: this.instructorCommentTableModel,
    });
  }

  /**
   * Handles deleting an instructor comment.
   */
  deleteCommentEventHandler(data: InstructorCommentEventData): void {
    this.commentService.deleteComment({
      data,
      instructorCommentTableModel: this.instructorCommentTableModel,
    });
  }

  /**
   * Handles updating an instructor comment.
   */
  updateCommentEventHandler(data: InstructorCommentEventData): void {
    this.commentService.updateComment({
      data,
      timezone: this.session.timeZone,
      instructorCommentTableModel: this.instructorCommentTableModel,
    });
  }

  /**
   * Handle publish result button event.
   */
  publishResultHandler(): void {
    const isPublished: boolean = this.session.publishStatus === FeedbackSessionPublishStatus.PUBLISHED;
    let modalRef: NgbModalRef;
    if (isPublished) {
      const modalContent = `An email will be sent to students to inform them that the session has been unpublished
          and the session responses will no longer be viewable by students.`;
      modalRef = this.simpleModalService.openConfirmationModal(
        `Unpublish this session <strong>${this.session.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING,
        modalContent,
      );
    } else {
      const modalContent = 'An email will be sent to students to inform them that the responses are ready for viewing.';
      modalRef = this.simpleModalService.openConfirmationModal(
        `Publish this session <strong>${this.session.feedbackSessionName}</strong>?`,
        SimpleModalType.WARNING,
        modalContent,
      );
    }

    modalRef.result.then(
      () => {
        const response = isPublished
          ? this.feedbackSessionsService.unpublishFeedbackSession(this.session.feedbackSessionId)
          : this.feedbackSessionsService.publishFeedbackSession(this.session.feedbackSessionId);

        response.subscribe({
          next: (res: FeedbackSession) => {
            this.session = res;
            if (this.session.resultVisibleFromTimestamp) {
              this.formattedResultVisibleFromTime = this.timezoneService.formatToString(
                this.session.resultVisibleFromTimestamp,
                this.session.timeZone,
                TIME_FORMAT,
              );
              this.statusMessageService.showSuccessToast(
                'The feedback session has been published. ' +
                  'Please allow up to 1 hour for all the notification emails to be sent out.',
              );
            } else {
              this.formattedResultVisibleFromTime = 'Not applicable';
              this.statusMessageService.showSuccessToast('The feedback session has been unpublished.');
            }
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }

  /**
   * Handle print view button event.
   */
  printViewHandler(): void {
    globalThis.print();
  }

  /**
   * Handle download results button event.
   */
  downloadResultHandler(): void {
    this.isDownloadingResults = true;
    of(
      this.feedbackSessionActionsService.downloadSessionResult(
        this.courseId,
        this.session.feedbackSessionName,
        this.session.feedbackSessionId,
        this.indicateMissingResponses,
        this.showStatistics,
        Object.values(this.questionsModel).map((questionTabModel: QuestionTabModel) => questionTabModel.question),
        this.section.length === 0
          ? undefined
          : {
              groupBySectionId: this.section,
              sectionDetail: this.sectionType,
              sectionNameForCsv: this.selectedSectionName,
            },
      ),
    )
      .pipe(
        finalize(() => {
          this.isDownloadingResults = false;
        }),
      )
      .subscribe();
  }

  downloadQuestionResultHandler(question: { questionNumber: number; questionId: string }): void {
    const filename = `${this.session.courseId}_${this.session.feedbackSessionName}_question${question.questionNumber}.csv`;

    this.feedbackSessionsService
      .downloadSessionResults(
        this.session.feedbackSessionId,
        this.indicateMissingResponses,
        this.showStatistics,
        question.questionId,
      )
      .subscribe({
        next: (resp: string) => {
          const blob = new Blob([resp], { type: 'text/csv' });
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

    for (const sectionId of Object.keys(this.sectionsModel)) {
      this.sectionsModel[sectionId].isTabExpanded = true;
      this.loadSectionTab(sectionId);
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

    for (const sectionId of Object.keys(this.sectionsModel)) {
      this.sectionsModel[sectionId].isTabExpanded = false;
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
            'Reminder e-mails have been sent out to those students and instructors. ' +
              'Please allow up to 1 hour for all the notification emails to be sent out.',
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  navigateToIndividualSessionResultPage(): void {
    this.navigationService.navigateByURL(`/web/instructor/sessions/${this.feedbackSessionId}/result`);
  }
}
