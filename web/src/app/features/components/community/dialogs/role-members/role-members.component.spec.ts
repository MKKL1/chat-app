import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleMembersComponent } from './role-members.component';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {RoleService} from "../../../../services/role.service";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

const roleServiceMock = {
  changeRoleMember: jest.fn()
};

describe('RoleMembersComponent', () => {
  let component: RoleMembersComponent;
  let fixture: ComponentFixture<RoleMembersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoleMembersComponent,
      BrowserAnimationsModule],
      providers: [
        MatDialog,
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        {provide: RoleService, useValue: roleServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoleMembersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
