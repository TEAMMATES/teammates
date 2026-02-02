import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleResponseComponent } from './single-response.component';

describe('SingleResponseComponent', () => {
  let component: SingleResponseComponent;
  let fixture: ComponentFixture<SingleResponseComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
