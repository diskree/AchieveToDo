package com.diskree.achievetodo;

public enum AbilitiesBranchType {

    MAIN(1),
    ACTIONS(2),
    BLOCKS(2),
    UPGRADE(2),
    FOOD(4),
    TRADING(2);

    private final int sublistCount;

    AbilitiesBranchType(int sublistCount) {
        this.sublistCount = sublistCount;
    }

    public int getSublistCount() {
        return sublistCount;
    }
}
