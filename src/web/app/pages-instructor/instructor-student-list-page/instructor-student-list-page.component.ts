import { HttpStatusCode } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { CourseService, CourseStatistics } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  Course,
  Courses,
  InstructorPermissionSet,
  InstructorPrivilege,
  Student,
  Students,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { JoinStatePipe } from '../../components/student-list/join-state.pipe';
import { StudentListRowModel } from '../../components/student-list/student-list.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentIndexedData {
  [key: string]: Student[];
}

export interface CourseTab {
  course: Course;
  studentList: StudentListRowModel[];
  studentSortBy: SortBy;
  studentSortOrder: SortOrder;
  hasTabExpanded: boolean;
  hasStudentLoaded: boolean;
  hasLoadingFailed: boolean;
  isAbleToViewStudents: boolean;
  stats: CourseStatistics;
}

/**
 * Instructor student list page.
 */
@Component({
  selector: 'tm-instructor-student-list-page',
  templateUrl: './instructor-student-list-page.component.html',
  styleUrls: ['./instructor-student-list-page.component.scss'],
  animations: [collapseAnim],
})
export class InstructorStudentListPageComponent implements OnInit {

  courseTabList: CourseTab[] = [];
  hasLoadingFailed: boolean = false;
  isLoadingCourses: boolean = false;

  // enum
  SortBy: typeof SortBy = SortBy;

  coursesSortBy: SortBy = SortBy.COURSE_CREATION_DATE;

