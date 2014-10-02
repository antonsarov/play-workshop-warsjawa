package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;

/**
 * Created by Anton Sarov on 27.9.2014 г..
 */
@Entity
public class User extends Model {

    public static Finder<Long, User> find = new Finder<>(Long.class, User.class);

    @Id
    private Long id;

    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static User create(String username) {
        User user = new User();
        user.setUsername(username);
        try {
            user.save();
        } catch (PersistenceException pe) {
            return null;
        }
        return user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static User findByUsername(String username) {
        return find.where().eq("username", username).findUnique();
    }
}
