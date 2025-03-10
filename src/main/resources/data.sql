insert ignore into `member` (email, password, nickname, activated, join_type, role) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 1,'LOCAL','ROLE_ADMIN');
insert ignore into `member` (email, password, nickname, activated, join_type, role) values ('member', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'member', 1,'LOCAL', 'ROLE_USER');
insert ignore into `member` (email, password, nickname, activated, join_type, role) values ('c1004sos@example.com', '{bcrypt}$2a$10$BF3rHvfqpzkmShGYZeVQ1.r8jn93XGinrVkJe.Jy8e27smerI4Kom', '몸무', true, 'LOCAL', 'ROLE_USER');

-- insert ignore into member_authority (member_id, authority_name) values (1, 'ROLE_member');
-- insert ignore into member_authority (member_id, authority_name) values (1, 'ROLE_ADMIN');
-- insert ignore into member_authority (member_id, authority_name) values (2, 'ROLE_member');


INSERT IGNORE INTO category (category_name) VALUES ('육류');
INSERT IGNORE INTO category (category_name) VALUES ('어류 및 해산물');
INSERT IGNORE INTO category (category_name) VALUES ('유제품');
INSERT IGNORE INTO category (category_name) VALUES ('계란 및 단백질 대체 식품');
INSERT IGNORE INTO category (category_name) VALUES ('채소류');
INSERT IGNORE INTO category (category_name) VALUES ('과일류');
INSERT IGNORE INTO category (category_name) VALUES ('곡류 및 가공식품');
INSERT IGNORE INTO category (category_name) VALUES ('조미료 및 양념');
INSERT IGNORE INTO category (category_name) VALUES ('냉동식품');
INSERT IGNORE INTO category (category_name) VALUES ('음료류');
INSERT IGNORE INTO category (category_name) VALUES ('가공육 및 햄류');
INSERT IGNORE INTO category (category_name) VALUES ('반찬류');
INSERT IGNORE INTO category (category_name) VALUES ('국/탕/찌개류');

