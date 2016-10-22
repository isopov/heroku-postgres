[![Build Status](https://travis-ci.org/isopov/heroku-postgres.svg?branch=master)](https://travis-ci.org/isopov/heroku-postgres)
# Используем базу данных на Heroku

Это описание и пример использования Postgres на Heroku.

Активируется этот аддон нажатием одной кнопки, но после этого нехитрого действия возникает вопрос "а что же делать дальше?". Вот ответом на него и служит эта инструкция.

Давайте я сразу оговорюсь - необязательно всё делать точно также (и я даже специально добавил сюда шагов, которые уж совсем явно необязательны)

## Добавляем поддержку Postgres в наше приложение
Для этого в pom.xml добавляем следующие зависимости:
```
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jdbc</artifactId>
		</dependency>

		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
```
И в `application.properties` конфиг для подключения к БД на Heroku:
```
spring.datasource.url=${JDBC_DATABASE_URL}
```

На этом всё. Дальше можно не читать, идут опциональные вещи.
Например в `application.properties` давайте добавим ограничение на число одновременных подключений к БД, чтобы Heroku на нас не обиделся:
```
spring.datasource.maxActive=5
```


## Добавляем удобства
### Создаем схему, таблицы и всё прочее
Как нам создать схему нашей базы данных? Можно:
- зайти в настройки Postgres Heroku addon-а
- скопировать оттуда url, username, password
- подключиться любимым клиентом
- выполнить скрипт создания схемы

Казалось бы, что может быть проще? Оказывается - может! Если мы добавить еще одну зависимость:
```
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-core</artifactId>
		</dependency>
```
И файл `src/main/properties/V1__init.sql` с первоначальной схемой нашей БД, то при первом старте она "магическим образом" создаться в нашей базе.

Теперь нюансы - если вам потребуется (а скорее всего так и будет) изменить вашу схему, то файл `src/main/properties/V1__init.sql` менять нельзя. Вам потребуется создать другой файл - `src/main/properties/V2__alter.sql` в который уже написать скрипт для изменения вашей схемы. Да, и его после деплоя тоже менять уже нельзя (проще следовать правилу, что такие файлы нельзя менять полсе коммита). Если подумать об этом "неудобстве", то окажется, что после деплоя первой версии вашего приложения шаловливые пользователи уже записали туда какие-то свои данные, и терять эти данные не хочется. А значит даже если бы не использовали систему миграци структуры БД (flyway в нашем случае), нам всё равно пришлось бы "мигрировать" уже существующую базу. 

### Чиним и пишем новые тесты
Внезапно оказывается, что тесты наши перестали работать, потому что настройки нашего приложения требуют для их запуска хоркнутого постгреса. Да и мало того - у нас даже постгресового драйвера во врем выполнения тестов нет - он подключен у нас со `<scope>runtime</scope>`

Чтобы это починить давайте создадим файл `src/test/resources/application.properties`. В принципе мы можем записать в него параметры доступа к тестовому постгресу и выполнять тесты на нем (тогда придется ставить его еще и на travis), но можно пойти другим путём. Добавим тестовую зависимость на еще одну базу данных (я начал говорить уже о третьей базе данных за один курс, не потому что студенты прошлых курсов и сами тащили всякий ужас типа Монги в свои проекты, а просто из естественной вредности).
```
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>test</scope>
		</dependency>
```
После этого наши тесты начнут работать, если мы написали нашу схему БД и миграции достаточно кросплатформенно.

### Локальный запуск
Убедившись, что тесты проходят, мы пробуем запустить наше приложение локально и обнаруживаем, что оно таки требует локального постгреса и таки в том же виде, что и даёт Heroku. Неужели всё было зря и мы можем выкинуть в трубу наш кроссплатформенный sql? 

Не стоит! Мы можем на такой же H2 (которая в нашем случае работает в in-memory режиме - то есть является более крутым аналогом CHM) запускать не только тесты, но и всё наше приложение. Для этого:
- в тестах создадим еще один main-class и запускать из Идеи (или Эклипсы) будем его.
- поместим его не в тот же пакет, что и наш основной класс (иначе он будет конфилктовать с ним) а в дочерний - `test` или `testlaunch`.
- импортируем через `@Import` в тестовом SpringBootApplication-е основной. 
Вуаля - мы можем запускать его из Идеи (или Эклипсы) работать локально с нашим приложением.
