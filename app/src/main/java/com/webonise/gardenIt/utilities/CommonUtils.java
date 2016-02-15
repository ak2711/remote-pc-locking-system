package com.webonise.gardenIt.utilities;

import java.util.Collection;

public class CommonUtils {

    public static <E> boolean isEmpty(final Collection<E> list)
    {
        return list == null || list.isEmpty();
    }

    public static <E> boolean isEmpty(final Object object)
    {
        return object == null;
    }
}
