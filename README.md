# HotelDAE


### Conexión con la base de datos para la versión con JPA

La forma más sencilla de preparar la base de datos es utilizando la imagen
`mysql` de docker, para ello hay que tener instalado Docker Desktop
(https://www.docker.com/products/docker-desktop). Después basta con ejecutar
los siguientes comandos:

```
docker run -d -p 33060:3306 --name mysql-db -e MYSQL_ROOT_PASSWORD=secret mysql
```

Esto descarga e instala la imagen oficial de mysql (última versión).
Después arranca el contenedor, define _secret_ como clave de root y
asocia MySQL al puerto de la máquina anfitrión 33060.

```
docker exec mysql-db mysql -psecret -e "create database hoteldae; use hoteldae; create user 'hoteldae' identified by 'secret'; grant all privileges on hoteldae.* to 'hoteldae'@'%'"
```

Este comando ejecuta la utilidad de administración `mysql` dentro del contenedor,
crea la base de datos *hoteldae*, un usuario con el mismo nombre y clave _secret_
y finalmente le otorga los permisos necesarios para trabajar con la base
de datos.

Para el testing, crear una nueva base de datos hoteldae_test y dar permisos al usuario creado anteriormente.

```
docker exec mysql-db mysql -psecret -e "create database hoteldae_test; use hoteldae_test; grant all privileges on hoteldae_test.* to 'hoteldae'@'%'"
```

Una vez ya hemos realizado estos paso en la terminal de windows, vamos a la aplicación Docker Desktop y una vez estamos dentro del contenedor con el cliente arrancado, ejecutamos: 
```
sh-4.4# mysql -p 
secret
Welcome to the MySQL monitor. Commands ends with ; or \g
mysql>use hoteldae
mysql>SHOW TABLES;
mysql>SELECT * FROM hotel;
```


