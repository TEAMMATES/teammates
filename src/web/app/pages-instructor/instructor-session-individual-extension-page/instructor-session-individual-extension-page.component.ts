import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { Course, FeedbackSession, Instructor, Instructors, Student, Students } from '../../../types/api-output';
import {
  FeedbackSessionBasicRequest,
  FeedbackSessionUpdateRequest,
  Intent,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../components/sortable-table/sortable-table.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorExtensionTableColumnModel, StudentExtensionTableColumnModel } from './extension-table-column-model';
import {
  ExtensionModalType,
  IndividualExtensionConfirmModalComponent,
} from './individual-extension-confirm-modal/individual-extension-confirm-modal.component';
import {
  IndividualExtensionDateModalComponent,
} from './individual-extension-date-modal/individual-extension-date-modal.component';

/**
 * Send reminders to respondents modal.
 */
@Component({
  selector: 'tm-instructor-session-individual-extension-page',
  templateUrl: './instructor-session-individual-extension-page.component.html',
  styleUrls: ['./instructor-session-individual-extension-page.component.scss'],
})
export class InstructorSessionIndividualExtensionPageComponent implements OnInit {

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortStudentsBy: SortBy = SortBy.SESSION_END_DATE;
  sortStudentOrder: SortOrder = SortOrder.DESC;
  sortInstructorsBy: SortBy = SortBy.SESSION_END_DATE;
  sortInstructorOrder: SortOrder = SortOrder.DESC;

  isAllStudentsSelected: boolean = false;
  isAllInstructorsSelected: boolean = false;

  courseId: string = '';
  courseName: string = '';
  feedbackSessionName: string = '';

  feedbackSessionEndingTimestamp: number = 0;
  feedbackSessionTimeZone: string = 'UTC';

