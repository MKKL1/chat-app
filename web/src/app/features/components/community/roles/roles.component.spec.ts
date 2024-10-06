import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolesComponent } from './roles.component';
import {MatDialog} from "@angular/material/dialog";

describe('RolesComponent', () => {
  let component: RolesComponent;
  let fixture: ComponentFixture<RolesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RolesComponent],
      providers: [
        MatDialog
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
