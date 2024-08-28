import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerifiedplantComponent } from './verifiedplant.component';

describe('VerifiedplantComponent', () => {
  let component: VerifiedplantComponent;
  let fixture: ComponentFixture<VerifiedplantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VerifiedplantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VerifiedplantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
