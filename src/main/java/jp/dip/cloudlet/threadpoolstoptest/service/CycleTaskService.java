package jp.dip.cloudlet.threadpoolstoptest.service;

import jp.dip.cloudlet.threadpoolstoptest.db.repository.TrantestRepository;
import jp.dip.cloudlet.threadpoolstoptest.db.entity.Trantest;
import jp.dip.cloudlet.threadpoolstoptest.event.ThreadRegisterEvent;
import jp.dip.cloudlet.threadpoolstoptest.event.ThreadUnRegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 子スレッドからEventを投げてThreadを覚える方式
 * (note) 問題点：UnRegistEventを受けてMapを更新するより前に子スレッドが先に別処理に割り振られるのを制しできない。
 */
@Service
public class CycleTaskService {
    private Logger log = LogManager.getLogger();

    @Value("${app.watchdir}")
    private String watchdir;

    @Autowired
    TrantestRepository repository;

    @Autowired
    AsyncCallerService asyncCallerService;

    private int counter = 0;

    private Object lock = new Object();

    private Map<Integer, Thread> threadMap = new LinkedHashMap<>();

    // 有効にしたければ以下のコメントを外す
//    @Scheduled(fixedRate = 2000L, initialDelay = 2000L)
    public void execute() {
        // 監視チェック
        checkdir();

        // レコードの存在チェック
        Trantest rec = repository.find(++counter);
        if (rec == null) {
            // レコードを登録する(トランザクションはすぐに終了）
            rec = repository.register("first");
        }

        // 子スレッドを呼び出す
        CompletableFuture<Long> cf = asyncCallerService.execTask(rec.getId());
        cf.whenComplete((ret, ex) -> {
            if (ex == null) {
//               log.info("### child success : {}", ret);
            } else {
                log.info("### child failed  : {},{}", ret, ex);
            }
        });
    }

    @EventListener
    public void onThreadRegistEvent(ThreadRegisterEvent event) {
        //log.info("### ThreadRegistEvent:  {}, {}", event.getId(), event.getThread().toString());
        synchronized (lock) {
            threadMap.put(event.getId(), event.getThread());
        }
    }

    @EventListener
    public void onThreadUnRegistEvent(ThreadUnRegisterEvent event) {
        log.info("### ThreadUnRegistEvent: {}, {}", event.getId(), event.getThread().toString());
        synchronized (lock) {
            threadMap.remove(event.getId());
        }
    }

    private void checkdir() {
        File dir = new File(watchdir);
        for (File f : dir.listFiles()) {
            String name = f.getName();

            try {
                int id = Integer.parseInt(name);
                synchronized (lock) {
                    Thread child = threadMap.get(id);
                    if (child != null) {
                        log.info("### FOUND : {}, {}", id, child);
                        // スレッドに割り込みをかける
                        child.interrupt();
                    } else {
//                        log.info("### NOT found thread : {}", id);
                    }
                }
            } catch (NumberFormatException e) {
                // 例外が起きたら何もしない
            }
        }
    }
}
