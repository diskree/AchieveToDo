package com.diskree.achievetodo;

public enum TreeLine {
    MAIN(1),
    ACTIONS(2),
    BLOCKS(2),
    UPGRADE(2),
    FOOD(4),
    TRADING(2);

    private final int sublistCount;

    TreeLine(int sublistCount) {
        this.sublistCount = sublistCount;
    }

    public int getSublistCount() {
        return sublistCount;
    }
}
