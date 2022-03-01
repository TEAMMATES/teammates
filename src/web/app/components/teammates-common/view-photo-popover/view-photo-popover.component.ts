import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * Component to display a popover for photos
 */
@Component({
  selector: 'tm-view-photo-popover',
  templateUrl: './view-photo-popover.component.html',
  styleUrls: ['./view-photo-popover.component.scss'],
})
export class ViewPhotoPopoverComponent {

  @Input()
  photoUrl: string = '';

  @Input()
  useViewPhotoBtn: boolean = false;

  @Output()
  showPhotoEvent: EventEmitter<any> = new EventEmitter();

  isPhotoShown: boolean = false;

  missingPhotoEventHandler(): void {
    this.photoUrl = '/assets/images/profile_picture_default.png';
  }
}
