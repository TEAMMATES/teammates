import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'tm-cell-with-tooltip',
  templateUrl: './cell-with-tooltip.component.html',
  imports: [NgbTooltipModule],
  standalone: true,
})
export class CellWithToolTipComponent implements OnChanges {
  @Input() toolTip: string = '';
  @Input() value: string = '';

  ngOnChanges(changes: SimpleChanges): void {
    this.toolTip = changes['toolTip']?.currentValue;
  }
}
