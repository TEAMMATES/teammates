import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { finalize, map } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { TimezoneService } from '../../../services/timezone.service';
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
  DATETIME_FORMAT: string = 'd MMM YYYY h:mm:ss';

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortStudentsBy: SortBy = SortBy.SECTION_NAME;
  sortStudentOrder: SortOrder = SortOrder.DESC;
  sortInstructorsBy: SortBy = SortBy.SECTION_NAME;
  sortInstructorOrder: SortOrder = SortOrder.DESC;

  isAllStudentsSelected: boolean = false;
  isAllInstructorsSelected: boolean = false;

  courseId: string = '';
  courseName: string = '';
  feedbackSessionName: string = '';

  feedbackSessionEndingTime: number = 0;
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
    public timezoneService: TimezoneService,
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
   * Loads a feedback session.
   */
   loadFeedbackSessionAndIndividuals(): void {
    this.isLoadingAllStudents = true;
    this.hasLoadedAllStudentsFailed = false;
    this.isLoadingFeedbackSession = true;
    this.hasLoadingFeedbackSessionFailed = false;
    this.isLoadingAllInstructors = true;
    this.hasLoadedAllStudentsFailed = false;
    this.courseService
      .getCourseAsInstructor(this.courseId)
      .pipe(finalize(() => { this.isLoadingFeedbackSession = false; }))
      .subscribe((course: Course) => {
        this.courseName = course.courseName;
        this.feedbackSessionsService
          .getFeedbackSession({
            courseId: this.courseId,
            feedbackSessionName: this.feedbackSessionName,
            intent: Intent.FULL_DETAIL,
          })
          .pipe(map((feedbackSession: FeedbackSession) => {
            this.getFeedbackSessionDetails(feedbackSession);
          }))
          .subscribe(() => {
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
  getAllStudentsOfCourse(): void {
    this.studentService
      .getStudentsFromCourse({ courseId: this.courseId })
      .pipe(finalize(() => { this.isLoadingAllStudents = false; }),
        map((students: Students) => {
          this.studentsOfCourse = students.students.map((student) => this.mapStudentToStudentModel(student));
        }))
      .subscribe(() => {},
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadedAllStudentsFailed = true;
        },
      );
  }

  private getFeedbackSessionDetails(feedbackSession: FeedbackSession): void {
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
    this.feedbackSessionEndingTime = feedbackSession.submissionEndTimestamp;
    this.feedbackSessionTimeZone = feedbackSession.timeZone;
    this.studentDeadlines = feedbackSession.studentDeadlines ?? {};
    this.instructorDeadlines = feedbackSession.instructorDeadlines ?? {};
  }

  private mapStudentToStudentModel(student: Student):
    StudentExtensionTableColumnModel {
    const studentData: StudentExtensionTableColumnModel = {
      sectionName: student.sectionName,
      teamName: student.teamName,
      name: student.name,
      email: student.email,
      extensionDeadline: this.feedbackSessionEndingTime,
      hasExtension: false,
      isSelected: false,
    };

    if (student.email in this.studentDeadlines) {
      studentData.hasExtension = true;
      studentData.extensionDeadline = this.studentDeadlines[student.email];
    }

    return studentData;
  }

  getNumberOfSelectedStudents(): number {
    return this.studentsOfCourse.filter((x) => x.isSelected).length;
  }

  /**
   * Loads the instructors in the course
   */
  getAllInstructorsOfCourse(): void {
    this.instructorService.loadInstructors({ courseId: this.courseId, intent: Intent.FULL_DETAIL })
      .pipe(finalize(() => { this.isLoadingAllInstructors = false; }),
      map((instructors: Instructors) => {
        this.instructorsOfCourse = instructors.instructors.map((instructor) => {
          return this.mapInstructorToInstructorModel(instructor);
        });
      }))
      .subscribe(() => {}, (resp: ErrorMessageOutput) => {
        this.hasLoadedAllInstructorsFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  private mapInstructorToInstructorModel(instructor: Instructor): InstructorExtensionTableColumnModel {
    const instructorData: InstructorExtensionTableColumnModel = {
      name: instructor.name,
      role: instructor.role,
      email: instructor.email,
      extensionDeadline: this.feedbackSessionEndingTime,
      hasExtension: false,
      isSelected: false,
    };

    if (instructor.email in this.instructorDeadlines) {
      instructorData.hasExtension = true;
      instructorData.extensionDeadline = this.instructorDeadlines[instructor.email];
    }
    return instructorData;
  }

  getNumberOfSelectedInstructors(): number {
    return this.instructorsOfCourse.filter((x) => x.isSelected).length;
  }

  onExtend(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionDateModalComponent);
    modalRef.componentInstance.numStudents = this.getNumberOfSelectedStudents();
    modalRef.componentInstance.numInstructors = this.getNumberOfSelectedInstructors();
    modalRef.componentInstance.feedbackSessionEndingTime = this.feedbackSessionEndingTime;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.onConfirmCallBack.subscribe((extensionTimestamp: number) => {
      this.onConfirmExtension(extensionTimestamp);
      modalRef.close();
    });
  }

  private onConfirmExtension(extensionTimestamp: number): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    const selectedStudents = this.getSelectedStudents();
    const selectedInstructors = this.getSelectedInstructors();
    modalRef.componentInstance.selectedStudents = selectedStudents;
    modalRef.componentInstance.selectedInstructors = selectedInstructors;
    modalRef.componentInstance.extensionTimestamp = extensionTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.modalType = ExtensionModalType.EXTEND;

    modalRef.componentInstance.onConfirmExtensionCallBack.subscribe((isNotifyIndividuals: boolean) => {
      this.handleCreateDeadlines(selectedStudents, selectedInstructors, extensionTimestamp, isNotifyIndividuals);
      modalRef.close();
    });
  }

  onDelete(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    const selectedStudents = this.getSelectedStudents();
    const selectedInstructors = this.getSelectedInstructors();
    modalRef.componentInstance.modalType = ExtensionModalType.DELETE;
    modalRef.componentInstance.selectedStudents = selectedStudents;
    modalRef.componentInstance.selectedInstructors = selectedInstructors;
    modalRef.componentInstance.extensionTimestamp = this.feedbackSessionEndingTime;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.onConfirmExtensionCallBack.subscribe((isNotifyIndividuals: boolean) => {
      this.handleDeleteDeadlines(selectedStudents, selectedInstructors, isNotifyIndividuals);
      modalRef.close();
    });
  }

  private handleCreateDeadlines(selectedStudents: StudentExtensionTableColumnModel[],
                          selectedInstructors: InstructorExtensionTableColumnModel[],
                          extensionTimestamp: number, isNotifyIndividuals: boolean): void {

    const request: FeedbackSessionUpdateRequest = {
      studentDeadlines: this.getUpdatedDeadlines(selectedStudents, extensionTimestamp, true),
      instructorDeadlines: this.getUpdatedDeadlines(selectedInstructors, extensionTimestamp, false),
      isGoingToNotifyAboutDeadlines: isNotifyIndividuals,
      ...this.feedbackSessionDetails,
    };

    this.isSubmittingDeadlines = true;
    this.feedbackSessionsService.updateFeedbackSession(this.courseId, this.feedbackSessionName, request)
        .pipe(finalize(() => { this.isSubmittingDeadlines = false; }))
        .subscribe(() => {
          this.loadFeedbackSessionAndIndividuals();
          this.statusMessageService.showSuccessToast(
          `Extension(s) for ${selectedStudents.length} student(s) and ${selectedInstructors.length} instructor(s)
           have been successful!`);
        }, (resp: ErrorMessageOutput) => {
         this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  private getUpdatedDeadlines(selectedIndividuals: StudentExtensionTableColumnModel[] |
    InstructorExtensionTableColumnModel[], extensionTimestamp: number, isStudent: boolean): Record<string, number> {
      let record: Record<string, number> = {};
      if (isStudent) {
        record = { ...this.studentDeadlines };
      } else {
        record = { ...this.instructorDeadlines };
      }

      selectedIndividuals.forEach((x) => { record[x.email] = extensionTimestamp; });
      return record;
  }

  private handleDeleteDeadlines(selectedStudents: StudentExtensionTableColumnModel[],
    selectedInstructors: InstructorExtensionTableColumnModel[], isNotifyIndividuals: boolean): void {

    const request: FeedbackSessionUpdateRequest = {
      studentDeadlines: this.getDeletedDeadlines(selectedStudents, true),
      instructorDeadlines: this.getDeletedDeadlines(selectedInstructors, false),
      isGoingToNotifyAboutDeadlines: isNotifyIndividuals,
      ...this.feedbackSessionDetails,
    };

    this.isSubmittingDeadlines = true;
    this.feedbackSessionsService.updateFeedbackSession(this.courseId, this.feedbackSessionName, request)
    .pipe(finalize(() => { this.isSubmittingDeadlines = false; }))
    .subscribe(() => {
      this.loadFeedbackSessionAndIndividuals();
      this.statusMessageService.showSuccessToast(
      `Deletion of extension(s) for ${selectedStudents.length} student(s) and ${selectedInstructors.length} `
      + 'instructor(s) have been successful!');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  private getDeletedDeadlines(selectedIndividuals: StudentExtensionTableColumnModel[] |
  InstructorExtensionTableColumnModel[], isStudent: boolean): Record<string, number> {
    let record: Record<string, number> = {};
    if (isStudent) {
      record = { ...this.studentDeadlines };
    } else {
      record = { ...this.instructorDeadlines };
    }

    selectedIndividuals.forEach((x) => { delete record[x.email]; });
    return record;
  }

  getSelectedStudents(): StudentExtensionTableColumnModel[] {
    return this.studentsOfCourse.filter((x) => x.isSelected);
  }

  getSelectedInstructors(): InstructorExtensionTableColumnModel[] {
    return this.instructorsOfCourse.filter((x) => x.isSelected);
  }

  hasSelected(): boolean {
    for (const student of this.studentsOfCourse) {
      if (student.isSelected) return true;
    }
    for (const instructor of this.instructorsOfCourse) {
      if (instructor.isSelected) return true;
    }
    return false;
  }

  hasSelectedValidForDeletion(): boolean {
    let hasStudentSelected = false;
    let hasInstructorSelected = false;
    for (const student of this.studentsOfCourse) {
      if (student.isSelected) {
        if (!student.hasExtension) return false;
        hasStudentSelected = true;
      }
    }
    for (const instructor of this.instructorsOfCourse) {
      if (instructor.isSelected) {
        if (!instructor.hasExtension) return false;
        hasInstructorSelected = true;
      }
    }
    return (hasStudentSelected || hasInstructorSelected);
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
  }

  selectIntructor(i: number): void {
    this.instructorsOfCourse[i].isSelected = !this.instructorsOfCourse[i].isSelected;
  }

  sortStudentColumnsBy(by: SortBy): void {
    this.sortStudentsBy = by;
    this.sortStudentOrder = this.sortStudentOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.studentsOfCourse.sort(this.sortStudentPanelsBy(by));
  }

  sortStudentPanelsBy(by: SortBy): (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel)
    => number {
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

  sortInstructorPanelsBy(by: SortBy): (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel)
  => number {
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
          strA = a.role ? a.role : '';
          strB = b.role ? b.role : '';
          break  
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
