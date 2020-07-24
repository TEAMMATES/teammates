import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

/**
 * Component to display a popover for photos
 */
@Component({
  selector: 'tm-view-photo-popover',
  templateUrl: './view-photo-popover.component.html',
  styleUrls: ['./view-photo-popover.component.scss'],
})
export class ViewPhotoPopoverComponent implements OnInit {

  @Input()
  photoUrl: string = '';

  @Input()
  useViewPhotoBtn: boolean = false;

  @Output()
  showPhotoEvent: EventEmitter<any> = new EventEmitter();

  isPhotoShown: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

  missingPhotoEventHandler(): void {
    this.photoUrl = '/assets/images/profile_picture_default.png';
  }
}
