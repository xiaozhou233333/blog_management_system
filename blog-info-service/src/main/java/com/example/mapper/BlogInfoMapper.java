package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.BlogInfo;

@Mapper
public interface BlogInfoMapper extends BaseMapper<BlogInfo> {

}
