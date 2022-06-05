import { Clipboard } from '@angular/cdk/clipboard';
import { Location } from '@angular/common';
import { Component, ElementRef, EventEmitter, Input, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { collapseAnim } from './collapse-anim';

/**
 * A standalone panel for instructor help page content.
 */
@Component({
  selector: 'tm-instructor-help-panel',
  templateUrl: './instructor-help-panel.component.html',
  styleUrls: ['./instructor-help-panel.component.scss'],
  animations: [collapseAnim],
})
export class InstructorHelpPanelComponent {

  @Input() id: string = '';
  @Input() section: string = '';
  @Input() headerText: string = '';

  isPanelExpandedValue: boolean = false;
  @Output() isPanelExpandedChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input() get isPanelExpanded(): boolean {
    return this.isPanelExpandedValue;
  }

  set isPanelExpanded(value: boolean) {
    this.isPanelExpandedValue = value;
    this.isPanelExpandedChange.emit(value);
  }

  constructor(public elementRef: ElementRef,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private location: Location,
              private clipboard: Clipboard,
  ) { }

  copyUrlToClipboard(event: Event): void {
    // Prevent panel from changing state
    event.stopPropagation();

    const frontendUrl = window.location.origin;
    const queryParams = { section: this.section, questionId: this.id };
    const path = this.router.createUrlTree(
        [],
        { relativeTo: this.activatedRoute, queryParams },
    ).toString();
    const urlToCopy = frontendUrl + path;

    this.clipboard.copy(urlToCopy);
    this.location.go(path);
  }
}
