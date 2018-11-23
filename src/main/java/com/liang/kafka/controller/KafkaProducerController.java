package com.liang.kafka.controller;

import com.liang.kafka.excel.ExcelColumnTypeEnum;
import com.liang.kafka.excel.ExcelData;
import com.liang.kafka.excel.ExcelUtils;
import com.liang.kafka.service.KafkaProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author lianghaiyang
 * @date 2018/11/22
 */
@RestController
public class KafkaProducerController {
    @Resource
    private KafkaProducerService kafkaProducerService;

    /**
     * 读取excel中的数据进行发送
     */
    @PostMapping("/excel/upload")
    public String sendMsg(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            assert fileName != null : "文件名不能为空!";
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            InputStream inputStream = file.getInputStream();
            // 从excel中获取数据, 封装入ExcelData
            List<ExcelColumnTypeEnum> cellTypes = Arrays.asList(ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING,
                    ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING,
                    ExcelColumnTypeEnum.STRING, ExcelColumnTypeEnum.STRING);
            ExcelData excelData = ExcelUtils.readExcel(inputStream, suffix, cellTypes);
            kafkaProducerService.send(excelData);
        } catch (IOException e) {
            e.printStackTrace();
            return "文件获取失败";
        }
        return "发送成功";
    }
}
