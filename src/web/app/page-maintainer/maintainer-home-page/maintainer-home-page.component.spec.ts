import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MaintainerHomePageComponent } from './maintainer-home-page.component';

describe('MaintainerHomePageComponent', () => {
  let component: MaintainerHomePageComponent;
  let fixture: ComponentFixture<MaintainerHomePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MaintainerHomePageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MaintainerHomePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
