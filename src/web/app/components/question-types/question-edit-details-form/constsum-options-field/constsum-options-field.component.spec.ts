import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConstsumOptionsFieldComponent } from './constsum-options-field.component';

describe('ConstsumOptionsFieldComponent', () => {
  let component: ConstsumOptionsFieldComponent;
  let fixture: ComponentFixture<ConstsumOptionsFieldComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConstsumOptionsFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
