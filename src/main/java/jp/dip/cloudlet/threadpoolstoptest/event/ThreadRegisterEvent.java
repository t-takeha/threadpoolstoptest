package jp.dip.cloudlet.threadpoolstoptest.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ThreadRegisterEvent extends ApplicationEvent {
    private Thread thread;
    private int id;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ThreadRegisterEvent(Object source, Thread thread, int id) {
        super(source);
        this.thread = thread;
        this.id = id;
    }

    public Thread getThread() {
        return this.thread;
    }

    public int getId() {
        return this.id;
    }
}
