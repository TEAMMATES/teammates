import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin, Observable } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  Course,
  CourseArchive,
  JoinState,
  MessageOutput,
  Student,
  Students,
  Courses,
  InstructorPrivilege,
} from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  CoursePermanentDeletionConfirmModalComponent,
} from './course-permanent-deletion-confirm-modal/course-permanent-deletion-confirm-modal.component';
import {
  CourseSoftDeletionConfirmModalComponent,
} from './course-soft-deletion-confirm-modal/course-soft-deletion-confirm-modal.component';

interface ActiveCourse extends Course{
  canModifyCourse: boolean;
  canModifyStudent: boolean;
}

interface ArchivedOrSoftDeletedCourse extends Course{
  canModifyCourse: boolean;
}

/**
 * Sort criteria for the courses table.
 */
export enum SortBy {
  /**
   * Nothing.
   */
  NONE,

  /**
   * Course ID.
   */
  COURSE_ID,

  /**
   * Course Name.
   */
  COURSE_NAME,

  /**
   * Creation Date.
   */
  CREATION_DATE,
}

/**
 * Sort order for the courses table.
 */
export enum SortOrder {
  /**
   * Descending sort order.
   */
  DESC,

  /**
   * Ascending sort order
   */
  ASC,
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

  activeCourses: ActiveCourse[] = [];
  archivedCourses: ArchivedOrSoftDeletedCourse[] = [];
  softDeletedCourses: ArchivedOrSoftDeletedCourse[] = [];
  courseStats: { [key: string]: { [key: string]: number } } = {};

