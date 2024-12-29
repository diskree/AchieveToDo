package com.diskree.achievetodo.ability.generation;

import com.diskree.achievetodo.ability.AbilityType;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class AbilityUnlockMessagesGenerator implements DataProvider {

    protected final FabricDataOutput dataOutput;

    protected AbilityUnlockMessagesGenerator(FabricDataOutput dataOutput) {
        this.dataOutput = dataOutput;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return CompletableFuture.runAsync(() -> {
            try {
                createFunctions(writer);
            } catch (IOException ignored) {
            }
        }, Util.getMainWorkerExecutor());
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private void createFunctions(DataWriter dataWriter) throws IOException {
        for (AbilityType ability : AbilityType.values()) {
            String function = buildFunction(ability);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
            try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8))
            ) {
                bufferedWriter.write(function);
            }
            Path functionsPath = dataOutput
                .getResolver(DataOutput.OutputType.DATA_PACK, "function")
                .resolve(AbilityAdvancementsGenerator.buildAdvancementId(ability), "mcfunction");
            dataWriter.write(functionsPath, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
        }
    }

    private @NotNull String buildFunction(@NotNull AbilityType ability) {
        String function = """
            tellraw @a {
                "translate":"achievetodo.ability_unlocked_chat_message",
                "with":[
                    {
                        "selector":"@s"
                    },
                    {
                        "color":"yellow",
                        "text":"["
                    },
                    {
                        "color":"yellow",
                        "translate":"achievetodo.ability.{NAME}.name",
                        "clickEvent":{
                            "action":"run_command",
                            "value":"/advancementssearch highlight {ADVANCEMENT_ID} obtained_status"
                        },
                        "hoverEvent":{
                            "action":"show_text",
                            "contents":{
                                "color":"yellow",
                                "translate":"achievetodo.ability.{NAME}.name",
                                "extra":[
                                    {
                                        "text":"\\n"
                                    },
                                    {
                                        "color":"yellow",
                                        "translate":"achievetodo.ability.{NAME}.description"
                                    },
                                    {
                                        "text":"\\n\\n"
                                    },
                                    {
                                        "color":"gray",
                                        "italic":true,
                                        "translate":"%1$s tab",
                                        "with":[
                                            {
                                                "text":"AchieveToDo"
                                            }
                                        ]
                                    }
                                ]
                            }
                        }
                    },
                    {
                        "color":"yellow",
                        "text":"]"
                    }
                ]
            }
            """
            .replace("{NAME}", ability.getLowerCaseName())
            .replace("{ADVANCEMENT_ID}", AbilityAdvancementsGenerator.buildAdvancementId(ability).toString());
        return String.join("", Arrays.stream(function.split("\\R")).map(String::trim).toArray(String[]::new));
    }
}
