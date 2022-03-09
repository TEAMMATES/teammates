import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NgbModal, NgbModalRef } from "@ng-bootstrap/ng-bootstrap";
import { finalize } from "rxjs/operators";
import { FeedbackSessionsService } from "src/web/services/feedback-sessions.service";
import { StudentService } from "src/web/services/student.service";
import { Course, FeedbackSession, Student, Students } from "src/web/types/api-output";
import { Intent } from "src/web/types/api-request";
import { CourseService } from "../../../services/course.service";
import { SimpleModalService } from "../../../services/simple-modal.service";
import { StatusMessageService } from "../../../services/status-message.service";
import { TableComparatorService } from "../../../services/table-comparator.service";
import { SortBy, SortOrder } from "../../../types/sort-properties";
import { SimpleModalType } from "../../components/simple-modal/simple-modal-type";
import { ColumnData, SortableTableCellData } from "../../components/sortable-table/sortable-table.component";
import { ErrorMessageOutput } from "../../error-message-output";
import { IndividualExtensionConfirmModalComponent } from "./individual-extension-confirm-modal/individual-extension-confirm-modal.component";
import { IndividualExtensionDateModalComponent } from "./individual-extension-date-modal/individual-extension-date-modal.component";

// Columns for the table: Section, Team, Student Name, Email, New Deadline
interface StudentExtensionTableColumnData {
  sectionName: string;
  teamName: string;
  studentName: string;
  studentEmail: string;
  studentExtensionDeadline: number;
  hasExtension: boolean;
  selected: boolean;
}

/**
 * Send reminders to respondents modal.
 */
@Component({
  selector: "tm-instructor-session-individual-extension-page",
  templateUrl: "./instructor-session-individual-extension-page.component.html",
  styleUrls: ["./instructor-session-individual-extension-page.component.scss"],
})
export class InstructorSessionIndividualExtensionPageComponent implements OnInit {
  isOpen: boolean = true;

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortBy: SortBy = SortBy.SECTION_NAME;
  sortOrder: SortOrder = SortOrder.DESC;
  isAllSelected: boolean = false;

  courseId: string = "0";
  courseName: string = "";
  feedbackSessionName: string = "";

  feedbackSessionEndingTime: number = 0;
  feedbackSessionTimeZone: String = "";

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  studentsOfCourse: StudentExtensionTableColumnData[] = [];

  isLoadingAllStudents: boolean = true;
  hasLoadedAllStudentsFailed: boolean = false;
  hasLoadingFeedbackSessionFailed: boolean = false;
  isLoadingFeedbackSession: boolean = true;

