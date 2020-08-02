import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PanelChevronComponent } from './panel-chevron.component';

describe('PanelChevronComponent', () => {
  let component: PanelChevronComponent;
  let fixture: ComponentFixture<PanelChevronComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PanelChevronComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PanelChevronComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
