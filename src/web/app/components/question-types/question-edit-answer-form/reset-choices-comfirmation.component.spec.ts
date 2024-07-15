import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetChoicesComfirmationComponent } from './reset-choices-comfirmation.component';

describe('ResetChoicesComfirmationComponent', () => {
  let component: ResetChoicesComfirmationComponent;
  let fixture: ComponentFixture<ResetChoicesComfirmationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResetChoicesComfirmationComponent],
    });
    fixture = TestBed.createComponent(ResetChoicesComfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
