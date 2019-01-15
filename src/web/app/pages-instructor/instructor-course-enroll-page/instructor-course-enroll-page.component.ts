import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';
import { StatusMessage } from '../../status-message/status-message';

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
  coursePresent?: boolean;
  @ViewChild('moreInfo') moreInfo?: ElementRef;
  statusMessage: StatusMessage[] = [];

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
   * Checks whether the course is present.
   */
  getCourseEnrollPageData(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/course/enroll/pageData', paramMap).subscribe(
    (resp: CourseEnrollPageData) => {
      this.coursePresent = resp.isCoursePresent;
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
        .nativeElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

}
