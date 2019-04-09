import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material';
import { MsqFieldComponent } from './msq-field.component';

describe('MsqFieldComponent', () => {
  let component: MsqFieldComponent;
  let fixture: ComponentFixture<MsqFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MsqFieldComponent],
      imports: [
        FormsModule,
        MatSnackBarModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
