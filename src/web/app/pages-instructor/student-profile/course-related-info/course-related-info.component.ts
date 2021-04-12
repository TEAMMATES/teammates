import { Component, Input, OnInit } from '@angular/core';
import { Student } from '../../../../types/api-output';

/**
 * A simple table to show course-related information about a student
 */
@Component({
  selector: 'tm-course-related-info',
  templateUrl: './course-related-info.component.html',
  styleUrls: ['./course-related-info.component.scss'],
})
export class CourseRelatedInfoComponent implements OnInit {

  @Input() student?: Student;
  @Input() isDisplayOnly: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
