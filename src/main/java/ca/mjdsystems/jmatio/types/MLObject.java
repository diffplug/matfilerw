package ca.mjdsystems.jmatio.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MLObject extends MLArray
{
    private final List<Map<String, MLArray>> objects = new ArrayList<Map<String, MLArray>>();
    private final String className;
    
    public MLObject(String name, String className, int[] dimensions, int attributes)
    {
        super( name, dimensions, MLArray.mxOBJECT_CLASS, 0 );
        for (int i = 0; i < getSize(); ++i) {
            objects.add(null);
        }
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }
    
    public Map<String, MLArray> getFields(int index)
    {
        return objects.get(index);
    }

    public MLObject setFields(int index, Map<String, MLArray> fields)
    {
        if (index >= getSize()) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + getSize());
        }
        objects.set(index, fields);
        return this;
    }
}
