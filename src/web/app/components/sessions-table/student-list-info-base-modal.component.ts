import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { FeedbackSessionStudentResponse, FeedbackSessionStudentsResponse } from '../../../types/api-output';
import { FeedbackSessionStudentRemindRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';
import { SortBy, SortOrder, StudentStatusTableRowModel } from './sessions-table-model';

/**
 * The base modal information for a list of students.
 */
export abstract class StudentListInfoBaseModalComponent {

  // enum
  SortBy: typeof SortBy = SortBy;
  SortOrder: typeof SortOrder = SortOrder;

  studentStatusTableRows: StudentStatusTableRowModel[] = [];
  studentsTableRowSortBy: SortBy = SortBy.NONE;
  studentsTableRowSortOrder: SortOrder = SortOrder.DESC;

  loading: boolean = false;
  isAjaxSuccess: boolean = true;

  protected constructor(protected httpRequestService: HttpRequestService,
                        protected statusMessageService: StatusMessageService) { }

  /**
   * Get the list of students to remind in table form.
   */
  getStudentStatusTableRowModel(paramMap: { [key: string]: string }, model: StudentStatusTableRowModel[]): void {
    this.httpRequestService.get('/session/students/response', paramMap)
      .subscribe((feedbackSessionStudentsResponse: FeedbackSessionStudentsResponse) => {
        feedbackSessionStudentsResponse.studentsResponse
          .forEach((studentResponse: FeedbackSessionStudentResponse) => {

            const studentStatusTableRowModel: StudentStatusTableRowModel = {
              feedbackSessionStudentResponse: studentResponse,
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
      ((a: { feedbackSessionStudentResponse: FeedbackSessionStudentResponse },
        b: { feedbackSessionStudentResponse: FeedbackSessionStudentResponse }) => number) {
    return ((a: { feedbackSessionStudentResponse: FeedbackSessionStudentResponse },
             b: { feedbackSessionStudentResponse: FeedbackSessionStudentResponse }): number => {
      let strA: string;
      let strB: string;
      switch (by) {
        case SortBy.SECTION_NAME:
          strA = a.feedbackSessionStudentResponse.sectionName;
          strB = b.feedbackSessionStudentResponse.sectionName;
          break;
        case SortBy.TEAM_NAME:
          strA = a.feedbackSessionStudentResponse.teamName;
          strB = b.feedbackSessionStudentResponse.teamName;
          break;
        case SortBy.STUDENT_NAME:
          strA = a.feedbackSessionStudentResponse.name;
          strB = b.feedbackSessionStudentResponse.name;
          break;
        case SortBy.STUDENT_EMAIL:
          strA = a.feedbackSessionStudentResponse.email;
          strB = b.feedbackSessionStudentResponse.email;
          break;
        case SortBy.SUBMIT_STATUS:
          strA = a.feedbackSessionStudentResponse.responseStatus.toString();
          strB = b.feedbackSessionStudentResponse.responseStatus.toString();
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
  collateStudentsToSend(model: StudentStatusTableRowModel[]): FeedbackSessionStudentRemindRequest {
    const remindStudentList: string[] = [];
    for (const studentStatusTableRow of model) {
      if (studentStatusTableRow.isChecked) {
        remindStudentList.push(studentStatusTableRow.feedbackSessionStudentResponse.email);
      }
    }
    return { usersToRemind: remindStudentList };
  }
}
