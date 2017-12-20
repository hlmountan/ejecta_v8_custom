package ag.boersego.bgjs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by martin on 26.09.17.
 */

final public class JNIV8Array extends JNIV8Object implements Iterable<Object> {
    public static native JNIV8Array Create(V8Engine engine);
    public static native JNIV8Array CreateWithLength(V8Engine engine, int length);
    public static native JNIV8Array CreateWithArray(V8Engine engine, Object[] elements);
    public static JNIV8Array CreateWithElements(V8Engine engine, Object... elements) {
        return CreateWithArray(engine, elements);
    }

    public boolean isEmpty() {
        return getV8Length() == 0;
    }

    /**
     * returns the length of the array
     */
    public native int getV8Length();

    /**
     * Returns all objects inside of the array
     */
    public @NonNull Object[] getV8Elements() {
        return _getV8Elements(0, 0, Object.class,0, Integer.MAX_VALUE);
    }
    @SuppressWarnings({"unchecked"})
    public @NonNull <T> T[] getV8Elements(int flags, @NonNull Class<T> returnType) {
        return (T[]) _getV8Elements(flags, returnType.hashCode(), returnType, 0, Integer.MAX_VALUE);
    }

    /**
     * Returns all objects from a specified range inside of the array
     */
    public @NonNull Object[] getV8Elements(int from, int to) {
        return _getV8Elements( 0, 0, Object.class, from, to);
    }
    @SuppressWarnings({"unchecked"})
    public @NonNull <T> T[] getV8Elements(int flags, @NonNull Class<T> returnType, int from, int to) {
        return (T[]) _getV8Elements(flags, returnType.hashCode(), returnType, from, to);
    }

    /**
     * Returns the object at the specified index
     * if index is out of bounds, returns JNIV8Undefined
     */
    public @Nullable Object getV8Element(int index) {
        return _getV8Element(0, 0, Object.class, index);
    }
    @SuppressWarnings({"unchecked"})
    public @Nullable <T> T getV8Element(int flags, @NonNull Class<T> returnType, int index) {
        return (T) _getV8Element(flags, returnType.hashCode(), returnType, index);
    }

    /**
     * releases the JS array
     * when working with a lot of objects, calling this manually might improve memory usage
     * using this method is completely optional
     *
     * NOTE: object must not be used anymore after calling this method
     */
    public void dispose() throws RuntimeException {
        super.dispose();
    }

    //------------------------------------------------------------------------
    // internal fields & methods
    private native Object _getV8Element(int flags, int type, Class returnType, int index);
    private native Object[] _getV8Elements(int flags, int type, Class returnType, int from, int to);

    protected JNIV8Array(V8Engine engine, long jsObjPtr, Object[] arguments) {
        super(engine, jsObjPtr, arguments);
    }

    @NonNull
    @Override
    public Iterator iterator() {
        return new Iterator();
    }

    public class Iterator implements java.util.Iterator<Object> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < getV8Length();
        }

        @Override
        public Object next() {
            index++;
            return getV8Element(index);
        }
    }
}
