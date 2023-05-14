package com.kyle.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kyle.kyapicommon.model.entity.InterfaceInfo;
import com.kyle.project.model.entity.Post;

/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add 是否为创建校验
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
