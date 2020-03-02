import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import { Course, Gender, Instructor, Instructors, JoinState, Student, StudentProfile,
  Students } from '../../../types/api-output';
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
  Gender: typeof Gender = Gender; // enum

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

  constructor(private route: ActivatedRoute,
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
          this.statusMessageService.showErrorMessage(resp.error.message);
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
              `${environment.backendUrl}/webapi/student/profilePic?courseid=${courseId}&studentemail=${student.email}`;

                    const newTeammateProfile: StudentProfileWithPicture = {
                      studentProfile,
                      photoUrl : newPhotoUrl,
                    };

                    this.teammateProfiles.push(newTeammateProfile);
                  });
          });

        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Loads the instructors of the course.
   * @param courseid: id of the course queried
   */
  loadInstructors(courseId: string): void {
    this.instructorService.getInstructorsFromCourse(courseId)
        .subscribe((instructors: Instructors) => {
          this.instructorDetails = instructors.instructors;
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Sets the profile picture of a student as the default image
   */
  setDefaultPic(teammateProfile: StudentProfileWithPicture): void {
    teammateProfile.photoUrl = '/assets/images/profile_picture_default.png';
  }
}
