import { Component, ElementRef, EventEmitter, Input, OnInit, Output } from '@angular/core';
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
export class InstructorHelpPanelComponent implements OnInit {

  @Input() id: string = '';
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

  constructor(public elementRef: ElementRef) { }

  ngOnInit(): void {
  }

}
