import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommunitiesListComponent } from './communities-list.component';
import {CommunityService} from "../../../services/community.service";
import {CommunityQuery} from "../../../store/community/community.query";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const communityServiceMock = {
  fetchCommunity: jest.fn(),
  getCommunities: jest.fn()
};

describe('CommunitiesListComponent', () => {
  let component: CommunitiesListComponent;
  let fixture: ComponentFixture<CommunitiesListComponent>;

  let communityQuery: CommunityQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommunitiesListComponent, NoopAnimationsModule],
      providers: [
        {provide: CommunityService, useValue: communityServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommunitiesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    communityQuery = TestBed.inject(CommunityQuery);
  })

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
