import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HotTableRegisterer } from '@handsontable/angular';
import Handsontable from 'handsontable';
import { DetailedSettings } from 'handsontable/plugins/contextMenu';
import { concat, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { ProgressBarService } from '../../../services/progress-bar.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { EnrollStudents, HasResponses, JoinState, Student, Students } from '../../../types/api-output';
import { StudentEnrollRequest, StudentsEnrollRequest } from '../../../types/api-request';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { StatusMessage } from '../../components/status-message/status-message';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import { EnrollStatus } from './enroll-status';

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
  animations: [collapseAnim],
})
export class InstructorCourseEnrollPageComponent implements OnInit {
  GENERAL_ERROR_MESSAGE: string = `You may check that: "Section" and "Comment" are optional while "Team", "Name",
        and "Email" must be filled. "Section", "Team", "Name", and "Comment" should start with an
        alphabetical character, unless wrapped by curly brackets "{}", and should not contain vertical bar "|" and
        percentage sign "%". "Email" should contain some text followed by one "@" sign followed by some
        more text. "Team" should not have the same format as email to avoid mis-interpretation.`;
  SECTION_ERROR_MESSAGE: string = 'Section cannot be empty if the total number of students is more than 100. ';
  TEAM_ERROR_MESSAGE: string = 'Duplicated team detected in different sections. ';

  // enum
  EnrollStatus: typeof EnrollStatus = EnrollStatus;
  courseId: string = '';
  coursePresent?: boolean;
  isLoadingCourseEnrollPage: boolean = false;
  showEnrollResults?: boolean = false;
  enrollErrorMessage: string = '';
  statusMessage: StatusMessage[] = [];
  unsuccessfulEnrolls: { [email: string]: string } = {};

  @ViewChild('moreInfo') moreInfo?: ElementRef;

  @Input() isNewStudentsPanelCollapsed: boolean = false;
  @Input() isExistingStudentsPanelCollapsed: boolean = true;

  colHeaders: string[] = ['Section', 'Team', 'Name', 'Email', 'Comments'];
  contextMenuOptions: DetailedSettings = {
    items: {
      row_above: {},
      row_below: {},
      remove_row: {},
      undo: {},
      redo: {},
      cut: {},
      copy: {},
      paste: {
        key: 'paste',
        name: 'Paste',
        callback: this.pasteClick,
      },
      make_read_only: {},
      alignment: {},
    },
  };

  hotRegisterer: HotTableRegisterer = new HotTableRegisterer();
  newStudentsHOT: string = 'newStudentsHOT';

  enrollResultPanelList?: EnrollResultPanel[];
  existingStudents: Student[] = [];

  existingStudentsHOT: string = 'existingStudentsHOT';
  isExistingStudentsPresent: boolean = true;
  hasLoadingStudentsFailed: boolean = false;
  isLoadingExistingStudents: boolean = false;
  isAjaxSuccess: boolean = true;
  isEnrolling: boolean = false;

