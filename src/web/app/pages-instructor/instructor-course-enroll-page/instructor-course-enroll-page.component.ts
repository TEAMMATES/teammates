import { NgClass } from '@angular/common';
import { Component, OnInit, DOCUMENT, inject, viewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { type CellValue } from 'handsontable/common';
import { PageScrollService } from 'ngx-page-scroll-core';
import { concat, finalize, Observable } from 'rxjs';
import { EnrollStatus } from './enroll-status';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { ProgressBarService } from '../../../services/progress-bar.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { EnrollStudents, HasResponses, JoinState, Student, Students } from '../../../types/api-output';
import { StudentEnrollRequest, StudentsEnrollRequest } from '../../../types/api-request';
import { AjaxLoadingComponent } from '../../components/ajax-loading/ajax-loading.component';
import { AjaxPreloadComponent } from '../../components/ajax-preload/ajax-preload.component';
import { DataGridComponent } from '../../components/data-grid/data-grid.component';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { PanelChevronComponent } from '../../components/panel-chevron/panel-chevron.component';
import { ProgressBarComponent } from '../../components/progress-bar/progress-bar.component';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { StatusMessage } from '../../components/status-message/status-message';
import { StatusMessageComponent } from '../../components/status-message/status-message.component';
import { areEmailsEqual, normalizeEmail } from '../../components/teammates-common/email-utils';
import { ErrorMessageOutput } from '../../error-message-output';

interface EnrollResultPanel {
  status: EnrollStatus;
  messageForEnrollmentStatus: string;
  studentList: Student[];
}

/**
 * Instructor course enroll page.
 */
@Component({
  selector: 'tm-instructor-course-enroll-page',
  templateUrl: './instructor-course-enroll-page.component.html',
  styleUrls: ['./instructor-course-enroll-page.component.scss'],
  imports: [
    LoadingSpinnerDirective,
    LoadingRetryComponent,
    StatusMessageComponent,
    AjaxPreloadComponent,
    PanelChevronComponent,
    NgClass,
    ProgressBarComponent,
    AjaxLoadingComponent,
    DataGridComponent,
  ],
})
export class InstructorCourseEnrollPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly feedbackSessionService = inject(FeedbackSessionsService);
  private readonly studentService = inject(StudentService);
  private readonly progressBarService = inject(ProgressBarService);
  private readonly simpleModalService = inject(SimpleModalService);
  private readonly pageScrollService = inject(PageScrollService);
  private readonly document = inject(DOCUMENT);

  GENERAL_ERROR_MESSAGE = `You may check that: "Section" and "Comment" are optional while "Team", "Name",
        and "Email" must be filled. "Section", "Team", "Name", and "Comment" should start with an
        alphabetical character, unless wrapped by curly brackets "{}", and should not contain vertical bar "|" and
        percentage sign "%". "Email" should contain some text followed by one "@" sign followed by some
        more text. "Team" should not have the same format as email to avoid mis-interpretation.`;
  SECTION_ERROR_MESSAGE = 'Section cannot be empty if the total number of students is more than 100. ';
  TEAM_ERROR_MESSAGE = 'Duplicated team detected in different sections. ';

  COL_HEADERS = ['Section', 'Team', 'Name', 'Email', 'Comments'];
  ENROLL_BATCH_SIZE = 50;

  newStudentsGrid = viewChild.required<DataGridComponent>('newStudentsGrid');

  // enum
  EnrollStatus: typeof EnrollStatus = EnrollStatus;
  courseId = '';
  coursePresent?: boolean;
  isLoadingCourseEnrollPage = false;
  showEnrollResults?: boolean = false;
  enrollErrorMessage = '';
  statusMessage: StatusMessage[] = [];
  unsuccessfulEnrolls: { [email: string]: string } = {};

  isNewStudentsPanelCollapsed = false;
  isExistingStudentsPanelCollapsed = true;

  enrollResultPanelList?: EnrollResultPanel[];
  existingStudents: Student[] = [];
  existingStudentsData: string[][] = [];

  isExistingStudentsPresent = true;
  hasLoadingStudentsFailed = false;
  isLoadingExistingStudents = false;
  isEnrolling = false;

  allStudentChunks: StudentEnrollRequest[][] = [];
  invalidRowsIndex: Set<number> = new Set();
  newStudentRowsIndex: Set<number> = new Set();
  modifiedStudentRowsIndex: Set<number> = new Set();
  unchangedStudentRowsIndex: Set<number> = new Set();

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.getCourseEnrollPageData(queryParams.courseid);
    });
  }

  private mapStudentsToRows(students: Student[] | undefined): string[][] {
    if (!students) {
      return [];
    }

    return students.map((student: Student) => [
      student.sectionName,
      student.teamName,
      student.name,
      student.email,
      student.comments || '',
    ]);
  }

  /**
   * Submits enroll data
   */
  submitEnrollData(): void {
    this.isEnrolling = true;
    this.enrollErrorMessage = '';
    this.allStudentChunks = [];
    this.invalidRowsIndex = new Set();
    this.newStudentRowsIndex = new Set();
    this.modifiedStudentRowsIndex = new Set();
    this.unchangedStudentRowsIndex = new Set();

    const data = this.newStudentsGrid().getData();

    // Reset error highlighting on a new submission
    this.newStudentsGrid().resetTableStyles();

    // Record the row with its index on the table
    const studentEnrollRequests: Map<number, StudentEnrollRequest> = new Map();
    const normalizeCell = (value: CellValue): string => {
      if (value === null || value === undefined) {
        return '';
      }
      return typeof value === 'string' ? value.trim() : String(value);
    };

    // Parse the user input to be requests.
    data.forEach((row: CellValue[], index: number) => {
      if (!row.every((cell: CellValue) => normalizeCell(cell) === '')) {
        studentEnrollRequests.set(index, {
          section: normalizeCell(row[0]),
          team: normalizeCell(row[1]),
          name: normalizeCell(row[2]),
          email: normalizeCell(row[3]),
          comments: normalizeCell(row[4]),
        });
      }
    });

    if (studentEnrollRequests.size === 0) {
      this.enrollErrorMessage = 'Empty table';
      this.isEnrolling = false;
      return;
    }

    this.checkCompulsoryFields(studentEnrollRequests);
    this.checkEmailNotRepeated(studentEnrollRequests);
    this.checkTeamsValid(studentEnrollRequests);

    if (this.invalidRowsIndex.size > 0) {
      this.setTableStyleBasedOnFieldChecks();
      this.isEnrolling = false;
      return;
    }

    this.partitionStudentEnrollRequests(Array.from(studentEnrollRequests.values()));
    const enrolledStudents: Student[] = [];

    // Use concat because we cannot afford to parallelize with forkJoin when there's data dependency
    const enrollRequests: Observable<EnrollStudents> = concat(
      ...this.allStudentChunks.map((studentChunk: StudentEnrollRequest[]) => {
        const request: StudentsEnrollRequest = {
          studentEnrollRequests: studentChunk,
        };
        return this.studentService.enrollStudents(this.courseId, request);
      }),
    );

    this.progressBarService.updateProgress(0);
    enrollRequests
      .pipe(
        finalize(() => {
          this.isEnrolling = false;
        }),
      )
      .subscribe({
        next: (resp: EnrollStudents) => {
          enrolledStudents.push(...resp.studentsData.students);

          if (resp.unsuccessfulEnrolls != null) {
            for (const unsuccessfulEnroll of resp.unsuccessfulEnrolls) {
              const normalizedEmail: string = normalizeEmail(unsuccessfulEnroll.studentEmail);
              this.unsuccessfulEnrolls[normalizedEmail] = unsuccessfulEnroll.errorMessage;

              for (const index of studentEnrollRequests.keys()) {
                if (normalizeEmail(studentEnrollRequests.get(index)?.email) === normalizedEmail) {
                  this.invalidRowsIndex.add(index);
                  break;
                }
              }
            }
          }
          const percentage: number = Math.round((100 * enrolledStudents.length) / studentEnrollRequests.size);
          this.progressBarService.updateProgress(percentage);
        },
        complete: () => {
          this.showEnrollResults = true;
          this.statusMessage.pop(); // removes any existing error status message
          this.statusMessageService.showSuccessToast('Enrollment successful. Summary given below.');
          this.prepareEnrollmentResults(enrolledStudents, studentEnrollRequests);

          if (
            this.invalidRowsIndex.size > 0 ||
            this.newStudentRowsIndex.size > 0 ||
            this.modifiedStudentRowsIndex.size > 0 ||
            this.unchangedStudentRowsIndex.size > 0
          ) {
            this.setTableStyleBasedOnFieldChecks();
          }
        },
        error: (resp: ErrorMessageOutput) => {
          if (enrolledStudents.length > 0) {
            this.showEnrollResults = true;
            this.prepareEnrollmentResults(enrolledStudents, studentEnrollRequests);
          }

          // Set error message after populating result panels to avoid it being overridden
          this.enrollErrorMessage = resp.error.message;
        },
      });
  }

  private prepareEnrollmentResults(
    enrolledStudents: Student[],
    studentEnrollRequests: Map<number, StudentEnrollRequest>,
  ): void {
    this.enrollResultPanelList = this.populateEnrollResultPanelList(
      this.existingStudents,
      enrolledStudents,
      studentEnrollRequests,
    );
    this.getExistingStudents(this.courseId);
  }

  private getExistingStudents(courseId: string): void {
    this.isLoadingExistingStudents = true;
    this.hasLoadingStudentsFailed = false;
    this.studentService
      .getStudentsFromCourse({ courseId })
      .pipe(
        finalize(() => {
          this.isLoadingExistingStudents = false;
        }),
      )
      .subscribe({
        next: (resp: Students) => {
          this.existingStudents = resp.students;
          this.existingStudentsData = this.mapStudentsToRows(resp.students);
          this.isExistingStudentsPresent = resp.students.length > 0;
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasLoadingStudentsFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  private partitionStudentEnrollRequests(studentEnrollRequests: StudentEnrollRequest[]): void {
    let currentStudentChunk: StudentEnrollRequest[] = [];
    for (const request of studentEnrollRequests) {
      currentStudentChunk.push(request);
      if (currentStudentChunk.length >= this.ENROLL_BATCH_SIZE) {
        this.allStudentChunks.push(currentStudentChunk);
        currentStudentChunk = [];
      }
    }
    if (currentStudentChunk.length > 0) {
      this.allStudentChunks.push(currentStudentChunk);
    }
  }

  private checkTeamsValid(studentEnrollRequests: Map<number, StudentEnrollRequest>): void {
    const teamSectionMap: Map<string, string> = new Map();
    const teamIndexMap: Map<string, number> = new Map();
    const invalidRowsOriginalSize: number = this.invalidRowsIndex.size;

    Array.from(studentEnrollRequests.keys()).forEach((key: number) => {
      const request: StudentEnrollRequest | undefined = studentEnrollRequests.get(key);
      if (request === undefined) {
        return;
      }

      if (!teamSectionMap.has(request.team)) {
        teamSectionMap.set(request.team, request.section);
        teamIndexMap.set(request.team, key);
        return;
      }

      if (teamSectionMap.get(request.team) !== request.section) {
        this.invalidRowsIndex.add(key);
        const firstIndex: number | undefined = teamIndexMap.get(request.team);
        if (firstIndex !== undefined) {
          this.invalidRowsIndex.add(firstIndex);
        }
      }
    });
    if (this.invalidRowsIndex.size > invalidRowsOriginalSize) {
      this.enrollErrorMessage += 'Found duplicated teams in different sections. ';
    }
  }

  private checkCompulsoryFields(studentEnrollRequests: Map<number, StudentEnrollRequest>): void {
    const invalidRowsOriginalSize: number = this.invalidRowsIndex.size;

    Array.from(studentEnrollRequests.keys()).forEach((key: number) => {
      const request: StudentEnrollRequest | undefined = studentEnrollRequests.get(key);
      if (request === undefined) {
        return;
      }

      if (
        (studentEnrollRequests.size >= 100 && request.section === '') ||
        request.team === '' ||
        request.name === '' ||
        request.email === ''
      ) {
        this.invalidRowsIndex.add(key);
      }
    });
    if (this.invalidRowsIndex.size > invalidRowsOriginalSize) {
      this.enrollErrorMessage += 'Found empty compulsory fields and/or sections with more than 100 students. ';
    }
  }

  private checkEmailNotRepeated(studentEnrollRequests: Map<number, StudentEnrollRequest>): void {
    const emailMap: Map<string, number> = new Map();
    const invalidRowsOriginalSize: number = this.invalidRowsIndex.size;

    Array.from(studentEnrollRequests.keys()).forEach((key: number) => {
      const request: StudentEnrollRequest | undefined = studentEnrollRequests.get(key);
      if (request === undefined) {
        return;
      }

      const normalizedEmail: string = normalizeEmail(request.email);
      if (!emailMap.has(normalizedEmail)) {
        emailMap.set(normalizedEmail, key);
        return;
      }

      this.invalidRowsIndex.add(key);
      const firstIndex: number | undefined = emailMap.get(normalizedEmail);
      if (firstIndex !== undefined) {
        this.invalidRowsIndex.add(firstIndex);
      }
    });
    if (this.invalidRowsIndex.size > invalidRowsOriginalSize) {
      this.enrollErrorMessage += 'Found duplicated emails. ';
    }
  }

  private setTableStyleBasedOnFieldChecks(): void {
    const rowIdxToClass: Record<number, string> = {};
    for (const index of this.invalidRowsIndex) {
      rowIdxToClass[index] = 'invalid-row';
    }
    for (const index of this.newStudentRowsIndex) {
      rowIdxToClass[index] = 'new-row';
    }
    for (const index of this.modifiedStudentRowsIndex) {
      rowIdxToClass[index] = 'modified-row';
    }
    for (const index of this.unchangedStudentRowsIndex) {
      rowIdxToClass[index] = 'unchanged-row';
    }

    this.newStudentsGrid().styleRows(rowIdxToClass);
  }

  private populateEnrollResultPanelList(
    existingStudents: Student[],
    enrolledStudents: Student[],
    enrollRequests: Map<number, StudentEnrollRequest>,
  ): EnrollResultPanel[] {
    const panels: EnrollResultPanel[] = [];
    const studentLists: Student[][] = [];
    const statuses: (string | EnrollStatus)[] = Object.values(EnrollStatus).filter(
      (value: string | EnrollStatus) => typeof value === 'string',
    );

    for (let i = 0; i < statuses.length; i += 1) {
      studentLists.push([]);
    }

    const emailToIndexMap: Map<string, number> = new Map();
    enrollRequests.forEach((enrollRequest: StudentEnrollRequest, index: number) => {
      emailToIndexMap.set(normalizeEmail(enrollRequest.email), index);
    });

    // Identify students not in the enroll list.
    for (const existingStudent of existingStudents) {
      const enrolledStudent: Student | undefined = enrolledStudents.find((student: Student) => {
        return areEmailsEqual(student.email, existingStudent.email);
      });
      if (enrolledStudent === undefined) {
        studentLists[EnrollStatus.UNMODIFIED].push(existingStudent);
      }
    }

    // Identify new students, modified students, and students that are modified without any changes.
    for (const enrolledStudent of enrolledStudents) {
      const unchangedStudent: Student | undefined = existingStudents.find((student: Student) => {
        return this.isSameEnrollInformation(student, enrolledStudent);
      });
      const modifiedStudent: Student | undefined = existingStudents.find((student: Student) => {
        return areEmailsEqual(student.email, enrolledStudent.email);
      });

      if (unchangedStudent !== undefined) {
        studentLists[EnrollStatus.MODIFIED_UNCHANGED].push(enrolledStudent);
        this.addToRowsIndexSet(enrolledStudent.email, emailToIndexMap, this.unchangedStudentRowsIndex);
      } else if (unchangedStudent === undefined && modifiedStudent !== undefined) {
        studentLists[EnrollStatus.MODIFIED].push(enrolledStudent);
        this.addToRowsIndexSet(enrolledStudent.email, emailToIndexMap, this.modifiedStudentRowsIndex);
      } else if (unchangedStudent === undefined && modifiedStudent === undefined) {
        studentLists[EnrollStatus.NEW].push(enrolledStudent);
        this.addToRowsIndexSet(enrolledStudent.email, emailToIndexMap, this.newStudentRowsIndex);
      }
    }

    // Identify students that failed to enroll.
    for (const request of enrollRequests.values()) {
      const enrolledStudent: Student | undefined = enrolledStudents.find((student: Student) => {
        return areEmailsEqual(student.email, request.email);
      });

      if (enrolledStudent === undefined) {
        studentLists[EnrollStatus.ERROR].push({
          userId: '',
          email: request.email,
          courseId: this.courseId,
          name: request.name,
          sectionName: request.section,
          teamName: request.team,
          comments: request.comments,
          joinState: JoinState.NOT_JOINED,
        });
      }
    }

    const statusMessage: Record<number, string> = {
      0: `${studentLists[EnrollStatus.ERROR].length} student(s) failed to be enrolled:`,
      1: `${studentLists[EnrollStatus.NEW].length} student(s) added:`,
      2: `${studentLists[EnrollStatus.MODIFIED].length} student(s) modified:`,
      3: `${studentLists[EnrollStatus.MODIFIED_UNCHANGED].length} student(s) updated with no changes:`,
      4: `${studentLists[EnrollStatus.UNMODIFIED].length} student(s) remain unmodified:`,
    };

    for (const status of statuses) {
      panels.push({
        status: EnrollStatus[status as keyof typeof EnrollStatus],
        messageForEnrollmentStatus: statusMessage[EnrollStatus[status as keyof typeof EnrollStatus]],
        studentList: studentLists[EnrollStatus[status as keyof typeof EnrollStatus]],
      });
    }

    if (studentLists[EnrollStatus.ERROR].length > 0) {
      this.enrollErrorMessage = this.GENERAL_ERROR_MESSAGE;
      this.statusMessageService.showErrorToast('Some students failed to be enrolled, see the summary below.');
    }
    return panels;
  }

  private addToRowsIndexSet(email: string, emailToIndexMap: Map<string, number>, rowsIndex: Set<number>): void {
    const index: number | undefined = emailToIndexMap.get(normalizeEmail(email));
    if (index !== undefined) {
      rowsIndex.add(index);
    }
  }

  private isSameEnrollInformation(enrolledStudent: Student, existingStudent: Student): boolean {
    return (
      areEmailsEqual(enrolledStudent.email, existingStudent.email) &&
      enrolledStudent.name === existingStudent.name &&
      enrolledStudent.teamName === existingStudent.teamName &&
      enrolledStudent.sectionName === existingStudent.sectionName &&
      enrolledStudent.comments === existingStudent.comments
    );
  }

  getUnsuccessfulEnrollError(email: string): string | undefined {
    return this.unsuccessfulEnrolls[normalizeEmail(email)];
  }

  /**
   * Adds new rows to the 'New students' spreadsheet interface
   */
  addRows(numRows: number): void {
    if (!numRows) {
      return;
    }

    this.newStudentsGrid().addRows(numRows);
  }

  /**
   * Toggles the view of 'New Students' spreadsheet interface
   * and/or its affiliated buttons
   */
  toggleNewStudentsPanel(): void {
    this.isNewStudentsPanelCollapsed = !this.isNewStudentsPanelCollapsed;
  }

  /**
   * Transforms the first uppercase letter of a string into a lowercase letter.
   */
  unCapitalizeFirstLetter(targetString: string): string {
    return targetString.charAt(0).toLowerCase() + targetString.slice(1);
  }

  /**
   * Converts returned student list to a suitable format required by Handsontable.
   */
  studentListDataToHandsontableData(studentsData: Student[], handsontableColHeader: any[]): string[][] {
    const headers: string[] = handsontableColHeader.map(this.unCapitalizeFirstLetter);
    return studentsData.map((student: Student) =>
      headers.map((header: string) => {
        if (header === 'team') {
          return (student as any).teamName;
        }
        if (header === 'section') {
          return (student as any).sectionName;
        }
        return (student as any)[header];
      }),
    );
  }

  /**
   * Toggles the view of 'Existing Students' spreadsheet interface
   */
  toggleExistingStudentsPanel(): void {
    this.isExistingStudentsPanelCollapsed = !this.isExistingStudentsPanelCollapsed;
  }

  /**
   * Checks whether the course is present
   */
  getCourseEnrollPageData(courseid: string): void {
    this.isLoadingCourseEnrollPage = true;
    this.feedbackSessionService.hasResponsesForAllFeedbackSessionsInCourse(courseid, 'instructor').subscribe({
      next: (resp: HasResponses) => {
        this.coursePresent = true;
        this.courseId = courseid;
        if (resp.hasResponsesBySession === undefined) {
          return;
        }
        for (const sessionName of Object.keys(resp.hasResponsesBySession)) {
          if (resp.hasResponsesBySession[sessionName]) {
            const modalContent = `<p><strong>There are existing feedback responses for this course.</strong></p>
          Modifying records of enrolled students will result in some existing responses
          from those modified students to be deleted. You may wish to download the data
          before you make the changes.`;
            this.simpleModalService.openInformationModal(
              'Existing feedback responses',
              SimpleModalType.WARNING,
              modalContent,
            );
          }
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.coursePresent = false;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
      complete: () => {
        this.isLoadingCourseEnrollPage = false;
      },
    });
    this.getExistingStudents(courseid);
  }

  /**
   * Scrolls user to the target section.
   */
  navigateTo(target: string): void {
    this.pageScrollService.scroll({
      document: this.document,
      duration: 500,
      scrollTarget: `#${target}`,
      scrollOffset: 70,
    });
  }
}
