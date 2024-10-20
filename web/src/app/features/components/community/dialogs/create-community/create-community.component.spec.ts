import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCommunityComponent } from './create-community.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {CommunityService} from "../../../../services/community.service";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const communityServiceMock = {
  createCommunity: jest.fn()
};

const matDataObjMock = {
  editing: true,
  community: {
    id: '123',
    name: 'test',
    imageUrl: 'testUrl',
    ownerId: '15',
    fullyFetched: true,
    channels: [],
    members: [],
    roles: []
  }
};

describe('CreateCommunityComponent', () => {
  let component: CreateCommunityComponent;
  let fixture: ComponentFixture<CreateCommunityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateCommunityComponent, NoopAnimationsModule],
      providers: [
        {provide: MatDialogRef, useValue: {}},
        {provide: MAT_DIALOG_DATA, useValue: matDataObjMock},
        {provide: CommunityService, useValue: communityServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCommunityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
