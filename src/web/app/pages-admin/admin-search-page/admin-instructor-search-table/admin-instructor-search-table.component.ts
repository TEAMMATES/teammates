import { KeyValuePipe, NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { FeedbackSessionsGroup, InstructorAccountSearchResult } from '../../../../services/search.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { UserService } from '../../../../services/user.service';
import { RegenerateKey } from '../../../../types/api-output';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../../error-message-output';
import { SearchTermsHighlighterPipe } from '../../../pipes/search-terms-highlighter.pipe';

@Component({
  selector: 'tm-admin-instructor-search-table',
  templateUrl: './admin-instructor-search-table.component.html',
  imports: [NgClass, NgbTooltip, NgbCollapse, AjaxLoadingComponent, KeyValuePipe, SearchTermsHighlighterPipe],
})
export class AdminInstructorSearchTableComponent implements OnChanges {
  private statusMessageService = inject(StatusMessageService);
  private simpleModalService = inject(SimpleModalService);
  private userService = inject(UserService);

  @Input()
  instructors: InstructorAccountSearchResult[] = [];

  @Input()
  searchString = '';

  @Output()
  instructorReset = new EventEmitter<void>();

  isRegeneratingInstructorKeys: boolean[] = [];

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

  regenerateUserKey(instructor: InstructorAccountSearchResult, index: number): void {
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
        this.userService.regenerateUserKey(instructor.userId).subscribe({
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
