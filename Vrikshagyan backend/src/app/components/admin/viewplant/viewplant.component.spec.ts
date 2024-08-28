import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewplantComponent } from './viewplant.component';

describe('ViewplantComponent', () => {
  let component: ViewplantComponent;
  let fixture: ComponentFixture<ViewplantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewplantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewplantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
