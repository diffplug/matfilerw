/*
 * Copyright 2014 Matthew Dawson <matthew@mjdsystems.ca>
 */
package ca.mjdsystems.jmatio.io;

import ca.mjdsystems.jmatio.types.MLArray;

/**
 *
 * @author Matthew Dawson <matthew@mjdsystems.ca>
 */
class MLObjectPlaceholder extends MLArray {
    MLObjectPlaceholder( String name, String className, int[][] information )
    {
        super( name, new int[] {1, 1}, -1, 0 );
        this.className = className;
        this.information = information;
    }

    final String className;
    final int[][] information;
}
