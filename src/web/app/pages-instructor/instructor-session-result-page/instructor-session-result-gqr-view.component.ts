import { Component, EventEmitter, Output } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page GQR view.
 */
@Component({
  selector: 'tm-instructor-session-result-gqr-view',
  templateUrl: './instructor-session-result-gqr-view.component.html',
  styleUrls: ['./instructor-session-result-gqr-view.component.scss'],
})
export class InstructorSessionResultGqrViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  constructor() {
    super(InstructorSessionResultViewType.GQR);
  }

  /**
   * Expands the tab of the specified section.
   */
  expandSectionTab(sectionName: string, section: any): void {
    section.isTabExpanded = !section.isTabExpanded;
    if (section.isTabExpanded) {
      this.loadSection.emit(sectionName);
    }
  }

}
