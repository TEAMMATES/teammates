import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { StudentService } from '../../../services/student.service';
import { Course, Instructors, Student, Students } from '../../../types/api-output';
import { Gender } from '../../../types/gender';
import { ErrorMessageOutput } from '../../error-message-output';
import { Intent } from '../../Intent';
import { StudentProfile } from '../../pages-instructor/student-profile/student-profile';

interface StudentProfileWithPicture extends StudentProfile {
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
  user: string = '';
  student?: Student;
  course?: Course;
  instructorDetails?: Instructors;
  teammateProfiles: StudentProfileWithPicture[] = [];

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private studentProfileService: StudentProfileService,
              private studentService: StudentService,
              private courseService: CourseService,
              private statusMessageService: StatusMessageService) { }

  /**
   * Fetches relevant data to be displayed on page.
   */
  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadStudent(queryParams.courseid);
      this.loadCourse(queryParams.courseid);
      this.loadInstructors(queryParams.courseid);
    });
  }

  /**
   * Loads the course details.
   * @param courseid: id of the course queried
   */
  loadCourse(courseid: string): void {
    this.courseService.getCourse(courseid).subscribe((course: Course) => {
      this.course = course;
    });
  }

  /**
   * Loads the students of the course.
   * @param courseid: id of the course queried
   */
  loadStudent(courseid: string): void {
    const paramMap: { [key: string]: string } = {
      courseid,
    };

    this.httpRequestService.get('/student', paramMap)
        .subscribe((student: Student) => {
          this.student = student;
          this.loadTeammates(courseid, student.teamName);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Loads the teammates of the current student.
   * @param courseid: id of the course queried
   * @param teamName: team of current student
   */
  loadTeammates(courseid: string, teamName: string): void {
    this.studentService.getStudentsFromCourseAndTeam(courseid, teamName)
        .subscribe((students: Students) => {
          students.students.forEach((student: Student) => {
            this.studentProfileService.getStudentProfile(student.email, courseid)
                  .subscribe((studentProfile: StudentProfile) => {
                    const newPhotoUrl: string =
              `${environment.backendUrl}/webapi/student/profilePic?courseid=${courseid}&studentemail=${student.email}`;

                    const newTeammateProfile: StudentProfileWithPicture = {
                      shortName: studentProfile.shortName,
                      email: studentProfile.email,
                      institute: studentProfile.institute,
                      nationality: studentProfile.institute,
                      gender: studentProfile.gender,
                      moreInfo: studentProfile.moreInfo,
                      pictureKey: studentProfile.pictureKey,
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
  loadInstructors(courseid: string): void {
    const paramMap: { [key: string]: string } = {
      courseid,
      intent: Intent.FULL_DETAIL,
    };

    this.httpRequestService.get('/instructors', paramMap)
        .subscribe((instructors: Instructors) => {
          this.instructorDetails = instructors;
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
