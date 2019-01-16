import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';
import { StatusMessage } from '../../status-message/status-message';

import { HotTableRegisterer } from '@handsontable/angular';

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
  courseid?: string;
  coursePresent?: boolean;
  statusMessage: StatusMessage[] = [];
  @ViewChild('moreInfo') moreInfo?: ElementRef;

  @Input() isCollapsed: boolean = false;
  colHeaders: String[] = ['Section', 'Team', 'Name', 'Email', 'Comments'];
  targetElement!: Element;

  hotRegisterer: HotTableRegisterer = new HotTableRegisterer();
  newStudentsHOT: string = 'newStudentsHOT';

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseEnrollPageData(queryParams.courseid);
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
  togglePanel(event: any): void {
    this.targetElement =
        event.target.closest('div').querySelector('i');
    if (this.targetElement !== null) {
      this.toggleChevron();
      this.isCollapsed = !this.isCollapsed; // toggle boolean value
    }
  }

  /**
   * Handles chevron classes for toggle action
   */
  toggleChevron(): void {
    if (this.targetElement.className.includes('up')) {
      this.targetElement.classList.remove('fa-chevron-up');
      this.targetElement.classList.add('fa-chevron-down');
    } else {
      this.targetElement.classList.add('fa-chevron-up');
      this.targetElement.classList.remove('fa-chevron-down');
    }
  }

  /**
   * Checks whether the course is present.
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
