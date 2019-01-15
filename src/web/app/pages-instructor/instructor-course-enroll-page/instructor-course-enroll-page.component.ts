import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';

interface CourseInfo {
  isCoursePresent: boolean;
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

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseInfo(queryParams.courseid);
    });
  }

  /**
   * Checks whether the course is present.
   */
  getCourseInfo(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/course/present', paramMap).subscribe(
    (resp: CourseInfo) => {
      this.coursePresent = resp.isCoursePresent;
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
