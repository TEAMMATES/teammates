import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs/index';
import { map } from 'rxjs/internal/operators';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';
import { StudentListSectionData, StudentListStudentData } from '../student-list/student-list-section-data';

interface Course {
  id: string;
  name: string;
  isArchived: boolean;
  isInstructorAllowedToModify: boolean;
  isChecked: boolean | undefined;
}

interface StudentDetails {
  name: string;
  email: string;
  status: string;
  team: string;
  section: string;
  courseId: string;
  isChecked: boolean | undefined;
}

interface TeamDetails {
  name: string;
  students: StudentDetails[];
  section: string;
  courseId: string;
  isChecked: boolean | undefined;
}

interface SectionDetails {
  name: string;
  teams: TeamDetails[];
  isAllowedToViewStudents: boolean;
  isAllowedtoEditStudents: boolean;
  courseId: string;
  isChecked: boolean | undefined;
}

interface CourseDetails {
  id: string;
  name: string;
  createdAt: string;
  sections: SectionDetails[];
  isChecked: boolean | undefined;
}

interface GetCourseResponse {
  courses: Course[];
  isDisplayArchive: boolean;
}

interface GetCourseDetailsResponse {
  course: CourseDetails;
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
  isDisplayArchive: boolean = false;
  courses: Course[] = [];

  courseDetailsList: CourseDetails[] = [];
  sectionDetailsList: SectionDetails[] = [];
  teamDetailsList: TeamDetails[] = [];
  studentDetailsList: StudentDetails[] = [];

  coursesMap: Map<string, Course> = new Map();
  courseDetailsMap: Map<string, CourseDetails> = new Map();

  coursesStateMap: Map<string, boolean> = new Map();
  sectionsStateMap: Map<string, boolean> = new Map();
  teamsStateMap: Map<string, boolean> = new Map();
  studentsStateMap: Map<string, boolean> = new Map();

