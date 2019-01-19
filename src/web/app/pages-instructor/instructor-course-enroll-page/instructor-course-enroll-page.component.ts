import { Component, ContentChild, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';

import { HotTableRegisterer } from '@handsontable/angular';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { StatusMessage } from '../../components/status-message/status-message';

/**
 * Colors representing the server side status message
 */
enum color {
  /**
   * yellow status box
   */
  WARNING,
  /**
   * red status box
   */
  DANGER,
  /**
   * no status message to show
   */
  NONE,
}

interface ServerStatusMessage {
  color: color;
  text: string;
}

interface CourseEnrollPageData {
  isCoursePresent: boolean;
  statusMessage: ServerStatusMessage;
}

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

  @Input() isCollapsed: boolean = false;
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

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
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
    this.httpRequestService.post('/courses/enrollSave', paramMap, this.enrollData)
        .subscribe((resp: EnrollResultPanelList) => {
          this.showEnrollResults = true;
          this.statusMessage.pop(); // removes any existing status message
          this.statusMessage.push({
            message: 'Enrollment Successful. Summary given below',
            color: 'success',
          });
          this.enrollResultPanelList = resp.enrollResultPanelList;
        }, (resp: ErrorMessageOutput) => {
          this.statusMessage.pop(); // removes any existing status message
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
   * Toggles the view of spreadsheet interface
   * and/or its affiliated buttons
   */
  togglePanel(): void {
    this.isCollapsed = !this.isCollapsed;
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
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/course/enroll/pageData', paramMap).subscribe(
    (resp: CourseEnrollPageData) => {
      this.coursePresent = resp.isCoursePresent;
      this.courseid = courseid;
      if (resp.statusMessage && resp.statusMessage.text !== '') {
        this.statusMessage.push({
          message: resp.statusMessage.text,
          color: resp.statusMessage.color.toString().toLowerCase(),
        });
      }
    }, (resp: ErrorMessageOutput) => {
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
