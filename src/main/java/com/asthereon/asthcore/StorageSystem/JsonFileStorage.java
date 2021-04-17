package com.asthereon.asthcore.StorageSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minestom.server.storage.StorageOptions;
import net.minestom.server.storage.StorageSystem;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class JsonFileStorage implements StorageSystem {

    private final String FOLDER = "jsonFileStorage";
    private File file;
    private JSONObject json;
    private final JSONParser parser = new JSONParser();
    private TreeMap<String, String> treeMap;

    public String getFolder() {
        return FOLDER;
    }

    @Override
    public boolean exists(@NotNull String location) {
        if (file == null) {
            file = new File(getFolder(), location + ".json");
        }
        return file.getParentFile().exists() && file.exists();
    }

    @Override
    public void open(@NotNull String location, @NotNull StorageOptions storageOptions) {
        if (file == null) {
            file = new File(getFolder(), location + ".json");
        }
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                createDataFile();
            }
            this.json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            treeMap = new TreeMap<>();
            treeMap.putAll(this.json);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createDataFile() {
        try {
            PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8);
            pw.print("{");
            pw.print("}");
            pw.flush();
            pw.close();
            this.json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            treeMap = new TreeMap<>();
            treeMap.putAll(this.json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public @Nullable byte[] get(@NotNull String key) {
        String toDecode = this.treeMap.get(key);
        return Base64.decodeBase64(toDecode);
    }

    @Override
    public void set(@NotNull String key, byte[] data) {
        String encoded = Base64.encodeBase64String(data);
        this.treeMap.put(key, encoded);
        save();
    }

    @Override
    public void delete(@NotNull String key) {
        this.treeMap.remove(key);
        save();
    }

    @Override
    public void close() {
        save();
    }

    private void save() {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        String prettyJsonString = g.toJson(treeMap);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(prettyJsonString);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
