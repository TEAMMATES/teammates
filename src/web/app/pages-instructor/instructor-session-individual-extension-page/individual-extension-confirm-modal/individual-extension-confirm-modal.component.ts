import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TableComparatorService } from '../../../../services/table-comparator.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { SortBy, SortOrder } from '../../../../types/sort-properties';
import { InstructorExtensionTableColumnModel, StudentExtensionTableColumnModel } from '../extension-table-column-model';

export enum ExtensionModalType {
  EXTEND, DELETE,
}

/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'tm-individual-extension-confirm-modal',
  templateUrl: './individual-extension-confirm-modal.component.html',
  styleUrls: ['./individual-extension-confirm-modal.component.scss'],
})
export class IndividualExtensionConfirmModalComponent {

  @Input()
  modalType: ExtensionModalType = ExtensionModalType.EXTEND;

  @Input()
  studentsSelected: StudentExtensionTableColumnModel[] = [];

  @Input()
  instructorsSelected: InstructorExtensionTableColumnModel[] = [];

  @Input()
  extensionTimestamp: number = 0;

  @Input()
  feedbackSessionTimeZone: string = '';

  @Output()
  onConfirmExtensionCallBack: EventEmitter<boolean> = new EventEmitter();

  constructor(public activeModal: NgbActiveModal,
              public timezoneService: TimezoneService,
              private tableComparatorService: TableComparatorService) { }

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortStudentsBy: SortBy = SortBy.SECTION_NAME;
  sortStudentOrder: SortOrder = SortOrder.DESC;
  sortInstructorsBy: SortBy = SortBy.SECTION_NAME;
  sortInstructorOrder: SortOrder = SortOrder.DESC;

  DATETIME_FORMAT: string = 'd MMM YYYY h:mm:ss';

  isNotifyStudents: boolean = false;

  onExtend(): void {
    this.onConfirmExtensionCallBack.emit(this.isNotifyStudents);
  }

  onDelete(): void {
    this.onConfirmExtensionCallBack.emit(this.isNotifyStudents);
  }

  isDeleteModal() : boolean {
    return this.modalType === ExtensionModalType.DELETE;
  }

  isExtendModal() : boolean {
    return this.modalType === ExtensionModalType.EXTEND;
  }

  sortStudentColumnsBy(by: SortBy): void {
    this.sortStudentsBy = by;
    this.sortStudentOrder = this.sortStudentOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.studentsSelected.sort(this.sortStudentPanelsBy(by));
  }

  sortStudentPanelsBy(by: SortBy): (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel)
    => number {
    return (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.sectionName;
          strB = b.sectionName;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortStudentOrder, strA, strB);
    };
  }

  sortInstructorsColumnsBy(by: SortBy): void {
    this.sortInstructorsBy = by;
    this.sortInstructorOrder = this.sortInstructorOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.instructorsSelected.sort(this.sortInstructorPanelsBy(by));
  }

  sortInstructorPanelsBy(by: SortBy): (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel)
  => number {
    return (a: InstructorExtensionTableColumnModel, b: InstructorExtensionTableColumnModel): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.INSTITUTION:
          if (!a.institute) a.institute = '';
          if (!b.institute) b.institute = '';
          strA = a.institute;
          strB = b.institute;
          break;
        case SortBy.RESPONDENT_NAME:
          strA = a.name;
          strB = b.name;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.email;
          strB = b.email;
          break;
        case SortBy.SESSION_END_DATE:
          strA = a.extensionDeadline.toString();
          strB = b.extensionDeadline.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      return this.tableComparatorService.compare(by, this.sortStudentOrder, strA, strB);
    };
  }
}
