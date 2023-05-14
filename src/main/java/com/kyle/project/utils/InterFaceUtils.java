package com.kyle.project.utils;

import com.kyle.project.common.ErrorCode;
import com.kyle.project.exception.BusinessException;
import com.kyle.project.mapper.UserInterfaceInfoMapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.kyle.project.constant.UserConstant.INTERFACE_COUNT;


@Component
public class InterFaceUtils {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private RedissonClient redissonClient;



    /**
     *
     * 默认每天给用户分配10次调用接口的次数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void interfaceCount() {
        RLock lock = redissonClient.getLock("api:interface:lock");
        //更新调用接口次数
        try {
            //只有一个线程能取到锁
            if (lock.tryLock(0,-1, TimeUnit.MINUTES)) {
                boolean isInterfaceCount = userInterfaceInfoMapper.interfaceCount(INTERFACE_COUNT);
                System.out.println("getLock:" + Thread.currentThread().getId());

            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        } finally {
            //只能释放线程自己的锁
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }

        }

    }
}