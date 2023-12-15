package websimilaritiespj3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a serializable version of ArrayList. It extends ArrayList
 * and ensures that
 * all elements stored in it are serializable. This class is particularly useful
 * when you need to
 * serialize data structures that contain lists of serializable items.
 * 
 * @param <T> the type of elements in this list, which must be Serializable
 * @author Joel Santos
 * @version 3.0
 * @since 11-10-2023
 */
public class SerializableList<T extends Serializable> extends ArrayList<T> {

    /**
     * Constructs an empty SerializableList.
     */
    public SerializableList() {
        super();
    }

    /**
     * Constructs a SerializableList containing the elements of the specified list.
     *
     * @param list the list whose elements are to be placed into this
     *             SerializableList
     */
    public SerializableList(List<T> list) {
        super(list);
    }
}
