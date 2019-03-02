import { Component, EventEmitter, Output } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page RGQ view.
 */
@Component({
  selector: 'tm-instructor-session-result-rgq-view',
  templateUrl: './instructor-session-result-rgq-view.component.html',
  styleUrls: ['./instructor-session-result-rgq-view.component.scss'],
})
export class InstructorSessionResultRgqViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  constructor() {
    super(InstructorSessionResultViewType.RGQ);
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
