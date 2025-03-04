insert ignore into `member` (email, password, nickname, activated) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 1);
insert ignore into `member` (email, password, nickname, activated) values ('member', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'member', 1);
insert ignore into `member` (email, password, nickname, activated) values ('c1004sos@example.com', '{bcrypt}$2a$10$BF3rHvfqpzkmShGYZeVQ1.r8jn93XGinrVkJe.Jy8e27smerI4Kom', '몸무', true);

insert ignore into authority (authority_name) values ('ROLE_member');
insert ignore into authority (authority_name) values ('ROLE_ADMIN');

insert ignore into member_authority (member_id, authority_name) values (1, 'ROLE_member');
insert ignore into member_authority (member_id, authority_name) values (1, 'ROLE_ADMIN');
insert ignore into member_authority (member_id, authority_name) values (2, 'ROLE_member');


INSERT IGNORE INTO category (category_name, description) VALUES ('육류', '소고기, 돼지고기, 닭고기 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('어류 및 해산물', '생선, 갑각류, 연체류 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('유제품', '우유, 치즈, 버터 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('계란 및 단백질 대체 식품', '계란, 메추리알, 두부, 콩단백 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('채소류', '잎채소, 뿌리채소, 열매채소 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('과일류', '감귤류, 핵과류, 베리류 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('곡류 및 가공식품', '쌀, 면류, 빵류 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('조미료 및 양념', '소금, 설탕, 간장 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('냉동식품', '냉동 야채, 냉동 생선 및 해산물');
INSERT IGNORE INTO category (category_name, description) VALUES ('음료류', '물, 탄산음료, 과일주스 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('가공육 및 햄류', '햄, 소시지, 베이컨 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('반찬류', '김치류, 장아찌류, 젓갈류 등');
INSERT IGNORE INTO category (category_name, description) VALUES ('국/탕/찌개류', '국류, 탕류, 찌개류 등');

