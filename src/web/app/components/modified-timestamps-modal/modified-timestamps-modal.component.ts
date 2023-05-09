import { Component, Input } from '@angular/core';
import { TweakedTimestampData } from '../../pages-instructor/instructor-session-base-page.component';

@Component({
  selector: 'tm-modified-timestamps-modal',
  templateUrl: './modified-timestamps-modal.component.html',
  styleUrls: ['./modified-timestamps-modal.component.scss'],
})
export class ModifiedTimestampModalComponent {
  @Input()
  coursesOfModifiedSession: string[] = [];
  @Input()
  modifiedSessions: Record<string, TweakedTimestampData> = {};
}
