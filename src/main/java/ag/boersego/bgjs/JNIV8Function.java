package ag.boersego.bgjs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ag.boersego.v8annotations.V8Flags;

/**
 * Created by martin on 26.09.17.
 */

final public class JNIV8Function extends JNIV8Object {
    public interface Handler {
        Object Callback(Object receiver, Object[] arguments);
    }

    public static native JNIV8Function Create(V8Engine engine, JNIV8Function.Handler handler);

    public @Nullable Object callAsV8Function(@Nullable Object... arguments) {
        return _callAsV8Function(false, 0, 0, Object.class,null, arguments);
    }

    public @Nullable Object applyAsV8Function(@NonNull Object[] arguments) {
        return _callAsV8Function(false, 0, 0, Object.class,null, arguments);
    }

    public @Nullable
    Object callAsV8FunctionWithReceiver(@NonNull Object receiver, @Nullable Object... arguments) {
        return _callAsV8Function(false, 0, 0, Object.class, receiver, arguments);
    }

    public @Nullable Object applyAsV8FunctionWithReceiver(@NonNull Object receiver, @NonNull Object[] arguments) {
        return _callAsV8Function(false, 0, 0, Object.class, receiver, arguments);
    }

    public @NonNull Object callAsV8Constructor(@Nullable Object... arguments) {
        return _callAsV8Function(true, V8Flags.NonNull, 0, Object.class,null, arguments);
    }

    public @NonNull Object applyAsV8Constructor(@Nullable Object[] arguments) {
        return _callAsV8Function(true, V8Flags.NonNull, 0, Object.class, null, arguments);
    }


    @SuppressWarnings("unchecked")
    public @Nullable <T> T callAsV8Function(int flags, @NonNull Class<T> returnType, @Nullable Object... arguments) {
        return (T)_callAsV8Function(false, flags, returnType.hashCode(), returnType, null, arguments);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T callAsV8FunctionWithReceiver(int flags, @NonNull Class<T> returnType, @NonNull Object receiver, @Nullable Object... arguments) {
        return (T)_callAsV8Function(false, flags, returnType.hashCode(), returnType, receiver, arguments);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T applyAsV8Function(int flags, @NonNull Class<T> returnType, @NonNull Object[] arguments) {
        return (T)_callAsV8Function(false, flags, returnType.hashCode(), returnType,null, arguments);
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T applyAsV8FunctionWithReceiver(int flags, @NonNull Class<T> returnType, @NonNull Object receiver, @NonNull Object[] arguments) {
        return (T)_callAsV8Function(false, flags, returnType.hashCode(), returnType, receiver, arguments);
    }

    public void dispose() throws RuntimeException {
        super.dispose();
    }

    //------------------------------------------------------------------------
    // internal fields & methods
    private native Object _callAsV8Function(boolean asConstructor, int flags, int type, Class returnType, Object receiver, Object... arguments);

    protected JNIV8Function(V8Engine engine, long jsObjPtr, Object[] arguments) {
        super(engine, jsObjPtr, arguments);
    }
}
