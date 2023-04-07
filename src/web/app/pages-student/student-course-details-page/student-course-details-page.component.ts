import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  Course, Instructor, Instructors, JoinState, Student, Students,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Student course details page.
 */
@Component({
  selector: 'tm-student-course-details-page',
  templateUrl: './student-course-details-page.component.html',
  styleUrls: ['./student-course-details-page.component.scss'],
})
export class StudentCourseDetailsPageComponent implements OnInit {
  // enum
  SortBy: typeof SortBy = SortBy;
  teammateProfilesSortBy: SortBy = SortBy.NONE;

  // data
  student: Student = {
    email: '',
    courseId: '',
    name: '',
    comments: '',
    joinState: JoinState.NOT_JOINED,
    teamName: '',
    sectionName: '',
  };

  course: Course = {
    courseId: '',
    courseName: '',
    institute: '',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };

  courseId: string = '';
  instructorDetails: Instructor[] = [];
  teammateProfiles: Student[] = [];

  isLoadingCourse: boolean = false;
  isLoadingStudent: boolean = false;
  isLoadingInstructor: boolean = false;
  isLoadingTeammates: boolean = false;
  hasLoadingFailed: boolean = false;

  constructor(private tableComparatorService: TableComparatorService,
              private route: ActivatedRoute,
              private instructorService: InstructorService,
              private studentService: StudentService,
              private courseService: CourseService,
              private statusMessageService: StatusMessageService) { }

  /**
   * Fetches relevant data to be displayed on page.
   */
  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.loadStudent(queryParams.courseid);
      this.loadCourse(queryParams.courseid);
      this.loadInstructors(queryParams.courseid);
    });
  }

  /**
   * Loads the course details.
   *
   * @param courseId id of the course queried
   */
  loadCourse(courseId: string): void {
    this.isLoadingCourse = true;
    this.courseService.getCourseAsStudent(courseId)
        .pipe(finalize(() => {
          this.isLoadingCourse = false;
        }))
        .subscribe({
          next: (course: Course) => {
            this.course = course;
          },
          error: (resp: ErrorMessageOutput) => {
            this.hasLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Loads the current logged-in student of the course.
   *
   * @param courseId id of the course queried
   */
  loadStudent(courseId: string): void {
    this.isLoadingStudent = true;
    this.studentService.getStudent(courseId)
        .pipe(finalize(() => {
          this.isLoadingStudent = false;
        }))
        .subscribe({
          next: (student: Student) => {
            this.student = student;
            this.loadTeammates(courseId, student.teamName);
          },
          error: (resp: ErrorMessageOutput) => {
            this.hasLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Loads the teammates of the current student.
   *
   * @param courseId id of the course queried
   * @param teamName team of current student
   */
  loadTeammates(courseId: string, teamName: string): void {
    this.isLoadingTeammates = true;
    this.teammateProfiles = [];
    this.studentService.getStudentsFromCourseAndTeam(courseId, teamName)
      .subscribe({
        next: (students: Students) => {
          // No teammates
          if (students.students.length === 1 && students.students[0].email === this.student.email) {
            this.isLoadingTeammates = false;
          }
          students.students.forEach((student: Student) => {
            // filter away current user
            if (student.email === this.student.email) {
              return;
            }
            this.teammateProfiles.push(student);
          });
          this.isLoadingTeammates = false;
        },
        error: (resp: ErrorMessageOutput) => {
          this.isLoadingTeammates = false;
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Loads the instructors of the course.
   *
   * @param courseId id of the course queried
   */
  loadInstructors(courseId: string): void {
    this.isLoadingInstructor = true;
    this.instructorService.loadInstructors({ courseId })
        .pipe(finalize(() => {
          this.isLoadingInstructor = false;
        }))
        .subscribe({
          next: (instructors: Instructors) => {
            this.instructorDetails = instructors.instructors;
          },
          error: (resp: ErrorMessageOutput) => {
            this.hasLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Checks the option selected to sort teammates.
   *
   * @param sortOption option for sorting
   */
  isSelectedForSorting(sortOption: SortBy): boolean {
    return this.teammateProfilesSortBy === sortOption;
  }

  /**
   * Sorts the teammates according to selected option.
   *
   * @param sortOption option for sorting
   */
  sortTeammatesBy(sortOption: SortBy): void {
    this.teammateProfilesSortBy = sortOption;

    if (this.teammateProfiles.length > 1) {
      this.teammateProfiles.sort(this.sortPanelsBy(sortOption));
    }
  }

  /**
   * Sorts the panels of teammates in order.
   *
   * @param sortOption option for sorting
   */
  sortPanelsBy(sortOption: SortBy):
      ((a: Student, b: Student) => number) {
    return ((a: Student, b: Student): number => {
      let strA: string;
      let strB: string;
      switch (sortOption) {
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(sortOption, SortOrder.ASC, strA, strB);
    });
  }

  retryLoading(): void {
    this.hasLoadingFailed = false;
    this.loadCourse(this.courseId);
    this.loadInstructors(this.courseId);
    this.loadStudent(this.courseId);
  }
}
