import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GenericLogLineComponent } from './generic-log-line.component';

describe('GenericLogLineComponent', () => {
  let component: GenericLogLineComponent;
  let fixture: ComponentFixture<GenericLogLineComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GenericLogLineComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericLogLineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
