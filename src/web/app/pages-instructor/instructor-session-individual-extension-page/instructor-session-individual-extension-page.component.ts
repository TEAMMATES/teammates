import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { finalize } from 'rxjs/operators';
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
}
from '../../../types/api-request';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ColumnData, SortableTableCellData } from '../../components/sortable-table/sortable-table.component';
import { ErrorMessageOutput } from '../../error-message-output';
import { InstructorExtensionTableColumnModel, StudentExtensionTableColumnModel } from './extension-table-column-model';
import { ExtensionModalType, IndividualExtensionConfirmModalComponent }
from './individual-extension-confirm-modal/individual-extension-confirm-modal.component';
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

  courseId: string = '0';
  courseName: string = '';
  feedbackSessionName: string = '';

  feedbackSessionEndingTime: number = 0;
  feedbackSessionTimeZone: string = '';

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
  hasLoadedAllInstructorsFailed: boolean = true;
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
            intent: Intent.INSTRUCTOR_RESULT,
          })
          .subscribe(
            (feedbackSession: FeedbackSession) => {
              this.feedbackSessionEndingTime = feedbackSession.submissionEndTimestamp;
              this.feedbackSessionTimeZone = feedbackSession.timeZone;
              this.studentDeadlines = feedbackSession.studentDeadlines ?? {};
              this.instructorDeadlines = feedbackSession.instructorDeadlines ?? {};
              this.feedbackSessionDetails = this.getFeedbackSessionDetails(feedbackSession);
            },
            (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
              this.hasLoadingFeedbackSessionFailed = true;
            },
          );
        this.getAllStudentsOfCourse(); // Both students and instructors need feedback ending time.
        this.getAllInstructorsOfCourse();
      },
      (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isLoadingAllStudents = false;
        this.hasLoadingFeedbackSessionFailed = true;
      },
    );
  }

  /**
   * Gets all students of a course.
   */
  private getAllStudentsOfCourse(): void {
    // TODO: Highlight all the students after getting them, from the map.
    this.studentService
      .getStudentsFromCourse({ courseId: this.courseId })
      .pipe(finalize(() => { this.isLoadingAllStudents = false; }))
      .subscribe((students: Students) => {
          // Map on the new deadline and hasExtension
          this.studentsOfCourse = students.students.map((student) => this.mapStudentToStudentModel(student));
        },
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadedAllStudentsFailed = true;
        },
      );
  }

  private getFeedbackSessionDetails(feedbackSession: FeedbackSession): FeedbackSessionBasicRequest {
    const details: FeedbackSessionBasicRequest = {
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
    return details;
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
      selected: false,
    };

    if (student.email in this.studentDeadlines) {
      studentData.hasExtension = true;
      studentData.extensionDeadline = this.studentDeadlines[student.email];
    }

    return studentData;
  }

  getNumberOfSelectedStudents(): number {
    return this.studentsOfCourse.filter((x) => x.selected).length;
  }

  /**
   * Loads the instructors in the course
   */
  private getAllInstructorsOfCourse(): void {
    this.instructorService.loadInstructors({ courseId: this.courseId, intent: Intent.FULL_DETAIL })
    .pipe(finalize(() => { this.isLoadingAllInstructors = false; }))
    .subscribe((instructors: Instructors) => {
      this.instructorsOfCourse = instructors.instructors
                               .map((instructor) => this.mapInstructorToInstructorModel(instructor));
    }, (resp: ErrorMessageOutput) => {
      this.hasLoadedAllInstructorsFailed = true;
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  private mapInstructorToInstructorModel(instructor: Instructor): InstructorExtensionTableColumnModel {
    const instructorData: InstructorExtensionTableColumnModel = {
      institute: instructor.institute,
      name: instructor.name,
      email: instructor.email,
      extensionDeadline: this.feedbackSessionEndingTime,
      hasExtension: false,
      selected: false,
    };

    if (instructor.email in this.instructorDeadlines) {
      instructorData.hasExtension = true;
      instructorData.extensionDeadline = this.instructorDeadlines[instructorData.email];
    }

    return instructorData;
  }

  getNumberOfSelectedInstructors(): number {
    return this.instructorsOfCourse.filter((x) => x.selected).length;
  }

  onExtend(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionDateModalComponent);
    modalRef.componentInstance.numberOfStudents = this.getNumberOfSelectedStudents();
    modalRef.componentInstance.numberOfInstructors = this.getNumberOfSelectedInstructors();
    modalRef.componentInstance.feedbackSessionEndingTime = this.feedbackSessionEndingTime;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.onConfirmCallBack.subscribe((extensionTimestamp: number) => {
      this.onConfirmExtension(extensionTimestamp);
      modalRef.close();
    });
  }

  private onConfirmExtension(extensionTimestamp: number): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    const studentsSelected = this.getSelectedStudents();
    const instructorsSelected = this.getSelectedInstructors();
    modalRef.componentInstance.studentsSelected = studentsSelected;
    modalRef.componentInstance.instructorsSelected = instructorsSelected;
    modalRef.componentInstance.extensionTimestamp = extensionTimestamp;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.modalType = ExtensionModalType.EXTEND;

    modalRef.componentInstance.onConfirmExtensionCallBack.subscribe((isNotifyStudents: boolean) => {
      this.handleCreateDeadlines(studentsSelected, instructorsSelected, extensionTimestamp, isNotifyStudents);
      modalRef.close();
    });
  }

  onDelete(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    const studentsSelected = this.getSelectedStudents();
    const instructorsSelected = this.getSelectedInstructors();
    modalRef.componentInstance.modalType = ExtensionModalType.DELETE;
    modalRef.componentInstance.studentsSelected = studentsSelected;
    modalRef.componentInstance.instructorsSelected = instructorsSelected;
    modalRef.componentInstance.extensionTimestamp = this.feedbackSessionEndingTime;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.onConfirmExtensionCallBack.subscribe((isNotifyStudents: boolean) => {
      this.handleDeleteDeadlines(studentsSelected, instructorsSelected, isNotifyStudents);
      modalRef.close();
    });
  }

  private handleCreateDeadlines(studentsSelected: StudentExtensionTableColumnModel[],
                          instructorsSelected: InstructorExtensionTableColumnModel[],
                          extensionTimestamp: number, isNotifyStudents: boolean): void {

    const request: FeedbackSessionUpdateRequest = {
      studentDeadlines: this.getUpdatedDeadlines(studentsSelected, extensionTimestamp),
      instructorDeadlines: this.getUpdatedDeadlines(instructorsSelected, extensionTimestamp),
      isGoingToNotifyAboutDeadlines: isNotifyStudents,
      ...this.feedbackSessionDetails,
    };

    this.isSubmittingDeadlines = true;
    this.feedbackSessionsService.updateFeedbackSession(this.courseId, this.feedbackSessionName, request)
        .pipe(finalize(() => { this.isSubmittingDeadlines = false; }))
        .subscribe((updatedFeedbackSession: FeedbackSession) => {
          this.studentDeadlines = updatedFeedbackSession.studentDeadlines ?? {};
          this.instructorDeadlines = updatedFeedbackSession.instructorDeadlines ?? {};
          this.statusMessageService.showSuccessToast(
          `Extension for ${studentsSelected.length} students and ${instructorsSelected.length} instructors
           have been successful!`);
        }, (resp: ErrorMessageOutput) => {
         this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  private getUpdatedDeadlines(individualsSelected: StudentExtensionTableColumnModel[] |
                            InstructorExtensionTableColumnModel[],
                            extensionTimestamp: number): Record<string, number> {
    let record: Record<string, number> = {};
    if (this.isStudents(individualsSelected)) {
      record = this.studentDeadlines;
    } else {
      record = this.instructorDeadlines;
    }

    individualsSelected.forEach((x) => { record[x.email] = extensionTimestamp; });
    return record;
  }

  private handleDeleteDeadlines(studentsSelected: StudentExtensionTableColumnModel[],
    instructorsSelected: InstructorExtensionTableColumnModel[], isNotifyStudents: boolean): void {

    const request: FeedbackSessionUpdateRequest = {
      studentDeadlines: this.getDeletedDeadlines(studentsSelected),
      instructorDeadlines: this.getDeletedDeadlines(instructorsSelected),
      isGoingToNotifyAboutDeadlines: isNotifyStudents,
      ...this.feedbackSessionDetails,
    };

    this.isSubmittingDeadlines = true;
    this.feedbackSessionsService.updateFeedbackSession(this.courseId, this.feedbackSessionName, request)
    .pipe(finalize(() => { this.isSubmittingDeadlines = false; }))
    .subscribe((updatedFeedbackSession: FeedbackSession) => {
      this.studentDeadlines = updatedFeedbackSession.studentDeadlines ?? {};
      this.instructorDeadlines = updatedFeedbackSession.instructorDeadlines ?? {};
      this.statusMessageService.showSuccessToast(
      `Deletion for ${studentsSelected.length} students and ${instructorsSelected.length} instructors
      have been successful!`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  private getDeletedDeadlines(individualsSelected: StudentExtensionTableColumnModel[] |
    InstructorExtensionTableColumnModel[]): Record<string, number> {
    let record: Record<string, number> = {};
    if (this.isStudents(individualsSelected)) {
      record = this.studentDeadlines;
    } else {
      record = this.instructorDeadlines;
    }

    individualsSelected.forEach((x) => { delete record[x.email]; });
    return record;
  }

  private isStudents(individualsSelected: StudentExtensionTableColumnModel[] |
    InstructorExtensionTableColumnModel[]): boolean {
      return 'sectionName' in individualsSelected;

    }

  getSelectedStudents(): StudentExtensionTableColumnModel[] {
    return this.studentsOfCourse.filter((x) => x.selected);
  }

  getSelectedInstructors(): InstructorExtensionTableColumnModel[] {
    return this.instructorsOfCourse.filter((x) => x.selected);
  }

  hasSelected(): boolean {
    for (const student of this.studentsOfCourse) {
      if (student.selected) return true;
    }
    for (const instructor of this.instructorsOfCourse) {
      if (instructor.selected) return true;
    }
    return false;
  }

  hasSelectedValidForDeletion(): boolean {
    let hasStudentSelected = false;
    let hasInstructorSelected = false;
    for (const student of this.studentsOfCourse) {
      if (student.selected) {
        if (!student.hasExtension) return false;
        hasStudentSelected = true;
      }
    }
    for (const instructor of this.instructorsOfCourse) {
      if (instructor.selected) {
        if (!instructor.hasExtension) return false;
        hasInstructorSelected = true;
      }
    }
    return (hasStudentSelected || hasInstructorSelected);
  }

  selectAllStudents(): void {
    this.isAllStudentsSelected = !this.isAllStudentsSelected;
    this.studentsOfCourse.forEach((x) => { x.selected = this.isAllStudentsSelected; });
  }

  selectAllInstructors(): void {
    this.isAllInstructorsSelected = !this.isAllInstructorsSelected;
    this.instructorsOfCourse.forEach((x) => { x.selected = this.isAllInstructorsSelected; });
  }

  selectStudent(i: number): void {
    this.studentsOfCourse[i].selected = !this.studentsOfCourse[i].selected;
  }

  selectIntructor(i: number): void {
    this.instructorsOfCourse[i].selected = !this.instructorsOfCourse[i].selected;
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
        // TODO: Session End_Date
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
        case SortBy.INSTITUTION:
          if (!a.institute) a.institute = '';
          if (!b.institute) b.institute = '';
          strA = a.institute;
          strB = b.institute;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        // TODO: Session End_Date
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

}
