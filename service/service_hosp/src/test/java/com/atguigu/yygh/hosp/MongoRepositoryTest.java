package com.atguigu.yygh.hosp;

import com.atguigu.yygh.hosp.bean.User;
import com.atguigu.yygh.hosp.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import sun.security.krb5.internal.crypto.NullEType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class MongoRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    /**
     * 新增insert,save
     * insert只能新增不能修改
     * 使用save做修改时,要先查询,在修改
     *
     *
     */
    @Test
    public void saveTest(){
        User user = new User(null, "和尚", 40, "男");
        User save = userRepository.save(user);
        System.out.println("save = " + save);
    }

    @Test
    public void insertTest(){
        User user = new User(null, "常乃超", 40, "男");
        userRepository.insert(user);
    }

    //save修改
    @Test
    public void insertTest2(){
        User user = userRepository.findById("636614b3a8a94f6414983d07").get();
        user.setName("李云龙");
        userRepository.save(user);
    }

    @Test
    public void insertTest3(){
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(null,"秀琴",30,"女"));
        users.add(new User(null,"丁伟",41,"男"));
        users.add(new User(null,"孔捷",41,"男"));
        userRepository.insert(users);
    }

    /**
     * 删除
     */
    @Test
    public void removeTest(){
        //deleteById只能根据id删除
        userRepository.deleteById("63685b86a4f5b42e667f2f68");
    }

    //批量删除,不加条件全删
    @Test
    public void removeAllTest(){
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("6368615790d1304e64b9f2f9","秀琴",30,"女"));
        users.add(new User("6368615790d1304e64b9f2fa","丁伟",41,"男"));
        users.add(new User("6368615790d1304e64b9f2fb","孔捷",41,"男"));
        userRepository.deleteAll(users);
    }

    /**
     * 修改
     */
    @Test
    public void updateTest(){
        User user = new User("63685b384b3e24031d740620", "和尚111", 401, "男");
        userRepository.save(user);
    }

    /**
     * 查询
     * findOne:查询符合条件的第一个
     */
    @Test
    public void findTest(){
        User user = new User();
        user.setAge(40);
        Example<User> example = Example.of(user);
        Optional<User> users = userRepository.findOne(example);
        User user1 = users.get();
        System.out.println(user1);
    }

    @Test
    public void findAll(){
        List<User> userList = userRepository.findAll();
        for (User user : userList) {
            System.out.println(user);
        }
    }

    //条件查询
    @Test
    public void findAll1(){
        Example<User> example = Example.of(new User(null,null,40,null));
        List<User> userList = userRepository.findAll(example);
        for (User user : userList) {
            System.out.println(user);
        }
    }

    //模糊查询
    @Test
    public void findAll2(){
        User user = new User(null,null,null,"男");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<User> example = Example.of(user,exampleMatcher);
        List<User> userList = userRepository.findAll(example);
        for (User user1 : userList) {
            System.out.println(user1);
        }
    }

    //分页查询
    @Test
    public void findAll3(){
        User user = new User(null, null, null, "男");
        Example<User> example = Example.of(user);
        int pageNum = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNum - 1,pageSize);
        Page<User> page = userRepository.findAll(example, pageable);
        System.out.println("总页数 = " + page.getTotalPages());
        System.out.println("总记录数 = " + page.getTotalElements());
        List<User> userList = page.getContent();
        for (User user1 : userList) {
            System.out.println(user1);
        }
    }

    //自定义精确查询
    @Test
    public void customMethodsTest(){
        List<User> userList = userRepository.findByName("李云龙");
        for (User user : userList) {
            System.out.println(user);
        }
    }

    //自定义模糊查询
    @Test
    public void  customMethodsTest2(){
        List<User> userList = userRepository.findByGenderLike("女");
        for (User user : userList) {
            System.out.println(user);
        }
    }

    //自定义多条件查询
    @Test
    public void customMethodsTest3(){
        List<User> userList = userRepository.findByGenderAndAgeBetween("男", 40, 50);
        for (User user : userList) {
            System.out.println(user);
        }
    }



}
