package org.ezhik.authTG.usersconfiguration;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Loader {
    void setPlayerName(UUID uuid, String playername);

    void setPasswordHash(UUID uuid, String password);

    void setActive(UUID uuid, boolean active);

    boolean isActive(UUID uuid);

    boolean passwordValid(UUID uuid, String password);

    String getPlayerName(UUID uuid);

    boolean getTwofactor(UUID uuid);

    boolean getActiveTG(UUID uuid);

    List<String> getListFriends(UUID uuid);

    String getUserName(UUID uuid);

    String getFirstName(UUID uuid);

    String getLastName(UUID uuid);

    Long getChatID(UUID uuid);

    UUID getCurrentUUID(Long chatid);

    void setCurrentUUID(UUID uuid, Long chatid);

    void setChatID(UUID uuid, Long chatid);

    void setUsername(UUID uuid, String username);

    void setLastName(UUID uuid, String lastname);

    void setFirstName(UUID uuid, String firstname);

    void setTwofactor(UUID uuid, boolean state);

    void setActiveTG(UUID uuid, boolean state);

    List<UUID> getPlayerNames(Long chatid);

    void setPlayerNames(Long chatid, UUID uuid);

    Set<Long> getChatID();

    void addFriend(UUID uuid, String friend);

    UUID getUUIDbyPlayerName(String playername);

    void removeFriend(UUID uuid, String friend);

    void setAdmin(UUID uuid);

    void removeAdmin(UUID uuid);

    Set<String> getAdminList();

    List<String> getCommands(UUID uuid);

    void addCommand(UUID uuid, String command);

    void removeCommand(UUID uuid, String command);

    boolean isAdmin(UUID uuid);
}