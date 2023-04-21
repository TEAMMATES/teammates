import { Component, Input, OnInit } from '@angular/core';
import { TweakedTimestampData } from '../instructor-session-base-page.component'

@Component({
  selector: 'tm-modified-timestamps-modal',
  templateUrl: './modified-timestamps-modal.component.html',
  styleUrls: ['./modified-timestamps-modal.component.scss']
})
export class ModifiedTimestampModalComponent implements OnInit {
  @Input()
  coursesOfModifiedSession: string[] = [];
  @Input()
  modifiedSessions: Record<string, TweakedTimestampData> = {};

  constructor() { }

  ngOnInit(): void {
  }

}
