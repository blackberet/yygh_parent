package com.atguigu.yygh.hosp;

import com.atguigu.yygh.hosp.bean.User;
import com.mongodb.client.result.DeleteResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootTest
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * @Document
     * @Id
     * @Field
     */
    /**
     * 新增insert, save都可以
     * save可以新增也可以修改,修改的时候会覆盖之前的,如果只修改某些字段,可以先查询出来,然后修改字段内容,再写回去
     * 批量添加数据只能用insert
     *
     */
    @Test
    public void insertTest(){
        User user = new User("1","张三",25,"女");
        User insert = mongoTemplate.insert(user);
        System.out.println("insert = " + insert);
    }

    @Test
    public void saveTest(){
        User user = new User("1","李四",20,"男");
        User save = mongoTemplate.save(user);
        System.out.println("save = " + save);
    }

    //批量添加
    @Test
    public void insertBatchTest(){
        ArrayList<User> list = new ArrayList<>();
        list.add(new User(null,"李云龙",40,"男"));
        list.add(new User(null,"张大彪",35,"男"));
        list.add(new User(null,"田雨",30,"女"));
        list.add(new User(null,"楚云飞",45,"男"));
        list.add(new User(null,"赵刚",45,"男"));
        mongoTemplate.insert(list,User.class);
    }

    //删除
    @Test
    public void removeTest(){
        //删除年龄为25的记录
        //Query query = new Query(Criteria.where("age").is(25));
        //删除age=25 and name=田雨
        //Query query = new Query(Criteria.where("age").is(30).and("name").is("田雨"));
        //删除age35或者name李云龙
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("age").is(35),Criteria.where("name").is("李云龙"));
        Query query = new Query(criteria);
        DeleteResult remove = mongoTemplate.remove(query,User.class);
        System.out.println("remove = " + remove);
    }

    /**
     * upsert:修改,若修改的记录不存在,则新增
     * save:
     * updateFirst 只修改符合条件的第一个文档
     * updateMulti 修改符合条件的全部文档
     */
    //修改,若修改的记录不存在,则新增
    @Test
    public void updateTest(){
        Query query = new Query(Criteria.where("name").is("孙德胜"));
        Update update = new Update();
        update.set("age",50);
        mongoTemplate.upsert(query,update,User.class);
    }

    //age=45的全部修改成46
    @Test
    public void updateMultiTest(){
        Query query = new Query(Criteria.where("age").is(45));
        Update update = new Update();
        update.set("age",46);
        mongoTemplate.updateMulti(query,update,User.class);
    }

    //age=45符合条件的第一个
    @Test
    public void updateFirstTest(){
        Query query = new Query(Criteria.where("age").is(46));
        Update update = new Update();
        update.set("age",47);
        mongoTemplate.updateFirst(query,update,User.class);
    }


    //根据id查询
    @Test
    public void findByIdTest(){
        User user = mongoTemplate.findById("636614b3a8a94f6414983d07", User.class);
        System.out.println("user = " + user);
    }

    //条件查询一条
    @Test
    public void find(){
        Query query = new Query(Criteria.where("age").is(47));
        User user = mongoTemplate.findOne(query, User.class);
        System.out.println(user);
    }

    //条件查询多条
    @Test
    public void findListTest(){
        Query query = new Query(Criteria.where("gender").is("男"));
        List<User> users = mongoTemplate.find(query, User.class);
        for (User user : users) {
            System.out.println(user);
        }
    }

    //模糊查询
    @Test
    public void mohuTest(){
        //Query query = new Query(Criteria.where("name").regex(".*李.*"));
        String str = String.format("%s%s%s", ".*", "李", ".*");
        //Pattern pattern = Pattern.compile(str);
        Pattern pattern = Pattern.compile(str,Pattern.CASE_INSENSITIVE);//不区分大小写
        Query query = new Query(Criteria.where("name").regex(pattern));
        List<User> userList = mongoTemplate.find(query, User.class);
        for (User user : userList) {
            System.out.println(user);
        }
    }

    //查全部
    @Test
    public void findAllTest(){
        List<User> userList = mongoTemplate.findAll(User.class);
        for (User user : userList) {
            System.out.println(user);
        }
    }

    //分页查询
    @Test
    public void pageTest(){
        Integer pageNum = 2;
        Integer pageSize = 3;
        HashMap<String, Object> map = new HashMap<>();
        Query query = new Query(Criteria.where("gender").is("男"));
        long total = mongoTemplate.count(query, User.class);
        List<User> list = mongoTemplate.find(query.skip((pageNum - 1) * pageSize).limit(pageSize), User.class);
        map.put("list",list);
        map.put("total",total);

        System.out.println(total);
        for (User user : list) {
            System.out.println(user);
        }
    }

}
