import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { RespondentListInfoTableComponent } from '../respondent-list-info-table/respondent-list-info-table.component';
import { SendRemindersToStudentModalComponent } from './send-reminders-to-student-modal.component';

@Component({ selector: 'tm-ajax-preload', template: '' })
class AjaxPreloadComponent {}

describe('SendRemindersToStudentModalComponent', () => {
  let component: SendRemindersToStudentModalComponent;
  let fixture: ComponentFixture<SendRemindersToStudentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        SendRemindersToStudentModalComponent,
        AjaxPreloadComponent,
        RespondentListInfoTableComponent,
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
    fixture = TestBed.createComponent(SendRemindersToStudentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
