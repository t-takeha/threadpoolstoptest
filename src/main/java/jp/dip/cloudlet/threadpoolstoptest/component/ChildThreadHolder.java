package jp.dip.cloudlet.threadpoolstoptest.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AsyncCaller2Serviceを動かしているThreadを保持するクラス
 */
@Component
public class ChildThreadHolder {

    private Logger log = LogManager.getLogger();

    private Object lock = new Object();
    private ConcurrentHashMap<Integer, Thread> threadMap = new ConcurrentHashMap<>();

    public void register(int key, Thread thread) {
        synchronized (lock) {
            threadMap.put(key, thread);
        }
    }

    public void unRegister(int key) {
        synchronized (lock) {
            threadMap.remove(key);
        }
    }

    public void interruptThread(int key) {
        synchronized (lock) {
            Thread child = threadMap.get(key);
            if (child != null) {
                log.info("### FOUND : {}, {}", key, child);
                // スレッドに割り込みをかける
                child.interrupt();
                //child.stop();
            }
        }
    }
}
