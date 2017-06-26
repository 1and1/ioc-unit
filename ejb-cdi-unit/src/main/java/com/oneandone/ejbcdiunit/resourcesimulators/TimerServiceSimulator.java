package com.oneandone.ejbcdiunit.resourcesimulators;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.ejb.EJBException;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

/**
 * Dummy TimerService to be injected as Resource.
 *
 * @author aschoerk
 */
public class TimerServiceSimulator implements TimerService {
    /**
     * Create a single-action timer that expires after a specified duration.
     *
     * @param duration the number of milliseconds that must elapse before
     *                 the timer expires.
     * @param info     application information to be delivered along
     *                 with the timer expiration notification. This can be null.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If duration is negative
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method fails due to a
     *                                  system-level failure.
     */
    @Override
    public Timer createTimer(long duration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create a single-action timer that expires after a specified duration.
     *
     * @param duration    the number of milliseconds that must elapse before
     *                    the timer expires.
     * @param timerConfig timer configuration.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If duration is negative
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method fails due to a
     *                                  system-level failure.
     * @since EJB 3.1
     */
    @Override
    public Timer createSingleActionTimer(long duration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create an interval timer whose first expiration occurs after a specified
     * duration, and whose subsequent expirations occur after a specified
     * interval.
     *
     * @param initialDuration  The number of milliseconds that must elapse
     *                         before the first timer expiration notification.
     * @param intervalDuration The number of milliseconds that must elapse
     *                         between timer expiration notifications.  Expiration notifications are
     *                         scheduled relative to the time of the first expiration.  If expiration
     *                         is delayed (e.g. due to the interleaving of other method calls on the
     *                         bean), two or more expiration notifications may occur in close
     *                         succession to "catch up".
     * @param info             application information to be delivered along
     *                         with the timer expiration. This can be null.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If initialDuration is
     *                                  negative or intervalDuration is negative.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     */
    @Override
    public Timer createTimer(long initialDuration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create an interval timer whose first expiration occurs after a specified
     * duration, and whose subsequent expirations occur after a specified
     * interval.
     *
     * @param initialDuration  The number of milliseconds that must elapse
     *                         before the first timer expiration notification.
     * @param intervalDuration The number of milliseconds that must elapse
     *                         between timer expiration notifications.  Expiration notifications are
     *                         scheduled relative to the time of the first expiration.  If expiration
     *                         is delayed (e.g. due to the interleaving of other method calls on the
     *                         bean), two or more expiration notifications may occur in close
     *                         succession to "catch up".
     * @param timerConfig      timer configuration
     * @return the newly created Timer.
     * @throws IllegalArgumentException If initialDuration is
     *                                  negative or intervalDuration is negative.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     * @since EJB 3.1
     */
    @Override
    public Timer createIntervalTimer(long initialDuration, long intervalDuration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create a single-action timer that expires at a given point in time.
     *
     * @param expiration The point in time at which the timer must expire.
     * @param info       application information to be delivered along
     *                   with the timer expiration notification. This can be null.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If expiration is null or
     *                                  expiration.getTime() is negative.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     */
    @Override
    public Timer createTimer(Date expiration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create a single-action timer that expires at a given point in time.
     *
     * @param expiration  the point in time at which the timer must expire.
     * @param timerConfig timer configuration.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If expiration is null or
     *                                  expiration.getTime() is negative.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     * @since EJB 3.1
     */
    @Override
    public Timer createSingleActionTimer(Date expiration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create an interval timer whose first expiration occurs at a given
     * point in time and whose subsequent expirations occur after a specified
     * interval.
     *
     * @param initialExpiration the point in time at which the first timer
     *                          expiration must occur.
     * @param intervalDuration  the number of milliseconds that must elapse
     *                          between timer expiration notifications.  Expiration notifications are
     *                          scheduled relative to the time of the first expiration.  If expiration
     *                          is delayed (e.g. due to the interleaving of other method calls on the
     *                          bean), two or more expiration notifications may occur in close
     *                          succession to "catch up".
     * @param info              application information to be delivered along
     *                          with the timer expiration. This can be null.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If
     *                                  initialExpiration is null, if initialExpiration.getTime() is
     *                                  negative, or if intervalDuration is negative.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     */
    @Override
    public Timer createTimer(Date initialExpiration, long intervalDuration, Serializable info) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create an interval timer whose first expiration occurs at a given
     * point in time and whose subsequent expirations occur after a specified
     * interval.
     *
     * @param initialExpiration the point in time at which the first timer
     *                          expiration must occur.
     * @param intervalDuration  the number of milliseconds that must elapse
     *                          between timer expiration notifications.  Expiration notifications are
     *                          scheduled relative to the time of the first expiration.  If expiration
     *                          is delayed (e.g. due to the interleaving of other method calls on the
     *                          bean), two or more expiration notifications may occur in close
     *                          succession to "catch up".
     * @param timerConfig       timer configuration.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If
     *                                  initialExpiration is null, if initialExpiration.getTime() is
     *                                  negative, or if intervalDuration is negative.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     * @since EJB 3.1
     */
    @Override
    public Timer createIntervalTimer(Date initialExpiration, long intervalDuration, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create a calendar-based timer based on the input schedule expression.
     *
     * @param schedule a schedule expression describing the timeouts
     *                 for this timer.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If Schedule represents an
     *                                  invalid schedule expression.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     * @since EJB 3.1
     */
    @Override
    public Timer createCalendarTimer(ScheduleExpression schedule) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Create a calendar-based timer based on the input schedule expression.
     *
     * @param schedule    a schedule expression describing the timeouts for this timer.
     * @param timerConfig timer configuration.
     * @return the newly created Timer.
     * @throws IllegalArgumentException If Schedule represents an
     *                                  invalid schedule expression.
     * @throws IllegalStateException    If this method is
     *                                  invoked while the instance is in a state that does not allow access
     *                                  to this method.
     * @throws EJBException             If this method could not complete
     *                                  due to a system-level failure.
     * @since EJB 3.1
     */
    @Override
    public Timer createCalendarTimer(ScheduleExpression schedule, TimerConfig timerConfig) throws IllegalArgumentException, IllegalStateException, EJBException {
        return null;
    }

    /**
     * Returns all active timers associated with this bean. These include both the
     * programmatically-created timers and the automatically-created timers.
     *
     * @return a collection of <code>javax.ejb.Timer</code> objects.
     * @throws IllegalStateException If this method is
     *                               invoked while the instance is in a state that does not allow access
     *                               to this method.
     * @throws EJBException          If this method could not complete
     *                               due to a system-level failure.
     */
    @Override
    public Collection<Timer> getTimers() throws IllegalStateException, EJBException {
        return null;
    }

    /**
     * Returns all active timers associated with the beans in the same module in
     * which the caller bean is packaged. These include both the
     * programmatically-created timers and the automatically-created timers.
     *
     * @return a collection of <code>javax.ejb.Timer</code> objects.
     * @throws IllegalStateException If this method is
     *                               invoked while the instance is in a state that does not allow access
     *                               to this method.
     * @throws EJBException          If this method could not complete
     *                               due to a system-level failure.
     * @since EJB 3.2
     */
    @Override
    public Collection<Timer> getAllTimers() throws IllegalStateException, EJBException {
        return null;
    }
}
