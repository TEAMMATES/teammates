import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { MatSnackBarModule } from '@angular/material';
import { StudentListInfoTableComponent } from '../student-list-info-table/student-list-info-table.component';
import { ResendResultsLinkToStudentModalComponent } from './resend-results-link-to-student-modal.component';

@Component({ selector: 'tm-ajax-preload', template: '' })
class AjaxPreloadComponent {}

describe('ResendResultsLinkToStudentModalComponent', () => {
  let component: ResendResultsLinkToStudentModalComponent;
  let fixture: ComponentFixture<ResendResultsLinkToStudentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ResendResultsLinkToStudentModalComponent,
        AjaxPreloadComponent,
        StudentListInfoTableComponent,
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule,
        MatSnackBarModule,
      ],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResendResultsLinkToStudentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
