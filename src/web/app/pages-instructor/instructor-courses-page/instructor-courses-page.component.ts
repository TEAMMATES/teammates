import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { HttpRequestService } from "../../../services/http-request.service";
import { StatusMessageService } from "../../../services/status-message.service";
import { ErrorMessageOutput } from "../../message-output";
import {environment} from "../../../environments/environment";

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

  activeCourses?: ActiveCourses;
  archivedCourses?: ArchivedCourses;
  softDeletedCourses?: SoftDeletedCourses;

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

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

}
