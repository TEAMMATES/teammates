import { Component, Input } from '@angular/core';
import { Student } from '../../../types/api-output';
import { RouterLink } from '@angular/router';

/**
 * A simple table to show course-related information about a student
 */
@Component({
  selector: 'tm-course-related-info',
  templateUrl: './course-related-info.component.html',
  styleUrls: ['./course-related-info.component.scss'],
  imports: [RouterLink],
})
export class CourseRelatedInfoComponent {
  @Input() student?: Student;
  @Input() isDisplayOnly = false;
}
