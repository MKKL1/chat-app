import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleDialogComponent } from './role-dialog.component';
import {NoopAnimationsModule} from "@angular/platform-browser/animations";
import {RoleService} from "../../../../services/role.service";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";

const mockRoleService = {
  changeRoleMember: jest.fn()
};

const mockDialogData = {
  roleToUpdate: {
    name: 'Mock Role',
    permissionOverwrites: 0n
  }
};

describe('RoleDialogComponent', () => {
  let component: RoleDialogComponent;
  let fixture: ComponentFixture<RoleDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoleDialogComponent, NoopAnimationsModule],
      providers: [
        MatDialog,
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: mockDialogData },
        {provide: RoleService, useValue: mockRoleService}
      ]
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
