import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MsqFieldComponent } from './msq-field.component';

describe('MsqFieldComponent', () => {
  let component: MsqFieldComponent;
  let fixture: ComponentFixture<MsqFieldComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
