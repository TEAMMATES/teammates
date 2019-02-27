import { Component } from '@angular/core';
import { InstructorSessionsResultView } from './instructor-sessions-result-view';
import { InstructorSessionsResultViewType } from './instructor-sessions-result-view-type.enum';

/**
 * Instructor sessions results page GQR view.
 */
@Component({
  selector: 'tm-instructor-sessions-result-gqr-view',
  templateUrl: './instructor-sessions-result-gqr-view.component.html',
  styleUrls: ['./instructor-sessions-result-gqr-view.component.scss'],
})
export class InstructorSessionsResultGqrViewComponent extends InstructorSessionsResultView {

  constructor() {
    super(InstructorSessionsResultViewType.GQR);
  }

}
