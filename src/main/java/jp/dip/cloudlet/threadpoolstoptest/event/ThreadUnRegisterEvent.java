package jp.dip.cloudlet.threadpoolstoptest.event;

import org.springframework.context.ApplicationEvent;

public class ThreadUnRegisterEvent extends ApplicationEvent {
    private Thread thread;
    private int id;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ThreadUnRegisterEvent(Object source, Thread thread, int id) {
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
