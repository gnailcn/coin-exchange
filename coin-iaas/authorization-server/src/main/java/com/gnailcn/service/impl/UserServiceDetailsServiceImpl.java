package com.gnailcn.service.impl;

import com.gnailcn.constant.LoginConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String loginType = requestAttributes.getRequest().getParameter("login_type"); // 区分时后台人员还是我们的用户登录
        if (StringUtils.isEmpty(loginType)) {
            throw new AuthenticationServiceException("登录类型不能为null");
        }
        UserDetails userDetails = null;
        try{
            String grantType = requestAttributes.getRequest().getParameter("grant_type");   //refresh_token进行纠正
            if(LoginConstant.REFRESH_TYPE.equals(grantType.toUpperCase())){
                username = adjustUsername(username, loginType);
            }
            switch (loginType) {
                case LoginConstant.ADMIN_TYPE:
                    userDetails = loadSysUserByUsername(username);
                    break;
                case LoginConstant.MEMBER_TYPE:
                    userDetails = loadMemberUserByUsername(username);
                    break;
                default:
                    throw new AuthenticationServiceException("登录类型不支持: " + loginType);
            }
        }catch(DataAccessException e){
            throw new UsernameNotFoundException("用户：" + username + "不存在");
        }
        return userDetails;
    }

    private String adjustUsername(String username, String loginType)
    {
        if(LoginConstant.ADMIN_TYPE.equals(loginType))
        {
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_USER_WITH_ID, String.class, username);
        }
        if (LoginConstant.MEMBER_TYPE.equals(loginType)) {
            return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_USER_WITH_ID, String.class, username);
        }
        return username;
    }

    //后台管理员登录
    private UserDetails loadSysUserByUsername(String username) {
        //注意jdbcTemplate.queryForObject()方法，如果查询结果为空，会抛出EmptyResultDataAccessException异常
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_ADMIN_SQL, new RowMapper<User>(){

            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                if (rs.wasNull()) {
                    throw new UsernameNotFoundException("用户：" + username + "不存在");
                }
                Long id = rs.getLong("id");
                String password = rs.getString("password");
                int status = rs.getInt("status");
                User user = new User(
                        String.valueOf(id), // 使用用户的id 代替用户的名称，这样会使得后面的很多情况得以处理
                        password,
                        status == 1,
                        true,
                        true,
                        true,
                        getSysUserPermissions(id));
                return user;

            }
        }, username);
    }

    //通过用户的id 获取用户的权限
    private Collection<? extends GrantedAuthority> getSysUserPermissions(Long id) {
        // 查询用户是否为管理员
        String roleCode = jdbcTemplate.queryForObject(LoginConstant.QUERY_ROLE_CODE_SQL, String.class, id);
        List<String> permissions = null;
        //1.当用户是管理员时，他拥有所有的的权限权限
        if(LoginConstant.ADMIN_ROLE_CODE.equals(roleCode))
        {
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_ALL_PERMISSIONS, String.class);
        }
        else {  //2.当用户不是管理员时，需要使用角色->权限数据
            permissions = jdbcTemplate.queryForList(LoginConstant.QUERY_PERMISSION_SQL, String.class, id);
        }
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptySet();
        }
        return permissions
                .stream()
                .distinct() // 去重
                .map(
                        perm -> new SimpleGrantedAuthority(perm) // perm - >security可以识别的权限
                )
                .collect(Collectors.toSet());
    }

    //会员登录
    private UserDetails loadMemberUserByUsername(String username) {
        return jdbcTemplate.queryForObject(LoginConstant.QUERY_MEMBER_SQL, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                if(rs.wasNull()){
                    throw new UsernameNotFoundException("会员：" + username + "不存在");
                }
                long id = rs.getLong("id"); // 获取用户的id
                String password = rs.getString("password");
                int status = rs.getInt("status");
                return new User(
                        String.valueOf(id),
                        password,
                        status == 1 ,
                        true ,
                        true ,
                        true,
                        Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
        }, username, username);
    }
}
