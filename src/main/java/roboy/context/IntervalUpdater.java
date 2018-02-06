package roboy.context;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * An implementation of the UpdatePolicy which performs regular updates on a target object.
 * The method update() needs to be implemented in the subclass.
 *
 * @param <Target> The class of the target object.
 */
public abstract class IntervalUpdater<Target> extends ExternalUpdater {
    protected final Target target;
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static int updateFrequencySeconds = 1;

    /**
     * Create a new updater service, executing the update() method at regular time intervals.
     *
     * @param target                 The target attribute of the update() method.
     */
    public IntervalUpdater(Target target) {
        this.target = target;
        start();
    }

    /**
     * Starts the ScheduledExecutorService of the updating thread.
     */
    private void start() {
        final Runnable updater = () -> update();
        // Schedules regular updates, starting 1 second after initialization.
        final ScheduledFuture<?> updaterHandle = scheduler.scheduleAtFixedRate(
                updater, 1, updateFrequencySeconds, SECONDS);
        // UPDATE: cancel() KILLS THE UPDATER AFTER 30 SECONDS.
        // Updaters should run for the entire DM runtime, so do not kill them.
        // Leaving for reference.
        // scheduler.schedule((Runnable) () -> updaterHandle.cancel(true), 30, SECONDS);
    }

}
