import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MaintainerPageComponent } from './maintainer-page.component';

describe('MaintainerPageComponent', () => {
  let component: MaintainerPageComponent;
  let fixture: ComponentFixture<MaintainerPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MaintainerPageComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MaintainerPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
