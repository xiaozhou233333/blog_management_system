package com.example.api.pojo;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.pojo.Result;

import jakarta.validation.Valid;

/**
 * blog-service 的调用契约（Feign 接口）。
 *
 * 注意：
 * 1. 简单类型参数必须写 @RequestParam + 名字，否则 Feign 会把它当成请求体放进 GET body。
 * 2. 返回类型已统一成 Result<T>，与服务端 @RestController 的实际返回一致。
 * 3. 异常处理不写在这里（由 blog-common 的 ExceptionAdvice 统一处理）。
 */
@FeignClient(value = "blog-service", url = "${services.blog-service.url:}", path = "/blog")
public interface BlogInfoApi {

    @GetMapping("/getList")
    Result<List<BlogInfoResponse>> getList();

    @GetMapping("/getBlogDetail")
    Result<BlogInfoResponse> getBlogDetail(@RequestParam("blogId") Integer blogId);

    @PostMapping("/add")
    Result<Boolean> addBlog(@Valid @RequestBody AddBlogInfoParam param);

    @PostMapping("/update")
    Result<Boolean> updateBlog(@Valid @RequestBody UpBlogParam upBlogParam);

    @PostMapping("/delete")
    Result<Boolean> deleteBlog(@RequestParam("blogId") Integer blogId);
}
