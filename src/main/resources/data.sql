insert ignore into `member` (email, password, nickname, activated) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 1);
insert ignore into `member` (email, password, nickname, activated) values ('member', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'member', 1);


insert ignore into authority (authority_name) values ('ROLE_member');
insert ignore into authority (authority_name) values ('ROLE_ADMIN');

insert ignore into member_authority (member_id, authority_name) values (1, 'ROLE_member');
insert ignore into member_authority (member_id, authority_name) values (1, 'ROLE_ADMIN');
insert ignore into member_authority (member_id, authority_name) values (2, 'ROLE_member');