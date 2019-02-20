import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MoreInfoComponent } from './more-info.component';

describe('MoreInfoComponent', () => {
  let component: MoreInfoComponent;
  let fixture: ComponentFixture<MoreInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MoreInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MoreInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
