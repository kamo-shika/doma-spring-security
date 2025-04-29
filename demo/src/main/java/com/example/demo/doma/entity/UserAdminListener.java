package com.example.demo.doma.entity;

import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

/**
 * 
 */
public class UserAdminListener implements EntityListener<UserAdmin> {

    @Override
    public void preInsert(UserAdmin entity, PreInsertContext<UserAdmin> context) {
    }

    @Override
    public void preUpdate(UserAdmin entity, PreUpdateContext<UserAdmin> context) {
    }

    @Override
    public void preDelete(UserAdmin entity, PreDeleteContext<UserAdmin> context) {
    }

    @Override
    public void postInsert(UserAdmin entity, PostInsertContext<UserAdmin> context) {
    }

    @Override
    public void postUpdate(UserAdmin entity, PostUpdateContext<UserAdmin> context) {
    }

    @Override
    public void postDelete(UserAdmin entity, PostDeleteContext<UserAdmin> context) {
    }
}
