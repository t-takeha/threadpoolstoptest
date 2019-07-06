package jp.dip.cloudlet.threadpoolstoptest.service;

import jp.dip.cloudlet.threadpoolstoptest.db.entity.Trantest;
import jp.dip.cloudlet.threadpoolstoptest.db.repository.TrantestRepository;
import jp.dip.cloudlet.threadpoolstoptest.event.ThreadRegisterEvent;
import jp.dip.cloudlet.threadpoolstoptest.event.ThreadUnRegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncCallerService {

    private Logger log = LogManager.getLogger();

    @Autowired
    TrantestRepository repository;

    @Autowired
    ApplicationEventPublisher publisher;

    @Transactional
    @Async
    public CompletableFuture<Long> execTask(int id) {
        log.info("$$$ START {}, {} $$$", id, Thread.currentThread());

        // このスレッドを登録するイベントを送信する
        publisher.publishEvent(new ThreadRegisterEvent(this, Thread.currentThread(), id));

        try {
//            TimeUnit.SECONDS.sleep(5);

            // テーブルを更新
            Trantest trantest = repository.find(id);

            trantest.setName(Thread.currentThread().toString());
            repository.save(trantest);

        } catch (Exception e) {
            //e.printStackTrace();
            log.info("$$$ INTERRUPTED {}, {} : {}$$$", id, Thread.currentThread(), e.getMessage());
            Thread.currentThread().interrupt();
        }
        // このスレッドを未登録にするイベントを送信する
        publisher.publishEvent(new ThreadUnRegisterEvent(this, Thread.currentThread(), id));

        return CompletableFuture.completedFuture(Long.valueOf(id));
    }
}
