# threadpoolstoptest
ThreadPoolTaskExecutorのworkerスレッドを無理矢理外部から止める方法の検証

■方式1  
(1) @Asyncが付いたメソッドの頭でキーとThreadを持つ登録Eventをpublishする  
(2) 同メソッドの末尾で、キーとThreadを持つ登録解除Eventをpublishする  
(3) 上記のEventを受けるリスナーを定義して、キーとThreadをMapに記録しておく。  
(4) 監視ディレクトリにキー名のファイルが置かれたら、MapからThreadを取り出して、interruptする  

■方式２  
上記のEventをコンポーネントメソッド呼び出しに変えただけ


【結論】  
Event方式だと登録解除Event直前、または、子スレッドがすでに別のタスクに割り当てられた後で、
interruptされる可能性があるので、あまりよろしくない。  
コンポーネント方式ではその可能性はないが、interruptすると、Atomikos＋Oracleが悲鳴を上げて、
Atomikosのトランザクションログが100%の再現率で壊れてしまう。JVMをリスタートしなさいとメッセージが出てきている。  
さすがにこれはダメだ。クリティカルセッションとして保護したとしても、そもそもOracleの低速Queryを割り込みたいので本末転倒。  

以上により、これらの方式でThreadを止めるのはやめたほうがいい。  
Thread#stopも同様に、Atomikos+Oracleがおかしくなる。そもそも、stopするとヒープもおかしくなるので使えたものではない。
