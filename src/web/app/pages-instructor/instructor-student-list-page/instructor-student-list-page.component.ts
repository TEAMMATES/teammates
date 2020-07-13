import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { CourseService, CourseStatistics } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { LoadingBarService } from '../../../services/loading-bar.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Course, Courses, InstructorPrivilege, Student, Students } from '../../../types/api-output';
import { StudentListRowModel } from '../../components/student-list/student-list.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentIndexedData {
  [key: string]: Student[];
}

interface CourseTab {
  course: Course;
  studentList: StudentListRowModel[];
  hasTabExpanded: boolean;
  hasStudentLoaded: boolean;
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

  constructor(private instructorService: InstructorService,
              private courseService: CourseService,
              private studentService: StudentService,
              private statusMessageService: StatusMessageService,
              private loadingBarService: LoadingBarService) {
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  /**
   * Loads courses of current instructor.
   */
  loadCourses(): void {
    this.loadingBarService.showLoadingBar();
    this.courseService.getAllCoursesAsInstructor('active')
        .pipe(finalize(() => this.loadingBarService.hideLoadingBar()))
        .subscribe((courses: Courses) => {
          courses.courses.forEach((course: Course) => {
            const courseTab: CourseTab = {
              course,
              studentList: [],
              hasTabExpanded: false,
              hasStudentLoaded: false,
              stats: {
                numOfSections: 0,
                numOfStudents: 0,
                numOfTeams: 0,
              },
            };

            this.courseTabList.push(courseTab);
          });
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
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
    this.studentService.getStudentsFromCourse({ courseId: courseTab.course.courseId })
        .pipe(finalize(() => courseTab.hasStudentLoaded = true))
        .subscribe((students: Students) => {
          courseTab.studentList = []; // Reset the list of students for the course
          const sections: StudentIndexedData = students.students.reduce((acc: StudentIndexedData, x: Student) => {
            const term: string = x.sectionName;
            (acc[term] = acc[term] || []).push(x);
            return acc;
          }, {});

          Object.keys(sections).forEach((key: string) => {
            const studentsInSection: Student[] = sections[key];
            const studentList: StudentListRowModel[] = studentsInSection.map((studentInSection: Student) => {
              return {
                student: studentInSection,
                isAllowedToModifyStudent: false,
                isAllowedToViewStudentInSection: false,
              };
            });

            this.loadPrivilege(courseTab, key, studentList);
          });

          courseTab.stats = this.courseService.calculateCourseStatistics(students.students);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  loadPrivilege(courseTab: CourseTab, sectionName: string, students: StudentListRowModel[]): void {
    this.instructorService.loadInstructorPrivilege({ sectionName, courseId: courseTab.course.courseId })
        .subscribe((instructorPrivilege: InstructorPrivilege) => {
          students.forEach((studentModel: StudentListRowModel) => {
            if (studentModel.student.sectionName === sectionName) {
              studentModel.isAllowedToViewStudentInSection = instructorPrivilege.canViewStudentInSections;
              studentModel.isAllowedToModifyStudent = instructorPrivilege.canModifyStudent;
            }
          });

          courseTab.studentList.push(...students);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Removes the student from course and update the course statistics.
   */
  removeStudentFromCourse(courseTab: CourseTab, studentEmail: string): void {
    this.courseService.removeStudentFromCourse(courseTab.course.courseId, studentEmail).subscribe(() => {
      courseTab.studentList =
          courseTab.studentList.filter(
              (studentModel: StudentListRowModel) => studentModel.student.email !== studentEmail);

      const students: Student[] =
          courseTab.studentList.map((studentModel: StudentListRowModel) => studentModel.student);
      courseTab.stats = this.courseService.calculateCourseStatistics(students);

      this.statusMessageService
          .showSuccessToast(`Student is successfully deleted from course "${courseTab.course.courseId}"`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }
}
