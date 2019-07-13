package info.szadkowski.contact.throttle.spring.aspect;

import info.szadkowski.contact.exception.ThrottledRequestException;
import info.szadkowski.contact.throttle.Throttler;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
public class ThrottlingAspect {
  private final Throttler ipThrottler;
  private final Throttler allThrottler;

  public ThrottlingAspect(Throttler ipThrottler, Throttler allThrottler) {
    this.ipThrottler = ipThrottler;
    this.allThrottler = allThrottler;
  }

  @Before("@annotation(throttle)")
  public void throttle(Throttle throttle) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    if (!ipThrottler.canProcess(request.getRemoteAddr()) || !allThrottler.canProcess("all")) {
      throw new ThrottledRequestException();
    }
  }
}
