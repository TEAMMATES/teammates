import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { FeedbackSessionsService } from "../../../services/feedback-sessions.service";
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentProfileService } from "../../../services/student-profile.service";
import { StudentProfile, FeedbackSessions, FeedbackSession } from "../../../types/api-output";
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
  studentName: string = '';
  studentEmail: string = '';
  studentProfile?: StudentProfile ;
  sessions: Session[] = [];
  photoUrl: string = '';

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private studentProfileService: StudentProfileService,
              private feedbackSessionsService: FeedbackSessionsService,) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      const courseId: string = queryParams.courseid;
      const studentEmail: string = queryParams.studentemail;

      this.loadStudentRecords(courseId, studentEmail);
      this.photoUrl
          = `${environment.backendUrl}/webapi/student/profilePic?courseid=${courseId}&studentemail=${studentEmail}`;
    });
  }

  /**
   * Loads the student's records based on the given course ID and email.
   */
  loadStudentRecords(courseid: string, studentemail: string): void {
    this.studentProfileService.getStudentProfile(studentemail, courseid).subscribe((resp: StudentProfile) => {
      this.courseId = courseid;
      this.studentName = resp.name;
      this.studentEmail = resp.email;
      this.studentProfile = resp;
      if (!this.studentProfile) {
        this.statusMessageService.showWarningMessage(
            'Normally, we would show the student\'s profile here. '
            + 'However, either this student has not created a profile yet, '
            + 'or you do not have access to view this student\'s profile.');
      }
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });

    this.feedbackSessionsService.getFeedbackSessionsForInstructor(courseid).subscribe((fss: FeedbackSessions) => {
      this.sessions = fss.feedbackSessions.map((fs: FeedbackSession) => ({ name: fs.feedbackSessionName,
        isCollapsed: false }));
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });

  }
}
