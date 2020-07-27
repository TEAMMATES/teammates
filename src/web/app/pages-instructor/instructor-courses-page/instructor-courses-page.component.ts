import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  Course,
  CourseArchive,
  Courses,
  InstructorPrivilege,
  JoinState,
  MessageOutput,
  Student,
  Students,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';

interface CourseModel {
  course: Course;
  canModifyCourse: boolean;
  canModifyStudent: boolean;
  isLoadingCourseStats: boolean;
}

/**
 * Instructor courses list page.
 */
@Component({
  selector: 'tm-instructor-courses-page',
  templateUrl: './instructor-courses-page.component.html',
  styleUrls: ['./instructor-courses-page.component.scss'],
  animations: [collapseAnim],
})
export class InstructorCoursesPageComponent implements OnInit {

  activeCourses: CourseModel[] = [];
  archivedCourses: CourseModel[] = [];
  softDeletedCourses: CourseModel[] = [];
  courseStats: Record<string, Record<string, number>> = {};

  activeTableSortOrder: SortOrder = SortOrder.ASC;
  activeTableSortBy: SortBy = SortBy.COURSE_CREATION_DATE;
  archivedTableSortOrder: SortOrder = SortOrder.ASC;
  archivedTableSortBy: SortBy = SortBy.COURSE_NAME;
  deletedTableSortOrder: SortOrder = SortOrder.ASC;
  deletedTableSortBy: SortBy = SortBy.COURSE_NAME;

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  isLoading: boolean = false;
  isRecycleBinExpanded: boolean = false;
  canDeleteAll: boolean = true;
  canRestoreAll: boolean = true;
  isAddNewCourseFormExpanded: boolean = false;
  isArchivedCourseExpanded: boolean = false;

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private simpleModalService: SimpleModalService,
              private tableComparatorService: TableComparatorService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      if (queryParams.isAddNewCourse) {
        this.isAddNewCourseFormExpanded = queryParams.isAddNewCourse;
      }
      this.loadInstructorCourses();
    });
  }

  /**
   * Loads instructor courses required for this page.
   */
  loadInstructorCourses(): void {
    this.isLoading = true;
    this.activeCourses = [];
    this.archivedCourses = [];
    this.softDeletedCourses = [];
    this.courseService.getAllCoursesAsInstructor('active').subscribe((resp: Courses) => {
      forkJoin(
          resp.courses.map((course: Course) =>
              this.instructorService.loadInstructorPrivilege({ courseId: course.courseId })),
      ).subscribe((privileges: InstructorPrivilege[]) => {
        resp.courses.forEach((course: Course, index: number) => {
          const canModifyCourse: boolean = privileges[index].canModifyCourse;
          const canModifyStudent: boolean = privileges[index].canModifyStudent;
          const isLoadingCourseStats: boolean = false;
          const activeCourse: CourseModel = Object.assign({},
              { course, canModifyCourse, canModifyStudent, isLoadingCourseStats });
          this.activeCourses.push(activeCourse);
        });
        this.sortCoursesEvent(SortBy.COURSE_CREATION_DATE);
      }, (error: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(error.error.message);
      });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    }, () => this.isLoading = false);

    this.courseService.getAllCoursesAsInstructor('archived').subscribe((resp: Courses) => {
      for (const course of resp.courses) {
        this.instructorService.loadInstructorPrivilege({
          courseId: course.courseId,
        }).subscribe((instructorPrivilege: InstructorPrivilege) => {
          const canModifyCourse: boolean = instructorPrivilege.canModifyCourse;
          const canModifyStudent: boolean = instructorPrivilege.canModifyStudent;
          const isLoadingCourseStats: boolean = false;
          const archivedCourse: CourseModel = Object.assign({},
              { course, canModifyCourse, canModifyStudent, isLoadingCourseStats });
          this.archivedCourses.push(archivedCourse);
        }, (error: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(error.error.message);
        });
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });

    this.courseService.getAllCoursesAsInstructor('softDeleted').subscribe((resp: Courses) => {
      for (const course of resp.courses) {
        this.instructorService.loadInstructorPrivilege({ courseId: course.courseId })
            .subscribe((instructorPrivilege: InstructorPrivilege) => {
              const canModifyCourse: boolean = instructorPrivilege.canModifyCourse;
              const canModifyStudent: boolean = instructorPrivilege.canModifyStudent;
              const isLoadingCourseStats: boolean = false;
              const softDeletedCourse: CourseModel = Object.assign({},
                  { course, canModifyCourse, canModifyStudent, isLoadingCourseStats });
              this.softDeletedCourses.push(softDeletedCourse);
              if (!softDeletedCourse.canModifyCourse) {
                this.canDeleteAll = false;
                this.canRestoreAll = false;
              }
            }, (error: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(error.error.message);
            });
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Constructs the url for course stats from the given course id.
   */
  getCourseStats(idx: number): void {
    const course: CourseModel = this.activeCourses[idx];
    const courseId: string = course.course.courseId;
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }
    course.isLoadingCourseStats = true;
    this.studentService.getStudentsFromCourse({ courseId })
        .pipe(finalize(() => course.isLoadingCourseStats = false))
        .subscribe((students: Students) => {
          this.courseStats[courseId] = {
            sections: (new Set(students.students.map((value: Student) => value.sectionName))).size,
            teams: (new Set(students.students.map((value: Student) => value.teamName))).size,
            students: students.students.length,
            unregistered: students.students.filter((value: Student) => value.joinState === JoinState.NOT_JOINED).length,
          };
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Changes the status of an archived course.
   */
  changeArchiveStatus(courseId: string, toArchive: boolean): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }
    this.courseService.changeArchiveStatus(courseId, {
      archiveStatus: toArchive,
    }).subscribe((courseArchive: CourseArchive) => {
      if (courseArchive.isArchived) {
        this.changeModelFromActiveToArchived(courseId);
        this.statusMessageService.showSuccessToast(`The course ${courseId} has been archived.
          It will not appear on the home page anymore.`);
      } else {
        this.changeModelFromArchivedToActive(courseId);
        this.statusMessageService.showSuccessToast('The course has been unarchived.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Moves a course model from active courses list to archived list.
   * This is to reduce the need to refresh the entire list of courses multiple times.
   */
  changeModelFromActiveToArchived(courseId: string): void {
    const courseToBeRemoved: CourseModel | undefined = this.findCourse(this.activeCourses, courseId);
    this.activeCourses = this.removeCourse(this.activeCourses, courseId);
    if (courseToBeRemoved !== undefined) {
      this.archivedCourses.push(courseToBeRemoved);
      this.archivedCourses.sort(this.sortBy(this.archivedTableSortBy, this.archivedTableSortOrder));
    }
  }

  /**
   * Moves a course model from archived courses list to active list.
   * This is to reduce the need to refresh the entire list of courses multiple times.
   */
  changeModelFromArchivedToActive(courseId: string): void {
    const courseToBeRemoved: CourseModel | undefined = this.findCourse(this.archivedCourses, courseId);
    this.archivedCourses = this.removeCourse(this.archivedCourses, courseId);
    if (courseToBeRemoved !== undefined) {
      this.activeCourses.push(courseToBeRemoved);
      this.activeCourses.sort(this.sortBy(this.activeTableSortBy, this.activeTableSortOrder));
    }
  }

  /**
   * Finds and returns a course from the target course list.
   */
  findCourse(targetList: CourseModel[], courseId: string): CourseModel | undefined {
    return targetList.find((model: CourseModel) => {
      return model.course.courseId === courseId;
    });
  }

  /**
   * Removes a course from the target course list and returns the result list.
   */
  removeCourse(targetList: CourseModel[], courseId: string): CourseModel[] {
    return targetList.filter((model: CourseModel) => {
      return model.course.courseId !== courseId;
    });
  }

  /**
   * Moves an active/archived course to Recycle Bin.
   */
  onDelete(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal('Warning: The course will be moved to the recycle bin.',
            SimpleModalType.WARNING, 'Are you sure you want to continue?');
    modalRef.result.then(() => {
      this.courseService.binCourse(courseId).subscribe((course: Course) => {
        this.moveCourseToRecycleBin(courseId, course.deletionTimestamp);
        this.statusMessageService.showSuccessToast(
          `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Moves an active/archived course to Recycle Bin.
   * This is to reduce the need to refresh the entire list of courses multiple times.
   */
  moveCourseToRecycleBin(courseId: string, deletionTimeStamp: number): void {
    const activeCourseToBeRemoved: CourseModel | undefined = this.findCourse(this.activeCourses, courseId);
    this.activeCourses = this.removeCourse(this.activeCourses, courseId);
    if (activeCourseToBeRemoved !== undefined) {
      activeCourseToBeRemoved.course.deletionTimestamp = deletionTimeStamp;
      this.softDeletedCourses.push(activeCourseToBeRemoved);
      this.softDeletedCourses.sort(this.sortBy(this.deletedTableSortBy, this.deletedTableSortOrder));
    } else {
      const archivedCourseToBeRemoved: CourseModel | undefined = this.findCourse(this.archivedCourses, courseId);
      this.archivedCourses = this.removeCourse(this.archivedCourses, courseId);
      if (archivedCourseToBeRemoved !== undefined) {
        archivedCourseToBeRemoved.course.deletionTimestamp = deletionTimeStamp;
        this.softDeletedCourses.push(archivedCourseToBeRemoved);
        this.softDeletedCourses.sort(this.sortBy(this.deletedTableSortBy, this.deletedTableSortOrder));
      }
    }
  }

  /**
   * Permanently deletes a soft-deleted course in Recycle Bin.
   */
  onDeletePermanently(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }
    const modalContent: string = `<strong>Are you sure you want to permanently delete ${courseId}?</strong><br>
        This operation will delete all students and sessions in these courses.
        All instructors of these courses will not be able to access them hereafter as well.`;

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete course <strong>${ courseId }</strong> permanently?`, SimpleModalType.DANGER, modalContent);
    modalRef.componentInstance.courseId = courseId;
    modalRef.result.then(() => {
      this.courseService.deleteCourse(courseId).subscribe(() => {
        this.softDeletedCourses = this.removeCourse(this.softDeletedCourses, courseId);
        this.statusMessageService.showSuccessToast(`The course ${courseId} has been permanently deleted.`);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
    }, () => {});
  }

  /**
   * Restores a soft-deleted course from Recycle Bin.
   */
  onRestore(courseId: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast(`Course ${courseId} is not found!`);
      return;
    }

    this.courseService.restoreCourse(courseId).subscribe((resp: MessageOutput) => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessToast(resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Permanently deletes all soft-deleted courses in Recycle Bin.
   */
  onDeleteAll(): void {
    const modalContent: string = `<strong>Are you sure you want to permanently delete all the courses in the Recycle Bin?</strong><br>
        This operation will delete all students and sessions in these courses.
        All instructors of these courses will not be able to access them hereafter as well.`;

    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        'Deleting all courses permanently?', SimpleModalType.DANGER, modalContent);
    modalRef.result.then(() => {
      const deleteRequests: Observable<MessageOutput>[] = [];
      this.softDeletedCourses.forEach((courseToDelete: CourseModel) => {
        deleteRequests.push(this.courseService.deleteCourse(courseToDelete.course.courseId));
      });

      forkJoin(deleteRequests).subscribe(() => {
        this.softDeletedCourses = [];
        this.statusMessageService.showSuccessToast('All courses have been permanently deleted.');
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });

    }, () => {});
  }

  /**
   * Restores all soft-deleted courses from Recycle Bin.
   */
  onRestoreAll(): void {
    const restoreRequests: Observable<MessageOutput>[] = [];
    this.softDeletedCourses.forEach((courseToRestore: CourseModel) => {
      restoreRequests.push(this.courseService.restoreCourse(courseToRestore.course.courseId));
    });

    forkJoin(restoreRequests).subscribe(() => {
      this.loadInstructorCourses();
      this.statusMessageService.showSuccessToast('All courses have been restored.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Sorts the active courses table
   */
  sortCoursesEvent(by: SortBy): void {
    this.activeTableSortBy = by;
    this.activeTableSortOrder =
        this.activeTableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.activeCourses.sort(this.sortBy(by, this.activeTableSortOrder));
  }

  /**
   * Sorts the archived courses table
   */
  sortArchivedCoursesEvent(by: SortBy): void {
    this.archivedTableSortBy = by;
    this.archivedTableSortOrder =
      this.archivedTableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.archivedCourses.sort(this.sortBy(by, this.archivedTableSortOrder));
  }

  /**
   * Sorts the soft-deleted courses table
   */
  sortDeletedCoursesEvent(by: SortBy): void {
    this.deletedTableSortBy = by;
    this.deletedTableSortOrder =
      this.deletedTableSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.softDeletedCourses.sort(this.sortBy(by, this.deletedTableSortOrder));
  }

  /**
   * Returns a function to determine the order of sort
   */
  sortBy(by: SortBy, order: SortOrder):
      ((a: CourseModel , b: CourseModel) => number) {
    return (a: CourseModel, b: CourseModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.COURSE_ID:
          strA = a.course.courseId ? a.course.courseId : '';
          strB = b.course.courseId ? b.course.courseId : '';
          break;
        case SortBy.COURSE_NAME:
          strA = a.course.courseName;
          strB = b.course.courseName;
          break;
        case SortBy.COURSE_CREATION_DATE:
          strA = a.course.creationTimestamp.toString();
          strB = b.course.creationTimestamp.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    };
  }
}
