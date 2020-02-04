import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { LoadingBarService } from '../../../services/loading-bar.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Course, Courses, InstructorPrivilege, Student, Students } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListSectionData, StudentListStudentData } from '../student-list/student-list-section-data';

interface Statistic {
  numOfStudents: number;
  numOfSections: number;
  numOfTeams: number;
}

interface StudentIndexedData {
  [key: string]: Student[];
}

interface CourseTab {
  course: Course;
  studentListSectionDataList: StudentListSectionData[];
  hasTabExpanded: boolean;
  hasStudentLoaded: boolean;
  stats: Statistic;
}

/**
 * Instructor student list page.
 */
@Component({
  selector: 'tm-instructor-student-list-page',
  templateUrl: './instructor-student-list-page.component.html',
  styleUrls: ['./instructor-student-list-page.component.scss'],
})
export class InstructorStudentListPageComponent implements OnInit {

  courseTabList: CourseTab[] = [];

  constructor(private httpRequestService: HttpRequestService,
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
              studentListSectionDataList: [],
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
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Toggles specific card and loads students if needed
   */
  toggleCard(courseTab: CourseTab): void {
    courseTab.hasTabExpanded = !courseTab.hasTabExpanded;
    if (!courseTab.hasStudentLoaded) {
      this.loadStudents(courseTab);
      courseTab.hasStudentLoaded = true;
    }
  }

  /**
   * Loads students of a specified course.
   */
  loadStudents(courseTab: CourseTab): void {
    this.studentService.getStudentsFromCourse(courseTab.course.courseId)
        .subscribe((students: Students) => {
          const sections: StudentIndexedData = students.students.reduce((acc: StudentIndexedData, x: Student) => {
            const term: string = x.sectionName;
            (acc[term] = acc[term] || []).push(x);
            return acc;
          }, {});

          const teams: StudentIndexedData = students.students.reduce((acc: StudentIndexedData, x: Student) => {
            const term: string = x.teamName;
            (acc[term] = acc[term] || []).push(x);
            return acc;
          }, {});

          courseTab.stats = {
            numOfStudents: students.students.length,
            numOfSections: Object.keys(sections).length,
            numOfTeams: Object.keys(teams).length,
          };

          Object.keys(sections).forEach((key: string) => {
            const studentsInSection: Student[] = sections[key];

            const data: StudentListStudentData[] = [];
            studentsInSection.forEach((student: Student) => {
              const studentData: StudentListStudentData = {
                name : student.name,
                status : student.joinState,
                email : student.email,
                team : student.teamName,
              };
              data.push(studentData);
            });

            this.loadPrivilege(courseTab, key, data);
          });
        }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  loadPrivilege(courseTab: CourseTab, sectionName: string, students: StudentListStudentData[]): void {
    this.httpRequestService.get('/instructor/privilege', {
      courseid: courseTab.course.courseId,
      sectionname: sectionName,
    }).subscribe((instructorPrivilege: InstructorPrivilege) => {
      const sectionData: StudentListSectionData = {
        sectionName,
        students,
        isAllowedToViewStudentInSection : instructorPrivilege.canViewStudentInSections,
        isAllowedToModifyStudent : instructorPrivilege.canModifyStudent,
      };

      courseTab.studentListSectionDataList.push(sectionData);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(courseTab: CourseTab, studentEmail: string): void {
    this.courseService.removeStudentFromCourse(courseTab.course.courseId, studentEmail).subscribe(() => {
      this.statusMessageService
          .showSuccessMessage(`Student is successfully deleted from course "${courseTab.course.courseId}"`);
      courseTab.studentListSectionDataList.forEach(
          (section: StudentListSectionData) => {
            section.students = section.students.filter(
                (student: StudentListStudentData) => student.email !== studentEmail);
          });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
