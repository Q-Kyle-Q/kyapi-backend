package com.kyle.project.service.impl.inner;

import com.kyle.kyapicommon.model.entity.UserInterfaceInfo;
import com.kyle.kyapicommon.service.InnerUserInterfaceInfoService;
import com.kyle.project.common.ErrorCode;
import com.kyle.project.exception.BusinessException;
import com.kyle.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    @Override
    public boolean leftInvokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.leftInvokeCount(interfaceInfoId, userId);
    }
}
