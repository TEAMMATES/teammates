import { Component, OnInit, Input } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentListSectionData } from '../student-list/student-list-section-data';
import { StudentListStudentData} from '../student-list/student-list-section-data';
import { ErrorMessageOutput } from '../../message-output';

@Component({
  selector: 'tm-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss']
})
export class StudentListComponent implements OnInit {

  @Input() courseId: string = '';
  @Input() sections: StudentListSectionData[] = [];
  @Input() useGrayHeading: boolean = true;

  constructor(private httpRequestService: HttpRequestService, private statusMessageService: StatusMessageService,
    private ngbModal: NgbModal) { }

  ngOnInit() {
  }

  hasSection(): boolean {
    return !((this.sections.length == 1) && (this.sections[0].sectionName == "None"));
  }

  trackByFn(index: number, item: StudentListStudentData) {
    return item.email;
  }

  /**
   * Open the student delete confirmation modal.
   */
  openModal(content: any): void {
    this.ngbModal.open(content).result.then((studentEmail) => this.removeStudentFromCourse(studentEmail));
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(studentEmail: string): void {
    const paramMap: { [key: string]: string } = {
      courseid: this.courseId,
      studentemail: studentEmail,
    };
    this.httpRequestService.delete('/students', paramMap).subscribe(() => {
      this.statusMessageService.showSuccessMessage(`Student is successfully deleted from course "${this.courseId}"`);
      this.sections.forEach(
        section => section.students = section.students.filter(student => student.email != studentEmail))
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
