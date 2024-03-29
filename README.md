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

1. Для работы приложения в тестовой среде, необходимо развернуть БД. Это удобно сделать в `Docker` контейнере, поэтому для начала необходимо установить Docker Compose на компьютер: [https://www.docker.com](https://www.docker.com/)
2. Далее, нужно собрать образ тестовой БД и запустить контейнер. Это можно сделать с помощью команды:
   `docker-compose -f db_test/docker-compose.yml up`
3. Последний шаг, это запуск приложения. Обычно это можно сделать в самой IDE, посредством графического интерфейса, но можно и с помощью команды:
   `gradlew run`
4. По умолчанию сервер доступен по адресу: `http://0.0.0.0:8080`, если нужно поменять путь, то это можно сделать в `Const.kt`
переменная: `PROJECT_DEFAULT_PATH = ""`

### Запуск приложения (Prod)

#### Инструкция:
1. Доступ к серверу по SSH, через терминал. Например: `ssh root@194.83.96.181`
2. Установить **Git** (`sudo apt install git`)
3. А так же актуальную версия **Docker** (https://docs.docker.com/engine/install/ubuntu/)
4. Если вам необходимо, чтобы доступ к сайту был по **https**, то прочтите что сказано в пункте **"Установка SSL сертификата"**, если вам это не надо - то переходите сразу к следующему пункту.
5. Склонировать репозиторий с бекендом(`git clone https://github.com/shalkov/KtorServer_ScheduleBus`)
6. Далее заходим в папку с бекендом: `cd KtorServer_ScheduleBus/`
7. Если есть необходимость поправить настройки БД(логин, пароль и т.д) можно это сделать в файле `.env`
8. Запускаем Ktor сервер и БД(PostgresQL) командой: `docker compose up`. По умолчанию сервер доступен на 81 порту, если нужно изменить, это можно сделать в файле `docker-compose.yml`
9. Всё, сервер и БД должны запуститься и быть доступны для работы.

#### Установка SSL сертификата:
1. Прежде чем установить SSL сертификат, нужно привязать к хостингу ваш домен. Так как сертификат выдаётся именно с прявязкой к домену (например: mysite.ru).
Домен не сразу становиться доступным, нужно подождать до 72 часов, прежде чем обновяться DNS записи.<br>
**Обратить внимание:** если вы пропишете DNS сервера Cloudflare (https://dash.cloudflare.com), то у вас автоматически доступ к сайту будет по https,
поэтому шаг с установкой `Certbot` надо пропустить. В остальном действуйте по шагам.
2. Как только домен станет доступен и привяжется к вашему хостингу, необходимо на сервере установить Nginx (https://www.digitalocean.com/community/tutorials/how-to-install-nginx-on-ubuntu-20-04)
3. Если вам нужно установить Certbot, то после установки Nginx, его нужно отключить(`sudo systemctl stop nginx`)
4. Установите Certbot (https://certbot.eff.org/instructions?ws=nginx&os=ubuntufocal)
5. После установки Certbot, нужно включить Nginx(`sudo systemctl start nginx`) и проверить, чтобы сайт нормально открывался по https.
Если есть какие-то проблемы на этом шаге, то нужно разобраться с ними, прежде чем приступать к следующему шагу.
6. Далее, необходимо прописать проксирование в nginx файле: `/etc/nginx/sites-enabled`.
Нужно добавить `proxy_pass` в `location /`, должно быть так:
```
location / {
    # 81 - это порт который указан в docker-compose.yml, он может быть другой, если вы его меняли
    proxy_pass http://0.0.0.0:81;
}
```
7. Теперь нужно перезапустить Nginx, чтобы все изменения применились(`sudo systemctl restart nginx`)
8. Настройки SSL закончены, можно запускать сервер.

#### Конфигурация выделенного сервера:

- **Дата-Центр:** Rucloud: Россия, Королёв.
- **Операционная Система:** Ubuntu 20.04 LTS
- **Конфигурация:** 2x2.2ГГц, 2Гб RAM
- **Диск:** 20Гб SSD
