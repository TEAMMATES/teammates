import { Component, Input, OnInit } from '@angular/core';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';

/**
 * A list of responses grouped in GRQ/RGQ mode.
 */
@Component({
  selector: 'tm-grouped-responses',
  templateUrl: './grouped-responses.component.html',
  styleUrls: ['./grouped-responses.component.scss'],
})
export class GroupedResponsesComponent implements OnInit {

  @Input() responses: any = [];
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;

  @Input() isGrq: boolean = true;
  @Input() header: string = '';

  constructor() { }

  ngOnInit(): void {
  }

}
