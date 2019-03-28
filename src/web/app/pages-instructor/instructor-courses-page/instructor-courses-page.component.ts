import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin, Observable } from 'rxjs';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Course, CourseArchive, MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  CoursePermanentDeletionConfirmModalComponent,
} from './course-permanent-deletion-confirm-modal/course-permanent-deletion-confirm-modal.component';
import {
  CourseSoftDeletionConfirmModalComponent,
} from './course-soft-deletion-confirm-modal/course-soft-deletion-confirm-modal.component';

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
              private courseService: CourseService,
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
    const paramMap: { [key: string]: string } = {
      user: this.user,
    };
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
      this.softDeletedCourses.forEach((courseToDelete: SoftDeletedCourse) => {
        deleteRequests.push(this.courseService.deleteCourse(courseToDelete.id));
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
    this.softDeletedCourses.forEach((courseToRestore: SoftDeletedCourse) => {
      restoreRequests.push(this.courseService.restoreCourse(courseToRestore.id));
    });

    forkJoin(restoreRequests).subscribe(() => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessMessage('All courses have been restored.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