  tableSortOrder: SortOrder = SortOrder.ASC;
  tableSortBy: SortBy = SortBy.NONE;

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  isRecycleBinExpanded: boolean = false;
  canDeleteAll: boolean = true;
  canRestoreAll: boolean = true;

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private studentService: StudentService,
              private modalService: NgbModal) { }

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
    const activeCourses: ActiveCourse[] = [];
    const archivedCourses: ArchivedOrSoftDeletedCourse[] = [];
    const softDeletedCourses: ArchivedOrSoftDeletedCourse[] = [];
    this.courseService.getAllCoursesAsInstructor('active').subscribe((resp: Courses) => {
      for (const course of resp.courses) {
        this.httpRequestService.get('/instructor/privilege', {
          courseid: course.courseId,
        }).subscribe((instructorPrivilege: InstructorPrivilege) => {
          const canModifyCourse = instructorPrivilege.canModifyCourse;
          const canModifyStudent = instructorPrivilege.canModifyStudent;
          const activeCourse: ActiveCourse = Object.assign(course, { canModifyCourse, canModifyStudent });
          activeCourses.push(activeCourse);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
      }
      this.activeCourses = activeCourses;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });

    this.courseService.getAllCoursesAsInstructor('archived').subscribe((resp: Courses) => {
      for (const course of resp.courses) {
        this.httpRequestService.get('/instructor/privilege', {
          courseid: course.courseId,
        }).subscribe((instructorPrivilege: InstructorPrivilege) => {
          const canModifyCourse = instructorPrivilege.canModifyCourse;
          const archivedCourse: ArchivedOrSoftDeletedCourse = Object.assign(course, { canModifyCourse });
          archivedCourses.push(archivedCourse);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
      }
      this.archivedCourses = archivedCourses;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });

    this.courseService.getAllCoursesAsInstructor('softDeleted').subscribe((resp: Courses) => {
      for (const course of resp.courses) {
        this.httpRequestService.get('/instructor/privilege', {
          courseid: course.courseId,
        }).subscribe((instructorPrivilege: InstructorPrivilege) => {
          const canModifyCourse = instructorPrivilege.canModifyCourse;
          const softDeletedCourse: ArchivedOrSoftDeletedCourse = Object.assign(course, { canModifyCourse });
          softDeletedCourses.push(softDeletedCourse);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
      }
      this.softDeletedCourses = softDeletedCourses;
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
    this.studentService.getStudentsFromCourse(courseId).subscribe((students: Students) => {
      this.courseStats[courseId] = {
        sections: (new Set(students.students.map((value: Student) => value.sectionName))).size,
        teams: (new Set(students.students.map((value: Student) => value.teamName))).size,
        students: students.students.length,
        unregistered: students.students.filter((value: Student) => value.joinState === JoinState.NOT_JOINED)
          .length,
      };
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
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
        this.statusMessageService.showSuccessMessage(`The course ${courseId} has been archived.
          It will not appear on the home page anymore.`);
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
    const modalRef: NgbModalRef = this.modalService.open(CourseSoftDeletionConfirmModalComponent);
    modalRef.result.then(() => {
      this.courseService.binCourse(courseId).subscribe((course: Course) => {
        this.loadInstructorCourses();
        this.statusMessageService.showSuccessMessage(
          `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Permanently deletes a soft-deleted course in Recycle Bin.
   */
  onDeletePermanently(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorMessage(`Course ${courseId} is not found!`);
      return;
    }
    const modalRef: NgbModalRef = this.modalService.open(CoursePermanentDeletionConfirmModalComponent);
    modalRef.componentInstance.courseId = courseId;
    modalRef.result.then(() => {
      this.courseService.deleteCourse(courseId).subscribe(() => {
        this.loadInstructorCourses();
        this.statusMessageService.showSuccessMessage(`The course ${courseId} has been permanently deleted.`);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });
    }, () => {});
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
    const modalRef: NgbModalRef = this.modalService.open(CoursePermanentDeletionConfirmModalComponent);
    modalRef.componentInstance.isDeleteAll = true;
    modalRef.result.then(() => {
      const deleteRequests: Observable<MessageOutput>[] = [];
      this.softDeletedCourses.forEach((courseToDelete: ArchivedOrSoftDeletedCourse) => {
        deleteRequests.push(this.courseService.deleteCourse(courseToDelete.courseId));
      });

      forkJoin(deleteRequests).subscribe(() => {
        this.loadInstructorCourses();
        this.statusMessageService.showSuccessMessage('All courses have been permanently deleted.');
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
      });

    }, () => {});
  }

  /**
   * Restores all soft-deleted courses from Recycle Bin.
   */
  onRestoreAll(): void {
    const restoreRequests: Observable<MessageOutput>[] = [];
    this.softDeletedCourses.forEach((courseToRestore: ArchivedOrSoftDeletedCourse) => {
      restoreRequests.push(this.courseService.restoreCourse(courseToRestore.courseId));
    });

    forkJoin(restoreRequests).subscribe(() => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessMessage('All courses have been restored.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Sorts the courses table
   */
  sortCoursesEvent(by: SortBy): void {
    this.tableSortBy = by;
    this.tableSortOrder =
        this.tableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.activeCourses.sort(this.sortBy(by));
  }

  /**
   * Returns a function to determine the order of sort
   */
  sortBy(by: SortBy):
      ((a: ActiveCourse , b: ActiveCourse) => number) {
    return (a: ActiveCourse, b: ActiveCourse): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.COURSE_ID:
          strA = a.courseId ? a.courseId : '';
          strB = b.courseId ? b.courseId : '';
          break;
        case SortBy.COURSE_NAME:
          strA = a.courseName;
          strB = b.courseName;
          break;
        case SortBy.CREATION_DATE:
          strA = a.creationTimestamp.toString();
          strB = b.creationTimestamp.toString();
          break;
        default:
          strA = '';
          strB = '';
      }

      if (this.tableSortOrder === SortOrder.ASC) {
        return strA.localeCompare(strB);
      }
      if (this.tableSortOrder === SortOrder.DESC) {
        return strB.localeCompare(strA);
      }

      return 0;
    };
  }
}
