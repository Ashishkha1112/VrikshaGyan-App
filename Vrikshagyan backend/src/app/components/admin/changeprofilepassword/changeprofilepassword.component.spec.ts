import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeprofilepasswordComponent } from './changeprofilepassword.component';

describe('ChangeprofilepasswordComponent', () => {
  let component: ChangeprofilepasswordComponent;
  let fixture: ComponentFixture<ChangeprofilepasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChangeprofilepasswordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeprofilepasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
