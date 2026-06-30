# AuthTG

**AuthTG** - плагин авторизации для Minecraft-серверов на Paper/Spigot с интеграцией Telegram, VK, почтовой 2FA, капчей, системой друзей, модераторскими командами и поддержкой YAML/MySQL-хранилища.

Проект предназначен для серверов, где нужно защитить аккаунты игроков паролем, подтверждением входа через мессенджер или почту, а также дать администраторам возможность управлять частью серверных действий прямо из Telegram.


## Содержание

- [Возможности](#возможности)
- [Требования](#требования)
- [Структура проекта](#структура-проекта)
- [Быстрый старт](#быстрый-старт)
- [Сборка из исходников](#сборка-из-исходников)
- [Установка на сервер](#установка-на-сервер)
- [Настройка `config.yml`](#настройка-configyml)
- [Настройка Telegram](#настройка-telegram)
- [Настройка VK](#настройка-vk)
- [Настройка почты и mail-2FA](#настройка-почты-и-mail-2fa)
- [YAML и MySQL-хранилище](#yaml-и-mysql-хранилище)
- [Команды Minecraft](#команды-minecraft)
- [Команды Telegram-бота](#команды-telegram-бота)
- [Команды VK-бота](#команды-vk-бота)
- [Права доступа](#права-доступа)
- [PlaceholderAPI](#placeholderapi)
- [Сценарии авторизации](#сценарии-авторизации)
- [Макросы Telegram-команд](#макросы-telegram-команд)
- [Капча](#капча)
- [Файлы данных](#файлы-данных)
- [Безопасность](#безопасность)
- [Troubleshooting](#troubleshooting)
- [Лицензия](#лицензия)


## Возможности

- Регистрация и вход игроков через `/register` и `/login`.
- Сессии авторизации по IP с настраиваемым временем жизни.
- Привязка Telegram-аккаунта к Minecraft-аккаунту.
- Привязка VK-аккаунта к Minecraft-аккаунту.
- Подтверждение входа через Telegram/VK inline-кнопки.
- Почтовая верификация и 2FA через `/mail` и `/2fa`.
- Выбор предпочитаемого 2FA-метода через `/prefer 2fa <mail|tg|vk|off>`.
- Поддержка локального, API и SMTP-режимов отправки email-кодов.
- Капча через инвентарь Minecraft.
- Ограничение длины никнейма и пароля.
- Запрет конкретных никнеймов.
- Ограничение количества регистраций с одного IP.
- Ограничение количества аккаунтов, привязанных к одному Telegram/VK.
- Система друзей с уведомлениями о входе и сообщениями через Telegram.
- Модераторские команды: kick, mute, ban, unban, unmute.
- Управление администраторами и правами Telegram-команд из Minecraft.
- Broadcast в Minecraft и Telegram.
- Макросы Telegram-команд, вызывающие Minecraft-команды.
- Поддержка PlaceholderAPI.
- Поддержка YAML-хранилища и MySQL через HikariCP.
- Автоматическое добавление новых ключей в `config.yml` и `messages.yml` через временные шаблоны.


## Требования

| Компонент | Требование |
|---|---|
| Java | 17 |
| Minecraft-сервер | Paper/Spigot API 1.19+ |
| Сборка | Maven |
| База данных | MySQL опционально |
| Telegram | BotFather-токен опционально |
| VK | группа VK + token опционально |
| PlaceholderAPI | опционально, подключается через `softdepend` |

В `pom.xml` используется `paper-api:1.19-R0.1-SNAPSHOT`, поэтому базовая целевая версия - **1.19**. На более новых версиях Paper плагин может работать, но это нужно проверять на тестовом сервере.


## Структура проекта

```text
AuthTG-main/
├── pom.xml
├── LICENSE
├── .gitignore
└── src/main/
    ├── java/org/ezhik/authTG/
    │   ├── AuthTG.java                  # главный класс плагина
    │   ├── BotTelegram.java             # Telegram long polling bot
    │   ├── BotVK.java                   # VK long poll bot
    │   ├── User.java                    # модель пользователя и привязка аккаунтов
    │   ├── PasswordHasher.java          # SHA-256 хеширование паролей
    │   ├── IPManager.java               # авторизованные IP-сессии
    │   ├── commandMC/                   # Minecraft-команды
    │   ├── commandTG/                   # Telegram-команды
    │   ├── commandVK/                   # VK-команды
    │   ├── calbackQuery/                # обработчики Telegram callback-кнопок
    │   ├── callbackQueryVK/             # обработчики VK callback-кнопок
    │   ├── captcha/                     # inventory captcha
    │   ├── events/                      # Bukkit events
    │   ├── handlers/                    # auth/tick/2FA/VK handlers
    │   ├── mail/                        # LOCAL/API/SMTP отправка email-кодов
    │   ├── migrates/                    # миграции YAML <-> MySQL
    │   ├── otherAPI/                    # PlaceholderAPI и Log4J filter
    │   ├── security/                    # защита регистра никнейма
    │   ├── usersconfiguration/          # YAML/MySQL loader + schema migrator
    │   └── util/                        # async bridge и форматирование сообщений
    └── resources/
        ├── plugin.yml
        ├── config.yml
        ├── messages.yml
        ├── temp-config.yml
        └── temp-messages.yml
```

> В проекте папка `calbackQuery` названа именно так. Если переименовывать её, нужно одновременно менять `package` и импорты.


## Быстрый старт

1. Соберите jar:

   ```bash
   mvn clean package
   ```

2. Скопируйте jar из `target/` в папку `plugins/` сервера.
3. Запустите сервер один раз, чтобы сгенерировалась папка `plugins/AuthTG/`.
4. Остановите сервер.
5. Настройте `plugins/AuthTG/config.yml`:
   - `bot.token` и `bot.username`, если нужен Telegram;
   - `vk.enabled`, `vk.groupId`, `vk.token`, если нужен VK;
   - `mysql.use`, если нужно MySQL-хранилище;
   - `mail.provider`, если нужна email-верификация.
6. Запустите сервер снова.
7. Игрок регистрируется в Minecraft:

   ```text
   /register <пароль> <повтор_пароля>
   /login <пароль>
   ```

8. Для привязки Telegram игрок пишет боту `/start` или `/link`, вводит ник и пароль, затем подтверждает код в Minecraft:

   ```text
   /code <код_из_telegram>
   ```


## Сборка из исходников

### Maven

```bash
mvn clean package
```

Готовый jar будет создан в папке:

```text
target/
```

В `pom.xml` настроен `maven-shade-plugin`, который включает зависимости в итоговый jar и выполняет relocation для:

- `org.bstats` -> `org.ezhik.authTG`
- `com.zaxxer.hikari` -> `org.ezhik.authTG.libs.hikari`

### Основные зависимости

| Зависимость | Назначение |
|---|---|
| `paper-api` | Bukkit/Paper API |
| `telegrambots` | Telegram Bot API |
| `mysql-connector-j` | MySQL-драйвер |
| `HikariCP` | пул соединений MySQL |
| `jakarta.mail` | SMTP-отправка писем |
| `okhttp` | VK API-запросы |
| `org.json` | разбор VK Long Poll ответов |
| `bstats-bukkit` | метрики bStats |
| `placeholderapi` | плейсхолдеры, если установлен PlaceholderAPI |


## Установка на сервер

1. Убедитесь, что сервер запускается на Java 17.
2. Поместите `AuthTG-2.9.jar` в:

   ```text
   plugins/
   ```

3. Запустите сервер.
4. После первого запуска появится папка:

   ```text
   plugins/AuthTG/
   ```

5. Остановите сервер и настройте:

   ```text
   plugins/AuthTG/config.yml
   plugins/AuthTG/messages.yml
   ```

6. Запустите сервер повторно.

Для применения большинства обычных настроек можно использовать:

```text
/authtg reload
```

Но бот и настройки базы данных безопаснее применять через полный рестарт сервера. В самом плагине при reload выводится предупреждение, что новые настройки бота и БД могут не примениться без перезапуска.


## Настройка `config.yml`

Ниже - основные секции и ключи из стандартного `config.yml`.

### Telegram

```yaml
tg: true

bot:
  token: changeme
  username: changeme
  proxy:
    enabled: false
    type: "SOCKS5" # HTTP | SOCKS4 | SOCKS5
    host: "127.0.0.1"
    port: 1080
    username: ""
    password: ""
```

| Ключ | Описание |
|---|---|
| `tg` | включает/отключает Telegram-интеграцию |
| `bot.token` | токен Telegram-бота от BotFather |
| `bot.username` | username бота без `@` |
| `bot.proxy.enabled` | включает прокси для Telegram polling |
| `bot.proxy.type` | `HTTP`, `SOCKS4` или `SOCKS5` |
| `bot.proxy.host`, `port` | адрес и порт прокси |
| `bot.proxy.username`, `password` | авторизация прокси, если нужна |

### VK

```yaml
vk:
  enabled: false
  groupId: 0
  token: changeme
```

| Ключ | Описание |
|---|---|
| `vk.enabled` | включает/отключает VK-интеграцию |
| `vk.groupId` | ID группы VK |
| `vk.token` | токен группы с доступом к сообщениям/Long Poll |

### Captcha

```yaml
captcha:
  enabled: true
  timeoutCaptcha: 3
  openimmediately: true
```

| Ключ | Описание |
|---|---|
| `captcha.enabled` | включает капчу при входе |
| `captcha.timeoutCaptcha` | на сколько дней не показывать капчу после успешного прохождения |
| `captcha.openimmediately` | открывать inventory-капчу сразу при входе |

### MySQL

```yaml
mysql:
  use: false
  host: 'localhost'
  db: 'AuthTG'
  user: 'root'
  pass: '123'
  pool:
    maximumPoolSize: 10
    connectionTimeoutMs: 30000
    idleTimeoutMs: 600000
    maxLifetimeMs: 1800000
    jdbcParams: "useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC"
```

| Ключ | Описание |
|---|---|
| `mysql.use` | `true` - использовать MySQL, `false` - YAML-файлы |
| `mysql.host` | хост базы, при необходимости вместе с портом, например `localhost:3306` |
| `mysql.db` | имя базы данных |
| `mysql.user` / `mysql.pass` | пользователь и пароль |
| `mysql.pool.*` | настройки HikariCP |
| `mysql.pool.jdbcParams` | параметры JDBC-подключения |

### Основная авторизация

```yaml
notRegAndLogin: false
authNecessarily: false
authNecessarilyPrefer: "TG"
minLenghtNickname: 3
maxLenghtNickname: 15
minLenghtPassword: 3
maxLenghtPassword: 32
timeoutSession: 60
kickTimeout: 30
forbiddenNicknames: ['Notch']
ipregmax: 10
```

| Ключ | Описание |
|---|---|
| `notRegAndLogin` | отключает классическую схему `/register` + `/login` |
| `authNecessarily` | требует обязательное подтверждение через внешний метод авторизации |
| `authNecessarilyPrefer` | предпочитаемый метод: `TG`, `VK` или `MAIL` |
| `minLenghtNickname` / `maxLenghtNickname` | минимальная/максимальная длина ника |
| `minLenghtPassword` / `maxLenghtPassword` | минимальная/максимальная длина пароля |
| `timeoutSession` | время IP-сессии в минутах |
| `kickTimeout` | через сколько секунд кикать неавторизованного игрока; `0` отключает таймер |
| `forbiddenNicknames` | список запрещённых ников |
| `ipregmax` | максимум регистраций с одного IP |

> В названиях ключей используется `Lenght`, как в исходном конфиге. Не переименовывайте их в `Length`, иначе код их не прочитает.

### Чат и аккаунты

```yaml
activeChatinTG: true
maxAccountTGCount: 5
maxAccountVKCount: 5
mutecommands: ['/tell']
commandsPreAuthorization: ['/tell']
```

| Ключ | Описание |
|---|---|
| `activeChatinTG` | включает пересылку обычных сообщений в Telegram-чате |
| `maxAccountTGCount` | максимум Minecraft-аккаунтов на один Telegram chat id; `0` - без лимита |
| `maxAccountVKCount` | максимум Minecraft-аккаунтов на один VK peer id |
| `mutecommands` | команды, запрещённые для замученных игроков |
| `commandsPreAuthorization` | команды, доступные до авторизации |

### Spawn

```yaml
spawn:
  x: 0
  y: 0
  z: 0
  world: 'none'
```

Если `spawn.world` равен `none`, игрок замораживается на текущей позиции. Если указан мир и координаты, игрок при входе до авторизации переносится на этот spawn.

Настройка через команду:

```text
/setspawn
/setspawn none
```


## Настройка Telegram

1. Создайте бота через [BotFather](https://t.me/BotFather).
2. Скопируйте token.
3. Укажите в `config.yml`:

   ```yaml
   tg: true
   bot:
     token: "123456:ABCDEF..."
     username: "YourBotName"
   ```

4. Перезапустите сервер.
5. Игрок должен быть онлайн.
6. Игрок пишет боту:

   ```text
   /start
   ```

   или:

   ```text
   /link
   ```

7. Бот попросит ник игрока.
8. Если включена классическая регистрация, бот попросит пароль от Minecraft-аккаунта.
9. Бот отправит код.
10. Игрок вводит в Minecraft:

   ```text
   /code <код>
   ```

После успешной привязки Telegram может использоваться для подтверждения входа, уведомлений, друзей, модераторских команд и broadcast-сообщений.

### Telegram chat bridge

В Telegram-боте:

- сообщение, начинающееся с `#`, отправляется в Minecraft-чат от имени привязанного игрока;
- обычное сообщение пересылается другим привязанным Telegram-пользователям, если `activeChatinTG: true`.


## Настройка VK

1. Создайте или выберите группу VK.
2. Включите сообщения сообщества и Long Poll API.
3. Создайте token группы с доступом к сообщениям.
4. Укажите в `config.yml`:

   ```yaml
   vk:
     enabled: true
     groupId: 123456789
     token: "vk1.a...."
   ```

5. Перезапустите сервер.
6. Игрок пишет группе:

   ```text
   /start
   ```

   или:

   ```text
   /link
   ```

7. Бот попросит ник и, если нужно, пароль.
8. Бот отправит код.
9. Игрок вводит в Minecraft:

   ```text
   /vk <код>
   ```

После успешной привязки VK может использоваться как метод 2FA и для подтверждения входа.


## Настройка почты и mail-2FA

Почтовая подсистема включается секцией:

```yaml
mail:
  enabled: true
  provider: "LOCAL" # LOCAL | API | SMTP
  codeLength: 6
  codeExpireSeconds: 300
```

| Provider | Назначение |
|---|---|
| `LOCAL` | код показывается игроку в Minecraft; удобно для разработки и тестов |
| `API` | плагин отправляет HTTP-запрос на внешний mail API |
| `SMTP` | плагин отправляет письмо через SMTP |

### LOCAL

```yaml
mail:
  enabled: true
  provider: "LOCAL"
```

В этом режиме код подтверждения выводится игроку сообщением в Minecraft. Не используйте `LOCAL` как полноценную защиту на публичном сервере.

### API

```yaml
mail:
  provider: "API"
  api:
    url: "https://example.com/send-code"
    method: "POST"
    contentType: "application/json"
    connectTimeoutMs: 5000
    readTimeoutMs: 5000
    headers:
      X-Internal-Token: "secret"
    fields:
      nickname: "{PLAYER}"
      email: "{EMAIL}"
      code: "{CODE}"
    twoFactorFields:
      nickname: "{PLAYER}"
      email: "{EMAIL}"
      code: "{CODE}"
```

Поддерживаемые placeholders в API-полях:

| Placeholder | Значение |
|---|---|
| `{PLAYER}` | ник игрока |
| `{EMAIL}` | email |
| `{CODE}` | код подтверждения |
| `{UUID}` | UUID игрока |
| `{IP}` | IP игрока |

Успешным считается HTTP-ответ с кодом `2xx`.

### SMTP

```yaml
mail:
  provider: "SMTP"
  smtp:
    host: "smtp.gmail.com"
    port: 587
    auth: true
    startTls: true
    username: "example@gmail.com"
    password: "app-password"
    fromEmail: "example@gmail.com"
    fromName: "AuthTG"
    subject: "Код подтверждения для {PLAYER}"
    html: false
    body:
      - "Здравствуйте, {PLAYER}!"
      - "Ваш код подтверждения: {CODE}"
      - "Почта: {EMAIL}"
```

Для Gmail обычно нужен пароль приложения, а не обычный пароль аккаунта.

### Команды почты

```text
/mail link <email>
/mail verify <код>
/mail status
/mail unlink
/2fa <код>
```

### Выбор mail как 2FA

```text
/prefer 2fa mail
```

Команда `/prefer` работает только при включённом MySQL, потому что выбранный метод хранится в поле `preferred2fa` таблицы `AuthTGUsers`.


## YAML и MySQL-хранилище

AuthTG поддерживает два режима хранения данных.

### YAML

Используется по умолчанию:

```yaml
mysql:
  use: false
```

Данные игроков хранятся в:

```text
plugins/AuthTG/users/<uuid>.yml
```

YAML подходит для небольших серверов и простого запуска без базы данных.

### MySQL

Включение:

```yaml
mysql:
  use: true
  host: 'localhost:3306'
  db: 'AuthTG'
  user: 'root'
  pass: 'password'
```

При старте плагин создаёт и обновляет таблицы:

- `AuthTGMeta`
- `AuthTGUsers`
- `AuthTGFriends`
- `AuthTGCommands`
- `AuthTGBans`
- `AuthTGMutes`

Также используется мигратор схемы `MySQLSchemaMigrator`, который добавляет новые поля, например:

- `ipRegistration`
- `email`
- `isVerifiedEmail`
- `preferred2fa`
- `captchaTimeout`
- `peerid`
- `activevk`

### Миграции между YAML и MySQL

В конфиге есть служебная секция:

```yaml
onceUsed:
  mysql: false
```

Её не нужно редактировать вручную. Код использует её, чтобы определить, нужно ли выполнить миграцию при переключении `mysql.use`.

- При переходе с YAML на MySQL запускается `MySQLMigrate`.
- При переходе с MySQL на YAML запускается `YAMLMigrate`.

Перед сменой режима хранения обязательно сделайте резервную копию папки `plugins/AuthTG/` и базы данных.


## Команды Minecraft

### Игроки

| Команда | Алиасы | Описание |
|---|---|---|
| `/register <пароль> <повтор>` | `/reg` | регистрация аккаунта |
| `/login <пароль>` | `/l` | вход в аккаунт |
| `/logout` | нет | выйти из сессии и кикнуть себя с сервера |
| `/changepassword <старый> <новый> <повтор>` | `/cp` | смена пароля |
| `/code <код>` | нет | подтвердить привязку/отвязку Telegram |
| `/vk <код>` | нет | подтвердить привязку/отвязку VK |
| `/2fa <код>` | нет | ввести код mail-2FA |
| `/captcha` | нет | открыть капчу, если она ожидается |

### Почта и 2FA

| Команда | Описание |
|---|---|
| `/mail link <email>` | отправить код подтверждения на email |
| `/mail verify <код>` | подтвердить email |
| `/mail status` | показать статус привязки email |
| `/mail unlink` | отвязать email |
| `/prefer 2fa tg` | выбрать Telegram как предпочтительный 2FA-метод |
| `/prefer 2fa vk` | выбрать VK как предпочтительный 2FA-метод |
| `/prefer 2fa mail` | выбрать email как предпочтительный 2FA-метод |
| `/prefer 2fa off` | сбросить 2FA-метод, если `authNecessarily: false` |

### Друзья

| Команда | Описание |
|---|---|
| `/friend add <игрок>` | отправить запрос в друзья через Telegram |
| `/friend list` | список друзей |
| `/friend rem <игрок>` | удалить друга |
| `/friend tell <игрок> <сообщение>` | отправить сообщение другу через Telegram |

### Администрирование и модерация

| Команда | Permission | Описание |
|---|---|---|
| `/authtg reload` | `authtg.authtg` | перезагрузить runtime-конфиги плагина |
| `/admin add <ник>` | `authtg.admin` | сделать пользователя AuthTG-администратором |
| `/admin rem <ник>` | `authtg.admin` | снять AuthTG-администратора |
| `/admin list` | `authtg.admin` | список AuthTG-администраторов |
| `/command add <ник> <ban|kick|mute>` | AuthTG admin | выдать право команды в Telegram |
| `/command rem <ник> <ban|kick|mute>` | AuthTG admin | забрать право команды в Telegram |
| `/command list <ник>` | AuthTG admin | список выданных команд |
| `/kick <ник> <причина>` | `authtg.kick` | кикнуть игрока |
| `/mute <ник> <время> <причина>` | `authtg.mute` | выдать мут |
| `/unmute <ник>` | `authtg.unmute` | снять мут |
| `/ban <ник> <время> <причина>` | `authtg.ban` | выдать бан |
| `/unban <ник>` | `authtg.unban` | снять бан |
| `/setpassword <ник> <новый> <повтор>` | `authtg.setpassword` | сменить пароль другому игроку |
| `/unlink <ник>` | `authtg.unlink` | отвязать Telegram у игрока |
| `/setspawn` | `authtg.setspawn` | установить auth-spawn в текущей точке |
| `/setspawn none` | `authtg.setspawn` | отключить auth-spawn |
| `/mcbc <сообщение>` | `authtg.mcbc` | broadcast в Minecraft |
| `/tgbc <сообщение>` | `authtg.tgbc` | broadcast всем привязанным Telegram-пользователям |

### Формат времени для ban/mute

Поддерживаются суффиксы:

| Формат | Значение |
|---|---|
| `10s` | 10 секунд |
| `5m` | 5 минут |
| `2h` | 2 часа |
| `7d` | 7 дней |
| `-s` | навсегда |

Примеры:

```text
/ban Steve 7d grief
/mute Alex 30m flood
/ban BadPlayer -s cheating
```

Причина ban/mute ограничена 120 символами.


## Команды Telegram-бота

| Команда | Описание |
|---|---|
| `/start` | начать привязку Minecraft-аккаунта |
| `/link` | то же, что `/start` |
| `/accounts` | выбрать активный Minecraft-аккаунт, если привязано несколько |
| `/unlink` | запросить отвязку Telegram от аккаунта |
| `/resetpassword` | сгенерировать новый пароль для текущего аккаунта |
| `/tfon` | включить Telegram 2FA |
| `/tfoff` | отключить Telegram 2FA, если это разрешено режимом авторизации |
| `/kickme` | кикнуть свой Minecraft-аккаунт с сервера |
| `/friends` | открыть список друзей с inline-действиями |
| `/kick <ник> <причина>` | кикнуть игрока, если есть права |
| `/ban <ник> <время> <причина>` | забанить игрока, если есть права |
| `/mute <ник> <время> <причина>` | замутить игрока, если есть права |
| `/unban <ник>` | разбанить игрока, если есть права |
| `/unmute <ник>` | размутить игрока, если есть права |
| `/command add <ник> <ban|kick|mute>` | выдать Telegram-команду игроку, если отправитель AuthTG admin |
| `/command rem <ник> <ban|kick|mute>` | забрать Telegram-команду |
| `/command list <ник>` | показать команды игрока |

Дополнительно:

- `/ban`, `/mute`, `/kick` без аргументов запускают пошаговый сценарий ввода.
- Команды модерации доступны AuthTG-админам или игрокам, которым выдали конкретные права через `/command`.
- Команды из секции `macro` становятся Telegram-командами автоматически.


## Команды VK-бота

| Команда | Описание |
|---|---|
| `/start` | начать привязку Minecraft-аккаунта |
| `/link` | то же, что `/start` |
| `/resetpassword` | сгенерировать новый пароль |
| `/tfon` | включить VK 2FA |
| `/tfoff` | отключить VK 2FA, если разрешено |
| `/kickme` | кикнуть свой Minecraft-аккаунт с сервера |

VK также используется для подтверждения входа через кнопки `Да`/`Нет`.


## Права доступа

| Permission | Для чего нужно |
|---|---|
| `authtg.authtg` | `/authtg reload` |
| `authtg.admin` | `/admin add/rem/list` |
| `authtg.ban` | `/ban` |
| `authtg.unban` | `/unban` |
| `authtg.mute` | `/mute` |
| `authtg.unmute` | `/unmute` |
| `authtg.kick` | `/kick` |
| `authtg.mcbc` | `/mcbc` |
| `authtg.tgbc` | `/tgbc` |
| `authtg.setpassword` | `/setpassword` |
| `authtg.setspawn` | `/setspawn` |
| `authtg.unlink` | `/unlink` |

Отдельно от Bukkit permissions есть внутренняя роль **AuthTG admin**, выдаваемая командой:

```text
/admin add <ник>
```

Она используется для управления Telegram-командами и админ-функциями внутри AuthTG.


## PlaceholderAPI

Если на сервере установлен PlaceholderAPI, AuthTG регистрирует expansion с идентификатором:

```text
authtg
```

Доступные placeholders:

| Placeholder | Значение |
|---|---|
| `%authtg_username%` | Telegram username игрока |
| `%authtg_firstname%` | имя из Telegram |
| `%authtg_lastname%` | фамилия из Telegram |
| `%authtg_activetg%` | статус привязки Telegram |
| `%authtg_twofactor%` | статус 2FA |
| `%authtg_status%` | `admin`, `moderator` или `user` |

Тексты для placeholder-значений настраиваются в `messages.yml` в секции `placeholders`.


## Сценарии авторизации

### Классический режим

```yaml
notRegAndLogin: false
authNecessarily: false
```

Игроки должны использовать:

```text
/register <пароль> <повтор>
/login <пароль>
```

Если у игрока включена 2FA, после `/login` будет запрошено подтверждение через выбранный или доступный метод.

### Без `/register` и `/login`

```yaml
notRegAndLogin: true
authNecessarily: false
```

Плагин не требует классическую регистрацию и вход. Игрок может быть пропущен без ввода пароля.

### Обязательная внешняя авторизация

```yaml
notRegAndLogin: true
authNecessarily: true
authNecessarilyPrefer: "TG"
```

Игрок обязан привязать внешний метод авторизации и подтверждать вход через Telegram/VK/mail в зависимости от настроек.

### Классический пароль + обязательная 2FA

```yaml
notRegAndLogin: false
authNecessarily: true
authNecessarilyPrefer: "TG"
```

Игрок сначала вводит пароль через `/login`, затем подтверждает вход через доступный 2FA-метод.


## Макросы Telegram-команд

Секция `macro` позволяет создать Telegram-команды, которые будут выполнять Minecraft-команды.

Пример из стандартного конфига:

```yaml
macro:
  bc:
    mccmd: "tgbc [arg1]*"
    nsmsg: "Write message to users with telegram auth"
```

После этого в Telegram появляется команда:

```text
/bc <сообщение>
```

Она выполнит на сервере:

```text
tgbc <сообщение>
```

Подстановки:

| Подстановка | Описание |
|---|---|
| `[arg1]` | первый аргумент |
| `[arg2]` | второй аргумент |
| `[arg1]*` | все аргументы начиная с первого одной строкой |

Права для макроса проверяются так же, как для команд: игрок должен быть AuthTG admin или иметь команду с именем макроса.


## Капча

Если `captcha.enabled: true`, при входе игроку показывается inventory-капча.

Принцип работы:

1. Плагин выбирает случайный цвет шерсти.
2. Игрок должен выбрать правильный блок в GUI.
3. Максимум попыток - 3.
4. После успешного прохождения капча не показывается `captcha.timeoutCaptcha` дней.
5. После капчи продолжается обычная авторизация.

Если `captcha.openimmediately: false`, игроку покажется сообщение и title с просьбой открыть капчу командой:

```text
/captcha
```


## Файлы данных

После запуска плагина используются:

```text
plugins/AuthTG/config.yml
plugins/AuthTG/messages.yml
plugins/AuthTG/users/
```

При старте плагин также сохраняет временные шаблоны:

```text
plugins/AuthTG/temp-config.yml
plugins/AuthTG/temp-messages.yml
```

Затем сравнивает их с текущими `config.yml` и `messages.yml`, добавляет отсутствующие ключи и удаляет временные файлы. Это позволяет обновлять конфиги без ручного переноса каждого нового параметра.


## Безопасность

Рекомендации для публичного сервера:

- Не публикуйте `config.yml` с реальными `bot.token`, `vk.token`, MySQL-паролем или SMTP-паролем.
- Перед переключением YAML/MySQL делайте backup.
- Используйте `SMTP` или внешний `API` для email-2FA; `LOCAL` оставляйте для разработки.
- Используйте отдельного MySQL-пользователя только для базы AuthTG.
- Ограничьте доступ к базе по IP/firewall.
- Для Telegram/VK включайте только нужные интеграции.
- Проверяйте, что `maxAccountTGCount`, `maxAccountVKCount` и `ipregmax` соответствуют правилам сервера.
- Учитывайте, что пароль хешируется через SHA-256. Для максимально строгой защиты в будущем стоит рассмотреть BCrypt/Argon2 с солью.


## Troubleshooting

### Telegram bot не запускается

Проверьте:

```yaml
tg: true
bot:
  token: "..."
  username: "..."
```

Также убедитесь, что значения не остались `changeme`. Если используется прокси, проверьте `bot.proxy.*`.

### VK bot не запускается

Проверьте:

```yaml
vk:
  enabled: true
  groupId: 123456789
  token: "..."
```

У группы должны быть включены сообщения и Long Poll API.

### `/prefer` пишет, что хранилище недоступно

`/prefer` доступен только при MySQL, потому что выбранный метод 2FA хранится в MySQL-поле `preferred2fa`.

Включите:

```yaml
mysql:
  use: true
```

### Игрока кикает из-за таймера входа

Увеличьте или отключите:

```yaml
kickTimeout: 30
```

`0` отключает кик по таймеру.

### Капча появляется слишком часто

Увеличьте:

```yaml
captcha:
  timeoutCaptcha: 3
```

Значение указано в днях.

### Сообщения выглядят неправильно

Проверьте `messages.yml`. Плагин использует MiniMessage-подобные теги вида:

```text
<#FFB26F><bold>[AuthTG]
```

Также поддерживается `{BR}` для переноса строки.

### После `/authtg reload` не применились настройки бота или БД

Перезапустите сервер полностью. Код плагина сам предупреждает, что новые настройки бота и базы данных могут не примениться через runtime reload.


## Для разработчиков

### Главный lifecycle

`AuthTG.onEnable()` выполняет:

1. создание папок данных;
2. сохранение стандартных ресурсов;
3. синхронизацию новых ключей config/messages;
4. загрузку параметров;
5. регистрацию событий;
6. регистрацию PlaceholderAPI, если установлен;
7. запуск bStats;
8. запуск scheduler handlers;
9. инициализацию YAML/MySQL loader;
10. регистрацию команд и tab completers;
11. запуск Telegram-бота;
12. запуск VK-бота.

### Основные классы

| Класс | Назначение |
|---|---|
| `AuthTG` | главный класс плагина |
| `BotTelegram` | Telegram polling, команды, callbacks |
| `BotVK` | VK Long Poll, команды, callbacks |
| `User` | данные игрока и операции привязки |
| `TwoFactorAuthService` | выбор и запуск 2FA-метода |
| `YAMLLoader` / `MySQLLoader` | чтение и запись данных игроков |
| `MySQLSchemaMigrator` | создание/обновление MySQL-схемы |
| `MailDeliveryService` | маршрутизация LOCAL/API/SMTP отправки кодов |
| `Captcha` | логика inventory-капчи |
| `NicknameCaseGuard` | защита от входа с тем же ником в другом регистре |


## Лицензия

Проект распространяется под лицензией **GNU GPL v3**. Полный текст находится в файле [`LICENSE`](LICENSE).
