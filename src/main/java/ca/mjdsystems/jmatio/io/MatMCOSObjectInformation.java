/*
 * Copyright 2014 Matthew Dawson <matthew@mjdsystems.ca>
 */
package ca.mjdsystems.jmatio.io;

import ca.mjdsystems.jmatio.types.MLStructure;

/**
 *
 * @author Matthew Dawson <matthew@mjdsystems.ca>
 */
class MatMCOSObjectInformation {
    MatMCOSObjectInformation(String className, int classId, int objectId, int segment4PropertiesIndex)
    {
        this.className = className;

        this.objectId = objectId;
        this.classId = classId;

        this.segment4PropertiesIndex = segment4PropertiesIndex;
    }

    final String className;
    final int objectId;
    final int classId;
    final int segment4PropertiesIndex;
    final MLStructure structure = new MLStructure("", new int[]{1,1});
}
