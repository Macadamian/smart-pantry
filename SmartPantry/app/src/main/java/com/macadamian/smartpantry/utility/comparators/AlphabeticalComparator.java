package com.macadamian.smartpantry.utility.comparators;

import java.util.Comparator;

public class AlphabeticalComparator implements Comparator<String> {

    public enum SortType {
        TYPE_ASC,
        TYPE_DESC
    };

    private final SortType mSortType;

    public AlphabeticalComparator(final SortType sortType) {
        mSortType = sortType;
    }

    public AlphabeticalComparator() {
        mSortType = SortType.TYPE_ASC;
    }

    @Override
    public int compare(String lhs, String rhs) {
        return mSortType == SortType.TYPE_ASC ? lhs.compareTo(rhs) : rhs.compareTo(lhs);
    }
}
