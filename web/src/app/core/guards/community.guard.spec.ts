import {provideRouter, Router} from "@angular/router";
import {TestBed} from "@angular/core/testing";
import {Component} from "@angular/core";
import {communityGuard} from "./community.guard";
import {RouterTestingHarness} from "@angular/router/testing";
import {CommunityQuery} from "../../features/store/community/community.query";

@Component({ template: '' })
export class TextChannelDummyComponent {}

@Component({ template: '' })
export class VoiceChannelDummyComponent {}

@Component({ template: '' })
export class CommunityDummyComponent {}

const communityQueryMock = {
  hasActive: jest.fn()
};

describe('Test guard', () => {
  let communityQuery: CommunityQuery;
  let router: Router;

  beforeEach(async() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: CommunityQuery, useValue: communityQueryMock},
        provideRouter([
          { path: 'app/voice', component: VoiceChannelDummyComponent, canActivate: [communityGuard] },
          { path: 'app/text', component: TextChannelDummyComponent, canActivate: [communityGuard] },
          { path: 'app/communities', component: CommunityDummyComponent}
        ])
      ]
    });
    router = TestBed.inject(Router);
    communityQuery = TestBed.inject(CommunityQuery);
  });

  it('should navigate to voice component', async () => {
    jest.spyOn(communityQuery, 'hasActive').mockReturnValueOnce(true);
    const harness = await RouterTestingHarness.create();

    let activatedComponent = await harness.navigateByUrl('/app/voice', VoiceChannelDummyComponent);

    expect(activatedComponent).toBeInstanceOf(VoiceChannelDummyComponent);
    expect(router.url).toBe("/app/voice");
  });

  it('should navigate to text component', async () => {
    jest.spyOn(communityQuery, 'hasActive').mockReturnValueOnce(true);
    const harness = await RouterTestingHarness.create();

    let activatedComponent = await harness.navigateByUrl('/app/text', TextChannelDummyComponent);

    expect(activatedComponent).toBeInstanceOf(TextChannelDummyComponent);
    expect(router.url).toBe("/app/text");
  });

  it('should not navigate to text component', async () => {
    jest.spyOn(communityQuery, 'hasActive').mockReturnValueOnce(false);
    const createUrlTreeSpy = jest.spyOn(router, 'createUrlTree');
    const harness = await RouterTestingHarness.create();

    await harness.navigateByUrl('/app/text');

    expect(createUrlTreeSpy).toHaveBeenCalledWith(['/', 'app', 'communities']);
    expect(router.url).toBe('/app/communities');
  });

  it('should not navigate to voice component', async () => {
    jest.spyOn(communityQuery, 'hasActive').mockReturnValueOnce(false);
    const createUrlTreeSpy = jest.spyOn(router, 'createUrlTree');
    const harness = await RouterTestingHarness.create();

    await harness.navigateByUrl('/app/voice');

    expect(createUrlTreeSpy).toHaveBeenCalledWith(['/', 'app', 'communities']);
    expect(router.url).toBe('/app/communities');
  });
});
