import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { InstructorExtensionTableColumnModel, StudentExtensionTableColumnModel } from './extension-table-column-model';
import {
  IndividualExtensionDateModalComponent,
} from './individual-extension-date-modal/individual-extension-date-modal.component';
import { CourseService } from '../../../services/course.service';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  Course,
  FeedbackSession,
  FeedbackSessionSubmittedGiverSet,
  Instructors,
  Students,
} from '../../../types/api-output';
import {
  FeedbackSessionBasicRequest,
  FeedbackSessionUpdateRequest,
  Intent,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import {
  ExtensionConfirmModalComponent,
  ExtensionModalType,
} from '../../components/extension-confirm-modal/extension-confirm-modal.component';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Send reminders to respondents modal.
 */
@Component({
  selector: 'tm-instructor-session-individual-extension-page',
  templateUrl: './instructor-session-individual-extension-page.component.html',
  styleUrls: ['./instructor-session-individual-extension-page.component.scss'],
})
export class InstructorSessionIndividualExtensionPageComponent implements OnInit {
  feedbackSessionDetails: FeedbackSessionBasicRequest = {
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTimestamp: 0,
    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTimestamp: 0,
    isClosingSoonEmailEnabled: false,
    isPublishedEmailEnabled: false,
  };

  feedbackSessionEndingTimestamp: number = 0;
  feedbackSessionTimeZone: string = 'UTC';
  courseId: string = '';
  courseName: string = '';
  feedbackSessionName: string = '';

  studentsOfCourse: StudentExtensionTableColumnModel[] = [];
  instructorsOfCourse: InstructorExtensionTableColumnModel[] = [];
  studentDeadlines: Record<string, number> = {};
  instructorDeadlines: Record<string, number> = {};

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortStudentsBy: SortBy = SortBy.SESSION_END_DATE;
  sortStudentOrder: SortOrder = SortOrder.DESC;
  sortInstructorsBy: SortBy = SortBy.SESSION_END_DATE;
  sortInstructorOrder: SortOrder = SortOrder.DESC;

  isAllStudentsSelected: boolean = false;
  isAllInstructorsSelected: boolean = false;

  isAllYetToSubmitStudentsSelected: boolean = false;
  isAllYetToSubmitInstructorsSelected: boolean = false;

  isLoadingAllStudents: boolean = true;
  hasLoadedAllStudentsFailed: boolean = false;
  isLoadingAllInstructors: boolean = true;
  hasLoadedAllInstructorsFailed: boolean = false;
  isLoadingFeedbackSession: boolean = true;
  hasLoadingFeedbackSessionFailed: boolean = false;
  isSubmittingDeadlines: boolean = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.isAllYetToSubmitInstructorsSelected = queryParams.preselectnonsubmitters === 'true';
      this.isAllYetToSubmitStudentsSelected = queryParams.preselectnonsubmitters === 'true';
      this.loadFeedbackSessionAndIndividuals();
    });
  }

  constructor(
    private statusMessageService: StatusMessageService,
    private feedbackSessionsService: FeedbackSessionsService,
    private studentService: StudentService,
    private ngbModal: NgbModal,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private tableComparatorService: TableComparatorService,
    private instructorService: InstructorService,
  ) { }

  /**
   * Loads a feedback session and individuals
   */
  loadFeedbackSessionAndIndividuals(): void {
    this.resetTables();
    this.isLoadingAllStudents = true;
    this.hasLoadedAllStudentsFailed = false;
    this.isLoadingFeedbackSession = true;
    this.hasLoadingFeedbackSessionFailed = false;
    this.isLoadingAllInstructors = true;
    this.hasLoadedAllStudentsFailed = false;
    forkJoin([
      this.courseService.getCourseAsInstructor(this.courseId),
      this.feedbackSessionsService.getFeedbackSession({
        courseId: this.courseId,
        feedbackSessionName: this.feedbackSessionName,
        intent: Intent.FULL_DETAIL,
      }),
    ])
      .pipe(finalize(() => {
        this.isLoadingFeedbackSession = false;
        this.isLoadingAllStudents = false;
        this.isLoadingAllInstructors = false;
      }))
      .subscribe({
        next: ([course, feedbackSession]: [Course, FeedbackSession]) => {
          this.courseName = course.courseName;
          this.setFeedbackSessionDetails(feedbackSession);
          this.getAllStudentsOfCourse(); // Both students and instructors need feedback ending time.
          this.getAllInstructorsOfCourse();
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadingFeedbackSessionFailed = true;
        },
      });
  }

  /**
   * Gets all students of a course.
   */
  private getAllStudentsOfCourse(): void {
    this.studentService
      .getStudentsFromCourse({ courseId: this.courseId })
      .pipe(map(({ students }: Students) => DeadlineExtensionHelper
        .mapStudentsToStudentModels(students, this.studentDeadlines, this.feedbackSessionEndingTimestamp)),
      )
      .subscribe({
        next: (studentModels: StudentExtensionTableColumnModel[]) => {
          this.studentsOfCourse = studentModels;
          this.initialSortOfStudents();
          this.getNonSubmitterStudents();

          if (this.isAllYetToSubmitStudentsSelected) {
            this.changeSelectionStatusForAllYetSubmittedStudentsHandler(true);
          }
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadedAllStudentsFailed = true;
        },
      });
  }

  private setFeedbackSessionDetails(feedbackSession: FeedbackSession): void {
    this.feedbackSessionDetails = {
      instructions: feedbackSession.instructions,
      submissionStartTimestamp: feedbackSession.submissionStartTimestamp,
      submissionEndTimestamp: feedbackSession.submissionEndTimestamp,
      gracePeriod: feedbackSession.gracePeriod,
      sessionVisibleSetting: feedbackSession.sessionVisibleSetting,
      customSessionVisibleTimestamp: feedbackSession.customSessionVisibleTimestamp,
      responseVisibleSetting: feedbackSession.responseVisibleSetting,
      customResponseVisibleTimestamp: feedbackSession.customResponseVisibleTimestamp,
      isClosingSoonEmailEnabled: feedbackSession.isClosingSoonEmailEnabled,
      isPublishedEmailEnabled: feedbackSession.isPublishedEmailEnabled,
    };
    this.feedbackSessionEndingTimestamp = feedbackSession.submissionEndTimestamp;
    this.feedbackSessionTimeZone = feedbackSession.timeZone;
    this.studentDeadlines = feedbackSession.studentDeadlines ?? {};
    this.instructorDeadlines = feedbackSession.instructorDeadlines ?? {};
  }

  private initialSortOfStudents(): void {
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(SortBy.TEAM_NAME));
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(SortBy.SECTION_NAME));
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(SortBy.SESSION_END_DATE));
  }

  private getNonSubmitterStudents(): void {
    this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
    }).subscribe({
      next: (feedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet) => {
        this.studentsOfCourse
          .forEach((studentColumnModel: StudentExtensionTableColumnModel) => {
            studentColumnModel.hasSubmittedSession =
              feedbackSessionSubmittedGiverSet.giverIdentifiers.includes(studentColumnModel.email);
          });
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasLoadedAllStudentsFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Changes selection state for all yet to submit students.
   */
  changeSelectionStatusForAllYetSubmittedStudentsHandler(shouldSelect: boolean): void {
    this.isAllYetToSubmitStudentsSelected = shouldSelect;

    this.studentsOfCourse.forEach((model: StudentExtensionTableColumnModel) => {
      if (!model.hasSubmittedSession) {
        model.isSelected = shouldSelect;
      }
    });
    this.updateSelectAllStudents();
  }

  /**
   * Loads the instructors in the course
   */
  private getAllInstructorsOfCourse(): void {
    this.instructorService
      .loadInstructors({ courseId: this.courseId, intent: Intent.FULL_DETAIL })
      .pipe(map(({ instructors }: Instructors) => DeadlineExtensionHelper
        .mapInstructorsToInstructorModels(instructors, this.instructorDeadlines, this.feedbackSessionEndingTimestamp)))
      .subscribe({
        next: (instructorModels: InstructorExtensionTableColumnModel[]) => {
          this.instructorsOfCourse = instructorModels;
          this.initialSortOfInstructors();
          this.getNonSubmitterInstructors();

          if (this.isAllYetToSubmitInstructorsSelected) {
            this.changeSelectionStatusForAllYetSubmittedInstructorsHandler(true);
          }
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasLoadedAllInstructorsFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  private initialSortOfInstructors(): void {
    this.instructorsOfCourse.sort(this.sortInstructorPanelsBy(SortBy.INSTRUCTOR_PERMISSION_ROLE));
    this.instructorsOfCourse.sort(this.sortInstructorPanelsBy(SortBy.SESSION_END_DATE));
  }

  private getNonSubmitterInstructors(): void {
    this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({
      courseId: this.courseId,
      feedbackSessionName: this.feedbackSessionName,
    }).subscribe({
      next: (feedbackSessionSubmittedGiverSet: FeedbackSessionSubmittedGiverSet) => {
        this.instructorsOfCourse
          .forEach((instructorColumnModel: InstructorExtensionTableColumnModel) => {
            instructorColumnModel.hasSubmittedSession =
              feedbackSessionSubmittedGiverSet.giverIdentifiers.includes(instructorColumnModel.email);
          });
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasLoadedAllInstructorsFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Changes selection state for all yet to submit instructors.
   */
  changeSelectionStatusForAllYetSubmittedInstructorsHandler(shouldSelect: boolean): void {
    this.isAllYetToSubmitInstructorsSelected = shouldSelect;

    this.instructorsOfCourse.forEach((model: InstructorExtensionTableColumnModel) => {
      if (!model.hasSubmittedSession) {
        model.isSelected = shouldSelect;
      }
    });
    this.updateSelectAllInstructors();
  }

  /**
   * Handles the the date and time selection modal to create/update deadlines.
   */
  onExtend(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionDateModalComponent);
    modalRef.componentInstance.numStudents = this.getNumberOfSelectedStudents();
    modalRef.componentInstance.numInstructors = this.getNumberOfSelectedInstructors();
    modalRef.componentInstance.feedbackSessionEndingTimestamp = this.feedbackSessionEndingTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.confirmCallbackEvent.subscribe((extensionTimestamp: number) => {
      this.onConfirmExtension(extensionTimestamp);
      modalRef.close();
    });
  }

  /**
   * Handles the opening the confirmation modal to create/update deadlines.
   */
  private onConfirmExtension(extensionTimestamp: number): void {
    const modalRef: NgbModalRef = this.ngbModal.open(ExtensionConfirmModalComponent);
    const selectedStudents = this.getSelectedStudents();
    const selectedInstructors = this.getSelectedInstructors();
    modalRef.componentInstance.modalType = ExtensionModalType.EXTEND;
    modalRef.componentInstance.selectedStudents = selectedStudents;
    modalRef.componentInstance.selectedInstructors = selectedInstructors;
    modalRef.componentInstance.extensionTimestamp = extensionTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;

    modalRef.componentInstance.confirmExtensionCallbackEvent.subscribe((isNotifyDeadlines: boolean) => {
      this.handleCreateDeadlines(selectedStudents, selectedInstructors, isNotifyDeadlines, extensionTimestamp);
      modalRef.componentInstance.isSubmitting = false;
      modalRef.close();
    });
  }

  /**
   * Handles the opening the confirmation modal to delete deadlines.
   */
  onDelete(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(ExtensionConfirmModalComponent);
    const selectedStudents = this.getSelectedStudentsWithExtensions();
    const selectedInstructors = this.getSelectedInstructorsWithExtensions();
    modalRef.componentInstance.modalType = ExtensionModalType.DELETE;
    modalRef.componentInstance.selectedStudents = selectedStudents;
    modalRef.componentInstance.selectedInstructors = selectedInstructors;
    modalRef.componentInstance.extensionTimestamp = this.feedbackSessionEndingTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;

    modalRef.componentInstance.confirmExtensionCallbackEvent.subscribe((isNotifyDeadlines: boolean) => {
      this.handleDeleteDeadlines(selectedStudents, selectedInstructors, isNotifyDeadlines);
      modalRef.componentInstance.isSubmitting = false;
      modalRef.close();
    });
  }

  private handleCreateDeadlines(
    selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[],
    isNotifyDeadlines: boolean,
    extensionTimestamp: number,
  ): void {
    const updatedDeadlinesForCreation = this.getUpdatedDeadlinesForCreation(
      selectedStudents, selectedInstructors, extensionTimestamp);
    const request: FeedbackSessionUpdateRequest = {
      ...updatedDeadlinesForCreation,
      ...this.feedbackSessionDetails,
    };

    this.handleUpdateDeadlines(request, selectedStudents.length,
      selectedInstructors.length, isNotifyDeadlines, 'created');
  }

  private handleDeleteDeadlines(
    selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[],
    isNotifyDeadlines: boolean,
  ): void {
    const updatedDeadlinesForDeletion = this.getUpdatedDeadlinesForDeletion(
      selectedStudents, selectedInstructors);
    const request: FeedbackSessionUpdateRequest = {
      ...updatedDeadlinesForDeletion,
      ...this.feedbackSessionDetails,
    };
    this.handleUpdateDeadlines(request, selectedStudents.length,
      selectedInstructors.length, isNotifyDeadlines, 'deleted');
  }

  private handleUpdateDeadlines(
    request: FeedbackSessionUpdateRequest,
    numStudentsUpdated: number,
    numInstructorsUpdated: number,
    isNotifyDeadlines: boolean,
    actionForToast: string,
  ): void {
    this.isSubmittingDeadlines = true;
    this.feedbackSessionsService
      .updateFeedbackSession(this.courseId, this.feedbackSessionName, request, isNotifyDeadlines)
      .pipe(finalize(() => { this.isSubmittingDeadlines = false; }))
      .subscribe({
        next: () => {
          this.loadFeedbackSessionAndIndividuals();
          this.showSuccessToast(actionForToast, numStudentsUpdated, numInstructorsUpdated);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  private getUpdatedDeadlinesForCreation(selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[], extensionTimestamp: number,
  ): { studentDeadlines: Record<string, number>, instructorDeadlines: Record<string, number> } {
    const studentDeadlines = DeadlineExtensionHelper.getUpdatedDeadlinesForCreation(
      selectedStudents, this.studentDeadlines, extensionTimestamp);
    const instructorDeadlines = DeadlineExtensionHelper.getUpdatedDeadlinesForCreation(
      selectedInstructors, this.instructorDeadlines, extensionTimestamp);

    return { studentDeadlines, instructorDeadlines };
  }

  private getUpdatedDeadlinesForDeletion(selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[],
  ): { studentDeadlines: Record<string, number>, instructorDeadlines: Record<string, number> } {
    const studentDeadlines = DeadlineExtensionHelper.getUpdatedDeadlinesForDeletion(
      selectedStudents, this.studentDeadlines);
    const instructorDeadlines = DeadlineExtensionHelper.getUpdatedDeadlinesForDeletion(
      selectedInstructors, this.instructorDeadlines);

    return { studentDeadlines, instructorDeadlines };
  }

  private showSuccessToast(updateAction: string, numOfStudentsUpdated: number, numOfInstructorsUpdated: number): void {
    this.statusMessageService.showSuccessToast(
      `Successfully ${updateAction} extension(s) for ${numOfStudentsUpdated} student(s) and`
      + ` ${numOfInstructorsUpdated} instructor(s)!`);
  }

  private getSelectedStudents(): StudentExtensionTableColumnModel[] {
    return this.studentsOfCourse.filter((x) => x.isSelected);
  }

  getNumberOfSelectedStudents(): number {
    return this.getSelectedStudents().length;
  }

  private getSelectedStudentsWithExtensions(): StudentExtensionTableColumnModel[] {
    return this.studentsOfCourse.filter((x) => x.isSelected && x.hasExtension);
  }

  private getSelectedInstructors(): InstructorExtensionTableColumnModel[] {
    return this.instructorsOfCourse.filter((x) => x.isSelected);
  }

  private getNumberOfSelectedInstructors(): number {
    return this.getSelectedInstructors().length;
  }

  private getSelectedInstructorsWithExtensions(): InstructorExtensionTableColumnModel[] {
    return this.instructorsOfCourse.filter((x) => x.isSelected && x.hasExtension);
  }

  hasSelected(): boolean {
    return this.studentsOfCourse.some((user) => user.isSelected)
      || this.instructorsOfCourse.some((user) => user.isSelected);
  }

  /**
   * Checks if at least one valid extension has been selected
   */
  hasSelectedValidForDeletion(): boolean {
    return this.studentsOfCourse.some((user) => user.isSelected && user.hasExtension)
      || this.instructorsOfCourse.some((user) => user.isSelected && user.hasExtension);
  }

  selectAllStudents(): void {
    this.isAllStudentsSelected = !this.isAllStudentsSelected;
    this.studentsOfCourse.forEach((x) => { x.isSelected = this.isAllStudentsSelected; });
  }

  selectAllInstructors(): void {
    this.isAllInstructorsSelected = !this.isAllInstructorsSelected;
    this.instructorsOfCourse.forEach((x) => { x.isSelected = this.isAllInstructorsSelected; });
  }

  selectStudent(i: number): void {
    this.studentsOfCourse[i].isSelected = !this.studentsOfCourse[i].isSelected;
    this.updateSelectAllStudents();
  }

  private updateSelectAllStudents(): void {
    const numStudentsSelected = this.getNumberOfSelectedStudents();
    const numStudents = this.studentsOfCourse.length;
    this.isAllStudentsSelected = numStudentsSelected === numStudents;
  }

  selectIntructor(i: number): void {
    this.instructorsOfCourse[i].isSelected = !this.instructorsOfCourse[i].isSelected;
    this.updateSelectAllInstructors();
  }

  private updateSelectAllInstructors(): void {
    const numInstructorsSelected = this.getNumberOfSelectedInstructors();
    const numInstructors = this.instructorsOfCourse.length;
    this.isAllInstructorsSelected = numInstructorsSelected === numInstructors;
  }

  sortStudentColumnsBy(by: SortBy): void {
    this.sortStudentsBy = by;
    this.sortStudentOrder = this.sortStudentOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(by));
  }

  getAriaSortStudent(by: SortBy): string {
    if (by !== this.sortStudentsBy) {
      return 'none';
    }
    return this.sortStudentOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  private resetTables(): void {
    this.isAllInstructorsSelected = false;
    this.isAllStudentsSelected = false;
  }

  private sortStudentPanelsBy(
    by: SortBy,
  ): (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel) => number {
    return (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.teamName;
          strB = b.teamName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortStudentOrder, strA, strB);
    };
  }

  sortInstructorsColumnsBy(by: SortBy): void {
    this.sortInstructorsBy = by;
    this.sortInstructorOrder = this.sortInstructorOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.instructorsOfCourse.sort(this.sortInstructorPanelsBy(by));
  }

  getAriaSortInstructor(by: SortBy): string {
    if (by !== this.sortInstructorsBy) {
      return 'none';
    }
    return this.sortInstructorOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  private sortInstructorPanelsBy(
    by: SortBy,
  ): (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel) => number {
    return (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.INSTRUCTOR_PERMISSION_ROLE:
          strA = a.role || '';
          strB = b.role || '';
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortInstructorOrder, strA, strB);
    };
  }

  getAriaLabel(user: StudentExtensionTableColumnModel | InstructorExtensionTableColumnModel): string {
    return `Select ${user.name}`;
  }
}
