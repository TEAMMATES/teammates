import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import { Course, Gender, Instructor, Instructors, JoinState, Student, StudentProfile,
  Students } from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * A student profile which also has the profile picture URL
 */
export interface StudentProfileWithPicture extends StudentProfile {
  photoUrl: string;
}

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
  Gender: typeof Gender = Gender;
  SortBy: typeof SortBy = SortBy;
  teammateProfilesSortBy: SortBy = SortBy.NONE;

  // data
  student: Student = {
    email: '',
    courseId: '',
    name: '',
    lastName: '',
    comments: '',
    joinState: JoinState.NOT_JOINED,
    teamName: '',
    sectionName: '',
  };

  course: Course = {
    courseId: '',
    courseName: '',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 0,
  };

  courseId: string = '';
  instructorDetails: Instructor[] = [];
  teammateProfiles: StudentProfileWithPicture[] = [];

  isLoadingCourse: boolean = false;
  isLoadingStudent: boolean = false;
  isLoadingInstructor: boolean = false;
  isLoadingTeammates: boolean = false;
  hasLoadingFailed: boolean = false;

  constructor(private tableComparatorService: TableComparatorService,
              private route: ActivatedRoute,
              private instructorService: InstructorService,
              private studentProfileService: StudentProfileService,
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
   * @param courseid: id of the course queried
   */
  loadCourse(courseId: string): void {
    this.isLoadingCourse = true;
    this.courseService.getCourseAsStudent(courseId)
        .pipe(finalize(() => this.isLoadingCourse = false))
        .subscribe((course: Course) => {
          this.course = course;
        }, (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Loads the current logged-in student of the course.
   * @param courseid: id of the course queried
   */
  loadStudent(courseId: string): void {
    this.isLoadingStudent = true;
    this.studentService.getStudent(courseId)
        .pipe(finalize(() => this.isLoadingStudent = false))
        .subscribe((student: Student) => {
          this.student = student;
          this.loadTeammates(courseId, student.teamName);
        }, (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Loads the teammates of the current student.
   * @param courseid: id of the course queried
   * @param teamName: team of current student
   */
  loadTeammates(courseId: string, teamName: string): void {
    this.isLoadingTeammates = true;
    this.teammateProfiles = [];
    this.studentService.getStudentsFromCourseAndTeam(courseId, teamName)
      .subscribe((students: Students) => {
        // No teammates
        if (students.students.length === 1 && students.students[0].email === this.student.email) {
          this.isLoadingTeammates = false;
        }
        students.students.forEach((student: Student) => {
          // filter away current user
          if (student.email === this.student.email) {
            return;
          }

          this.studentProfileService.getStudentProfile(student.email, courseId)
                .pipe(finalize(() => this.isLoadingTeammates = false))
                .subscribe((studentProfile: StudentProfile) => {
                  const newPhotoUrl: string =
                    `${environment.backendUrl}/webapi/student/profilePic`
                    + `?courseid=${courseId}&studentemail=${student.email}`;

                  const newTeammateProfile: StudentProfileWithPicture = {
                    ...studentProfile,
                    email: student.email,
                    name: student.name,
                    photoUrl : newPhotoUrl,
                  };

                  this.teammateProfiles.push(newTeammateProfile);
                }, (resp: ErrorMessageOutput) => {
                  this.hasLoadingFailed = true;
                  this.statusMessageService.showErrorToast(resp.error.message);
                });
        });
      }, (resp: ErrorMessageOutput) => {
        this.isLoadingTeammates = false;
        this.hasLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  /**
   * Loads the instructors of the course.
   * @param courseid: id of the course queried
   */
  loadInstructors(courseId: string): void {
    this.isLoadingInstructor = true;
    this.instructorService.loadInstructors({ courseId })
        .pipe(finalize(() => this.isLoadingInstructor = false))
        .subscribe((instructors: Instructors) => {
          this.instructorDetails = instructors.instructors;
        }, (resp: ErrorMessageOutput) => {
          this.hasLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        });
  }

  /**
   * Sets the profile picture of a student as the default image
   */
  setDefaultPic(teammateProfile: StudentProfileWithPicture): void {
    teammateProfile.photoUrl = '/assets/images/profile_picture_default.png';
  }

  /**
   * Checks the option selected to sort teammates.
   * @param sortOption: option for sorting
   */
  isSelectedForSorting(sortOption: SortBy): boolean {
    return this.teammateProfilesSortBy === sortOption;
  }

  /**
   * Sorts the teammates according to selected option.
   * @param sortOption: option for sorting
   */
  sortTeammatesBy(sortOption: SortBy): void {
    this.teammateProfilesSortBy = sortOption;

    if (this.teammateProfiles.length > 1) {
      this.teammateProfiles.sort(this.sortPanelsBy(sortOption));
    }
  }

  /**
   * Sorts the panels of teammates in order.
   * @param sortOption: option for sorting
   */
  sortPanelsBy(sortOption: SortBy):
      ((a: StudentProfile, b: StudentProfile) => number) {
    return ((a: StudentProfile, b: StudentProfile): number => {
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
        case SortBy.STUDENT_GENDER:
          strA = a.gender;
          strB = b.gender;
          break;
        case SortBy.INSTITUTION:
          strA = a.institute;
          strB = b.institute;
          break;
        case SortBy.NATIONALITY:
          strA = a.nationality;
          strB = b.nationality;
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
