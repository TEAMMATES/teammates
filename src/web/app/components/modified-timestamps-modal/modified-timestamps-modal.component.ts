import { Component, Input } from '@angular/core';
import { TweakedTimestampData } from '../../pages-instructor/instructor-session-base-page.component';
import { NgIf, NgFor, NgClass, KeyValuePipe } from '@angular/common';

@Component({
  selector: 'tm-modified-timestamps-modal',
  templateUrl: './modified-timestamps-modal.component.html',
  styleUrls: ['./modified-timestamps-modal.component.scss'],
  imports: [
    NgIf,
    NgFor,
    NgClass,
    KeyValuePipe,
  ],
})
export class ModifiedTimestampModalComponent {
  @Input()
  coursesOfModifiedSession: string[] = [];
  @Input()
  modifiedSessions: Record<string, TweakedTimestampData> = {};
}
