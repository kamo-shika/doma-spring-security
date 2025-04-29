package com.example.demo.doma.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

import com.example.demo.doma.entity.UserAdmin;

/**
 */
@Dao
@ConfigAutowireable
public interface UserAdminDao {

    /**
     * @param userId
     * @return the UserAdmin entity
     */
    @Select
    UserAdmin selectById(String userId);

    /**
     * @param entity
     * @return affected rows
     */
    @Insert
    int insert(UserAdmin entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Update
    int update(UserAdmin entity);

    /**
     * @param entity
     * @return affected rows
     */
    @Delete
    int delete(UserAdmin entity);
}
