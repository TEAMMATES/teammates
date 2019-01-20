import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import moment from 'moment-timezone';
import { HttpRequestService } from "../../../services/http-request.service";
import { StatusMessageService } from "../../../services/status-message.service";
import { TimezoneService } from "../../../services/timezone.service";
import {ErrorMessageOutput, MessageOutput} from "../../message-output";

interface ActiveCourse {
  id: string;
  name: string;
  createdAt: string;
  canModifyCourse: boolean;
  canModifyStudent: boolean;
}

interface ArchivedCourse {
  id: string;
  name: string;
  createdAt: string;
  canModifyCourse: boolean;
}

interface SoftDeletedCourse {
  id: string;
  name: string;
  createdAt: string;
  deletedAt: string;
  canModifyCourse: boolean;
}

interface CourseStats {
  sectionsTotal: number;
  teamsTotal: number;
  studentsTotal: number;
  unregisteredTotal: number;
}

interface InstructorPrivileges {
  courseLevel: { [key: string]: boolean };
}

interface Instructor {
  courseId: string;
  privileges: InstructorPrivileges;
}

interface InstructorCourses {
  activeCourses: ActiveCourse[];
  archivedCourses: ArchivedCourse[];
  softDeletedCourses: SoftDeletedCourse[];
  instructorList: Instructor[];
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

  activeCourses?: ActiveCourse[];
  archivedCourses?: ArchivedCourse[];
  softDeletedCourses?: SoftDeletedCourse[];
  instructorList?: Instructor[];

  canDeleteAll: boolean = true;
  canRestoreAll: boolean = true;

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
      this.instructorList = resp.instructorList;

      for (let course of this.activeCourses) {
        for (let instructor of this.instructorList) {
          if (course.id == instructor.courseId) {
            course.canModifyCourse = instructor.privileges.courseLevel['canmodifycourse'];
            course.canModifyStudent = instructor.privileges.courseLevel['canmodifystudent'];
            break;
          }
        }
      }

      for (let course of this.archivedCourses) {
        for (let instructor of this.instructorList) {
          if (course.id == instructor.courseId) {
            course.canModifyCourse = instructor.privileges.courseLevel['canmodifycourse'];
            break;
          }
        }
      }

      for (let course of this.softDeletedCourses) {
        for (let instructor of this.instructorList) {
          if (course.id == instructor.courseId) {
            course.canModifyCourse = instructor.privileges.courseLevel['canmodifycourse'];
            if (!course.canModifyCourse) {
              this.canDeleteAll = this.canRestoreAll = false;
            }
            break;
          }
        }
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Constructs the url for course stats from the given course id.
   */
  getCourseStats(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage('Course' + courseId + 'is not found!');
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    this.httpRequestService.get('/course/stats', paramMap).subscribe((resp: CourseStats) => {
      const sectionsElement = document.getElementById('course-sections-' + courseId);
      const teamsElement = document.getElementById('course-teams-' + courseId);
      const studentsElement = document.getElementById('course-students-' + courseId);
      const unregisteredElement = document.getElementById('course-unregistered-' + courseId);
      if (sectionsElement && teamsElement && studentsElement && unregisteredElement) {
        sectionsElement.innerHTML = String(resp.sectionsTotal);
        sectionsElement.setAttribute("class", "link-disabled");
        teamsElement.innerHTML = String(resp.teamsTotal);
        teamsElement.setAttribute("class", "link-disabled");
        studentsElement.innerHTML = String(resp.studentsTotal);
        studentsElement.setAttribute("class", "link-disabled");
        unregisteredElement.innerHTML = String(resp.unregisteredTotal);
        unregisteredElement.setAttribute("class", "link-disabled");
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
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
    this.httpRequestService.put('/instructor/courses/archive', paramMap).subscribe((resp: MessageOutput) => {
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
    };
    this.httpRequestService.put('/instructor/courses/archive', paramMap).subscribe((resp: MessageOutput) => {
          this.loadInstructorCourses(this.user);
          this.statusMessageService.showSuccessMessage(resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Moves an active/archived course to Recycle Bin.
   */
  onDelete(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage('Course' + courseId + 'is not found!');
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      next: '/instructor/courses',
    };
    this.httpRequestService.put('/instructor/courses/delete', paramMap).subscribe((resp: MessageOutput) => {
      this.loadInstructorCourses(this.user);
      this.statusMessageService.showSuccessMessage(resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Permanently deletes a soft-deleted course in Recycle Bin.
   */
  onDeletePermanently(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage('Course' + courseId + 'is not found!');
      return;
    }
    if (confirm('Are you sure you want to permanently delete the course: ' + courseId + '? This operation will delete all students and sessions in this course. All instructors of this course will not be able to access it hereafter as well.')) {
      const paramMap: { [key: string]: string } = {
        courseid: courseId,
      };
      this.httpRequestService.delete('/instructor/courses/permanentlyDelete', paramMap).subscribe((resp: MessageOutput) => {
        this.loadInstructorCourses(this.user);
        this.statusMessageService.showSuccessMessage(resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    }
  }

  /**
   * Restores a soft-deleted course from Recycle Bin.
   */
  onRestore(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage('Course' + courseId + 'is not found!');
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
    };
    this.httpRequestService.put('/instructor/courses/restore', paramMap).subscribe((resp: MessageOutput) => {
      this.loadInstructorCourses(this.user);
      this.statusMessageService.showSuccessMessage(resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Permanently deletes all soft-deleted courses in Recycle Bin.
   */
  onDeleteAll(): void {
    if (confirm('Are you sure you want to permanently delete all the courses in Recycle Bin? This operation will delete all students and sessions in these courses. All instructors of these courses will not be able to access them hereafter as well.')) {
      const paramMap: { [key: string]: string } = {};
      this.httpRequestService.delete('/instructor/courses/permanentlyDeleteAll', paramMap).subscribe((resp: MessageOutput) => {
        this.loadInstructorCourses(this.user);
        this.statusMessageService.showSuccessMessage(resp.message);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    }
  }

  /**
   * Restores all soft-deleted courses from Recycle Bin.
   */
  onRestoreAll(): void {
    const paramMap: { [key: string]: string } = {};
    this.httpRequestService.put('/instructor/courses/restoreAll', paramMap).subscribe((resp: MessageOutput) => {
      this.loadInstructorCourses(this.user);
      this.statusMessageService.showSuccessMessage(resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
