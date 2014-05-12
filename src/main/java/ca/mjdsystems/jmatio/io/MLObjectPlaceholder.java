/*
 * Copyright 2014 Matthew Dawson <matthew@mjdsystems.ca>
 */
package ca.mjdsystems.jmatio.io;

import ca.mjdsystems.jmatio.types.MLArray;

import java.util.Arrays;

/**
 *
 * @author Matthew Dawson <matthew@mjdsystems.ca>
 */
class MLObjectPlaceholder extends MLArray {
    MLObjectPlaceholder(String name, String className, int[] dims, int[][] information)
    {
        super( name, dims, -1, 0 );
        this.className = className;
        this.information = information;

        this.objectIds = new int[information.length - 5];
        for (int i = 0; i < objectIds.length; ++i) {
            objectIds[i] = information[i + 4][0];
        }
        this.classId = information[information.length - 1][0];
    }

    final String className;
    final int[][] information;
    final int[] objectIds;
    final int classId;
}