  constructor(private instructorService: InstructorService,
              private courseService: CourseService,
              private studentService: StudentService,
              private statusMessageService: StatusMessageService,
              private tableComparatorService: TableComparatorService) {
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  /**
   * Loads courses of current instructor.
   */
  loadCourses(): void {
    this.hasLoadingFailed = false;
    this.isLoadingCourses = true;
    this.courseService.getAllCoursesAsInstructor('active')
        .pipe(finalize(() => {
          this.isLoadingCourses = false;
        }))
        .subscribe({
          next: (courses: Courses) => {
            courses.courses.forEach((course: Course) => {
              const courseTab: CourseTab = {
                course,
                studentList: [],
                studentSortBy: SortBy.NONE,
                studentSortOrder: SortOrder.ASC,
                hasTabExpanded: false,
                hasStudentLoaded: false,
                hasLoadingFailed: false,
                isAbleToViewStudents: true,
                stats: {
                  numOfSections: 0,
                  numOfStudents: 0,
                  numOfTeams: 0,
                },
              };

              this.courseTabList.push(courseTab);
            });
          },
          error: (resp: ErrorMessageOutput) => {
            this.courseTabList = [];
            this.hasLoadingFailed = true;
            this.statusMessageService.showErrorToast(resp.error.message);
          },
          complete: () => this.sortCourses(this.coursesSortBy),
        });
  }

  /**
   * Toggles specific card and loads students if needed
   */
  toggleCard(courseTab: CourseTab): void {
    courseTab.hasTabExpanded = !courseTab.hasTabExpanded;
    if (!courseTab.hasStudentLoaded) {
      this.loadStudents(courseTab);
    }
  }

  /**
   * Loads students of a specified course.
   */
  loadStudents(courseTab: CourseTab): void {
    courseTab.hasLoadingFailed = false;
    courseTab.hasStudentLoaded = false;
    this.studentService.getStudentsFromCourse({ courseId: courseTab.course.courseId })
        .subscribe({
          next: (students: Students) => {
            courseTab.studentList = []; // Reset the list of students for the course
            const sections: StudentIndexedData = students.students.reduce((acc: StudentIndexedData, x: Student) => {
              const term: string = x.sectionName;
              (acc[term] = acc[term] || []).push(x);
              return acc;
            }, {});

            this.instructorService.loadInstructorPrivilege({
              courseId: courseTab.course.courseId,
            })
                .pipe(finalize(() => {
                  courseTab.hasStudentLoaded = true;
                }))
                .subscribe({
                  next: (instructorPrivilege: InstructorPrivilege) => {
                    const courseLevelPrivilege: InstructorPermissionSet = instructorPrivilege.privileges.courseLevel;

                    Object.keys(sections).forEach((sectionName: string) => {
                      const sectionLevelPrivilege: InstructorPermissionSet =
                          instructorPrivilege.privileges.sectionLevel[sectionName] || courseLevelPrivilege;

                      const studentsInSection: Student[] = sections[sectionName];
                      const studentModels: StudentListRowModel[] = studentsInSection.map((stuInSection: Student) => {
                        return {
                          student: stuInSection,
                          isAllowedToViewStudentInSection: sectionLevelPrivilege.canViewStudentInSections,
                          isAllowedToModifyStudent: sectionLevelPrivilege.canModifyStudent,
                        };
                      });

                      courseTab.studentList.push(...studentModels);
                      courseTab.studentList.sort(this.sortStudentBy(SortBy.NONE, SortOrder.ASC));
                    });

                    courseTab.stats = this.courseService.calculateCourseStatistics(students.students);

                  },
                  error: (resp: ErrorMessageOutput) => {
                    courseTab.hasLoadingFailed = true;
                    courseTab.studentList = [];
                    this.statusMessageService.showErrorToast(resp.error.message);
                  },
                });
          },
          error: (resp: ErrorMessageOutput) => {
            if (resp.status === HttpStatusCode.Forbidden) {
              courseTab.isAbleToViewStudents = false;
              courseTab.hasStudentLoaded = true;
            } else {
              courseTab.hasLoadingFailed = true;
            }
            courseTab.studentList = [];
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
  }

  /**
   * Removes the student from course and update the course statistics.
   */
  removeStudentFromCourse(courseTab: CourseTab, studentEmail: string): void {
    this.courseService.removeStudentFromCourse(courseTab.course.courseId, studentEmail).subscribe({
      next: () => {
        courseTab.studentList =
            courseTab.studentList.filter(
                (studentModel: StudentListRowModel) => studentModel.student.email !== studentEmail);

        const students: Student[] =
            courseTab.studentList.map((studentModel: StudentListRowModel) => studentModel.student);
        courseTab.stats = this.courseService.calculateCourseStatistics(students);

        this.statusMessageService
            .showSuccessToast(`Student is successfully deleted from course "${courseTab.course.courseId}"`);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Checks the option selected to sort courses.
   */
  isSelectedForSorting(by: SortBy): boolean {
    return this.coursesSortBy === by;
  }

  /**
   * Sorts the courses in the list according to selected option.
   */
  sortCourses(by: SortBy): void {
    this.coursesSortBy = by;

    if (this.courseTabList.length > 1) {
      const coursesCopy: CourseTab[] = JSON.parse(JSON.stringify(this.courseTabList));
      coursesCopy.sort(this.sortCoursesBy(by));
      this.courseTabList = coursesCopy;
    }
  }

  /**
   * Returns a function to determine the order of sort for courses.
   */
  sortCoursesBy(by: SortBy):
    ((a: { course: Course }, b: { course: Course }) => number) {
    return ((a: { course: Course }, b: { course: Course }): number => {
      let strA: string;
      let strB: string;
      let order: SortOrder;
      switch (by) {
        case SortBy.COURSE_NAME:
          strA = a.course.courseName;
          strB = b.course.courseName;
          order = SortOrder.ASC;
          break;
        case SortBy.COURSE_ID:
          strA = a.course.courseId;
          strB = b.course.courseId;
          order = SortOrder.ASC;
          break;
        case SortBy.COURSE_CREATION_DATE:
          strA = String(a.course.creationTimestamp);
          strB = String(b.course.creationTimestamp);
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
   * Sorts the student list.
   */
  sortStudentList(courseTab: CourseTab, by: SortBy): void {
    courseTab.studentSortBy = by;
    courseTab.studentSortOrder =
      courseTab.studentSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    courseTab.studentList.sort(this.sortStudentBy(by, courseTab.studentSortOrder));
  }

  /**
   * Returns a function to determine the order of sort for students.
   */
  sortStudentBy(by: SortBy, order: SortOrder):
      ((a: StudentListRowModel, b: StudentListRowModel) => number) {
    const joinStatePipe: JoinStatePipe = new JoinStatePipe();
    if (by === SortBy.NONE) {
      // Default order: section name > team name > student name
      return ((a: StudentListRowModel, b: StudentListRowModel): number => {
        return this.tableComparatorService
            .compare(SortBy.SECTION_NAME, order, a.student.sectionName, b.student.sectionName)
          || this.tableComparatorService.compare(SortBy.TEAM_NAME, order, a.student.teamName, b.student.teamName)
          || this.tableComparatorService.compare(SortBy.RESPONDENT_NAME, order, a.student.name, b.student.name);
      });
    }
    return (a: StudentListRowModel, b: StudentListRowModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.student.sectionName;
          strB = b.student.sectionName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.student.name;
          strB = b.student.name;
          break;
        case SortBy.TEAM_NAME:
          strA = a.student.teamName;
          strB = b.student.teamName;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.student.email;
          strB = b.student.email;
          break;
        case SortBy.JOIN_STATUS:
          strA = joinStatePipe.transform(a.student.joinState);
          strB = joinStatePipe.transform(b.student.joinState);
          break;
        default:
          strA = '';
          strB = '';
      }

      return this.tableComparatorService.compare(by, order, strA, strB);
    };
  }
}
