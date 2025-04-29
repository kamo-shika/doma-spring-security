package com.example.demo.doma.entity;

import java.time.LocalDateTime;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Id;
import org.seasar.doma.Metamodel;
import org.seasar.doma.Table;

/**
 */
@Entity(listener = UserListener.class, metamodel = @Metamodel)
@Table(name = "user")
public class User extends AbstractUser {

    /** */
    @Id
    @Column(name = "user_id")
    String userId;

    /** */
    @Column(name = "user_name")
    String userName;

    /** */
    @Column(name = "email")
    String email;

    /** */
    @Column(name = "password_hash")
    String passwordHash;

    /** */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /** */
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    /** 
     * Returns the userId.
     * 
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /** 
     * Sets the userId.
     * 
     * @param userId the userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** 
     * Returns the userName.
     * 
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /** 
     * Sets the userName.
     * 
     * @param userName the userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** 
     * Returns the email.
     * 
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /** 
     * Sets the email.
     * 
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** 
     * Returns the passwordHash.
     * 
     * @return the passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** 
     * Sets the passwordHash.
     * 
     * @param passwordHash the passwordHash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** 
     * Returns the createdAt.
     * 
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** 
     * Sets the createdAt.
     * 
     * @param createdAt the createdAt
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /** 
     * Returns the updatedAt.
     * 
     * @return the updatedAt
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /** 
     * Sets the updatedAt.
     * 
     * @param updatedAt the updatedAt
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
