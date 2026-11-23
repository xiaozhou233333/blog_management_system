package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.exception.BlogException;
import com.example.mapper.BlogInfoMapper;
import com.example.pojo.AddBlogInfoParam;
import com.example.pojo.BlogInfo;
import com.example.pojo.BlogInfoResponse;
import com.example.pojo.UpBlogParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {
    private final BlogInfoMapper blogInfoMapper;

    @Override
    public List<BlogInfoResponse> getList() {
        LambdaQueryWrapper<BlogInfo> wrapper = new LambdaQueryWrapper<BlogInfo>();
        wrapper.eq(BlogInfo::getDeleteFlag, 0).orderByDesc(BlogInfo::getId);
        List<BlogInfo> blogInfos = blogInfoMapper.selectList(wrapper);
        return blogInfos.stream().map(blogInfo -> {
            BlogInfoResponse response = new BlogInfoResponse();
            BeanUtils.copyProperties(blogInfo, response);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public BlogInfoResponse getBlogDetail(Integer blogId) {
        BlogInfo blogInfo = getBlogInfo(blogId);
        if (blogInfo == null) {
            throw new BlogException("Blog does not exist!");
        }
        BlogInfoResponse blogInfoResponse = new BlogInfoResponse();
        BeanUtils.copyProperties(blogInfo, blogInfoResponse);
        return blogInfoResponse;
    }

    public BlogInfo getBlogInfo(Integer blogId) {
        return blogInfoMapper.selectOne(new LambdaQueryWrapper<BlogInfo>()
            .eq(BlogInfo::getDeleteFlag, 0)
            .eq(BlogInfo::getId, blogId));
    }

    @Override
    public Boolean addBlog(AddBlogInfoParam param) {
        BlogInfo blogInfo = new BlogInfo();
        BeanUtils.copyProperties(param, blogInfo);
        try {
            return blogInfoMapper.insert(blogInfo) == 1;
        } catch (Exception e) {
            log.error("failed to upload the blog, e: ", e);
            return false;
        }
    }

    @Override
    public Boolean update(UpBlogParam upBlogParam) {
        BlogInfo blogInfo = new BlogInfo();
        BeanUtils.copyProperties(upBlogParam, blogInfo);
        try {
            return blogInfoMapper.updateById(blogInfo) >= 1;
        } catch (Exception e) {
            log.error("updateBlog Error, e: ", e);
            return false;
        }
    }

    /** 逻辑删除 */
    @Override
    public Boolean delete(Integer blogId) {
        BlogInfo blogInfo = new BlogInfo();
        blogInfo.setId(blogId);
        blogInfo.setDeleteFlag(1);
        try {
            return blogInfoMapper.updateById(blogInfo) >= 1;
        } catch (Exception e) {
            log.error("deleteBlog Error, e: ", e);
            return false;
        }
    }
}
