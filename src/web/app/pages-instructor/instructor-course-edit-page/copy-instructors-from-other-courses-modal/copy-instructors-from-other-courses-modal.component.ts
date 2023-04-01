import { Component, EventEmitter, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { InstructorService } from '../../../../services/instructor.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import { Instructor, InstructorPermissionRole, Instructors } from '../../../../types/api-output';
import { Intent } from '../../../../types/api-request';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { ErrorMessageOutput } from '../../../error-message-output';
import { CourseTabModel, InstructorToCopyCandidateModel } from './copy-instructors-from-other-courses-modal-model';

/**
 * Modal to select instructors to copy from other courses.
 */
@Component({
  selector: 'tm-copy-instructors-from-other-courses-modal',
  templateUrl: './copy-instructors-from-other-courses-modal.component.html',
  styleUrls: ['./copy-instructors-from-other-courses-modal.component.scss'],
})
export class CopyInstructorsFromOtherCoursesModalComponent {

  @Output()
  copyClickedEvent: EventEmitter<Instructor[]> = new EventEmitter();

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  InstructorPermissionRole: typeof InstructorPermissionRole = InstructorPermissionRole;

  // data
  courses: CourseTabModel[] = [];

  readonly notDisplayedToStudentText: string = '(NOT displayed to students)';
  coursesSortBy: SortBy | undefined;
  isCopyingSelectedInstructors: boolean = false;

  constructor(public activeModal: NgbActiveModal,
              public statusMessageService: StatusMessageService,
              public instructorService: InstructorService,
              private tableComparatorService: TableComparatorService) { }

  /**
   * Toggles specific card and loads instructors if needed.
   */
  toggleCard(course: CourseTabModel): void {
    course.isTabExpanded = !course.isTabExpanded;
    if (!course.hasInstructorsLoaded) {
      this.loadInstructors(course);
    }
  }

  /**
   * Loads the instructors in the course.
   */
  loadInstructors(course: CourseTabModel): void {
    course.hasInstructorsLoaded = false;
    course.hasLoadingFailed = false;
    course.instructorCandidates = [];
    this.instructorService.loadInstructors({
      courseId: course.courseId,
      intent: Intent.FULL_DETAIL,
    }).subscribe({
      next: (response: Instructors) => {
        response.instructors.forEach((i: Instructor) => {
          const instructorToCopy: InstructorToCopyCandidateModel = {
            instructor: i,
            isSelected: false,
          };
          course.instructorCandidates.push(instructorToCopy);
        });
        course.hasInstructorsLoaded = true;
      },
      error: (resp: ErrorMessageOutput) => {
        course.hasLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Attempts to copy the selected instructors.
   */
  copySelectedInstructors(): void {
    this.isCopyingSelectedInstructors = true;
    const instructors: Instructor[] = this.getSelectedInstructors();
    this.copyClickedEvent.emit(instructors);
  }

  /**
   * Gets the selected instructors to copy.
   */
  getSelectedInstructors(): Instructor[] {
    const selectedInstructors: Instructor[] = [];
    this.courses.forEach((course: CourseTabModel) => {
      if (course.instructorCandidates.length > 0) {
        selectedInstructors.push(
          ...course.instructorCandidates
            .filter((c: InstructorToCopyCandidateModel) => c.isSelected)
            .map((c: InstructorToCopyCandidateModel) => c.instructor),
        );
      }
    });
    return selectedInstructors;
  }

  /**
   * Checks the option selected to sort courses.
   */
  isSelectedForSorting(by: SortBy): boolean {
    return this.coursesSortBy === by;
  }

  /**
   * Check whether the course instructors are sorted by the given type and in the given order.
   */
  isSortInstructorsBy(course: CourseTabModel, by: SortBy, order: SortOrder): boolean {
    return course.instructorCandidatesSortBy === by && course.instructorCandidatesSortOrder === order;
  }

  /**
   * Sorts the list of courses.
   */
  sortCourses(by: SortBy): void {
    this.coursesSortBy = by;
    this.courses.sort(this.sortCoursesBy(by));
  }

  getAriaSort(course: CourseTabModel, by: SortBy): String {
    if (course.instructorCandidatesSortBy !== by) {
      return 'none';
    }
    return course.instructorCandidatesSortOrder === SortOrder.ASC ? 'ascending' : 'descending';
  }

  /**
   * Sorts the list of instructors for a course.
   */
  sortInstructorsToCopyForCourse(course: CourseTabModel, by: SortBy): void {
    course.instructorCandidatesSortBy = by;
    // reverse the sort order
    course.instructorCandidatesSortOrder =
      course.instructorCandidatesSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    course.instructorCandidates.sort(this.sortInstructorsBy(by, course.instructorCandidatesSortOrder));
  }

  /**
   * Generates a sorting function for courses.
   */
  protected sortCoursesBy(by: SortBy):
      ((a: CourseTabModel, b: CourseTabModel) => number) {
    return ((a: CourseTabModel, b: CourseTabModel): number => {
      let strA: string;
      let strB: string;
      let order: SortOrder;
      switch (by) {
        case SortBy.COURSE_ID:
          strA = a.courseId;
          strB = b.courseId;
          order = SortOrder.ASC;
          break;
        case SortBy.COURSE_NAME:
          strA = a.courseName;
          strB = b.courseName;
          order = SortOrder.ASC;
          break;
        case SortBy.COURSE_CREATION_DATE:
          strA = a.creationTimestamp.toString();
          strB = b.creationTimestamp.toString();
          order = SortOrder.DESC;
          break;
        default:
          strA = '';
          strB = '';
          order = SortOrder.ASC;
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }
  /**
   * Generates a sorting function for instructors.
   */
  protected sortInstructorsBy(by: SortBy, order: SortOrder):
      ((a: InstructorToCopyCandidateModel, b: InstructorToCopyCandidateModel) => number) {
    return ((a: InstructorToCopyCandidateModel, b: InstructorToCopyCandidateModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.RESPONDENT_NAME:
          strA = a.instructor.name;
          strB = b.instructor.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.instructor.email;
          strB = b.instructor.email;
          break;
        case SortBy.INSTRUCTOR_DISPLAYED_TEXT:
          strA = a.instructor.isDisplayedToStudents
            ? a.instructor.displayedToStudentsAs || ''
            : this.notDisplayedToStudentText;
          strB = b.instructor.isDisplayedToStudents
            ? b.instructor.displayedToStudentsAs || ''
            : this.notDisplayedToStudentText;
          break;
        case SortBy.INSTRUCTOR_PERMISSION_ROLE:
          strA = a.instructor.role || '';
          strB = b.instructor.role || '';
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, order, strA, strB);
    });
  }

  /**
   * Checks whether there are any selected instructors.
   */
  get isAnyInstructorCandidatesSelected(): boolean {
    return this.courses.reduce((a: boolean, b: CourseTabModel) => {
      return a || !!b.instructorCandidates.find((c: InstructorToCopyCandidateModel) => c.isSelected);
    }, false);
  }

}
