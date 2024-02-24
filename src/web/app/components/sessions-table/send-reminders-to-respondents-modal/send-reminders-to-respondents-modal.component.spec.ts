import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SendRemindersToRespondentsModalComponent } from './send-reminders-to-respondents-modal.component';
import { RespondentListInfoTableComponent } from '../respondent-list-info-table/respondent-list-info-table.component';

@Component({ selector: 'tm-ajax-preload', template: '' })
class AjaxPreloadComponent {}

describe('SendRemindersToRespondentsModalComponent', () => {
  let component: SendRemindersToRespondentsModalComponent;
  let fixture: ComponentFixture<SendRemindersToRespondentsModalComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SendRemindersToRespondentsModalComponent,
        AjaxPreloadComponent,
        RespondentListInfoTableComponent,
      ],
      imports: [
        HttpClientTestingModule,
        FormsModule,
      ],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SendRemindersToRespondentsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
