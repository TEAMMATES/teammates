import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CourseLevelPrivileges } from '../../../instructor-privilege';

/**
 * Modal to view instructor privileges.
 */
@Component({
  selector: 'tm-view-privileges-modal',
  templateUrl: './view-privileges-modal.component.html',
  styleUrls: ['./view-privileges-modal.component.scss'],
})
export class ViewPrivilegesModalComponent implements OnInit {

  @Input()
  instructorrole: string = '';

  @Input()
  model: CourseLevelPrivileges = {
    canmodifycourse: false,
    canmodifyinstructor: false,
    canmodifysession: false,
    canmodifystudent: false,
    canviewstudentinsection: false,
    canviewsessioninsection: false,
    cansubmitsessioninsection: false,
    canmodifysessioncommentinsection: false,
  };

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
