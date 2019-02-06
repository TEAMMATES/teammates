import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of } from 'rxjs/index';
import { map } from 'rxjs/internal/operators';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';
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
  isAllowedToEditStudents: boolean;
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

  searchQuery: string = '';

  courseDetailsList: CourseDetails[] = [];
  sectionDetailsList: SectionDetails[] = [];
  teamDetailsList: TeamDetails[] = [];
  studentDetailsList: StudentDetails[] = [];

  get allPresentCourses(): Course[] {
    return this.courses.filter((course: Course) => !(course.isArchived && !this.isDisplayArchive));
  }

  get allCheckedCourses(): Course[] {
    return this.allPresentCourses.filter((course: Course) => course.isChecked);
  }

  get allPresentCourseDetails(): CourseDetails[] {
    const courseDetailsList: CourseDetails[] = [];

    this.allPresentCourses.forEach((course: Course) =>  {
      const courseDetails: CourseDetails | null = this.getCourseDetails(course.id);
      if (courseDetails) {
        courseDetailsList.push(courseDetails);
      }
    });

    return courseDetailsList;
  }

  get allCheckedCourseDetails(): CourseDetails[] {
    return this.allPresentCourseDetails.filter((courseDetails: CourseDetails) => courseDetails.isChecked);
  }

  get allPresentSectionDetails(): SectionDetails[] {
    let allPresentSectionDetails: SectionDetails[] = [];

    this.allCheckedCourseDetails.forEach((courseDetails: CourseDetails) => {
      allPresentSectionDetails = allPresentSectionDetails.concat(courseDetails.sections);
    });

    return allPresentSectionDetails;
  }

  get allCheckedSectionDetails(): SectionDetails[] {
    return this.allPresentSectionDetails.filter((sectionDetails: SectionDetails) => sectionDetails.isChecked);
  }

  get allPresentTeamDetails(): TeamDetails[] {
    let allPresentTeamDetails: TeamDetails[] = [];

    this.allCheckedSectionDetails.forEach((sectionDetails: SectionDetails) => {
      allPresentTeamDetails = allPresentTeamDetails.concat(sectionDetails.teams);
    });

    return allPresentTeamDetails;
  }

  get allCheckedTeamDetails(): TeamDetails[] {
    return this.allPresentTeamDetails.filter((teamDetails: TeamDetails) => teamDetails.isChecked);
  }

  get allPresentStudentDetails(): StudentDetails[] {
    let allPresentStudentDetails: StudentDetails[] = [];

    this.allCheckedTeamDetails.forEach((teamDetails: TeamDetails) => {
      allPresentStudentDetails = allPresentStudentDetails.concat(teamDetails.students);
    });

    return allPresentStudentDetails;
  }

  get allCheckedStudentDetails(): StudentDetails[] {
    return this.allPresentStudentDetails.filter((studentDetails: StudentDetails) => studentDetails.isChecked);
  }

  get isAllPresentCoursesChecked(): boolean {
    if (this.allPresentCourses.length === 0) {
      return false;
    }
    return this.allPresentCourses.length === this.allCheckedCourses.length;
  }

  get isAllPresentCourseDetailsChecked(): boolean {
    if (this.allPresentCourseDetails.length === 0) {
      return false;
    }
    return this.allPresentCourseDetails.length === this.allCheckedCourseDetails.length;
  }

  get isAllPresentSectionDetailsChecked(): boolean {
    if (this.allPresentSectionDetails.length === 0) {
      return false;
    }
    return this.allPresentSectionDetails.length === this.allCheckedSectionDetails.length;
  }

  get isAllPresentTeamDetailsChecked(): boolean {
    if (this.allPresentTeamDetails.length === 0) {
      return false;
    }
    return this.allPresentTeamDetails.length === this.allCheckedTeamDetails.length;
  }

  get isAllPresentStudentDetailsChecked(): boolean {
    if (this.allPresentStudentDetails.length === 0) {
      return false;
    }
    return this.allPresentStudentDetails.length === this.allCheckedStudentDetails.length;
  }

  get isAnyPresentCoursesChecked(): boolean {
    if (this.allPresentCourses.length === 0) {
      return false;
    }
    return this.allCheckedCourses.length > 0;
  }

  courseStudentListSectionDataMap: { [key: string]: StudentListSectionData[] } = {};

  constructor(private route: ActivatedRoute, private router: Router, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadCourses(this.user);
    });
  }

  /**
   * Navigate to the instructor search page with query input and query type: student.
   */
  search(): void {
    this.router.navigate(['/web/instructor/search'], { queryParams: { studentSearchkey: this.searchQuery } });
  }

  /**
   * Navigate to the instructor enroll page for course.
   */
  enroll(courseid: string): void {
    this.router.navigate(['/web/instructor/courses/enroll'], { queryParams: { courseid } });
  }

  /**
   * Function to get initial data of the instructor's courses.
   */
  loadCourses(user: string): void {
    const paramMap: { [key: string]: string } = { user };
    this.httpRequestService.get('/instructor/students/courses', paramMap)
        .subscribe((resp: GetCourseResponse) => {
          this.courses = resp.courses;

          if (!this.courses) {
            this.statusMessageService.showWarningMessage('You do not have any courses yet.');
          }

          if (this.isDisplayArchive === undefined) {
            this.statusMessageService.showErrorMessage('Error retrieving indicator for showing archived courses');
          }
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Change value of the checkbox Display Archive Courses.
   */
  toggleDisplayArchive(): void {
    this.isDisplayArchive = !this.isDisplayArchive;
    const isDisplayArchiveChecked: boolean = this.isDisplayArchive;
    this.courses.forEach((course: Course) => {
      if (course && course.isArchived) {
        this.toggleCourseStateAtInput(course, isDisplayArchiveChecked);
      }
    });
  }

  /**
   * Function to change states of all courses.
   */
  toggleAllPresentCoursesStateAtInput(defaultState: boolean): void {
    this.allPresentCourses.forEach((course: Course) => {
      if (course) {
        this.toggleCourseStateAtInput(course, defaultState);
      }
    });
  }

  /**
   * Change the state of all sections presented on the screen.
   */
  toggleAllPresentSectionDetailsState(defaultState: boolean): void {
    this.allPresentSectionDetails.forEach((sectionDetails: SectionDetails) => {
      this.toggleSectionState(sectionDetails, defaultState);
    });
  }

  /**
   * Change the state of all teams presented on the screen.
   */
  toggleAllPresentTeamDetailsState(defaultState: boolean): void {
    this.allPresentTeamDetails.forEach((teamDetails: TeamDetails) => {
      this.toggleTeamState(teamDetails, defaultState);
    });
  }

  /**
   * Change the state of all students presented on the screen.
   */
  toggleAllPresentStudentDetailsState(defaultState: boolean): void {
    this.allPresentStudentDetails.forEach((studentDetails: StudentDetails) => {
      this.toggleStudentState(studentDetails, defaultState);
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
        .subscribe((courseDetails: CourseDetails | null) => {
          if (courseDetails) {
            this.toggleCourseState(courseDetails, state);
            this.courseStudentListSectionDataMap[courseDetails.id]
                = this.getStudentListSectionDataFromCourseDetails(courseDetails);
          }
        }, (error: any) => {
          this.statusMessageService.showErrorMessage(error);
        });
  }

  /**
   * Function to call backend API to get CourseDetails data for a course.
   */
  fetchCourseDetails(courseid: string): Observable<CourseDetails | null> {
    if (this.isCourseDetailsInList(courseid)) {
      return of(this.getCourseDetails(courseid));
    }

    const paramMap: { [courseid: string ]: string } = { courseid };
    return this.httpRequestService.get('/instructor/students', paramMap)
        .pipe(map((resp: GetCourseDetailsResponse) => {
          if (resp.course) {
            resp.course.isChecked = false;
            this.courseDetailsList.push(resp.course);
            return this.getCourseDetails(resp.course.id);
          }

          this.statusMessageService.showErrorMessage(`Error retrieving course details for course of id ${courseid}`);
          return null;
        }));
  }

  /**
   * If the courseDetails of a course is fetched.
   */
  isCourseDetailsInList(courseId: string): boolean {
    const filteredCourseDetailsList: CourseDetails[] =
        this.courseDetailsList.filter((courseDetails: CourseDetails) => courseDetails.id === courseId);

    return filteredCourseDetailsList.length > 0;
  }

  /**
   * If the sectionDetails is fetched.
   */
  isSectionDetailsInList(section: SectionDetails): boolean {
    const filteredSectionDetailsList: SectionDetails[] = this.sectionDetailsList.filter(
        (sectionDetails: SectionDetails) =>
            sectionDetails.name === section.name
            && sectionDetails.courseId === section.courseId);

    return filteredSectionDetailsList.length > 0;
  }

  /**
   * If the teamDetails is fetched.
   */
  isTeamDetailsInList(team: TeamDetails): boolean {
    const filteredTeamDetailsList: TeamDetails[] = this.teamDetailsList.filter(
        (teamDetails: TeamDetails) =>
            team.name === teamDetails.name
            && team.section === teamDetails.section
            && team.courseId === teamDetails.courseId);

    return filteredTeamDetailsList.length > 0;
  }

  /**
   * If the studentDetails is fetched.
   */
  isStudentDetailsInList(student: StudentDetails): boolean {
    const filteredStudentDetailsList: StudentDetails[] = this.studentDetailsList.filter(
        (studentDetails: StudentDetails) =>
            student.email === studentDetails.email
            && student.team === studentDetails.team
            && student.section === studentDetails.section
            && student.courseId === studentDetails.courseId);

    return filteredStudentDetailsList.length > 0;
  }

  /**
   * Get the course from the list of courses.
   */
  getCourse(courseId: string): Course | null {
    const filteredCourseList: Course[] =
        this.courses.filter((course: Course) => course.id === courseId);

    if (filteredCourseList.length > 0) {
      return filteredCourseList[0];
    }

    return null;
  }

  /**
   * Get the courseDetails from the list of courseDetails.
   */
  getCourseDetails(courseId: string): CourseDetails | null {
    const filteredCourseDetailsList: CourseDetails[] =
        this.courseDetailsList.filter((courseDetails: CourseDetails) => courseDetails.id === courseId);

    if (filteredCourseDetailsList.length > 0) {
      return filteredCourseDetailsList[0];
    }

    return null;
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

    const course: Course | null = this.getCourse(courseDetails.id);
    if (course) {
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
  getStudentsToHide(courseId: string): string[] {
    const studentsToHide: string[] = this.studentDetailsList
        .filter((student: StudentDetails) => student.courseId === courseId && !student.isChecked)
        .map((student: StudentDetails) => student.email);

    return studentsToHide;
  }

  /*------------------------------Functions to formulate data for student list from item------------------------*/
  /**
   * Function to formulate data for student list.
   */
  getStudentListSectionDataForCourse(courseId: string): StudentListSectionData[] {
    const data: StudentListSectionData[] | undefined = this.courseStudentListSectionDataMap[courseId];
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
      isAllowedToModifyStudent: sectionDetails.isAllowedToEditStudents,
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
