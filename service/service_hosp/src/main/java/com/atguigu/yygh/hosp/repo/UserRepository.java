package com.atguigu.yygh.hosp.repo;

import com.atguigu.yygh.hosp.bean.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    public List<User> findByName(String name);

    public List<User> findByGenderLike(String gender);

    public List<User> findByGenderAndAgeBetween(String gender,Integer min,Integer max);
}
