# Schedule Bus (API)

API сервис для получения расписания автобусных маршрутов. Дополнительно реализована возможность авторизации/регистрации с разграничением ролей.

### Фичи:

1. Авторизация/Регистрация (JWT).
2. Разграничение пользователей по ролям: USER, MODERATOR, ADMIN.
3. Админка для просмотра/изменения/создания/удаления данных.
4. Swagger спецификация с описание всех запросов этого сервиса.
5. Docker Compose для удобного развёртывания.

### Технологии:

1. Kotlin - [https://kotlinlang.org](https://kotlinlang.org/)
2. Ktor Server - [https://ktor.io](https://ktor.io/)
3. Netty web server - https://netty.io/
4. PostrgeSQL - [https://www.postgresql.org](https://www.postgresql.org/)
5. HikariCP - https://github.com/brettwooldridge/HikariCP
6. Exposed - https://github.com/JetBrains/Exposed
7. JWT Auth - https://ktor.io/docs/jwt.html
8. Koin - https://insert-koin.io/docs/reference/koin-ktor/ktor/
9. Bcrypt - https://github.com/jeremyh/jBCrypt
10. Freemarker - https://ktor.io/docs/freemarker.html
11. Docker Compose - https://docs.docker.com/compose/

### Видео демонстрация работы приложения:

Запуск сервера: https://youtu.be/aZxtT2HqoII

Запросы: https://youtu.be/AYsQwGf254c

Админка: https://youtu.be/w8T0TjacN44

### Запуск приложения (Test) :

1. Для работы приложения в тестовой среде, необходимо развернуть БД. Это удобно сделать в Docker контейнере, поэтому для начала необходимо установить Docker Compose на компьютер: [https://www.docker.com](https://www.docker.com/)
2. Далее, нужно собрать образ тестовой БД и запустить контейнер. Это можно сделать с помощью команды:
   `docker-compose -f db_test/docker-compose.yml up`
3. Последний шаг, это запуск приложения. Обычно это можно сделать в самой IDE, посредством графического интерфейса, но можно и с помощью команды:
   `gradlew run`

### Запуск приложения (Prod) :

1. Для развёртывания приложения в прод, запускаются сразу два Docker контейнера, один с БД, а другой с самим приложением. Поэтому для работы, на компьютере должен быть установлен Docker Compose: [https://www.docker.com](https://www.docker.com/)
2. Необходимые настройки БД(логин, пароль и т.д) можно поправить в файле `.env`
3. Далее, нужно собрать все два образа и запустить контейнеры. Это можно сделать с помощью команды:
   `docker compose up`

### Конфигурация выделенного серера

- **Дата-Центр:** Rucloud: Россия, Королёв. 
- **Операционная Система:** Ubuntu 20.04 LTS
- **Конфигурация:** 2x2.2ГГц, 2Гб RAM
- **Диск:** 20Гб SSD
- **Аренда:** https://ruvds.com/
- 
#### Инструкция:
1. Доступ к серверу по SSH, через терминал. Например: `ssh root@194.83.96.181`
2. Установить **Git** (`sudo apt install git`)
3. А так же актуальную версия **Docker** (https://docs.docker.com/engine/install/ubuntu/)
4. Склонирован репозиторий с бекендом(`git clone https://github.com/shalkov/KtorServer_ScheduleBus`)
5. Далее заходим в папку с бекендом: `cd KtorServer_ScheduleBus/`
6. И выполнить команду: `docker compose up`
7. Всё, сервер должен запуститься и быть доступен по IP адресу
