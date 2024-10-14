import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditAvatarComponent } from './edit-avatar.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

const matDataObjMock = {
  imageUrl: ''
}

describe('EditAvatarComponent', () => {
  let component: EditAvatarComponent;
  let fixture: ComponentFixture<EditAvatarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditAvatarComponent],
      providers: [
        {provide: MatDialogRef, useValue: {}},
        {provide: MAT_DIALOG_DATA, useValue: matDataObjMock},
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditAvatarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
