import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import moment from 'moment-timezone';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { Course, CourseArchive, MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

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

  activeCourses: ActiveCourse[] = [];
  archivedCourses: ArchivedCourse[] = [];
  softDeletedCourses: SoftDeletedCourse[] = [];
  instructorList: Instructor[] = [];
  courseStats: { [key: string]: { [key: string]: number } } = {};

  canDeleteAll: boolean = true;
  canRestoreAll: boolean = true;

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService,
              private courseService: CourseService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadInstructorCourses();
    });
  }

  /**
   * Loads instructor courses required for this page.
   */
  loadInstructorCourses(): void {
    const paramMap: { [key: string]: string } = {
      user: this.user,
    };
    this.timezones = Object.keys(this.timezoneService.getTzOffsets());
    this.timezone = moment.tz.guess();
    this.httpRequestService.get('/instructor/courses', paramMap).subscribe((resp: InstructorCourses) => {
      this.activeCourses = resp.activeCourses;
      this.archivedCourses = resp.archivedCourses;
      this.softDeletedCourses = resp.softDeletedCourses;
      this.instructorList = resp.instructorList;

      for (const course of this.activeCourses) {
        for (const instructor of this.instructorList) {
          if (course.id === instructor.courseId) {
            course.canModifyCourse = instructor.privileges.courseLevel.canmodifycourse;
            course.canModifyStudent = instructor.privileges.courseLevel.canmodifystudent;
            break;
          }
        }
      }

      for (const course of this.archivedCourses) {
        for (const instructor of this.instructorList) {
          if (course.id === instructor.courseId) {
            course.canModifyCourse = instructor.privileges.courseLevel.canmodifycourse;
            break;
          }
        }
      }

      for (const course of this.softDeletedCourses) {
        for (const instructor of this.instructorList) {
          if (course.id === instructor.courseId) {
            course.canModifyCourse = instructor.privileges.courseLevel.canmodifycourse;
            if (!course.canModifyCourse) {
              this.canDeleteAll = false;
              this.canRestoreAll = false;
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
      this.statusMessageService.showErrorMessage(`Course ${courseId} is not found!`);
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      user: this.user,
    };
    this.httpRequestService.get('/course/stats', paramMap).subscribe((resp: CourseStats) => {
      this.courseStats[courseId] = {
        sections: resp.sectionsTotal,
        teams: resp.teamsTotal,
        students: resp.studentsTotal,
        unregistered: resp.unregisteredTotal,
      };
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Auto-detects timezone for instructor.
   */
  onAutoDetectTimezone(): void {
    this.timezone = moment.tz.guess();
  }

  /**
   * Submits the data to add the new course.
   */
  onSubmit(): void {
    if (!this.newCourseId || !this.newCourseName) {
      this.statusMessageService.showErrorMessage(
          'Please make sure you have filled in both Course ID and Name before adding the course!');
      return;
    }

    this.courseService.createCourse({
      courseName: this.newCourseName,
      timeZone: this.timezone,
      courseId: this.newCourseId,
    }).subscribe((course: Course) => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessMessage('The course has been added. '
          + `Click <a href=\"/web/instructor/courses/enroll?courseid=${course.courseId}\">here</a> `
          + `to add students to the course or click <a href=\"/web/instructor/courses/edit?courseid=${course.courseId}`
          + '\">here</a> to add other instructors.'
          + '<br>If you don\'t see the course in the list below, please refresh the page after a few moments.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });

    this.newCourseId = '';
    this.newCourseName = '';
    this.timezone = moment.tz.guess();
  }

  /**
   * Changes the status of an archived course.
   */
  changeArchiveStatus(courseId: string, toArchive: boolean): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage(`Course ${courseId} is not found!`);
      return;
    }
    this.courseService.changeArchiveStatus(courseId, {
      archiveStatus: toArchive,
    }).subscribe((courseArchive: CourseArchive) => {
      this.loadInstructorCourses();
      if (courseArchive.isArchived) {
        this.statusMessageService.showSuccessMessage(`The course has been archived.
          It will not appear in the home page any more. You can access archived courses from the 'Courses' tab.
          Go there to undo the archiving and bring the course back to the home page.`);
      } else {
        this.statusMessageService.showSuccessMessage('The course has been unarchived.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Moves an active/archived course to Recycle Bin.
   */
  onDelete(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage(`Course ${courseId} is not found!`);
      return;
    }
    this.courseService.binCourse(courseId).subscribe((course: Course) => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessMessage(
        `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Permanently deletes a soft-deleted course in Recycle Bin.
   */
  onDeletePermanently(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage(`Course ${courseId} is not found!`);
      return;
    }
    if (confirm(`Are you sure you want to permanently delete the course: ${courseId}? `
            + 'This operation will delete all students and sessions in this course. '
            + 'All instructors of this course will not be able to access it hereafter as well.')) {
      this.courseService.deleteCourse(courseId).subscribe(() => {
        this.loadInstructorCourses();
        this.statusMessageService.showSuccessMessage(`The course ${courseId} has been permanently deleted.`);
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
      this.statusMessageService.showErrorMessage(`Course ${courseId} is not found!`);
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: courseId,
      user: this.user,
    };
    this.httpRequestService.delete('/bin/course', paramMap).subscribe((resp: MessageOutput) => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessMessage(resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Permanently deletes all soft-deleted courses in Recycle Bin.
   */
  onDeleteAll(): void {
    if (confirm('Are you sure you want to permanently delete all the courses in Recycle Bin? ' +
            'This operation will delete all students and sessions in these courses. ' +
            'All instructors of these courses will not be able to access them hereafter as well.')) {
      const paramMap: { [key: string]: string } = {
        user: this.user,
      };
      this.httpRequestService.delete('/instructor/courses/permanentlyDeleteAll', paramMap)
          .subscribe((resp: MessageOutput) => {
            this.loadInstructorCourses();
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
    const paramMap: { [key: string]: string } = {
      user: this.user,
    };
    this.httpRequestService.put('/instructor/courses/restoreAll', paramMap).subscribe((resp: MessageOutput) => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessMessage(resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
