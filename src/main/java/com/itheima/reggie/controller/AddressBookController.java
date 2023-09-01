package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lh
 * @Date 2023/8/8 9:38
 * @ 意图：
 */
@Controller
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    @ResponseBody
    public R<AddressBook> addressBook(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    @PutMapping("/default")
    @ResponseBody
    public R<AddressBook> updateDefault (@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId, userId)
                .ne(AddressBook::getId, addressBook.getId())
                .set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/list")
    @ResponseBody
    public R<List<AddressBook>> list(AddressBook addressBook){
//        设置userId,用于查询该用户下所有地址
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("address={}",addressBook);
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookService.list(queryWrapper);
        return R.success(addressBookList);
    }

    @GetMapping("/default")
    @ResponseBody
    public R<AddressBook> getDefault(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault,1);
        queryWrapper.eq(AddressBook::getUserId,userId);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        return R.success(addressBook);
    }

    @GetMapping("{id}")
    public R<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }
}
