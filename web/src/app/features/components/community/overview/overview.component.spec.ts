import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverviewComponent } from './overview.component';
import {MatDialog} from "@angular/material/dialog";
import {CommunityQuery} from "../../../store/community/community.query";
import {CommunityService} from "../../../services/community.service";

const communityServiceMock = {
  deleteCommunity: jest.fn()
};

describe('OverviewComponent', () => {
  let component: OverviewComponent;
  let fixture: ComponentFixture<OverviewComponent>;

  let communityQuery: CommunityQuery;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverviewComponent],
      providers: [
        MatDialog,
        {provide: CommunityService, useValue: communityServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OverviewComponent);
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
