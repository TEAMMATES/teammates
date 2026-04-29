import { Component, ElementRef, EventEmitter, Input, Output } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { NavigationService } from '../../../../services/navigation.service';
import { PanelChevronComponent } from '../../../components/panel-chevron/panel-chevron.component';

/**
 * A standalone panel for instructor help page content.
 */
@Component({
  selector: 'tm-instructor-help-panel',
  templateUrl: './instructor-help-panel.component.html',
  styleUrls: ['./instructor-help-panel.component.scss'],
  imports: [PanelChevronComponent, NgbCollapse],
})
export class InstructorHelpPanelComponent {
  @Input() id = '';
  @Input() section = '';
  @Input() headerText = '';

  isPanelExpandedValue = false;
  @Output() isPanelExpandedChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input() get isPanelExpanded(): boolean {
    return this.isPanelExpandedValue;
  }

  set isPanelExpanded(value: boolean) {
    this.isPanelExpandedValue = value;
    this.isPanelExpandedChange.emit(value);
  }

  constructor(
    public elementRef: ElementRef,
    private navigationService: NavigationService,
  ) {}

  changeBrowserUrl(event: Event): void {
    // Prevent panel from changing state
    event.stopPropagation();

    const queryParams: Record<string, string> = { section: this.section, questionId: this.id };
    this.navigationService.changeBrowserUrl(queryParams);
  }
}
