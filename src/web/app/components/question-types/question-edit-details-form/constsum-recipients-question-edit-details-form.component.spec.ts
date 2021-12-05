import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import {
  ConstsumRecipientsQuestionEditDetailsFormComponent,
} from './constsum-recipients-question-edit-details-form.component';

describe('ConstsumRecipientsQuestionEditDetailsFormComponent', () => {
  let component: ConstsumRecipientsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<ConstsumRecipientsQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ConstsumRecipientsQuestionEditDetailsFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumRecipientsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
