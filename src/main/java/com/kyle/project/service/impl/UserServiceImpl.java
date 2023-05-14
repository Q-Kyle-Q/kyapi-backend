package com.kyle.project.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kyle.kyapicommon.model.entity.InterfaceInfo;
import com.kyle.kyapicommon.model.entity.User;
import com.kyle.project.common.BaseResponse;
import com.kyle.project.common.ErrorCode;
import com.kyle.project.common.ResultUtils;
import com.kyle.project.exception.BusinessException;
import com.kyle.project.mapper.InterfaceInfoMapper;
import com.kyle.project.mapper.UserInterfaceInfoMapper;
import com.kyle.project.mapper.UserMapper;
import com.kyle.project.model.dto.user.UserLoginRequest;
import com.kyle.project.model.vo.UserVO;
import com.kyle.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;

import static com.kyle.project.constant.UserConstant.*;


/**
 * 用户服务实现类
 *
 * @author kyle
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "kyle";

    private static final String USERNAME_PREFIX = "KyApi__";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userPhone) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //校验手机号
        //开头数字必须为1，第二位必须为3至9之间的数字，后九尾必须为0至9组织成的十一位电话号码
        if (!ReUtil.isMatch(MOBILE_REGEX, userPhone)) {

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误！");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();

            queryWrapper.eq("userAccount", userAccount);
            queryWrapper1.eq("userPhone", userPhone);

            long count = userMapper.selectCount(queryWrapper);
            long count1 = userMapper.selectCount(queryWrapper1);

            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            if (count1 > 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号已被注册！");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

            // 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));

            // 4. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserPhone(userPhone);
            user.setAccessKey(accessKey);
            user.setSecretKey(secretKey);
            user.setUserAvatar(USER_DEFAULT_AVATAR);
            user.setUserName(USERNAME_PREFIX +  RandomUtil.randomNumbers(4) + userAccount);
            boolean saveResult = this.save(user);

            // 5. 为新创建的用户添加所有的接口
            List<InterfaceInfo> interfaceInfos = interfaceInfoMapper.selectList(null);
            HashMap<Object, Object> map = new HashMap<>();
            map.put("userId", user.getId());
            for (InterfaceInfo interfaceInfo : interfaceInfos) {
                map.put("interfaceInfoId", interfaceInfo.getId());
                userInterfaceInfoMapper.addNewInterface(map);
            }

            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }


    @Override
    public BaseResponse<UserVO> codeLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null || request == null){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //手机号校验
        String userPhone = userLoginRequest.getUserPhone();
        //开头数字必须为1，第二位必须为3至9之间的数字，后九尾必须为0至9组织成的十一位电话号码
        String mobileRegEx = "^1[3,4,5,6,7,8,9][0-9]{9}$";
        if (!ReUtil.isMatch(mobileRegEx, userPhone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误！");
        }

        // 3.从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + userPhone);
        String code = userLoginRequest.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) {
            // 不一致，报错
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误！");
        }

        // 4.一致，根据手机号查询用户 select * from tb_user where phone = ?
        User user = query().eq("userPhone", userPhone).one();
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在！");
        }

        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);


        return ResultUtils.success(userVO);
    }

}



