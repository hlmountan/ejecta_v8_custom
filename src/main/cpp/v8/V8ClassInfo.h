//
// Created by Martin Kleinhans on 18.08.17.
//

#ifndef TRADINGLIB_SAMPLE_V8CLASSINFO_H
#define TRADINGLIB_SAMPLE_V8CLASSINFO_H

#include "../bgjs/BGJSV8Engine.h"

class V8ClassInfo;
class V8ClassInfoContainer;
class JNIV8Object;

// these declarations use JNIV8Object, but the type is valid for methods on all subclasses, see:
// http://www.open-std.org/jtc1/sc22/WG21/docs/wp/html/nov97-2/expr.html#expr.static.cast
// there is no implicit conversion however, so methods of derived classes have to be casted manually
typedef void(JNIV8Object::*JNIV8ObjectConstructorCallback)(const v8::FunctionCallbackInfo<v8::Value>& args);
typedef void(JNIV8Object::*JNIV8ObjectMethodCallback)(const std::string &methodName, const v8::FunctionCallbackInfo<v8::Value>& args);
typedef void(JNIV8Object::*JNIV8ObjectAccessorGetterCallback)(const std::string &propertyName, const v8::PropertyCallbackInfo<v8::Value> &info);
typedef void(JNIV8Object::*JNIV8ObjectAccessorSetterCallback)(const std::string &propertyName, v8::Local<v8::Value> value, const v8::PropertyCallbackInfo<void> &info);

struct V8ClassInfo {
    friend class JNIV8Object;
    friend class JNIV8Wrapper;
public:
    BGJSV8Engine* getEngine() const {
        return engine;
    };

    void registerConstructor(JNIV8ObjectConstructorCallback callback);
    void registerMethod(const std::string& methodName, JNIV8ObjectMethodCallback callback);
    void registerAccessor(const std::string& propertyName, JNIV8ObjectAccessorGetterCallback getter, JNIV8ObjectAccessorSetterCallback setter = 0, v8::PropertyAttribute settings = v8::None);
    v8::Local<v8::FunctionTemplate> getFunctionTemplate() const;
    v8::Local<v8::Function> getConstructor() const;
private:
    V8ClassInfo(V8ClassInfoContainer *container, BGJSV8Engine *engine);

    BGJSV8Engine *engine;
    V8ClassInfoContainer *container;
    v8::Persistent<v8::FunctionTemplate> functionTemplate;
    JNIV8ObjectConstructorCallback constructorCallback;
};

// internal helper methods for creating and initializing objects
typedef void(*JNIV8ObjectInitializer)(V8ClassInfo *info);
typedef std::shared_ptr<JNIV8Object>(*JNIV8ObjectCreator)(V8ClassInfo *info, v8::Persistent<v8::Object> *jsObj);

/**
 * internal container object for managing all class info instances (one for each v8 engine) of an object
 */
struct V8ClassInfoContainer {
    friend class JNIV8Wrapper;
private:
    V8ClassInfoContainer(const std::string& canonicalName, JNIV8ObjectInitializer i, JNIV8ObjectCreator c, size_t size);

    size_t size;
    std::string canonicalName;
    JNIV8ObjectInitializer initializer;
    JNIV8ObjectCreator creator;
    std::vector<V8ClassInfo*> classInfos;
};

/**
 * internal struct for storing member function pointers for property accessors
 */
struct JNIV8ObjectAccessorHolder {
    JNIV8ObjectAccessorHolder(const std::string &name, JNIV8ObjectAccessorGetterCallback getterCb, JNIV8ObjectAccessorSetterCallback setterCb) {
        propertyName = name;
        getterCallback = getterCb;
        setterCallback = setterCb;
    }
    JNIV8ObjectAccessorGetterCallback getterCallback;
    JNIV8ObjectAccessorSetterCallback setterCallback;
    std::string propertyName;
};

/**
 * internal struct for storing member function pointers for methods
 */
struct JNIV8ObjectCallbackHolder {
    JNIV8ObjectCallbackHolder(const std::string &name, JNIV8ObjectMethodCallback cb) {
        callback = cb;
        methodName = name;
    }
    JNIV8ObjectMethodCallback callback;
    std::string methodName;
};



#endif //TRADINGLIB_SAMPLE_V8CLASSINFO_H
