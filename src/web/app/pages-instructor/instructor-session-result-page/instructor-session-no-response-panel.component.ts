import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin } from 'rxjs';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  FeedbackSession, FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  FeedbackSessionSubmittedGiverSet,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student, Students,
} from '../../../types/api-output';
import {
  SendRemindersToStudentModalComponent,
} from '../../components/sessions-table/send-reminders-to-student-modal/send-reminders-to-student-modal.component';
import {
  StudentListInfoTableRowModel,
} from '../../components/sessions-table/student-list-info-table/student-list-info-table-model';
import {
    ErrorMessageOutput,
} from '../../error-message-output';

/**
 * Instructor sessions results page No Response Panel.
 */
@Component({
  selector: 'tm-instructor-session-no-response-panel',
  templateUrl: './instructor-session-no-response-panel.component.html',
  styleUrls: ['./instructor-session-no-response-panel.component.scss'],
})
export class InstructorSessionNoResponsePanelComponent implements OnInit, OnChanges {

  @Input() noResponseStudents: Student[] = [];
  @Input() section: string = '';
  @Input() session: FeedbackSession = {
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };
  isTabExpanded: boolean = false;

  noResponseStudentsInSection: Student[] = [];

  constructor(
    private modalService: NgbModal,
    private feedbackSessionsService: FeedbackSessionsService,
    private statusMessageService: StatusMessageService,
    private studentService: StudentService) { }

  ngOnInit(): void {
    this.filterStudentsBySection();
  }

  ngOnChanges(): void {
    this.filterStudentsBySection();
  }

  private filterStudentsBySection(): void {
    if (this.section) {
      this.noResponseStudentsInSection =
          this.noResponseStudents.filter((student: Student) => student.sectionName === this.section);
    } else {
      this.noResponseStudentsInSection = this.noResponseStudents;
    }
  }

  openSendReminderModal(event: any): void {
    event.stopPropagation();

    const courseId: string = this.session.courseId;
    const feedbackSessionName: string = this.session.feedbackSessionName;

    const modalRef: NgbModalRef = this.modalService.open(SendRemindersToStudentModalComponent);
    modalRef.componentInstance.courseId = courseId;
    modalRef.componentInstance.feedbackSessionName = feedbackSessionName;

    forkJoin(
        this.studentService.getStudentsFromCourse({ courseId }),
        this.feedbackSessionsService.getFeedbackSessionSubmittedGiverSet({ courseId, feedbackSessionName }))
        .subscribe(
            (result: any[]) => {
              const students: Student[] = (result[0] as Students).students;
              const giverSet: Set<string> = new Set((result[1] as FeedbackSessionSubmittedGiverSet).giverIdentifiers);
              modalRef.componentInstance.studentListInfoTableRowModels
                = students.map((student: Student) => ({
                  email: student.email,
                  name: student.name,
                  teamName: student.teamName,
                  sectionName: student.sectionName,

                  hasSubmittedSession: giverSet.has(student.email),
                  isSelected: true,
                } as StudentListInfoTableRowModel));

              modalRef.result.then((studentsToRemind: StudentListInfoTableRowModel[]) => {
                this.feedbackSessionsService
                      .remindFeedbackSessionSubmissionForStudent(courseId, feedbackSessionName, {
                        usersToRemind: studentsToRemind.map((m: StudentListInfoTableRowModel) => m.email),
                      }).subscribe(() => {
                        this.statusMessageService.showSuccessMessage(
                          'Reminder e-mails have been sent out to those students and instructors. '
                          + 'Please allow up to 1 hour for all the notification emails to be sent out.');

                      }, (resp: ErrorMessageOutput) => {
                        this.statusMessageService.showErrorMessage(resp.error.message);
                      });
              }, () => {});

            }, (resp: ErrorMessageOutput) => { this.statusMessageService.showErrorMessage(resp.error.message); },
        );
  }

  /**
   * Toggles the tab of the no response panel.
   */
  toggleTab(): void {
    this.isTabExpanded = !this.isTabExpanded;
  }

  /**
   * Expands the tab of the no response panel.
   */
  expandTab(): void {
    this.isTabExpanded = true;
  }

  /**
   * Collapses the tab of the no response panel.
   */
  collapseTab(): void {
    this.isTabExpanded = false;
  }

}
