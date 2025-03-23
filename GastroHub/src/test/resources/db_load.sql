
INSERT INTO `roles` VALUES
    (1,'Admin'),
    (2,'Cliente'),
    (3,'Dono');

INSERT INTO `users` VALUES
    (1,'',NULL,'admin@gastrohub.com',NULL,'admin','swordfish',1),
    (2,'Valentino Volpato, 80',NULL,'altair@altair.campo',NULL,'Altair','s3nh4',2),
    (3,'Rua das rosas, 160',NULL,'guilherme@guilherme.com',NULL,'guilherme','s3nh4',2),
    (4,'Rua dos jasmins, 85',NULL,'romeu@padariadoromeu.com',NULL,'romeu','boasenha',3);

INSERT INTO `restaurants` VALUES
    (1,'Rua dos jasmins, 85','Padaria','Padaria do Romeu','6 as 18 horas',4);

INSERT INTO `products` VALUES
    (1,'No local','Pao frances 50g','Pao frances','c:\\gastrohub\\pao.png',1.56,1),
    (2,'No local ou entrega','Pao de queijo de minas','Pao de queijo','c:\\gastrohub\\paoqueijo.png',2.50,1);