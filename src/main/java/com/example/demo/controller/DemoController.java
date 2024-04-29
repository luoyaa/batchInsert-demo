package com.example.demo.controller;

import cn.hutool.core.date.DateUtil;
import com.example.demo.entity.UserInfo;
import com.example.demo.repo.UserInfoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author luoxuri
 * @date 2024-03-25 17:56:19
 */
@Slf4j
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private UserInfoRepo userInfoRepo;

    /**
     * 单条循环插入
     * 耗时长
     */
    @PostMapping("/insert")
    public void insert() {
        log.info("开始插入数据, 时间：{}", this.currentTime());
        List<UserInfo> list = this.initList(10000);
        for (UserInfo userInfo : list) {
            this.userInfoRepo.insert(userInfo);
        }
        log.info("结束插入数据, 时间：{}", this.currentTime());
    }

    /**
     * 批量插入1
     * 一次性全部批量插入
     * 数据包超过了mysql配置的最大限制，就会报错
     */
    @PostMapping("/insert/batch1")
    public void insertBatch1() {
        List<UserInfo> userInfoList = this.initList(1000000);
        log.info("开始插入数据, 时间：{}", this.currentTime());
        this.processBatch1(userInfoList);
        log.info("结束插入数据, 时间：{}", this.currentTime());
    }

    /**
     * 批量插入2
     * 分批插入，每次插入2000条
     * 耗时较长，需要提前计算好2000条数据包是否超过了mysql配置的最大限制（大多数情况下不会超过）
     * 相比批量插入1，耗时提升明显
     */
    @PostMapping("/insert/batch2")
    public void insertBatch2() {
        List<UserInfo> userInfoList = this.initList(1000000);
        log.info("开始插入数据, 时间：{}", this.currentTime());
        this.processBatch2(userInfoList, 2000);
        log.info("结束插入数据, 时间：{}", this.currentTime());
    }

    /**
     * 批量插入3
     * 线程池批量插入，每个线程插入2000条
     * 耗时短，需要提前计算好2000条数据包是否超过了mysql配置的最大限制（大多数情况下不会超过）
     * 相比批量插入2，耗时提升明显
     */
    @PostMapping("/insert/batch3")
    public void insertBatch3() {
        List<UserInfo> userInfoList = this.initList(1000000);
        log.info("开始插入数据, 时间：{}", this.currentTime());
        this.processBatch3(userInfoList, 2000);
        log.info("结束插入数据, 时间：{}", this.currentTime());
    }

    private void processBatch3(List<UserInfo> list, int batchSize) {
        // 根据CPU核数，创建对应数量的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int fromIndex = 0; fromIndex < list.size(); fromIndex += batchSize) {
            // 计算本次处理的结束索引，保证不超过实际列表大小
            int toIndex = Math.min(fromIndex + batchSize, list.size());
            // 获取当前批次的数据子集
            List<UserInfo> batchList = list.subList(fromIndex, toIndex);

            executorService.submit(() -> processBatch1(batchList));
        }
        // 关闭线程池
        executorService.shutdown();
        try {
            // 具体等待超时的时间，请根据任务平均执行时间、最大可接受延迟、资源释放需求、系统稳定性设置
            if (executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (Exception e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void processBatch2(List<UserInfo> list, int batchSize) {
        for (int fromIndex = 0; fromIndex < list.size(); fromIndex += batchSize) {
            // 计算本次处理的结束索引，保证不超过实际列表大小
            int toIndex = Math.min(fromIndex + batchSize, list.size());
            // 获取当前批次的数据子集
            List<UserInfo> batchList = list.subList(fromIndex, toIndex);

            this.processBatch1(batchList);
        }
    }

    private void processBatch1(List<UserInfo> list) {
        this.userInfoRepo.insertBatch(list);
    }

    private List<UserInfo> initList(int fillSize) {
        List<UserInfo> list = new ArrayList<>();
        for (int i = 0; i < fillSize; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setNumber(UUID.randomUUID().toString());
            userInfo.setName("哎呦呵-" + i);
            list.add(userInfo);
        }
        log.info("填充完毕");
        return list;
    }

    private String currentTime() {
        return DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");
    }
}
