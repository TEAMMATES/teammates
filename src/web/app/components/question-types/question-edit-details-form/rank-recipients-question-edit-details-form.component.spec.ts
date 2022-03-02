import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RankRecipientsQuestionEditDetailsFormComponent } from './rank-recipients-question-edit-details-form.component';

describe('RankRecipientsQuestionEditDetailsFormComponent', () => {
  let component: RankRecipientsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RankRecipientsQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RankRecipientsQuestionEditDetailsFormComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankRecipientsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
