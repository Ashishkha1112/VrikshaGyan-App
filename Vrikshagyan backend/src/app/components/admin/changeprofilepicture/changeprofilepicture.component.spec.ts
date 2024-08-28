import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeprofilepictureComponent } from './changeprofilepicture.component';

describe('ChangeprofilepictureComponent', () => {
  let component: ChangeprofilepictureComponent;
  let fixture: ComponentFixture<ChangeprofilepictureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChangeprofilepictureComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeprofilepictureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
