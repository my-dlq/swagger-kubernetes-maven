package mydlq.swagger.example;

import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

public class User {
    @ApiModelProperty(value = "姓名", required = true)
    private String name;
    @ApiModelProperty(value = "性别", required = true)
    private String sex;
    @ApiModelProperty(value = "岁数", required = true)
    private Integer age;
    @ApiModelProperty(value = "生日")
    private Date birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

}
