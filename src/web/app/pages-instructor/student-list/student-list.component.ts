import { Component, OnInit, Input } from '@angular/core';
import { StudentListSectionData } from '../student-list/student-list-section-data';

@Component({
  selector: 'tm-student-list',
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.scss']
})
export class StudentListComponent implements OnInit {

  @Input() courseId: string = '';
  @Input() sections: StudentListSectionData[] = [];
  @Input() useGrayHeading: boolean = true;

  constructor() { }

  ngOnInit() {
  }

  hasSection(): boolean {
    return (this.sections.length == 1) && (this.sections[0].sectionName == "None");
  }

}
