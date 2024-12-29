package com.diskree.achievetodo.ability;

public enum AbilitiesTreeBranchType {

    MAIN(1),
    ACTIONS(2),
    BLOCKS(2),
    UPGRADE(2),
    FOOD(4),
    TRADING(2);

    private final int rowsCount;

    AbilitiesTreeBranchType(int rowsCount) {
        this.rowsCount = rowsCount;
    }

    public int getRowsCount() {
        return rowsCount;
    }
}
