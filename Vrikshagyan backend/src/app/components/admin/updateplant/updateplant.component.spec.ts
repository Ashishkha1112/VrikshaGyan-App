import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateplantComponent } from './updateplant.component';

describe('UpdateplantComponent', () => {
  let component: UpdateplantComponent;
  let fixture: ComponentFixture<UpdateplantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UpdateplantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateplantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
