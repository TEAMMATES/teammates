import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RankOptionsFieldComponent } from './rank-options-field.component';

describe('RankOptionsFieldComponent', () => {
  let component: RankOptionsFieldComponent;
  let fixture: ComponentFixture<RankOptionsFieldComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(RankOptionsFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
