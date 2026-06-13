import { NgClass } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { StudentAccountSearchResult } from '../../../../services/search.service';
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
  selector: 'tm-admin-student-search-table',
  templateUrl: './admin-student-search-table.component.html',
  imports: [NgClass, NgbTooltip, AjaxLoadingComponent, SearchTermsHighlighterPipe],
})
export class AdminStudentSearchTableComponent implements OnChanges {
  private statusMessageService = inject(StatusMessageService);
  private simpleModalService = inject(SimpleModalService);
  private userService = inject(UserService);
  private ngbModal = inject(NgbModal);

  @Input()
  students: StudentAccountSearchResult[] = [];

  @Input()
  searchString = '';

  @Output()
  studentReset = new EventEmitter<void>();

  isRegeneratingStudentKeys: boolean[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['students']) {
      this.isRegeneratingStudentKeys = new Array(this.students.length).fill(false);
    }
  }

  openSessionLinksModal(student: StudentAccountSearchResult): void {
    const modalRef: NgbModalRef = this.ngbModal.open(AdminSessionLinksModalComponent, { size: 'xl' });
    modalRef.componentInstance.userId = student.userId;
    modalRef.componentInstance.userName = student.name;
  }

  regenerateUserKey(student: StudentAccountSearchResult, index: number): void {
    this.isRegeneratingStudentKeys[index] = true;
    const modalContent = `Are you sure you want to regenerate the registration key for
        <strong>${student.name}</strong> for the course <strong>${student.courseId}</strong>?
        An email will be sent to the student with all the new course registration and feedback session links.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Regenerate <strong>${student.name}</strong>'s course links?`,
      SimpleModalType.WARNING,
      modalContent,
    );

    modalRef.dismissed.subscribe(() => {
      this.isRegeneratingStudentKeys[index] = false;
    });

    modalRef.result.then(
      () => {
        this.userService.regenerateUserKey(student.userId).subscribe({
          next: (resp: RegenerateKey) => {
            this.statusMessageService.showSuccessToast(resp.message);
            this.isRegeneratingStudentKeys[index] = false;
          },
          error: (response: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(response.error.message);
            this.isRegeneratingStudentKeys[index] = false;
          },
        });
      },
      () => {},
    );
  }
}
