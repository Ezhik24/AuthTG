## Авторизация пользователя
1. Когда игрок заходит на сервер срабатывает эвент onJoinEvent, вызываем метод FreezerEvent.freezeplayer для заморозки игрока,при этом игрока телепортирует на спавн
2. Если конфиг пользователя существует,мы мьютим игрока вызова метода MuterEvent.mute
3. 

**AuthTGEM - Главный класс (Класс,в котором запускается плагин)**

**User:**

    getUser(String playername) - Если user привязан к аккаунту,то функция вернет user по никнейму игрока, в остальных случаях вернёт null

    getUser(UUID uuid) - Если user привязан к аккаунту,то функция вернет user по uuid игрока, в остальных случаях вернёт null

    getUserList() - Возвращает список users

    generateConfirmationCode() - Cоздает рандомный код и возвращает его. 

    getSpawnLocation() - Если в конфиге есть какая-нибудь Локация то вернет ее координаты, в остальных случаях вернёт null

    setSpawnLocation() - Функция,которая ставит в конфиг Локацию спавна игрока (при использовании команды /setspawn).

    getplayerstatus(String playername) - Функция,которая возвращает онлайн или оффлайн друг

    remFriend(String friendname) - Функция,которая удаляет друга.Возвращает,то что друг успешно удален или если друг не найден,возвращает что друг не найден

    remFriendFromConf(String friendname) - Функция,которая удаляет друга в конфиге игрока

**BotTelegram:**

    sendMessage(Long chatid, String message) - Функция,которая отправляет сообщение определнному user

    deleteMessage(Message messgae) - Функция,которая удаляет сообщение

    chosoPlayer(Long chatid) - Выбор аккаунтов,которые есть у пользователя

    showFriendsList(Message message) - Функция,которая показывает список друзей,с которыми можно взаимодействовать

    friendAction(String friendname, Message message) - Функция,которая показывает действия с определенным другом

    sendBroadcastMessage(String message, String sendername) - Функция,которая отправляет сообщение в чат в телеграмме

**PasswordHasher -** _Класс,благодаря которому ,хешируются пароли_

**Handler -** _Класс,в котором,выполняются асинхроности_

**AddFriendCMD -** _Обработка команды /addfriend в майнкрафте_

**RemFriendCMD -** _Обработка команды /removefriend в майнкрафте_

**ChangePasswordCMD -** _Обработка команды /changepassword + /cp в майнкрафте_

**CodeCMD -** _Обработка команды /code в майнкрафте_

**ListFriendsCMD -** _Обработка команды /listfriends в майнкрафте_

**LoginCMD -** _Обработка команды /login в майнкрафте_

**RegisterCMD -** _Обработка команды /register в майнкрафте_

**SetPasswordCMD** **-** _Обработка команды /setpassword в майнкрафте_

**SetSpawnCMD -** _Обработка команды /setspawn в майнкрафте_

**TellFriendsCMD -** _Обработка команды /tellfriends в майнкрафте_

**TgbcCMD -** _Обработка команды /tgbc в майнкрафте_

**MessageTranslationMC** **-** _Заключение сообщений (майнкрафта) в конфиг_

**MessageTranslationMC** **-** _Заключение сообщений (телеграма) в конфиг_

**FreezerEvent -** _Эвент,который замораживает игрок_

**MuterEvent -** _Эвент,который мьютит игрока_

**BlockCommandEvent -** _Эвент,который запрещает использование команд_

**BlockDropItemEvent -** _Эвент,который запрещает выкидывание предмета_

**OnJoinEvent** **-** _Эвент,который выполняет действия,когда игрок заходит_