  extensionModal: NgbModalRef | null = null;
  extensionTimestamp: number = 0;

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.feedbackSessionName = queryParams.fsname;
      this.loadFeedbackSession();
      this.getAllStudentsOfCourse();
    });
  }

  // Columns for the table: Section, Team, Student Name, Email, Original Deadline, New Deadline
  constructor(
    private statusMessageService: StatusMessageService,
    private feedbackSessionsService: FeedbackSessionsService,
    private simpleModalService: SimpleModalService,
    private studentService: StudentService,
    private ngbModal: NgbModal,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private tableComparatorService: TableComparatorService
  ) {}

  /**
   * Gets all students of a course.
   */
  getAllStudentsOfCourse(): void {
    // TODO: Highlight all the students after getting them, from the map.
    this.studentService
      .getStudentsFromCourse({ courseId: this.courseId })
      .pipe(finalize(() => (this.isLoadingAllStudents = false)))
      .subscribe(
        (students: Students) => {
          this.isLoadingAllStudents = true;
          this.hasLoadedAllStudentsFailed = false;
          // Map on the new deadline and hasExtension
          this.studentsOfCourse = this.mapStudentsOfCourse(students.students, new Map());
        },
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadedAllStudentsFailed = true;
        }
      );
  }

  // TODO: Check if we even need a map here, or how is the "map" going to be transferred here
  mapStudentsOfCourse(students: Student[], deadlineMap: Map<String, Number>): StudentExtensionTableColumnData[] {
    return students.map(student => this.mapStudentToStudentColumnData(student, deadlineMap))
  }

  mapStudentToStudentColumnData(student: Student, deadline: Map<String, Number>): StudentExtensionTableColumnData {
    const studentData: StudentExtensionTableColumnData = {
      sectionName: student.sectionName,
      teamName: student.teamName,
      studentName: student.name,
      studentEmail: student.email,
      studentExtensionDeadline: this.feedbackSessionEndingTime,
      //TODO: Race condition with getting the original submission deadline.
      hasExtension: false, // TODO: Default
      selected: false
    }

    if (deadline.has(student.email)) {
      studentData.hasExtension = true;
      studentData.studentExtensionDeadline = deadline.get(student.email)!.valueOf();
    }

    return studentData
  };  

  /**
   * Loads a feedback session.
   */
  loadFeedbackSession(): void {
    this.hasLoadingFeedbackSessionFailed = false;
    this.isLoadingFeedbackSession = true;
    this.courseService
      .getCourseAsInstructor(this.courseId)
      .pipe(finalize(() => (this.isLoadingFeedbackSession = false)))
      .subscribe(
        (course: Course) => {
          this.courseName = course.courseName;

          this.feedbackSessionsService
            .getFeedbackSession({
              courseId: this.courseId,
              feedbackSessionName: this.feedbackSessionName,
              intent: Intent.INSTRUCTOR_RESULT,
            })
            .subscribe(
              (feedbackSession: FeedbackSession) => {
                this.feedbackSessionEndingTime = feedbackSession.submissionEndTimestamp
                this.feedbackSessionTimeZone = feedbackSession.timeZone;
              },
              (resp: ErrorMessageOutput) => {
                this.statusMessageService.showErrorToast(resp.error.message);
                this.hasLoadingFeedbackSessionFailed = true;
              }
            );
        },
        (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
          this.hasLoadingFeedbackSessionFailed = true;
        }
    );
  }

  getNumberOfSelectedStudents(): number {
    return this.studentsOfCourse.filter(x => x.selected).length;
  }

  onExtend(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionDateModalComponent);
    this.extensionModal = modalRef;
    modalRef.componentInstance.numberOfStudents = this.getNumberOfSelectedStudents();
    modalRef.componentInstance.feedbackSessionEndingTime = this.feedbackSessionEndingTime;
    modalRef.componentInstance.feedbackSessionTimeZone = this.feedbackSessionTimeZone;
    modalRef.componentInstance.onConfirmCallBack.subscribe((extensionTimestamp: number) => this.onConfirmExtension(extensionTimestamp));
  }

  onConfirmExtension(extensionTimestamp: number): void {
    this.extensionTimestamp = extensionTimestamp
    this.extensionModal?.close();
    console.log("Close, and open. Successful emission!")
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    console.log(modalRef);
  }

  onDelete(): void {
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      "Confirm deleting feedback session extension?",
      SimpleModalType.DANGER,
      `Do you want to delete the feedback session extension(s) for <b>${this.getNumberOfSelectedStudents()} student(s)</b>? Their feedback session deadline will be reverted back to the original deadline.`
    );
    modalRef.result.then(() => console.log("Confirmed!"));
  }

  hasSelectedStudents(): boolean {
    for (const student of this.studentsOfCourse) {
      if (student.selected) return true;
    }
    return false;
  }

  selectAllStudents(): void {
    this.isAllSelected = !this.isAllSelected
    this.studentsOfCourse.map(x => x.selected = this.isAllSelected);
  }

  selectStudent(i: number): void {
    this.studentsOfCourse[i].selected = !this.studentsOfCourse[i].selected 
  }

  sortCoursesBy(by: SortBy): void {
    this.sortBy = by;
    this.sortOrder = this.sortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.studentsOfCourse.sort(this.sortPanelsBy(by));
  }

  sortPanelsBy(by: SortBy): (a: StudentExtensionTableColumnData, b: StudentExtensionTableColumnData) => number {
    return (a: StudentExtensionTableColumnData, b: StudentExtensionTableColumnData): number => {
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
          strA = a.studentName;
          strB = b.studentName;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.studentEmail;
          strB = b.studentEmail;
          break;
        //TODO: Session End_Date
        case SortBy.SESSION_END_DATE:
          strA = this.feedbackSessionEndingTime.toString();
          strB = this.feedbackSessionEndingTime.toString();
          break;
        default:
          strA = "";
          strB = "";
      }
      return this.tableComparatorService.compare(by, this.sortOrder, strA, strB);
    };
  }
}