  feedbackSessionDetails: FeedbackSessionBasicRequest = {
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    customSessionVisibleTimestamp: 0,
    responseVisibleSetting: ResponseVisibleSetting.CUSTOM,
    customResponseVisibleTimestamp: 0,
    isClosingEmailEnabled: false,
    isPublishedEmailEnabled: false,
  };

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  studentsOfCourse: StudentExtensionTableColumnModel[] = [];
  instructorsOfCourse: InstructorExtensionTableColumnModel[] = [];
  studentDeadlines: Record<string, number> = {};
  instructorDeadlines: Record<string, number> = {};

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
  ) {}

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
      .pipe(finalize(() => { this.isLoadingFeedbackSession = false; }))
      .subscribe(
        (value: any[]) => {
          const course = value[0] as Course;
          this.courseName = course.courseName;
          const feedbackSession = value[1] as FeedbackSession;
          this.setFeedbackSessionDetails(feedbackSession);
          this.getAllStudentsOfCourse(); // Both students and instructors need feedback ending time.
          this.getAllInstructorsOfCourse();
        },
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadingFeedbackSessionFailed = true;
          this.isLoadingAllStudents = false;
          this.isLoadingAllInstructors = false;
        },
      );
  }

  /**
   * Gets all students of a course.
   */
  private getAllStudentsOfCourse(): void {
    this.studentService
      .getStudentsFromCourse({ courseId: this.courseId })
      .pipe(finalize(() => { this.isLoadingAllStudents = false; }),
        map(({ students }: Students) => this.mapStudentsToStudentModels(students)),
      )
      .subscribe(
        (studentModels: StudentExtensionTableColumnModel[]) => {
          this.studentsOfCourse = studentModels;
          this.initialSortOfStudents();
        },
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadedAllStudentsFailed = true;
        },
      );
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
      isClosingEmailEnabled: feedbackSession.isClosingEmailEnabled,
      isPublishedEmailEnabled: feedbackSession.isPublishedEmailEnabled,
    };
    this.feedbackSessionEndingTimestamp = feedbackSession.submissionEndTimestamp;
    this.feedbackSessionTimeZone = feedbackSession.timeZone;
    this.studentDeadlines = feedbackSession.studentDeadlines ?? {};
    this.instructorDeadlines = feedbackSession.instructorDeadlines ?? {};
  }

  private mapStudentsToStudentModels(students: Student[]): StudentExtensionTableColumnModel[] {
    return students.map((student) => {
      const studentData: StudentExtensionTableColumnModel = {
        sectionName: student.sectionName,
        teamName: student.teamName,
        name: student.name,
        email: student.email,
        extensionDeadline: this.feedbackSessionEndingTimestamp,
        hasExtension: false,
        isSelected: false,
      };

      if (student.email in this.studentDeadlines) {
        studentData.hasExtension = true;
        studentData.extensionDeadline = this.studentDeadlines[student.email];
      }

      return studentData;
    });
  }

  private initialSortOfStudents(): void {
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(SortBy.TEAM_NAME));
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(SortBy.SECTION_NAME));
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(SortBy.SESSION_END_DATE));
  }

  /**
   * Loads the instructors in the course
   */
  private getAllInstructorsOfCourse(): void {
    this.instructorService
      .loadInstructors({ courseId: this.courseId, intent: Intent.FULL_DETAIL })
      .pipe(
        finalize(() => { this.isLoadingAllInstructors = false; }),
        map(({ instructors }: Instructors) => this.mapInstructorsToInstructorModels(instructors)),
      )
      .subscribe((instructorModels: InstructorExtensionTableColumnModel[]) => {
        this.instructorsOfCourse = instructorModels;
        this.initialSortOfInstructors();
      }, (resp: ErrorMessageOutput) => {
          this.hasLoadedAllInstructorsFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      );
  }

  private mapInstructorsToInstructorModels(instructors: Instructor[]): InstructorExtensionTableColumnModel[] {
    return instructors.map((instructor) => {
      const instructorData: InstructorExtensionTableColumnModel = {
        name: instructor.name,
        role: instructor.role,
        email: instructor.email,
        extensionDeadline: this.feedbackSessionEndingTimestamp,
        hasExtension: false,
        isSelected: false,
      };

      if (instructor.email in this.instructorDeadlines) {
        instructorData.hasExtension = true;
        instructorData.extensionDeadline = this.instructorDeadlines[instructor.email];
      }
      return instructorData;
    });
  }

  private initialSortOfInstructors(): void {
    this.instructorsOfCourse.sort(this.sortInstructorPanelsBy(SortBy.INSTRUCTOR_PERMISSION_ROLE));
    this.instructorsOfCourse.sort(this.sortInstructorPanelsBy(SortBy.SESSION_END_DATE));
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
    modalRef.componentInstance.onConfirmCallBack.subscribe((extensionTimestamp: number) => {
      this.onConfirmExtension(extensionTimestamp);
      modalRef.close();
    });
  }

  /**
   * Handles the opening the confirmation modal to create/update deadlines.
   */
  private onConfirmExtension(extensionTimestamp: number): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    const selectedStudents = this.getSelectedStudents();
    const selectedInstructors = this.getSelectedInstructors();
    modalRef.componentInstance.modalType = ExtensionModalType.EXTEND;
    modalRef.componentInstance.selectedStudents = selectedStudents;
    modalRef.componentInstance.selectedInstructors = selectedInstructors;
    modalRef.componentInstance.extensionTimestamp = extensionTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;

    modalRef.componentInstance.onConfirmExtensionCallBack.subscribe((isNotifyDeadlines: boolean) => {
      this.handleCreateDeadlines(selectedStudents, selectedInstructors, extensionTimestamp, isNotifyDeadlines);
      modalRef.componentInstance.isSubmitting = false;
      modalRef.close();
    });
  }

  /**
   * Handles the opening the confirmation modal to delete deadlines.
   */
  onDelete(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    const selectedStudents = this.getSelectedStudentsWithExtensions();
    const selectedInstructors = this.getSelectedInstructorsWithExtensions();
    modalRef.componentInstance.modalType = ExtensionModalType.DELETE;
    modalRef.componentInstance.selectedStudents = selectedStudents;
    modalRef.componentInstance.selectedInstructors = selectedInstructors;
    modalRef.componentInstance.extensionTimestamp = this.feedbackSessionEndingTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;

    modalRef.componentInstance.onConfirmExtensionCallBack.subscribe((isNotifyDeadlines: boolean) => {
      this.handleDeleteDeadlines(selectedStudents, selectedInstructors, isNotifyDeadlines);
      modalRef.componentInstance.isSubmitting = false;
      modalRef.close();
    });
  }

  private handleCreateDeadlines(
    selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[],
    extensionTimestamp: number,
    isNotifyDeadlines: boolean,
  ): void {
    const request: FeedbackSessionUpdateRequest = {
      studentDeadlines: this.getUpdatedDeadlines(selectedStudents, extensionTimestamp, true),
      instructorDeadlines: this.getUpdatedDeadlines(selectedInstructors, extensionTimestamp, false),
      ...this.feedbackSessionDetails,
    };

    this.isSubmittingDeadlines = true;
    this.feedbackSessionsService
      .updateFeedbackSession(this.courseId, this.feedbackSessionName, request, isNotifyDeadlines)
      .pipe(finalize(() => { this.isSubmittingDeadlines = false; }))
      .subscribe(() => {
          this.loadFeedbackSessionAndIndividuals();
          this.statusMessageService.showSuccessToast(
            `Sucessfully created extension(s) for ${selectedStudents.length} student(s)`
            + ` and ${selectedInstructors.length} instructor(s)!`,
          );
        },
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      );
  }

  private getUpdatedDeadlines(
    selectedIndividuals: StudentExtensionTableColumnModel[] | InstructorExtensionTableColumnModel[],
    extensionTimestamp: number,
    isStudent: boolean,
  ): Record<string, number> {
    let record: Record<string, number> = {};
    if (isStudent) {
      record = { ...this.studentDeadlines };
    } else {
      record = { ...this.instructorDeadlines };
    }

    selectedIndividuals.forEach((x) => {
      record[x.email] = extensionTimestamp;
    });
    return record;
  }

  private handleDeleteDeadlines(
    selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[],
    isNotifyDeadlines: boolean,
  ): void {
    const request: FeedbackSessionUpdateRequest = {
      studentDeadlines: this.getDeletedDeadlines(selectedStudents, true),
      instructorDeadlines: this.getDeletedDeadlines(selectedInstructors, false),
      ...this.feedbackSessionDetails,
    };

    this.isSubmittingDeadlines = true;
    this.feedbackSessionsService
      .updateFeedbackSession(this.courseId, this.feedbackSessionName, request, isNotifyDeadlines)
      .pipe(finalize(() => { this.isSubmittingDeadlines = false; }))
      .subscribe(
        () => {
          this.loadFeedbackSessionAndIndividuals();
          this.statusMessageService.showSuccessToast(
            `Successfully deleleted extension(s) for ${selectedStudents.length} student(s) and`
            + ` ${selectedInstructors.length} instructor(s)!`,
          );
        },
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      );
  }

  private getDeletedDeadlines(
    selectedIndividuals: StudentExtensionTableColumnModel[] | InstructorExtensionTableColumnModel[],
    isStudent: boolean,
  ): Record<string, number> {
    let record: Record<string, number> = {};
    if (isStudent) {
      record = { ...this.studentDeadlines };
    } else {
      record = { ...this.instructorDeadlines };
    }

    selectedIndividuals.forEach((x) => {
      delete record[x.email];
    });
    return record;
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
    return [...this.studentsOfCourse, ...this.instructorsOfCourse].some((user) => user.isSelected);
  }

  /**
   * Checks if at least one valid extension has been selected
   */
  hasSelectedValidForDeletion(): boolean {
    return [...this.studentsOfCourse, ...this.instructorsOfCourse].some((user) => user.isSelected && user.hasExtension);
  }

  selectAllStudents(): void {
    this.isAllStudentsSelected = !this.isAllStudentsSelected;
    this.studentsOfCourse.forEach((x) => {
      x.isSelected = this.isAllStudentsSelected;
    });
  }

  selectAllInstructors(): void {
    this.isAllInstructorsSelected = !this.isAllInstructorsSelected;
    this.instructorsOfCourse.forEach((x) => {
      x.isSelected = this.isAllInstructorsSelected;
    });
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
          strA = a.sectionName;
          strB = b.sectionName;
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
}
