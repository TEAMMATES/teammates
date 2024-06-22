import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import {
  NgbDropdownModule,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import { AjaxLoadingModule } from '../../../components/ajax-loading/ajax-loading.module';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';

@Component({
  selector: 'tm-group-buttons',
  templateUrl: './cell-with-actions.component.html',
  standalone: true,
  imports: [
    CommonModule,
    TeammatesRouterModule,
    AjaxLoadingModule,
    NgbDropdownModule,
    NgbTooltipModule,
  ],
})

export class CellWithActionsComponent {
  @Input() idx: number = 0;
  @Input() notificationId: number = 0;

  @Input() editNotification: () => void = () => {};
  @Input() deleteNotification : () => void = () => {};

}
