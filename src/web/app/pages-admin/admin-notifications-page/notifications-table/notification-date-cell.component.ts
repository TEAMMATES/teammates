import { Component, Input } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { FormatDateBriefPipe } from '../../../components/teammates-common/format-date-brief.pipe';
import { FormatDateDetailPipe } from '../../../components/teammates-common/format-date-detail.pipe';

@Component({
  selector: 'tm-notification-date-cell',
  templateUrl: './notification-date-cell.component.html',
  imports: [NgbTooltip, FormatDateBriefPipe, FormatDateDetailPipe],
})
export class NotificationDateCellComponent {
  @Input() timestamp = 0;
  @Input() guessTimezone = 'UTC';
}
