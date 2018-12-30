package info.szadkowski.contact.throttle;

public interface Throttler {
  boolean canProcess(String key);
  void clearExpired();
}
