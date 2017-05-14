package example.com.xinyuepleayer.bean;

import java.io.Serializable;

/**
 * Created by 82165 on 2017/5/4.
 */
public class UserBean implements Serializable{
    private String name;
    private String phone;
    private String pass;

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
