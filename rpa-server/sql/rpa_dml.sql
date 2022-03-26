# MySQL
insert into app(id, name, status, created_time)
values ('wechat', '微信', 1, now()),
       ('wecom', '企业微信', 1, now()),
       ('qq', 'QQ', 1, now()),
       ('tim', 'TIM', 1, now()),
       ('dingtalk', '钉钉', 1, now()),
       ('lark', '飞书', 1, now());

insert into dict(`type`, `key`, `value`, `remark`, `order`)
values ('task_type:wechat', 'login', '0', 'Login', 0),
       ('task_type:wechat', 'logout', '1', 'Logout', 1),
       ('task_type:wechat', 'send_private_messages', '100', 'Send Private Messages', 2),
       ('task_type:wechat', 'send_group_messages', '100', 'Send Group Messages', 3),
       ('task_type:wechat', 'add_contacts', '100', 'Add Contacts', 4),
       ('task_type:wechat', 'post_moments', '100', 'Post Moments', 5),

       ('task_type:wecom', 'login', '0', 'Login', 0),
       ('task_type:wecom', 'logout', '1', 'Logout', 1),
       ('task_type:wecom', 'send_private_messages', '100', 'Send Private Messages', 2),
       ('task_type:wecom', 'send_group_messages', '100', 'Send Group Messages', 3),

       ('task_type:qq', 'login', '0', 'Login', 0),
       ('task_type:qq', 'logout', '1', 'Logout', 1),
       ('task_type:qq', 'send_private_messages', '100', 'Send Private Messages', 2),
       ('task_type:qq', 'send_group_messages', '100', 'Send Group Messages', 3),

       ('task_type:tim', 'login', '0', 'Login', 0),
       ('task_type:tim', 'logout', '1', 'Logout', 1),
       ('task_type:tim', 'send_private_messages', '100', 'Send Private Messages', 2),
       ('task_type:tim', 'send_group_messages', '100', 'Send Group Messages', 3),

       ('task_type:dingtalk', 'login', '0', 'Login', 0),
       ('task_type:dingtalk', 'logout', '1', 'Logout', 1),
       ('task_type:dingtalk', 'send_private_messages', '100', 'Send Private Messages', 2),
       ('task_type:dingtalk', 'send_group_messages', '100', 'Send Group Messages', 3),

       ('task_type:lark', 'login', '0', 'Login', 0),
       ('task_type:lark', 'logout', '1', 'Logout', 1),
       ('task_type:lark', 'send_private_messages', '100', 'Send Private Messages', 2),
       ('task_type:lark', 'send_group_messages', '100', 'Send Group Messages', 3);

