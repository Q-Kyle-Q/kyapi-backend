package com.kyle.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kyle.kyapicommon.model.entity.UserInterfaceInfo;

import java.util.HashMap;
import java.util.List;

/**
 * @Entity com.yupi.project.model.entity.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

    boolean interfaceCount(int count);

    int buyInterface(Long userId, Long interfaceId, int count);

    boolean addNewInterface(HashMap map);
}




