import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { TableComparatorService } from 'src/web/services/table-comparator.service';
import { SortBy, SortOrder } from 'src/web/types/sort-properties';
import { StudentExtensionTableColumnModel } from '../instructor-session-individual-extension-page.component';
/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'individual-extension-confirm-modal',
  templateUrl: './individual-extension-confirm-modal.component.html',
  styleUrls: ['./individual-extension-confirm-modal.component.scss'],
})
export class IndividualExtensionConfirmModalComponent {

  @Input()
  courseId: string = '';

  @Input()
  studentsToExtend: StudentExtensionTableColumnModel[] = [];

  @Input()
  extensionTimestamp: number = 0;

  @Input()
  feedbackSessionName: string = '';

  @Output()
  onConfirmExtensionCallBack: EventEmitter<boolean> = new EventEmitter();

  constructor(public activeModal: NgbActiveModal,
              private tableComparatorService: TableComparatorService) { }

  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;
  sortBy: SortBy = SortBy.SECTION_NAME;
  sortOrder: SortOrder = SortOrder.DESC;
  
  isNotifyStudents: boolean = false;

  onExtend() {
    this.onConfirmExtensionCallBack.emit(this.isNotifyStudents);
  }
  
  sortCoursesBy(by: SortBy): void {
    this.sortBy = by;
    this.sortOrder = this.sortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.studentsToExtend.sort(this.sortPanelsBy(by));
  }

  sortPanelsBy(by: SortBy): (a: StudentExtensionTableColumnModel, b: StudentExtensionTableColumnModel) => number {
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
          strA = a.studentName;
          strB = b.studentName;
          break;
        case SortBy.RESPONDENT_EMAIL:
          strA = a.studentEmail;
          strB = b.studentEmail;
          break;
        //TODO: Session End_Date
        case SortBy.SESSION_END_DATE:
          strA = this.extensionTimestamp.toString();
          strB = this.extensionTimestamp.toString();
          break;
        default:
          strA = "";
          strB = "";
      }
      return this.tableComparatorService.compare(by, this.sortOrder, strA, strB);
    };
  }
}