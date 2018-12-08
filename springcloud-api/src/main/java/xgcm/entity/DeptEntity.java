package xgcm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author YXG
 * @Date 2018-11-06 21:23
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)

public class DeptEntity implements Serializable {
    private String deptName;
    private String deptId;
    private String dataSource;

    public static void main(String[] args) {

        // 链式调用
        DeptEntity zhangsan = new DeptEntity().setDeptName("张三").setDeptId("a");

        System.out.println("zhangsan = " + zhangsan);

    }
}
