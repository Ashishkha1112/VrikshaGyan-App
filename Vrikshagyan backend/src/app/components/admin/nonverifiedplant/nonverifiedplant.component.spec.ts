import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NonverifiedplantComponent } from './nonverifiedplant.component';

describe('NonverifiedplantComponent', () => {
  let component: NonverifiedplantComponent;
  let fixture: ComponentFixture<NonverifiedplantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NonverifiedplantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NonverifiedplantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
