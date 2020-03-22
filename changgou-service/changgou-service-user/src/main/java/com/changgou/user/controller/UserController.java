package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.entity.BCrypt;
import com.changgou.entity.JwtUtil;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false)  User user, @PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<User>> findList(@RequestBody(required = false)  User user){
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  User user,@PathVariable String id){
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   User user){
        //调用UserService实现添加User
        userService.add(user);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable String id){
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true,StatusCode.OK,"查询成功",user);
    }

    /***
     * 查询User全部数据   该方法需要用户拥有了role_admin角色才有权限访问
     * @return
     */
    @GetMapping
    @PreAuthorize(value = "hasAnyAuthority('goods_list')")//这个注解表示在方法执行前进行校验,这里写的是表示拥有了这个角色的人才可以访问这个方法
    public Result<List<User>> findAll(){
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK,"查询成功",list) ;
    }

    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse httpServletResponse){//加入HttpServletResponse是为了给客户cookie中加入令牌并返回
        //1 根据用户名 查询用户是否存在
        //调用UserService实现根据主键查询User
        User user = userService.findById(username);
        //2 判断用户名是否存在 不存在直接返回
        if (user == null){
            return  new Result(false,StatusCode.LOGINERROR,"用户名或密码错误，登录失败！");
        }
        //3 判断密码是否正确 密码不正确 登录失败
        //把数据库里的密码（已经加密了）取出来 和页面传过来的密码进行匹配
        // 但是存在密码加密MD5 bcryt+盐 这个密码加密之后就不能解开了 只能再拿传过来的去加密
        if (!BCrypt.checkpw(password,user.getPassword())){//使用工具类 BCrypt.checkpw（）方法判断明文密码和数据库的暗文密码是否匹配
            return  new Result(false,StatusCode.LOGINERROR,"用户名或密码错误，登录失败！");
        }



        //用户输入信息正确 可以登录 这里给用户颁发令牌
        //定义json数据 map和json数据格式一致 所有就给map中添加数据
        Map<String,Object> subject = new HashMap<>();
        subject.put("username",username);
        subject.put("role","admin");
        subject.put("success","true");
        String jwt = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(subject), null);//创建一个令牌
        //令牌生成了 给客户放到cookie中
        Cookie cookie = new Cookie("Authorization",jwt);//cookie中的格式也是key value

        httpServletResponse.addCookie(cookie);//可以放到cookie中 也可以放到下面的结果参数中

        //4 登录成功
        return  new Result(true,StatusCode.OK,"登录成功！",jwt);//之后 还需要给用户返回一个令牌
    }


    /**
     * 独立给认证服务器使用的
     * @param id
     * @return
     */
    //user-api中userFeign的根据查询用户用户数据的方法
    @GetMapping("/load/{id}")
    public Result<User> findByUsername(@PathVariable(name = "id") String id){
        User user = userService.findById(id);
        return new Result<User>(true,StatusCode.OK,"查询成功",user);
    };

    //测试 BCryptPasswordEncoder加密
    public static void main(String[] args) {
        String encode = new BCryptPasswordEncoder().encode("123456");
        System.out.println(encode);
    }

    //userfeign调用的 添加积分功能
    @GetMapping(value = "/points/add")
    public Result addPoints(@RequestParam(value = "points") Integer points
            , @RequestParam(value = "username") String username){
         Integer count = userService.addPoint(points,username);
         if (count>=1){
             return new Result(true,StatusCode.OK,"积分添加成功");
         }else{
             return new Result(false,StatusCode.ERROR,"积分添加失败");
         }
    }


}
