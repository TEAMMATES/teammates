import { Component, ContentChild, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { HasResponses } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

import { HotTableRegisterer } from '@handsontable/angular';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { StatusMessage } from '../../components/status-message/status-message';

interface StudentAttributes {
  email: string;
  course: string;
  name: string;
  lastName: string;
  comments: string;
  team: string;
  section: string;
}

interface EnrollResultPanel {
  panelClass: string;
  messageForEnrollmentStatus: string;
  studentList: StudentAttributes[];
}

interface EnrollResultPanelList {
  enrollResultPanelList: EnrollResultPanel[];
}

interface StudentListResults {
  enrolledStudents: StudentAttributes[];
}

/**
 * Instructor course enroll page.
 */
@Component({
  selector: 'tm-instructor-course-enroll-page',
  templateUrl: './instructor-course-enroll-page.component.html',
  styleUrls: ['./instructor-course-enroll-page.component.scss'],
})
export class InstructorCourseEnrollPageComponent implements OnInit {

  user: string = '';
  courseid: string = '';
  coursePresent?: boolean;
  showEnrollResults?: boolean = false;
  statusMessage: StatusMessage[] = [];

  @ViewChild('moreInfo') moreInfo?: ElementRef;
  @ContentChild('pasteModalBox') pasteModalBox?: NgbModal;

  @Input() isNewStudentsPanelCollapsed: boolean = false;
  @Input() isExistingStudentsPanelCollapsed: boolean = true;

  colHeaders: String[] = ['Section', 'Team', 'Name', 'Email', 'Comments'];
  contextMenuOptions: String[] | Object[] =
    ['row_above',
      'row_below',
      'remove_row',
      'undo',
      'redo',
      {
        key: 'paste',
        name: 'Paste',
        callback: this.pasteClick,
      },
      'make_read_only',
      'alignment'];

  hotRegisterer: HotTableRegisterer = new HotTableRegisterer();
  newStudentsHOT: string = 'newStudentsHOT';

  enrollData?: string;
  enrollResultPanelList?: EnrollResultPanel[];

  existingStudentsHOT: string = 'existingStudentsHOT';
  isExistingStudentsPresent: boolean = true;
  loading: boolean = false;
  isAjaxSuccess: boolean = true;

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private ngbModal: NgbModal) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseEnrollPageData(queryParams.courseid);
    });
  }

  /**
   * Retrieves updated column header order and generates a header string.
   *
   * Example: Changes this array ['Section', 'Team', 'Name', 'Email', 'Comments']
   * into a string = "Section|Team|Name|Email|Comments\n"
   *
   */
  getUpdatedHeaderString(handsontableColHeader: string[]): string {
    const colHeaders: string = handsontableColHeader.join('|');
    return colHeaders.concat('\n');
  }

  /**
   * Retrieves user data rows in the spreadsheet interface and transforms it into a string.
   *
   * Null value from cell is changed to empty string after .join(). Filters empty rows in the process.
   *
   * Example:
   * 2 by 5 spreadsheetData (before)
   * ['TestSection1', 'Team1', 'null', 'test1@xample.com', 'test1comments']
   * ['TestSection2', null, 'TestName2', 'test2@example.com', null]
   *
   * 2 by 5 spreadsheetData (after)
   * "TestSection1|Team1||test1@xample.com|test1comments\n
   *  TestSection2||TestName2|test2@example.com|\n"
   */
  getUserDataRows(spreadsheetData: string[][]): string {
    // needs to check for '' as an initial empty row with null values will be converted to e.g. "||||" after .map
    return spreadsheetData.filter((row: string[]) => (!row.every((cell: string) => cell === null || cell === '')))
        .map((row: string[]) => row.join('|'))
        .map((row: string) => row.replace(/\n|\r/g, ''))
        .join('\n');
  }

  /**
   * Submits enroll data
   */
  submitEnrollData(): void {
    const newStudentsHOTInstance: Handsontable =
        this.hotRegisterer.getInstance(this.newStudentsHOT);
    const spreadsheetData: string[][] = newStudentsHOTInstance.getData();

    const hotInstanceColHeaders: string[] = (newStudentsHOTInstance.getColHeader() as string[]);
    const dataPushToTextarea: string =
        this.getUpdatedHeaderString(hotInstanceColHeaders);
    const userDataRows: string = this.getUserDataRows(spreadsheetData);

    this.enrollData = (userDataRows === ''
        ? '' : dataPushToTextarea + userDataRows); // only include header string if userDataRows is not empty

    const paramMap: { [key: string]: string } = {
      courseid: this.courseid,
      user: this.user,
    };
    this.httpRequestService.post('/course/enrollSave', paramMap, this.enrollData)
        .subscribe((resp: EnrollResultPanelList) => {
          this.showEnrollResults = true;
          this.statusMessage.pop(); // removes any existing error status message
          this.statusMessageService.showSuccessMessage('Enrollment successful. Summary given below.');
          this.enrollResultPanelList = resp.enrollResultPanelList;
        }, (resp: ErrorMessageOutput) => {
          this.statusMessage.pop(); // removes any existing error status message
          this.statusMessage.push({
            message: resp.error.message,
            color: 'danger',
          });
        });
  }

  /**
   * Adds new rows to the 'New students' spreadsheet interface
   * according to user input
   */
  addRows(numOfRows: number): void {
    this.hotRegisterer.getInstance(this.newStudentsHOT).alter(
        'insert_row', [], numOfRows);
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
  studentListDataToHandsontableData(studentsData: StudentAttributes[], handsontableColHeader: any[]): string[][] {
    const headers: string[] = handsontableColHeader.map(this.unCapitalizeFirstLetter);
    return studentsData.map((student: StudentAttributes) => (headers.map(
        (header: string) => (student as any)[header])));
  }

  /**
   * Loads existing student data into the spreadsheet interface.
   */
  loadExistingStudentsData(existingStudentsHOTInstance: Handsontable, studentsData: StudentAttributes[]): void {
    existingStudentsHOTInstance.loadData(this.studentListDataToHandsontableData(
        studentsData, (existingStudentsHOTInstance.getColHeader() as any[])));
  }

  /**
   * Toggles the view of 'Existing Students' spreadsheet interface
   */
  toggleExistingStudentsPanel(): void {
    // Has to be done before the API call is made so that HOT is available for data population
    this.isExistingStudentsPanelCollapsed = !this.isExistingStudentsPanelCollapsed;
    this.loading = true;
    const existingStudentsHOTInstance: Handsontable =
        this.hotRegisterer.getInstance(this.existingStudentsHOT);

    // Calling REST API only the first time when spreadsheet has no data
    if (this.getSpreadsheetLength(existingStudentsHOTInstance.getData()) !== 0) {
      this.loading = false;
      return;
    }

    const paramMap: { [key: string]: string } = {
      courseid: this.courseid,
      user: this.user,
    };
    this.httpRequestService.get('/course/enroll/students', paramMap).subscribe(
        (resp: StudentListResults) => {
          if (resp.enrolledStudents.length !== 0) {
            this.loadExistingStudentsData(existingStudentsHOTInstance, resp.enrolledStudents);
          } else {
            // Shows a message if there are no existing students. Panel would not be expanded.
            this.isExistingStudentsPresent = false;
            this.isExistingStudentsPanelCollapsed = !this.isExistingStudentsPanelCollapsed; // Collapse the panel again
          }
        }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
      this.isAjaxSuccess = false;
      this.isExistingStudentsPanelCollapsed = !this.isExistingStudentsPanelCollapsed; // Collapse the panel again
    });
    this.loading = false;
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
  showPasteModalBox(pasteModalBox: any): void {
    this.ngbModal.open(pasteModalBox);
  }

  /**
   * Reset page to default view
   */
  hideEnrollResults(): void {
    this.showEnrollResults = false;
    this.statusMessage.pop();
    window.scroll(0, 0);
  }

  /**
   * Checks whether the course is present
   */
  getCourseEnrollPageData(courseid: string): void {
    this.courseService.hasResponsesForCourse(courseid).subscribe((resp: HasResponses) => {
      this.coursePresent = true;
      this.courseid = courseid;
      if (resp.hasResponses) {
        this.statusMessageService.showWarningMessage('There are existing feedback responses for this course. '
            + 'Modifying records of enrolled students will result in some existing responses '
            + 'from those modified students to be deleted. You may wish to download the data '
            + 'before you make the changes.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.coursePresent = false;
      this.statusMessageService.showErrorMessage(resp.error.message);
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
