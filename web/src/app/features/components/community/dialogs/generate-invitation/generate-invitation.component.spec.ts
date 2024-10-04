import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateInvitationComponent } from './generate-invitation.component';
import {MatSnackBar} from "@angular/material/snack-bar";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {CommunityService} from "../../../../services/community.service";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const matDataObjMock = {
  id: '123'
};

const communityServiceMock = {
  createInvitation: jest.fn()
};

describe('GenerateInvitationComponent', () => {
  let component: GenerateInvitationComponent;
  let fixture: ComponentFixture<GenerateInvitationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateInvitationComponent, NoopAnimationsModule],
      providers: [
        MatSnackBar,
        {provide: MatDialogRef, useValue: {}},
        {provide: MAT_DIALOG_DATA, useValue: matDataObjMock},
        {provide: CommunityService, useValue: communityServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerateInvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
