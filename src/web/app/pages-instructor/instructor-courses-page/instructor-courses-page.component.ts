import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import moment from 'moment-timezone';
import { HttpRequestService } from "../../../services/http-request.service";
import { StatusMessageService } from "../../../services/status-message.service";
import { TimezoneService } from "../../../services/timezone.service";
import {ErrorMessageOutput, MessageOutput} from "../../message-output";
import { environment } from "../../../environments/environment";

interface ActiveCourse {
  id: string;
  name: string;
  createdAt: string;
  teamLink: string;
}

interface ActiveCourses {
  activeCourses: ActiveCourse[];
}

interface ArchivedCourse {
  id: string;
  name: string;
  createdAt: string;
}

interface ArchivedCourses {
  archivedCourses: ArchivedCourse[];
}

interface SoftDeletedCourse {
  id: string;
  name: string;
  createdAt:string;
  deletedAt: string;
}

interface SoftDeletedCourses {
  softDeletedCourses: SoftDeletedCourse[];
}

interface InstructorCourses {
  activeCourses: ActiveCourses;
  archivedCourses: ArchivedCourses;
  softDeletedCourses: SoftDeletedCourses;
}

/**
 * Instructor courses list page.
 */
@Component({
  selector: 'tm-instructor-courses-page',
  templateUrl: './instructor-courses-page.component.html',
  styleUrls: ['./instructor-courses-page.component.scss'],
})
export class InstructorCoursesPageComponent implements OnInit {

  user: string = '';

  timezones: string[] = [];
  timezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';

  activeCourses?: ActiveCourses;
  archivedCourses?: ArchivedCourses;
  softDeletedCourses?: SoftDeletedCourses;

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadInstructorCourses(queryParams.user);
    });
  }

  /**
   * Loads instructor courses required for this page.
   */
  loadInstructorCourses(user: string): void {
    const paramMap: { [key: string]: string } = { user };
    this.timezones = Object.keys(this.timezoneService.getTzOffsets());
    this.timezone = moment.tz.guess();
    this.httpRequestService.get('/instructor/courses', paramMap).subscribe((resp: InstructorCourses) => {
      this.activeCourses = resp.activeCourses;
      this.archivedCourses = resp.archivedCourses;
      this.softDeletedCourses = resp.softDeletedCourses;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Constructs the url for course stats from the given course id.
   */
  getCourseStatsUrl(user: string, courseId: string): string {
    return `${this.backendUrl}/course/stats?courseid=${courseId}&user=${user}`;
  }

  onAutoDetectTimezone(): void {
    this.timezone = moment.tz.guess();
  }

  /**
   * Submits the data to add the new course.
   */
  onSubmit(): void {
    if (!this.newCourseId || !this.newCourseName) {
      this.statusMessageService.showErrorMessage('Please make sure you have filled in both Course ID and Name before adding the course!');
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: this.newCourseId,
      coursename: this.newCourseName,
      coursetimezone: this.timezone,
    };
    this.newCourseId = '';
    this.newCourseName = '';
    this.timezone = moment.tz.guess();
    this.httpRequestService.post('/instructor/courses', paramMap).subscribe((resp: MessageOutput) => {
          this.loadInstructorCourses(this.user);
          this.statusMessageService.showSuccessMessage(resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Archives an active course.
   */
  onArchive(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage('Course' + courseId + 'is not found!');
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      archive: 'true',
      next: '/instructor/courses',
    };
    this.httpRequestService.put('/instructor/courses', paramMap).subscribe((resp: MessageOutput) => {
          this.loadInstructorCourses(this.user);
          this.statusMessageService.showSuccessMessage(resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Unarchives an archived course.
   */
  onUnarchive(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage('Course' + courseId + 'is not found!');
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      archive: 'false',
      next: '/instructor/courses',
    };
    this.httpRequestService.put('/instructor/courses', paramMap).subscribe((resp: MessageOutput) => {
          this.loadInstructorCourses(this.user);
          this.statusMessageService.showSuccessMessage(resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

}
