import { Component, Input, OnInit } from '@angular/core';
import { StudentAttributes } from '../student-attributes';

@Component({
  selector: 'tm-course-related-info',
  templateUrl: './course-related-info.component.html',
  styleUrls: ['./course-related-info.component.scss']
})
export class CourseRelatedInfoComponent implements OnInit {

  @Input() student?: StudentAttributes;

  constructor() { }

  ngOnInit() {
  }

}
