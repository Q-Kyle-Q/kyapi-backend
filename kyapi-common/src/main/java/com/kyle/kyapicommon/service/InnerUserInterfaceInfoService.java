package com.kyle.kyapicommon.service;

import com.kyle.kyapicommon.model.entity.UserInterfaceInfo;

/**
 *
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);

    boolean leftInvokeCount(long interfaceInfoId, long userId);
}
