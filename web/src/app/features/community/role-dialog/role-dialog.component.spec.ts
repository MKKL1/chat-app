import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleDialogComponent } from './role-dialog.component';

describe('RoleDialogComponent', () => {
  let component: RoleDialogComponent;
  let fixture: ComponentFixture<RoleDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoleDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RoleDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
