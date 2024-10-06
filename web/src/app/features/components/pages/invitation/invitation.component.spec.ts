import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvitationComponent } from './invitation.component';
import {provideRouter} from "@angular/router";
import {CommunityQuery} from "../../../store/community/community.query";
import {CommunityService} from "../../../services/community.service";

// todo finished working on this component before testing

const communityServiceMock = {
  fetchCommunity: jest.fn(),
  acceptInvitation: jest.fn()
}

describe('InvitationComponent', () => {
  let component: InvitationComponent;
  let fixture: ComponentFixture<InvitationComponent>;

  let communityQuery: CommunityQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvitationComponent],
      providers: [
        provideRouter([]),
        {provide: CommunityService, useValue: communityServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  beforeEach(() => {
    communityQuery = TestBed.inject(CommunityQuery);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
