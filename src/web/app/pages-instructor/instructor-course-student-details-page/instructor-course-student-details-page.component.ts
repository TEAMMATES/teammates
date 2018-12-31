import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from '../../../services/http-request.service';
import { ErrorMessageOutput } from '../../message-output';
import { StatusMessageService } from 'src/web/services/status-message.service';
import { environment } from 'src/web/environments/environment';

interface StudentAttributes {
  email: string,
  course: string,
  name: string,
  googleId: string,
  lastName: string,
  comments: string,
  team: string,
  section: string,
  key: string
}

interface StudentProfile {
  googleId: string,
  shortName: string,
  email: string,
  institute: string,
  nationality: string,
  gender: string,
  moreInfo: string,
  pictureKey: string,
  modifiedDate: Date
}

interface StudentDetails {
  student: StudentAttributes,
  studentProfile: StudentProfile,
  hasSection: boolean
}

/**
 * Instructor course student details page.
 */
@Component({
  selector: 'tm-instructor-course-student-details-page',
  templateUrl: './instructor-course-student-details-page.component.html',
  styleUrls: ['./instructor-course-student-details-page.component.scss'],
})
export class InstructorCourseStudentDetailsPageComponent implements OnInit {

  private backendUrl: string = environment.backendUrl;

  user: string = '';
  student?: StudentAttributes;
  studentProfile?: StudentProfile;

  constructor(private route: ActivatedRoute, private httpRequestService: HttpRequestService,
    private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadStudentDetails(queryParams.courseid, queryParams.studentemail);
    });
  }

  loadStudentDetails(courseid: string, studentemail: string): void {
    const paramMap: { [key: string]: string } = { courseid, studentemail };
    this.httpRequestService.get('/courses/students/details', paramMap).subscribe((resp: StudentDetails) => {
      this.student = resp.student;
      this.studentProfile = resp.studentProfile;
      this.studentProfile.modifiedDate = new Date(this.studentProfile.modifiedDate);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  getPictureUrl(pictureKey: string): string {
    if (!pictureKey) {
        return "/assets/images/profile_picture_default.png";
    }
    return `${this.backendUrl}/students/profilePic?blob-key=${pictureKey}`;
  }

}
