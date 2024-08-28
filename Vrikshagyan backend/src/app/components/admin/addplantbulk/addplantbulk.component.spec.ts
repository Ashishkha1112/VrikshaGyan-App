import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddplantbulkComponent } from './addplantbulk.component';

describe('AddplantbulkComponent', () => {
  let component: AddplantbulkComponent;
  let fixture: ComponentFixture<AddplantbulkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddplantbulkComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddplantbulkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
