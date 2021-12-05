import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExampleBoxComponent } from './example-box.component';

describe('ExampleBoxComponent', () => {
  let component: ExampleBoxComponent;
  let fixture: ComponentFixture<ExampleBoxComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ExampleBoxComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExampleBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
