package com.example.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.UserServiceApi;
import com.example.api.pojo.UserInfoResponse;
import com.example.exception.BlogException;
import com.example.pojo.AddBlogInfoParam;
import com.example.pojo.BlogInfoResponse;
import com.example.pojo.Result;
import com.example.pojo.UpBlogParam;
import com.example.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;
    private final UserServiceApi userServiceApi;

    @GetMapping("/getList")
    public Result<List<BlogInfoResponse>> getList() {
        return Result.success(blogService.getList());
    }

    @GetMapping("/getBlogDetail")
    public Result<BlogInfoResponse> getBlogDetail(@RequestParam("blogId") Integer blogId) {
        log.info("getBlogDetail, blogId: {}", blogId);
        return Result.success(blogService.getBlogDetail(blogId));
    }

    /**
     * 作者信息：由 blog-service 先查文章拿到 userId，再通过 Feign 调 user-service。
     * 这样运行时调用链是单向的（blog -> user），不会再出现两个服务互相调用。
     */
    @GetMapping("/getAuthorInfo")
    public Result<UserInfoResponse> getAuthorInfo(@RequestParam("blogId") Integer blogId) {
        BlogInfoResponse blog = blogService.getBlogDetail(blogId);
        // 老数据可能没写 user_id，直接透传 null 会让 Feign 拼出 /user/getUserInfo 缺参
        if (blog.getUserId() == null) {
            throw new BlogException("The author of this blog is unknown");
        }
        return userServiceApi.getUserInfo(blog.getUserId());
    }

    @PostMapping("/add")
    public Result<Boolean> addBlog(@Validated @Valid @RequestBody AddBlogInfoParam param) {
        log.info("addBlog received params: {}", param);
        return Result.success(blogService.addBlog(param));
    }

    @PostMapping("/update")
    public Result<Boolean> updateBlog(@Valid @RequestBody UpBlogParam upBlogParam) {
        log.info("updateBlog got Params: {}", upBlogParam);
        return Result.success(blogService.update(upBlogParam));
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteBlog(@RequestParam("blogId") Integer blogId) {
        log.info("deleteBlog got Params: {}", blogId);
        return Result.success(blogService.delete(blogId));
    }
}
