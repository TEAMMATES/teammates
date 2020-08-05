import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { TableComparatorService } from '../../../services/table-comparator.service';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
  Student,
} from '../../../types/api-output';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { StudentListInfoTableRowModel } from '../../components/sessions-table/respondent-list-info-table/respondent-list-info-table-model';
import { SendRemindersToRespondentsModalComponent } from '../../components/sessions-table/send-reminders-to-respondents-modal/send-reminders-to-respondents-modal.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';

/**
 * Instructor sessions results page No Response Panel.
 */
@Component({
  selector: 'tm-instructor-session-no-response-panel',
  templateUrl: './instructor-session-no-response-panel.component.html',
  styleUrls: ['./instructor-session-no-response-panel.component.scss'],
  animations: [collapseAnim],
})
export class InstructorSessionNoResponsePanelComponent implements OnInit, OnChanges {

  // enum
  FeedbackSessionSubmissionStatus: typeof FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus;
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  @Input() isDisplayOnly: boolean = false;
  @Input() allStudents: Student[] = [];
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

  sortBy: SortBy = SortBy.NONE;
  sortOrder: SortOrder = SortOrder.ASC;

  noResponseStudentsInSection: Student[] = [];

  @Output() studentsToRemindEvent: EventEmitter<StudentListInfoTableRowModel[]> = new EventEmitter();

  constructor(private ngbModal: NgbModal,
              private tableComparatorService: TableComparatorService) { }

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

    const nonResponseStudentEmails: string[] = this.noResponseStudents.map((student: Student) => student.email);
    const nonResponseStudentEmailSet: Set<string> = new Set(nonResponseStudentEmails);

    const modalRef: NgbModalRef = this.ngbModal.open(SendRemindersToRespondentsModalComponent);
    modalRef.componentInstance.courseId = courseId;
    modalRef.componentInstance.feedbackSessionName = feedbackSessionName;
    modalRef.componentInstance.studentListInfoTableRowModels
      = this.allStudents.map((student: Student) => ({
        email: student.email,
        name: student.name,
        teamName: student.teamName,
        sectionName: student.sectionName,

        hasSubmittedSession: !nonResponseStudentEmailSet.has(student.email),
        isSelected: nonResponseStudentEmailSet.has(student.email),
      } as StudentListInfoTableRowModel));

    modalRef.result.then((studentsToRemind: StudentListInfoTableRowModel[]) => {
      this.studentsToRemindEvent.emit(studentsToRemind);
    }, () => {});
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

  /**
   * Sorts the no response panel.
   */
  sortParticipantsBy(sortBy: SortBy): void {
    this.sortBy = sortBy;
    this.sortOrder = this.sortOrder === SortOrder.ASC ? SortOrder.DESC : SortOrder.ASC;

    this.noResponseStudentsInSection.sort((a: Student, b: Student) => {
      let strA: string;
      let strB: string;
      switch (this.sortBy) {
        case SortBy.TEAM_NAME:
          strA = a.teamName;
          strB = b.teamName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(this.sortBy, this.sortOrder, strA, strB);
    });
  }
}
