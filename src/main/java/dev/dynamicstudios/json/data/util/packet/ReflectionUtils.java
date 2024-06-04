package dev.dynamicstudios.json.data.util.packet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {

  private static final HashMap<Class<?>, HashMap<CacheHolder, Method>> METHOD_CACHE = new HashMap<>();
  private static final HashMap<Class<?>, HashMap<CacheHolder, Field>> FIELD_CACHE = new HashMap<>();
  private static final HashMap<Class<?>, HashMap<CacheHolder, Constructor<?>>> CONSTRUCTOR_CACHE = new HashMap<>();

  public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
	  CacheHolder key = new CacheHolder(null, clazz, params, null);
	  if(CONSTRUCTOR_CACHE.containsKey(clazz)) {
		  if(CONSTRUCTOR_CACHE.get(clazz).containsKey(key)) return CONSTRUCTOR_CACHE.get(clazz).get(key);
    }
    Set<Constructor<?>> methods = new HashSet<>();
    Collections.addAll(methods, clazz.getConstructors());
    Collections.addAll(methods, clazz.getDeclaredConstructors());
    for(Constructor<?> constructor : methods) {
      if(constructor.getParameterCount() == params.length) {
        boolean match = true;
        for(int i = 0; i < constructor.getParameterTypes().length; i++)
          if(params[i] == null || !constructor.getParameterTypes()[i].getName().equals(params[i].getName())) {
            match = false;
            break;
          }
	      HashMap<CacheHolder, Constructor<?>> map = CONSTRUCTOR_CACHE.getOrDefault(clazz, new HashMap<>());
	      if(match) {
		      map.put(key, constructor);
          CONSTRUCTOR_CACHE.put(clazz, map);
          return constructor;
        }else {
					if(!map.containsKey(key)) {
						map.put(key, null);
						CONSTRUCTOR_CACHE.put(clazz, map);
					}
        }
      }
    }
    return null;
  }

  public static Field getField(Class<?> clazz, Class<?> returnType) {
    if(clazz == null) return null;
    if(FIELD_CACHE.containsKey(clazz) && FIELD_CACHE.get(clazz) != null) {
      CacheHolder cache = new CacheHolder(null, returnType, null, null);
      if(FIELD_CACHE.get(clazz).containsKey(cache)) return FIELD_CACHE.get(clazz).get(cache);
    }
    Set<Field> fields = new HashSet<>();
    Collections.addAll(fields, clazz.getFields());
	  Collections.addAll(fields, clazz.getDeclaredFields());
		if(clazz.getSuperclass() != null) {
			Collections.addAll(fields, clazz.getSuperclass().getDeclaredFields());
			Collections.addAll(fields, clazz.getSuperclass().getFields());
		}
    for(Field field : fields) {
      if(field.getType().getName().equals(returnType.getName())) {
				field.setAccessible(true);
        HashMap<CacheHolder, Field> map = FIELD_CACHE.getOrDefault(clazz, new HashMap<>());
        map.put(new CacheHolder(null, returnType, null, null), field);
        FIELD_CACHE.put(clazz, map);
        return field;
      }
    }
    return null;
  }

  public static Method getMethod(Class<?> clazz, String name, Class<?>... params) {
    if(clazz == null) return null;
    if(METHOD_CACHE.containsKey(clazz) && METHOD_CACHE.get(clazz) != null) {
      CacheHolder cache = new CacheHolder(name, null, params, null);
      if(METHOD_CACHE.get(clazz).containsKey(cache)) return METHOD_CACHE.get(clazz).get(cache);
    }
    Set<Method> methods = new HashSet<>();
    Collections.addAll(methods, clazz.getMethods());
    Collections.addAll(methods, clazz.getDeclaredMethods());
    for(Method method : methods) {
      if(method.getName().equals(name)) {
        if(method.getParameterCount() == params.length) {
          boolean match = true;
          for(int i = 0; i < method.getParameterTypes().length; i++)
            if(!method.getParameterTypes()[i].getName().equals(params[i].getName())) {
              match = false;
              break;
            }
          if(match) {
            HashMap<CacheHolder, Method> map = METHOD_CACHE.getOrDefault(clazz, new HashMap<>());
            map.put(new CacheHolder(name, null, params, null), method);
            METHOD_CACHE.put(clazz, map);
            return method;
          }
        }
      }
    }
    return null;
  }


  public static Method getMethod(Class<?> clazz, Class<?> returnType, Class<?>... params) {
    if(clazz == null) return null;
    if(METHOD_CACHE.containsKey(clazz) && METHOD_CACHE.get(clazz) != null) {
      CacheHolder cache = new CacheHolder(null, returnType, params, null);
      if(METHOD_CACHE.get(clazz).containsKey(cache)) return METHOD_CACHE.get(clazz).get(cache);
    }
    Set<Method> methods = new HashSet<>();
    Collections.addAll(methods, clazz.getMethods());
    Collections.addAll(methods, clazz.getDeclaredMethods());
    for(Method method : methods) {
      if(method.getReturnType().getName().equals(returnType.getName())) {
        if(method.getParameterCount() == params.length) {
          boolean match = true;
          for(int i = 0; i < method.getParameterTypes().length; i++)
            if(!method.getParameterTypes()[i].getName().equals(params[i].getName())) {
              match = false;
              break;
            }
          if(match) {
            HashMap<CacheHolder, Method> map = METHOD_CACHE.getOrDefault(clazz, new HashMap<>());
            map.put(new CacheHolder(null, returnType, params, null), method);
            METHOD_CACHE.put(clazz, map);
            return method;
          }
        }
      }
    }
    return null;
  }


  private static <T> String toString(T[] arr) {
    return Arrays.stream(arr).map(T::toString).collect(Collectors.joining(","));
  }


  public static class TypeArg {
    Class<?> clazz;
    TypeArg[] typeArgs;

    public TypeArg(Class<?> clazz, TypeArg... types) {
      this.clazz = clazz;
      this.typeArgs = types;
    }

    @Override
    public boolean equals(Object o) {
      if(this == o) return true;
      if(o == null || getClass() != o.getClass()) return false;
      TypeArg typeArg = (TypeArg) o;
      return Objects.equals(clazz, typeArg.clazz) && Arrays.equals(typeArgs, typeArg.typeArgs);
    }

    @Override
    public int hashCode() {
      int result = Objects.hash(clazz);
      result = 31 * result + Arrays.hashCode(typeArgs);
      return result;
    }

    @Override
    public String toString() {
      return clazz.getName() + (typeArgs.length > 0 ? "<" + ReflectionUtils.toString(typeArgs) + ">" : "");
    }
  }

  private static class CacheHolder {

    String name;
    Class<?> returnType;
    Class<?>[] params;
    TypeArg[] typeArgs;
    Integer modifiers;

    public CacheHolder(String name, Class<?> returnType, Class<?>[] params, TypeArg[] typeArgs) {
      this.name = name;
      this.typeArgs = typeArgs;
      this.returnType = returnType;
      this.params = params;
    }

    @Override
    public boolean equals(Object o) {
      if(this == o) return true;
      if(o == null || getClass() != o.getClass()) return false;
      CacheHolder that = (CacheHolder) o;
      boolean paramsMatch = true;
      if(params != null && that.params != null) {
        if(that.params.length != params.length) return false;
        for(int i = 0; i < that.params.length; i++)
          if(!that.params[i].getName().equals(params[i].getName())) {
            paramsMatch = false;
            break;
          }
      }
      boolean dTypeArgsMatch = true;
      if(typeArgs != null && that.typeArgs != null) {
        if(that.typeArgs.length != typeArgs.length) return false;
        dTypeArgsMatch = ReflectionUtils.toString(that.typeArgs).equals(ReflectionUtils.toString(typeArgs));
      }
      boolean returnTypeMatch = true;
      if(returnType != null && that.returnType != null)
        returnTypeMatch = returnType.getName().equals(that.returnType.getName());
      boolean modifiersMatch = true;
      if(modifiers != null && that.modifiers != null)
        modifiersMatch = modifiers.equals(that.modifiers);
      boolean nameMatch = true;
      if(name != null && that.name != null)
        nameMatch = Objects.equals(name, that.name);
      return nameMatch && modifiersMatch && returnTypeMatch && paramsMatch && dTypeArgsMatch;
    }

    @Override
    public int hashCode() {
      int result = name != null ? Objects.hash(name) : Objects.hash(returnType);
      if(returnType != null && name != null) result = 31 * result + Objects.hash(returnType);
      if(modifiers != null) result = 31 * result + Objects.hash(modifiers);
      if(params != null) result = 31 * result + Arrays.hashCode(params);
      if(typeArgs != null) result = 31 * result + Arrays.hashCode(typeArgs);
      return result;
    }
  }

}
