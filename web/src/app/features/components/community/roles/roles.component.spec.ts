import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolesComponent } from './roles.component';
import {MatDialog} from "@angular/material/dialog";
import {RoleService} from "../../../services/role.service";
import {UserService} from "../../../../core/services/user.service";

const mockRoleService = {
  deleteRole: jest.fn()
};

const mockUserService = {
  getPermission: jest.fn().mockReturnValue({
    isAdministrator: true
  })
};

describe('RolesComponent', () => {
  let component: RolesComponent;
  let fixture: ComponentFixture<RolesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RolesComponent],
      providers: [
        MatDialog,
        {provide: RoleService, useValue: mockRoleService},
        {provide: UserService, useValue: mockUserService}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RolesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
