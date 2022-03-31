import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenericLogDetailsComponent } from './generic-log-details.component';

describe('GenericLogDetailsComponent', () => {
  let component: GenericLogDetailsComponent;
  let fixture: ComponentFixture<GenericLogDetailsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLogDetailsComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLogDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
