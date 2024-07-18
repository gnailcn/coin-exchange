package com.gnailcn.constant;

public class LoginConstant {

    //管理员登陆类型
    public static final String ADMIN_TYPE = "admin_type" ;

    //会员登陆类型
    public static final String MEMBER_TYPE  = "member_type" ;

    //使用用户名查询用户信息
    public static final String QUERY_ADMIN_SQL =
            "SELECT `id` ,`username`, `password`, `status` FROM sys_user WHERE username = ? ";

    //查询用户的角色code
    public static final String QUERY_ROLE_CODE_SQL =
            "SELECT `code` FROM sys_role LEFT JOIN sys_user_role ON sys_role.id = sys_user_role.role_id WHERE sys_user_role.user_id= ?";

    //查询所有的权限名称（针对管理员而言）
    public static final String QUERY_ALL_PERMISSIONS =
            "SELECT `name` FROM sys_privilege";

    //对于非管理员，需要先查询角色，再查询权限
    public static final String QUERY_PERMISSION_SQL =
            "SELECT 'name' FROM sys_privilege LEFT JOIN sys_role_privilege ON sys_role_privilege.privilege_id = sys_privilege.id LEFT JOIN sys_user_role  ON sys_role_privilege.role_id = sys_user_role.role_id WHERE sys_user_role.user_id = ?";

    public static final String ADMIN_ROLE_CODE = "ROLE_ADMIN";

    //查询会员信息
    public static final String QUERY_MEMBER_SQL = "SELECT `id`,`password`, `status` FROM `user` WHERE mobile = ? or email = ? ";

    public static final String REFRESH_TYPE = "REFRESH_TOKEN";

    /**
     * 使用管理员用户的id 查询用户名称
     */
    public static  final  String QUERY_ADMIN_USER_WITH_ID = "SELECT `username` FROM sys_user where id = ?" ;

    /**
     * 使用会员用户的id 查询用户名称
     */
    public static  final  String QUERY_MEMBER_USER_WITH_ID = "SELECT `mobile` FROM user where id = ?" ;

}
