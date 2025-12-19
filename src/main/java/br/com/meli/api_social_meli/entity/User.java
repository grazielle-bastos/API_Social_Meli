package br.com.meli.api_social_meli.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "user_name")
    private String userName;

    public User() {
    }

    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