  allStudentChunks: StudentEnrollRequest[][] = [];
  invalidRowsIndex: Set<number> = new Set();
  newStudentRowsIndex: Set<number> = new Set();
  modifiedStudentRowsIndex: Set<number> = new Set();
  unchangedStudentRowsIndex: Set<number> = new Set();
  numberOfStudentsPerRequest: number = 50; // at most 50 students per chunk

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private studentService: StudentService,
              private progressBarService: ProgressBarService,
              private simpleModalService: SimpleModalService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.getCourseEnrollPageData(queryParams.courseid);
    });
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

    const lastColIndex: number = 4;
    const newStudentsHOTInstance: Handsontable =
        this.hotRegisterer.getInstance(this.newStudentsHOT);
    const hotInstanceColHeaders: string[] = (newStudentsHOTInstance.getColHeader() as string[]);

    // Reset error highlighting on a new submission
    this.resetTableStyle(newStudentsHOTInstance, 0,
        newStudentsHOTInstance.getData().length - 1,
        0,
        hotInstanceColHeaders.indexOf(this.colHeaders[lastColIndex]));

    // Remove error highlight on click
    newStudentsHOTInstance.addHook('afterSelectionEnd', (row: number, column: number,
                                                         row2: number, column2: number) => {
      this.resetTableStyle(newStudentsHOTInstance, row, row2, column, column2);
    });

    // Record the row with its index on the table
    const studentEnrollRequests: Map<number, StudentEnrollRequest> = new Map();

    // Parse the user input to be requests.
    // Handsontable contains null value initially,
    // see https://github.com/handsontable/handsontable/issues/3927
    newStudentsHOTInstance.getData()
        .forEach((row: string[], index: number) => {
          if (!row.every((cell: string) => cell === null || cell === '')) {
            studentEnrollRequests.set(index, {
              section: row[hotInstanceColHeaders.indexOf(this.colHeaders[0])] === null
                  ? '' : row[hotInstanceColHeaders.indexOf(this.colHeaders[0])].trim(),
              team: row[hotInstanceColHeaders.indexOf(this.colHeaders[1])] === null
                  ? '' : row[hotInstanceColHeaders.indexOf(this.colHeaders[1])].trim(),
              name: row[hotInstanceColHeaders.indexOf(this.colHeaders[2])] === null
                  ? '' : row[hotInstanceColHeaders.indexOf(this.colHeaders[2])].trim(),
              email: row[hotInstanceColHeaders.indexOf(this.colHeaders[3])] === null
                  ? '' : row[hotInstanceColHeaders.indexOf(this.colHeaders[3])].trim(),
              comments: row[hotInstanceColHeaders.indexOf(this.colHeaders[4])] === null
                  ? '' : row[hotInstanceColHeaders.indexOf(this.colHeaders[4])].trim(),
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
      this.setTableStyleBasedOnFieldChecks(newStudentsHOTInstance, hotInstanceColHeaders);
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
          return this.studentService.enrollStudents(
              this.courseId, request,
          );
        }),
    );

    this.progressBarService.updateProgress(0);
    enrollRequests.pipe(finalize(() => {
      this.isEnrolling = false;
    })).subscribe({
      next: (resp: EnrollStudents) => {
        enrolledStudents.push(...resp.studentsData.students);

        if (resp.unsuccessfulEnrolls != null) {
          for (const unsuccessfulEnroll of resp.unsuccessfulEnrolls) {
            this.unsuccessfulEnrolls[unsuccessfulEnroll.studentEmail] = unsuccessfulEnroll.errorMessage;

            for (const index of studentEnrollRequests.keys()) {
              if (studentEnrollRequests.get(index)?.email === unsuccessfulEnroll.studentEmail) {
                this.invalidRowsIndex.add(index);
                break;
              }
            }
          }
        }
        const percentage: number = Math.round(100 * enrolledStudents.length / studentEnrollRequests.size);
        this.progressBarService.updateProgress(percentage);
      },
      complete: () => {
        this.showEnrollResults = true;
        this.statusMessage.pop(); // removes any existing error status message
        this.statusMessageService.showSuccessToast('Enrollment successful. Summary given below.');
        this.prepareEnrollmentResults(enrolledStudents, studentEnrollRequests);

        if (this.invalidRowsIndex.size > 0
          || this.newStudentRowsIndex.size > 0
          || this.modifiedStudentRowsIndex.size > 0
          || this.unchangedStudentRowsIndex.size > 0) {
          this.setTableStyleBasedOnFieldChecks(newStudentsHOTInstance, hotInstanceColHeaders);
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

  private prepareEnrollmentResults(enrolledStudents: Student[],
                                   studentEnrollRequests: Map<number, StudentEnrollRequest>): void {
    this.enrollResultPanelList = this.populateEnrollResultPanelList(this.existingStudents,
        enrolledStudents, studentEnrollRequests);

    this.studentService.getStudentsFromCourse({ courseId: this.courseId }).subscribe((resp: Students) => {
      this.existingStudents = resp.students;
      if (!this.isExistingStudentsPanelCollapsed) {
        const existingStudentTable: Handsontable = this.hotRegisterer.getInstance(this.existingStudentsHOT);
        this.loadExistingStudentsData(existingStudentTable, this.existingStudents);
      }
      this.isExistingStudentsPresent = true;
    });
  }

  private partitionStudentEnrollRequests(studentEnrollRequests: StudentEnrollRequest[]): void {
    let currentStudentChunk: StudentEnrollRequest[] = [];
    for (const request of studentEnrollRequests) {
      currentStudentChunk.push(request);
      if (currentStudentChunk.length >= this.numberOfStudentsPerRequest) {
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

      if ((studentEnrollRequests.size >= 100 && request.section === '')
          || request.team === '' || request.name === '' || request.email === '') {
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

      if (!emailMap.has(request.email)) {
        emailMap.set(request.email, key);
        return;
      }

      this.invalidRowsIndex.add(key);
      const firstIndex: number | undefined = emailMap.get(request.email);
      if (firstIndex !== undefined) {
        this.invalidRowsIndex.add(firstIndex);
      }
    });
    if (this.invalidRowsIndex.size > invalidRowsOriginalSize) {
      this.enrollErrorMessage += 'Found duplicated emails. ';
    }
  }

  private resetTableStyle(newStudentsHOTInstance: Handsontable,
                                 startRow: number, endRow: number, startCol: number, endCol: number): void {
    for (let row: number = startRow; row <= endRow; row += 1) {
      for (let col: number = startCol; col <= endCol; col += 1) {
        newStudentsHOTInstance.setCellMeta(row, col, 'className', 'valid-row');
      }
    }
    newStudentsHOTInstance.render();
  }

  private setTableStyleBasedOnFieldChecks(newStudentsHOTInstance: Handsontable,
                                          hotInstanceColHeaders: string[]): void {
    this.setRowStyle(this.invalidRowsIndex, 'invalid-row', newStudentsHOTInstance, hotInstanceColHeaders);
    this.setRowStyle(this.newStudentRowsIndex, 'new-row', newStudentsHOTInstance, hotInstanceColHeaders);
    this.setRowStyle(this.modifiedStudentRowsIndex, 'modified-row', newStudentsHOTInstance, hotInstanceColHeaders);
    this.setRowStyle(this.unchangedStudentRowsIndex, 'unchanged-row', newStudentsHOTInstance, hotInstanceColHeaders);

    newStudentsHOTInstance.render();
  }

  private setRowStyle(rowsIndex: Set<number>, style: string, newStudentsHOTInstance: Handsontable,
    hotInstanceColHeaders: string[]): void {
    for (const index of rowsIndex) {
      for (const header of this.colHeaders) {
        newStudentsHOTInstance.setCellMeta(index, hotInstanceColHeaders.indexOf(header), 'className', style);
      }
    }
  }

  private populateEnrollResultPanelList(existingStudents: Student[], enrolledStudents: Student[],
                                        enrollRequests: Map<number, StudentEnrollRequest>): EnrollResultPanel[] {

    const panels: EnrollResultPanel[] = [];
    const studentLists: Student[][] = [];
    const statuses: (string | EnrollStatus)[] = Object.values(EnrollStatus)
        .filter((value: string | EnrollStatus) => typeof value === 'string');

    for (let i = 0; i < statuses.length; i += 1) {
      studentLists.push([]);
    }

    const emailToIndexMap: Map<string, number> = new Map();
    enrollRequests.forEach((enrollRequest: StudentEnrollRequest, index: number) => {
      emailToIndexMap.set(enrollRequest.email, index);
    });

    // Identify students not in the enroll list.
    for (const existingStudent of existingStudents) {
      const enrolledStudent: Student | undefined = enrolledStudents.find((student: Student) => {
        return student.email === existingStudent.email;
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
        return student.email === enrolledStudent.email;
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
        return student.email === request.email;
      });

      if (enrolledStudent === undefined) {
        studentLists[EnrollStatus.ERROR].push({
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
    const index: number | undefined = emailToIndexMap.get(email);
    if (index !== undefined) {
      rowsIndex.add(index);
    }
  }

  private isSameEnrollInformation(enrolledStudent: Student, existingStudent: Student): boolean {
    return enrolledStudent.email === existingStudent.email
        && enrolledStudent.name === existingStudent.name
        && enrolledStudent.teamName === existingStudent.teamName
        && enrolledStudent.sectionName === existingStudent.sectionName
        && enrolledStudent.comments === existingStudent.comments;
  }

  /**
   * Adds new rows to the 'New students' spreadsheet interface
   * according to user input
   */
  addRows(numOfRows: number): void {
    this.hotRegisterer.getInstance(this.newStudentsHOT).alter(
        'insert_row_below', [], numOfRows);
  }

  /**
   * Toggles the view of 'New Students' spreadsheet interface
   * and/or its affiliated buttons
   */
  toggleNewStudentsPanel(): void {
    this.isNewStudentsPanelCollapsed = !this.isNewStudentsPanelCollapsed;
  }

  /**
   * Returns the length of the current spreadsheet.
   * Rows with all null values are filtered.
   */
  getSpreadsheetLength(dataHandsontable: string[][]): number {
    return dataHandsontable
        .filter((row: string[]) => (!row.every((cell: string) => cell === null)))
        .length;
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
    return studentsData.map((student: Student) => (headers.map(
        (header: string) => {
          if (header === 'team') {
            return (student as any).teamName;
          }
          if (header === 'section') {
            return (student as any).sectionName;
          }
          return (student as any)[header];
        },
    )));
  }

  /**
   * Loads existing student data into the spreadsheet interface.
   */
  loadExistingStudentsData(existingStudentsHOTInstance: Handsontable, studentsData: Student[]): void {
    existingStudentsHOTInstance.loadData(this.studentListDataToHandsontableData(
        studentsData, (existingStudentsHOTInstance.getColHeader() as any[])));
  }

  /**
   * Toggles the view of 'Existing Students' spreadsheet interface
   */
  toggleExistingStudentsPanel(): void {
    // Has to be done before the API call is made so that HOT is available for data population
    this.isExistingStudentsPanelCollapsed = !this.isExistingStudentsPanelCollapsed;
    this.isLoadingExistingStudents = true;
    const existingStudentsHOTInstance: Handsontable =
        this.hotRegisterer.getInstance(this.existingStudentsHOT);

    // Calling REST API only the first time when spreadsheet has no data
    if (this.getSpreadsheetLength(existingStudentsHOTInstance.getData()) !== 0) {
      this.isLoadingExistingStudents = false;
      return;
    }

    this.studentService.getStudentsFromCourse({ courseId: this.courseId }).subscribe({
      next: (resp: Students) => {
        if (resp.students.length) {
          this.loadExistingStudentsData(existingStudentsHOTInstance, resp.students);
        } else {
          // Shows a message if there are no existing students. Panel would not be expanded.
          this.isExistingStudentsPresent = false;
          this.isExistingStudentsPanelCollapsed = !this.isExistingStudentsPanelCollapsed; // Collapse the panel again
        }
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.isAjaxSuccess = false;
        this.isExistingStudentsPanelCollapsed = !this.isExistingStudentsPanelCollapsed; // Collapse the panel again
      },
      complete: () => {
        this.isLoadingExistingStudents = false;
      },
    });
  }

  /**
   * Trigger click button
   */
  pasteClick(): void {
    const element: HTMLElement =
        (document.getElementById('paste') as HTMLElement);
    element.click();
  }

  /**
   * Shows modal box when user clicks on the 'paste' option in the
   * Handsontable context menu
   */
  showPasteModalBox(): void {
    const modalContent: string =
      `Pasting data through the context menu is not supported due to browser restrictions.<br>
      Please use <kbd>Ctrl + V</kbd> or <kbd>⌘ + V</kbd> to paste your data instead.`;
    this.simpleModalService.openInformationModal('Pasting data through the context menu',
        SimpleModalType.WARNING, modalContent);
  }

  /**
   * Checks whether the course is present
   */
  getCourseEnrollPageData(courseid: string): void {
    this.existingStudents = [];
    this.hasLoadingStudentsFailed = false;
    this.isLoadingCourseEnrollPage = true;
    this.courseService.hasResponsesForCourse(courseid).subscribe({
      next: (resp: HasResponses) => {
        this.coursePresent = true;
        this.courseId = courseid;
        if (resp.hasResponsesBySession === undefined) {
          return;
        }
        for (const sessionName of Object.keys(resp.hasResponsesBySession)) {
          if (resp.hasResponsesBySession[sessionName]) {
            const modalContent: string = `<p><strong>There are existing feedback responses for this course.</strong></p>
          Modifying records of enrolled students will result in some existing responses
          from those modified students to be deleted. You may wish to download the data
          before you make the changes.`;
            this.simpleModalService.openInformationModal(
                'Existing feedback responses', SimpleModalType.WARNING, modalContent);
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
    this.studentService.getStudentsFromCourse({ courseId: courseid }).subscribe({
      next: (resp: Students) => {
        this.existingStudents = resp.students;
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasLoadingStudentsFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Shows user more information about the spreadsheet interfaces
   */
  navigateToMoreInfo(): void {
    (this.moreInfo as ElementRef)
        .nativeElement.scrollIntoView({ behavior: 'auto', block: 'start' });
  }
}
