package com.example.service;

import java.util.List;

import com.example.pojo.AddBlogInfoParam;
import com.example.pojo.BlogInfoResponse;
import com.example.pojo.UpBlogParam;

public interface BlogService {
    List<BlogInfoResponse> getList();

    BlogInfoResponse getBlogDetail(Integer blogId);

    Boolean addBlog(AddBlogInfoParam param);

    Boolean update(UpBlogParam upBlogParam);

    Boolean delete(Integer blogId);
}
