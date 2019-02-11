import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';
import { StudentFeedbackSessionResponseStatus, StudentsFeedbackSessionResponseStatus } from '../../student';
import { SortBy, SortOrder, StudentStatusTableRowModel } from './sessions-table-model';

/**
 * The base modal information for a list of students.
 */
export abstract class StudentListInfoBaseModalComponent {

  // enum
  protected SortBy: typeof SortBy = SortBy;
  protected SortOrder: typeof SortOrder = SortOrder;

  protected studentStatusTableRows: StudentStatusTableRowModel[] = [];
  protected studentsTableRowSortBy: SortBy = SortBy.NONE;
  protected studentsTableRowSortOrder: SortOrder = SortOrder.DESC;

  protected loading: boolean = false;
  protected isAjaxSuccess: boolean = true;

  protected constructor(protected activeModal: NgbActiveModal, protected httpRequestService: HttpRequestService,
                        protected statusMessageService: StatusMessageService) { }

  /**
   * Get the list of students to remind in table form.
   */
  getStudentStatusTableRowModel(paramMap: { [key: string]: string }, model: StudentStatusTableRowModel[]): void {
    this.httpRequestService.get('/session/remind/submission', paramMap)
      .subscribe((studentsFeedbackSessionResponseStatus: StudentsFeedbackSessionResponseStatus) => {
        studentsFeedbackSessionResponseStatus.studentsFeedbackSessionResponseStatus
          .forEach((studentFeedbackSessionResponseStatus: StudentFeedbackSessionResponseStatus) => {

            const studentStatusTableRowModel: StudentStatusTableRowModel = {
              studentFeedbackSessionResponseStatus,
              isChecked: false,
            };

            model.push(studentStatusTableRowModel);
          });
        this.sortStudentsTableRows(SortBy.SUBMIT_STATUS);
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorMessage(resp.error.message);
        this.isAjaxSuccess = false;
      });
    this.loading = false;
  }

  /**
   * Sort the students in according to selection option.
   */
  sortStudentsTableRows(by: SortBy): void {
    this.studentsTableRowSortBy = by;
    // reverse the sort order
    this.studentsTableRowSortOrder =
        this.studentsTableRowSortOrder === SortOrder.DESC ? SortOrder.ASC : SortOrder.DESC;
    this.studentStatusTableRows.sort(this.sortRowsBy(by, this.studentsTableRowSortOrder));
  }

  /**
   * Sorts the rows of students in order.
   */
  sortRowsBy(by: SortBy, order: SortOrder):
      ((a: { studentFeedbackSessionResponseStatus: StudentFeedbackSessionResponseStatus },
        b: { studentFeedbackSessionResponseStatus: StudentFeedbackSessionResponseStatus }) => number) {
    return ((a: { studentFeedbackSessionResponseStatus: StudentFeedbackSessionResponseStatus },
             b: { studentFeedbackSessionResponseStatus: StudentFeedbackSessionResponseStatus }): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.studentFeedbackSessionResponseStatus.sectionName;
          strB = b.studentFeedbackSessionResponseStatus.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.studentFeedbackSessionResponseStatus.teamName;
          strB = b.studentFeedbackSessionResponseStatus.teamName;
          break;
        case SortBy.STUDENT_NAME:
          strA = a.studentFeedbackSessionResponseStatus.name;
          strB = b.studentFeedbackSessionResponseStatus.name;
          break;
        case SortBy.STUDENT_EMAIL:
          strA = a.studentFeedbackSessionResponseStatus.email;
          strB = b.studentFeedbackSessionResponseStatus.email;
          break;
        case SortBy.SUBMIT_STATUS:
          strA = a.studentFeedbackSessionResponseStatus.responseStatus.toString();
          strB = b.studentFeedbackSessionResponseStatus.responseStatus.toString();
          break;
        default:
          strA = '';
          strB = '';
      }
      if (order === SortOrder.ASC) {
        return strA.localeCompare(strB);
      }
      if (order === SortOrder.DESC) {
        return strB.localeCompare(strA);
      }
      return 0;
    });
  }

  /**
   * Check all the checkbox of the students.
   */
  checkAllStudents(model: StudentStatusTableRowModel[], isCheck: boolean): void {
    for (const remindStudentRow of model) {
      remindStudentRow.isChecked = isCheck;
    }
  }

  /**
   * Collates a list of selected students with selected checkbox.
   */
  collateStudentsToSend(model: StudentStatusTableRowModel[]): string[] {
    const remindStudentList: string[] = [];
    for (const studentStatusTableRow of model) {
      if (studentStatusTableRow.isChecked) {
        remindStudentList.push(studentStatusTableRow.studentFeedbackSessionResponseStatus.email);
      }
    }
    return remindStudentList;
  }
}
