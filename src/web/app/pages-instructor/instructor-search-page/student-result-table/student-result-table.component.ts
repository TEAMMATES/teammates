import { Component, OnInit, Input } from '@angular/core';
import { SearchStudentsTable } from '../instructor-search-page.component';

@Component({
  selector: 'tm-student-result-table',
  templateUrl: './student-result-table.component.html',
  styleUrls: ['./student-result-table.component.scss']
})
export class StudentResultTableComponent implements OnInit {

  @Input() studentTables: SearchStudentsTable[] = [];

  constructor() { }

  ngOnInit() {
  }

}
