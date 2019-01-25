import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewProfileFaqComponent } from './view-profile-faq.component';

describe('ViewProfileFaqComponent', () => {
  let component: ViewProfileFaqComponent;
  let fixture: ComponentFixture<ViewProfileFaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewProfileFaqComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewProfileFaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
