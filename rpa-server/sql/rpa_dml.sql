# MySQL
insert into app(id, name, status, created_time)
values ('wechat', '微信', 1, now()),
       ('wecom', '企业微信', 1, now() + 1),
       ('qq', '腾讯QQ', 1, now() + 2),
       ('dingtalk', '钉钉', 1, now() + 3),
       ('lark', '飞书', 1, now() + 4);

insert into func(app_id, name, remark, param, priority, status, created_time)
values ('wechat', 'login', 'Login', null, 0, 1, now()),
       ('wechat', 'logout', 'Logout', null, 1, 1, now()),
       ('wechat', 'send_private_messages', 'Send Private Messages', null, 100, 1, now()),
       ('wechat', 'send_group_messages', 'Send Group Messages', null, 100, 1, now()),
       ('wechat', 'add_contacts', 'Add Contacts', null, 100, 1, now()),
       ('wechat', 'post_moments', 'Post Moments', null, 100, 1, now()),

       ('wecom', 'login', 'Login', null, 0, 1, now()),
       ('wecom', 'logout', 'Logout', null, 1, 1, now()),
       ('wecom', 'send_private_messages', 'Send Private Messages', null, 100, 1, now()),
       ('wecom', 'send_group_messages', 'Send Group Messages', null, 100, 1, now()),
       ('wecom', 'add_contacts', 'Add Contacts', null, 100, 1, now()),

       ('qq', 'login', 'Login', null, 0, 1, now()),
       ('qq', 'logout', 'Logout', null, 1, 1, now()),
       ('qq', 'send_private_messages', 'Send Private Messages', null, 100, 1, now()),
       ('qq', 'send_group_messages', 'Send Group Messages', null, 100, 1, now()),

       ('dingtalk', 'login', 'Login', null, 0, 1, now()),
       ('dingtalk', 'logout', 'Logout', null, 1, 1, now()),
       ('dingtalk', 'send_private_messages', 'Send Private Messages', null, 100, 1, now()),
       ('dingtalk', 'send_group_messages', 'Send Group Messages', null, 100, 1, now()),

       ('lark', 'login', 'Login', null, 0, 1, now()),
       ('lark', 'logout', 'Logout', null, 1, 1, now()),
       ('lark', 'send_private_messages', 'Send Private Messages', null, 100, 1, now()),
       ('lark', 'send_group_messages', 'Send Group Messages', null, 100, 1, now());

# number, string, select, multiple, array

update func
set param='[{"key":"target","value":null,"name":"Friend","type":"string","required":true},{"key":"messages","value":[],"name":"Messages","type":"array","required":true,"children":[{"key":"type","value":null,"name":"Message Type","type":"select","required":true,"options":[{"name":"Text","value":"text"},{"name":"Image","value":"image"},{"name":"Video","value":"video"},{"name":"File","value":"file"}]},{"key":"content","value":null,"name":"Content","type":"string","required":true}]}]'
where name = 'send_private_messages'
  and app_id in ('wechat', 'wecom', 'qq', 'dingtalk', 'lark');

update func
set param='[{"key":"target","value":null,"name":"Friend","type":"string","required":true},{"key":"messages","value":[],"name":"Messages","type":"array","required":true,"children":[{"key":"type","value":null,"name":"Message Type","type":"select","required":true,"options":[{"name":"Text","value":"text"},{"name":"Image","value":"image"},{"name":"Video","value":"video"},{"name":"File","value":"file"},{"name":"Mention","value":"mention"}]},{"key":"content","value":null,"name":"Content","type":"string","required":true}]}]'
where name = 'send_group_messages'
  and app_id in ('wechat', 'wecom', 'qq', 'dingtalk', 'lark');

update func
set param='[{"key":"contacts","value":null,"name":"Messages","type":"array","required":true,"children":[{"key":"target","value":null,"name":"Mobile or email","type":"string","required":true},{"key":"reason","value":null,"name":"Reason","type":"string","required":false}]}]'
where name = 'add_contacts'
  and app_id in ('wechat', 'wecom', 'qq', 'dingtalk', 'lark');

update func
set param='[{"key":"username","value":null,"name":"Username","type":"string","required":true},{"key":"password","value":null,"name":"Password","type":"string","required":true}]'
where name = 'login'
  and app_id in ('qq', 'dingtalk', 'lark');

