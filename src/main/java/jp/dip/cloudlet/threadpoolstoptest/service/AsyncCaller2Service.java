package jp.dip.cloudlet.threadpoolstoptest.service;

import jp.dip.cloudlet.threadpoolstoptest.component.ChildThreadHolder;
import jp.dip.cloudlet.threadpoolstoptest.db.entity.Trantest;
import jp.dip.cloudlet.threadpoolstoptest.db.repository.TrantestRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class AsyncCaller2Service {

    private Logger log = LogManager.getLogger();

    @Autowired
    TrantestRepository repository;

    @Autowired
    ChildThreadHolder holder;

    @Transactional
    @Async
    public CompletableFuture<Long> execTask(int id) {
        log.info("$$$ START {}, {}, {} $$$", id, Thread.currentThread(), Thread.currentThread().isInterrupted());

        // このスレッドを記録する
        holder.register(id, Thread.currentThread());

        try {
            //TimeUnit.SECONDS.sleep(5);

            // テーブルを更新
            Trantest trantest = repository.find(id);

            trantest.setName(Thread.currentThread().toString());
            repository.save(trantest);

        } catch (Exception e) {
            log.info("$$$ INTERRUPTED {}, {}, {}, {} $$$", id, Thread.currentThread(), Thread.currentThread().isInterrupted(), e.getMessage());
            Thread.currentThread().interrupt();
        }
        // このスレッドを未登録にする
        holder.unRegister(id);

        return CompletableFuture.completedFuture(Long.valueOf(id));
    }
}
