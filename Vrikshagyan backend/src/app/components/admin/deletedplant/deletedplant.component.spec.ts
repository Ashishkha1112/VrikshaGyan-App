import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeletedplantComponent } from './deletedplant.component';

describe('DeletedplantComponent', () => {
  let component: DeletedplantComponent;
  let fixture: ComponentFixture<DeletedplantComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeletedplantComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeletedplantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
