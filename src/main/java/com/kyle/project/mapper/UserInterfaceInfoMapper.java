package com.kyle.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kyle.project.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * @Entity com.yupi.project.model.entity.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




