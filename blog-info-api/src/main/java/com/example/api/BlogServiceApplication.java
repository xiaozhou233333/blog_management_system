package com.example.api;

// 这个类原先是放在 api 模块里的启动类，已被废弃：
// 启动类现在在 blog-info-service 的 com.example.BlogServiceApplication。
//
// 留着它会让 blog-service 扫到两个同名 @SpringBootApplication：
//   com.example.api.BlogServiceApplication（旧）
//   com.example.BlogServiceApplication（新）
// 报 ConflictingBeanDefinitionException: bean name 'blogServiceApplication' conflicts。
// 所以这里不再声明任何类，只保留包声明。
