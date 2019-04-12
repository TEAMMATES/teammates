import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Course, Courses, InstructorPrivilege, JoinState, Student, Students } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentListSectionData, StudentListStudentData } from '../student-list/student-list-section-data';

interface Statistic {
  students: number;
  sections: number;
  teams: number;
}

interface StudentIndexedData {
  [key: string]: Student[];
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
  user: string = '';
  courseList!: Courses;
  viewStudent!: boolean[];
  courseInfo: {[key: string]: StudentListSectionData[]} = {};
  overallStats!: Statistic;
  courseStats: {[key: string]: Statistic} = {};

  constructor(private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;

      this.overallStats = {
        students: 0,
        sections: 0,
        teams: 0,
      };

      this.loadCourses();
    });
  }

  /**
   * Loads courses of current instructor.
   */
  loadCourses(): void {
    this.httpRequestService.get('/courses', {
      entitytype: 'instructor',
      coursestatus: 'active',
    }).subscribe((courses: Courses) => {
      this.courseList = courses;

      this.courseList.courses.forEach((course: Course) => {
        this.loadStudents(course.courseId);
      });

      this.viewStudent = Array<boolean>(this.courseList.courses.length).fill(false);

    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Loads students of a specified course.
   */
  loadStudents(courseId: string): void {
    this.httpRequestService.get('/students', {
      courseid: courseId,
    }).subscribe((students: Students) => {

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

      this.courseStats[courseId] = {
        students: students.students.length,
        sections: Object.keys(sections).length,
        teams: Object.keys(teams).length,
      };

      this.overallStats.students += this.courseStats[courseId].students;
      this.overallStats.sections += this.courseStats[courseId].sections;
      this.overallStats.teams += this.courseStats[courseId].teams;

      Object.keys(sections).forEach((key: string) => {
        const studentsInSection: Student[] = sections[key];

        const data: StudentListStudentData[] = [];
        studentsInSection.forEach((student: Student) => {
          const studentData: StudentListStudentData = {
            name : student.name,
            status : (student.joinState === JoinState.JOINED) ? 'Joined' : 'Yet to Join',
            email : student.email,
            team : student.teamName,
          };
          data.push(studentData);
        });

        this.loadPrivilege(courseId, key, data);
      });
    }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); });
  }

  /**
   * Loads privilege of an instructor for a specified course and section.
   */
  loadPrivilege(courseId: string, sectionName: string, students: StudentListStudentData[]): void {
    this.httpRequestService.get('/instructor/privilege', {
      courseid: courseId,
      sectionname: sectionName,
    }).subscribe((instructorPrivilege: InstructorPrivilege) => {
      const temp: StudentListSectionData = {
        sectionName,
        students,
        isAllowedToViewStudentInSection : instructorPrivilege.canViewStudentInSections,
        isAllowedToModifyStudent : instructorPrivilege.canModifyStudent,
      };

      if (!this.courseInfo[courseId]) {
        this.courseInfo[courseId] = [];
      }

      this.courseInfo[courseId].push(temp);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
