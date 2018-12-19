package com.liang.kafka.service;

import com.liang.kafka.excel.ExcelData;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author lianghaiyang
 * @date 2018/11/22
 */
@Service
public class KafkaProducerService {

    @Resource
    private Executor executor;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    /**
     *  发送消息
     */
    public void send(ExcelData excelData) {
        List<List<Object>> rows = excelData.getRows();
        // do something
        for (List<Object> row : rows) {
            executor.execute(() -> {
                String msg = row.stream().map(String::valueOf).collect(Collectors.joining(","));
                kafkaTemplate.sendDefault(msg);
            });
        }
    }
}
