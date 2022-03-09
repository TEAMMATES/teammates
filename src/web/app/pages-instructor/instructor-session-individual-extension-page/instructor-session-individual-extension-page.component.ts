import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NgbModal, NgbModalRef } from "@ng-bootstrap/ng-bootstrap";
import moment from 'moment-timezone';
import { finalize } from "rxjs/operators";
import { DateFormat } from 'src/web/app/components/datepicker/datepicker.component';
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
import { TimeFormat } from "../../components/timepicker/timepicker.component";
import { ErrorMessageOutput } from "../../error-message-output";
import { IndividualExtensionConfirmModalComponent } from "./individual-extension-confirm-modal/individual-extension-confirm-modal.component";
import { IndividualExtensionDateModalComponent } from "./individual-extension-date-modal/individual-extension-date-modal.component";
import { TimezoneService } from '../../../services/timezone.service';

// Columns for the table: Section, Team, Student Name, Email, New Deadline
interface StudentExtensionTableColumnData {
  sectionName: string;
  teamName: string;
  studentName: string;
  studentEmail: string;
  studentExtensionDeadline: string;
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

  // Load course information. (Course ID, Time Zone, Course Name, Session Name, Original Deadline)
  feedbackSessionDateTime: {date: DateFormat, time: TimeFormat, timeZone: String} = {
    date: { year: 0, month: 0, day: 0 },
    time: { hour: 23, minute: 59 },
    timeZone: "",
  };

  columnsData: ColumnData[] = [];
  rowsData: SortableTableCellData[][] = [];

  studentsOfCourse: StudentExtensionTableColumnData[] = [];

  isLoadingAllStudents: boolean = true;
  hasLoadedAllStudentsFailed: boolean = false;
  hasLoadingFeedbackSessionFailed: boolean = false;
  isLoadingFeedbackSession: boolean = true;

  extensionModal: NgbModalRef | null = null;

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
    private timezoneService: TimezoneService,
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
  mapStudentsOfCourse(students: Student[], deadlineMap: Map<String, String>): StudentExtensionTableColumnData[] {
    return students.map(student => this.mapStudentToStudentColumnData(student, deadlineMap))
  }

  mapStudentToStudentColumnData(student: Student, deadline: Map<String, String>): StudentExtensionTableColumnData {
    const studentData: StudentExtensionTableColumnData = {
      sectionName: student.sectionName,
      teamName: student.teamName,
      studentName: student.name,
      studentEmail: student.email,
      studentExtensionDeadline: this.feedbackSessionDateTime.date.toString(),
      //TODO: Race condition with getting the original submission deadline.
      hasExtension: false, // TODO: Default
      selected: false
    }

    if (deadline.has(student.email)) {
      studentData.hasExtension = true;
      studentData.studentExtensionDeadline = deadline.get(student.email)!.toString();
    }

    return studentData
  };  

  // TODO: Refactor this, copied from another file
  /**
   * Get the local date and time of timezone from timestamp.
   */
  private getDateTimeAtTimezone(timestamp: number, timeZone: string, resolveMidnightTo2359: boolean):
     { date: DateFormat; time: TimeFormat } {
    let momentInstance: moment.Moment = this.timezoneService.getMomentInstance(timestamp, timeZone);
    if (resolveMidnightTo2359 && momentInstance.hour() === 0 && momentInstance.minute() === 0) {
      momentInstance = momentInstance.subtract(1, 'minute');
    }
    const date: DateFormat = {
      year: momentInstance.year(),
      month: momentInstance.month() + 1, // moment return 0-11 for month
      day: momentInstance.date(),
    };
    const time: TimeFormat = {
      minute: momentInstance.minute(),
      hour: momentInstance.hour(),
    };
    return { date, time };
  }

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
                const dateTime: {date: DateFormat, time: TimeFormat } =
                this.getDateTimeAtTimezone(feedbackSession.submissionEndTimestamp, feedbackSession.timeZone, true);
                this.feedbackSessionDateTime.date = dateTime.date
                this.feedbackSessionDateTime.time = dateTime.time
                this.feedbackSessionDateTime.timeZone = feedbackSession.timeZone;
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

  onExtend(): void {
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionDateModalComponent);
    this.extensionModal = modalRef;
    modalRef.componentInstance.numberOfStudents = this.studentsOfCourse.length;
    modalRef.componentInstance.feedbackSessionDateTime = this.feedbackSessionDateTime;
    modalRef.componentInstance.onConfirmExtension = this.onConfirmExtension;
  }

  onConfirmExtension(): void {
    this.extensionModal?.close();
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    console.log(modalRef);
  }

  onDelete(): void {
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      "Confirm deleting feedback session extension?",
      SimpleModalType.DANGER,
      "Do you want to delete the feedback session extension(s) for <b>3 student(s)</b>? Their feedback session deadline will be reverted back to the original deadline."
    );
    modalRef.result.then(() => console.log("Confirmed!"));
  }

  setA(a: boolean) {
    this.isOpen = a;
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
  // Columns for the table: Section, Team, Student Name, Email, New Deadline
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
          strA = this.feedbackSessionDateTime.date.toString();
          strB = this.feedbackSessionDateTime.date.toString();
          break;
        default:
          strA = "";
          strB = "";
      }
      return this.tableComparatorService.compare(by, this.sortOrder, strA, strB);
    };
  }
}
