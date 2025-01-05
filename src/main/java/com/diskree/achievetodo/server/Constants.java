package com.diskree.achievetodo.server;

import com.diskree.achievetodo.BuildConfig;

public class Constants {

    public static class NbtKey {
        public static final String LEVEL_CONFIG_NAME = BuildConfig.MOD_ID + "_" + "configName";
        public static final String FEATURE_LANDMARKS = BuildConfig.MOD_ID + "_" + "featureLandmarks";
        public static final String STRUCTURE_LANDMARK = BuildConfig.MOD_ID + "_" + "landmark";

        public static final String LANDMARK_TYPE = "landmarkType";
        public static final String BLOCK_BOX_MIN_X = "minX";
        public static final String BLOCK_BOX_MIN_Y = "minY";
        public static final String BLOCK_BOX_MIN_Z = "minZ";
        public static final String BLOCK_BOX_MAX_X = "maxX";
        public static final String BLOCK_BOX_MAX_Y = "maxY";
        public static final String BLOCK_BOX_MAX_Z = "maxZ";
    }

    public static class FileExtension {
        public static final String TOML = ".toml";
        public static final String ZIP = ".zip";
    }
}
