-- origin
-- 建表SQL
create database if not exists java_blog_spring charset utf8mb4;
use java_blog_spring;
-- ⽤⼾表
DROP TABLE IF EXISTS java_blog_spring.user_info;
CREATE TABLE java_blog_spring.user_info(
`id` INT NOT NULL AUTO_INCREMENT,
`user_name` VARCHAR ( 128 ) NOT NULL,
`password` VARCHAR ( 128 ) NOT NULL,
`github_url` VARCHAR ( 128 ) NULL,
`delete_flag` TINYINT ( 4 ) NULL DEFAULT 0,
`create_time` DATETIME DEFAULT now(),
`update_time` DATETIME DEFAULT now() ON UPDATE now(),
PRIMARY KEY ( id ),
UNIQUE INDEX user_name_UNIQUE ( user_name ASC )) ENGINE = INNODB DEFAULT
CHARACTER
SET = utf8mb4 COMMENT = '⽤⼾表';
-- 博客表
drop table if exists java_blog_spring.blog_info;
CREATE TABLE java_blog_spring.blog_info (
`id` INT NOT NULL AUTO_INCREMENT,
`title` VARCHAR(200) NULL,
`content` TEXT NULL,
`user_id` INT(11) NULL,
`delete_flag` TINYINT(4) NULL DEFAULT 0,
`create_time` DATETIME DEFAULT now(),
`update_time` DATETIME DEFAULT now() ON UPDATE now(),
PRIMARY KEY (id))
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '博客表';
-- 新增⽤⼾信息
insert into java_blog_spring.user_info (user_name, 
password,github_url)values("zhangsan","123456","https://gitee.com/bubble-fish666/class-java45");
insert into java_blog_spring.user_info (user_name, 
password,github_url)values("lisi","123456","https://gitee.com/bubble-fish666/class-java45");
insert into java_blog_spring.blog_info (title,content,user_id) values("第⼀篇博
客","111我是博客正⽂我是博客正⽂我是博客正⽂",1);
insert into java_blog_spring.blog_info (title,content,user_id) values("第⼆篇博
客","222我是博客正⽂我是博客正⽂我是博客正⽂",2);

update user_info set
password='ae99e44c868b49cabdbbf33b59eaae45ee62977529c8d141f7822e4770414712'

-- cloud



create database if not exists spring_cloud_user charset utf8mb4;
use spring_cloud_user;
-- ⽤⼾表
DROP TABLE IF EXISTS spring_cloud_user.user_info;
CREATE TABLE spring_cloud_user.user_info(
`id` INT NOT NULL AUTO_INCREMENT,
`user_name` VARCHAR ( 128 ) NOT NULL,
`password` VARCHAR ( 128 ) NOT NULL,
`github_url` VARCHAR ( 128 ) NULL,
`email` VARCHAR ( 128 ) NULL,
`delete_flag` TINYINT ( 4 ) NULL DEFAULT 0,
`create_time` DATETIME DEFAULT now(),
`update_time` DATETIME DEFAULT now() ON UPDATE now(),
PRIMARY KEY ( id ),
UNIQUE INDEX user_name_UNIQUE (user_name ASC )) ENGINE = INNODB DEFAULT CHARACTER SET = utf8mb4 COMMENT = '⽤⼾表';
-- 新增⽤⼾信息
insert into spring_cloud_user.user_info (user_name, password,github_url)values("zhangsan","edb4142105ea4f73bda32fc5c09bcd9e186c98d92127b24fae8bd12134cc5d70","https://gitee.com/bubble-fish666");
insert into spring_cloud_user.user_info (user_name, password,github_url)values("lisi","602eb4f640bf4e9ab8822c25e4d6641cb779187ca84488c07130dc430749832b","https://gitee.com/bubble-fish666");

-- 博客服务数据库
create database if not exists spring_cloud_blog charset utf8mb4;
use spring_cloud_blog;
-- 博客表
drop table if exists spring_cloud_blog.blog_info;
CREATE TABLE spring_cloud_blog.blog_info (
`id` INT NOT NULL AUTO_INCREMENT,
`title` VARCHAR(200) NULL,
`content` TEXT NULL,
`user_id` INT(11) NULL,
`delete_flag` TINYINT(4) NULL DEFAULT 0,
`create_time` DATETIME DEFAULT now(),
`update_time` DATETIME DEFAULT now() ON UPDATE now(),
PRIMARY KEY (id))
ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '博客表';

insert into spring_cloud_blog.blog_info (title,content,user_id) values("第⼀篇博客","111我是博客正⽂我是博客正⽂我是博客正⽂",1);
insert into spring_cloud_blog.blog_info (title,content,user_id) values("第⼆篇博客","222我是博客正⽂我是博客正⽂我是博客正⽂",2);




-- nacos
create database if not EXISTS nacos_config charset utf8mb4;
use nacos_config;
source mysql-schema.sql;
show tables;

USE nacos_config;

INSERT INTO users (username, password, enabled)
VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');

select * from config_info where id = 2287;