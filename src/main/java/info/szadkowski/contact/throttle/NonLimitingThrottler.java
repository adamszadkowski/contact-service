package info.szadkowski.contact.throttle;

class NonLimitingThrottler implements Throttler {

  @Override
  public boolean canProcess(String key) {
    return true;
  }

  @Override
  public void clearExpired() {
    // do nothing
  }
}
