import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { McqFieldComponent } from './mcq-field.component';

describe('McqFieldComponent', () => {
  let component: McqFieldComponent;
  let fixture: ComponentFixture<McqFieldComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [McqFieldComponent],
      imports: [
        FormsModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be false by default for isQuestionDropdownEnabled', () => {
    expect(component.isQuestionDropdownEnabled).toBeFalsy();
  });
});
