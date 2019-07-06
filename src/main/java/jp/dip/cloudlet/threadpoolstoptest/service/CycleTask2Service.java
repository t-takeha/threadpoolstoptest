package jp.dip.cloudlet.threadpoolstoptest.service;

import jp.dip.cloudlet.threadpoolstoptest.component.ChildThreadHolder;
import jp.dip.cloudlet.threadpoolstoptest.db.entity.Trantest;
import jp.dip.cloudlet.threadpoolstoptest.db.repository.TrantestRepository;
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
 * 子スレッドからコンポーネントを介してThreadを覚える方式.
 * (note)
 *  恐らく、Eventにあった問題はなくなったのだろうが、Thread#interruptするとAtomikosのXAが壊れてしまう。
 *  状況としては、あるレコードを別トランザクションからUPDATEしておき、子スレッド側でUPDATEをかけてロック待ちさせる。
 *  この状態で、Thread#interruptすると発生する。今のところ、100%の再現率。
 *  これが起きると、Atomikosのトランザクションログが壊れてしまうようで、あらゆるスレッドのSQL発行がすべて失敗する。
 *  JVMを再起動してというErrorレベルの例外も発生するのでもう全然ダメ。
 *  Atomikosの所ではなく、例えば、Thread#sleepしているときにinterruptされた場合にはうまく止まってくるが、そもそも
 *  sleepしている余裕なんてないし、最も止めたいのはAtomikosでOracleを見に行っているところなので、全然ダメ。
 *
 *  同様にThread#stopもおかしくなるこことがある。そもそも、Thread#stopをすると共有リソースがメモリリークしたり
 *  するので、絶対に使ってはダメなパターン。
 *
 * 以上により、外から子スレッドを強制的に殺すのはダメということがよく分かった。
 */
@Service
public class CycleTask2Service {
    private Logger log = LogManager.getLogger();

    @Value("${app.watchdir}")
    private String watchdir;

    @Autowired
    TrantestRepository repository;

    @Autowired
    AsyncCaller2Service asyncCaller;

    @Autowired
    ChildThreadHolder holder;

    private int counter = 0;

    @Scheduled(fixedRate = 2000L, initialDelay = 2000L)
    public void execute() {
        // レコードの存在チェック
        Trantest rec = repository.find(++counter);
        if (rec == null) {
            // レコードを登録する(トランザクションはすぐに終了）
            rec= repository.register("first");
        }

        // 子スレッドを呼び出す
        CompletableFuture<Long> cf = asyncCaller.execTask(rec.getId());
        cf.whenComplete((ret, ex) -> {
           if (ex != null) {
               log.info("### child failed  : {},{}", ret, ex);
           }
        });

        // 監視チェック
        checkdir();
    }

    private void checkdir() {
        File dir = new File(watchdir);
        for (File f : dir.listFiles()) {
            String name = f.getName();

            try {
                holder.interruptThread(Integer.parseInt(name));
            } catch (NumberFormatException e) {
                // 数値以外のファイルやディレクトリがあっても無視
            }
        }
    }
}
