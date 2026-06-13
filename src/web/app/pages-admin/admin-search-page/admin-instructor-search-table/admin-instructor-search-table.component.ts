import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { InstructorAccountSearchResult } from '../../../../services/search.service';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { UserService } from '../../../../services/user.service';
import { RegenerateKey } from '../../../../types/api-output';
import { AjaxLoadingComponent } from '../../../components/ajax-loading/ajax-loading.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../../error-message-output';
import { SearchTermsHighlighterPipe } from '../../../pipes/search-terms-highlighter.pipe';
import { AdminSessionLinksModalComponent } from '../admin-session-links-modal/admin-session-links-modal.component';

@Component({
  selector: 'tm-admin-instructor-search-table',
  templateUrl: './admin-instructor-search-table.component.html',
  imports: [NgClass, NgbTooltip, AjaxLoadingComponent, SearchTermsHighlighterPipe],
})
export class AdminInstructorSearchTableComponent implements OnChanges {
  private statusMessageService = inject(StatusMessageService);
  private simpleModalService = inject(SimpleModalService);
  private userService = inject(UserService);
  private ngbModal = inject(NgbModal);

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

  openSessionLinksModal(instructor: InstructorAccountSearchResult): void {
    const modalRef: NgbModalRef = this.ngbModal.open(AdminSessionLinksModalComponent, { size: 'xl' });
    modalRef.componentInstance.userId = instructor.userId;
    modalRef.componentInstance.userName = instructor.name;
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
}
