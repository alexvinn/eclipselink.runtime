/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/
package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.internal.unmarshaller.AbstractDeserializer;
import org.eclipse.persistence.json.bind.internal.unmarshaller.EmbeddedItem;
import org.eclipse.persistence.json.bind.internal.unmarshaller.ResolvedParameterizedType;

import javax.json.bind.JsonbException;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Utility class for resolution of generics during unmarshalling.
 *
 * @author Roman Grigoriadi
 */
public class ReflectionUtils {

    private static final Logger logger = Logger.getLogger(ReflectionUtils.class.getName());

    /**
     * Get raw type by type.
     * Only for ParametrizedTypes, GenericArrayTypes and Classes.
     *
     * Empty optional is returned if raw type cannot be resolved.
     *
     * @param type Type to get class information from, not null.
     * @return Class of a type.
     */
    public static Optional<Class<?>> getOptionalRawType(Type type) {
        if (type instanceof Class) {
            return Optional.of((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return Optional.of((Class<?>) ((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            return Optional.of(((GenericArrayType) type).getClass());
        }
        return Optional.empty();
        }

    /**
     * Get raw type by type.
     * Resolves only ParametrizedTypes, GenericArrayTypes and Classes.
     *
     * Exception is thrown if raw type cannot be resolved.
     *
     * @param type Type to get class information from, not null.
     * @return Class of a raw type.
     */
    public static Class<?> getRawType(Type type) {
        return getOptionalRawType(type)
                .orElseThrow(()->new JsonbException(Messages.getMessage(MessageKeys.TYPE_RESOLUTION_ERROR, type)));
    }

    /**
     * Get a raw type of any type.
     * If type is a {@link TypeVariable} recursively search {@link AbstractDeserializer} for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param item item containing wrapper class of a type field, not null.
     * @param type type to resolve, typically field type or generic bound, not null.
     * @return resolved raw class
     */
    public static Class<?> resolveRawType(RuntimeTypeInfo item, Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            return getRawType(resolveType(item, type));
        }
    }

    /**
     * Resolve a type by item.
     * If type is a {@link TypeVariable} recursively search {@link AbstractDeserializer} for resolution of typevar.
     * If type is a {@link WildcardType} find most specific upper / lower bound, which can be used. If most specific
     * bound is a {@link TypeVariable}, perform typevar resolution.
     *
     * @param item item containing wrapper class of a type field, not null.
     * @param type type to resolve, typically field type or generic bound, not null.
     * @return resolved type
     */
    public static Type resolveType(RuntimeTypeInfo item, Type type) {
        if (type instanceof WildcardType) {
            return resolveMostSpecificBound(item, (WildcardType) type);
        } else if (type instanceof TypeVariable) {
            return resolveItemVariableType(item, (TypeVariable<?>) type);
        } else if (type instanceof ParameterizedType && item != null) {
            return resolveTypeArguments((ParameterizedType) type, item.getRuntimeType());
        }
        return type;
    }

    /**
     * Resolve a bounded type variable type by its wrapper types.
     * Resolution could be done only if a compile time generic information is provided, either:
     * by generic field or subclass of a generic class.
     * @param item item to search "runtime" generic type of a TypeVariable.
     * @param typeVariable type to search in item for, not null.
     * @return Type of a generic "runtime" bound, not null.
     */
    public static Type resolveItemVariableType(RuntimeTypeInfo item, TypeVariable<?> typeVariable) {
        if (item == null) {
            //Bound not found, treat it as an Object.class
            //TODO needs a field declaration identification.
            logger.warning(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND, typeVariable, typeVariable.getGenericDeclaration()));
            return Object.class;
        }

        //Embedded items doesn't hold information about variable types
        if (item instanceof EmbeddedItem) {
            return resolveItemVariableType(item.getWrapper(), typeVariable);
        }

        ParameterizedType wrapperParameterizedType = findParameterizedSuperclass(item.getRuntimeType());

        VariableTypeInheritanceSearch search = new VariableTypeInheritanceSearch();
        Type foundType = search.searchParametrizedType(wrapperParameterizedType, typeVariable);
        if (foundType != null) {
            if (foundType instanceof TypeVariable) {
                return resolveItemVariableType(item.getWrapper(), (TypeVariable<?>) foundType);
            }
            return foundType;
        }

        return resolveItemVariableType(item.getWrapper(), typeVariable);
    }

    public static Type resolveTypeArguments(ParameterizedType typeToResolve, Type typeToSearch) {
        final Type[] unresolvedArgs = typeToResolve.getActualTypeArguments();
        Type[] resolvedArgs = new Type[unresolvedArgs.length];
        for (int i = 0; i< unresolvedArgs.length; i++) {
            if (!(unresolvedArgs[i] instanceof TypeVariable)) {
                resolvedArgs[i] = unresolvedArgs[i];
            } else {
                resolvedArgs[i] = new VariableTypeInheritanceSearch().searchParametrizedType(typeToSearch, (TypeVariable<?>) unresolvedArgs[i]);
                if (resolvedArgs[i] == null) {
                    //TODO happens with mistyped runtime type, better explanation whats wrong
                    throw new IllegalStateException();
//                    logger.warning(Messages.getMessage(MessageKeys.GENERIC_BOUND_NOT_FOUND, unresolvedArgs[i], typeToSearch));
                }
            }
            if (resolvedArgs[i] instanceof ParameterizedType) {
                resolvedArgs[i] = resolveTypeArguments((ParameterizedType) resolvedArgs[i], typeToSearch);
            }
        }
        return Arrays.equals(resolvedArgs, unresolvedArgs) ?
                typeToResolve : new ResolvedParameterizedType(typeToResolve, resolvedArgs);
    }

    /**
     * Search for no argument constructor of a class and create instance.
     *
     *  @param clazz not null
     * @param <T> type of instance
     * @return instance
     */
    public static <T> T createNoArgConstructorInstance(Class<T> clazz) {
        Objects.requireNonNull(clazz);
        try {
            final Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            return declaredConstructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonbException("Can't create instance", e);
        } catch (NoSuchMethodException e) {
            throw new JsonbException(Messages.getMessage(MessageKeys.NO_DEFAULT_CONSTRUCTOR, clazz), e);
        }
    }

    /**
     * For generic adapters like:
     * <p>
     *     {@code
     *     interface ContainerAdapter<T> extends JsonbAdapter<Box<T>, Crate<T>>...;
     *     class IntegerBoxToCrateAdapter implements ContainerAdapter<Integer>...;
     *     }
     * </p>
     * We need to find a JsonbAdapter class which will hold basic generic type arguments,
     * and resolve them if they are TypeVariables from there.
     *
     * @param classToSearch class to resolve parameterized interface
     * @param parameterizedInterface interface to search
     *
     * @return type of JsonbAdapter
     */
    public static ParameterizedType findParameterizedType(Class<?> classToSearch, Class<?> parameterizedInterface) {
        Class current = classToSearch;
        while (current != Object.class) {
            for (Type currentInterface : current.getGenericInterfaces()) {
                if (currentInterface instanceof ParameterizedType &&
                        ((ParameterizedType) currentInterface).getRawType().equals(parameterizedInterface)) {
                    return (ParameterizedType) currentInterface;
                }
            }
            current = current.getSuperclass();
        }
        //TODO messages
        throw new JsonbException(String.format("Type: %s does not implement a parametrized type interface", parameterizedInterface));
    }

    private static ParameterizedType findParameterizedSuperclass(Type type) {
        if (type == null || type instanceof ParameterizedType) {
            return (ParameterizedType) type;
        }
        if (!(type instanceof Class)) {
            throw new JsonbException("Can't resolve ParameterizedType superclass for: " + type);
        }
        return findParameterizedSuperclass(((Class) type).getGenericSuperclass());
    }

    /**
     * Resolves a wildcard most specific upper or lower bound.
     */
    private static Type resolveMostSpecificBound(RuntimeTypeInfo item, WildcardType wildcardType) {
        Class<?> result = Object.class;
        for (Type upperBound : wildcardType.getUpperBounds()) {
            result = getMostSpecificBound(item, result, upperBound);
        }
        for (Type lowerBound : wildcardType.getLowerBounds()) {
            result = getMostSpecificBound(item, result, lowerBound);
        }
        return result;
    }

    private static Class<?> getMostSpecificBound(RuntimeTypeInfo item, Class<?> result, Type bound) {
        if (bound == Object.class) {
            return result;
        }
        //if bound is type variable search recursively for wrapper generic expansion
        Type resolvedBoundType = bound instanceof TypeVariable ? resolveType(item, bound) : bound;
        Class<?> boundRawType = getRawType(resolvedBoundType);
        //resolved class is a subclass of a result candidate
        if (result.isAssignableFrom(boundRawType)) {
            result = boundRawType;
        }
        return result;
    }

}
