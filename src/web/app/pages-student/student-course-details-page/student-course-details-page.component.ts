import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Gender } from '../../../types/gender';
import { ErrorMessageOutput } from '../../error-message-output';

interface StudentAttributes {
  email: string;
  course: string;
  name: string;
  googleId: string;
  team: string;
  section: string;
}

interface InstructorDetails {
  name: string;
  email: string;
}

interface CourseAttributes {
  createdAt: string;
  id: string;
  name: string;
  timeZone: string;
}

interface TeammateProfile {
  shortName: string;
  email: string;
  institute: string;
  nationality: string;
  gender: Gender;
  pictureKey: string;
}

interface StudentCourseDetails {
  student: StudentAttributes;
  course: CourseAttributes;
  instructorDetails: InstructorDetails[];
  teammateProfiles?: TeammateProfile[];
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
  student?: StudentAttributes;
  course?: CourseAttributes;
  instructorDetails?: InstructorDetails[];
  teammateProfiles?: TeammateProfile[];

  private backendUrl: string = environment.backendUrl;

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadStudentDetails(queryParams.courseid, this.user);
    });
  }

  /**
   * Fetch the data to be displayed on the page.
   * @param courseid: id of the course queried
   * @param user: only used in admin masquerade mode, value should be student id
   */
  loadStudentDetails(courseid: string, user: string): void {
    const paramMap: { [key: string]: string } = { courseid, user };
    this.httpRequestService.get('/student/course', paramMap).subscribe((resp: StudentCourseDetails) => {
      this.student = resp.student;
      this.instructorDetails = resp.instructorDetails;
      this.course = resp.course;
      this.teammateProfiles = resp.teammateProfiles;

      if (!this.student) {
        this.statusMessageService.showErrorMessage('Error retrieving student details');
      }

      if (!this.course) {
        this.statusMessageService.showErrorMessage('Error retrieving course details');
      }

      if (!resp.instructorDetails) {
        this.statusMessageService.showErrorMessage('Error retrieving instructor details');
      }

      if (!this.teammateProfiles) {
        this.statusMessageService.showWarningMessage('You do not have any teammates yet.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Construct the url for the profile picture from the given key.
   */
  getPictureUrl(pictureKey: string): string {
    if (!pictureKey) {
      return '/assets/images/profile_picture_default.png';
    }
    return `${this.backendUrl}/webapi/students/profilePic?blob-key=${pictureKey}`;
  }

}
