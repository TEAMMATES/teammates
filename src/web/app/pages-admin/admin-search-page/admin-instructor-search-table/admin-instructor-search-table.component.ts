import { KeyValuePipe, NgClass } from '@angular/common';
import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { NgbModalRef, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { AccountService } from '../../../../services/account.service';
import { InstructorService } from '../../../../services/instructor.service';
import { FeedbackSessionsGroup, InstructorAccountSearchResult } from '../../../../services/search.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { RegenerateKey } from '../../../../types/api-output';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../../error-message-output';
import { SearchTermsHighlighterPipe } from '../../../pipes/search-terms-highlighter.pipe';

@Component({
  selector: 'tm-admin-instructor-search-table',
  templateUrl: './admin-instructor-search-table.component.html',
  imports: [NgClass, NgbTooltip, AjaxLoadingComponent, KeyValuePipe, SearchTermsHighlighterPipe],
})
export class AdminInstructorSearchTableComponent implements OnChanges {
  @Input()
  instructors: InstructorAccountSearchResult[] = [];

  @Input()
  searchString = '';

  @Output()
  instructorReset = new EventEmitter<void>();

  isRegeneratingInstructorKeys: boolean[] = [];

  constructor(
    private statusMessageService: StatusMessageService,
    private simpleModalService: SimpleModalService,
    private accountService: AccountService,
    private instructorService: InstructorService,
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['instructors']) {
      this.isRegeneratingInstructorKeys = new Array(this.instructors.length).fill(false);
    }
  }

  showAllInstructorsLinks(): void {
    for (const instructor of this.instructors) {
      instructor.showLinks = true;
    }
  }

  hideAllInstructorsLinks(): void {
    for (const instructor of this.instructors) {
      instructor.showLinks = false;
    }
  }

  resetInstructorGoogleId(instructor: InstructorAccountSearchResult, event: any): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    const modalContent = `Are you sure you want to reset the Google account ID currently associated for
        <strong>${instructor.name}</strong> in the course <strong>${instructor.courseId}</strong>?
        The user will need to re-associate their account with a new Google ID.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Reset <strong>${instructor.name}</strong>'s Google ID?`,
      SimpleModalType.WARNING,
      modalContent,
    );

    modalRef.result.then(
      () => {
        this.accountService.resetInstructorAccount(instructor.courseId, instructor.email).subscribe({
          next: () => {
            this.instructorReset.emit();
            this.statusMessageService.showSuccessToast("The instructor's Google ID has been reset.");
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }

  regenerateInstructorKey(instructor: InstructorAccountSearchResult, index: number): void {
    this.isRegeneratingInstructorKeys[index] = true;
    const modalContent = `Are you sure you want to regenerate the registration key for
        <strong>${instructor.name}</strong> for the course <strong>${instructor.courseId}</strong>?
        An email will be sent to the instructor with all the new course registration and feedback session links.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Regenerate <strong>${instructor.name}</strong>'s course links?`,
      SimpleModalType.WARNING,
      modalContent,
    );

    modalRef.dismissed.subscribe(() => {
      this.isRegeneratingInstructorKeys[index] = false;
    });

    modalRef.result.then(
      () => {
        this.instructorService.regenerateInstructorKey(instructor.courseId, instructor.email).subscribe({
          next: (resp: RegenerateKey) => {
            this.statusMessageService.showSuccessToast(resp.message);
            this.updateDisplayedInstructorCourseLinks(instructor, resp.newRegistrationKey);
            this.isRegeneratingInstructorKeys[index] = false;
          },
          error: (response: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(response.error.message);
            this.isRegeneratingInstructorKeys[index] = false;
          },
        });
      },
      () => {},
    );
  }

  private updateDisplayedInstructorCourseLinks(instructor: InstructorAccountSearchResult, newKey: string): void {
    const updateSessions = (sessions: FeedbackSessionsGroup): void => {
      Object.keys(sessions).forEach((key: string): void => {
        sessions[key].feedbackSessionUrl = this.getUpdatedUrl(sessions[key].feedbackSessionUrl, newKey);
      });
    };

    instructor.courseJoinLink = this.getUpdatedUrl(instructor.courseJoinLink, newKey);
    updateSessions(instructor.awaitingSessions);
    updateSessions(instructor.openSessions);
    updateSessions(instructor.notOpenSessions);
    updateSessions(instructor.publishedSessions);
  }

  private getUpdatedUrl(link: string, newVal: string): string {
    const param = 'key';
    const regex = new RegExp(`(${param}=)[^&]+`);

    return link.replace(regex, `$1${newVal}`);
  }
}
