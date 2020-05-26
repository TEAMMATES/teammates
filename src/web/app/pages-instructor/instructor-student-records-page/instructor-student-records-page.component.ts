import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from '../../../services/student-profile.service';
import { Gender, StudentProfile } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

interface Session {
  name: string;
  isCollapsed: boolean;
}

/**
 * Instructor student records page.
 */
@Component({
  selector: 'tm-instructor-student-records-page',
  templateUrl: './instructor-student-records-page.component.html',
  styleUrls: ['./instructor-student-records-page.component.scss'],
})
export class InstructorStudentRecordsPageComponent implements OnInit {

  courseId: string = '';
  studentEmail: string = '';

  studentProfile: StudentProfile = {
    name: '',
    shortName: '',
    email: '',
    institute: '',
    nationality: '',
    gender: Gender.OTHER,
    moreInfo: '',
  };
  sessions: Session[] = [];
  photoUrl: string = '';

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private studentProfileService: StudentProfileService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.studentEmail = queryParams.studentemail;

      this.loadStudentRecords();
      this.photoUrl
          = `${environment.backendUrl}/webapi/student/profilePic?`
            + `courseid=${this.courseId}&studentemail=${this.studentEmail}`;
    });
  }

  /**
   * Loads the student's records based on the given course ID and email.
   */
  loadStudentRecords(): void {
    this.studentProfileService.getStudentProfile(this.studentEmail, this.courseId).subscribe((resp: StudentProfile) => {
      this.studentProfile = resp;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }
}
