import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditMemberRoleComponent } from './edit-member-role.component';

describe('EditMemberRoleComponent', () => {
  let component: EditMemberRoleComponent;
  let fixture: ComponentFixture<EditMemberRoleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditMemberRoleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditMemberRoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