  courseStudentListSectionDataMap: Map<string, StudentListSectionData[]> = new Map();

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadCourses(this.user, this.isDisplayArchive);
    });
  }

  /**
   * Function to get initial data of the instructor's courses.
   */
  loadCourses(user: string, isDisplayArchive: boolean): void {
    const displayarchive: string = isDisplayArchive.toString();
    const paramMap: { [key: string]: string } = { displayarchive, user };
    this.httpRequestService.get('/instructor/students/courses', paramMap)
        .subscribe((resp: GetCourseResponse) => {
          this.courses = resp.courses;
          this.isDisplayArchive = resp.isDisplayArchive;

          if (!this.courses) {
            this.statusMessageService.showWarningMessage('You do not have any courses yet.');
          } else {
            this.courses.forEach((course: Course) => {
              this.coursesMap.set(course.id, course);
            });
          }

          if (this.isDisplayArchive === null) {
            this.statusMessageService.showErrorMessage('Error retrieving indicator for showing archived courses');
          }
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Function to change states of all courses.
   */
  toggleAllCoursesStateAtInput(defaultState: boolean): void {
    this.statusMessageService.showWarningMessage(`Default state input at toggleAllCoursesStateAtInput: ${defaultState}`);
    this.courses.forEach((course: Course) => {
      if (course !== undefined) {
        this.toggleCourseStateAtInput(course, defaultState);
      }
    });
  }

  /**
   * Function to trigger getting CourseDetails data for a course from backend.
   */
  toggleCourseStateAtInput(course: Course, designatedState?: boolean): void {
    if (course.isChecked === undefined) {
      course.isChecked = false;
    }
    const state: boolean = designatedState === undefined
        ? !course.isChecked
        : designatedState;

    this.fetchCourseDetails(course.id)
        .subscribe((courseDetails: CourseDetails | undefined) => {
          if (courseDetails !== undefined) {
            this.toggleCourseState(courseDetails, state);
            this.courseStudentListSectionDataMap.set(
                courseDetails.id, this.getStudentListSectionDataFromCourseDetails(courseDetails));
          }

        }, (error: any) => {
          this.statusMessageService.showErrorMessage(error);
        });
  }

  /**
   * Function to call backend API to get CourseDetails data for a course.
   */
  fetchCourseDetails(courseid: string): Observable<CourseDetails | undefined> {
    if (this.isCourseDetailsInList(courseid)) {
      return of(this.getCourseDetails(courseid));
    }

    const paramMap: { [courseid: string ]: string } = { courseid };
    return this.httpRequestService.get('/instructor/students', paramMap)
        .pipe(map((resp: GetCourseDetailsResponse | undefined) => {
          if (resp !== undefined) {
            if (resp.course !== undefined) {
              resp.course.isChecked = false;
              this.courseDetailsList.push(resp.course);
              return this.getCourseDetails(resp.course.id);
            }
          }

          this.statusMessageService.showErrorMessage(`Error retrieving course details for course of id ${courseid}`);
          return undefined;
        }));
  }

  isCourseDetailsInList(courseId: string): boolean {
    const filteredCourseDetailsList: CourseDetails[] =
        this.courseDetailsList.filter((courseDetails: CourseDetails) => courseDetails.id === courseId);

    return filteredCourseDetailsList.length > 0;
  }

  isSectionDetailsInList(section: SectionDetails): boolean {
    const filteredSectionDetailsList: SectionDetails[] = this.sectionDetailsList.filter(
        (sectionDetails: SectionDetails) =>
            sectionDetails.name === section.name
            && sectionDetails.courseId === section.courseId);

    return filteredSectionDetailsList.length > 0;
  }

  isTeamDetailsInList(team: TeamDetails): boolean {
    const filteredTeamDetailsList: TeamDetails[] = this.teamDetailsList.filter(
        (teamDetails: TeamDetails) =>
            team.name === teamDetails.name
            && team.section === teamDetails.section
            && team.courseId === teamDetails.courseId);

    return filteredTeamDetailsList.length > 0;
  }

  isStudentDetailsInList(student: StudentDetails): boolean {
    const filteredStudentDetailsList: StudentDetails[] = this.studentDetailsList.filter(
        (studentDetails: StudentDetails) =>
            student.email === studentDetails.email
            && student.team === studentDetails.team
            && student.section === studentDetails.section
            && student.courseId === studentDetails.courseId);

    return filteredStudentDetailsList.length > 0;
  }

  getCourse(courseId: string): Course | undefined {
    const filteredCourseList: Course[] =
        this.courses.filter((course: Course) => course.id === courseId);

    if (filteredCourseList.length > 0) {
      return filteredCourseList[0];
    }

    return undefined;
  }

  getCourseDetails(courseId: string): CourseDetails | undefined {
    const filteredCourseDetailsList: CourseDetails[] =
        this.courseDetailsList.filter((courseDetails: CourseDetails) => courseDetails.id === courseId);

    if (filteredCourseDetailsList.length > 0) {
      return filteredCourseDetailsList[0];
    }

    return undefined;
  }

  getSectionDetails(courseId: string, sectionName: string): SectionDetails | undefined {
    const filteredSectionDetailsList: SectionDetails[] = this.sectionDetailsList.filter(
        (sectionDetails: SectionDetails) =>
            sectionDetails.name === sectionName
            && sectionDetails.courseId === courseId);

    if (filteredSectionDetailsList.length > 0) {
      return filteredSectionDetailsList[0];
    }

    return undefined;
  }

  /*------------------------------Functions to toggle item state in state maps----------------------------------*/

  /**
   * Function to change the state of a specific course.
   */
  toggleCourseState(courseDetails: CourseDetails, designatedState?: boolean): void {
    const state: boolean = designatedState === undefined
        ? !courseDetails.isChecked
        : designatedState;

    courseDetails.isChecked = state;

    if (!this.isCourseDetailsInList(courseDetails.id)) {
      this.courseDetailsList.push(courseDetails);
    }

    const course: Course | undefined = this.getCourse(courseDetails.id);
    if (course !== undefined) {
      course.isChecked = state;
    }
    courseDetails.sections.forEach((section: SectionDetails) => {
      this.toggleSectionState(section, state);
    });
  }

  /**
   * Function to change the state of a specific section.
   */
  toggleSectionState(section: SectionDetails, designatedState?: boolean): void {
    const state: boolean = designatedState === undefined
        ? !section.isChecked
        : designatedState;

    section.isChecked = state;

    if (!this.isSectionDetailsInList(section)) {
      this.sectionDetailsList.push(section);
    }

    section.teams.forEach((team: TeamDetails) => {
      this.toggleTeamState(team, state);
    });
  }

  /**
   * Function to change the state of a specific team.
   */
  toggleTeamState(team: TeamDetails, designatedState?: boolean): void {
    const state: boolean = designatedState === undefined
        ? !team.isChecked
        : designatedState;

    team.isChecked = state;

    if (!this.isTeamDetailsInList(team)) {
      this.teamDetailsList.push(team);
    }

    team.students.forEach((student: StudentDetails) => {
      this.toggleStudentState(student, state);
    });
  }

  /**
   * Function to change the state of a specific student.
   */
  toggleStudentState(student: StudentDetails, designatedState?: boolean): void {
    const state: boolean = designatedState === undefined
        ? !student.isChecked
        : designatedState;

    student.isChecked = state;

    if (!this.isStudentDetailsInList(student)) {
      this.studentDetailsList.push(student);
    }
  }

  /*------------------------------Functions to get item state from state maps-----------------------------------*/
  /**
   * Function to get the list of students which need to be hidden on the StudentTable display.
   */
  getStudentsToHide(): string[] {
    const studentsToHide: string[] = this.studentDetailsList
        .filter((student: StudentDetails) => !student.isChecked)
        .map((student: StudentDetails) => student.email);

    return studentsToHide;
  }

  /*------------------------------Functions to formulate data for student list from item------------------------*/
  /**
   * Function to formulate data for student list.
   */
  getStudentListSectionDataForCourse(courseId: string): StudentListSectionData[] {
    const data: StudentListSectionData[] | undefined = this.courseStudentListSectionDataMap.get(courseId);
    if (data === undefined) {
      return [];
    }

    return data;
  }

  /**
   * Function to formulate data for student list.
   */
  getStudentListSectionDataFromCourseDetails(courseDetails: CourseDetails): StudentListSectionData[] {
    const sections: StudentListSectionData[] = [];
    courseDetails.sections.forEach((section: SectionDetails) => {
      const sectionData: StudentListSectionData = this.mapSectionForStudentList(section);
      sections.push(sectionData);
    });

    return sections;
  }

  /**
   * Function to formulate data for student list from SectionDetails.
   */
  mapSectionForStudentList(sectionDetails: SectionDetails): StudentListSectionData {
    const students: StudentListStudentData[] = [];

    sectionDetails.teams.forEach((team: TeamDetails) => {
      team.students.forEach((student: StudentDetails) => {
        const studentData: StudentListStudentData = this.mapStudentForStudentList(student);
        students.push(studentData);
      });
    });

    const section: StudentListSectionData = {
      students,
      sectionName: sectionDetails.name,
      isAllowedToViewStudentInSection: sectionDetails.isAllowedToViewStudents,
      isAllowedToModifyStudent: sectionDetails.isAllowedtoEditStudents,
    };

    return section;
  }

  /**
   * Function to formulate data for student list from StudentDetails.
   */
  mapStudentForStudentList(studentDetails: StudentDetails): StudentListStudentData {
    const student: StudentListStudentData = {
      name: studentDetails.name,
      team: studentDetails.team,
      email: studentDetails.email,
      status: studentDetails.status,
    };

    return student;
  }
}
