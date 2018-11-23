package com.liang.kafka.core;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

/**
 * @author lianghaiyang
 * @date 2018/11/22
 */
@Slf4j
public class KafkaProducerMsgDispatcher extends Thread {
    private BlockingQueue<String> blockingQueue;

    public KafkaProducerMsgDispatcher(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        while (true) {
            String msg = this.blockingQueue.poll();
            if (msg != null) {
                log.info("接收到kafka消息: " + msg);
            } else {
                try {
                    // 线程重新争抢cpu执行权, 将cpu资源让给其它线程
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
