import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  Course, Gender, Instructor, Instructors, JoinState, Student, StudentProfile,
  Students
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * A student profile which also has the profile picture URL
 */
export interface StudentProfileWithPicture {
  studentProfile: StudentProfile;
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
  teammateProfilesSortBy: SortBy = SortBy.STUDENT_NAME;
  courseId: any;
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

  instructorDetails: Instructor[] = [];
  teammateProfiles: StudentProfileWithPicture[] = [];
  teammateProfilesInit: StudentProfileWithPicture[] = [];

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
    this.courseService.getCourseAsStudent(courseId).subscribe((course: Course) => {
      this.course = course;
    });
  }

  /**
   * Loads the current logged-in student of the course.
   * @param courseid: id of the course queried
   */
  loadStudent(courseId: string): void {
    this.studentService.getStudent(courseId)
      .subscribe((student: Student) => {
        this.student = student;
        this.loadTeammates(courseId, student.teamName);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  /**
   * Loads the teammates of the current student.
   * @param courseid: id of the course queried
   * @param teamName: team of current student
   */
  loadTeammates(courseId: string, teamName: string): void {
    this.studentService.getStudentsFromCourseAndTeam(courseId, teamName)
      .subscribe((students: Students) => {
        students.students.forEach((student: Student) => {
          // filter away current user
          if (student.email === this.student.email) {
            return;
          }

          this.studentProfileService.getStudentProfile(student.email, courseId)
            .subscribe((studentProfile: StudentProfile) => {
              const newPhotoUrl: string =
                `${environment.backendUrl}/webapi/student/profilePic`
                + `?courseid=${courseId}&studentemail=${student.email}`;

              const newTeammateProfile: StudentProfileWithPicture = {
                studentProfile: {
                  ...studentProfile,
                  email: student.email,
                  shortName: student.name,
                },
                photoUrl: newPhotoUrl,
              };

              this.teammateProfiles.push(newTeammateProfile);
            });
        });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  /**
   * Loads the instructors of the course.
   * @param courseid: id of the course queried
   */
  loadInstructors(courseId: string): void {
    this.instructorService.loadInstructors({ courseId })
      .subscribe((instructors: Instructors) => {
        this.instructorDetails = instructors.instructors;
      }, (resp: ErrorMessageOutput) => {
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
    ((a: { studentProfile: StudentProfile }, b: { studentProfile: StudentProfile }) => number) {
    return ((a: { studentProfile: StudentProfile }, b: { studentProfile: StudentProfile }): number => {
      let strA: string;
      let strB: string;
      switch (sortOption) {
        case SortBy.STUDENT_NAME:
          strA = a.studentProfile.shortName;
          strB = b.studentProfile.shortName;
          break;
        case SortBy.EMAIL:
          strA = a.studentProfile.email;
          strB = b.studentProfile.email;
          break;
        case SortBy.STUDENT_GENDER:
          strA = a.studentProfile.gender;
          strB = b.studentProfile.gender;
          break;
        case SortBy.INSTITUTION:
          strA = a.studentProfile.institute;
          strB = b.studentProfile.institute;
          break;
        case SortBy.NATIONALITY:
          strA = a.studentProfile.nationality;
          strB = b.studentProfile.nationality;
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(sortOption, SortOrder.ASC, strA, strB);
    });
  }

  /**
 * Search Profiles if the value of the search bar is included
 * in the name or the email of the profile
 */
  searchTeammates(): void {

    let inputValue = (<HTMLInputElement>document.getElementById("inputValue")).value;
    if (!inputValue) {
      this.teammateProfiles = this.teammateProfilesInit;
    }
    else {
      let temp: StudentProfileWithPicture[] = [];
      this.teammateProfilesInit.forEach(student => {
        if (student.studentProfile.name.toLocaleLowerCase().includes(inputValue.toLocaleLowerCase()) ||
          student.studentProfile.email.toLocaleLowerCase().includes(inputValue.toLocaleLowerCase())) {
          temp.push(student);
        }
      });
      this.teammateProfiles = temp;
    }
  }

}
