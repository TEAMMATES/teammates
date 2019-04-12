import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Courses, InstructorPrivilege, JoinState, Student, Students } from '../../../types/api-output';
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
  courseStats: {[key: string]: Statistic} = {};

  constructor(private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
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
      this.viewStudent = Array<boolean>(this.courseList.courses.length).fill(false);

    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Toggles specific card and loads students if needed
   */
  toggleCard(index: number, courseId: string): void {
    if (this.viewStudent[index]) {
      this.viewStudent[index] = false;
    } else {
      if (!this.courseInfo[courseId]) {
        this.loadStudents(courseId);
      }

      this.viewStudent[index] = true;
    }
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
