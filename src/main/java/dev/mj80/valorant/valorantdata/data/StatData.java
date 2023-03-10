package dev.mj80.valorant.valorantdata.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.mj80.valorant.valorantdata.DataUtils;
import dev.mj80.valorant.valorantdata.Messages;
import dev.mj80.valorant.valorantdata.ValorantData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter @Setter
public class StatData {
    private Player player;
    private File file;
    
    private long kills,deaths,assists,roundsPlayed,matchesPlayed,victories,loses,discordId;
    private double damageDealt,damageReceived,particles;
    
    public StatData(Player player) {
        this.player = player;
        file = new File(ValorantData.getDataPath() + File.separator + player.getUniqueId() + ".json");
        try {
            file.getParentFile().mkdirs();
            if (file.createNewFile() || DataUtils.readFile(file).equals("-")) {
                DataUtils.writeJSONObject(file, createData());
            }
        } catch(IOException e) {
            player.sendMessage(String.format(Messages.ERROR_CREATING_FILE.getMessage(), player.getName(), player.getUniqueId(), System.currentTimeMillis()));
            e.printStackTrace();
        }
    }
    
    public JsonObject createData() {
        JsonObject particles = new JsonObject();
        particles.addProperty("particles", this.particles);
    
        JsonArray preferencesArray = new JsonArray();
        preferencesArray.add(particles);
    
        JsonObject preferencesObject = new JsonObject();
        preferencesObject.add("preferences", preferencesArray);
    
        JsonObject firstLogin = new JsonObject();
        long firstPlayed = player.getFirstPlayed();
        if(firstPlayed == 0) firstPlayed = System.currentTimeMillis();
        firstLogin.addProperty("firstLogin", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z").format(new Date(firstPlayed)));
    
        JsonArray nameHistoryArray = new JsonArray();
        nameHistoryArray.add(player.getName());
    
        JsonObject nameHistory = new JsonObject();
        nameHistory.add("nameHistory", nameHistoryArray);
    
        JsonArray profileArray = new JsonArray();
        profileArray.add(preferencesObject);
        profileArray.add(firstLogin);
        profileArray.add(nameHistory);
    
        JsonObject profileObject = new JsonObject();
        profileObject.add("profile", profileArray);
    
        JsonObject kills = new JsonObject();
        kills.addProperty("kills", this.kills);
    
        JsonObject deaths = new JsonObject();
        deaths.addProperty("deaths", this.deaths);
    
        JsonObject assists = new JsonObject();
        assists.addProperty("assists", this.assists);
    
        JsonObject roundsPlayed = new JsonObject();
        roundsPlayed.addProperty("roundsPlayed", this.roundsPlayed);
    
        JsonObject matchesPlayed = new JsonObject();
        matchesPlayed.addProperty("matchesPlayed", this.matchesPlayed);
    
        JsonObject victories = new JsonObject();
        victories.addProperty("victories", this.victories);
    
        JsonObject loses = new JsonObject();
        loses.addProperty("loses", this.loses);
    
        JsonObject damageDealt = new JsonObject();
        damageDealt.addProperty("damageDealt", this.damageDealt);
    
        JsonObject damageReceived = new JsonObject();
        damageReceived.addProperty("damageReceived", this.damageReceived);
    
        JsonArray statisticsArray = new JsonArray();
        statisticsArray.add(kills);
        statisticsArray.add(deaths);
        statisticsArray.add(assists);
        statisticsArray.add(roundsPlayed);
        statisticsArray.add(matchesPlayed);
        statisticsArray.add(victories);
        statisticsArray.add(loses);
        statisticsArray.add(damageDealt);
        statisticsArray.add(damageReceived);
    
        JsonObject statisticsObject = new JsonObject();
        statisticsObject.add("statistics", statisticsArray);
        
        JsonObject discordId = new JsonObject();
        discordId.addProperty("linkId", this.discordId);
        
        JsonArray discordArray = new JsonArray();
        discordArray.add(discordId);
    
        JsonObject discordObject = new JsonObject();
        discordObject.add("discord", discordArray);
    
        JsonArray dataArray = new JsonArray();
        dataArray.add(profileObject);
        dataArray.add(statisticsObject);
        dataArray.add(discordObject);
    
        JsonObject dataObject = new JsonObject();
        dataObject.add("data", dataArray);
        
        return dataObject;
    }
    
    public void saveData() {
        long start = System.currentTimeMillis();
        player.sendMessage(Messages.SAVING_DATA.getMessage());
        JsonObject dataFile = DataUtils.parseJSON(file);
        assert dataFile != null;
        JsonArray data = dataFile.getAsJsonArray("data");
        JsonArray profile = data.get(0).getAsJsonObject().getAsJsonArray("profile");
        JsonArray preferences = profile.get(0).getAsJsonObject().getAsJsonArray("preferences");
        set(preferences, 0, "particles", particles);
        //
        JsonArray statistics = data.get(1).getAsJsonObject().getAsJsonArray("statistics");
        set(statistics, 0, "kills", kills);
        set(statistics, 1, "deaths", deaths);
        set(statistics, 2, "assists", assists);
        set(statistics, 3, "roundsPlayed", roundsPlayed);
        set(statistics, 4, "matchesPlayed", matchesPlayed);
        set(statistics, 5, "victories", victories);
        set(statistics, 6, "loses", loses);
        set(statistics, 7, "damageDealt", damageDealt);
        set(statistics, 8, "damageReceived", damageReceived);
        JsonArray discord = dataFile.getAsJsonArray("data").get(2).getAsJsonObject().getAsJsonArray("discord");
        set(discord, 0, "linkId", discordId);
        DataUtils.writeJSONObject(file, dataFile);
        player.sendMessage(String.format(Messages.SAVED_DATA.getMessage(), System.currentTimeMillis() - start));
    }
    
    public void updateData() {
        JsonObject dataFile = DataUtils.parseJSON(file);
        assert dataFile != null;
        JsonArray data = dataFile.getAsJsonArray("data");
        JsonArray profile = data.get(0).getAsJsonObject().getAsJsonArray("profile");
        JsonArray preferences = profile.get(0).getAsJsonObject().getAsJsonArray("preferences");
        particles = preferences.get(0).getAsJsonObject().get("particles").getAsDouble();
        //
        JsonArray statistics = data.get(1).getAsJsonObject().getAsJsonArray("statistics");
        kills = statistics.get(0).getAsJsonObject().get("kills").getAsLong();
        deaths = statistics.get(1).getAsJsonObject().get("deaths").getAsLong();
        assists = statistics.get(2).getAsJsonObject().get("assists").getAsLong();
        roundsPlayed = statistics.get(3).getAsJsonObject().get("roundsPlayed").getAsLong();
        matchesPlayed = statistics.get(4).getAsJsonObject().get("matchesPlayed").getAsLong();
        victories = statistics.get(5).getAsJsonObject().get("victories").getAsLong();
        loses = statistics.get(6).getAsJsonObject().get("loses").getAsLong();
        damageDealt = statistics.get(7).getAsJsonObject().get("damageDealt").getAsDouble();
        damageReceived = statistics.get(8).getAsJsonObject().get("damageReceived").getAsDouble();
        JsonArray nameHistory = profile.get(2).getAsJsonObject().getAsJsonArray("nameHistory");
        if(!nameHistory.contains(new JsonPrimitive(player.getName()))) nameHistory.add(player.getName());
        JsonArray discord = data.get(2).getAsJsonObject().getAsJsonArray("discord");
        discordId = discord.get(0).getAsJsonObject().get("linkId").getAsLong();
        DataUtils.writeJSONObject(file, dataFile);
    }
    
    private void set(JsonArray jsonArray, int index, String property, Number value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(property, value);
        jsonArray.set(index, jsonObject);
    }
}
