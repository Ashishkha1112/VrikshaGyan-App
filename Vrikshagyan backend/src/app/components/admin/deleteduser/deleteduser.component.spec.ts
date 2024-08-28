import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteduserComponent } from './deleteduser.component';

describe('DeleteduserComponent', () => {
  let component: DeleteduserComponent;
  let fixture: ComponentFixture<DeleteduserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteduserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteduserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
