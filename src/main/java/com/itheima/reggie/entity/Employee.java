package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.itheima.reggie.common.MyMetaObjectHandle;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lh
 * @Date 2023/7/22 16:02
 * @ 意图：
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    /**
     * 用于自动填充，配合common中元数据处理器行处理
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
