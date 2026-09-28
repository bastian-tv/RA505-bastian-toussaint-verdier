CREATE USER 'tp_spring_user'@'%' IDENTIFIED BY 'tp_spring_mdp';
GRANT ALL PRIVILEGES ON tp_spring.* TO 'tp_spring_user'@'%';
FLUSH PRIVILEGES;