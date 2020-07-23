import { Component, Input, OnInit } from '@angular/core';

/**
 * Chevron icon used in panel headers.
 *
 * Note that this icon is optimized for usage in panel headers and may not be suitable in other conditions.
 */
@Component({
  selector: 'tm-panel-chevron',
  templateUrl: './panel-chevron.component.html',
  styleUrls: ['./panel-chevron.component.scss'],
})
export class PanelChevronComponent implements OnInit {

  @Input() isExpanded: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
