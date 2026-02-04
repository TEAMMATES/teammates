import { Component, Input } from '@angular/core';
import { Student } from '../../../types/api-output';
import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';

/**
 * A simple table to show course-related information about a student
 */
@Component({
    selector: 'tm-course-related-info',
    templateUrl: './course-related-info.component.html',
    styleUrls: ['./course-related-info.component.scss'],
    imports: [TeammatesRouterDirective],
})
export class CourseRelatedInfoComponent {

  @Input() student?: Student;
  @Input() isDisplayOnly: boolean = false;

}
