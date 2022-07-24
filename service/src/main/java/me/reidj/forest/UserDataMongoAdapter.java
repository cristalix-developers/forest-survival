package me.reidj.forest;

import com.mongodb.async.client.MongoClient;
import stat.Stat;

public class UserDataMongoAdapter extends MongoAdapter<Stat> {

    public UserDataMongoAdapter(MongoClient client, String dbName) {
        super(client, dbName, "data", Stat.class);
    }

    /*public CompletableFuture<List<PlayerTopEntry<Object>>> getTop(TopPackage.TopType topType, int limit) {
        return makeRatingByField(topType.name().toLowerCase(), limit).thenApplyAsync(entries -> {
            List<PlayerTopEntry<Object>> playerEntries = new ArrayList<>();
            for (val userInfoObjectTopEntry : entries) {
                PlayerTopEntry<Object> objectPlayerTopEntry = new PlayerTopEntry<>(userInfoObjectTopEntry.getKey(), userInfoObjectTopEntry.getValue());
                playerEntries.add(objectPlayerTopEntry);
            }
            try {
                List<UUID> uuids = new ArrayList<>();
                for (TopEntry<Stat, Object> entry : entries) {
                    UUID uuid = entry.getKey().getUniqueId();
                    uuids.add(uuid);
                }
                List<GroupData> groups = ISocketClient.get()
                        .<BulkGroupsPackage>writeAndAwaitResponse(new BulkGroupsPackage(uuids))
                        .get(5L, TimeUnit.SECONDS)
                        .getGroups();
                Map<UUID, GroupData> map = groups.stream()
                        .collect(Collectors.toMap(GroupData::getUuid, Function.identity()));
                for (PlayerTopEntry<Object> playerEntry : playerEntries) {
                    GroupData data = map.get(playerEntry.getKey().getUniqueId());
                    playerEntry.setUserName(data.getUsername());
                    //val prefix = playerEntry.getKey().getCurrentPrefix();
                    //playerEntry.setDisplayName((prefix == null || prefix == PrefixType.NONE ? "" : prefix) + " " + UtilCristalix.createDisplayName(data));
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                // Oh shit
                playerEntries.forEach(entry -> {
                    entry.setUserName("ERROR");
                    entry.setDisplayName("ERROR");
                });
            }
            return playerEntries;
        });
    }*/

}
