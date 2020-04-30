import { Component, EventEmitter, Output } from '@angular/core';
import { InstructorSessionResultView } from './instructor-session-result-view';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Instructor sessions results page GRQ view.
 */
@Component({
  selector: 'tm-instructor-session-result-grq-view',
  templateUrl: './instructor-session-result-grq-view.component.html',
  styleUrls: ['./instructor-session-result-grq-view.component.scss'],
})
export class InstructorSessionResultGrqViewComponent extends InstructorSessionResultView {

  @Output()
  loadSection: EventEmitter<string> = new EventEmitter();

  constructor() {
    super(InstructorSessionResultViewType.GRQ);
  }

  /**
   * Toggles the tab of the specified section.
   */
  toggleSectionTab(sectionName: string, section: any): void {
    section.isTabExpanded = !section.isTabExpanded;
    if (section.isTabExpanded) {
      this.loadSection.emit(sectionName);
    }
  }

  /**
   * Expands the tab of all sections.
   */
  expandAllSectionTabs(): void {
    for (const sectionName of Object.keys(this.responses)) {
      this.responses[sectionName].isTabExpanded = true;
      this.loadSection.emit(sectionName);
    }
  }

  /**
   * Collapses the tab of all sections.
   */
  collapseAllSectionTabs(): void {
    for (const sectionName of Object.keys(this.responses)) {
      this.responses[sectionName].isTabExpanded = false;
    }
  }

}
