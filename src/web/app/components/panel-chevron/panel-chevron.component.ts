import { Component, Input } from '@angular/core';
import { NgIf } from '@angular/common';

/**
 * Chevron icon used in panel headers.
 *
 * Note that this icon is optimized for usage in panel headers and may not be suitable in other conditions.
 */
@Component({
  selector: 'tm-panel-chevron',
  templateUrl: './panel-chevron.component.html',
  styleUrls: ['./panel-chevron.component.scss'],
  imports: [NgIf],
})
export class PanelChevronComponent {

  @Input() isExpanded: boolean = false;

  @Input() chevronColor: string = 'white';
}
