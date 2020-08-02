import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { RespondentListInfoTableComponent } from '../respondent-list-info-table/respondent-list-info-table.component';
import { ResendResultsLinkToRespondentModalComponent } from './resend-results-link-to-respondent-modal.component';

@Component({ selector: 'tm-ajax-preload', template: '' })
class AjaxPreloadComponent {}

describe('ResendResultsLinkToRespondentModalComponent', () => {
  let component: ResendResultsLinkToRespondentModalComponent;
  let fixture: ComponentFixture<ResendResultsLinkToRespondentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ResendResultsLinkToRespondentModalComponent,
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
    fixture = TestBed.createComponent(ResendResultsLinkToRespondentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
