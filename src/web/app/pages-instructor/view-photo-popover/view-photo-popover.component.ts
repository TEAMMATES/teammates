import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'tm-view-photo-popover',
  templateUrl: './view-photo-popover.component.html',
  styleUrls: ['./view-photo-popover.component.scss']
})
export class ViewPhotoPopoverComponent implements OnInit {

  @Input()
  photoUrl: string = '';

  @Input()
  isContentAvailable: boolean = true;

  @Output()
  loadPhotoEvent: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  missingPhotoEventHandler(): void {
    this.photoUrl = '/assets/images/profile_picture_default.png';
  }
}
