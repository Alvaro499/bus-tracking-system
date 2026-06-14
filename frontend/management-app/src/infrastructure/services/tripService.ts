import { realTripService } from './tripService.real';
import { mockTripService } from './mocks/tripService.mock';

export const tripService = (function () {
  if (process.env.NEXT_PUBLIC_USE_MOCKS === 'true') {
    return mockTripService;
  } else {
    return realTripService;
  }
})();