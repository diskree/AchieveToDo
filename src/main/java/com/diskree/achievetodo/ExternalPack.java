package com.diskree.achievetodo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public enum ExternalPack {

    BACAP(
        "BlazeandCave's Advancements Pack",
        Formatting.LIGHT_PURPLE,
        "https://modrinth.com/datapack/blazeandcaves-advancements-pack",
        "https://cdn.modrinth.com/data/VoVJ47kN/versions/i8N5hYLH/BlazeandCave%27s%20Advancements%20Pack%201.18.1.zip",
        null,
        "45b8bb0076bbf5b92fde7dc9590c6686937abbc0"
    ),
    BACAP_HARDCORE(
        "BlazeandCave's Advancements Pack (Hardcore version)",
        Formatting.RED,
        "https://modrinth.com/datapack/blazeandcaves-advancements-pack-hardcore-version",
        "https://cdn.modrinth.com/data/QEv1xmKi/versions/uRKM9Bou/BlazeandCave%27s%20Advancements%20Pack%20Hardcore.zip",
        null,
        "ec5203496a822e6145562cd81e781ca0eea2c968"
    ),
    BACAP_TERRALITH(
        "BlazeandCave's Advancements Pack (Terralith version)",
        Formatting.GREEN,
        "https://www.planetminecraft.com/data-pack/blazeandcave-s-advancements-pack-terralith-version/",
        "https://www.mediafire.com/file/ljb8qwofxk4dq9i/%255BUNZIP_ME%255D_BlazeandCave%2527s_Advancements_Pack_Terralith_1.18.zip/file",
        "2699070cf5040ab519c223178ee64ee9eafe3691",
        "3d8cc170c1bf2a00460a8d7e779acbe9d5034dea"
    ),
    BACAP_AMPLIFIED_NETHER(
        "BlazeandCave's Advancements Pack (Amplified Nether version)",
        Formatting.DARK_RED,
        "https://www.planetminecraft.com/data-pack/blazeandcave-s-advancements-pack-terralith-version/",
        "https://www.mediafire.com/file/ak5sjemiz60mzrc/%255BUNZIP_ME%255D_BlazeandCave%2527s_Advancements_Pack_Amplified_Nether_1.18.zip/file",
        "981ff801e3cf7eace1ddc2fff8b6165c12ea52b0",
        "9956d0a7d26e0b7d166711fa4b6bf856d2993a44"
    ),
    BACAP_NULLSCAPE(
        "BlazeandCave's Advancements Pack (Nullscape version)",
        Formatting.DARK_PURPLE,
        "https://www.planetminecraft.com/data-pack/blazeandcave-s-advancements-pack-terralith-version/",
        "https://www.mediafire.com/file/hsj4koctw778e43/%255BUNZIP_ME%255D_BlazeandCave%2527s_Advancements_Pack_Nullscape_1.18.zip/file",
        "029c29644a9e94dd8c4111dc3ab2165e79fe4d66",
        "6a50de576558b6b9079a60ffdff73cd9e622eac1"
    ),
    TERRALITH(
        "Terralith (World Generation Pack)",
        Formatting.GREEN,
        "https://www.planetminecraft.com/data-pack/terralith-overworld-evolved-100-biomes-caves-and-more/",
        "https://github.com/Stardust-Labs-MC/Terralith/releases/download/v2.4.11/Terralith_1.20.4_v2.4.11.zip",
        null,
        "c7e7f40e6a4da281bbd26eff16a3c7417b096b85"
    ),
    AMPLIFIED_NETHER(
        "Amplified Nether (World Generation Pack)",
        Formatting.DARK_RED,
        "https://www.planetminecraft.com/data-pack/amplified-nether-1-18/",
        "https://github.com/Stardust-Labs-MC/Amplified-Nether/releases/download/v1.2.4/Amplified_Nether_1.20.4_v1.2.4.zip",
        null,
        "c67ce6110fe14271da660598384cffb44591c49b"
    ),
    NULLSCAPE(
        "Nullscape (World Generation Pack)",
        Formatting.DARK_PURPLE,
        "https://www.planetminecraft.com/data-pack/nullscape/",
        "https://github.com/Stardust-Labs-MC/Nullscape/releases/download/v1.2.4/Nullscape_1.20.4_v1.2.4.zip",
        null,
        "2554559b4ab7d498c64ba5157515cd3f16d77d6d"
    );

    private final String name;
    private final Formatting color;
    private final String pageUrl;
    private final String downloadUrl;
    private final String wrapperSha1;
    private final String sha1;

    ExternalPack(String name, Formatting color, String pageUrl, String downloadUrl, String wrapperSha1, String sha1) {
        this.name = name;
        this.color = color;
        this.pageUrl = pageUrl;
        this.downloadUrl = downloadUrl;
        this.wrapperSha1 = wrapperSha1;
        this.sha1 = sha1;
    }

    public String getName() {
        return name;
    }

    public Formatting getColor() {
        return color;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getWrapperSha1() {
        return wrapperSha1;
    }

    public String getSha1() {
        return sha1;
    }

    public String getFileName() {
        return name().toLowerCase() + ".zip";
    }

    public @NotNull String getDatapackName() {
        return "file/" + getFileName();
    }
}
