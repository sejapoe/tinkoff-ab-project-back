# Студенческий форум

## Что это такое

Это бэкенд для проекта студенческого форума

## Как этим пользоваться

Запустить СУБД:

```shell
docker run --name my_postgres -e POSTGRES_DB=test_db -e POSTGRES_HOST_AUTH_METHOD=trust -p 5432:5432 -d postgres
```

Запустить само приложение через класс `NinjaMireaCloneApplication`.

## Какие есть возможности

Приложение предоставляет Swagger по url: `localhost:8080/swagger-ui/index.html`.

Взаимодействие осуществляется по REST API.

Доступные возможности:

- Регистрация, авторизация
- Ролевая система: пользователь, модератор, администратор
- Форум: создание тем, постинг, загрузка файлов
- Новостная лента: постинг, добавление комментариев и ответов к комментариям (треды)

## Техническая реализация

- Spring Boot + Java
- PostgreSQL как основная СУБД
- Minio S3 (тесты) и Яндекс.Облако для хранения файлов

## Демонстрация возможностей

Далее показаны скриншоты различных частей системы на примере клиентской части (https://github.com/sejapoe/tinkoff-ab-project-front) для большей наглядности

Ветки:

<img width="1512" alt="Screenshot 2024-09-19 at 02 50 15" src="https://github.com/user-attachments/assets/eba02f5a-1505-432d-a33b-0276755b3fa0">

Создание темы:

<img width="288" alt="Screenshot 2024-09-19 at 02 50 21" src="https://github.com/user-attachments/assets/b1d0270f-cfc8-4e15-8f23-e270f8bc38b8">

Постинг в теме:

<img width="546" alt="Screenshot 2024-09-19 at 02 50 26" src="https://github.com/user-attachments/assets/307a025e-6114-4778-91ca-69d650aea90b">

Новостная лента:

<img width="1512" alt="Screenshot 2024-09-19 at 02 53 28" src="https://github.com/user-attachments/assets/50538880-daeb-4e00-ade8-87f6fa47e04c">

Комментарии к новости:

<img width="1022" alt="Screenshot 2024-09-19 at 02 53 32" src="https://github.com/user-attachments/assets/94b4a0ff-eb75-4cb4-b32e-dca7fa01aab8">

Админка:

<img width="1017" alt="Screenshot 2024-09-19 at 02 54 03" src="https://github.com/user-attachments/assets/e8d13bf7-f97b-4771-9e45-7b1d45e0ee0f">
