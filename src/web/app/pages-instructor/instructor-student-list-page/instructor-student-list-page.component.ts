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
}

interface StudentDetails {
  name: string;
  email: string;
  status: string;
}

interface TeamDetails {
  name: string;
  students: StudentDetails[];
}

interface SectionDetails {
  name: string;
  teams: TeamDetails[];
  isAllowedToViewStudents: boolean;
  isAllowedtoEditStudents: boolean;
}

interface CourseDetails {
  id: string;
  name: string;
  createdAt: string;
  sections: SectionDetails[];
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
    this.courses.forEach((course: Course) => {
      if (course !== undefined) {
        this.toggleCourseStateAtInput(course, defaultState);
      }
    });
  }

  /**
   * Function to change states of all sections.
   */
  toggleAllSections(defaultState: boolean): void {
    this.courses.forEach((course: Course) => {
      if (course !== undefined) {
        const courseDetails: CourseDetails | undefined = this.courseDetailsMap.get(course.id);
        if (courseDetails !== undefined) {
          courseDetails.sections.forEach((section: SectionDetails) => {
            this.toggleSectionState(courseDetails.id, section, defaultState);
          });
        }
      }
    });
  }

  /**
   * Function to change states of all teams.
   */
  toggleAllTeams(defaultState: boolean): void {
    this.courses.forEach((course: Course) => {
      if (course !== undefined) {
        const courseDetails: CourseDetails | undefined = this.courseDetailsMap.get(course.id);
        if (courseDetails !== undefined) {
          courseDetails.sections.forEach((section: SectionDetails) => {
            section.teams.forEach((team: TeamDetails) => {
              this.toggleTeamState(courseDetails.id, section.name, team, defaultState);
            });
          });
        }
      }
    });
  }

  /**
   * Function to change states of all students.
   */
  toggleAllStudents(): void {
    this.courses.forEach((course: Course) => {
      if (course !== undefined) {
        const courseDetails: CourseDetails | undefined = this.courseDetailsMap.get(course.id);
        if (courseDetails !== undefined) {
          courseDetails.sections.forEach((section: SectionDetails) => {
            section.teams.forEach((team: TeamDetails) => {
              team.students.forEach((student: StudentDetails) => {
                this.toggleStudentState(student);
              });
            });
          });
        }
      }
    });
  }

  /**
   * Function to trigger getting CourseDetails data for a course from backend.
   */
  toggleCourseStateAtInput(course: Course, designatedState?: boolean): void {
    let recordedState: boolean = this.coursesStateMap.get(course.id);
    if (recordedState === null || recordedState === undefined) {
      recordedState = true;
    }
    const state: boolean = (designatedState === null || designatedState === undefined)
        ? !recordedState
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
    if (this.courseDetailsMap.has(courseid)) {
      return of(this.courseDetailsMap.get(courseid));
    }

    const paramMap: { [courseid: string ]: string } = { courseid };
    return this.httpRequestService.get('/instructor/students', paramMap)
        .pipe(map((resp: GetCourseDetailsResponse | null) => {
          if (resp !== null) {
            if (resp.course !== null) {
              this.courseDetailsMap.set(resp.course.id, resp.course);
              return this.courseDetailsMap.get(resp.course.id);
            }
          }

          this.statusMessageService.showErrorMessage(`Error retrieving course details for course of id ${courseid}`);
          return undefined;
        }));
  }

  /*------------------------------Functions to toggle item state in state maps----------------------------------*/

  /**
   * Function to change the state of a specific course.
   */
  toggleCourseState(course: CourseDetails, designatedState?: boolean): void {
    const key: string = this.getCoursesStateMapKey(course);

    let recordedState: boolean = this.coursesStateMap.get(key);
    if (recordedState === null || recordedState === undefined) {
      recordedState = true;
    }
    const state: boolean = (designatedState === null || designatedState === undefined)
        ? !recordedState
        : designatedState;

    this.coursesStateMap.set(key, state);

    course.sections.forEach((section: SectionDetails) => {
      this.toggleSectionState(course.id, section, state);
    });
  }

  /**
   * Function to change the state of a specific section.
   */
  toggleSectionState(courseId: string, section: SectionDetails, designatedState?: boolean): void {
    const key: string = this.getSectionsStateMapKey(courseId, section);

    let recordedState: boolean = this.sectionsStateMap.get(key);
    if (recordedState === null || recordedState === undefined) {
      recordedState = true;
    }
    const state: boolean = (designatedState === null || designatedState === undefined)
        ? !recordedState
        : designatedState;

    this.sectionsStateMap.set(key, state);

    section.teams.forEach((team: TeamDetails) => {
      this.toggleTeamState(courseId, section.name, team, state);
    });
  }

  /**
   * Function to change the state of a specific team.
   */
  toggleTeamState(courseId: string, sectionName: string, team: TeamDetails, designatedState?: boolean): void {
    const key: string = this.getTeamsStateMapKey(courseId, sectionName, team);

    let recordedState: boolean = this.teamsStateMap.get(key);
    if (recordedState === null || recordedState === undefined) {
      recordedState = true;
    }
    const state: boolean = (designatedState === null || designatedState === undefined)
        ? !recordedState
        : designatedState;

    this.teamsStateMap.set(key, state);

    team.students.forEach((student: StudentDetails) => {
      this.toggleStudentState(student, state);
    });
  }

  /**
   * Function to change the state of a specific student.
   */
  toggleStudentState(student: StudentDetails, designatedState?: boolean): void {
    const key: string = this.getStudentsStateMapKey(student);

    let recordedState: boolean = this.studentsStateMap.get(key);
    if (recordedState === null || recordedState === undefined) {
      recordedState = true;
    }
    const state: boolean = (designatedState === null || designatedState === undefined)
        ? !recordedState
        : designatedState;

    this.studentsStateMap.set(key, state);
  }

  /*------------------------------Functions to get item state from state maps-----------------------------------*/

  /**
   * Function to get all the states of courses.
   */
  getAllCoursesState(): boolean {
    const state: boolean = this.getAllStatesOf('Course');
    this.statusMessageService.showWarningMessage(`allStatesOfCourse: ${typeof state}`);
    if (typeof state === 'object') {
      this.statusMessageService.showErrorMessage(state.value);
    }

    return state;
  }

  /**
   * Function to get all the states of sections.
   */
  getAllSectionsState(): boolean {
    return this.getAllStatesOf('SectionDetails');
  }

  /**
   * Function to get all the states of teams.
   */
  getAllTeamsState(): boolean {
    return this.getAllStatesOf('TeamDetails');
  }

  /**
   * Function to get all the states of students.
   */
  getAllStudentsState(): boolean {
    return this.getAllStatesOf('StudentDetails');
  }

  /**
   * Function to get all the states of a specific type of items.
   */
  getAllStatesOf(itemType: string): boolean {
    let state: boolean = false;
    let mapOfAllStates: Map<string, any> | undefined;
    switch (itemType) {
      case 'Course':
      case 'CourseDetails':
        mapOfAllStates = this.courseDetailsMap;
        break;
      case 'SectionDetails':
        mapOfAllStates = this.sectionsStateMap;
        break;
      case 'TeamDetails':
        mapOfAllStates = this.teamsStateMap;
        break;
      case 'StudentDetails':
        mapOfAllStates = this.studentsStateMap;
        break;
      default:
        mapOfAllStates = undefined;
    }

    if (mapOfAllStates !== undefined) {
      if (mapOfAllStates.size === 0) {
        return true;
      }
      mapOfAllStates.forEach((value: boolean) => {
        state = value || state;
      });
    }

    if (state === true) {
      return true;
    }

    return false;
  }

  /**
   * Function to get the state of a specific course.
   */
  getCourseState(course: CourseDetails): boolean {
    const state: boolean | undefined = this.coursesStateMap.get(this.getCoursesStateMapKey(course));
    return state === undefined ? true : state;
  }

  /**
   * Function to get the state of a specific section.
   */
  getSectionState(courseId: string, section: SectionDetails): boolean {
    const state: boolean | undefined = this.sectionsStateMap.get(this.getSectionsStateMapKey(courseId, section));
    return state === undefined ? true : state;
  }

  /**
   * Function to get the state of a specific team.
   */
  getTeamState(courseId: string, sectionName: string, team: TeamDetails): boolean {
    const state: boolean | undefined = this.teamsStateMap.get(this.getTeamsStateMapKey(courseId, sectionName, team));
    return state === undefined ? true : state;
  }

  /**
   * Function to get the state of a specific student.
   */
  getStudentState(student: StudentDetails): boolean {
    const state: boolean | undefined = this.studentsStateMap.get(this.getStudentsStateMapKey(student));
    return state === undefined ? true : state;
  }

  /**
   * Function to get the list of students which need to be hidden on the StudentTable display.
   */
  getStudentsToHide(): string[] {
    const studentsToHide: string[] = [];

    this.studentsStateMap.forEach((state: boolean, key: string): void => {
      if (state) {
        studentsToHide.push(key);
      }
    });

    return studentsToHide;
  }
  /*------------------------------Functions to formulate state map keys from item-------------------------------*/
  /**
   * Function to formulate state map keys from CourseDetails.
   */
  getCoursesStateMapKey(course: CourseDetails): string {
    return course.id;
  }

  /**
   * Function to formulate state map keys from SectionDetails.
   */
  getSectionsStateMapKey(courseId: string, section: SectionDetails): string {
    return `${courseId}:${section.name}`;
  }

  /**
   * Function to formulate state map keys from TeamDetails.
   */
  getTeamsStateMapKey(courseId: string, sectionName: string, team: TeamDetails): string {
    return `${courseId}:${sectionName}:${team.name}`;
  }

  /**
   * Function to formulate state map keys from StudentDetails.
   */
  getStudentsStateMapKey(student: StudentDetails): string {
    return `${student.email}`;
  }

  /*------------------------------Functions to formulate data for student list from item------------------------*/
  /**
   * Function to formulate data for student list.
   */
  getStudentListSectionDataForCourse(courseId: string): StudentListSectionData[] {
    const data: StudentListSectionData[] = this.courseStudentListSectionDataMap.get(courseId);
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
        const studentData: StudentListStudentData = this.mapStudentForStudentList(student, team.name);
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
  mapStudentForStudentList(studentDetails: StudentDetails, teamName: string): StudentListStudentData {
    const student: StudentListStudentData = {
      name: studentDetails.name,
      team: teamName,
      email: studentDetails.email,
      status: studentDetails.status,
    };

    return student;
  }
}
