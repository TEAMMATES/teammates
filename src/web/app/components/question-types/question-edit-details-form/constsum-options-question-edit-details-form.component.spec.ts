import { DragDropModule } from '@angular/cdk/drag-drop';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { ConstsumOptionsFieldComponent } from './constsum-options-field/constsum-options-field.component';
import {
  ConstsumOptionsQuestionEditDetailsFormComponent,
} from './constsum-options-question-edit-details-form.component';

describe('ConstsumOptionsQuestionEditDetailsFormComponent', () => {
  let component: ConstsumOptionsQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<ConstsumOptionsQuestionEditDetailsFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        DragDropModule,
      ],
      declarations: [
        ConstsumOptionsQuestionEditDetailsFormComponent,
        ConstsumOptionsFieldComponent,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsQuestionEditDetailsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
