package info.szadkowski.contact.throttle.counter;

import java.time.Duration;

public class TumblingCounter {
  private final Object lock = new Object();
  private final long windowSizeMillis;

  private long windowOpenTimeStampMillis = -1;
  private long counter = 0;

  public TumblingCounter(Duration windowSize) {
    this.windowSizeMillis = windowSize.toMillis();
  }

  public long count(long timeStampMillis) {
    synchronized (lock) {
      if (isNewWindow(timeStampMillis)) {
        windowOpenTimeStampMillis = timeStampMillis;
        counter = 0;
      }

      return ++counter;
    }
  }

  public boolean isNewWindow(long timeStampMillis) {
    synchronized (lock) {
      return isFirstWindow() || isTimeStampAfterWindow(timeStampMillis);
    }
  }

  private boolean isTimeStampAfterWindow(long timeStampMillis) {
    return windowOpenTimeStampMillis + windowSizeMillis <= timeStampMillis;
  }

  private boolean isFirstWindow() {
    return windowOpenTimeStampMillis < 0;
  }
}
