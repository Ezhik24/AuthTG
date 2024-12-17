User:

    getUser(String playername) - Если user привязан к аккаунту,то функция вернет user по никнейму игрока, в остальных случаях вернёт null

    getUser(UUID uuid) - Если user привязан к аккаунту,то функция вернет user по uuid игрока, в остальных случаях вернёт null

    getUserList() - Возвращает список users

    generateConfirmationCode() - Cоздает рандомный код и возвращает его. 

    getSpawnLocation() - Если в конфиге есть какая-нибудь Локация то вернет ее координаты, в остальных случаях вернёт null

    setSpawnLocation() - Функция,которая ставит в конфиг Локацию спавна игрока (при использовании команды /setspawn).

    getplayerstatus(String playername) - Функция,которая возвращает онлайн или оффлайн друг

    remFriend(String friendname) - Функция,которая удаляет друга.Возвращает,то что друг успешно удален или если друг не найден,возвращает что друг не найден

    remFriendFromConf(String friendname) - Функция,которая удаляет друга в конфиге игрока
