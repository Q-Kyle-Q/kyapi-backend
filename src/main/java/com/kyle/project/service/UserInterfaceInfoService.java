package com.kyle.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kyle.kyapicommon.model.entity.UserInterfaceInfo;

/**
 *
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 是否剩余调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean leftInvokeCount(long interfaceInfoId, long userId);
}
