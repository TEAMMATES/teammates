import { Component, Input } from '@angular/core';
import { Student } from '../../../types/api-output';

/**
 * A simple table to show course-related information about a student
 */
@Component({
  selector: 'tm-course-related-info',
  templateUrl: './course-related-info.component.html',
  styleUrls: ['./course-related-info.component.scss'],
})
export class CourseRelatedInfoComponent {

  @Input() student?: Student;
  @Input() isDisplayOnly: boolean = false;

}
