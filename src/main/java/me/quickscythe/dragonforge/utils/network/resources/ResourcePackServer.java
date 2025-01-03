package me.quickscythe.dragonforge.utils.network.resources;//package me.quickscythe.dragonforge.utils.network.resources;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.quickscythe.dragonforge.utils.CoreUtils;
import me.quickscythe.dragonforge.utils.network.NetworkUtils;
import me.quickscythe.dragonforge.utils.storage.DataManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static net.kyori.adventure.text.Component.text;

public class ResourcePackServer {

    private final File pack;
    private final int port;
    String url = "";
    byte[] hash = new byte[0];

    public ResourcePackServer(int port) {
        pack = new File(DataManager.getDataFolder(), "resources/pack.zip");
        if (!pack.getParentFile().isDirectory()) pack.getParentFile().mkdirs();
        this.port = port;
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/resources.zip", new ResourcePackHandler(this));
            server.createContext("/update", new ResourcePackUpdater(this));
            server.setExecutor(null);
            server.start();
            updatePack();
        } catch (IOException e) {
            CoreUtils.logger().error("ResourcePackServer", e);
        }
    }

    public boolean enabled() {
        return !url.isEmpty();
    }

    public void setUrl(String url) {
        this.url = url;
        updatePack();
    }

    public void updatePack() {
        if (!enabled()) return;
        String[] props = new String[]{CoreUtils.config().getData().getString("jenkins_user"), CoreUtils.config().getData().getString("jenkins_password"), CoreUtils.config().getData().getString("jenkins_url"), CoreUtils.config().getData().getString("jenkins_api_endpoint")};
        try {
            InputStream in = NetworkUtils.downloadFile(url, props[0], props[1]);
            pack.delete();
            FileOutputStream out = new FileOutputStream(pack);
            byte[] buffer = new byte[8192];
            int count;
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (BufferedInputStream bis = new BufferedInputStream(in)) {
                while ((count = bis.read(buffer)) > 0) {
                    digest.update(buffer, 0, count);
                    out.write(buffer, 0, count);
                }
            }
            out.close();
//            NetworkUtils.saveStream(in, out);

            hash = digest.digest();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                Component msg = text("Resource pack updated. Click here to reload.").color(TextColor.color(0x49DFFF));
                msg = msg.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/resourcepack reload"));
                player.sendMessage(msg);
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public File pack() {
        return pack;
    }

    public void setPack(Player player) throws IOException, NoSuchAlgorithmException {
        if(!enabled()) return;
        String url = "http://" + CoreUtils.config().getData().getString("serverIp") + ":" + port + "/resources.zip";

        player.setResourcePack(url, hash, text("This pack is required for the best experience on this server."));
    }

    static class ResourcePackHandler implements HttpHandler {

        private final ResourcePackServer server;

        public ResourcePackHandler(ResourcePackServer server) {
            this.server = server;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = server.pack().getPath(); // Update this path to your file
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));

            // Set the response headers and status code
            exchange.sendResponseHeaders(200, fileBytes.length);

            // Write the file bytes to the response body
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        }
    }

    static class ResourcePackUpdater implements HttpHandler {

        private final ResourcePackServer server;

        public ResourcePackUpdater(ResourcePackServer server) {
            this.server = server;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            server.updatePack();
        }
    }
}
////
////package me.quickscythe.quipt.utils.network.resources;
////
////import com.sun.net.httpserver.HttpExchange;
////import com.sun.net.httpserver.HttpHandler;
////import com.sun.net.httpserver.HttpServer;
////import me.quickscythe.quipt.api.config.ConfigManager;
////import me.quickscythe.quipt.api.config.files.HashesConfig;
////import me.quickscythe.quipt.api.config.files.ResourceConfig;
////import me.quickscythe.quipt.utils.CoreUtils;
////import me.quickscythe.quipt.utils.chat.Logger;
////import net.kyori.adventure.text.Component;
////import net.kyori.adventure.text.event.ClickEvent;
////import net.kyori.adventure.text.format.TextColor;
////import org.bukkit.Bukkit;
////import org.bukkit.entity.Player;
////import org.eclipse.jgit.api.Git;
////import org.eclipse.jgit.api.errors.GitAPIException;
////import org.eclipse.jgit.revwalk.RevCommit;
////import org.eclipse.jgit.revwalk.RevWalk;
////
////import java.io.File;
////import java.io.IOException;
////import java.io.InputStream;
////import java.io.OutputStream;
////import java.net.InetSocketAddress;
////import java.nio.charset.StandardCharsets;
////import java.nio.file.Files;
////import java.nio.file.Path;
////import java.nio.file.Paths;
////import java.security.MessageDigest;
////import java.security.NoSuchAlgorithmException;
////import java.util.Comparator;
////import java.util.zip.ZipEntry;
////import java.util.zip.ZipOutputStream;
////
////import static net.kyori.adventure.text.Component.text;
//
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpServer;
//import me.quickscythe.dragonforge.utils.CoreUtils;
//import me.quickscythe.dragonforge.utils.chat.Logger;
//import me.quickscythe.dragonforge.utils.config.ConfigFile;
//import me.quickscythe.dragonforge.utils.config.ConfigFileManager;
//import me.quickscythe.dragonforge.utils.storage.DataManager;
//import net.kyori.adventure.text.Component;
//import net.kyori.adventure.text.event.ClickEvent;
//import net.kyori.adventure.text.format.TextColor;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.eclipse.jgit.api.Git;
//import org.eclipse.jgit.api.errors.GitAPIException;
//import org.eclipse.jgit.revwalk.RevCommit;
//import org.eclipse.jgit.revwalk.RevWalk;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Comparator;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//import static net.kyori.adventure.text.Component.text;
//
//public class ResourcePackServer {
//
//    private final File pack;
//    private final File repo;
//
//    private final ConfigFile hashData;
//    private final int port;
//    String url = "";
//    private boolean serverStarted = false;
//    private byte[] storedHash = new byte[0];
//
////    byte[] hash = new byte[0];
//
//    public ResourcePackServer(int port) {
//        this.port = port;
//        hashData = ConfigFileManager.getFile(CoreUtils.plugin(), "resource_hashes");
//        if(!hashData.getData().has("commit_hash")) hashData.getData().put("commit_hash", "");
//        if (!hashData.getData().has("encrypted_zip_hash")) hashData.getData().put("encrypted_zip_hash", "");
//        hashData.save();
//        pack = new File(DataManager.getDataFolder(), "resources/pack.zip");
//        repo = new File(DataManager.getDataFolder(), "resources/repo/");
//        if (!pack.getParentFile().isDirectory())
//            CoreUtils.logger().log(Logger.LogLevel.INFO, "Resources", pack.getParentFile().mkdirs() ? "Set up 'pack.zip' parents." : "Couldn't set up 'pack.zip' parents.");
//        if (!repo.exists())
//            CoreUtils.logger().log(Logger.LogLevel.INFO, "Resources", repo.mkdirs() ? "Set up 'repo' directory." : "Couldn't set up 'repo' directory.");
//        if (pack.exists()) {
//            startServer();
//        }
//    }
//
//    private void startServer() {
//        try {
//
//            //Setup server
//            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
//            server.createContext("/resources.zip", new ResourcePackHandler(this));
//            server.createContext("/update", new ResourcePackUpdater(this));
//            server.setExecutor(null);
//            server.start();
//
//            //
//            MessageDigest digest = MessageDigest.getInstance("SHA-1");
//            try (InputStream in = Files.newInputStream(pack.toPath())) {
//                byte[] buffer = new byte[8192];
//                int count;
//                while ((count = in.read(buffer)) > 0) {
//                    digest.update(buffer, 0, count);
//                }
//            }
//            storedHash = digest.digest();
//            serverStarted = true;
//            //sync
//            sync();
//        } catch (IOException | NoSuchAlgorithmException e) {
//            CoreUtils.logger().error("ResourcePackServer", e);
//        }
//    }
//
//    public void sync() {
//        CoreUtils.logger().log("Resources", "Syncing resource pack.");
//        if (repo.exists() && new File(repo, ".git").exists()) updateRepo();
//        else cloneRepo();
//    }
//
//    private void updateRepo() {
//        CoreUtils.logger().log("Resources", "Updating resource pack.");
//        try {
//            Git git = Git.open(repo);
//            git.pull().call();
//            RevCommit commit = new RevWalk(git.getRepository()).parseCommit(git.getRepository().findRef("HEAD").getObjectId());
//            git.close();
//
//            zip(commit);
//        } catch (GitAPIException | IOException e) {
//            CoreUtils.logger().error("Resources", e);
//        }
//    }
//
//    private void cloneRepo() {
//        if (!enabled()) return;
//        CoreUtils.logger().log("Resources", "Cloning resource pack.");
//        try {
//            Git git = Git.cloneRepository().setURI(url).setDirectory(repo).setBranch("master").call();
//
//            CoreUtils.logger().log("Resources", "Cloned resource pack.");
//            RevCommit commit = new RevWalk(git.getRepository()).parseCommit(git.getRepository().findRef("HEAD").getObjectId());
//            git.close();
//            zip(commit);
//        } catch (GitAPIException e) {
//            CoreUtils.logger().log("Resources", "Error cloning repository: " + e.getMessage());
//            CoreUtils.logger().error("Resources", e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void zip(RevCommit commit) {
//        CoreUtils.logger().log("Resources", "Zipping resource pack.");
//        try {
//            zipFolder(repo.toPath(), pack.toPath());
//            MessageDigest digest = MessageDigest.getInstance("SHA-1");
//            try (InputStream in = Files.newInputStream(pack.toPath())) {
//                byte[] buffer = new byte[8192];
//                int count;
//                while ((count = in.read(buffer)) > 0) {
//                    digest.update(buffer, 0, count);
//                }
//            }
//            byte[] newHash = digest.digest();
//
//            String newEncryptedHash = new String(newHash, StandardCharsets.UTF_8);
//
//            String newCommitHash = commit.getId().getName();
//
//            if (newEncryptedHash.equals(hashData.getData().getString("encrypted_zip_hash"))) {
//                CoreUtils.logger().log("Resources", "Resource pack hash matches. Skipping update.");
//                return;
//            }
////            if (hashData.getData().getString("commit_hash").equals(newCommitHash)) {
////                CoreUtils.logger().log("Resources", "Commit hash match. Skipping update.");
////                return;
////            }
//            storedHash = newHash;
//            hashData.getData().put("commit_hash", newCommitHash);
//            hashData.getData().put("encrypted_zip_hash", newEncryptedHash);
//            hashData.save();
//            CoreUtils.logger().log("Resources", "Resource pack hash mismatch. Updating pack.");
//            updatePack();
//        } catch (IOException | NoSuchAlgorithmException e) {
//            CoreUtils.logger().log("Resources", "Error zipping resource pack: " + e.getMessage());
//            CoreUtils.logger().error("Resources", e);
//        }
//    }
//
//    public boolean enabled() {
//        return !url.isEmpty();
//    }
//
//    public void setUrl(String url) {
//        if (url.isEmpty()) return;
//        this.url = url;
//        if (!serverStarted) startServer();
//        CoreUtils.logger().log("Resources", "Resource pack URL changed. Updating pack.");
//        try {
//            Files.walk(repo.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
//            cloneRepo();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    public void updatePack() {
//        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
//            Component msg = text("Resource pack updated. Click here to reload.").color(TextColor.color(0x49DFFF));
//            msg = msg.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/resourcepack reload"));
//            player.sendMessage(msg);
//        }
//    }
//
//    public File pack() {
//        return pack;
//    }
//
//    public void setPack(Player player) throws IOException, NoSuchAlgorithmException {
//        if (!enabled()) return;
//        String url = "http://" + CoreUtils.config().getData().getString("serverIp") + ":" + port + "/resources.zip";
//        CoreUtils.logger().log("Resources", "Setting resource pack for " + player.getName());
//        player.setResourcePack(url, storedHash, text("This pack is required for the best experience on this server."));
//    }
//
//    private void zipFolder(Path sourceFolderPath, Path zipPath) throws IOException {
//        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
//            Files.walk(sourceFolderPath).filter(path -> !Files.isDirectory(path)).forEach(path -> {
//                ZipEntry zipEntry = new ZipEntry(sourceFolderPath.relativize(path).toString());
//                try {
//                    zos.putNextEntry(zipEntry);
//                    Files.copy(path, zos);
//                    zos.closeEntry();
//                } catch (IOException e) {
//                    System.err.println("Error zipping file: " + path + " - " + e.getMessage());
//                }
//            });
//        }
//    }
//
//
//    static class ResourcePackHandler implements HttpHandler {
//
//        private final ResourcePackServer server;
//
//        public ResourcePackHandler(ResourcePackServer server) {
//            this.server = server;
//        }
//
//
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            String filePath = server.pack().getPath(); // Update this path to your file
//            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
//
//            // Set the response headers and status code
//            exchange.sendResponseHeaders(200, fileBytes.length);
//
//            // Write the file bytes to the response body
//            OutputStream os = exchange.getResponseBody();
//            os.write(fileBytes);
//            os.close();
//        }
//    }
//
//    static class ResourcePackUpdater implements HttpHandler {
//
//        private final ResourcePackServer server;
//
//        public ResourcePackUpdater(ResourcePackServer server) {
//            this.server = server;
//        }
//
//
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            server.sync();
//        }
//    }
//}
//
