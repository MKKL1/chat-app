import { TestBed } from '@angular/core/testing';

import { TextChannelService } from './channel.service';

describe('TextChannelService', () => {
  let service: TextChannelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TextChannelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
