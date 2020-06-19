import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DragDropModule } from '@angular/cdk/drag-drop';
import { FormsModule } from '@angular/forms';
import { RankOptionsFieldComponent } from './rank-options-field/rank-options-field.component';
import { RankOptionsQuestionEditDetailsFormComponent } from './rank-options-question-edit-details-form.component';

describe('RankOptionsQuestionEditDetailsFormComponent', () => {
  let component: RankOptionsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RankOptionsQuestionEditDetailsFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        RankOptionsQuestionEditDetailsFormComponent,
        RankOptionsFieldComponent],
      imports: [
        FormsModule,
        DragDropModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
