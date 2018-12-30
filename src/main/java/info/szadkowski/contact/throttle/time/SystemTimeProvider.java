package info.szadkowski.contact.throttle.time;

public class SystemTimeProvider implements TimeProvider {

  @Override
  public long getCurrentMillis() {
    return System.currentTimeMillis();
  }
}
