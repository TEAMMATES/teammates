import { Component, Input, OnInit } from '@angular/core';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { StringHelper } from '../../../../services/string-helper';

/**
 * Display student name with photo popover
 */
@Component({
  selector: 'tm-student-name-with-photo',
  templateUrl: './student-name-with-photo.component.html',
  styleUrls: ['./student-name-with-photo.component.scss'],
})
export class StudentNameWithPhotoComponent implements OnInit {

  @Input()
  name: string = '';

  @Input()
  courseId: string = '';

  @Input()
  email: string = '';

  iconClass: string = 'far';
  tooltipTimer: any;

  constructor() { }

  ngOnInit(): void {
    this.name = StringHelper.removeAnonymousHash(this.name);
  }

  handleMouseover(t: NgbTooltip): void {
    this.iconClass = 'fa';
    this.tooltipTimer = setTimeout(() => {
      if (!t.isOpen()) {
        t.open();
      }
    }, 400);
  }

  handleMouseout(t: NgbTooltip): void {
    this.iconClass = 'far';
    this.clearTooltipTimer();
    if (t.isOpen()) {
      t.close();
    }
  }

  clearTooltipTimer(): void {
    clearTimeout(this.tooltipTimer);
  }
}
