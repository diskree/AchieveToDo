package com.diskree.achievetodo.gui;

import com.diskree.achievetodo.ExternalPack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Environment(EnvType.CLIENT)
public class DownloadExternalPackScreen extends ConfirmScreen {

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_MARGIN = 5;

    private final Screen parent;
    private final ExternalPack externalPack;
    private final BooleanConsumer exitCallback;
    private boolean exitWithCreateLevel;

    public DownloadExternalPackScreen(Screen parent, ExternalPack externalPack, BooleanConsumer exitCallback) {
        super(
                null,
                Text.translatable("achievetodo.external_pack.required_prefix").append(Text.of(externalPack.getName()).copy().formatted(externalPack.getColor(), Formatting.ITALIC)),
                Text.translatable("achievetodo.external_pack.help").copy().formatted(Formatting.YELLOW)
        );
        this.parent = parent;
        this.externalPack = externalPack;
        this.exitCallback = exitCallback;
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
            exitCallback.accept(exitWithCreateLevel);
        }
    }

    @Override
    protected void addButtons(int y) {
        int selectFileButtonX = (width - BUTTON_WIDTH) / 2;

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("achievetodo.external_pack.download"),
                button -> Util.getOperatingSystem().open(externalPack.getDownloadUrl())
        ).dimensions(selectFileButtonX - BUTTON_MARGIN - BUTTON_WIDTH, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("achievetodo.external_pack.select_file"),
                button -> {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        PointerBuffer filters = stack.mallocPointer(1);
                        filters.put(0, stack.UTF8("*.zip"));

                        String selectedFilePath = TinyFileDialogs.tinyfd_openFileDialog(
                                Text.translatable("achievetodo.external_pack.picker").getString(),
                                System.getProperty("user.home"),
                                filters,
                                null,
                                false
                        );
                        if (selectedFilePath != null) {
                            handleDatapackFile(Paths.get(selectedFilePath));
                        }
                    }
                }
        ).dimensions(selectFileButtonX, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("achievetodo.external_pack.open_page"),
                button -> Util.getOperatingSystem().open(externalPack.getPageUrl())
        ).dimensions(selectFileButtonX + BUTTON_WIDTH + BUTTON_MARGIN, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());

        addDrawableChild(ButtonWidget.builder(
                ScreenTexts.CANCEL,
                button -> close()
        ).dimensions(selectFileButtonX + BUTTON_WIDTH + BUTTON_MARGIN, y + BUTTON_HEIGHT + BUTTON_MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onFilesDropped(List<Path> paths) {
        super.onFilesDropped(paths);
        if (paths == null || paths.size() != 1) {
            return;
        }
        handleDatapackFile(paths.getFirst());
    }

    private void handleDatapackFile(Path path) {
        if (client == null) {
            return;
        }
        boolean isWrapper;
        try {
            String sha1 = calculateSHA1(path);
            if (sha1 == null) {
                return;
            }
            isWrapper = sha1.equals(externalPack.getWrapperSha1());
            if (!isWrapper && !sha1.equalsIgnoreCase(externalPack.getSha1())) {
                client.setScreen(new ErrorScreen(
                        DownloadExternalPackScreen.this,
                        Text.translatable("achievetodo.external_pack.error")
                ));
                return;
            }
        } catch (Exception e) {
            return;
        }
        Path globalPacksDir = new File(client.runDirectory, "datapacks").toPath();
        try {
            if (Files.notExists(globalPacksDir)) {
                Files.createDirectory(globalPacksDir);
            }
            if (isWrapper) {
                Path extractedArchive = unzip(path, globalPacksDir);
                Files.move(extractedArchive, globalPacksDir.resolve(externalPack.getFileName()));
            } else {
                Files.copy(path, globalPacksDir.resolve(externalPack.getFileName()));
            }
        } catch (IOException e) {
            return;
        }
        exitWithCreateLevel = true;
        close();
    }

    private Path unzip(Path source, Path destination) throws IOException {
        Path firstExtractedFile = null;
        try (ZipFile zipFile = new ZipFile(source.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path outputPath = destination.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outputPath);
                } else {
                    if (firstExtractedFile == null) {
                        firstExtractedFile = outputPath;
                    }
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = Files.newOutputStream(outputPath)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
        return firstExtractedFile;
    }

    private String calculateSHA1(Path path) throws NoSuchAlgorithmException, IOException {
        if (path == null || Files.notExists(path)) {
            return null;
        }
        MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");
        try (InputStream is = Files.newInputStream(path)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                sha1Digest.update(buffer, 0, bytesRead);
            }
            byte[] bytes = sha1Digest.digest();
            return bytesToHex(bytes);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toLowerCase();
    }
}