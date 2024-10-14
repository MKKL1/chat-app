import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverviewComponent } from './overview.component';
import {MatDialog} from "@angular/material/dialog";
import {CommunityQuery} from "../../../store/community/community.query";
import {CommunityService} from "../../../services/community.service";
import {UserService} from "../../../../core/services/user.service";
import {of} from "rxjs";

const communityServiceMock = {
  deleteCommunity: jest.fn()
};

const userServiceMock = {
  getUser: jest.fn(() => '123')
};

describe('OverviewComponent', () => {
  let component: OverviewComponent;
  let fixture: ComponentFixture<OverviewComponent>;

  let communityQuery: CommunityQuery;
  const mockCommunity = {
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

  beforeEach(async () => {
    const communityQueryMock = {
      selectActive: jest.fn(() => of(mockCommunity))
    };

    await TestBed.configureTestingModule({
      imports: [OverviewComponent],
      providers: [
        MatDialog,
        { provide: CommunityQuery, useValue: communityQueryMock },
        {provide: CommunityService, useValue: communityServiceMock},
        {provide: UserService, useValue: userServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
